package it.polimi.ingsw.view.guirenderer;

import it.polimi.ingsw.view.ClientModel;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.ColorAdjust;
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
        List<Image> damageImage = damageImageSelector();
        for(ClientModel.SimplePlayer p : players) {
            int dmgAmount = p.getDamage(players).size();
            damageGrid.add(new GridPane());
            damageView.add(new ArrayList<>());
            for(int i=0; i< dmgAmount; i++) {
                damageView.get(gridIndex).add(new ImageView(damageImage.get(damageImageIndex(p.getDamage(players).get(i).getColor()))));
                damageView.get(gridIndex).get(i).setFitHeight(32*scalePB);
                damageView.get(gridIndex).get(i).setPreserveRatio(true);
                damageGrid.get(gridIndex).add(damageView.get(gridIndex).get(i),i,0);
                if( ! p.isFlipped()) {
                    damageGrid.get(gridIndex).setMargin(damageView.get(gridIndex).get(i), new javafx.geometry.Insets(0, -4.4 * scalePB, 0, 0));
                    damageGrid.get(gridIndex).setTranslateX(40 * scalePB);
                }
                else {
                    damageGrid.get(gridIndex).setMargin(damageView.get(gridIndex).get(i), new javafx.geometry.Insets(0, -5 * scalePB, 0, 0));
                    damageGrid.get(gridIndex).setTranslateX(45 * scalePB);
                }
                damageGrid.get(gridIndex).setTranslateY(45*scalePB);
            }
            gridIndex++;
        }
        return damageGrid;
    }

    private List<Image> damageImageSelector(){
        String[] color = {"green", "yellow", "grey", "purple", "blue"};
        List<Image> damageImage = new ArrayList<>();
        for(String c : color) {
            damageImage.add(new Image(getClass().getResourceAsStream("/images/miscellaneous/" + c + "Blood.png")));
        }
        return damageImage;
    }

    private int damageImageIndex(String color){
        switch (color){
            case "green":
                return 0;
            case "yellow":
                return 1;
            case "grey":
                return 2;
            case "purple":
                return 3;
                default:
                    return 4;
        }
    }

    public List<GridPane> marksRenderer(){
        List<GridPane> marksGrid = new ArrayList<>();
        List<ArrayList<ImageView>> marksView = new ArrayList<>();
        int gridIndex=0;
        List<Image> damageImage = damageImageSelector();
        for(ClientModel.SimplePlayer p : players) {
            int marksAmount = p.getMark(players).size();
            marksGrid.add(new GridPane());
            marksView.add(new ArrayList<>());
            for(int i=0; i< marksAmount; i++) {
                marksView.get(gridIndex).add(new ImageView(damageImage.get(damageImageIndex(p.getMark(players).get(i).getColor()))));
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
                if(p.isFlipped())
                    skullGrid.get(gridIndex).setTranslateX(130*scalePB);
                else
                    skullGrid.get(gridIndex).setTranslateX(100*scalePB);
                skullGrid.get(gridIndex).setTranslateY(80*scalePB);
            }
            gridIndex++;
        }
        return skullGrid;
    }

    public List<MenuButton> handRenderer(){
        System.out.println(inputButtons);
        System.out.println(labelButton);
        System.out.println(renderInstruction);
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
                if( ! w.isLoaded()) {
                    loadUnload.get(players.indexOf(p)).get(weapons.indexOf(w)).setText("SCARICA");
                    loadUnload.get(players.indexOf(p)).get(weapons.indexOf(w)).setTextFill(Color.web("#F8F8FF"));
                }
                loadUnload.get(players.indexOf(p)).get(weapons.indexOf(w)).setFont(new Font("Arial", 40*scalePB));
                loadUnload.get(players.indexOf(p)).get(weapons.indexOf(w)).setTranslateY(50*scalePB);
                weaponContainer.get(players.indexOf(p)).add(new Pane());
                weaponContainer.get(players.indexOf(p)).get(weapons.indexOf(w)).getChildren().addAll(weaponHandView.get(players.indexOf(p)).get(weapons.indexOf(w)),
                        loadUnload.get(players.indexOf(p)).get(weapons.indexOf(w)));
                if(renderInstruction.equals("Weapon") && p==clientModel.getCurrentPlayer()){
                    if(labelButton.contains(w.getName())) {
                        weaponContainer.get(players.indexOf(p)).get(weapons.indexOf(w)).getChildren().add(inputButtons.get(labelButton.indexOf(w.getName())));
                        inputButtons.get(labelButton.indexOf(w.getName())).setPrefWidth(180 * scalePB);
                        inputButtons.get(labelButton.indexOf(w.getName())).setPrefHeight(300 * scalePB);
                        inputButtons.get(labelButton.indexOf(w.getName())).setStyle("-fx-background-color: transparent;");
                    }else{
                        ColorAdjust lessVisible = new ColorAdjust();
                        lessVisible.setBrightness(-0.5);
                        weaponHandView.get(players.indexOf(p)).get(weapons.indexOf(w)).setEffect(lessVisible);
                    }
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
                List<String> labelButton2 = new ArrayList<>(labelButton);
                for(int i = 0; i < clientModel.getPowerUpInHand().size(); i++){
                    String color = clientModel.getColorPowerUpInHand().get(i);
                    String pu = clientModel.getPowerUpInHand().get(i);
                    Image puImage = new Image(getClass().getResourceAsStream("/images/cards/"+color+pu.replace(" ","_")+".png"));
                    puView.add(new ImageView(puImage));
                    puView.get(puView.size()-1).setFitHeight(200*scalePB);
                    puView.get(puView.size()-1).setPreserveRatio(true);
                    puContainer.add(new Pane());
                    puContainer.get(puContainer.size()-1).getChildren().add(puView.get(puView.size()-1));
                    if(renderInstruction.equals("PowerUp")){
                        StringBuilder builder = new StringBuilder();
                        builder.append(clientModel.getColorPowerUpInHand().get(i));
                        String labelPowerUp = clientModel.getColorPowerUpInHand().get(i);
                        labelPowerUp = labelPowerUp.substring(0, 1).toUpperCase() + labelPowerUp.substring(1);
                        labelPowerUp = labelPowerUp + " " + pu;
                        if(labelButton2.contains(labelPowerUp)) {
                            puContainer.get(puContainer.size() - 1).getChildren().add(inputButtons.get(labelButton2.indexOf(labelPowerUp)));
                            inputButtons.get(labelButton2.indexOf(labelPowerUp)).setPrefWidth(135*scalePB);
                            inputButtons.get(labelButton2.indexOf(labelPowerUp)).setPrefHeight(200*scalePB);
                            inputButtons.get(labelButton2.indexOf(labelPowerUp)).setStyle("-fx-background-color: transparent;");
                            labelButton2.set(labelButton2.indexOf(labelPowerUp), "used");
                        }else {
                            ColorAdjust lessVisible = new ColorAdjust();
                            lessVisible.setBrightness(-0.5);
                            puView.get(puView.size() - 1).setEffect(lessVisible);
                        }
                    }
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

    public GridPane pointsRenderer(){
        Image point1Image = new Image(getClass().getResourceAsStream("/images/miscellaneous/point1.png"));
        Image point2Image = new Image(getClass().getResourceAsStream("/images/miscellaneous/point2.png"));
        Image point4Image = new Image(getClass().getResourceAsStream("/images/miscellaneous/point4.png"));
        List<ImageView> point1View = new ArrayList<>();
        List<ImageView> point2View = new ArrayList<>();
        List<ImageView> point4View = new ArrayList<>();
        int p4=0;
        int rowIndex;
        GridPane pointsGrid = new GridPane();
        int pointsAmount;

            pointsAmount = clientModel.getCurrentPlayer().getPoints();
            rowIndex=0;
            while(pointsAmount-4>=0){
                pointsAmount-=4;
                point4View.add(new ImageView(point4Image));
                pointsGrid.add(point4View.get(p4),rowIndex,0);
                p4++; rowIndex++;
            }
            if(pointsAmount>=2){
                pointsAmount-=2;
                point2View.add(new ImageView(point2Image));
                pointsGrid.add(point2View.get(0),rowIndex,0);
                rowIndex++;
            }
            if(pointsAmount==1){
                point1View.add(new ImageView(point1Image));
                pointsGrid.add(point1View.get(0),rowIndex,0);
            }


        for(ImageView one : point1View){
            one.setFitHeight(40*scalePB);
            one.setPreserveRatio(true);
        }
        for(ImageView two : point2View){
            two.setFitHeight(40*scalePB);
            two.setPreserveRatio(true);
        }
        for(ImageView four : point4View){
            four.setFitHeight(40*scalePB);
            four.setPreserveRatio(true);
        }

        pointsGrid.setTranslateX(500*scalePB);
        pointsGrid.setTranslateY(70*scalePB);

        return pointsGrid;
    }

}
