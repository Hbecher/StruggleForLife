package org.angryautomata.game;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import javafx.application.Platform;
import org.angryautomata.game.action.Action;
import org.angryautomata.game.action.Nothing;
import org.angryautomata.game.scenery.Scenery;
import org.angryautomata.gui.Controller;

/**
 * Représente le jeu.<br />
 * C'est la classe gérant le simulateur, avec l'ordonnanceur et le système de mises à jour de décors.
 */
public class Game implements Runnable
{
	/**
	 * Trieur de populations suivant leurs points
	 */
	private static final Comparator<Population> GRADIENT_COMPARATOR = (o1, o2) -> o2.getGradient() - o1.getGradient();

	/**
	 * Points gagnés ou perdus lors d'un combat
	 */
	private static final int GRADIENT_COMBAT = 10;

	/**
	 * Le plateau de jeu
	 */
	private final Board board;

	/**
	 * Les joueurs
	 */
	private final List<Player> players;

	/**
	 * Toutes les populations
	 */
	private final List<Population> populations = new ArrayList<>();

	/**
	 * Les mises à jours de décors
	 */
	private final Map<Position, ArrayList<TileUpdate>> pendingUpdates = new HashMap<>();

	/**
	 * Les balises
	 */
	private final Map<Player, Position> markers = new HashMap<>();

	/**
	 * Ces listes contiennent les actions à afficher à l'écran
	 */
	private final Deque<Position> tileUpdates = new ArrayDeque<>(), conflicts = new ArrayDeque<>(), consumes = new ArrayDeque<>(), traps = new ArrayDeque<>(), invalids = new ArrayDeque<>();

	/**
	 * Le lien entre l'interface utilisateur et le simulateur
	 */
	private final Controller controller;

	/**
	 * La vitesse d'exécution du simulateur (temps d'attente en ms entre deux tours)
	 */
	private long tickSpeed = 200L;

	/**
	 * Pause et arrêt du jeu
	 */
	private boolean pause = true, run = true;

	/**
	 * Nombre de tours
	 */
	private int ticks = 0;

	/**
	 * Synchronisateur
	 */
	private CountDownLatch tickWait = new CountDownLatch(1);

	public Game(Controller controller, Board board, List<Player> players)
	{
		this.controller = controller;
		this.board = board;
		this.players = players;

		// au moins un joueur
		if(players == null || players.size() < 2)
		{
			throw new RuntimeException("There must be at least one player!");
		}

		// on ajoute les automates des joueurs au décor
		for(Player player : players)
		{
			Automaton automaton = player.getAutomaton();

			regenAutomaton(player);

			int actions = Scenery.sceneries(), states = automaton.numberOfStates();
			Position origin = automaton.getOrigin();
			int ox = origin.getX(), oy = origin.getY();

			// on place une population au hasard au bord de l'automate
			Position position = (int) (Math.random() * 2.0D) == 0 ? new Position(ox + (int) (Math.random() * states), oy + (int) (Math.random() * 2.0D) * actions) : new Position(ox + (int) (Math.random() * 2.0D) * states, oy + (int) (Math.random() * actions));

			addPopulation(new Population(player, 0, position));
		}
	}

	@Override
	public void run()
	{
		// tant que le jeu tourne
		while(run)
		{
			// pause
			while(pause)
			{
				try
				{
					Thread.sleep(9L);
				}
				catch(InterruptedException ignored)
				{
				}

				if(!run)
				{
					return;
				}
			}

			// synchronisateur
			Thread tickThread = new Thread(() ->
			{
				try
				{
					Thread.sleep(tickSpeed);
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}

				tickWait.countDown();
			}, "Tick thread");
			tickThread.setDaemon(true);
			tickThread.start();

			// listes contenant les clones et morts
			final List<Population> clones = new ArrayList<>(), dead = new ArrayList<>();

			// on commence par vérifier les combats
			populations.stream().filter(this::hasPopulationOnSelf).forEach(population ->
			{
				// si une case est occupée par plusieurs populations

				Position self = population.getPosition();
				List<Population> p = getPopulations(self);
				// on trie les populations par points
				p.sort(GRADIENT_COMPARATOR);
				// on récupère et retire de la liste celle avec le plus de points
				Population pp = p.get(0);
				p.remove(0);

				boolean enemy = false;

				// pour chaque population
				for(Population ppp : p)
				{
					// si c'est un ennemi, il perd des points, récupérés par l'autre
					if(!ppp.isTeammate(pp))
					{
						ppp.updateGradient(-GRADIENT_COMBAT);
						pp.updateGradient(GRADIENT_COMBAT);

						enemy = true;
					}

					// la population est forcée de se déplacer, comptant comme un tour joué
					ppp.moveTo(ppp.getPreviousPosition());

					// si la population est morte
					if(ppp.isDead())
					{
						dead.add(ppp);
					}

					ppp.played(true);

					tileUpdates.add(ppp.getPosition());

					if(enemy)
					{
						conflicts.add(self);
					}
				}
			});

			// on retire les morts
			dead.forEach(this::removePopulation);
			dead.clear();

			// on calcule les actions pour chaque personnage n'ayant pas encore joué
			populations.stream().filter(population -> !population.hasPlayed()).forEach(population ->
			{
				Position self = population.getPosition();
				Scenery o = board.getSceneryAt(self);

				// le clonage a la plus grande priorité
				if(population.canClone())
				{
					clones.add(population.createClone(this));
				}
				else
				{
					// le simulateur calcule une action et l'exécute
					Action action = action(population, o);

					action.execute(this, population);

					// si l'action met à jour la carte, on rajoute une mise à jour de décor
					if(action.updatesMap())
					{
						if(!pendingUpdates.containsKey(self))
						{
							pendingUpdates.put(self, new ArrayList<>());
						}

						ArrayList<TileUpdate> pending = pendingUpdates.get(self);
						TileUpdate tileUpdate = new TileUpdate(o.getFakeSymbol(), 80);
						pending.add(0, tileUpdate);
					}
				}

				if(population.isDead())
				{
					dead.add(population);

					tileUpdates.add(population.getPreviousPosition());
				}
				else
				{
					population.nextState(o.getFakeSymbol());
				}

				tileUpdates.add(population.getPosition());

				population.played(true);
			});

			// si une population se trouve sur le marqueur de son joueur, on le retire
			populations.stream().filter(this::hasOwnMarkerOnSelf).forEach(population -> delMarker(population.getPlayer().getName()));
			// on enlève les morts
			dead.forEach(this::removePopulation);
			// on permet à tout le monde de jouer au tour suivant
			populations.forEach(population -> population.played(false));
			// on rajoute les clones qui joueront au tour suivant
			clones.forEach(this::addPopulation);

			dead.clear();
			clones.clear();

			// si tout le monde est mort ou s'il reste un joueur en vie
			if(playersAlive() < 2)
			{
				// on arrête le jeu / c'est le dernier tour
				stop();
			}

			// décrémentation des temps d'attente des actions joueur
			for(Player player : players)
			{
				if(!hasMarker(player))
				{
					player.decMarkerCooldown();
				}

				player.decRegenCooldown();
			}

			int height = board.getHeight(), width = board.getWidth();

			// nombre de décors à vérifier pour mise à jour
			int randomTileUpdates = (int) ((height * width) * 0.2D);

			for(int k = 0; k < randomTileUpdates; k++)
			{
				// on prend un décor au hasard
				// un décor peut être testé plusieurs fois
				Position rnd = board.randomPos();
				ArrayList<TileUpdate> tileUpdates = pendingUpdates.get(rnd);

				// si une mise à jour est déjà en attente
				if(tileUpdates != null && !tileUpdates.isEmpty())
				{
					TileUpdate tileUpdate = tileUpdates.get(0);

					// si on peut mettre à jour
					if(tileUpdate.canUpdate())
					{
						// on met à jour
						board.setSceneryAt(rnd, Scenery.byId(tileUpdate.getPrevSymbol()));

						// on retire la mise à jour
						tileUpdates.remove(0);

						// on notifie l'affichage
						this.tileUpdates.add(rnd);
					}
					else
					{
						// sinon, on décrémente les ticks restants
						tileUpdate.countDown();
					}
				}
			}

			// incrémentation des tours
			ticks++;

			// on affiche le tour
			Platform.runLater(() -> controller.updateScreen(tileUpdates, conflicts, consumes, traps, invalids));

			// si le tour s'est calculé plus vite que l'attente entre deux tours, on attend
			try
			{
				tickWait.await();
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}

			tickWait = new CountDownLatch(1);
		}

		StringBuilder winners = new StringBuilder();

		for(Iterator<Player> iterator = players.iterator(); iterator.hasNext(); )
		{
			Player player = iterator.next();

			if(!player.isDead())
			{
				if(winners.length() > 0)
				{
					winners.append(iterator.hasNext() ? "," : "et").append(" ");
				}

				winners.append(player.getName());
			}
		}

		Platform.runLater(() -> controller.displayWinner(winners.length() == 0 ? "Personne n'a gagné" : winners.toString()));
	}

	/**
	 * Retourne une action possible à effectuer en fonction du décor.
	 *
	 * @param population la population
	 * @param o          le décor lu
	 * @return Une action au hasard suivant celles possibles
	 */
	private Action action(Population population, Scenery o)
	{
		int state = population.getState();
		Position self = population.getPosition();
		Position origin = population.getPlayer().getAutomaton().getOrigin();
		// on récupère le décor définissant le comportement de l'automate pour l'état et le symbole lu
		int id = board.getSceneryAt(board.torusPos(origin.getX() + state, origin.getY() + o.getFakeSymbol())).getSymbol();

		// si l'automate nous dit "se déplacer" ou si on se trouve sur un automate allié
		if(id == 0 || population.isOnTeamAutomaton())
		{
			List<Action> actions = Action.byId(0); // nord est sud ouest

			int x = self.getX(), y = self.getY();
			Position n = board.torusPos(x, y - 1);
			Position e = board.torusPos(x + 1, y);
			Position s = board.torusPos(x, y + 1);
			Position w = board.torusPos(x - 1, y);

			Position marker = markers.get(population.getPlayer());

			// si une balise existe pour notre joueur
			if(marker != null)
			{
				// on se déplace vers cette balise
				int markerX = marker.getX(), markerY = marker.getY();
				int c1 = x - markerX, c2 = y - markerY;
				int minH1 = -c1, minH2 = c1 + getWidth() - 1, minV1 = -c2, minV2 = c2 + getHeight() - 1;
				int orientX = (c1 < 0) ? ((minH1 < minH2) ? 1 : 3) : ((c1 > 0) ? ((minH1 < minH2) ? 3 : 1) : ((int) (Math.random() * 2.0D) == 0 ? 1 : 3));
				int orientY = (c2 < 0) ? ((minV1 < minV2) ? 2 : 0) : ((c2 > 0) ? ((minV1 < minV2) ? 0 : 2) : ((int) (Math.random() * 2.0D) == 0 ? 0 : 2));

				return actions.get((int) (Math.random() * 2.0D) == 0 ? orientX : orientY);
			}

			// sinon on prend une direction "au hasard" en privilégiant celles ayant un consommable et dont on ne vient pas
			List<Action> l = new ArrayList<>();

			if(canMoveTo(population, n))
			{
				l.add(actions.get(0));
			}

			if(canMoveTo(population, e))
			{
				l.add(actions.get(1));
			}

			if(canMoveTo(population, s))
			{
				l.add(actions.get(2));
			}

			if(canMoveTo(population, w))
			{
				l.add(actions.get(3));
			}

			// si on ne peut pas se déplacer vers une des 4 directions, on en prend une au hasard
			if(l.isEmpty())
			{
				if(!population.comesFrom(n))
				{
					l.add(actions.get(0));
				}

				if(!population.comesFrom(e))
				{
					l.add(actions.get(1));
				}

				if(!population.comesFrom(s))
				{
					l.add(actions.get(2));
				}

				if(!population.comesFrom(w))
				{
					l.add(actions.get(3));
				}
			}

			return l.get((int) (Math.random() * l.size()));
		}

		// sinon, on récupère l'action décrite par le décor
		List<Action> actions = Action.byId(id);
		Action action = actions.get((int) (Math.random() * actions.size()));

		// si l'action est valide
		if(o.matches(action))
		{
			return action;
		}

		invalids.add(self);

		return new Nothing();
	}

	private void addPopulation(Population population)
	{
		populations.add(population);
	}

	public List<Population> getPopulations()
	{
		return Collections.unmodifiableList(populations);
	}

	public List<Population> getPopulations(Position position)
	{
		return populations.stream().filter(population -> population.getPosition().equals(position)).collect(Collectors.toList());
	}

	private void removePopulation(Population population)
	{
		population.die();

		populations.remove(population);
	}

	/**
	 * @param population la population
	 * @return Si cette population n'est pas seule sur sa case
	 */
	private boolean hasPopulationOnSelf(Population population)
	{
		Position self = population.getPosition();

		for(Population p : populations)
		{
			if(p.getPosition().equals(self) && p != population)
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * @param population la population
	 * @return Si la population est sur son marqueur
	 */
	private boolean hasOwnMarkerOnSelf(Population population)
	{
		Position position = markers.get(population.getPlayer());

		return position != null && position.equals(population.getPosition());

	}

	/**
	 * @param population la population
	 * @param position   une position adjacente
	 * @return Si la population peut se déplacer sur la position
	 */
	private boolean canMoveTo(Population population, Position position)
	{
		return board.getSceneryAt(position).getSymbol() != 0 && !population.comesFrom(position);
	}

	public void pause()
	{
		pause = true;
	}

	public void resume()
	{
		pause = false;
	}

	public void stop()
	{
		run = false;
	}

	public boolean isPaused()
	{
		return pause;
	}

	public boolean isStopped()
	{
		return !run;
	}

	public int ticks()
	{
		return ticks;
	}

	public void setTickSpeed(long tickSpeed)
	{
		this.tickSpeed = tickSpeed;
	}

	public int getWidth()
	{
		return board.getWidth();
	}

	public int getHeight()
	{
		return board.getHeight();
	}

	public Scenery getSceneryAt(Position position)
	{
		return board.getSceneryAt(position);
	}

	public void setSceneryAt(Position position, Scenery scenery)
	{
		board.setSceneryAt(position, scenery);
	}

	public Position torusPos(int x, int y)
	{
		return board.torusPos(x, y);
	}

	public List<Player> getPlayers()
	{
		return Collections.unmodifiableList(players);
	}

	/**
	 * (Ré)génère l'automate du joueur sur le décor.
	 *
	 * @param player le joueur
	 */
	public void regenAutomaton(Player player)
	{
		Automaton automaton = player.getAutomaton();
		int symbols = Scenery.sceneries(), states = automaton.numberOfStates();
		Position origin = automaton.getOrigin();
		int ox = origin.getX(), oy = origin.getY();

		for(int y = 0; y < symbols; y++)
		{
			for(int x = 0; x < states; x++)
			{
				Position position = board.torusPos(x + ox, y + oy);

				board.setSceneryAt(position, Scenery.byId(automaton.initialAction(x, y)));

				if(ticks > 0)
				{
					pendingUpdates.remove(position);

					tileUpdates.add(position);
				}
			}
		}

		player.resetCooldowns();
	}

	/**
	 * Ajoute une balise pour le joueur
	 *
	 * @param name     le nom du joueur
	 * @param position la position de la balise
	 */
	public void addMarker(String name, Position position)
	{
		Player player = getPlayer(name);

		if(player != null)
		{
			Position prevMarker = markers.remove(player);

			if(prevMarker != null)
			{
				tileUpdates.add(prevMarker);
			}

			markers.put(player, position);

			player.resetCooldowns();
		}
	}

	/**
	 * Retire la balise du joueur
	 *
	 * @param name le nom du joueur
	 */
	public void delMarker(String name)
	{
		Player player = getPlayer(name);

		if(player != null)
		{
			Position position = markers.remove(player);

			if(position != null)
			{
				tileUpdates.add(position);
			}
		}
	}

	public boolean hasMarker(Player player)
	{
		return markers.containsKey(player);
	}

	public Map<Player, Position> getMarkers()
	{
		return Collections.unmodifiableMap(markers);
	}

	public Player getPlayer(String name)
	{
		for(Player player : players)
		{
			if(player.getName().equals(name))
			{
				return player;
			}
		}

		return null;
	}

	public int playersAlive()
	{
		int count = 0;

		for(Player player : players)
		{
			if(!player.isDead())
			{
				count++;
			}
		}

		return count;
	}
}