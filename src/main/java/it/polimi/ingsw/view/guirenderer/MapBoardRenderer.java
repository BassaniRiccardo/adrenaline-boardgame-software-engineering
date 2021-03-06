package it.polimi.ingsw.view.guirenderer;

import it.polimi.ingsw.view.ClientModel;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Class with methods used by render for rendering the map board.
 *
 * @author  davidealde
 */
public class MapBoardRenderer {

    private double scale;
    private ClientModel clientModel;
    private List<Button> inputButtons;
    private List<String> labelButton;

    private static final String SQUARE = "Square ";
    private static final String RESET = "Reset";
    private static final String NONE = "None";

    /**
     * The type of OPT message to display.
     */
    private String renderInstruction;

    /**
     * Whether there is the necessity to update the screen for multiple input that are managed as a single one from the client.
     */
    private boolean renderNeeded;
    private static final float MAPVIEW_H=800 ;
    private static final float EMPTYROOM_H_W=175 ;
    private static final float ROOMSGRID_TX=180 ;
    private static final float ROOMSGRID_TY=200 ;
    private static final float AMOOVIEW_H=65 ;
    private static final float AMMOVIEW_ALLMARGIN=55 ;
    private static final float SQUAREBUTTON_H_W=150 ;
    private static final float SQUAREBUTTON_TY=-20 ;
    private static final float SKULL_W=35 ;
    private static final float EMPTYSKULLSPACE_W=45 ;
    private static final float SKULL_MARGIN=10 ;
    private static final float SKULLSGRID_TX=70 ;
    private static final float SKULLSGRID_TY=45 ;
    private static final float ICONVIEW_H=80 ;
    private static final float ICONVIEW1_TX=45 ;
    private static final float ICONVIEW1_TY=-60 ;
    private static final float ICONVIEW2_TY=-60 ;
    private static final float ICONVIEW3_TX=90 ;
    private static final float ICONVIEW3_TY=-60 ;
    private static final float ICONVIEW0_TX=90 ;
    private static final float WEAPONGRID_MARGIN=19 ;
    private static final float WEAPONBUTTON_H=300 ;
    private static final float WEAPONBUTTON_W=180 ;
    private static final float WEAPONLISTZOOM_H=300 ;
    private static final float BUTTONWEAPONZOOM_H=160 ;
    private static final float BUTTONWEAPONZOOM_W=113 ;
    private static final float WEAPONGRID0_TX=540 ;
    private static final float WEAPONGRID1_TX=-98 ;
    private static final float WEAPONGRID1_ROTATE=90 ;
    private static final float WEAPONGRID1_TY=370 ;
    private static final float WEAPONGRID2_TX=270 ;
    private static final float WEAPONGRID2_TY=803 ;
    private static final float WEAPONGRID2_ROTATE=550 ;
    private static final float PUDECKVIEW_H=110 ;
    private static final float WEAPONDECKVIEW_H=160 ;
    private static final float PUDECKVIEW_TX=945 ;
    private static final float PUDECKVIEW_TY=45 ;
    private static final float WEAPONDECKVIEW_TX=919 ;
    private static final float WEAPONDECKVIEW_TY= 220;
    private static final float CARDSREMAININGPU_TX= 1000;
    private static final float CARDSREMAININGPU_TY= 45;
    private static final String CARDSREMAININGPU_COLOR= "#F8F8FF";
    private static final float CARDSREMAININGWEAPON_TX= 1000;
    private static final float CARDSREMAININGWEAPON_TY= 220;
    private static final String CARDSREMAININGWEAPON_COLOR= "#F8F8FF";
    private static final String SQUAREINPUTINSTRUCTION= "Square ";
    private static final String RESETBUTTONLABEL= "Reset";


    /**
     * Constructor.
     *
     * @param sc        the scale factor.
     * @param cm        the client model.
     */
    public MapBoardRenderer(double sc, ClientModel cm) {
        this.scale = sc;
        this.clientModel = cm;
        this.renderInstruction = "Normal";
        inputButtons = new ArrayList<>();
        labelButton = new ArrayList<>();
        renderNeeded = false;
    }


    public void setRenderNeeded(boolean renderNeeded) {
        this.renderNeeded = renderNeeded;
    }


    public boolean getRenderNeeded(){return renderNeeded;}


    public void setLabelButton(List<String> labelButton) {
        this.labelButton = labelButton;
    }


    public void setInputButtons(List<Button> inputButtons) {
        this.inputButtons = inputButtons;
    }


    public void setClientModel(ClientModel clientModel) {
        this.clientModel = clientModel;
    }

    public void setRenderInstruction(String renderInstruction) {
        this.renderInstruction = renderInstruction;
    }

    /**
     * Builds the map based on the game configuration.
     *
     * @return      a panel that contains the map.
     */
    public HBox mapRenderer() {

        InputStream mapLeft;
        InputStream mapRight;
        if (clientModel.getMapID() == 1 || clientModel.getMapID() == 3) {
            mapLeft = this.getClass().getResourceAsStream("/images/miscellaneous/mapLeft1.png");
        } else {
            mapLeft = this.getClass().getResourceAsStream("/images/miscellaneous/mapLeft2.png");
        }
        if (clientModel.getMapID() == 3 || clientModel.getMapID() == 4) {
            mapRight = this.getClass().getResourceAsStream("/images/miscellaneous/mapRight2.png");
        } else {
            mapRight = this.getClass().getResourceAsStream("/images/miscellaneous/mapRight1.png");
        }
        Image imageMapLeft = new Image(mapLeft);
        Image imageMapRight = new Image(mapRight);
        ImageView mapLeftView = new ImageView(imageMapLeft);
        ImageView mapRightView = new ImageView(imageMapRight);
        mapLeftView.setFitHeight(MAPVIEW_H * scale);
        mapRightView.setFitHeight(MAPVIEW_H * scale);


        mapLeftView.setPreserveRatio(true);
        mapRightView.setPreserveRatio(true);
        HBox map = new HBox();
        map.getChildren().addAll(mapLeftView, mapRightView);

        return map;
    }

    /**
     * Collocates in all the square the corresponding ammo and buttons.
     *
     * @return a GridPane representing the square, its ammo and its buttons.
     */
    public GridPane roomRenderer() {

        int mapId = clientModel.getMapID();
        List<ClientModel.SimpleSquare> squares = clientModel.getSquares();
        GridPane roomsGrid = new GridPane();
        Pane emptyRoom1 = new Pane();
        Pane emptyRoom2 = new Pane();
        Pane emptyRoom3 = new Pane();
        Pane emptyRoom4 = new Pane();
        Pane emptyRoom5 = new Pane();
        emptyRoom1.setMinSize(EMPTYROOM_H_W * scale, EMPTYROOM_H_W * scale);
        emptyRoom2.setMinSize(EMPTYROOM_H_W * scale, EMPTYROOM_H_W * scale);
        emptyRoom3.setMinSize(EMPTYROOM_H_W * scale, EMPTYROOM_H_W * scale);
        emptyRoom4.setMinSize(EMPTYROOM_H_W * scale, EMPTYROOM_H_W * scale);
        emptyRoom5.setMinSize(EMPTYROOM_H_W * scale, EMPTYROOM_H_W * scale);
        List<ImageView> ammoView = new ArrayList<>();
        int column = 0;
        int row = 0;
        int spawningPoint = 1;
        for (ClientModel.SimpleSquare s : squares) {
            if ((mapId == 1 || mapId == 2) && column == 3 && row == 0) {
                roomsGrid.add(emptyRoom1, column, row);
                row = 1;
                column = 0;
                roomsGrid.add(emptyRoom5, column, row);
                column++;
            } else if ((mapId == 1 || mapId == 3) && column == 0 && row == 2) {
                roomsGrid.add(emptyRoom2, column, row);
                column++;
                roomAmmoSquareBuilder(s, roomsGrid, ammoView, column, row);
                column++;
            } else {
                if (!s.isSpawnPoint()) {
                    roomAmmoSquareBuilder(s, roomsGrid, ammoView, column, row);
                } else if (spawningPoint == 1) {
                    roomsGrid.add(emptyRoom3, column, row);
                    spawningPoint++;
                } else if (spawningPoint == 2) {
                    roomsGrid.add(emptyRoom4, column, row);
                    spawningPoint++;
                }
                if (column == 3) {
                    column = 0;
                    row++;
                } else {
                    column++;
                }
            }
        }

    //buttons
        if (renderInstruction.equals("Square")) {
            column = 0;
            row = 0;
            spawningPoint = 1;
            int buttonIndex = 0;
            for (ClientModel.SimpleSquare s : squares) {
                if ((mapId == 1 || mapId == 2) && column == 3 && row == 0) {
                    row = 1;
                    column = 0;
                    addRoomButton(buttonIndex,roomsGrid,column,row,s);
                    column++;
                } else if ((mapId == 1 || mapId == 3) && column == 0 && row == 2) {
                    column++;
                    addRoomButton(buttonIndex,roomsGrid,column,row,s);
                    column++;
                } else {
                    if (!s.isSpawnPoint()) {
                        addRoomButton(buttonIndex,roomsGrid,column,row,s);
                    } else if (spawningPoint <= 3) {
                        addRoomButton(buttonIndex,roomsGrid,column,row,s);
                        spawningPoint++;
                    }
                    if (column == 3) {
                        column = 0;
                        row++;
                    } else {
                        column++;
                    }
                }
            }
        }

        roomsGrid.setTranslateX(ROOMSGRID_TX * scale);
        roomsGrid.setTranslateY((ROOMSGRID_TY * scale));
        return roomsGrid;
    }

    /**
     * Adds an ammo tile to a square.
     *
     * @param s             the square.
     * @param roomsGrid     the pane.
     * @param ammoView      the ammo view of the ammo.
     * @param column        the index of the column.
     * @param row           the index of the row.
     */
    private void roomAmmoSquareBuilder(ClientModel.SimpleSquare s, GridPane roomsGrid, List<ImageView> ammoView, int column, int row){
        ammoView.add(getImageOfSquare(s));
        roomsGrid.add(ammoView.get(ammoView.size() - 1), column, row);
        ammoView.get(ammoView.size() - 1).setFitHeight(AMOOVIEW_H * scale);
        ammoView.get(ammoView.size() - 1).setPreserveRatio(true);
        roomsGrid.setMargin(ammoView.get(ammoView.size() - 1), new javafx.geometry.Insets(AMMOVIEW_ALLMARGIN * scale,
                AMMOVIEW_ALLMARGIN * scale, AMMOVIEW_ALLMARGIN * scale, AMMOVIEW_ALLMARGIN * scale));
    }

    /**
     * Adds a button the a square.
     *
     * @param s             the square.
     * @param roomsGrid     the pane.
     * @param column        the index of the column.
     * @param row           the index of the row.
     */
    private void addRoomButton(int buttonIndex, GridPane roomsGrid, int column, int row, ClientModel.SimpleSquare s){

        if (buttonIndex < inputButtons.size() && (labelButton.contains(SQUAREINPUTINSTRUCTION + s.getId()))) {
                roomsGrid.add(inputButtons.get(labelButton.indexOf(SQUAREINPUTINSTRUCTION + s.getId())), column, row);
                inputButtons.get(labelButton.indexOf(SQUAREINPUTINSTRUCTION + s.getId())).setPrefSize(SQUAREBUTTON_H_W * scale, SQUAREBUTTON_H_W * scale);
                inputButtons.get(labelButton.indexOf(SQUAREINPUTINSTRUCTION + s.getId())).setTranslateY(SQUAREBUTTON_TY * scale);
                inputButtons.get(labelButton.indexOf(SQUAREINPUTINSTRUCTION + s.getId())).setStyle("-fx-background-color: rgb(200, 200, 200, 0.3)");
                buttonIndex++;
        }
    }

    /**
     * Takes from resources the right image of the ammo tile in the square.
     *
     * @param square    square of interest.
     * @return          the image view of the ammo tile.
     */
    private ImageView getImageOfSquare(ClientModel.SimpleSquare square) {

        int r;
        int b;
        int y;
        boolean pU;
        String k1 = "";
        String k2 = "";
        String k3 = "";
        String k4 = "";
        String k5 = "";
        String k6 = "";
        String k7 = "";

        r = square.getRedAmmo();
        b = square.getBlueAmmo();
        y = square.getYellowAmmo();
        pU = square.isPowerup();
        if (pU)
            k1 = "P";
        if (r >= 1)
            k2 = "R";
        if (r == 2)
            k3 = "R";
        if (b >= 1)
            k4 = "B";
        if (b == 2)
            k5 = "B";
        if (y >= 1)
            k6 = "Y";
        if (y == 2)
            k7 = "Y";
        InputStream ammoFile;
        if(r==0 && b==0 && y==0 && !pU)
            ammoFile = this.getClass().getResourceAsStream("/images/ammo/ammoBack.png");
        else
            ammoFile = this.getClass().getResourceAsStream("/images/ammo/ammo" + k1 + k2 + k3 + k4 + k5 + k6 + k7 + ".png");
        Image ammoImage = new Image(ammoFile);
        return new ImageView(ammoImage);
    }

    /**
     * Configures the killShotTrack.
     *
     * @param skullNumber   number of skulls on the killShotTrack.
     * @return              a GridPane with the skulls.
     */
    public GridPane killShotTrackRenderer(int skullNumber) {

        List<ImageView> skulls = new ArrayList<>();
        InputStream skullFile = this.getClass().getResourceAsStream("/images/miscellaneous/skull.png");
        Image skullImage = new Image(skullFile);
        for (int i = 0; i < skullNumber; i++)
            skulls.add(new ImageView(skullImage));

        for (ImageView s : skulls) {
            s.setFitWidth(SKULL_W * scale);
            s.setPreserveRatio(true);
        }
        GridPane skullsGrid = new GridPane();
        List<ColumnConstraints> columnConstraints = new ArrayList<>();
        for (int i = 0; i < 8 - skullNumber; i++) {
            columnConstraints.add(new ColumnConstraints(EMPTYSKULLSPACE_W * scale));
            skullsGrid.getColumnConstraints().add(columnConstraints.get(i));
        }
        for (int i = 8 - skullNumber; i < 8; i++) {
            skullsGrid.add(skulls.get(i - (8 - skullNumber)), i, 0, 1, 1);
            skullsGrid.setMargin(skulls.get(i - (8 - skullNumber)), new javafx.geometry.Insets(0, SKULL_MARGIN * scale,0, 0));
        }

        skullsGrid.setTranslateX(SKULLSGRID_TX * scale);
        skullsGrid.setTranslateY(SKULLSGRID_TY * scale);
        return skullsGrid;

    }

    /**
     * Returns the imageViews of the icons of the players and collocates them in a precise position in the square to avoid overlapping.
     * Makes some icons clickable if there is a graphic input on the players.
     *
     * @return all the images with some visual proprieties.
     */
    public List<ImageView> iconsRenderer() {

        List<ImageView> iconView = new ArrayList<>();
        List<ClientModel.SimplePlayer> players = clientModel.getPlayers();
        Image iconImage;
        String color;
        List<String>splittedList = new ArrayList<>();
        if (renderInstruction.equals("Player"))
            splittedList=splitStringsOfLabelButton();
        for (ClientModel.SimplePlayer p : players) {
            color = p.getColor();
            iconImage = new Image(getClass().getResourceAsStream("/images/miscellaneous/" + color + "Hero.png"));
            iconView.add(new ImageView(iconImage));
            iconView.get(iconView.size() - 1).setFitHeight(ICONVIEW_H * scale);
            iconView.get(iconView.size() - 1).setPreserveRatio(true);
            if (renderInstruction.equals("Player")) {
                if (splittedList.contains(p.getUsername()))
                    iconView.get(iconView.size() - 1).setOnMouseClicked((MouseEvent e) ->
                            targetListBuilder(p.getUsername()));
                else
                    iconView.get(iconView.size() - 1).setOpacity(0.4);
            }
        }

        for (ClientModel.SimplePlayer p : players){
            switch (players.indexOf(p)){
                case (1): //the first one of the list is ok in the default position
                    iconView.get(1).setTranslateX(ICONVIEW1_TX*scale);
                    iconView.get(1).setTranslateY(ICONVIEW1_TY*scale);
                    break;
                case(2):
                    iconView.get(2).setTranslateY(ICONVIEW2_TY*scale);
                    break;
                case(3):
                    iconView.get(3).setTranslateX(ICONVIEW3_TX*scale);
                    iconView.get(3).setTranslateY(ICONVIEW3_TY*scale);
                    break;
                case(0):
                    break;
                    default:
                        iconView.get(4).setTranslateX(ICONVIEW0_TX*scale);

            }
        }

        return iconView;
    }

    /**
     * Called from iconRenderer() when a player icon is clicked.
     * It eliminates the inputButtons that don't contain the selected player until only the
     * right button remains and it activates it.
     *
     * @param userName      user name of the clicked player.
     */
    private void targetListBuilder(String userName){
        List<String> labelButtonFake = new ArrayList<>(labelButton);
        for(String label : labelButtonFake) {
            List<String> splittedList = splitString(label);
            if (!splittedList.contains(userName)) {
                inputButtons.remove(labelButtonFake.indexOf(label));
                labelButton.remove(label);
                renderNeeded = true;
            }
        }
        if(inputButtons.size()==1)
            inputButtons.get(0).fire();
    }

    /**
     * Divides a labelButton in all the user names that are contained in it and add them to a list.
     *
     * @param label     label that has to be divided.
     * @return          list of user names.
     */
    private List<String> splitString(String label){
        List<String> splittedList = new ArrayList<>();
        List<StringBuilder> name = new ArrayList<>();
        name.add(new StringBuilder());
        for (int letterIndex = 0; letterIndex < label.length(); letterIndex++) {
            if (label.charAt(letterIndex) != ',') {
                name.get(name.size() - 1).append(label.charAt(letterIndex));
            }
            else {
                splittedList.add(name.get(name.size() - 1).toString());
                name.add(new StringBuilder());
                letterIndex++; //the space next the comma
            }
        }
        splittedList.add(name.get(name.size() - 1).toString());
        return splittedList;
    }

    /**
     * Divides all the elements of labelButton in all the user names that are contained in them and add them in a list.
     *
     * @return      list of all the user names of the players that can be clicked.
     */
    private List<String> splitStringsOfLabelButton() {
        List<String>splittedList = new ArrayList<>();
        StringBuilder name;

        if(labelButton.contains(RESETBUTTONLABEL)) {
            inputButtons.remove(labelButton.indexOf(RESETBUTTONLABEL));
            labelButton.remove(RESETBUTTONLABEL);
        }
        if(labelButton.contains(NONE)) {
            inputButtons.remove(labelButton.indexOf(NONE));
            labelButton.remove(NONE);
        }
        for (String label: labelButton) {
            name = new StringBuilder();
            for (int letterIndex = 0; letterIndex < labelButton.get(labelButton.indexOf(label)).length(); letterIndex++) {
                if (labelButton.get(labelButton.indexOf(label)).charAt(letterIndex) != ',') {
                    name.append(labelButton.get(labelButton.indexOf(label)).charAt(letterIndex));
                }
                else {
                    splittedList.add(name.toString());
                    name = new StringBuilder();
                    letterIndex++; //the space next the comma
                }
            }

            splittedList.add(name.toString());
        }
        return splittedList;
    }

    /**
     * Finds the index of the column of a square.
     *
     * @param square    the square of interest.
     * @return          index of the column.
     */
    public int columnFinder(ClientModel.SimpleSquare square) {
        if (clientModel.getMapID() == 4) {
            return square.getId() % 4;
        }
        if (clientModel.getMapID() == 3) {
            if (square.getId() <= 7)
                return square.getId() % 4;
            else
                return (square.getId() + 1) % 4;
        }
        if (clientModel.getMapID() == 2) {
            if (square.getId() <= 2)
                return square.getId() % 4;
            else
                return (square.getId() + 1) % 4;
        } else { //MAP1
            if (square.getId() <= 2)
                return square.getId() % 4;
            else if (square.getId() <= 6)
                return (square.getId() + 1) % 4;
            else
                return (square.getId() + 2) % 4;
        }
    }

    /**
     * Finds the index of the row of a square.
     *
     * @param square    the square of interest.
     * @return          index of the row.
     */
    public int rowFinder(ClientModel.SimpleSquare square) {
        if (clientModel.getMapID() == 4 || clientModel.getMapID() == 3) {
            return square.getId() / 4;
        } else if (square.getId() < 2)
            return 0;
        else
            return (square.getId() + 1) / 4;
    }

    /**
     * Configures the three sets of weapon images of the spawning points.
     *
     * @return      three panes containing the images of the weapons of the respective spawning points.
     */
    public List<GridPane> weaponRenderer() {
        List<GridPane> weaponGrid = new ArrayList<>();
        List<Image> weaponImage;
        List<ArrayList<ImageView>> weaponList = new ArrayList<>();
        List<ArrayList<ImageView>> weaponListZoom = new ArrayList<>();
        List<ArrayList<MenuItem>> itemWeaponZoom = new ArrayList<>();
        List<ArrayList<MenuButton>> buttonWeaponZoom = new ArrayList<>();
        List<Pane> weaponContainer = new ArrayList<>();

        List<ClientModel.SimpleSquare> squares = clientModel.getSquares();
        for(ClientModel.SimpleSquare s : squares) {
            if (s.isSpawnPoint()) {

                weaponGrid.add(new GridPane());
                weaponImage = getImageOfWeaponsInSquare(s);
                weaponList.add(new ArrayList<>());
                weaponListZoom.add(new ArrayList<>());
                for(Image img : weaponImage) {
                    weaponList.get(weaponList.size()-1).add(new ImageView(weaponImage.get(weaponImage.indexOf(img))));
                    weaponListZoom.get(weaponListZoom.size()-1).add(new ImageView(weaponImage.get(weaponImage.indexOf(img))));
                }
                itemWeaponZoom.add(new ArrayList<>());
                buttonWeaponZoom.add(new ArrayList<>());

                for (int i = 0; i < 3; i++) {
                    weaponList.get(weaponList.size() - 1).get(i).setFitHeight(160 * scale);
                    weaponList.get(weaponList.size() - 1).get(i).setPreserveRatio(true);
                    weaponGrid.get(weaponGrid.size() - 1).add(weaponList.get(weaponList.size() - 1).get(i), i, 0, 1, 1);
                    weaponGrid.get(weaponGrid.size() - 1).setMargin(weaponList.get(weaponList.size() - 1).get(i), new javafx.geometry.Insets(0, 0, 0, WEAPONGRID_MARGIN * scale));
                    itemWeaponZoom.get(itemWeaponZoom.size() - 1).add(new MenuItem());
                    if(renderInstruction.equals("Weapon")){
                        if(labelButton.get(0).equals(s.getWeapons().get(0).getName())){
                            weaponContainer.add(new Pane());
                            weaponContainer.get(i).getChildren().addAll(weaponListZoom.get(weaponListZoom.size() - 1).get(i),inputButtons.get(i));
                            inputButtons.get(i).setPrefHeight(WEAPONBUTTON_H*scale);
                            inputButtons.get(i).setPrefWidth(WEAPONBUTTON_W*scale);
                            inputButtons.get(i).setStyle("-fx-background-color: transparent;");
                            itemWeaponZoom.get(itemWeaponZoom.size() - 1).get(i).setGraphic(weaponContainer.get(i));
                        }else
                            itemWeaponZoom.get(itemWeaponZoom.size() - 1).get(i).setGraphic(weaponListZoom.get(weaponListZoom.size() - 1).get(i));
                    }else
                        itemWeaponZoom.get(itemWeaponZoom.size() - 1).get(i).setGraphic(weaponListZoom.get(weaponListZoom.size() - 1).get(i));
                    buttonWeaponZoom.get(buttonWeaponZoom.size() - 1).add(new MenuButton(" ", null, itemWeaponZoom.get(itemWeaponZoom.size() - 1).get(i)));
                    weaponGrid.get(weaponGrid.size() - 1).add(buttonWeaponZoom.get(buttonWeaponZoom.size() - 1).get(i), i, 0, 1, 1);
                    buttonWeaponZoom.get(buttonWeaponZoom.size() - 1).get(i).setStyle("-fx-background-color: transparent;");
                    weaponListZoom.get(weaponListZoom.size() - 1).get(i).setFitHeight(WEAPONLISTZOOM_H*scale);
                    weaponListZoom.get(weaponListZoom.size() - 1).get(i).setPreserveRatio(true);
                    buttonWeaponZoom.get(buttonWeaponZoom.size() - 1).get(i).setPrefHeight(BUTTONWEAPONZOOM_H * scale);
                    buttonWeaponZoom.get(buttonWeaponZoom.size() - 1).get(i).setPrefWidth(BUTTONWEAPONZOOM_W * scale);
                }
            }
        }
        weaponGrid.get(0).setTranslateX(WEAPONGRID0_TX*scale); //grid(0) is the top one, grid(1) the left one and grid(2) the right one
        weaponGrid.get(1).setRotate(WEAPONGRID1_ROTATE);
        weaponGrid.get(1).setTranslateX(WEAPONGRID1_TX*scale);
        weaponGrid.get(1).setTranslateY(WEAPONGRID1_TY*scale);
        weaponGrid.get(2).setRotate(WEAPONGRID2_TX);
        weaponGrid.get(2).setTranslateX(WEAPONGRID2_TY*scale);
        weaponGrid.get(2).setTranslateY(WEAPONGRID2_ROTATE*scale);

        return weaponGrid;
    }

    /**
     * Returns the list of images of the weapons in a determined spawning point.
     *
     * @param square    the square (the spawning point).
     * @return          the list of images of the weapons.
     */
    private List<Image> getImageOfWeaponsInSquare(ClientModel.SimpleSquare square){
            ArrayList<Image> weaponView = new ArrayList<>();
            int index=square.getId();
            List<ClientModel.SimpleWeapon> weaponList =(clientModel.getSquares().get(index)).getWeapons();
            for(ClientModel.SimpleWeapon w : weaponList){
                String key= w.getName();
                InputStream weaponFile = this.getClass().getResourceAsStream("/images/cards/"+key.replace(" ","_")+".png");
                Image weaponImage = new Image(weaponFile);
                weaponView.add(weaponImage);
            }
            while (weaponView.size()<3){
                InputStream weaponFile = this.getClass().getResourceAsStream("/images/cards/wBack.png");
                Image weaponImage = new Image(weaponFile);
                weaponView.add(weaponImage);
            }

            return weaponView;
    }

    /**
     * Puts in the pane of the board the powerups deck and the weapons deck with the relative
     * numbers of remaining cards.
     *
     * @param mapAndStuffAbove      the board pane.
     * @return                      the board pane including the decks.
     */
    public Pane deckRenderer(Pane mapAndStuffAbove){
            InputStream pUDeckFile = this.getClass().getResourceAsStream("/images/cards/pUBack.png");
            Image pUDeckImage = new Image(pUDeckFile);
            ImageView pUDeckView = new ImageView(pUDeckImage);
            pUDeckView.setFitHeight(PUDECKVIEW_H*scale);
            pUDeckView.setPreserveRatio(true);
            Label cardsRemainingPU = new Label(Integer.toString(clientModel.getPowerUpCardsLeft()));
            InputStream weaponDeckFile = this.getClass().getResourceAsStream("/images/cards/wBack.png");
            Image weaponDeckImage = new Image(weaponDeckFile);
            ImageView weaponDeckView = new ImageView(weaponDeckImage);
            weaponDeckView.setFitHeight(WEAPONDECKVIEW_H*scale);
            weaponDeckView.setPreserveRatio(true);
            Label cardsRemainingWeapons = new Label(Integer.toString(clientModel.getWeaponCardsLeft()));
            mapAndStuffAbove.getChildren().addAll(weaponDeckView,pUDeckView,cardsRemainingPU,cardsRemainingWeapons);
            pUDeckView.setTranslateX(PUDECKVIEW_TX*scale);
            pUDeckView.setTranslateY(PUDECKVIEW_TY*scale);
            weaponDeckView.setTranslateX(WEAPONDECKVIEW_TX*scale);
            weaponDeckView.setTranslateY(WEAPONDECKVIEW_TY*scale);
            cardsRemainingPU.setTranslateX(CARDSREMAININGPU_TX*scale);
            cardsRemainingPU.setTranslateY(CARDSREMAININGPU_TY*scale);
            cardsRemainingPU.setTextFill(Color.web(CARDSREMAININGPU_COLOR));
            cardsRemainingWeapons.setTranslateX(CARDSREMAININGWEAPON_TX*scale);
            cardsRemainingWeapons.setTranslateY(CARDSREMAININGWEAPON_TY*scale);
            cardsRemainingWeapons.setTextFill(Color.web(CARDSREMAININGWEAPON_COLOR));
            return mapAndStuffAbove;
        }


    }
