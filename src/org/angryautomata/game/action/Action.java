package org.angryautomata.game.action;

import java.util.ArrayList;
import java.util.List;

import org.angryautomata.game.Game;
import org.angryautomata.game.Player;
import org.angryautomata.game.Position;
import org.angryautomata.game.scenery.Scenery;

public abstract class Action
{
	private static final List<Action> ACTIONS = new ArrayList<>();

	private final int id;

	public Action(int id)
	{
		this.id = id;
	}

	public int getId()
	{
		return id;
	}

	public abstract void execute(Game game, Player player);

	public static Action byId(int id)
	{
		for(Action action : ACTIONS)
		{
			if(action.getId() == id)
			{
				return action;
			}
		}

		return null;
	}

	public static void register(Action action)
	{
		ACTIONS.add(action);
	}

	static
	{
		register(new Action(-1)
		{
			@Override
			public void execute(Game game, Player player, Object... params)
			{
				player.updateGradient(-1);
			}
		});
		register(new Action(0)
		{
			@Override
			public void execute(Game game, Player player)
			{
				Scenery n = board.getSceneryAt(board.torusPos((self.getX()), self.getY() - 1));
				Scenery e = board.getSceneryAt(board.torusPos(self.getX() + 1, self.getY()));
				Scenery s = board.getSceneryAt(board.torusPos(self.getX(), self.getY() + 1));
				Scenery w = board.getSceneryAt(board.torusPos(self.getX() - 1, self.getY()));;
				int relX, relY;

				switch(dir)
				{
					case NORTH:
						relX = 0;
						relY = -1;
						break;
					case EAST:
						relX = 1;
						relY = 0;
						break;
					case SOUTH:
						relX = 0;
						relY = 1;
						break;
					case WEST:
						relX = -1;
						relY = 0;
						break;
					default:
						relX = relY = 0;
						break;
				}

				player.move(relX, relY);
				player.updateGradient(-1);
			}
		});
		register(new Action(1)
		{
			@Override
			public void execute(Game game, Player player)
			{
				Position self = player.getPosition();

				//game.setSceneryAt(self, new Lake());

				player.updateGradient(-1);
			}
		});
		register(new Action(2)
		{
			@Override
			public void execute(Game game, Player player)
			{
				Position self = player.getPosition();

				game.setSceneryAt(self, game.getSceneryAt(self).getTrapped());

				player.updateGradient(-1);
			}
		});
		register(new Action(1)
		{
			@Override
			public void execute(Game game, Player player)
			{
				Position self = player.getPosition();

				game.setSceneryAt(self, game.getSceneryAt(self).getTrapped());

				player.updateGradient(-1);
			}
		});
	}
}
