package org.angryautomata.game;


import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import javafx.application.Platform;
import org.angryautomata.Controller;
import org.angryautomata.Player;
import org.angryautomata.game.action.Action;
import org.angryautomata.game.action.Nothing;
import org.angryautomata.game.scenery.Scenery;

public class Game implements Runnable
{
	private static final Comparator<Population> GRADIENT_COMPARATOR = (o1, o2) -> o2.getGradient() - o1.getGradient();
	private static final int GRADIENT_COMBAT = 10;

	private final Board board;
	private final Player[] players;
	private final List<Population> populations = new ArrayList<>();
	private final Map<Position, ArrayList<Update>> toUpdate = new HashMap<>();
	private final Map<Player, Position> markers = new HashMap<>();
	private final Deque<Position> tileUpdates = new ArrayDeque<>();
	private final Controller controller;
	private long tickSpeed = 200L;
	private boolean pause = true, run = true;
	private int ticks = 0;
	private CountDownLatch tickWait = new CountDownLatch(1);

	public Game(Controller controller, Board board, Player... players)
	{
		this.controller = controller;
		this.board = board;
		this.players = players;

		if(players == null || players.length < 1)
		{
			throw new RuntimeException("There must be at least one player");
		}

		for(int i = 0; i < players.length; i++)
		{
			Player player = players[i];
			Automaton automaton = player.getAutomaton();

			regenAutomaton(player);

			int actions = Scenery.sceneries(), states = automaton.numberOfStates();
			Position origin = automaton.getOrigin();
			int ox = origin.getX(), oy = origin.getY();

			addPopulation(new Population(player, 0, i, board.torusPos(ox + (int) (Math.random() * states), oy + (int) (Math.random() * actions))));
		}
	}

	@Override
	public void run()
	{
		while(run)
		{
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

			final List<Population> clones = new ArrayList<>(), dead = new ArrayList<>();

			populations.stream().filter(population -> !population.hasPlayed() && hasPopulationOnSelf(population)).forEach(population ->
			{
				Position self = population.getPosition();
				List<Population> p = getPopulations(self);
				p.sort(GRADIENT_COMPARATOR);
				Population pp = p.get(0);
				p.remove(0);

				for(Population ppp : p)
				{
					if(!ppp.isOnSameTeamAs(pp))
					{
						ppp.updateGradient(-GRADIENT_COMBAT);
						pp.updateGradient(GRADIENT_COMBAT);
					}
					else
					{
						ppp.moveTo(ppp.getPreviousPosition());
					}

					if(ppp.isDead())
					{
						dead.add(ppp);
					}

					ppp.played(true);

					tileUpdates.add(ppp.getPosition());
				}
			});

			populations.stream().filter(population -> !population.hasPlayed()).forEach(population ->
			{
				Position self = population.getPosition();
				Scenery o = board.getSceneryAt(self);

				if(population.canClone())
				{
					clones.add(population.createClone());
				}
				else
				{
					Action action = action(population, o);

					action.execute(this, population);

					if(action.updatesMap())
					{
						if(!toUpdate.containsKey(self))
						{
							toUpdate.put(self, new ArrayList<>());
						}

						ArrayList<Update> pending = toUpdate.get(self);
						Update update = new Update(o.getFakeSymbol(), 80);
						pending.add(0, update);
					}
				}

				if(population.isDead())
				{
					dead.add(population);

					tileUpdates.add(population.getPreviousPosition());
					tileUpdates.add(population.getPosition());
				}
				else
				{
					population.nextState(o.getFakeSymbol());
				}

				population.played(true);
			});

			dead.forEach(this::removePopulation);
			populations.forEach(population -> population.played(false));
			clones.forEach(this::addPopulation);

			dead.clear();
			clones.clear();

			if(populations.isEmpty())
			{
				stop();
			}

			int height = board.getHeight(), width = board.getWidth();

			int randomTileUpdates = (int) ((height * width) * 0.2F);

			for(int k = 0; k < randomTileUpdates; k++)
			{
				Position rnd = board.randomPos();
				ArrayList<Update> updates = toUpdate.get(rnd);

				if(updates != null && !updates.isEmpty())
				{
					Update update = updates.get(0);

					if(update.canUpdate())
					{
						board.setSceneryAt(rnd, Scenery.byId(update.getPrevSymbol()));

						updates.remove(0);
						tileUpdates.add(rnd);
					}
					else
					{
						update.countDown();
					}
				}
			}

			ticks++;

			Platform.runLater(() -> controller.updateScreen(getPopulations(), tileUpdates, getMarkers()));

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
	}

	private Action action(Population population, Scenery o)
	{
		int state = population.getState();
		Position origin = population.getPlayer().getAutomaton().getOrigin();
		int id = board.getSceneryAt(board.torusPos(origin.getX() + state, origin.getY() + o.getFakeSymbol())).getSymbol();

		if(id == 0 || population.isOnOwnAutomaton())
		{
			Action[] actions = Action.byId(0);

			Position self = population.getPosition();
			Position n = board.torusPos(self.getX(), self.getY() - 1);
			Position e = board.torusPos(self.getX() + 1, self.getY());
			Position s = board.torusPos(self.getX(), self.getY() + 1);
			Position w = board.torusPos(self.getX() - 1, self.getY());

			List<Action> l = new ArrayList<>();

			if(canMoveTo(population, n))
			{
				l.add(actions[0]);
			}

			if(canMoveTo(population, e))
			{
				l.add(actions[1]);
			}

			if(canMoveTo(population, s))
			{
				l.add(actions[2]);
			}

			if(canMoveTo(population, w))
			{
				l.add(actions[3]);
			}

			if(l.isEmpty())
			{
				if(!population.comesFrom(n))
				{
					l.add(actions[0]);
				}

				if(!population.comesFrom(e))
				{
					l.add(actions[1]);
				}

				if(!population.comesFrom(s))
				{
					l.add(actions[2]);
				}

				if(!population.comesFrom(w))
				{
					l.add(actions[3]);
				}
			}

			return l.get((int) (Math.random() * l.size()));
		}

		Action[] actions = Action.byId(id);
		Action action = actions[(int) (Math.random() * actions.length)];
		boolean matches = matches(action, o);

		return matches ? action : new Nothing();
	}

	private boolean matches(Action action, Scenery scenery)
	{
		int id = action.getId();
		int[] validActions = scenery.getValidActions();

		for(int validId : validActions)
		{
			if(validId == id)
			{
				return true;
			}
		}

		return false;
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

	public Player[] getPlayers()
	{
		return players;
	}

	public void regenAutomaton(Player player)
	{
		Automaton automaton = player.getAutomaton();
		int actions = Scenery.sceneries(), states = automaton.numberOfStates();
		Position origin = automaton.getOrigin();
		int ox = origin.getX(), oy = origin.getY();

		for(int y = 0; y < actions; y++)
		{
			for(int x = 0; x < states; x++)
			{
				Position position = board.torusPos(x + ox, y + oy);

				board.setSceneryAt(position, Scenery.byId(automaton.initialAction(x, y)));
				tileUpdates.add(position);
			}
		}
	}

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
		}
	}

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
}