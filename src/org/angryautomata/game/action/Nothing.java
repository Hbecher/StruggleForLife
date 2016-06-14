package org.angryautomata.game.action;

import org.angryautomata.game.Game;
import org.angryautomata.game.Player;

public class Nothing extends Action
{
	public Nothing()
	{
		super(-1);
	}

	@Override
	public void execute(Game game, Player player)
	{
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
		return false;
	}
}
