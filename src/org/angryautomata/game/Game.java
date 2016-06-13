package org.angryautomata.game;


import java.util.*;

import javafx.application.Platform;
import org.angryautomata.Controller;
import org.angryautomata.game.action.Action;
import org.angryautomata.game.scenery.Scenery;

public class Game implements Runnable
{
	private final Board board;
	private final Automaton[] automata;
	private final List<Player> players = new ArrayList<>();
	private final Map<Position, ArrayList<Update>> toUpdate = new HashMap<>();
	private final Controller controller;
	private long tickSpeed = 200L;
	private boolean pause = true, run = true;
	private int ticks = 0;

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

			List<Player> clones = new ArrayList<>(), dead = new ArrayList<>();

			for(Player player : players)
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

					if(action.changesMap())
					{
						if(!toUpdate.containsKey(self))
						{
							toUpdate.put(self, new ArrayList<>());
						}

						ArrayList<Update> pending = toUpdate.get(self);
						Update update = new Update(o.getFakeSymbol(), 0);
						pending.add(0, update);
					}
				}

				if(player.isDead())
				{
					dead.add(player);
				}
				else
				{
					player.nextState(o.getFakeSymbol());
				}
			}

			int height = board.getHeight(), width = board.getWidth();

			dead.forEach(this::removePlayer);
			clones.forEach(this::addPlayer);

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
					}
					else
					{
						update.countDown();
					}
				}
			}

			Platform.runLater(() -> controller.update(Collections.unmodifiableList(players)));

			clones.forEach(this::addPlayer);
			dead.forEach(this::removePlayer);

			if(players.isEmpty())
			{
				stop();
			}

			ticks++;

			try
			{
				Thread.sleep(tickSpeed);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	private Action action(Player player, Scenery o)
	{
		int state = player.getState();
		Position origin = player.getAutomaton().getOrigin();
		int id = board.getSceneryAt(board.torusPos(origin.getX() + state, origin.getY() + o.getFakeSymbol())).getSymbol();

		if(id == 0 || player.isInTrouble())
		{
			Action[] actions = Action.byId(0);
			Position self = player.getPosition();
			Position n = board.torusPos(self.getX(), self.getY() - 1);
			Position e = board.torusPos(self.getX() + 1, self.getY());
			Position s = board.torusPos(self.getX(), self.getY() + 1);
			Position w = board.torusPos(self.getX() - 1, self.getY());
			List<Action> l = new ArrayList<>();

			if(board.getSceneryAt(n).getSymbol() != 0 && !player.comesFrom(n))
			{
				l.add(actions[0]);
			}
			if(board.getSceneryAt(e).getSymbol() != 0 && !player.comesFrom(e))
			{
				l.add(actions[1]);
			}
			if(board.getSceneryAt(s).getSymbol() != 0 && !player.comesFrom(s))
			{
				l.add(actions[2]);
			}
			if(board.getSceneryAt(w).getSymbol() != 0 && !player.comesFrom(w))
			{
				l.add(actions[3]);
			}
			if(l.isEmpty())
			{
				Collections.addAll(l, actions);
			}

			return l.get((int) (Math.random() * l.size()));
		}

		Action[] actions = Action.byId(id);
		Action action = actions[(int) (Math.random() * actions.length)];
		boolean matches = matches(action.getId(), o.getFakeSymbol());

		return matches ? action : Action.byId(-1)[0];
	}

	private boolean matches(int id, int symbol)
	{
		return id <= 0 || symbol == 1 && (id == 1 || id == 2) || symbol == 2 && (id == 3 || id == 4) || symbol == 3 && (id == 5 || id == 6);
	}

	private void addPlayer(Player player)
	{
		players.add(player);
	}

	public List<Player> getPlayers()
	{
		return Collections.unmodifiableList(players);
	}

	public Player getPlayer(Position position)
	{
		for(Player player : players)
		{
			if(player.getPosition().equals(position))
			{
				return player;
			}
		}

		return null;
	}

	private void removePlayer(Player player)
	{
		players.remove(player);
		player.die();

		if(players.isEmpty())
		{
			stop();
		}
	}

	private boolean hasPlayerOn(Position position)
	{
		for(Player player : players)
		{
			if(player.getPosition().equals(position))
			{
				return true;
			}
		}

		return false;
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