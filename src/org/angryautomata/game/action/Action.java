package org.angryautomata.game.action;

import java.util.ArrayList;
import java.util.List;

import org.angryautomata.game.Game;
import org.angryautomata.game.Player;

public abstract class Action
{
	private final int id;
	private final boolean changesMap;

	protected Action(int id, boolean changesMap)
	{
		this.id = id;
		this.changesMap = changesMap;
	}

	public int getId()
	{
		return id;
	}

	public boolean changesMap()
	{
		return changesMap;
	}

	public abstract void execute(Game game, Player player);

	public static Action[] byId(int id)
	{
		List<Action> actions = new ArrayList<>();

		switch(id)
		{
			case -1:
			{
				actions.add(new Nothing());

				break;
			}

			case 0:
			{
				actions.add(new MoveNorth());
				actions.add(new MoveEast());
				actions.add(new MoveSouth());
				actions.add(new MoveWest());

				break;
			}

			case 1:
			{
				actions.add(new TrapLake());

				break;
			}

			case 2:
			{
				actions.add(new ConsumeLake());

				break;
			}

			case 3:
			{
				actions.add(new TrapMeadow());

				break;
			}

			case 4:
			{
				actions.add(new ConsumeMeadow());

				break;
			}

			case 5:
			{
				actions.add(new TrapForest());

				break;
			}

			case 6:
			{
				actions.add(new ConsumeForest());

				break;
			}

			default:
			{
				actions.add(new Nothing());

				break;
			}
		}

		return actions.toArray(new Action[0]);
	}
}
