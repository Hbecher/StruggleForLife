package org.angryautomata.game.scenery;

import javafx.scene.paint.Color;
import org.angryautomata.game.Images;

public class Desert extends Scenery
{
	public Desert()
	{
		super(0, new int[]{-1, 0}, 0, false, Color.GOLD, Images.desert);
	}
}
