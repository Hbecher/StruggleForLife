package org.angryautomata.game.action;

import org.angryautomata.game.Game;
import org.angryautomata.game.Player;
import org.angryautomata.game.Position;

public class TrapLake extends Action
{
	public TrapLake()
	{
		super(1, false);
	}

	@Override
	public void execute(Game game, Player player)
	{
		Position self = player.getPosition();

		game.getSceneryAt(self).setTrapped(true);
		player.updateGradient(-1);
	}
}
