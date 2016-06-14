package org.angryautomata.game.action;

import org.angryautomata.game.Game;
import org.angryautomata.game.Player;
import org.angryautomata.game.Position;

public class TrapForest extends Action
{
	public TrapForest()
	{
		super(5);
	}

	@Override
	public void execute(Game game, Player player)
	{
		Position self = player.getPosition();

		game.getSceneryAt(self).setTrapped(true);
		player.updateGradient(-1);
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
