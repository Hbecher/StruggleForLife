package org.angryautomata.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.scene.paint.Color;

public class Player
{
	private static final Color[] PLAYER_COLORS = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.ORANGE, Color.PURPLE, Color.BROWN, Color.GRAY};
	private static final int MARKER_COOLDOWN = 100, REGEN_COOLDOWN = 100;

	private final Automaton automaton;
	private final String name;
	private final int playerNumber;
	private final Color color;
	private final List<Population> populations = new ArrayList<>();
	private Team team = Team.NO_TEAM;
	private int markerCooldown = MARKER_COOLDOWN, regenCooldown = REGEN_COOLDOWN;

	public Player(Automaton automaton, String name, int playerNumber)
	{
		this.automaton = automaton;
		this.name = name;
		this.playerNumber = playerNumber;
		color = PLAYER_COLORS[playerNumber];
	}

	public static Color color(int playerNumber)
	{
		return PLAYER_COLORS[playerNumber];
	}

	public Automaton getAutomaton()
	{
		return automaton;
	}

	public String getName()
	{
		return name;
	}

	public List<Population> getPopulations()
	{
		return Collections.unmodifiableList(populations);
	}

	public void addPopulation(Population population)
	{
		if(!populations.contains(population))
		{
			populations.add(population);
		}
	}

	public boolean removePopulation(Population population)
	{
		return populations.remove(population);
	}

	public int getPlayerNumber()
	{
		return playerNumber;
	}

	public Color getColor()
	{
		return color;
	}

	public int getTotalGradient()
	{
		int gradient = 0;

		for(Population population : populations)
		{
			gradient += population.getGradient();
		}

		return gradient;
	}

	public int getMeanGradient()
	{
		return getTotalGradient() / populations.size();
	}

	public boolean isDead()
	{
		if(populations.isEmpty())
		{
			return true;
		}

		for(Population population : populations)
		{
			if(population.isDead())
			{
				return true;
			}
		}

		return false;
	}

	public void decMarkerCooldown()
	{
		if(markerCooldown > 0)
		{
			markerCooldown--;
		}
	}

	public void decRegenCooldown()
	{
		if(regenCooldown > 0)
		{
			regenCooldown--;
		}
	}

	public void resetCooldowns()
	{
		markerCooldown = MARKER_COOLDOWN;
		regenCooldown = REGEN_COOLDOWN;
	}

	public boolean canPlaceMarker()
	{
		return markerCooldown <= 0;
	}

	public boolean canRegenAutomaton()
	{
		return regenCooldown <= 0;
	}

	public Team getTeam()
	{
		return team;
	}

	public void setTeam(Team team)
	{
		this.team = team == null ? Team.NO_TEAM : team;
	}

	public boolean isTeammate(Player player)
	{
		return player.getTeam().isTeammate(this);
	}
}
