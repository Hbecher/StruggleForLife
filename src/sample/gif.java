package sample;/**
 * Created by Vincent on 09/06/2016.
 */

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.*;



public class gif extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        Group root = new Group();
        Scene scene = new Scene(root, 1024, 768, Color.BLACK);
        GridPane gridpane = new GridPane();
//        gridpane.setAlignment(Pos.CENTER);

        // pour charger les images
        Image desert = new Image("sample/desert-effect.gif");
        Image lake = new Image("sample/lake-effect.gif");
        Image meadow = new Image("sample/meadow-effect.gif");
        Image forest = new Image("sample/forest-effect.gif");
        Image trapped = new Image("sample/trapped-effect.gif");
        Image invalid = new Image("sample/invalid-effect.gif");
        Image pop = new Image("sample/pop-effect.gif");


        // pour remplir la gridpane des images ( symboles )
        int k = 0;
//        int l = 0;
        for (int j=0;j<32;j++){
            for (int i = 0; i < 5*4; i++) {
                ImageView imageView;
                if (k == 0) {imageView = new ImageView(forest); k = 1;}
                else if (k == 1) {imageView = new ImageView(meadow); k = 2;}
                else if (k == 2) {imageView = new ImageView(lake); k = 3;}
                else {imageView = new ImageView(desert); k = 0;}
                gridpane.add(imageView, j, i);// j et i sont les coordonnées (x,y) de la gridpane où mettre l'image

//                if (l == 0 && k != 0) {gridpane.add(imageView, j, i); gridpane.add(imageViewalter, j, i); l = 1;}
//                else if (l == 1 && k != 0) {gridpane.add(imageView, j, i); gridpane.add(imageViewinvalid, j, i); l = 2;}
//                else if (l == 2 && k != 0) {gridpane.add(imageView, j, i); gridpane.add(imageViewalter, j, i); gridpane.add(imageViewinvalid, j, i); l = 3;}
//                else {gridpane.add(imageView, j, i); l = 0;}
            }
        }

        // afficher 3 pièges par dessus les images des cases
        ImageView imageViewalter1 = new ImageView(trapped);
        gridpane.add(imageViewalter1, 15, 8);
        ImageView imageViewalter2 = new ImageView(trapped);
        gridpane.add(imageViewalter2, 14, 9);
        ImageView imageViewalter3 = new ImageView(trapped);
        gridpane.add(imageViewalter3, 13, 10);

        // afficher 4 populations ( personnage ) par dessus ce qui prècède
        ImageView imageViewpop = new ImageView(pop);
        gridpane.add(imageViewpop, 5, 6);
        ImageView imageViewpop2 = new ImageView(pop);
        gridpane.add(imageViewpop2, 12, 12);
        ImageView imageViewpop3 = new ImageView(pop);
        gridpane.add(imageViewpop3, 10, 13);
        ImageView imageViewpop4 = new ImageView(pop);
        gridpane.add(imageViewpop4, 16, 7);

        // afficher une action invlalide par dessus ce qui prècède
        ImageView imageViewinvalid = new ImageView(invalid);
        gridpane.add(imageViewinvalid, 16, 7);

        // Display image on screen
        root.getChildren().add(gridpane);
        primaryStage.setTitle("test animated");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(false);
        primaryStage.show();
    }
}
