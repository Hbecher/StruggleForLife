package org.angryautomata.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente une équipe.
 */
public class Team
{
	/**
	 * L'équipe "pas d'équipe"
	 */
	public static final Team NO_TEAM = new Team("-")
	{
		@Override
		public void addPlayer(Player player)
		{
		}

		@Override
		public void removePlayer(Player player)
		{
		}

		@Override
		public boolean isTeammate(Player player)
		{
			return false;
		}
	};

	/**
	 * Son nom
	 */
	private final String name;

	/**
	 * Les joueurs dans l'équipe
	 */
	private final List<Player> team = new ArrayList<>();

	public Team(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void addPlayer(Player player)
	{
		if(!team.contains(player))
		{
			team.add(player);
		}
	}

	public void removePlayer(Player player)
	{
		team.remove(player);
	}

	public boolean isTeammate(Player player)
	{
		return team.contains(player);
	}
}
