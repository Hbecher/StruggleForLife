package org.angryautomata.game.action;

import org.angryautomata.game.Game;
import org.angryautomata.game.Population;
import org.angryautomata.game.Position;

public class MoveNorth extends Action implements Move
{
	public MoveNorth()
	{
		super(0);
	}

	@Override
	public void execute(Game game, Population population)
	{
		Position position = population.getPosition();
		population.moveTo(game.torusPos(position.getX(), position.getY() - 1));
		population.updateGradient(-1);
	}

	@Override
	public boolean updatesMap()
	{
		return false;
	}

	@Override
	public boolean updatesPosition()
	{
		return true;
	}
}
