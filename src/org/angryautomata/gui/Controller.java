package org.angryautomata.gui;

import java.io.IOException;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.angryautomata.game.*;
import org.angryautomata.game.scenery.Scenery;

public class Controller extends BorderPane
{
	private static final int SQUARE_SIZE = 32;

	@FXML
	public MenuItem placeMarker, eraseMarker, regenAutomaton;
	@FXML
	public Slider tickSpeed;
	@FXML
	public ChoiceBox<String> playersBox;
	@FXML
	public Label ticksLabel, populationsLabel, totalGradientLabel, meanGradientLabel;
	@FXML
	public Button pauseOrResumeButton, stopButton, quitButton;
	@FXML
	public Canvas colorCanvas, screen, overlay;
	@FXML
	public GridPane gifScreen;
	private Game game = null;
	private Position selection = null;
	private ImageView[][][] tab3d = null;

	public Controller()
	{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("gui.fxml"));
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

		//int height = (SQUARE_SIZE + 1) * game.getHeight() + 1, width = (SQUARE_SIZE + 1) * game.getWidth() + 1;
		int height = game.getHeight(), width = game.getWidth();
		//screen.setHeight(height);
		//screen.setWidth(width);
		overlay.setHeight(height * SQUARE_SIZE);
		overlay.setWidth(width * SQUARE_SIZE);

		tab3d = new ImageView[height][width][8];

		for(int y = 0; y < height; y++)
		{
			for(int x = 0; x < width; x++)
			{
				for(int z = 0; z < 8; z++)
				{
					ImageView imageView = new ImageView(Images.vide);

					tab3d[y][x][z] = imageView;
					gifScreen.add(imageView, x, y);
				}
			}
		}

		tickSpeed.valueProperty().addListener((observable, oldValue, newValue) ->
		{
			game.setTickSpeed(1000L / newValue.longValue());
		});

		playersBox.getItems().clear();

		for(Player player : game.getPlayers())
		{
			playersBox.getItems().add(player.getName());
		}

		playersBox.getItems().sort((o1, o2) -> game.getPlayer(o1).getPlayerNumber() - game.getPlayer(o2).getPlayerNumber());

		playersBox.valueProperty().addListener((observable, oldValue, newValue) ->
		{
			if(!newValue.equals(oldValue))
			{
				updateData();
			}
		});

		init();
	}

	private void init()
	{
		/*GraphicsContext gc = screen.getGraphicsContext2D();
		final int height = game.getHeight(), width = game.getWidth();

		gc.clearRect(0.0D, 0.0D, screen.getWidth(), screen.getHeight());
		gc.setFill(Color.BLACK);

		double d = (SQUARE_SIZE + 1.0D) * width + 1.0D;

		for(int i = 0; i <= height; i++)
		{
			gc.fillRect(0.0D, i * (SQUARE_SIZE + 1.0D), d, 1.0D);
		}

		d = (SQUARE_SIZE + 1.0D) * height + 1.0D;

		for(int j = 0; j <= width; j++)
		{
			gc.fillRect(j * (SQUARE_SIZE + 1.0D), 0.0D, 1.0D, d);
		}

		for(int i = 0; i < height; i++)
		{
			for(int j = 0; j < width; j++)
			{
				gc.setFill(game.getSceneryAt(game.torusPos(j, i)).getColor());
				gc.fillRect(posToCanvas(j), posToCanvas(i), SQUARE_SIZE, SQUARE_SIZE);
			}
		}*/
		final int height = game.getHeight(), width = game.getWidth();

		for(int x = 0; x < width; x++)
		{
			for(int y = 0; y < height; y++)
			{
				setImage(x, y, 0, game.getSceneryAt(new Position(x, y)).getImage());
			}
		}

		updateOverlay();
		updateControls();
	}

	public void updateScreen(final Deque<Position> tileUpdates)
	{
		final List<Population> populations = game.getPopulations();
		final Map<Player, Position> markers = game.getMarkers();

		//final GraphicsContext gc = screen.getGraphicsContext2D();

		while(!tileUpdates.isEmpty())
		{
			Position position = tileUpdates.poll();

			clearImageView(position.getX(), position.getY());
			setImage(position.getX(), position.getY(), 0, game.getSceneryAt(position).getImage());
			/*gc.setFill(game.getSceneryAt(position).getColor());
			gc.fillRect(posToCanvas(position.getX()), posToCanvas(position.getY()), SQUARE_SIZE, SQUARE_SIZE);*/
		}

		for(Population population : populations)
		{
			Position previous = population.getPreviousPosition();

			/*gc.setFill(game.getSceneryAt(previous).getColor());
			gc.fillRect(posToCanvas(previous.getX()), posToCanvas(previous.getY()), SQUARE_SIZE, SQUARE_SIZE);

			gc.setFill(game.getSceneryAt(position).getColor());
			gc.fillRect(posToCanvas(position.getX()), posToCanvas(position.getY()), SQUARE_SIZE, SQUARE_SIZE);*/
			setImage(previous.getX(), previous.getY(), 6, Images.vide);
		}

		for(Population population : populations)
		{
			Position position = population.getPosition();
			int gradient = population.getGradient();
			Image image;

			if(gradient <= 5)
			{
				image = Images.pop5;
			}
			else if(gradient > 5 && gradient <= 25)
			{
				image = Images.pop25;
			}
			else if(gradient > 25 && gradient <= 50)
			{
				image = Images.pop50;
			}
			else if(gradient > 50 && gradient <= 75)
			{
				image = Images.pop75;
			}
			else
			{
				image = Images.pop95;
			}

			setImage(position.getX(), position.getY(), 6, image);

			/*double x = gradient >= Population.GRADIENT_MAX ? SQUARE_SIZE / 4.0D : gradient * 0.12D + 4.0D, length = 2.0D * x, offset = SQUARE_SIZE / 2.0D - x;

			gc.setFill(population.getColor());
			gc.fillRect(posToCanvas(position.getX()) + offset, posToCanvas(position.getY()) + offset, length, length);*/
		}

		for(Map.Entry<Player, Position> entry : markers.entrySet())
		{
			Position position = entry.getValue();

			/*gc.setFill(entry.getKey().getColor());
			gc.fillOval(posToCanvas(position.getX()) + 12.0D, posToCanvas(position.getY()) + 12.0D, 8.0D, 8.0D);*/
		}

		updateData();
		updateControls();
	}

	private double posToCanvas(int pos)
	{
		return pos * SQUARE_SIZE;//pos * (SQUARE_SIZE + 1.0D) + 1.0D;
	}

	private int canvasToPos(double pos)
	{
		return (int) (pos / SQUARE_SIZE);//(int) ((pos - 1.0D) / (SQUARE_SIZE + 1.0D));
	}

	private void updateOverlay()
	{
		overlay.getGraphicsContext2D().clearRect(0.0D, 0.0D, overlay.getWidth(), overlay.getHeight());

		drawAutomata();
		drawSelection();
	}

	private void drawAutomata()
	{
		final GraphicsContext gc = overlay.getGraphicsContext2D();

		for(Player player : game.getPlayers())
		{
			Automaton automaton = player.getAutomaton();
			Position origin = automaton.getOrigin();
			double x = posToCanvas(origin.getX()) - 2.0D, y = posToCanvas(origin.getY()) - 2.0D;
			double xl = SQUARE_SIZE * automaton.numberOfStates(), yl = SQUARE_SIZE * Scenery.sceneries();

			gc.setFill(player.getColor());
			gc.fillRect(x, y, xl + 3.0D, 3.0D);
			gc.fillRect(x, y, 3.0D, yl + 3.0D);
			gc.fillRect(x, y + yl, xl + 3.0D, 3.0D);
			gc.fillRect(x + xl, y, 3.0D, yl + 3.0D);
		}
	}

	private void drawSelection()
	{
		if(selection != null)
		{
			final GraphicsContext gc = overlay.getGraphicsContext2D();

			double x = posToCanvas(selection.getX()) - 0.5D, y = posToCanvas(selection.getY()) - 0.5D;
			double xx = x + SQUARE_SIZE, yy = y + SQUARE_SIZE;

			gc.setStroke(Color.BLACK);
			gc.setLineWidth(3.0D);
			gc.strokeLine(x, y, x, yy);
			gc.strokeLine(x, y, xx, y);
			gc.strokeLine(xx, yy, x, yy);
			gc.strokeLine(xx, yy, xx, y);
		}
	}

	private void updateData()
	{
		ticksLabel.setText(Integer.toString(game.ticks()));

		Player player = game.getPlayer(playersBox.getValue());

		if(player != null)
		{
			final GraphicsContext gc = colorCanvas.getGraphicsContext2D();
			gc.setFill(player.getColor());
			gc.fillRect(0.0D, 0.0D, colorCanvas.getWidth(), colorCanvas.getHeight());

			populationsLabel.setText(Integer.toString(player.getPopulations().size()));

			if(player.getPopulations().isEmpty())
			{
				totalGradientLabel.setText("N/A");
				meanGradientLabel.setText("N/A");
			}
			else
			{
				totalGradientLabel.setText(Integer.toString(player.getTotalGradient()));
				meanGradientLabel.setText(Integer.toString(player.getMeanGradient()));
			}
		}
	}

	private void updateControls()
	{
		Player player = game.getPlayer(playersBox.getValue());

		if(player != null)
		{
			System.out.println(player.isDead());
			if(!player.isDead())
			{
				placeMarker.setDisable(!player.canPlaceMarker());
				eraseMarker.setDisable(!game.hasMarker(player));
				regenAutomaton.setDisable(!player.canRegenAutomaton());
			}
			else
			{
				placeMarker.setDisable(true);
				eraseMarker.setDisable(true);
				regenAutomaton.setDisable(true);
			}
		}
		else
		{
			placeMarker.setDisable(true);
			eraseMarker.setDisable(true);
			regenAutomaton.setDisable(true);
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
		if(selection != null)
		{
			game.addMarker(playersBox.getValue(), selection);

			updateControls();
		}
	}

	@FXML
	public void delMarker(ActionEvent e)
	{
		game.delMarker(playersBox.getValue());

		updateControls();
	}

	@FXML
	public void regenAutomaton(ActionEvent e)
	{
		Player player = game.getPlayer(playersBox.getValue());

		if(player != null)
		{
			game.regenAutomaton(player);

			updateControls();
		}
	}

	@FXML
	public void onOverlayMouseClicked(MouseEvent e)
	{
		if(e.getButton() == MouseButton.SECONDARY || (e.getButton() == MouseButton.PRIMARY && e.getClickCount() > 1))
		{
			selection = new Position(canvasToPos(e.getX()), canvasToPos(e.getY()));

			updateOverlay();
		}
	}

	@FXML
	public void test(ActionEvent e)
	{
		Stage stage = (Stage) getScene().getWindow();
		stage.setFullScreen(!stage.isFullScreen());
	}

	public Position getSelection()
	{
		return selection;
	}

	private ImageView getImageView(int x, int y, int z)
	{
		try
		{
			return tab3d[y][x][z];
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			System.out.println(x + " " + y + " " + z);
		}

		return null;
	}

	private void setImage(int x, int y, int z, Image image)
	{
		getImageView(x, y, z).setImage(image);
	}

	private void clearImageView(int x, int y)
	{
		for(int z = 0; z < 8; z++)
		{
			setImage(x, y, z, Images.vide);
		}
	}
}