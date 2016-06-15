package org.angryautomata.game.action;

import org.angryautomata.game.Game;
import org.angryautomata.game.Population;

public class Nothing extends Action
{
	public Nothing()
	{
		super(-1);
	}

	@Override
	public void execute(Game game, Population population)
	{
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
		return false;
	}
}
