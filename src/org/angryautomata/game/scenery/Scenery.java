package org.angryautomata.game.scenery;

import javafx.scene.image.Image;
import org.angryautomata.game.action.Action;

/**
 * Représente un élement de décor associé à une action.<br />
 * Un décor correspond à une action.
 */
public class Scenery
{
	/**
	 * Le nombre de décors visibles par les automates.<br />
	 * Cette valeur est à mettre à jour manuellement lors de l'ajout de nouveaux décors.
	 */
	private static final int SCENERIES = 4;

	/**
	 * Le symbole du décor.
	 */
	private final int symbol;

	/**
	 * Le faux symbole vu par les automates.
	 */
	private final int fakeSymbol;

	/**
	 * Les points que rapporte ou enlève le décor quand consommé.
	 */
	private final int gradient;

	/**
	 * Les identifiants des actions valides sur ce décor.
	 */
	private final int[] validActions;

	/**
	 * La texture du décor.
	 */
	private final Image image;

	/**
	 * Si le décor est piégé ou non.
	 */
	private boolean trapped;

	/**
	 * Le constructeur d'un élément de décor.
	 *
	 * @param symbol       le symbole que le décor représente
	 * @param validActions la liste des actions valides (id d'actions)
	 * @param gradient     les points que le décor rapporte / enlève quand consommé
	 * @param trapped      si le décor est piégé ou non
	 * @param image        la texture du décor
	 */
	protected Scenery(int symbol, int[] validActions, int gradient, boolean trapped, Image image)
	{
		this.symbol = trapped ? symbol + SCENERIES - 1 : symbol;
		fakeSymbol = symbol;
		this.gradient = trapped ? -gradient : gradient;
		this.validActions = validActions;
		this.trapped = trapped;
		this.image = image;
	}

	/**
	 * Retourne le nombre de décors visibles par les automates.
	 *
	 * @return Le nombre de décors visibles par les automates
	 */
	public static int sceneries()
	{
		return SCENERIES;
	}

	/**
	 * Retourne le décor qui représente l'identifiant entier.
	 *
	 * @param id l'identifiant
	 * @return Le décor associé
	 */
	public static Scenery byId(int id)
	{
		switch(id)
		{
			case 0:
			{
				return new Desert();
			}

			case 1:
			{
				return new Lake(false);
			}

			case 2:
			{
				return new Meadow(false);
			}

			case 3:
			{
				return new Forest(false);
			}

			case 4:
			{
				return new Lake(true);
			}

			case 5:
			{
				return new Meadow(true);
			}

			case 6:
			{
				return new Forest(true);
			}

			default:
			{
				return new Desert();
			}
		}
	}

	public int getFakeSymbol()
	{
		return fakeSymbol;
	}

	public int getSymbol()
	{
		return symbol;
	}

	public int getGradient()
	{
		return gradient;
	}

	public boolean isTrapped()
	{
		return trapped;
	}

	public void setTrapped(boolean trapped)
	{
		this.trapped = trapped;
	}

	public int[] getValidActions()
	{
		return validActions;
	}

	public Image getImage()
	{
		return image;
	}

	/**
	 * Retourne si une action est applicable à ce décor.
	 *
	 * @param action l'action à tester
	 * @return Si l'action est applicable sur le décor
	 */
	public boolean matches(Action action)
	{
		int id = action.getId();

		for(int validId : validActions)
		{
			if(validId == id)
			{
				return true;
			}
		}

		return false;
	}
}
