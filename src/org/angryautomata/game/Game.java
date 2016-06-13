package org.angryautomata.game;


import java.util.*;

import javafx.application.Platform;
import org.angryautomata.Controller;
import org.angryautomata.game.action.Action;
import org.angryautomata.game.scenery.Desert;
import org.angryautomata.game.scenery.Meadow;
import org.angryautomata.game.scenery.Scenery;

public class Game implements Runnable
{
	private final Board board;
	private final Automaton[] automata;
	private final List<Player> players = new ArrayList<>();
	private final Map<Position, LinkedList<Update>> toUpdate = new HashMap<>();
	private final Controller controller;
	private long tickSpeed = 200L;
	private boolean pause = true, run = true;
	private int ticks = 0;
	private boolean mapUpdated = false;

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

			for(Map.Entry<Player, Position> entry : players.entrySet())
			{
				Player player = entry.getKey();

				Position self = entry.getValue();

				Scenery o = board.getSceneryAt(self);
				Scenery n = board.getSceneryAt(board.torusPos((self.getX()),self.getY()-1));
				Scenery e = board.getSceneryAt(board.torusPos(self.getX()+1,self.getY()));
				Scenery s = board.getSceneryAt(board.torusPos(self.getX(),self.getY()+1));
				Scenery w = board.getSceneryAt(board.torusPos(self.getX()-1,self.getY()));

				if(player.canClone())
				{
					clones.add(player.createClone());
				}
				else
				{
					Action action = action(player, o, n, e, s, w);

					if(action == Action.NOTHING)
					{
						player.updateGradient(-1);
					}
					else if(action == Action.NORD )
					{
						entry.setValue(board.torusPos(self.getX() , self.getY() - 1));

						player.updateGradient(-1);
					}
					else if(action == Action.SUD )
					{
						entry.setValue(board.torusPos(self.getX() , self.getY() + 1));

						player.updateGradient(-1);
					}
					else if(action == Action.EAST )
					{
						entry.setValue(board.torusPos(self.getX() +1, self.getY()));

						player.updateGradient(-1);
					}
					else if(action == Action.WEST )
					{
						entry.setValue(board.torusPos(self.getX() -1, self.getY()));

						player.updateGradient(-1);
					}
					else if(action == Action.POLLUTE || action == Action.CONTAMINATE || action == Action.POISON)
					{
						board.getSceneryAt(self).setTrapped(true);

						player.updateGradient(-1);
					}
					else
					{
						if(action == Action.DRAW)
						{
							player.updateGradient(o.gradient());

							board.setSceneryAt(self, new Desert());
						}
						else if(action == Action.HARVEST)
						{
							player.updateGradient(o.gradient());

							board.setSceneryAt(self, new Desert());
						}
						else if(action == Action.CUT)
						{
							player.updateGradient(o.gradient());

							board.setSceneryAt(self, new Meadow(false));
						}
					action.execute(this, player, self);

					if(!toUpdate.containsKey(self))
					{
						toUpdate.put(self, new LinkedList<>());
					}

					if(mapUpdated)
					{
						LinkedList<Update> pending = toUpdate.get(self);
						Update update = new Update(o.getFakeSymbol(), 50);
						pending.addFirst(update);

						mapUpdated = false;
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

			for(Player player : clones)
			{
				addPlayer(player, board.randomPos());
			}

			dead.forEach(this::removePlayer);

			int randomTileUpdates = (int) ((height * width) * 0.2F);

			for(int k = 0; k < randomTileUpdates; k++)
			{
				Position rnd = board.randomPos();
				LinkedList<Update> updates = toUpdate.get(rnd);

				if(updates != null && !updates.isEmpty())
				{
					Update update = updates.peekLast();

					if(update.canUpdate())
					{
						board.setSceneryAt(rnd, Scenery.byId(update.getPrevSymbol()));

						updates.removeLast();
					}
					else
					{
						update.countDown();
					}
				}
			}

			Platform.runLater(() -> controller.update(Collections.unmodifiableList(players)));

			for(Player player : clones)
			{
				addPlayer(player);
			}

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

	private Action action(Player player, Scenery o, Scenery n, Scenery e, Scenery s, Scenery w)
	{
		// balise - consommer ou pieger - migrer selon autour
		int state = player.getState();
		Position origin = player.getAutomaton().getOrigin();
		int id = board.getSceneryAt(board.torusPos(origin.getX() + state, origin.getY() + o.getFakeSymbol())).getSymbol();
		Action action = Action.byId(id);

		if(id == 0)
			{
				int symboln = n.getSymbol();
				int symbole = e.getSymbol();
				int symbols = s.getSymbol();
				int symbolw = w.getSymbol();
				List l = new ArrayList<Action>();

				if (symboln!=0) { l.add(Action.byId(-1));}
				if (symbols!=0) { l.add(Action.byId(-2));}
				if (symbole!=0) { l.add(Action.byId(-3));}
				if (symbolw!=0) { l.add(Action.byId(-4));}
				if(l.isEmpty())
				{
					l.add(Action.byId(-1));
					l.add(Action.byId(-2));
					l.add(Action.byId(-3));
					l.add(Action.byId(-4));
				}
				return (Action) l.get((int) (Math.random() * l.size()));
			}

		return matches(action, o) ? action : Action.NOTHING;
	}

	private boolean matches(Action action, Scenery scenery)
	{
		return action.getId() <= 0 || scenery.getFakeSymbol() == 1 && (action.getId() == 1 || action.getId() == 2) || scenery.getFakeSymbol() == 2 && (action.getId() == 3 || action.getId() == 4) || scenery.getFakeSymbol() == 3 && (action.getId() == 5 || action.getId() == 6);
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

		mapUpdated = true;
	}
}