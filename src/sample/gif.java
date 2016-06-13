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
        Image trapped = new Image("sample/trapped-effect2.gif");
        Image invalid = new Image("sample/invalid-effect.gif");
        Image pop5 = new Image("sample/pop5.gif");
        Image pop25 = new Image("sample/pop25.gif");
        Image pop50 = new Image("sample/pop50.gif");
        Image pop75 = new Image("sample/pop75.gif");
        Image pop95 = new Image("sample/pop95.gif");
        Image dead = new Image("sample/dead.gif");


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

        // afficher 1 tombe par dessus les images des cases
        ImageView imageViewdead1 = new ImageView(dead);
        gridpane.add(imageViewdead1, 0, 3);
        ImageView imageViewdead2 = new ImageView(dead);
        gridpane.add(imageViewdead2, 8, 8);

        // afficher 3 pièges par dessus les images des cases et des tombes
        ImageView imageViewalter1 = new ImageView(trapped);
        gridpane.add(imageViewalter1, 15, 8);
        ImageView imageViewalter2 = new ImageView(trapped);
        gridpane.add(imageViewalter2, 14, 9);
        ImageView imageViewalter3 = new ImageView(trapped);
        gridpane.add(imageViewalter3, 13, 10);
        ImageView imageViewalter4 = new ImageView(trapped);
        gridpane.add(imageViewalter4, 8, 8);

        // afficher 4 populations ( personnage ) par dessus ce qui prècède
        ImageView imageViewpop = new ImageView(pop5);
        gridpane.add(imageViewpop, 5, 6);
        ImageView imageViewpop2 = new ImageView(pop25);
        gridpane.add(imageViewpop2, 12, 12);
        ImageView imageViewpop3 = new ImageView(pop50);
        gridpane.add(imageViewpop3, 10, 13);
        ImageView imageViewpop4 = new ImageView(pop75);
        gridpane.add(imageViewpop4, 16, 7);
        ImageView imageViewpop5 = new ImageView(pop95);
        gridpane.add(imageViewpop5, 8, 8);

        // afficher une action invlalide par dessus ce qui prècède
        ImageView imageViewinvalid1 = new ImageView(invalid);
        gridpane.add(imageViewinvalid1, 16, 7);
        ImageView imageViewinvalid2 = new ImageView(invalid);
        gridpane.add(imageViewinvalid2, 8, 8);

        // Display image on screen
        root.getChildren().add(gridpane);
        primaryStage.setTitle("test animated");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(false);
        primaryStage.show();
    }
}
