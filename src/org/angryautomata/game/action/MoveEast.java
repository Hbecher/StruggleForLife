package org.angryautomata.game.action;

import org.angryautomata.game.Game;
import org.angryautomata.game.Population;
import org.angryautomata.game.Position;

public class MoveEast extends Action implements Move
{
	public MoveEast()
	{
		super(0);
	}

	@Override
	public void execute(Game game, Population population)
	{
		Position position = population.getPosition();
		population.moveTo(game.torusPos(position.getX() + 1, position.getY()));
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
