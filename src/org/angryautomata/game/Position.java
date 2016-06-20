package org.angryautomata.game;

/**
 * Représente une position (x, y) sur le plateau de jeu.
 */
public class Position
{
	/**
	 * L'origine
	 */
	public static final Position ORIGIN = new Position(0, 0);

	private final int x;
	private final int y;

	public Position(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o)
		{
			return true;
		}

		if(!(o instanceof Position))
		{
			return false;
		}

		Position that = (Position) o;

		return getX() == that.getX() && getY() == that.getY();

	}

	@Override
	public int hashCode()
	{
		return 31 * getX() + getY();
	}
}
