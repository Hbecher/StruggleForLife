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
        Image trap = new Image("sample/trap.gif");
        Image conflict = new Image("sample/conflict.gif");
        Image vide = new Image("sample/vide.gif");

        GridPane gridpanedecor = new GridPane();
        GridPane gridpaneregenfond = new GridPane();
        GridPane gridpanedead = new GridPane();
        GridPane gridpaneextnewpop = new GridPane();
        GridPane gridpanepiege = new GridPane();
        GridPane gridpanemigration = new GridPane();
        GridPane gridpanepop = new GridPane();
        GridPane gridpanefront = new GridPane();


        // pour remplir initialement les gridpanes (couches graphiques)
        int k = 0;
        ImageView[][][] tab3d = new ImageView[32][5*4][8];
        for (int l = 0; l < 8; l++) {
            for (int j = 0; j < 32; j++){
                for (int i = 0; i < 5*4; i++) {

                    //image vide par défaut pour les gridpanes
                    ImageView objetimage = new ImageView(vide);

                    if(l == 0) {
                        //gridpane decor
                        if (k == 0) {objetimage.setImage(forest); k = 1;}
                        else if (k == 1) {objetimage.setImage(meadow); k = 2;}
                        else if (k == 2) {objetimage.setImage(lake); k = 3;}
                        else {objetimage.setImage(desert); k = 0;}
                        gridpanedecor.add(objetimage, j, i);// j et i sont les coordonnées (x,y) de la gridpane où mettre l'image
                    }
                    else if(l == 1) {
                        //gridpane regen map / (back) conso case / conso case piégée / piégeage case
                        gridpaneregenfond.add(objetimage, j, i);
                    }
                    else if(l == 2) {
                        //gridpane tombes
                        gridpanedead.add(objetimage, j, i);
                    }
                    else if(l == 3) {
                        //gridpane [animation] extinction pop / split pop (4 directions)
                        gridpaneextnewpop.add(objetimage, j, i);
                    }
                    else if(l == 4) {
                        // gridpane piege en place (bulles)
                        gridpanepiege.add(objetimage, j, i);
                    }
                    else if(l == 5) {
                        // gridpane migration pop (4 directions)
                        gridpanemigration.add(objetimage, j, i);
                    }
                    else if(l == 6) {
                        // gridpane pop (5, 25, 50, 75, 95)
                        gridpanepop.add(objetimage, j, i);
                    }
                    else { //if(l == 7)
                        // gridpane  conflict / action invalide /(front) conso case / conso case piégée / piégeage case
                        gridpanefront.add(objetimage, j, i);
                    }
                    // tableau
                    tab3d[j][i][l] = objetimage;
                }
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
        tab3d[13][7][1].setImage(regenlake);
        tab3d[14][7][1].setImage(regenmeadow);
        tab3d[14][6][1].setImage(regenforest);
        // afficher l'animation de fond de consommation de case par dessus les images des cases
        tab3d[3][4][1].setImage(forage_back);
        // afficher l'animation de fond de consommation de case piégée par dessus ce qui précède
        tab3d[3][6][1].setImage(forage_trapped_back);
        // afficher l'animation de piégeage de case par dessus ce qui précède
        tab3d[3][3][1].setImage(forage_trapped_back);

        // afficher 1 tombe par dessus par dessus ce qui précède
        tab3d[6][7][2].setImage(dead);
        tab3d[9][6][2].setImage(dead);

        // afficher l'animation d'extinction de population  par dessus ce qui précède
        tab3d[10][7][3].setImage(pop_death);
        // afficher l'animation de split de population  par dessus ce qui précède(!!! 2 images => 1 image par case !!!)
        tab3d[10][9][3].setImage(popsplitda);// case départ
        tab3d[11][9][3].setImage(popsplitdb);//case arrivée
        tab3d[10][10][3].setImage(popsplitga);
        tab3d[9][9][3].setImage(popsplitgb);
        tab3d[10][12][3].setImage(popsplitha);
        tab3d[10][11][3].setImage(popsplithb);
        tab3d[10][13][3].setImage(popsplitba);
        tab3d[10][14][3].setImage(popsplitbb);

        // afficher 1 piège par dessus ce qui précède
        tab3d[8][6][4].setImage(trapped);
        tab3d[8][5][4].setImage(trapped);
        tab3d[8][4][4].setImage(trapped);
        tab3d[9][6][4].setImage(trapped);
        tab3d[10][6][4].setImage(trapped);
        tab3d[10][5][4].setImage(trapped);
        tab3d[10][4][4].setImage(trapped);
        tab3d[10][2][4].setImage(trapped);

        // afficher l'animation de migration de population  par dessus ce qui prècède (!!! 2 images => 1 image par case !!!)
        tab3d[6][6][5].setImage(popmigrateda);
        tab3d[7][6][5].setImage(popmigratedb);
        tab3d[6][5][5].setImage(popmigratega);
        tab3d[5][5][5].setImage(popmigrategb);
        tab3d[6][2][5].setImage(popmigrateha);
        tab3d[6][1][5].setImage(popmigratehb);
        tab3d[6][7][5].setImage(popmigrateba);
        tab3d[6][8][5].setImage(popmigratebb);

        // afficher 1 population ( selon gradient ) par dessus ce qui prècède
        tab3d[6][6][6].setImage(pop5);
        tab3d[6][5][6].setImage(pop25);
        tab3d[6][4][6].setImage(pop50);
        tab3d[6][3][6].setImage(pop75);
        tab3d[6][2][6].setImage(pop95);
        tab3d[10][6][6].setImage(pop5);
        tab3d[10][5][6].setImage(pop25);
        tab3d[10][4][6].setImage(pop50);
        tab3d[10][3][6].setImage(pop75);
        tab3d[10][2][6].setImage(pop95);
        tab3d[3][4][6].setImage(pop50);
        tab3d[3][6][6].setImage(pop50);
        tab3d[3][5][6].setImage(pop50);
        tab3d[3][5][6].setImage(pop50);

        // afficher l'animation de consommation de case par dessus ce qui précède
        tab3d[3][4][7].setImage(forage);
        // afficher l'animation de consommation de case piégée par dessus ce qui précède
        tab3d[3][6][7].setImage(forage_trapped);
        // afficher l'animation de piégeage de case par dessus ce qui précède
        tab3d[3][3][7].setImage(trap);
        // afficher l'animation de combat entre 2 populations par dessus ce qui précède
        tab3d[3][5][7].setImage(conflict);
        // afficher une action invalide par dessus ce qui prècède
        tab3d[10][2][7].setImage(invalid);
        tab3d[10][3][7].setImage(invalid);
        tab3d[10][4][7].setImage(invalid);
        tab3d[10][5][7].setImage(invalid);
        tab3d[10][6][7].setImage(invalid);

        // Display image on screen
        root.getChildren().addAll(gridpanedecor,gridpaneregenfond,gridpanedead,gridpaneextnewpop, gridpanepiege, gridpanemigration, gridpanepop, gridpanefront);

        //test remettre une image vide
        tab3d[31][19][0].setImage(vide);

        primaryStage.setTitle("test animated");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(false);
        primaryStage.show();
    }
}
