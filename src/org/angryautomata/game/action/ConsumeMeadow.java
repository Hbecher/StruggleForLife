package org.angryautomata.game.action;

import org.angryautomata.game.Game;
import org.angryautomata.game.Population;
import org.angryautomata.game.Position;
import org.angryautomata.game.scenery.Desert;
import org.angryautomata.game.scenery.Scenery;

public class ConsumeMeadow extends Action
{
	public ConsumeMeadow()
	{
		super(4);
	}

	@Override
	public void execute(Game game, Population population)
	{
		Position self = population.getPosition();
		Scenery scenery = game.getSceneryAt(self);

		game.setSceneryAt(self, new Desert());
		population.updateGradient(scenery.getGradient());
	}

	@Override
	public boolean updatesMap()
	{
		return true;
	}

	@Override
	public boolean updatesPosition()
	{
		return false;
	}
}
