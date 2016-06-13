package org.angryautomata.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.scene.paint.Color;

public class Automaton
{
	private final int[][] transitions;
	private final int[][] actions;
	private final Position origin;
	private final String name;
	private final Color color;
	private final List<Player> players = new ArrayList<>();

	public Automaton(int[][] transitions, int[][] actions, Position origin, String name, int rgba)
	{
		this.transitions = transitions;
		this.actions = actions;
		this.origin = origin;
		this.name = name;
		color = Color.rgb((rgba >> 16) & 0xFF, (rgba >> 8) & 0xFF, rgba & 0xFF, ((rgba >> 24) & 0xFF) / 255.0D);
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

	public String getName()
	{
		return name;
	}

	public List<Player> getPlayers()
	{
		return Collections.unmodifiableList(players);
	}

	public void addPlayer(Player player)
	{
		if(!players.contains(player))
		{
			players.add(player);
		}
	}

	public boolean removePlayer(Player player)
	{
		return players.remove(player);
	}

	public Color getColor()
	{
		return color;
	}
}
