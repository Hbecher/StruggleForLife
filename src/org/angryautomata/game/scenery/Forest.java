package org.angryautomata.game.scenery;

public class Forest extends Scenery
{
	public Forest(boolean trapped)
	{
		super(3, new int[]{-1, 0, 5, 6}, 5, trapped);
	}
}