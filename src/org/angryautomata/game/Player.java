package org.angryautomata.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.scene.paint.Color;

/**
 * Représente un joueur.<br />
 * Un joueur possède un automate, des populations, une équipe et une couleur / un numéro de joueur.<br />
 * Une partie se joue à 8 joueurs max.
 */
public class Player
{
	/**
	 * Les couleurs des 8 joueurs possibles
	 */
	private static final Color[] PLAYER_COLORS = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.ORANGE, Color.PURPLE, Color.BROWN, Color.GRAY};

	/**
	 * Le temps de rechargement des balises
	 */
	private static final int MARKER_COOLDOWN = 100;

	/**
	 * Le temps de rechargement de la réparation de l'automate
	 */
	private static final int REGEN_COOLDOWN = 100;

	/**
	 * L'automate du joueur
	 */
	private final Automaton automaton;

	/**
	 * Le nom du joueur
	 */
	private final String name;

	/**
	 * Le numéro du joueur
	 */
	private final int playerNumber;

	/**
	 * La couleur du joueur
	 */
	private final Color color;

	/**
	 * Les populations
	 */
	private final List<Population> populations = new ArrayList<>();

	/**
	 * L'équipe du joueur
	 */
	private Team team = Team.NO_TEAM;

	/**
	 * Les temps de rechargement
	 */
	private int markerCooldown = MARKER_COOLDOWN, regenCooldown = REGEN_COOLDOWN;

	/**
	 * Constructeur de joueur
	 *
	 * @param automaton    son automate
	 * @param name         son nom
	 * @param playerNumber le numéro de joueur
	 */
	public Player(Automaton automaton, String name, int playerNumber)
	{
		this.automaton = automaton;
		this.name = name;
		this.playerNumber = playerNumber;
		color = PLAYER_COLORS[playerNumber];
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

	/**
	 * @return Les points totaux du joueur.
	 */
	public int getTotalGradient()
	{
		int gradient = 0;

		for(Population population : populations)
		{
			gradient += population.getGradient();
		}

		return gradient;
	}

	/**
	 * @return Les points moyens du joueur par population.
	 */
	public int getMeanGradient()
	{
		return getTotalGradient() / populations.size();
	}

	/**
	 * @return Si le joueur est mort
	 */
	public boolean isDead()
	{
		// aucune population
		if(populations.isEmpty())
		{
			return true;
		}

		// on vérifie si les populations sont en vie
		for(Population population : populations)
		{
			if(population.isDead())
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Décrémente le nombre de tours à attendre pour réutiliser une balise.
	 */
	public void decMarkerCooldown()
	{
		if(markerCooldown > 0)
		{
			markerCooldown--;
		}
	}

	/**
	 * Décrémente le nombre de tours à attendre pour réparer l'automate.
	 */
	public void decRegenCooldown()
	{
		if(regenCooldown > 0)
		{
			regenCooldown--;
		}
	}

	/**
	 * Remet au max les temps de rechargements après utilisation d'un bonus.
	 */
	public void resetCooldowns()
	{
		markerCooldown = MARKER_COOLDOWN;
		regenCooldown = REGEN_COOLDOWN;
	}

	/**
	 * @return Si une balise peut être placée
	 */
	public boolean canPlaceMarker()
	{
		return markerCooldown <= 0;
	}

	/**
	 * @return Si l'automate peut être réparé
	 */
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

	/**
	 * @return Si le joueur est dans la même équipe
	 */
	public boolean isTeammate(Player player)
	{
		return player.getTeam().isTeammate(this);
	}
}
