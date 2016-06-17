package org.angryautomata.game.action;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.image.Image;
import org.angryautomata.game.Game;
import org.angryautomata.game.Population;

public abstract class Action
{
	private final int id;
	private final Image back, front, trapBack, trapFront, from, to;

	protected Action(int id, Image back, Image front, Image trapBack, Image trapFront, Image from, Image to)
	{
		this.id = id;
		this.back = back;
		this.front = front;
		this.trapBack = trapBack;
		this.trapFront = trapFront;
		this.from = from;
		this.to = to;
	}

	public int getId()
	{
		return id;
	}

	public Image getBackImage()
	{
		return back;
	}

	public Image getFrontImage()
	{
		return front;
	}

	public Image getTrapBackImage()
	{
		return trapBack;
	}

	public Image getTrapFrontImage()
	{
		return trapFront;
	}

	public Image getFromImage()
	{
		return from;
	}

	public Image getToImage()
	{
		return to;
	}

	public abstract void execute(Game game, Population population);

	public abstract boolean updatesMap();

	public abstract boolean updatesPosition();

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
