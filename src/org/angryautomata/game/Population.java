package org.angryautomata.game;

import javafx.scene.paint.Color;
import org.angryautomata.game.scenery.Scenery;

/**
 * Représente une population.<br />
 * Une population est une réalisation de l'automate associé.<br />
 * Celle-ci dispose d'un total de points propre, et connaît sa position et sa position précédente.
 */
public class Population
{
	public static final int GRADIENT_MIN = 0, GRADIENT_MAX = 100, GRADIENT_INIT = 50;

	/**
	 * Le joueur
	 */
	private final Player player;

	/**
	 * L'état
	 */
	private int state;

	/**
	 * Les points
	 */
	private int gradient = GRADIENT_INIT;

	/**
	 * La position et la position précédente
	 */
	private Position position, prev;

	/**
	 * Si cette population a joué dans le tour
	 */
	private boolean hasPlayed = false;

	/**
	 * Constructeur d'une population.
	 *
	 * @param player   le joueur possédant la population
	 * @param state    l'état initial de la population
	 * @param position la position initiale
	 */
	public Population(Player player, int state, Position position)
	{
		this.player = player;
		this.state = state;
		this.position = position;
		prev = position;

		// on ajoute cette population à la liste du joueur
		player.addPopulation(this);
	}

	public Player getPlayer()
	{
		return player;
	}

	public int getState()
	{
		return state;
	}

	/**
	 * @param symbol le symbole lu
	 * @return L'état suivant dans l'automate
	 */
	public int nextState(int symbol)
	{
		return state = player.getAutomaton().nextState(state, symbol);
	}

	public int getGradient()
	{
		return gradient;
	}

	public Team getTeam()
	{
		return player.getTeam();
	}

	public Position getPosition()
	{
		return position;
	}

	public Position getPreviousPosition()
	{
		return prev;
	}

	/**
	 * Déplace la population et sauvegarde sa position précédente.
	 *
	 * @param position la nouvelle position
	 */
	public void moveTo(Position position)
	{
		prev = this.position;
		this.position = position;
	}

	public Color getColor()
	{
		return player.getColor();
	}

	public void updateGradient(int grad)
	{
		gradient += grad;
	}

	public boolean isDead()
	{
		return gradient <= GRADIENT_MIN;
	}

	public boolean canClone()
	{
		return gradient >= GRADIENT_MAX;
	}

	/**
	 * Crée et retourne un "clone" de la population.
	 *
	 * @param game le jeu
	 * @return Le clone
	 */
	public Population createClone(Game game)
	{
		// on prend une case autour de nous
		Position clonePos = game.torusPos(position.getX() + (int) (Math.random() * 3.0D) - 1, position.getY() + (int) (Math.random() * 3.0D) - 1);

		// on crée le clone
		Population clone = new Population(player, state, clonePos);

		// on met ses points à 0
		clone.updateGradient(-GRADIENT_INIT);

		// on divise les points en 2
		int splitGradient = gradient / 2;

		// on transfère ces points de nous au clone
		updateGradient(-splitGradient);
		clone.updateGradient(splitGradient);

		return clone;
	}

	/**
	 * Retire la population du jeu
	 */
	public void die()
	{
		player.removePopulation(this);
	}

	/**
	 * @return Si la population se trouve sur un automate allié
	 */
	public boolean isOnTeamAutomaton()
	{
		Automaton automaton = player.getAutomaton();
		Position origin = automaton.getOrigin();
		int originX = origin.getX(), originY = origin.getY();
		int x = position.getX(), y = position.getY();

		return x >= originX && x < originX + automaton.numberOfStates() && y >= originY && y < originY + Scenery.sceneries();
	}

	public boolean comesFrom(Position position)
	{
		return prev.equals(position);
	}

	public boolean isTeammate(Population population)
	{
		return player.isTeammate(population.getPlayer());
	}

	public boolean hasPlayed()
	{
		return hasPlayed;
	}

	public void played(boolean hasPlayed)
	{
		this.hasPlayed = hasPlayed;
	}
}