package org.angryautomata.game;

import javafx.scene.paint.Color;
import org.angryautomata.game.scenery.Scenery;

public class Player
{
	private final Automaton automaton;
	private final int initGradient = 50;
	private final int team;
	private int state;
	private int gradient = initGradient;
	private Position position, prev = null;

	public Player(Automaton automaton, int state, int team, Position position)
	{
		this.automaton = automaton;
		this.state = state;
		this.team = team;
		this.position = position;

		automaton.addPlayer(this);
	}

	public Automaton getAutomaton()
	{
		return automaton;
	}

	public int getState()
	{
		return state;
	}

	public int nextState(int symbol)
	{
		return state = automaton.nextState(state, symbol);
	}

	public int getGradient()
	{
		return gradient;
	}

	public int getTeam()
	{
		return team;
	}

	public Position getPosition()
	{
		return position;
	}

	public void moveTo(Position position)
	{
		prev = this.position;
		this.position = position;
	}

	public void move(int relX, int relY)
	{
		prev = position;
		position = new Position(position.getX() + relX, position.getY() + relY);
	}

	public Color getColor()
	{
		return automaton.getColor();
	}

	public void updateGradient(int grad)
	{
		gradient += grad;
	}

	public boolean isDead()
	{
		return gradient <= 0;
	}

	public boolean canClone()
	{
		return gradient >= 100;
	}

	public Player createClone()
	{
		Position clonePos = new Position(position.getX() + (int) (Math.random() * 3.0D) - 1, position.getY() + (int) (Math.random() * 3.0D) - 1);

		Player clone = new Player(automaton, 0, team, clonePos);

		clone.updateGradient(-initGradient);

		int splitGradient = gradient / 2;

		updateGradient(-splitGradient);
		clone.updateGradient(splitGradient);

		return clone;
	}

	public void die()
	{
		automaton.removePlayer(this);
	}

	public boolean isInTrouble()
	{
		Position origin = automaton.getOrigin();
		int originX = origin.getX(), originY = origin.getY();
		int x = position.getX(), y = position.getY();

		return x >= originX && x < originX + automaton.numberOfStates() && y >= originY && y < originY + Scenery.sceneries();
	}
}