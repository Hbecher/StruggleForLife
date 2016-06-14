package org.angryautomata.game.scenery;

public class Scenery
{
	private static final int SCENERIES = 4;

	private final int symbol, fakeSymbol;
	private final int gradient;
	private final int[] validActions;
	private boolean trapped;

	protected Scenery(int symbol, int[] validActions, int gradient, boolean trapped)
	{
		this.symbol = trapped ? symbol + SCENERIES - 1 : symbol;
		fakeSymbol = symbol;
		this.gradient = trapped ? -gradient : gradient;
		this.validActions = validActions;
		this.trapped = trapped;
	}

	public static int sceneries()
	{
		return SCENERIES;
	}

	public static Scenery byId(int id)
	{
		switch(id)
		{
			case 0:
			{
				return new Desert();
			}

			case 1:
			{
				return new Lake(false);
			}

			case 2:
			{
				return new Meadow(false);
			}

			case 3:
			{
				return new Forest(false);
			}

			case 4:
			{
				return new Lake(true);
			}

			case 5:
			{
				return new Meadow(true);
			}

			case 6:
			{
				return new Forest(true);
			}

			default:
			{
				return new Desert();
			}
		}
	}

	public int getFakeSymbol()
	{
		return fakeSymbol;
	}

	public int getSymbol()
	{
		return symbol;
	}

	public int getGradient()
	{
		return gradient;
	}

	public boolean isTrapped()
	{
		return trapped;
	}

	public void setTrapped(boolean trapped)
	{
		this.trapped = trapped;
	}

	public int[] getValidActions()
	{
		return validActions;
	}
}
