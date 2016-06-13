package org.angryautomata.game.action;

import org.angryautomata.game.Game;
import org.angryautomata.game.Player;
import org.angryautomata.game.Position;

public class MoveNorth extends Action
{
	public MoveNorth()
	{
		super(0, false);
	}

	@Override
	public void execute(Game game, Player player)
	{
		Position position = player.getPosition();
		player.moveTo(game.torusPos(position.getX(), position.getY() - 1));
		player.updateGradient(-1);
	}
}
