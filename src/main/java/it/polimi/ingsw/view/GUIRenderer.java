package it.polimi.ingsw.view;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import javafx.scene.layout.*;
import javafx.event.*;
import javafx.scene.control.*;
import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import java.awt.*;
import javafx.scene.layout.*;
import javafx.event.*;
import javafx.scene.control.*;
import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.scene.control.*;


public class GUIRenderer {

    private double scalePB;
    private ClientModel clientModel;


    public GUIRenderer(ClientModel cm, double scalePB){
        this.clientModel=cm;
    }


public List<GridPane> ammoRender(List<ClientModel.SimplePlayer> players, double scalePB){
    List<GridPane> playerAmmoGrid = new ArrayList<>();

    InputStream redAmmoFile=this.getClass().getResourceAsStream("/images/miscellaneous/redAmmo.png");
    InputStream blueAmmoFile=this.getClass().getResourceAsStream("/images/miscellaneous/blueAmmo.png");
    InputStream yellowAmmoFile=this.getClass().getResourceAsStream("/images/miscellaneous/yellowAmmo.png");

    Image redAmmoImage=new Image(redAmmoFile);
    Image blueAmmoImage=new Image(blueAmmoFile);
    Image yellowAmmoImage=new Image(yellowAmmoFile);
System.out.println("mommy"+scalePB);
    List<ArrayList<ImageView>> redAmmoView = new ArrayList<>();
    List<ArrayList<ImageView>> blueAmmoView = new ArrayList<>();
    List<ArrayList<ImageView>> yellowAmmoView = new ArrayList<>();

    int gridIndex=0;
    for(ClientModel.SimplePlayer p : players) {
        playerAmmoGrid.add(new GridPane());
        redAmmoView.add(new ArrayList<>());
        int rAmmo = p.getRedAmmo();
        for (int i = 0; i < rAmmo; i++) {
            redAmmoView.get(gridIndex).add(new ImageView(redAmmoImage));
            redAmmoView.get(gridIndex).get(i).setFitHeight(20*scalePB);
            redAmmoView.get(gridIndex).get(i).setPreserveRatio(true);
            playerAmmoGrid.get(gridIndex).add(redAmmoView.get(gridIndex).get(i), i, 0);
            playerAmmoGrid.get(gridIndex).setMargin(redAmmoView.get(gridIndex).get(i),new Insets(0,0,5*scalePB,5*scalePB));
        }
        blueAmmoView.add(new ArrayList<>());
        int bAmmo = p.getBlueAmmo();
        for (int i = 0; i < bAmmo; i++) {
            blueAmmoView.get(gridIndex).add(new ImageView(blueAmmoImage));
            blueAmmoView.get(gridIndex).get(i).setFitHeight(20*scalePB);
            blueAmmoView.get(gridIndex).get(i).setPreserveRatio(true);
            playerAmmoGrid.get(gridIndex).add(blueAmmoView.get(gridIndex).get(i), i, 1);
            playerAmmoGrid.get(gridIndex).setMargin(blueAmmoView.get(gridIndex).get(i),new Insets(0,0,5*scalePB,5*scalePB));
        }
        yellowAmmoView.add(new ArrayList<>());
        int yAmmo = p.getYellowAmmo();
        for (int i = 0; i < yAmmo; i++) {
            yellowAmmoView.get(gridIndex).add(new ImageView(yellowAmmoImage));
            yellowAmmoView.get(gridIndex).get(i).setFitHeight(20*scalePB);
            yellowAmmoView.get(gridIndex).get(i).setPreserveRatio(true);
            playerAmmoGrid.get(gridIndex).add(yellowAmmoView.get(gridIndex).get(i), i, 2);
            playerAmmoGrid.get(gridIndex).setMargin(yellowAmmoView.get(gridIndex).get(i),new Insets(0,0,5*scalePB,5*scalePB));
        }
        gridIndex++;
    }

    return  playerAmmoGrid;
}

    public GridPane killShotTrackRender(int skullNumber, double scale){
        List<ImageView> skulls = new ArrayList<>();
        InputStream skullFile = this.getClass().getResourceAsStream("/images/miscellaneous/skull.png");

        Image skullImage = new Image(skullFile);
        for(int i=0; i<skullNumber;i++)
            skulls.add(new ImageView(skullImage));

        for(ImageView s : skulls){
            s.setFitWidth(44.5*scale);
            s.setPreserveRatio(true);
        }
        GridPane skullsGrid = new GridPane();
        List<ColumnConstraints> columnConstraints = new ArrayList<>();
        for(int i=0; i<8-skullNumber;i++){
            columnConstraints.add(new ColumnConstraints(44.5*scale));
            skullsGrid.getColumnConstraints().add(columnConstraints.get(i));
        }
        for(int i=8-skullNumber; i<8;i++)
            skullsGrid.add(skulls.get(i-(8-skullNumber)),i,0,1,1);

        return skullsGrid;

    }

    public List<GridPane> damagesRenderer(List<ClientModel.SimplePlayer> players, double scalePB){
        List<GridPane> damageGrid = new ArrayList<>();
        Image damageImage = new Image(getClass().getResourceAsStream("/images/miscellaneous/blood.png"));
        List<ArrayList<ImageView>> damageView = new ArrayList<>();
        int gridIndex=0;
        for(ClientModel.SimplePlayer p : players) {
            int dmgAmount = p.getDamage().size();
            damageGrid.add(new GridPane());
            damageView.add(new ArrayList<>());
            for(int i=0; i< dmgAmount; i++) {
                damageView.get(gridIndex).add(new ImageView(damageImage));
                damageView.get(gridIndex).get(i).setFitHeight(27.5*scalePB);
                damageView.get(gridIndex).get(i).setPreserveRatio(true);
                damageGrid.get(gridIndex).add(damageView.get(gridIndex).get(i),i,0);
                damageGrid.get(gridIndex).setTranslateX(40*scalePB);
                damageGrid.get(gridIndex).setTranslateY(50*scalePB);
            }
            gridIndex++;
        }
        return damageGrid;
    }

    public List<GridPane> marksRenderer(List<ClientModel.SimplePlayer> players, double scalePB){
        List<GridPane> marksGrid = new ArrayList<>();
        Image marksImage = new Image(getClass().getResourceAsStream("/images/miscellaneous/blood.png"));
        List<ArrayList<ImageView>> marksView = new ArrayList<>();
        int gridIndex=0;
        for(ClientModel.SimplePlayer p : players) {
            int marksAmount = p.getMarks().size();
            marksGrid.add(new GridPane());
            marksView.add(new ArrayList<>());
            for(int i=0; i< marksAmount; i++) {
                marksView.get(gridIndex).add(new ImageView(marksImage));
                marksView.get(gridIndex).get(i).setFitHeight(20*scalePB);
                marksView.get(gridIndex).get(i).setPreserveRatio(true);
                marksGrid.get(gridIndex).add(marksView.get(gridIndex).get(i),i,0);
                marksGrid.get(gridIndex).setTranslateX(230*scalePB);
            }
            gridIndex++;
        }
        return marksGrid;
    }

    public List<GridPane> skullsPlayerRenderer(List<ClientModel.SimplePlayer> players, double scalePB, int[] deathsNumber){
        List<GridPane> skullGrid = new ArrayList<>();
        Image skullImage = new Image(getClass().getResourceAsStream("/images/miscellaneous/skull.png"));
        List<ArrayList<ImageView>> skullView = new ArrayList<>();
        int gridIndex=0;
        for(ClientModel.SimplePlayer p : players) {
            int skullsAmount = deathsNumber[players.indexOf(p)];
            skullGrid.add(new GridPane());
            skullView.add(new ArrayList<>());
            for(int i=0; i< skullsAmount; i++) {
                skullView.get(gridIndex).add(new ImageView(skullImage));
                skullView.get(gridIndex).get(i).setFitHeight(20*scalePB);
                skullView.get(gridIndex).get(i).setPreserveRatio(true);
                skullGrid.get(gridIndex).add(skullView.get(gridIndex).get(i),i,0);
                skullGrid.get(gridIndex).setTranslateX(230*scalePB);
            }
            gridIndex++;
        }
        return skullGrid;
    }



}
