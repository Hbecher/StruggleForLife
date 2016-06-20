package org.angryautomata.game;

/**
 * Représente un automate.<br />
 * Un automate se définit par un tableau de transitions et un tableau d'actions initiales, ainsi qu'une position dans le jeu.
 */
public class Automaton
{
	/**
	 * Les transitions.
	 */
	private final int[][] transitions;

	/**
	 * Les actions initiales.
	 */
	private final int[][] actions;

	/**
	 * La position de l'automate dans le jeu.<br />
	 * L'automate récupèrera le décor à la position (originX + état, originY + symbole) pour calculer l'état suivant.
	 */
	private Position origin;

	/**
	 * Constructeur d'automate.
	 *
	 * @param transitions les transitions
	 * @param actions     les actions initiales
	 * @param origin      la position sur le plateau de jeu
	 */
	public Automaton(int[][] transitions, int[][] actions, Position origin)
	{
		this.transitions = transitions;
		this.actions = actions;
		this.origin = origin;
	}

	/**
	 * Retourne l'état suivant de l'automate.
	 *
	 * @param state  l'état courant
	 * @param symbol le symbole lu
	 * @return L'état suivant de l'automate
	 */
	public int nextState(int state, int symbol)
	{
		return transitions[symbol][state];
	}

	/**
	 * Retourne l'action initialement programmée de l'automate à un certain état à la lecture d'un certain symbole.
	 *
	 * @param state  l'état
	 * @param symbol le symbole
	 * @return L'action initialement programmée
	 */
	public int initialAction(int state, int symbol)
	{
		return actions[symbol][state];
	}

	/**
	 * Retourne le nombre d'états de l'automate.
	 */
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
