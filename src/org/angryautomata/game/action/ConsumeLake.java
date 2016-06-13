package org.angryautomata.game.action;

import org.angryautomata.game.Game;
import org.angryautomata.game.Player;
import org.angryautomata.game.Position;
import org.angryautomata.game.scenery.Desert;
import org.angryautomata.game.scenery.Scenery;

public class ConsumeLake extends Action
{
	public ConsumeLake()
	{
		super(2, true);
	}

	@Override
	public void execute(Game game, Player player)
	{
		Position self = player.getPosition();
		Scenery scenery = game.getSceneryAt(self);

		game.setSceneryAt(self, new Desert());
		player.updateGradient(scenery.getGradient());
	}
}
