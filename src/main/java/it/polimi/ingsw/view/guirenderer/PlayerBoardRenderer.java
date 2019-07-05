package it.polimi.ingsw.view.guirenderer;

import it.polimi.ingsw.view.ClientModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
 * Class containing methods to render the player boards.
 *
 * @author  davidealde
 */
public class PlayerBoardRenderer {

    private double scalePB;
    private List<ClientModel.SimplePlayer> players;
    private ClientModel clientModel;
    private List<Button> inputButtons;
    private List<String> labelButton;

    /**
     * the type of OPT message to display
     */
    private String renderInstruction;
    private String messageHistory1;
    private String messageHistory2;
    private String messageHistory3;

    private static final float AMMOVIEW_H=20 ;
    private static final float AMMOVIEW_MARGIN=5 ;
    private static final float DAMAGEVIEW_H=32 ;
    private static final Double DAMAGEVIEW_MAGRIN=-4.4 ;
    private static final float DAMAGEVIEW_TX=40 ;
    private static final float DAMAGEVIEWFLIPPED_MAGRIN=-5 ;
    private static final float DAMAGEVIEWFLIPPED_TX=45 ;
    private static final float DAMAGEGRID_TY=45 ;
    private static final float MARKSVIEW_H=20 ;
    private static final float MARKSVIEW_TX=230 ;
    private static final float SKULLVIEW_H=40 ;
    private static final float SKULLGRID_TX=130 ;
    private static final float SKULLGRIDFLIPPED_TX=100 ;
    private static final float SKULLGRID_TY=80 ;
    private static final float WEAPONHANDVIEW_H=300 ;
    private static final float LOADUNLOAD_FONT=25 ;
    private static final float LOADUNLOAD_TY=50 ;
    private static final float WEAPONBUTTON_W=180 ;
    private static final float WEAPONBUTTON_H=300 ;
    private static final float PUHANDNUMBER_FONT=60 ;
    private static final float PUHANDNUMBER_TX=-50 ;
    private static final float PUVIEW_H=200 ;
    private static final float PUBUTTON_W=135 ;
    private static final float PUBUTTON_H=200 ;
    private static final float HANDBUTTON_TX=410 ;
    private static final float HANDBUTTON_TY=80 ;
    private static final float POINTVIEW_H=40 ;
    private static final float POINTSGRID_TX=500 ;
    private static final float POINTSGRID=70 ;
    private static final String HISTORY_DIVIDER="----------------------------------";

    /**
     * Constructor
     *
     * @param scPB          scale factor
     * @param clientModel   client model
     */
    public PlayerBoardRenderer(double scPB, ClientModel clientModel){
        this.scalePB=scPB;
        this.clientModel=clientModel;
        messageHistory1="";
        messageHistory2="";
        messageHistory3="";
    }


    /*
     Setters
     */
    public void setScalePB(double scalePB) {
        this.scalePB = scalePB;
    }


    public void setPlayers(List<ClientModel.SimplePlayer> players) {this.players = players;}


    public void setClientModel(ClientModel clientModel) {
        this.clientModel = clientModel;
    }


    /**
     *renderInstruction gives information about the kind of input: if from message panel or a graphic one and what elements
     *of the screen involves
     *
     * @param renderInstruction     the instruction that indicates the kind of input
     */
    public void setRenderInstruction(String renderInstruction) {
        this.renderInstruction = renderInstruction;
    }


    public void setInputButtons(List<Button> inputButtons) {
        this.inputButtons = inputButtons;
    }


    public void setLabelButton(List<String> labelButton) {
        this.labelButton = labelButton;
    }


    /**
     * Configures the images of the ammo of the players.
     *
     * @return  a list where every element represents the ammo of a player.
     */
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
                redAmmoView.get(gridIndex).get(i).setFitHeight(AMMOVIEW_H * scalePB);
                redAmmoView.get(gridIndex).get(i).setPreserveRatio(true);
                playerAmmoGrid.get(gridIndex).add(redAmmoView.get(gridIndex).get(i), i, 0);
                playerAmmoGrid.get(gridIndex).setMargin(redAmmoView.get(gridIndex).get(i), new Insets(0, 0, AMMOVIEW_MARGIN * scalePB, AMMOVIEW_MARGIN * scalePB));
            }
            blueAmmoView.add(new ArrayList<>());
            int bAmmo = p.getBlueAmmo();
            for (int i = 0; i < bAmmo; i++) {
                blueAmmoView.get(gridIndex).add(new ImageView(blueAmmoImage));
                blueAmmoView.get(gridIndex).get(i).setFitHeight(AMMOVIEW_H * scalePB);
                blueAmmoView.get(gridIndex).get(i).setPreserveRatio(true);
                playerAmmoGrid.get(gridIndex).add(blueAmmoView.get(gridIndex).get(i), i, 1);
                playerAmmoGrid.get(gridIndex).setMargin(blueAmmoView.get(gridIndex).get(i), new Insets(0, 0, AMMOVIEW_MARGIN * scalePB, AMMOVIEW_MARGIN * scalePB));
            }
            yellowAmmoView.add(new ArrayList<>());
            int yAmmo = p.getYellowAmmo();
            for (int i = 0; i < yAmmo; i++) {
                yellowAmmoView.get(gridIndex).add(new ImageView(yellowAmmoImage));
                yellowAmmoView.get(gridIndex).get(i).setFitHeight(AMMOVIEW_H * scalePB);
                yellowAmmoView.get(gridIndex).get(i).setPreserveRatio(true);
                playerAmmoGrid.get(gridIndex).add(yellowAmmoView.get(gridIndex).get(i), i, 2);
                playerAmmoGrid.get(gridIndex).setMargin(yellowAmmoView.get(gridIndex).get(i), new Insets(0, 0, AMMOVIEW_MARGIN * scalePB, AMMOVIEW_MARGIN * scalePB));
            }
            gridIndex++;
        }
        return  playerAmmoGrid;
     }


    /**
     * Configures the images of the damages of every player.
     *
     * @return a list where every element represents the damages of a player.
     */
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
                damageView.get(gridIndex).get(i).setFitHeight(DAMAGEVIEW_H*scalePB);
                damageView.get(gridIndex).get(i).setPreserveRatio(true);
                damageGrid.get(gridIndex).add(damageView.get(gridIndex).get(i),i,0);
                if( ! p.isFlipped()) {
                    damageGrid.get(gridIndex).setMargin(damageView.get(gridIndex).get(i), new javafx.geometry.Insets(0, DAMAGEVIEW_MAGRIN * scalePB, 0, 0));
                    damageGrid.get(gridIndex).setTranslateX(DAMAGEVIEW_TX * scalePB);
                }
                else {
                    damageGrid.get(gridIndex).setMargin(damageView.get(gridIndex).get(i), new javafx.geometry.Insets(0, DAMAGEVIEWFLIPPED_MAGRIN * scalePB, 0, 0));
                    damageGrid.get(gridIndex).setTranslateX(DAMAGEVIEWFLIPPED_TX * scalePB);
                }
                damageGrid.get(gridIndex).setTranslateY(DAMAGEGRID_TY*scalePB);
            }
            gridIndex++;
        }
        return damageGrid;
    }


    /**
     * Returns all the images of the damages, one for each color.
     *
     * @return   all the images of the damages, one for each color.
     */
    private List<Image> damageImageSelector(){
        String[] colors = {"green", "yellow", "grey", "purple", "blue"};
        List<Image> damageImage = new ArrayList<>();
        for(String c : colors) {
            damageImage.add(new Image(getClass().getResourceAsStream("/images/miscellaneous/" + c + "Blood.png")));
        }
        return damageImage;
    }


    /**
     *Associates to a color the index of its position in the list returned from damageImageSelector()
     *
     * @param color     the given color.
     * @return          index of the color in the list of colors.
     */
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


    /**
     * Configures the images of the marks of every player.
     *
     * @return a list where every element represents the marks of a player.
     */
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
                marksView.get(gridIndex).get(i).setFitHeight(MARKSVIEW_H*scalePB);
                marksView.get(gridIndex).get(i).setPreserveRatio(true);
                marksGrid.get(gridIndex).add(marksView.get(gridIndex).get(i),i,0);
                marksGrid.get(gridIndex).setTranslateX(MARKSVIEW_TX*scalePB);
            }
            gridIndex++;
        }
        return marksGrid;
    }


    /**
     * Configures the images of the skulls of every player
     *
     * @param deathsNumber  number of deaths of every player
     * @return a list where every element represents the skulls of a player.
     */
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
                skullView.get(gridIndex).get(i).setFitHeight(SKULLVIEW_H*scalePB);
                skullView.get(gridIndex).get(i).setPreserveRatio(true);
                skullGrid.get(gridIndex).add(skullView.get(gridIndex).get(i),i,0);
                if(p.isFlipped())
                    skullGrid.get(gridIndex).setTranslateX(SKULLGRID_TX*scalePB);
                else
                    skullGrid.get(gridIndex).setTranslateX(SKULLGRIDFLIPPED_TX*scalePB);
                skullGrid.get(gridIndex).setTranslateY(SKULLGRID_TY*scalePB);
            }
            gridIndex++;
        }
        return skullGrid;
    }


    /**
     * Configures the buttons that make visible the cards every player has in his hand.
     * Powerups of the opponents are not visible, but the number of them is.
     * There are graphic effects that indicates when a weapon is unloaded and which cards are clickable.
     *
     * @return      list of buttons that open the content of the player hands
     */
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
                weaponHandView.get(players.indexOf(p)).get(weapons.indexOf(w)).setFitHeight(WEAPONHANDVIEW_H*scalePB);
                weaponHandView.get(players.indexOf(p)).get(weapons.indexOf(w)).setPreserveRatio(true);
                loadUnload.get(players.indexOf(p)).add(new Label());
                if( ! w.isLoaded()) {
                    loadUnload.get(players.indexOf(p)).get(weapons.indexOf(w)).setText("SCARICA");
                    loadUnload.get(players.indexOf(p)).get(weapons.indexOf(w)).setTextFill(Color.web("#F8F8FF"));
                }
                loadUnload.get(players.indexOf(p)).get(weapons.indexOf(w)).setFont(new Font("Arial", LOADUNLOAD_FONT*scalePB));
                loadUnload.get(players.indexOf(p)).get(weapons.indexOf(w)).setTranslateY(LOADUNLOAD_TY*scalePB);
                weaponContainer.get(players.indexOf(p)).add(new Pane());
                weaponContainer.get(players.indexOf(p)).get(weapons.indexOf(w)).getChildren().addAll(weaponHandView.get(players.indexOf(p)).get(weapons.indexOf(w)),
                        loadUnload.get(players.indexOf(p)).get(weapons.indexOf(w)));
                if(renderInstruction.equals("Weapon") && p==clientModel.getCurrentPlayer()){
                    if(labelButton.contains(w.getName())) {
                        weaponContainer.get(players.indexOf(p)).get(weapons.indexOf(w)).getChildren().add(inputButtons.get(labelButton.indexOf(w.getName())));
                        inputButtons.get(labelButton.indexOf(w.getName())).setPrefWidth(WEAPONBUTTON_W * scalePB);
                        inputButtons.get(labelButton.indexOf(w.getName())).setPrefHeight(WEAPONBUTTON_H * scalePB);
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
            puBackHandView.get(puBackHandView.size()-1).setFitHeight(PUVIEW_H*scalePB);
            puBackHandView.get(puBackHandView.size()-1).setPreserveRatio(true);
            puHandNumber.add(new Label(Integer.toString(p.getCardNumber())));
            puHandNumber.get(players.indexOf(p)).setFont(new Font("Arial", PUHANDNUMBER_FONT*scalePB));
            puHandNumber.get(players.indexOf(p)).setTextFill(Color.web("#F8F8FF"));
            puHandNumber.get(players.indexOf(p)).setTranslateX(PUHANDNUMBER_TX);
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
                    puView.get(puView.size()-1).setFitHeight(PUVIEW_H*scalePB);
                    puView.get(puView.size()-1).setPreserveRatio(true);
                    puContainer.add(new Pane());
                    puContainer.get(puContainer.size()-1).getChildren().add(puView.get(puView.size()-1));
                    if(renderInstruction.equals("PowerUp")){
                        String labelPowerUp = clientModel.getColorPowerUpInHand().get(i);
                        labelPowerUp = labelPowerUp.substring(0, 1).toUpperCase() + labelPowerUp.substring(1);
                        labelPowerUp = labelPowerUp + " " + pu;
                        if(labelButton2.contains(labelPowerUp)) {
                            puContainer.get(puContainer.size() - 1).getChildren().add(inputButtons.get(labelButton2.indexOf(labelPowerUp)));
                            inputButtons.get(labelButton2.indexOf(labelPowerUp)).setPrefWidth(PUBUTTON_W*scalePB);
                            inputButtons.get(labelButton2.indexOf(labelPowerUp)).setPrefHeight(PUBUTTON_H*scalePB);
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
            handButton.get(players.indexOf(p)).setTranslateX(HANDBUTTON_TX * scalePB);
            handButton.get(players.indexOf(p)).setTranslateY(HANDBUTTON_TY * scalePB);
        }
        return handButton;
    }


    /**
     * Configures the images of the points every player has.
     *
     * @return a list where every element represents the points of a player.
     */
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
            one.setFitHeight(POINTVIEW_H*scalePB);
            one.setPreserveRatio(true);
        }
        for(ImageView two : point2View){
            two.setFitHeight(POINTVIEW_H*scalePB);
            two.setPreserveRatio(true);
        }
        for(ImageView four : point4View){
            four.setFitHeight(POINTVIEW_H*scalePB);
            four.setPreserveRatio(true);
        }

        pointsGrid.setTranslateX(POINTSGRID_TX*scalePB);
        pointsGrid.setTranslateY(POINTSGRID*scalePB);

        return pointsGrid;
    }

    /**
     * Configures a panel that shows the last 3 moves of the players
     *
     * @param message description of the last move
     * @return  pane containing the moves
     */
    public VBox historyRenderer(String message){
        messageHistory3=messageHistory2;
        messageHistory2=messageHistory1;
        messageHistory1=message;

        Label messageLabel1 = new Label(messageHistory1);
        Label messageLabel2 = new Label(messageHistory2);
        Label messageLabel3 = new Label(messageHistory3);
        Label divider1 = new Label(HISTORY_DIVIDER);
        Label divider2 = new Label(HISTORY_DIVIDER);
        messageLabel1.setAlignment(Pos.CENTER);
        messageLabel2.setAlignment(Pos.CENTER);
        messageLabel3.setAlignment(Pos.CENTER);
        divider1.setAlignment(Pos.CENTER);
        divider2.setAlignment(Pos.CENTER);
        messageLabel1.setTextFill(Color.web("#F8F8FF"));
        messageLabel2.setTextFill(Color.web("#F8F8FF"));
        messageLabel3.setTextFill(Color.web("#F8F8FF"));
        divider1.setTextFill(Color.web("#F8F8FF"));
        divider2.setTextFill(Color.web("#F8F8FF"));

        VBox historyPane= new VBox();
        historyPane.getChildren().addAll(divider1,messageLabel3,messageLabel2,messageLabel1,divider2);
        historyPane.setAlignment(Pos.CENTER);
        return historyPane;
    }
}
