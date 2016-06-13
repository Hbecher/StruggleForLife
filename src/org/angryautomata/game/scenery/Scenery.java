package org.angryautomata.game.scenery;

import java.util.ArrayList;
import java.util.List;

import org.angryautomata.game.action.Action;

public class Scenery
{
	private static final List<Scenery> SCENERIES = new ArrayList<>();

	static
	{
		SCENERIES.add(new Scenery(0, null, 0));
		SCENERIES.add(new Scenery(1, null, 2));
		SCENERIES.add(new Scenery(2, null, 3));
		SCENERIES.add(new Scenery(3, null, 5));
	}

	private final int symbol;
	private final int gradient;
	private final Action[] validActions;

	private Scenery(int symbol, Action[] validActions, int gradient)
	{
		this.symbol = symbol;
		this.gradient = gradient;
		this.validActions = validActions;
	}

	public static int sceneries()
	{
		return SCENERIES.size();
	}

	public static Scenery byId(int id)
	{
		for(Scenery scenery : SCENERIES)
		{
			if(scenery.getSymbol() == id)
			{
				return scenery;
			}
		}

		return null;
	}

	public int getFakeSymbol()
	{
		return symbol >= sceneries() ? symbol + 1 - sceneries() : symbol;
	}

	public int getSymbol()
	{
		return symbol;
	}

	public int getGradient()
	{
		return gradient;
	}

	public Scenery getTrapped()
	{
		return symbol >= sceneries() ? this : new Scenery(getSymbol() + sceneries() - 1, validActions, -getGradient());
	}

	public Scenery getNotTrapped()
	{
		return symbol < sceneries() ? this : new Scenery(getSymbol() + 1 - sceneries(), validActions, -getGradient());
	}
}
