package it.polimi.ingsw.view;

import it.polimi.ingsw.model.cards.PowerUp;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Class with methods used by render for rendering the player boards
 *
 * @author  davidealde
 */
public class PlayerBoardRenderer {

    private double scalePB;
    private List<ClientModel.SimplePlayer> players;
    private ClientModel clientModel;
    private String renderInstruction;
    private List<Button> inputButtons;
    private List<String> labelButton;


    public PlayerBoardRenderer(double scPB, ClientModel clientModel){
        this.scalePB=scPB;
        this.clientModel=clientModel;
    }

    public void setScalePB(double scalePB) {
        this.scalePB = scalePB;
    }

    public void setPlayers(List<ClientModel.SimplePlayer> players) {this.players = players;}

    public void setClientModel(ClientModel clientModel) {
        this.clientModel = clientModel;
    }

    public void setRenderInstruction(String renderInstruction) {
        this.renderInstruction = renderInstruction;
    }

    public void setInputButtons(List<Button> inputButtons) {
        this.inputButtons = inputButtons;
    }

    public void setLabelButton(List<String> labelButton) {
        this.labelButton = labelButton;
    }

    public List<GridPane> ammoRender(){
    List<GridPane> playerAmmoGrid = new ArrayList<>();

    InputStream redAmmoFile=this.getClass().getResourceAsStream("/images/miscellaneous/redAmmo.png");
    InputStream blueAmmoFile=this.getClass().getResourceAsStream("/images/miscellaneous/blueAmmo.png");
    InputStream yellowAmmoFile=this.getClass().getResourceAsStream("/images/miscellaneous/yellowAmmo.png");

    Image redAmmoImage=new Image(redAmmoFile);
    Image blueAmmoImage=new Image(blueAmmoFile);
    Image yellowAmmoImage=new Image(yellowAmmoFile);
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

    public List<GridPane> damagesRenderer(){
        List<GridPane> damageGrid = new ArrayList<>();
        List<ArrayList<ImageView>> damageView = new ArrayList<>();
        int gridIndex=0;
        for(ClientModel.SimplePlayer p : players) {
            int dmgAmount = p.getDamage(players).size();
            damageGrid.add(new GridPane());
            damageView.add(new ArrayList<>());
            for(int i=0; i< dmgAmount; i++) {
                damageView.get(gridIndex).add(new ImageView(damageImageSelecter(p.getDamage(players).get(i))));
                damageView.get(gridIndex).get(i).setFitHeight(29*scalePB);
                damageView.get(gridIndex).get(i).setPreserveRatio(true);
                damageGrid.get(gridIndex).add(damageView.get(gridIndex).get(i),i,0);
                damageGrid.get(gridIndex).setTranslateX(40*scalePB);
                damageGrid.get(gridIndex).setTranslateY(50*scalePB);
            }
            gridIndex++;
        }
        return damageGrid;
    }

    private Image damageImageSelecter(ClientModel.SimplePlayer shooter){
        String color;
        color=shooter.getColor();
        Image damageImage = new Image(getClass().getResourceAsStream("/images/miscellaneous/"+color+"Blood.png"));
        return damageImage;
    }

    public List<GridPane> marksRenderer(){
        List<GridPane> marksGrid = new ArrayList<>();
        List<ArrayList<ImageView>> marksView = new ArrayList<>();
        int gridIndex=0;
        for(ClientModel.SimplePlayer p : players) {
            int marksAmount = p.getMark(players).size();
            marksGrid.add(new GridPane());
            marksView.add(new ArrayList<>());
            for(int i=0; i< marksAmount; i++) {
                marksView.get(gridIndex).add(new ImageView(damageImageSelecter(p.getMark(players).get(i))));
                marksView.get(gridIndex).get(i).setFitHeight(20*scalePB);
                marksView.get(gridIndex).get(i).setPreserveRatio(true);
                marksGrid.get(gridIndex).add(marksView.get(gridIndex).get(i),i,0);
                marksGrid.get(gridIndex).setTranslateX(230*scalePB);
            }
            gridIndex++;
        }
        return marksGrid;
    }

    public List<GridPane> skullsPlayerRenderer(List<Integer> deathsNumber){
        List<GridPane> skullGrid = new ArrayList<>();
        Image skullImage = new Image(getClass().getResourceAsStream("/images/miscellaneous/skull.png"));
        List<ArrayList<ImageView>> skullView = new ArrayList<>();
        int gridIndex=0;
        for(ClientModel.SimplePlayer p : players) {
            skullGrid.add(new GridPane());
            skullView.add(new ArrayList<>());
            for(int i=0; i< deathsNumber.get(players.indexOf(p)); i++) {
                skullView.get(gridIndex).add(new ImageView(skullImage));
                skullView.get(gridIndex).get(i).setFitHeight(40*scalePB);
                skullView.get(gridIndex).get(i).setPreserveRatio(true);
                skullGrid.get(gridIndex).add(skullView.get(gridIndex).get(i),i,0);
                skullGrid.get(gridIndex).setTranslateX(100*scalePB);
                skullGrid.get(gridIndex).setTranslateY(80*scalePB);
            }
            gridIndex++;
        }
        return skullGrid;
    }

    public List<MenuButton> handRenderer(){
        List<ArrayList<ImageView>> weaponHandView = new ArrayList<>();
        List<ArrayList<Label>> loadUnload = new ArrayList<>();
        List<ArrayList<Pane>> weaponContainer = new ArrayList<>();
        List<ImageView> puBackHandView = new ArrayList<>();
        List<Label> puHandNumber = new ArrayList<>();
        List<MenuItem> handItem = new ArrayList<>();
        List<HBox> handContainer = new ArrayList<>();
        List<MenuButton> handButton = new ArrayList<>();
        List<ClientModel.SimpleWeapon> weapons;
        Image weaponImage;
        Image puBackImage = new Image(getClass().getResourceAsStream("/images/cards/pUBack.png"));
        List<Pane> puContainer = new ArrayList<>();
        for(ClientModel.SimplePlayer p : players) {
            handContainer.add(new HBox());
            weaponHandView.add(new ArrayList<>());
            loadUnload.add(new ArrayList<>());
            weaponContainer.add(new ArrayList<>());
            weapons = p.getWeapons();

            for (ClientModel.SimpleWeapon w : weapons) {
                String key = w.getName();
                weaponImage = new Image(getClass().getResourceAsStream("/images/cards/"+key.replace(" ","_")+".png"));
                weaponHandView.get(players.indexOf(p)).add(new ImageView(weaponImage));
                weaponHandView.get(players.indexOf(p)).get(weapons.indexOf(w)).setFitHeight(300*scalePB);
                weaponHandView.get(players.indexOf(p)).get(weapons.indexOf(w)).setPreserveRatio(true);
                loadUnload.get(players.indexOf(p)).add(new Label());
                if(w.isLoaded()) {
                    loadUnload.get(players.indexOf(p)).get(weapons.indexOf(w)).setText("CARICA!!!");
                    loadUnload.get(players.indexOf(p)).get(weapons.indexOf(w)).setTextFill(Color.web("#FF0000"));
                }
                else {
                    loadUnload.get(players.indexOf(p)).get(weapons.indexOf(w)).setText("SCARICA");
                    loadUnload.get(players.indexOf(p)).get(weapons.indexOf(w)).setTextFill(Color.web("#F8F8FF"));
                }
                loadUnload.get(players.indexOf(p)).get(weapons.indexOf(w)).setFont(new Font("Arial", 40*scalePB));
                loadUnload.get(players.indexOf(p)).get(weapons.indexOf(w)).setTranslateY(50*scalePB);
                weaponContainer.get(players.indexOf(p)).add(new Pane());
                weaponContainer.get(players.indexOf(p)).get(weapons.indexOf(w)).getChildren().addAll(weaponHandView.get(players.indexOf(p)).get(weapons.indexOf(w)),
                        loadUnload.get(players.indexOf(p)).get(weapons.indexOf(w)));
                if(renderInstruction.equals("Weapon") && p==clientModel.getCurrentPlayer() && labelButton.contains(w.getName())){
                    weaponContainer.get(players.indexOf(p)).get(weapons.indexOf(w)).getChildren().add(inputButtons.get(labelButton.indexOf(w.getName())));
                    inputButtons.get(labelButton.indexOf(w.getName())).setPrefWidth(180*scalePB);
                    inputButtons.get(labelButton.indexOf(w.getName())).setPrefHeight(300*scalePB);
                    inputButtons.get(labelButton.indexOf(w.getName())).setStyle("-fx-background-color: transparent;");
                }
                handContainer.get(players.indexOf(p)).getChildren().add(weaponContainer.get(players.indexOf(p)).get(weapons.indexOf(w)));
            }
            puBackHandView.add(new ImageView(puBackImage));
            puBackHandView.get(puBackHandView.size()-1).setFitHeight(200*scalePB);
            puBackHandView.get(puBackHandView.size()-1).setPreserveRatio(true);
            puHandNumber.add(new Label(Integer.toString(p.getCardNumber())));
            puHandNumber.get(players.indexOf(p)).setFont(new Font("Arial", 60*scalePB));
            puHandNumber.get(players.indexOf(p)).setTextFill(Color.web("#F8F8FF"));
            puHandNumber.get(players.indexOf(p)).setTranslateX(-50);
            if(p.getId() != clientModel.getPlayerID())
                handContainer.get(players.indexOf(p)).getChildren().addAll(puBackHandView.get(players.indexOf(p)), puHandNumber.get(players.indexOf(p)));
            else{
                List<ImageView> puView = new ArrayList<>();
                for(String pu : clientModel.getPowerUpInHand()){
                    String color = clientModel.getColorPowerUpInHand().get(clientModel.getPowerUpInHand().indexOf(pu));
                    Image puImage = new Image(getClass().getResourceAsStream("/images/cards/"+color+pu.replace(" ","_")+".png"));
                    puView.add(new ImageView(puImage));
                    puView.get(puView.size()-1).setFitHeight(200*scalePB);
                    puView.get(puView.size()-1).setPreserveRatio(true);
                    puContainer.add(new Pane());
                    puContainer.get(puContainer.size()-1).getChildren().add(puView.get(puView.size()-1));
                    if(renderInstruction.equals("PowerUp")){
                        System.out.println("DENREO1");
                        String labelPowerUp = clientModel.getColorPowerUpInHand().get(clientModel.getPowerUpInHand().indexOf(pu));
                        labelPowerUp = labelPowerUp.substring(0, 1).toUpperCase() + labelPowerUp.substring(1);
                        labelPowerUp = labelPowerUp + " " + pu;
                        if(labelButton.contains(labelPowerUp)) {
                            System.out.println("dentro2");
                        puContainer.get(puContainer.size() - 1).getChildren().add(inputButtons.get(labelButton.indexOf(labelPowerUp)));
                        inputButtons.get(labelButton.indexOf(labelPowerUp)).setPrefWidth(135*scalePB);
                        inputButtons.get(labelButton.indexOf(labelPowerUp)).setPrefHeight(200*scalePB);
                        inputButtons.get(labelButton.indexOf(labelPowerUp)).setStyle("-fx-background-color: transparent;");
                    }}
                    handContainer.get(players.indexOf(p)).getChildren().add(puContainer.get(puContainer.size()-1));
                }
            }

            handItem.add(new MenuItem());
            handItem.get(players.indexOf(p)).setGraphic(handContainer.get(players.indexOf(p)));
            handButton.add(new MenuButton("CARTE", null, handItem.get(players.indexOf(p))));
            handButton.get(players.indexOf(p)).setTranslateX(410 * scalePB);
            handButton.get(players.indexOf(p)).setTranslateY(80 * scalePB);
        }
        return handButton;
    }

    public List<GridPane> pointsRenderer(){
        Image point1Image = new Image(getClass().getResourceAsStream("/images/miscellaneous/point1.png"));
        Image point2Image = new Image(getClass().getResourceAsStream("/images/miscellaneous/point2.png"));
        Image point4Image = new Image(getClass().getResourceAsStream("/images/miscellaneous/point4.png"));
        List<ImageView> point1View = new ArrayList<>();
        List<ImageView> point2View = new ArrayList<>();
        List<ImageView> point4View = new ArrayList<>();
        int p1=0;
        int p2=0;
        int p4=0;
        int rowIndex;
        List<GridPane> pointsGrid = new ArrayList<>();
        int pointsAmount;
        for(ClientModel.SimplePlayer p : players){
            pointsGrid.add(new GridPane());
            pointsAmount = p.getPoints();
            rowIndex=0;
            while(pointsAmount-4>=0){
                pointsAmount-=4;
                point4View.add(new ImageView(point4Image));
                pointsGrid.get(players.indexOf(p)).add(point4View.get(p4),rowIndex,0);
                p4++; rowIndex++;
            }
            if(pointsAmount>=2){
                pointsAmount-=2;
                point2View.add(new ImageView(point2Image));
                pointsGrid.get(players.indexOf(p)).add(point2View.get(p2),rowIndex,0);
                p2++; rowIndex++;
            }
            if(pointsAmount==1){
                point1View.add(new ImageView(point1Image));
                pointsGrid.get(players.indexOf(p)).add(point1View.get(p1),rowIndex,0);
                p1++;
            }

        }
        for(ImageView one : point1View){
            one.setFitHeight(30*scalePB);
            one.setPreserveRatio(true);
        }
        for(ImageView two : point2View){
            two.setFitHeight(30*scalePB);
            two.setPreserveRatio(true);
        }
        for(ImageView four : point4View){
            four.setFitHeight(30*scalePB);
            four.setPreserveRatio(true);
        }
        for(ClientModel.SimplePlayer p : players)
            pointsGrid.get(players.indexOf(p)).setTranslateX(30*scalePB);

        return pointsGrid;
    }

}
