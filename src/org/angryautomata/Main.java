package org.angryautomata;

import java.io.File;
import java.util.Collections;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.angryautomata.game.*;
import org.angryautomata.game.scenery.Scenery;
import org.angryautomata.gui.Controller;
import org.angryautomata.xml.XMLParser;

public class Main extends Application
{
	@Override
	public void start(Stage primaryStage)
	{
		primaryStage.setTitle("Struggle for Life");
		primaryStage.setResizable(true);

		Controller root = new Controller();

		primaryStage.setScene(new Scene(root));
		primaryStage.show();

		FileChooser fileChooser = new FileChooser();

		fileChooser.setTitle("Struggle for Life - choix des automates");
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Fichier automate (*.xml)", "*.xml"), new FileChooser.ExtensionFilter("Tous les fichiers", "*.*"));

		try
		{
			File currentDir = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();

			fileChooser.setInitialDirectory(currentDir);
		}
		catch(Exception ignored)
		{
		}

		List<File> selectedFiles = fileChooser.showOpenMultipleDialog(primaryStage);

		if(selectedFiles == null)
		{
			primaryStage.close();
			Platform.exit();

			return;
		}

		XMLParser parser = new XMLParser(selectedFiles);
		parser.parse();
		List<Player> players = parser.getPlayers();

		if(players == null || players.isEmpty())
		{
			primaryStage.close();
			Platform.exit();

			return;
		}

		int maxStates = 0;

		for(Player player : players)
		{
			int k = player.getAutomaton().numberOfStates();

			if(k > maxStates)
			{
				maxStates = k;
			}
		}

		final int padding = 4;
		int maxPerLine = Board.MAX_SIZE / (maxStates + 2 * padding), onLine = 0;

		Collections.shuffle(players);
		int x = 0, y = 0, xmax = 0, ymax = 0;

		for(Player player : players)
		{
			if(onLine > maxPerLine)
			{
				x = 0;
				y += Scenery.sceneries() + padding;
			}

			Automaton automaton = player.getAutomaton();
			int ax = x + (int) (Math.random() * padding), ay = y + (int) (Math.random() * Scenery.sceneries());

			automaton.setOrigin(new Position(ax, ay));

			if(ax + automaton.numberOfStates() > xmax)
			{
				xmax = ax + automaton.numberOfStates();
			}

			if(ay + Scenery.sceneries() > ymax)
			{
				ymax = ay + Scenery.sceneries();
			}

			x += maxStates + padding;
			onLine++;
		}

		int width = xmax + (int) (Math.random() * padding), height = ymax + (int) (Math.random() * padding);

		if(width < 16)
		{
			width = 16;
		}

		if(height < 8)
		{
			height = 8;
		}

		Board board = new Board(width, height);

		Game game = new Game(root, board, players);
		root.setGame(game);

		Thread gameThread = new Thread(game, "Game loop thread");
		gameThread.setDaemon(true);
		gameThread.start();
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
