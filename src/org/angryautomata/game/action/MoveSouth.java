package org.angryautomata.game.action;

import org.angryautomata.game.Game;
import org.angryautomata.game.Images;
import org.angryautomata.game.Population;
import org.angryautomata.game.Position;

public class MoveSouth extends Action
{
	public MoveSouth()
	{
		super(0, Images.vide, Images.vide, Images.vide, Images.vide, Images.popmigrateba, Images.popmigratebb);
	}

	@Override
	public void execute(Game game, Population population)
	{
		Position position = population.getPosition();
		population.moveTo(game.torusPos(position.getX(), position.getY() + 1));
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
