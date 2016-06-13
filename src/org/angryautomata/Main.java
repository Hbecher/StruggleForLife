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

		Automaton automaton1 = new Automaton(transitions1, actions1, origin1, "MonAutomateCool", 255);

		Position origin2 = new Position(3, 6);
		int[][] transitions2 = new int[4][9];
		for(int i = 0; i < 4; i++)
		{
			for(int j = 0; j < 9; j++)
			{
				transitions2[i][j] = (int) (Math.random() * 9);
			}
		}
		int[][] actions2 = {
				{0, 0, 0, 0, 0, 0, 0, 0, 0}, // desert
				{2, 1, 2, 1, 2, 2, 1, 2, 2}, // lac
				{4, 4, 3, 4, 3, 4, 4, 3, 4}, // prairie
				{5, 6, 5, 5, 6, 5, 6, 5, 5}  // foret
		};

		Automaton automaton2 = new Automaton(transitions2, actions2, origin2, "TonAutomateNase", 159);

		Board board = new Board(16, 16);

		Controller root = new Controller();
		Game game = new Game(root, board, automaton1, automaton2);
		root.setGame(game);

		primaryStage.setTitle("Struggle for Life");
		primaryStage.setResizable(false);
		primaryStage.setScene(new Scene(root));
		primaryStage.show();

		Thread gameThread = new Thread(game);
		gameThread.setDaemon(true);
		gameThread.start();
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
