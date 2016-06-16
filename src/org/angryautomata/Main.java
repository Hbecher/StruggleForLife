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
				{0, 2, 1},
				{0, 1, 1},
				{0, 2, 2},
				{0, 1, 2}
		};
		int[][] actions = {
				{0, 0, 0}, // desert
				{2, 1, 2}, // lac
				{4, 4, 3}, // prairie
				{6, 6, 6}  // foret
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

		Collections.shuffle(players);

		Position position = Position.ORIGIN;
		int numOfPlayers = players.size();

		for(int i = 0; i < numOfPlayers; i++)
		{
			Automaton automaton = players.remove(0).getAutomaton();
			Position origin = automaton.getOrigin();
			int width = automaton.numberOfStates(), height = Scenery.sceneries();
			boolean overlaps = false;

			for(Player player : players)
			{
				Position other = player.getAutomaton().getOrigin();
				int otherWidth = automaton.numberOfStates(), otherHeight = Scenery.sceneries();

				if(origin.getX() > other.getX() - width && origin.getX() < other.getY() + otherWidth)
				{
					overlaps = true;

					break;
				}
			}
		}

		int width = 128, height = 128;

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
