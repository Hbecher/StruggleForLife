package org.angryautomata;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.angryautomata.game.*;
import org.angryautomata.game.scenery.Scenery;
import org.angryautomata.gui.Controller;

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

		List<File> selectedFiles = fileChooser.showOpenMultipleDialog(primaryStage);

		/*if(selectedFiles == null)
		{
			primaryStage.close();

			Platform.exit();

			return;
		}*/

		// XMLReader renvoie une liste de joueurs dont la position initiale de leur automate n'est pas spécifiée
		// XMLReader.read(File[] ou List<File>) -> Player[] ou List<Player>
		////////
		List<Player> players = new ArrayList<>();

		int[][] transitions = {
				{0, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
		};
		int[][] actions = {
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // desert
				{2, 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // lac
				{4, 4, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // prairie
				{6, 6, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}  // foret
		};

		Player player1 = new Player(new Automaton(transitions, actions, Position.ORIGIN), "MonAutomateCool", 0xBF0D0D);
		Player player2 = new Player(new Automaton(transitions, actions, Position.ORIGIN), "TonAutomateNase", 0x916012);
		Player player3 = new Player(new Automaton(transitions, actions, Position.ORIGIN), "SonAutomateMeh", 0xFF00FF);

		Collections.addAll(players, player1, player2, player3);
		////////

		int maxStates = 0;

		for(Player player : players)
		{
			int k = player.getAutomaton().numberOfStates();

			if(k > maxStates)
			{
				maxStates = k;
			}
		}

		int numOfPlayers = players.size();
		int max = Board.MAX_SIZE / numOfPlayers;

		/*if(max > Board.MAX_SIZE / maxStates)
		{
			// erreur
			primaryStage.close();
			Platform.exit();

			return;
		}*/

		Collections.shuffle(players);
		int x = 0, y = 0, xmax = 0;

		for(Player player : players)
		{
			if(x > max)
			{
				x = 0;
				y += Scenery.sceneries() + 4;
			}

			Automaton automaton = player.getAutomaton();

			automaton.setOrigin(new Position(x + (int) (Math.random() * 4.0D), y + (int) (Math.random() * Scenery.sceneries())));

			if(automaton.getOrigin().getX() + automaton.numberOfStates() > xmax)
			{
				xmax = automaton.getOrigin().getX() + automaton.numberOfStates();
			}

			x += maxStates + 4;
		}

		int width = xmax + (int) (Math.random() * 4.0D), height = y + Scenery.sceneries() + (int) (Math.random() * 4.0D);

		Board board = new Board(width, height);

		Game game = new Game(root, board, player1, player2, player3);
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
