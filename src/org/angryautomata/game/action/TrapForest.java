package org.angryautomata.game.action;

import org.angryautomata.game.Game;
import org.angryautomata.game.Images;
import org.angryautomata.game.Population;
import org.angryautomata.game.Position;

public class TrapForest extends Action
{
	public TrapForest()
	{
		super(5, Images.forage_trapped_back, Images.trap, Images.forage_trapped_back, Images.trap, Images.vide, Images.vide);
	}

	@Override
	public void execute(Game game, Population population)
	{
		Position self = population.getPosition();

		game.getSceneryAt(self).setTrapped(true);
		population.updateGradient(-1);
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
