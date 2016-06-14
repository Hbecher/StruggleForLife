package org.angryautomata.game.action;

import org.angryautomata.game.Game;
import org.angryautomata.game.Player;
import org.angryautomata.game.Position;

public class MoveEast extends Action
{
	public MoveEast()
	{
		super(0);
	}

	@Override
	public void execute(Game game, Player player)
	{
		Position position = player.getPosition();
		player.moveTo(game.torusPos(position.getX() + 1, position.getY()));
		player.updateGradient(-1);
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
