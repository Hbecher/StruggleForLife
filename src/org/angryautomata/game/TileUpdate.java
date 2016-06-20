package org.angryautomata.game;

/**
 * Représente une mise à jour de décor.<br />
 * Lorsqu'un décor est modifié, celui-ci est ajouté à une file d'attente pour être restauré ultérieurement.<br />
 * La vérification n'ayant pas lieu à chaque tour, on parlera de "tick" pour décrire une vérification.
 */
public class TileUpdate
{
	/**
	 * Le décor précédent
	 */
	private final int prevSymbol;

	/**
	 * Le nombre de ticks à attendre pour régénérer le décor
	 */
	private final int regenTicks;

	/**
	 * Le nombre de ticks restants
	 */
	private int ticksLeft;

	/**
	 * @param prevSymbol le décor précédent
	 * @param regenTicks le nombre de ticks à attendre
	 */
	public TileUpdate(int prevSymbol, int regenTicks)
	{
		this.prevSymbol = prevSymbol;
		this.regenTicks = ticksLeft = regenTicks;
	}

	public void countDown()
	{
		ticksLeft--;
	}

	public boolean canUpdate()
	{
		return ticksLeft <= 0;
	}

	/**
	 * Lorsqu'une mise à jour est mise en attente alors qu'une autre est déjà en attente, on remet les tivks à attendre à la valeur initiale.
	 */
	public void reset()
	{
		ticksLeft = regenTicks;
	}

	public int ticksLeft()
	{
		return ticksLeft;
	}

	public int getPrevSymbol()
	{
		return prevSymbol;
	}
}
