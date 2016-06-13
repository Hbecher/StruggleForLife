package org.angryautomata.game.action;

import org.angryautomata.game.Game;
import org.angryautomata.game.Player;

public class Nothing extends Action
{
	public Nothing()
	{
		super(-1, false);
	}

	@Override
	public void execute(Game game, Player player)
	{
		player.updateGradient(-1);
	}
}
