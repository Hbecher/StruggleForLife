package org.angryautomata;

import java.io.IOException;
import java.util.List;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.angryautomata.game.Automaton;
import org.angryautomata.game.Game;
import org.angryautomata.game.Player;
import org.angryautomata.game.Position;
import org.angryautomata.game.scenery.Scenery;

public class Controller extends BorderPane
{
	private final int squareSize = 32;
	@FXML
	public CheckMenuItem showAutomata;
	@FXML
	public MenuItem eraseMarker;
	@FXML
	public MenuItem putMarker;
	@FXML
	public Slider tickSpeed;
	private Game game = null;
	@FXML
	public Button pauseOrResumeButton, stopButton, quitButton;
	@FXML
	public Canvas screen, overlay;

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
		if(this.game != null)
		{
			stopGame();
		}

		this.game = game;

		int height = (squareSize + 1) * game.getHeight() + 1, width = (squareSize + 1) * game.getWidth() + 1;
		screen.setHeight(height);
		screen.setWidth(width);
		overlay.setHeight(height);
		overlay.setWidth(width);

		tickSpeed.valueProperty().addListener((observable, oldValue, newValue) ->
		{
			if(newValue.intValue() == 0)
			{
				game.pause();
			}
			else
			{
				if(oldValue.intValue() == 0)
				{
					game.resume();
				}

				game.setTickSpeed(1000L / newValue.longValue());
			}
		});

		init();
	}

	private void init()
	{
		final GraphicsContext gc = screen.getGraphicsContext2D();
		final int height = game.getHeight(), width = game.getWidth();

		gc.clearRect(0.0D, 0.0D, screen.getHeight(), screen.getWidth());
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
			for(int j = 0; j < width; j++)
			{
				gc.setFill(ofScenery(game.getSceneryAt(game.torusPos(j, i))));
				gc.fillRect(posToCanvas(j), posToCanvas(i), squareSize, squareSize);
			}
		}
	}

	public void update(final List<Player> players, final List<Position> tiles)
	{
		final GraphicsContext gc = screen.getGraphicsContext2D();

		for(Position position : tiles)
		{
			gc.setFill(ofScenery(game.getSceneryAt(position)));
			gc.fillRect(posToCanvas(position.getX()), posToCanvas(position.getY()), squareSize, squareSize);
		}

		for(Player player : players)
		{
			Position position = player.getPosition(), previous = player.getPreviousPosition();

			gc.setFill(ofScenery(game.getSceneryAt(previous)));
			gc.fillRect(posToCanvas(previous.getX()), posToCanvas(previous.getY()), squareSize, squareSize);

			gc.setFill(ofScenery(game.getSceneryAt(position)));
			gc.fillRect(posToCanvas(position.getX()), posToCanvas(position.getY()), squareSize, squareSize);
		}

		for(Player player : players)
		{
			Position position = player.getPosition();

			double x = player.getGradient() * 0.12D + 4.0D, length = 2 * x, offset = squareSize / 2.0D - x;

			gc.setFill(player.getColor());
			gc.fillRect(posToCanvas(position.getX()) + offset, posToCanvas(position.getY()) + offset, length, length);
		}

		if(showAutomata.isSelected())
		{
			showAutomata();
		}
	}

	private double posToCanvas(int pos)
	{
		return pos * (squareSize + 1.0D) + 1.0D;
	}

	private int canvasToPos(double pos)
	{
		return (int) ((pos - 1.0D) / (squareSize + 1.0D));
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

	private void showAutomata()
	{
		for(Automaton automaton : game.getAutomata())
		{
			final GraphicsContext gc = overlay.getGraphicsContext2D();
			double x = posToCanvas(automaton.getOrigin().getX()) - 0.5D, y = posToCanvas(automaton.getOrigin().getY()) - 0.5D;
			double xx = x + posToCanvas(automaton.numberOfStates()) + 1.0D, yy = y + posToCanvas(Scenery.sceneries()) + 1.0D;

			gc.setFill(automaton.getColor());
			gc.setLineWidth(3.0D);
			gc.strokeLine(x, y, x, yy);
			gc.strokeLine(x, y, xx, y);
			gc.strokeLine(xx, yy, x, yy);
			gc.strokeLine(xx, yy, xx, y);
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
		stopGame();
		Stage stage = (Stage) getScene().getWindow();
		stage.close();
		Platform.exit();
	}

	@FXML
	public void putMarker(ActionEvent e)
	{

	}

	@FXML
	public void eraseMarker(ActionEvent e)
	{

	}

	@FXML
	public void onOverlayMouseClicked(MouseEvent e)
	{
		if(e.getButton() == MouseButton.SECONDARY || e.getClickCount() > 1)
		{
			final GraphicsContext gc = overlay.getGraphicsContext2D();
			double x = posToCanvas(canvasToPos(e.getX())) - 0.5D, y = posToCanvas(canvasToPos(e.getY())) - 0.5D;
			double xx = x + squareSize + 1.0D, yy = y + squareSize + 1.0D;

			gc.clearRect(0.0D, 0.0D, overlay.getWidth(), overlay.getHeight());
			gc.setFill(Color.BLACK);
			gc.setLineWidth(3.0D);
			gc.strokeLine(x, y, x, yy);
			gc.strokeLine(x, y, xx, y);
			gc.strokeLine(xx, yy, x, yy);
			gc.strokeLine(xx, yy, xx, y);
		}
	}
}
