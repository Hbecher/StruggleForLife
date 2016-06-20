package org.angryautomata.gui;

import java.io.IOException;
import java.util.ArrayDeque;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.angryautomata.game.*;
import org.angryautomata.game.scenery.Scenery;

public class Controller extends VBox
{
	private static final int SQUARE_SIZE = 32;

	private final Deque<Position> conflicts = new ArrayDeque<>(), consumes = new ArrayDeque<>(), consumesTrap = new ArrayDeque<>(), traps = new ArrayDeque<>(), invalids = new ArrayDeque<>(), deaths = new ArrayDeque<>();

	@FXML
	public MenuItem placeMarker, eraseMarker, regenAutomaton;
	@FXML
	public Slider tickSpeed;
	@FXML
	public ChoiceBox<String> playersBox;
	@FXML
	public Label ticksLabel, playersAliveLabel, populationsLabel, totalGradientLabel, meanGradientLabel, winnerLabel;
	@FXML
	public Button pauseOrResumeButton, stopButton, quitButton;
	@FXML
	public Canvas colorCanvas, gifOverlay, overlay;
	@FXML
	public GridPane gifScreen;
	private Game game = null;
	private Position selection = null;
	private ImageView[][][] images = null;

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

	/**
	 * @param game le jeu en cours
	 */
	public void setGame(Game game)
	{
		if(this.game != null)
		{
			stopGame();
		}

		this.game = game;

		int height = game.getHeight(), width = game.getWidth();
		gifOverlay.setHeight(height * SQUARE_SIZE);
		gifOverlay.setWidth(width * SQUARE_SIZE);
		overlay.setHeight(height * SQUARE_SIZE);
		overlay.setWidth(width * SQUARE_SIZE);

		images = new ImageView[height][width][8];

		for(int y = 0; y < height; y++)
		{
			for(int x = 0; x < width; x++)
			{
				for(int z = 0; z < 8; z++)
				{
					ImageView imageView = new ImageView(Images.vide);

					images[y][x][z] = imageView;
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

	/**
	 * Initialise l'affichage
	 */
	private void init()
	{
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

	/**
	 * Appelé à chaque mise à jour d'écran, avec les différentes files de modifications.
	 */
	public void updateScreen(final Deque<Position> tileUpdates, final Deque<Position> conflicts, final Deque<Position> consumes, final Deque<Position> consumesTrap, final Deque<Position> traps, final Deque<Position> invalids, final Deque<Position> deaths)
	{
		final List<Population> populations = game.getPopulations();
		final Map<Player, Position> markers = game.getMarkers();

		while(!tileUpdates.isEmpty())
		{
			Position position = tileUpdates.poll();

			Scenery scenery = game.getSceneryAt(position);
			setImage(position.getX(), position.getY(), 0, scenery.getImage());
			setImage(position.getX(), position.getY(), 2, scenery.isTrapped() ? Images.trapped : Images.vide);
			setImage(position.getX(), position.getY(), 4, Images.vide);
		}

		while(!this.conflicts.isEmpty())
		{
			Position conflict = this.conflicts.poll();

			setImage(conflict.getX(), conflict.getY(), 7, Images.vide);
		}

		while(!conflicts.isEmpty())
		{
			Position conflict = conflicts.poll();

			setImage(conflict.getX(), conflict.getY(), 7, Images.conflict);

			this.conflicts.add(conflict);
		}

		while(!this.consumes.isEmpty())
		{
			Position consume = this.consumes.poll();

			setImage(consume.getX(), consume.getY(), 1, Images.vide);
			setImage(consume.getX(), consume.getY(), 5, Images.vide);
		}

		while(!consumes.isEmpty())
		{
			Position consume = consumes.poll();

			setImage(consume.getX(), consume.getY(), 1, Images.forage_back);
			setImage(consume.getX(), consume.getY(), 5, Images.forage);

			this.consumes.add(consume);
		}

		while(!this.consumesTrap.isEmpty())
		{
			Position consumeTrap = this.consumesTrap.poll();

			setImage(consumeTrap.getX(), consumeTrap.getY(), 1, Images.vide);
			setImage(consumeTrap.getX(), consumeTrap.getY(), 5, Images.vide);
		}

		while(!consumesTrap.isEmpty())
		{
			Position consumeTrap = consumesTrap.poll();

			setImage(consumeTrap.getX(), consumeTrap.getY(), 1, Images.forage_trapped_back);
			setImage(consumeTrap.getX(), consumeTrap.getY(), 5, Images.forage_trapped);

			this.consumesTrap.add(consumeTrap);
		}

		while(!this.traps.isEmpty())
		{
			Position trap = this.traps.poll();

			setImage(trap.getX(), trap.getY(), 1, Images.vide);
			setImage(trap.getX(), trap.getY(), 5, Images.vide);
		}

		while(!traps.isEmpty())
		{
			Position trap = traps.poll();

			setImage(trap.getX(), trap.getY(), 1, Images.forage_trapped_back);
			setImage(trap.getX(), trap.getY(), 5, Images.trap);

			this.traps.add(trap);
		}

		while(!this.invalids.isEmpty())
		{
			Position invalid = this.invalids.poll();

			setImage(invalid.getX(), invalid.getY(), 6, Images.vide);
		}

		while(!invalids.isEmpty())
		{
			Position invalid = invalids.poll();

			setImage(invalid.getX(), invalid.getY(), 6, Images.invalid);

			this.invalids.add(invalid);
		}

		while(!this.deaths.isEmpty())
		{
			Position death = this.deaths.poll();

			setImage(death.getX(), death.getY(), 4, Images.vide);
		}

		while(!deaths.isEmpty())
		{
			Position death = deaths.poll();

			setImage(death.getX(), death.getY(), 4, Images.pop_death);

			this.deaths.add(death);
		}

		for(Population population : populations)
		{
			Position previous = population.getPreviousPosition();

			setImage(previous.getX(), previous.getY(), 4, Images.vide);
		}

		final GraphicsContext gc = gifOverlay.getGraphicsContext2D();

		gc.clearRect(0.0D, 0.0D, gifOverlay.getWidth(), gifOverlay.getHeight());

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

			setImage(position.getX(), position.getY(), 4, image);

			double x = posToCanvas(position.getX()), y = posToCanvas(position.getY());
			double xx = x + SQUARE_SIZE, yy = y + SQUARE_SIZE;

			gc.setStroke(population.getColor());
			gc.setLineWidth(2.0D);
			gc.strokeLine(x, y, x, yy);
			gc.strokeLine(x, y, xx, y);
			gc.strokeLine(xx, yy, x, yy);
			gc.strokeLine(xx, yy, xx, y);
		}

		double center = SQUARE_SIZE / 2.0D - 6.0D;

		for(Map.Entry<Player, Position> entry : markers.entrySet())
		{
			Position marker = entry.getValue();

			gc.setFill(entry.getKey().getColor());
			gc.fillOval(posToCanvas(marker.getX()) + center, posToCanvas(marker.getY()) + center, 12.0D, 12.0D);
		}

		updateData();
		updateControls();
	}

	/**
	 * Convertit une position en coordonnée sur l'écran
	 */
	private double posToCanvas(int pos)
	{
		return pos * SQUARE_SIZE;
	}

	/**
	 * Convertit une coordonnée sur l'écran en position
	 */
	private int canvasToPos(double pos)
	{
		return (int) (pos / SQUARE_SIZE);
	}

	/**
	 * Met à jour l'écran de sélection et d'affichage des automates
	 */
	private void updateOverlay()
	{
		overlay.getGraphicsContext2D().clearRect(0.0D, 0.0D, overlay.getWidth(), overlay.getHeight());

		drawAutomata();
		drawSelection();
	}

	/**
	 * Dessine les automates
	 */
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

	/**
	 * Dessine la position sélectionnée
	 */
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

	/**
	 * Met à jour les données de la partie
	 */
	private void updateData()
	{
		ticksLabel.setText(Integer.toString(game.ticks()));
		playersAliveLabel.setText(Integer.toString(game.playersAlive()));

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

	/**
	 * Met à jour les boutons de contrôle en fonction du joueur sélectionné et des actions possibles
	 */
	private void updateControls()
	{
		Player player = game.getPlayer(playersBox.getValue());

		if(player != null)
		{
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

	private ImageView getImageView(int x, int y, int z)
	{
		return images[y][x][z];
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

	/**
	 * Affiche le vainqueur en fin de partie et désactive les contrôles
	 */
	public void displayWinner(String playerName)
	{
		winnerLabel.setText(playerName);
		stopButton.setDisable(true);
		pauseOrResumeButton.setDisable(true);
		tickSpeed.setDisable(true);
	}
}