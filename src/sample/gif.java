package sample;/**
 * Created by Vincent on 09/06/2016.
 */

import javafx.application.Application;
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
        Image pop5 = new Image("sample/pop5.gif");
        Image pop25 = new Image("sample/pop25.gif");
        Image pop50 = new Image("sample/pop50.gif");
        Image pop75 = new Image("sample/pop75.gif");
        Image pop95 = new Image("sample/pop95.gif");
        Image dead = new Image("sample/dead.gif");
        Image pop_death = new Image("sample/pop_death.gif");
        Image popsplitda = new Image("sample/split_d_a.gif");
        Image popsplitdb = new Image("sample/split_d_b.gif");
        Image popsplitga = new Image("sample/split_g_a.gif");
        Image popsplitgb = new Image("sample/split_g_b.gif");
        Image popsplitha = new Image("sample/split_h_a.gif");
        Image popsplithb = new Image("sample/split_h_b.gif");
        Image popsplitba = new Image("sample/split_b_a.gif");
        Image popsplitbb = new Image("sample/split_b_b.gif");
        Image popmigrateda = new Image("sample/migrate_d_a.gif");
        Image popmigratedb = new Image("sample/migrate_d_b.gif");
        Image popmigratega = new Image("sample/migrate_g_a.gif");
        Image popmigrategb = new Image("sample/migrate_g_b.gif");
        Image popmigrateha = new Image("sample/migrate_h_a.gif");
        Image popmigratehb = new Image("sample/migrate_h_b.gif");
        Image popmigrateba = new Image("sample/migrate_b_a.gif");
        Image popmigratebb = new Image("sample/migrate_b_b.gif");
        Image regenlake = new Image("sample/regen_desert_lake.gif");
        Image regenmeadow = new Image("sample/regen_desert_meadow.gif");
        Image regenforest = new Image("sample/regen_meadow_forest.gif");
        Image forage_back = new Image("sample/forage_back.gif");
        Image forage_trapped_back = new Image("sample/forage_trapped_back.gif");
        Image forage = new Image("sample/forage.gif");
        Image forage_trapped = new Image("sample/forage_trapped.gif");
        Image conflict = new Image("sample/conflict.gif");


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

        // jouer du son !!!!! à développer
        /*
        final File file = new File("C:\\data\\audio\\chanaz\\RockMétal\\Tool\\10000 Days (2006)\\01 Vicarious.mp3");
        final Media media = new Media(file.toURI().toString());
        final MediaPlayer mediaPlayer = new MediaPlayer(media);
        Duration seconds = new Duration(3000000.0);
        mediaPlayer.setStopTime(seconds);
        mediaPlayer.play();
        */

        // afficher l'animation de régénération de case par dessus les images des cases
        ImageView imageViewregenlake = new ImageView(regenlake);
        gridpane.add(imageViewregenlake, 13, 7);
        ImageView imageViewregenmeadow = new ImageView(regenmeadow);
        gridpane.add(imageViewregenmeadow, 14, 7);
        ImageView imageViewregenforest = new ImageView(regenforest);
        gridpane.add(imageViewregenforest, 14, 6);

        // afficher l'animation de fond de consommation de case par dessus les images des cases
        ImageView imageViewforage_back = new ImageView(forage_back);
        gridpane.add(imageViewforage_back, 3,4);

        // afficher l'animation de fond de consommation de case piégée par dessus ce qui précède
        ImageView imageViewforage_trapped_back = new ImageView(forage_trapped_back);
        gridpane.add(imageViewforage_trapped_back, 3, 6);

        // afficher l'animation de piégeage de case par dessus ce qui précède
        ImageView imageViewtrap = new ImageView(forage_trapped_back);
        gridpane.add(imageViewtrap, 3, 3);

        // afficher 1 tombe par dessus par dessus ce qui précède
        ImageView imageViewdead1 = new ImageView(dead);
        gridpane.add(imageViewdead1, 6, 7);
        ImageView imageViewdead2 = new ImageView(dead);
        gridpane.add(imageViewdead2, 9, 6);

        // afficher l'animation d'extinction de population  par dessus ce qui précède
        ImageView imageViewpopdead1 = new ImageView(pop_death);
        gridpane.add(imageViewpopdead1, 10, 7);

        // afficher l'animation de split de population  par dessus ce qui précède(!!! 2 images => 1 image par case !!!)
        ImageView imageViewpopsplitda = new ImageView(popsplitda);// case départ
        gridpane.add(imageViewpopsplitda, 10, 9);
        ImageView imageViewpopsplitdb = new ImageView(popsplitdb);//case arrivée
        gridpane.add(imageViewpopsplitdb, 11, 9);
        ImageView imageViewpopsplitga = new ImageView(popsplitga);
        gridpane.add(imageViewpopsplitga, 10, 10);
        ImageView imageViewpopsplitgb = new ImageView(popsplitgb);
        gridpane.add(imageViewpopsplitgb, 9, 10);
        ImageView imageViewpopsplitha = new ImageView(popsplitha);
        gridpane.add(imageViewpopsplitha, 10, 12);
        ImageView imageViewpopsplithb = new ImageView(popsplithb);
        gridpane.add(imageViewpopsplithb, 10, 11);
        ImageView imageViewpopsplitba = new ImageView(popsplitba);
        gridpane.add(imageViewpopsplitba, 10, 13);
        ImageView imageViewpopsplitbb = new ImageView(popsplitbb);
        gridpane.add(imageViewpopsplitbb, 10, 14);

        // afficher 1 piège par dessus ce qui précède
        ImageView imageViewalter1 = new ImageView(trapped);
        gridpane.add(imageViewalter1, 8, 6);
        ImageView imageViewalter2 = new ImageView(trapped);
        gridpane.add(imageViewalter2, 8, 5);
        ImageView imageViewalter3 = new ImageView(trapped);
        gridpane.add(imageViewalter3, 8, 4);
        ImageView imageViewalter4 = new ImageView(trapped);
        gridpane.add(imageViewalter4, 9, 6);
        ImageView imageViewalter5 = new ImageView(trapped);
        gridpane.add(imageViewalter5, 10, 6);
        ImageView imageViewalter6 = new ImageView(trapped);
        gridpane.add(imageViewalter6, 10, 5);
        ImageView imageViewalter7 = new ImageView(trapped);
        gridpane.add(imageViewalter7, 10, 4);
        ImageView imageViewalter8 = new ImageView(trapped);
        gridpane.add(imageViewalter8, 10, 2);

        // afficher l'animation de migration de population  par dessus ce qui prècède (!!! 2 images => 1 image par case !!!)
        ImageView imageViewpopmigrateda = new ImageView(popmigrateda);// case départ
        gridpane.add(imageViewpopmigrateda, 6, 6);
        ImageView imageViewpopmigratedb = new ImageView(popmigratedb);//case arrivée
        gridpane.add(imageViewpopmigratedb, 7, 6);
        ImageView imageViewpopmigratega = new ImageView(popmigratega);
        gridpane.add(imageViewpopmigratega, 6, 5);
        ImageView imageViewpopmigrategb = new ImageView(popmigrategb);
        gridpane.add(imageViewpopmigrategb, 5, 5);
       ImageView imageViewpopmigrateha = new ImageView(popmigrateha);
        gridpane.add(imageViewpopmigrateha, 6, 2);
        ImageView imageViewpopmigratehb = new ImageView(popmigratehb);
        gridpane.add(imageViewpopmigratehb, 6, 1);
       ImageView imageViewpopmigrateba = new ImageView(popmigrateba);
        gridpane.add(imageViewpopmigrateba, 6, 7);
        ImageView imageViewpopmigratebb = new ImageView(popmigratebb);
        gridpane.add(imageViewpopmigratebb, 6, 8);

        // afficher 1 population ( selon gradient ) par dessus ce qui prècède
        ImageView imageViewpop = new ImageView(pop5);
        gridpane.add(imageViewpop, 6, 6);
        ImageView imageViewpop2 = new ImageView(pop25);
        gridpane.add(imageViewpop2, 6, 5);
        ImageView imageViewpop3 = new ImageView(pop50);
        gridpane.add(imageViewpop3, 6, 4);
        ImageView imageViewpop4 = new ImageView(pop75);
        gridpane.add(imageViewpop4, 6, 3);
        ImageView imageViewpop5 = new ImageView(pop95);
        gridpane.add(imageViewpop5, 6, 2);
        ImageView imageViewpop6 = new ImageView(pop5);
        gridpane.add(imageViewpop6, 10, 6);
        ImageView imageViewpop7 = new ImageView(pop25);
        gridpane.add(imageViewpop7, 10, 5);
        ImageView imageViewpop8 = new ImageView(pop50);
        gridpane.add(imageViewpop8, 10, 4);
        ImageView imageViewpop9 = new ImageView(pop75);
        gridpane.add(imageViewpop9, 10, 3);
        ImageView imageViewpop10 = new ImageView(pop95);
        gridpane.add(imageViewpop10, 10, 2);
        ImageView imageViewpop11 = new ImageView(pop50);
        gridpane.add(imageViewpop11, 3, 4);
        ImageView imageViewpop12 = new ImageView(pop50);
        gridpane.add(imageViewpop12, 3, 6);
        ImageView imageViewpop13 = new ImageView(pop50);
        gridpane.add(imageViewpop13, 3, 5);
        ImageView imageViewpop14 = new ImageView(pop50);
        gridpane.add(imageViewpop14, 3, 5);

        // afficher l'animation de consommation de case par dessus ce qui précède
        ImageView imageViewforage = new ImageView(forage);
        gridpane.add(imageViewforage, 3,4);

        // afficher l'animation de consommation de case piégée par dessus ce qui précède
        ImageView imageViewforage_trapped = new ImageView(forage_trapped);
        gridpane.add(imageViewforage_trapped, 3, 6);

        // afficher l'animation de combat entre 2 populations par dessus ce qui précède
        ImageView imageViewconflict = new ImageView(conflict);
        gridpane.add(imageViewconflict, 3, 5);

        /*
        // incompatible tore : afficher 1 split de populations par dessus ce qui prècède (!!! nécéssite 2 node adjacent !!!)
        ImageView imageViewpopsplit1 = new ImageView(popsplit);
        gridpane.setColumnSpan(imageViewpopsplit1, 2);
        gridpane.add(imageViewpopsplit1, 31, 1);
        */

        // afficher une action invalide par dessus ce qui prècède
        ImageView imageViewinvalid1 = new ImageView(invalid);
        gridpane.add(imageViewinvalid1, 10, 2);
        ImageView imageViewinvalid2 = new ImageView(invalid);
        gridpane.add(imageViewinvalid2, 10, 3);
        ImageView imageViewinvalid3 = new ImageView(invalid);
        gridpane.add(imageViewinvalid3, 10, 4);
        ImageView imageViewinvalid4 = new ImageView(invalid);
        gridpane.add(imageViewinvalid4, 10, 5);
        ImageView imageViewinvalid5 = new ImageView(invalid);
        gridpane.add(imageViewinvalid5, 10, 6);

        // Display image on screen
        root.getChildren().add(gridpane);
        primaryStage.setTitle("test animated");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(false);
        primaryStage.show();
    }
}
