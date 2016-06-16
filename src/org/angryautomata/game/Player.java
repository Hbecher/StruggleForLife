package org.angryautomata.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.scene.paint.Color;

public class Player
{
	private static final int MARKER_COOLDOWN = 100, REGEN_COOLDOWN = 100;

	private final Automaton automaton;
	private final String name;
	private final Color color;
	private final List<Population> populations = new ArrayList<>();
	private int markerCooldown = MARKER_COOLDOWN, regenCooldown = REGEN_COOLDOWN;

	public Player(Automaton automaton, String name, int rgb)
	{
		this.automaton = automaton;
		this.name = name;
		color = Color.rgb((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
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
}
