package org.angryautomata;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.angryautomata.game.Automaton;
import org.angryautomata.game.Board;
import org.angryautomata.game.Game;
import org.angryautomata.game.Position;

public class Main extends Application
{
	@Override
	public void start(Stage primaryStage) throws Exception
	{
		Position origin1 = new Position(0, 0);
		int[][] transitions1 = {
				{0},
				{0},
				{0},
				{0}
		};
		int[][] actions1 = {
				{0}, // desert
				{2}, // lac
				{4}, // prairie
				{6}  // foret
		};

		Automaton automaton1 = new Automaton(transitions1, actions1, origin1, "MonAutomateCool", 0XFFBF0D0D);

		Position origin2 = new Position(3, 6);
		int[][] transitions2 = {
				{0},
				{0},
				{0},
				{0}
		};
		int[][] actions2 = {
				{0}, // desert
				{2}, // lac
				{4}, // prairie
				{6}  // foret
		};

		Automaton automaton2 = new Automaton(transitions2, actions2, origin2, "TonAutomateNase", 0XFF916012);

		Position origin3 = new Position(7, 12);
		int[][] transitions3 = {
				{0},
				{0},
				{0},
				{0}
		};
		int[][] actions3 = {
				{0}, // desert
				{2}, // lac
				{4}, // prairie
				{6}  // foret
		};

		Automaton automaton3 = new Automaton(transitions3, actions3, origin3, "SonAutomateMeh", 0XFFFF00FF);

		Board board = new Board(64, 64);

		Controller root = new Controller();
		Game game = new Game(root, board, automaton1, automaton2, automaton3);
		root.setGame(game);

		primaryStage.setTitle("Struggle for Life");
		primaryStage.setResizable(false);
		primaryStage.setScene(new Scene(root));
		primaryStage.show();

		Thread gameThread = new Thread(game, "Main game loop thread");
		gameThread.setDaemon(true);
		gameThread.start();
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
