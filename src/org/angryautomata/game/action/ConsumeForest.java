package org.angryautomata.game.action;

import org.angryautomata.game.Game;
import org.angryautomata.game.Player;
import org.angryautomata.game.Position;
import org.angryautomata.game.scenery.Meadow;
import org.angryautomata.game.scenery.Scenery;

public class ConsumeForest extends Action
{
	public ConsumeForest()
	{
		super(6, true);
	}

	@Override
	public void execute(Game game, Player player)
	{
		Position self = player.getPosition();
		Scenery scenery = game.getSceneryAt(self);

		game.setSceneryAt(self, new Meadow(false));
		player.updateGradient(scenery.getGradient());
	}
}
