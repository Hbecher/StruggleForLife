package org.angryautomata;

import java.io.IOException;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.angryautomata.game.Game;
import org.angryautomata.game.Player;
import org.angryautomata.game.Position;
import org.angryautomata.game.scenery.Scenery;

public class Controller extends VBox
{
	public Button pauseOrResumeButton, stopButton, quitButton;
	private Game game = null;
	@FXML
	public Canvas canvas;
	private final int squareSize = 32;

	public Controller()
	{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("jfx.fxml"));
		loader.setRoot(this);
		loader.setController(this);

		try
		{
			loader.load();
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public Game getGame()
	{
		return game;
	}

	public void setGame(Game game)
	{
		this.game = game;

		canvas.setHeight((squareSize + 1) * game.getHeight() + 1);
		canvas.setWidth((squareSize + 1) * game.getWidth() + 1);
	}

	public Canvas getCanvas()
	{
		return canvas;
	}

	public void update(final List<Player> players)
	{
		final GraphicsContext gc = canvas.getGraphicsContext2D();
		final int height = game.getHeight(), width = game.getWidth();

		gc.clearRect(0.0D, 0.0D, canvas.getHeight(), canvas.getWidth());
		gc.setFill(Color.BLACK);

		double t = (squareSize + 1.0D) * width + 1.0D;

		for(int i = 0; i <= height; i++)
		{
			gc.fillRect(0.0D, i * (squareSize + 1.0D), t, 1.0D);
		}

		t = (squareSize + 1.0D) * height + 1.0D;

		for(int j = 0; j <= width; j++)
		{
			gc.fillRect(j * (squareSize + 1.0D), 0.0D, 1.0D, t);
		}

		for(int i = 0; i < height; i++)
		{
			double d = posToCanvas(i);

			for(int j = 0; j < width; j++)
			{
				gc.setFill(ofScenery(game.getSceneryAt(game.torusPos(j, i))));
				gc.fillRect(posToCanvas(j), d, squareSize, squareSize);
			}
		}

		for(Player player : players)
		{
			Position position = player.getPosition();

			double x = player.getGradient() * 0.12D + 4.0D, length = 2 * x, offset = squareSize / 2.0D - x;

			gc.setFill(player.getColor());
			gc.fillRect(posToCanvas(position.getX()) + offset, posToCanvas(position.getY()) + offset, length, length);
		}

		gc.setFill(Color.BLACK);

		for(int i = 0; i < height; i++)
		{
			double d = posToCanvas(i) + 12.0D;

			for(int j = 0; j < width; j++)
			{
				gc.fillText(Integer.toString(game.getSceneryAt(game.torusPos(j, i)).getSymbol()), posToCanvas(j) + 4.0D, d);
			}
		}
	}

	private double posToCanvas(int pos)
	{
		return pos * (squareSize + 1.0D) + 1.0D;
	}

	private Color ofScenery(Scenery scenery)
	{
		switch(scenery.getFakeSymbol())
		{
			case 0:
			{
				return Color.GOLD;
			}

			case 1:
			{
				return Color.AQUAMARINE;
			}

			case 2:
			{
				return Color.LAWNGREEN;
			}

			case 3:
			{
				return Color.FORESTGREEN;
			}

			default:
			{
				return Color.GRAY;
			}
		}
	}

	@FXML
	public void pauseOrResumeGame()
	{
		if(game.isPaused())
		{
			pauseOrResumeButton.setText("Suspendre");

			game.resume();
		}
		else
		{
			pauseOrResumeButton.setText("Reprendre");

			game.pause();
		}
	}

	@FXML
	public void stopGame()
	{
		game.stop();
	}

	@FXML
	public void quit()
	{
		Stage stage = (Stage) getScene().getWindow();
		stage.close();
		Platform.exit();
	}
}
