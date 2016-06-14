package org.angryautomata.game;


import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import javafx.application.Platform;
import org.angryautomata.Controller;
import org.angryautomata.game.action.Action;
import org.angryautomata.game.action.Nothing;
import org.angryautomata.game.scenery.Scenery;

public class Game implements Runnable
{
	private static final Comparator<Player> GRADIENT_COMPARATOR = (o1, o2) -> o2.getGradient() - o1.getGradient();

	private final Board board;
	private final Automaton[] automata;
	private final List<Player> players = new ArrayList<>();
	private final Map<Position, ArrayList<Update>> toUpdate = new HashMap<>();
	private final Controller controller;
	private long tickSpeed = 200L;
	private boolean pause = true, run = true;
	private int ticks = 0;
	private CountDownLatch tickWait = new CountDownLatch(1);

	public Game(Controller controller, Board board, Automaton... automata)
	{
		this.controller = controller;
		this.board = board;
		this.automata = automata;

		if(automata == null || automata.length < 1)
		{
			throw new RuntimeException("automata cannot be null and must contain at least one automaton");
		}

		for(Automaton automaton : automata)
		{
			int actions = Scenery.sceneries(), states = automaton.numberOfStates();
			Position origin = automaton.getOrigin();
			int ox = origin.getX(), oy = origin.getY();

			for(int y = 0; y < actions; y++)
			{
				for(int x = 0; x < states; x++)
				{
					board.setSceneryAt(board.torusPos(x + ox, y + oy), Scenery.byId(automaton.initialAction(x, y)));
				}
			}

			addPlayer(new Player(automaton, 0, 0, board.randomPos()));
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
					Thread.sleep(10L);
				}
				catch(InterruptedException ignored)
				{
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

			final List<Player> clones = new ArrayList<>(), dead = new ArrayList<>();
			final List<Position> tileUpdates = new ArrayList<>();

			players.stream().filter(player -> !player.hasPlayed() && hasPlayerOnSelf(player)).forEach(player ->
			{
				Position self = player.getPosition();
				List<Player> p = getPlayers(self);
				p.sort(GRADIENT_COMPARATOR);
				Player pp = p.get(0);
				p.remove(0);

				for(Player ppp : p)
				{
					if(!ppp.isOnSameTeamAs(pp))
					{
						int grad = 10;

						ppp.updateGradient(-grad);
						pp.updateGradient(grad);
					}

					if(ppp.isDead())
					{
						dead.add(ppp);
					}
					else
					{
						ppp.moveTo(ppp.getPreviousPosition());
					}

					ppp.played(true);
				}
			});

			players.stream().filter(player -> !player.hasPlayed()).forEach(player ->
			{
				Position self = player.getPosition();
				Scenery o = board.getSceneryAt(self);

				if(player.canClone())
				{
					clones.add(player.createClone());
				}
				else
				{
					Action action = action(player, o);

					action.execute(this, player);

					if(action.updatesMap())
					{
						if(!toUpdate.containsKey(self))
						{
							toUpdate.put(self, new ArrayList<>());
						}

						ArrayList<Update> pending = toUpdate.get(self);
						Update update = new Update(o.getFakeSymbol(), 20);
						pending.add(0, update);
					}
				}

				if(player.isDead())
				{
					dead.add(player);

					tileUpdates.add(player.getPosition());
				}
				else
				{
					player.nextState(o.getFakeSymbol());
				}

				player.played(true);
			});

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

			dead.forEach(this::removePlayer);
			players.forEach(player -> player.played(false));
			clones.forEach(this::addPlayer);
			dead.clear();
			clones.clear();

			if(players.isEmpty())
			{
				stop();
			}

			ticks++;

			Platform.runLater(() -> controller.update(getPlayers(), tileUpdates));

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

	private Action action(Player player, Scenery o)
	{
		int state = player.getState();
		Position origin = player.getAutomaton().getOrigin();
		int id = board.getSceneryAt(board.torusPos(origin.getX() + state, origin.getY() + o.getFakeSymbol())).getSymbol();

		if(id == 0 || player.isOnOwnAutomaton())
		{
			Action[] actions = Action.byId(0);

			Position self = player.getPosition();
			Position n = board.torusPos(self.getX(), self.getY() - 1);
			Position e = board.torusPos(self.getX() + 1, self.getY());
			Position s = board.torusPos(self.getX(), self.getY() + 1);
			Position w = board.torusPos(self.getX() - 1, self.getY());

			List<Action> l = new ArrayList<>();

			if(canMoveTo(player, n))
			{
				l.add(actions[0]);
			}

			if(canMoveTo(player, e))
			{
				l.add(actions[1]);
			}

			if(canMoveTo(player, s))
			{
				l.add(actions[2]);
			}

			if(canMoveTo(player, w))
			{
				l.add(actions[3]);
			}

			if(l.isEmpty())
			{
				if(!player.comesFrom(n))
				{
					l.add(actions[0]);
				}

				if(!player.comesFrom(e))
				{
					l.add(actions[1]);
				}

				if(!player.comesFrom(s))
				{
					l.add(actions[2]);
				}

				if(!player.comesFrom(w))
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

	private void addPlayer(Player player)
	{
		players.add(player);
	}

	public List<Player> getPlayers()
	{
		return Collections.unmodifiableList(players);
	}

	public List<Player> getPlayers(Position position)
	{
		return players.stream().filter(player -> player.getPosition().equals(position)).collect(Collectors.toList());
	}

	private void removePlayer(Player player)
	{
		player.die();

		players.remove(player);
	}

	private boolean hasPlayerOnSelf(Player player)
	{
		Position self = player.getPosition();

		for(Player p : players)
		{
			if(p.getPosition().equals(self) && p != player)
			{
				return true;
			}
		}

		return false;
	}

	private boolean canMoveTo(Player player, Position position)
	{
		return board.getSceneryAt(position).getSymbol() != 0 && !player.comesFrom(position);
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
}