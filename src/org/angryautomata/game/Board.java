package org.angryautomata.game;

import org.angryautomata.game.scenery.Scenery;

/**
 * Représente le plateau de jeu.
 */
public class Board
{
	/**
	 * La taille maximale du plateau
	 */
	public static final int MAX_SIZE = 128;

	/**
	 * Le décor
	 */
	private final Scenery[][] terrain;

	/**
	 * Hauteur et longueur
	 */
	private final int height, width;

	/**
	 * Constructeur d'un plateau de jeu.
	 *
	 * @param width  longueur
	 * @param height hauteur
	 */
	public Board(int width, int height)
	{
		terrain = new Scenery[height][width];
		this.width = width;
		this.height = height;

		int totalSceneries = Scenery.sceneries();

		// remplissage aléatoire initial de la carte
		for(int i = 0; i < height; i++)
		{
			for(int j = 0; j < width; j++)
			{
				Scenery scenery = Scenery.byId((int) (Math.random() * totalSceneries));

				terrain[i][j] = scenery;
			}
		}
	}

	public int getHeight()
	{
		return height;
	}

	public int getWidth()
	{
		return width;
	}

	/**
	 * Définit l'élément de décor à la position.
	 *
	 * @param position la position
	 * @param scenery  le décor
	 */
	public void setSceneryAt(Position position, Scenery scenery)
	{
		Position torus = torusPos(position.getX(), position.getY());

		terrain[torus.getY()][torus.getX()] = scenery;
	}

	/**
	 * Retourne l'élément de décor à la position.
	 *
	 * @param position la position
	 * @return Le décor
	 */
	public Scenery getSceneryAt(Position position)
	{
		Position torus = torusPos(position.getX(), position.getY());

		return terrain[torus.getY()][torus.getX()];
	}

	/**
	 * Calcule la position sur le tore (le plateau étant un tore).
	 *
	 * @param x coordonnée
	 * @param y coordonnée
	 * @return La coordonnée sur le tore
	 */
	public Position torusPos(int x, int y)
	{
		x %= width;
		y %= height;

		if(x < 0)
		{
			x += width;
		}

		if(y < 0)
		{
			y += height;
		}

		return new Position(x, y);
	}

	/**
	 * Retourne une position aléatoire sur la carte.
	 *
	 * @return Une position aléatoire sur la carte
	 */
	public Position randomPos()
	{
		return new Position((int) (Math.random() * width), (int) (Math.random() * height));
	}
}
