package org.angryautomata.game.action;

import java.util.ArrayList;
import java.util.List;

import org.angryautomata.game.Game;
import org.angryautomata.game.Population;

/**
 * Représente une action.<br />
 * Chaque action dispose d'un identifiant entier, associé à un élément de décor.
 */
public abstract class Action
{
	private final int id;

	/**
	 * Constructeur d'une action.
	 *
	 * @param id l'id de l'action
	 */
	protected Action(int id)
	{
		this.id = id;
	}

	public int getId()
	{
		return id;
	}

	/**
	 * L'action à exécuter.
	 *
	 * @param game       le jeu
	 * @param population la population qui exécute l'action
	 */
	public abstract void execute(Game game, Population population);

	/**
	 * Indique si l'action modifie la carte, pour notifier le jeu qu'un décor a changé.
	 *
	 * @return si l'action modifie la carte
	 */
	public abstract boolean updatesMap();

	/**
	 * Indique si l'action modifie la position de la population.
	 *
	 * @return si l'action modifie la position de la population
	 */
	public abstract boolean updatesPosition();

	/**
	 * Retourne la liste des actions liées à cet identifiant.<br />
	 * Une liste est renvoyée pour pouvoir représenter les quatre déplacements cardinaux.<br />
	 * Dans le cas général, ne renverra qu'on singleton.
	 *
	 * @param id l'identifiant
	 * @return La liste d'actions associées
	 */
	public static List<Action> byId(int id)
	{
		List<Action> actions = new ArrayList<>();

		switch(id)
		{
			case -1:
			{
				actions.add(new Nothing());

				break;
			}

			case 0:
			{
			actions.add(new MoveNorth());
			actions.add(new MoveEast());
			actions.add(new MoveSouth());
			actions.add(new MoveWest());

			break;
			}

			case 1:
			{
				actions.add(new TrapLake());

				break;
			}

			case 2:
			{
				actions.add(new ConsumeLake());

				break;
			}

			case 3:
			{
				actions.add(new TrapMeadow());

				break;
			}

			case 4:
			{
				actions.add(new ConsumeMeadow());

				break;
			}

			case 5:
			{
				actions.add(new TrapForest());

				break;
			}

			case 6:
			{
				actions.add(new ConsumeForest());

				break;
			}

			default:
			{
				actions.add(new Nothing());

				break;
			}
		}

		return actions;
	}
}
