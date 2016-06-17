package org.angryautomata.game.action;

import org.angryautomata.game.Game;
import org.angryautomata.game.Images;
import org.angryautomata.game.Population;
import org.angryautomata.game.Position;
import org.angryautomata.game.scenery.Desert;
import org.angryautomata.game.scenery.Scenery;

public class ConsumeLake extends Action
{
	public ConsumeLake()
	{
		super(2, Images.forage_back, Images.forage, Images.forage_trapped_back, Images.forage_trapped, Images.vide, Images.vide);
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
