package org.angryautomata.game.scenery;

public class Meadow extends Scenery
{
	public Meadow(boolean trapped)
	{
		super(2, new int[]{-1, 0, 3, 4}, 3, trapped);
	}
}