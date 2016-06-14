package org.angryautomata.game.scenery;

public class Lake extends Scenery
{
	public Lake(boolean trapped)
	{
		super(1, new int[]{-1, 0, 1, 2}, 2, trapped);
	}
}