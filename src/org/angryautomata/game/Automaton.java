package org.angryautomata.game;

public class Automaton
{
	private final int[][] transitions;
	private final int[][] actions;
	private Position origin;

	public Automaton(int[][] transitions, int[][] actions)
	{
		this(transitions, actions, null);
	}

	public Automaton(int[][] transitions, int[][] actions, Position origin)
	{
		this.transitions = transitions;
		this.actions = actions;
		this.origin = origin;
	}

	public int nextState(int state, int symbol)
	{
		return transitions[symbol][state];
	}

	public int initialAction(int state, int symbol)
	{
		return actions[symbol][state];
	}

	public int numberOfStates()
	{
		return transitions[0].length;
	}

	public Position getOrigin()
	{
		return origin;
	}

	public void setOrigin(Position origin)
	{
		this.origin = origin;
	}
}
