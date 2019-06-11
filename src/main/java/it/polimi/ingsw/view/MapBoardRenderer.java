package it.polimi.ingsw.view;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Class with methods used by render for rendering the map board
 *
 * @author  davidealde
 */
public class MapBoardRenderer {

    private double scale;
    private ClientModel clientModel;
    private String renderInstruction;
    private List<Button> inputButtons;
    private List<String> labelButton;


    public MapBoardRenderer(double sc, ClientModel cm) {
        this.scale = sc;
        this.clientModel = cm;
        this.renderInstruction = null;
        inputButtons = new ArrayList<>();
        labelButton = new ArrayList<>();
    }

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
        mapLeftView.setFitHeight(800 * scale);
        mapRightView.setFitHeight(800 * scale);


        mapLeftView.setPreserveRatio(true);
        mapRightView.setPreserveRatio(true);
        HBox map = new HBox();
        map.getChildren().addAll(mapLeftView, mapRightView);

        return map;
    }

    public GridPane roomRenderer() {
        int mapId = clientModel.getMapID();
        List<ClientModel.SimpleSquare> squares = clientModel.getSquares();
        GridPane roomsGrid = new GridPane();
        Pane emptyRoom1 = new Pane();
        Pane emptyRoom2 = new Pane();
        Pane emptyRoom3 = new Pane();
        Pane emptyRoom4 = new Pane();
        Pane emptyRoom5 = new Pane();
        List<Button> squareButton = new ArrayList<>();
        emptyRoom1.setMinSize(175 * scale, 175 * scale);
        emptyRoom2.setMinSize(175 * scale, 175 * scale);
        emptyRoom3.setMinSize(175 * scale, 175 * scale);
        emptyRoom4.setMinSize(175 * scale, 175 * scale);
        emptyRoom5.setMinSize(175 * scale, 175 * scale);
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
                ammoView.add(getImageOfSquare(s));
                roomsGrid.add(ammoView.get(ammoView.size() - 1), column, row);
                ammoView.get(ammoView.size() - 1).setFitHeight(65 * scale);
                ammoView.get(ammoView.size() - 1).setPreserveRatio(true);
                roomsGrid.setMargin(ammoView.get(ammoView.size() - 1), new javafx.geometry.Insets(55 * scale, 55 * scale, 55 * scale, 55 * scale));
                column++;
            } else {
                if (!s.isSpawnPoint()) {
                    ammoView.add(getImageOfSquare(s));
                    roomsGrid.add(ammoView.get(ammoView.size() - 1), column, row);
                    ammoView.get(ammoView.size() - 1).setFitHeight(65 * scale);
                    ammoView.get(ammoView.size() - 1).setPreserveRatio(true);
                    roomsGrid.setMargin(ammoView.get(ammoView.size() - 1), new javafx.geometry.Insets(55 * scale, 55 * scale, 55 * scale, 55 * scale));
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

        //aggiunta bottoni
        if (renderInstruction == "Square") {
            System.out.println("sono squaremode");

            System.out.println(squares);
            column = 0;
            row = 0;
            spawningPoint = 1;
            int buttonIndex = 0;

            System.out.println(labelButton);
            System.out.println(inputButtons);
            for (ClientModel.SimpleSquare s : squares) {
                if ((mapId == 1 || mapId == 2) && column == 3 && row == 0) {
                    row = 1;
                    column = 0;
                    System.out.println(s.getId());
                    if (buttonIndex < inputButtons.size()) {
                        System.out.println(labelButton.get(buttonIndex));
                        System.out.println(inputButtons.get(buttonIndex).getText());
                    }
                    if (buttonIndex < inputButtons.size())
                        if (("Square " + s.getId()).equals(inputButtons.get(buttonIndex).getText())) {
                            System.out.println("STO FACENDO GIUSTO1");
                            roomsGrid.add(inputButtons.get(buttonIndex), column, row);
                            inputButtons.get(buttonIndex).setPrefSize(150 * scale, 150 * scale);
                            inputButtons.get(buttonIndex).setTranslateY(-20 * scale);
                            // inputButtons.get(buttonIndex).setStyle("-fx-background-color: transparent;");
                            buttonIndex++;
                        }
                    column++;
                } else if ((mapId == 1 || mapId == 3) && column == 0 && row == 2) {
                    column++;
                    System.out.println(s.getId());
                    if (buttonIndex < inputButtons.size()) {
                        System.out.println(labelButton.get(buttonIndex));
                        System.out.println(inputButtons.get(buttonIndex).getText());
                    }
                    if (buttonIndex < inputButtons.size())
                        if (("Square " + s.getId()).equals(inputButtons.get(buttonIndex).getText())) {
                            System.out.println("STO FACENDO GIUSTO2");
                            roomsGrid.add(inputButtons.get(buttonIndex), column, row);
                            inputButtons.get(buttonIndex).setPrefSize(150 * scale, 150 * scale);
                            inputButtons.get(buttonIndex).setTranslateY(-20 * scale);
                            // inputButtons.get(buttonIndex).setStyle("-fx-background-color: transparent;");
                            buttonIndex++;
                        }
                    column++;
                } else {
                    if (!s.isSpawnPoint()) {
                        System.out.println(s.getId());
                        if (buttonIndex < inputButtons.size()) {
                            System.out.println(labelButton.get(buttonIndex));
                            System.out.println(inputButtons.get(buttonIndex).getText());
                        }
                        if (buttonIndex < inputButtons.size())
                            if (("Square " + s.getId()).equals(inputButtons.get(buttonIndex).getText())) {
                                System.out.println("STO FACENDO GIUSTO3");
                                roomsGrid.add(inputButtons.get(buttonIndex), column, row);
                                inputButtons.get(buttonIndex).setPrefSize(150 * scale, 150 * scale);
                                inputButtons.get(buttonIndex).setTranslateY(-20 * scale);
                                // inputButtons.get(buttonIndex).setStyle("-fx-background-color: transparent;");
                                buttonIndex++;
                            }
                    } else if (spawningPoint == 1) {
                        System.out.println(s.getId());
                        if (buttonIndex < inputButtons.size()) {
                            System.out.println(labelButton.get(buttonIndex));
                            System.out.println(inputButtons.get(buttonIndex).getText());
                        }
                        if (buttonIndex < inputButtons.size())
                            if (("Square " + s.getId()).equals(inputButtons.get(buttonIndex).getText())) {
                                System.out.println("STO FACENDO GIUSTO4");
                                roomsGrid.add(inputButtons.get(buttonIndex), column, row);
                                inputButtons.get(buttonIndex).setPrefSize(150 * scale, 150 * scale);
                                inputButtons.get(buttonIndex).setTranslateY(-20 * scale);
                                // inputButtons.get(buttonIndex).setStyle("-fx-background-color: transparent;");
                                buttonIndex++;
                            }
                        spawningPoint++;
                    } else if (spawningPoint == 2) {
                        System.out.println(s.getId());
                        if (buttonIndex < inputButtons.size()) {
                            System.out.println(labelButton.get(buttonIndex));
                            System.out.println(inputButtons.get(buttonIndex).getText());
                        }
                        if (buttonIndex < inputButtons.size())
                            if (("Square " + s.getId()).equals(inputButtons.get(buttonIndex).getText())) {
                                System.out.println("STO FACENDO GIUSTO5");
                                roomsGrid.add(inputButtons.get(buttonIndex), column, row);
                                inputButtons.get(buttonIndex).setPrefSize(150 * scale, 150 * scale);
                                inputButtons.get(buttonIndex).setTranslateY(-20 * scale);
                                // inputButtons.get(buttonIndex).setStyle("-fx-background-color: transparent;");
                                buttonIndex++;
                            }
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

        renderInstruction = null;
        roomsGrid.setTranslateX(180 * scale);
        roomsGrid.setTranslateY((200 * scale));
        return roomsGrid;
    }

    public ImageView getImageOfSquare(ClientModel.SimpleSquare square) {
        int r = 0;
        int b = 0;
        int y = 0;
        boolean pU = false;
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
        //try{
        InputStream ammoFile;
        if(r==0 && b==0 && y==0 && !pU)
            ammoFile = this.getClass().getResourceAsStream("/images/ammo/ammoBack.png");
        else
            ammoFile = this.getClass().getResourceAsStream("/images/ammo/ammo" + k1 + k2 + k3 + k4 + k5 + k6 + k7 + ".png");
        Image ammoImage = new Image(ammoFile);
        ImageView ammoView = new ImageView(ammoImage);
        return ammoView;
        //}catch (FileNotFoundException e){
        //    e.printStackTrace();
        //}
        //return null;
    }

    public GridPane killShotTrackRender(int skullNumber) {
        List<ImageView> skulls = new ArrayList<>();
        InputStream skullFile = this.getClass().getResourceAsStream("/images/miscellaneous/skull.png");

        Image skullImage = new Image(skullFile);
        for (int i = 0; i < skullNumber; i++)
            skulls.add(new ImageView(skullImage));

        for (ImageView s : skulls) {
            s.setFitWidth(44.5 * scale);
            s.setPreserveRatio(true);
        }
        GridPane skullsGrid = new GridPane();
        List<ColumnConstraints> columnConstraints = new ArrayList<>();
        for (int i = 0; i < 8 - skullNumber; i++) {
            columnConstraints.add(new ColumnConstraints(44.5 * scale));
            skullsGrid.getColumnConstraints().add(columnConstraints.get(i));
        }
        for (int i = 8 - skullNumber; i < 8; i++)
            skullsGrid.add(skulls.get(i - (8 - skullNumber)), i, 0, 1, 1);

        skullsGrid.setTranslateX(70 * scale);
        skullsGrid.setTranslateY(50 * scale);
        return skullsGrid;

    }

    public List<ImageView> iconsRenderer() {
        List<ImageView> iconView = new ArrayList<>();
        List<ClientModel.SimplePlayer> players = clientModel.getPlayers();
        Image iconImage;
        String color;
        for (ClientModel.SimplePlayer p : players) {
            color = p.getColor();
            iconImage = new Image(getClass().getResourceAsStream("/images/miscellaneous/" + color + "Hero.png"));
            iconView.add(new ImageView(iconImage));
            iconView.get(iconView.size() - 1).setFitHeight(80 * scale);
            iconView.get(iconView.size() - 1).setPreserveRatio(true);

        }

        return iconView;
    }

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
        } else {
            if (square.getId() <= 2)
                return square.getId() % 4;
            else if (square.getId() <= 6)
                return (square.getId() + 1) % 4;
            else
                return (square.getId() + 2) % 4;
        }
    }

    public int rowFinder(ClientModel.SimpleSquare square) {
        if (clientModel.getMapID() == 4 || clientModel.getMapID() == 3) {
            return square.getId() / 4;
        } else if (square.getId() < 2)
            return 0;
        else
            return (square.getId() + 1) / 4;
    }

    public List<GridPane> weaponRenderer() {
        List<GridPane> weaponGrid = new ArrayList<>();
        List<ArrayList<ImageView>> weaponList = new ArrayList<>();
        List<ArrayList<ImageView>> weaponListZoom = new ArrayList<>();
        List<ArrayList<MenuItem>> itemWeaponZoom = new ArrayList<>();
        List<ArrayList<MenuButton>> buttonWeaponZoom = new ArrayList<>();

        List<ClientModel.SimpleSquare> squares = clientModel.getSquares();
        for(ClientModel.SimpleSquare s : squares) {
            if (s.isSpawnPoint()) {

                weaponGrid.add(new GridPane());
                weaponList.add(new ArrayList<>());
                weaponListZoom.add(new ArrayList<>());
                itemWeaponZoom.add(new ArrayList<>());
                buttonWeaponZoom.add(new ArrayList<>());

                for (int i = 0; i < 3; i++) {
                    weaponList.get(weaponList.size() - 1).add(getImageOfWeaponsInSquare(s).get(i));
                    weaponList.get(weaponList.size() - 1).get(i).setFitHeight(160 * scale);
                    weaponList.get(weaponList.size() - 1).get(i).setPreserveRatio(true);
                    weaponGrid.get(weaponGrid.size() - 1).add(weaponList.get(weaponList.size() - 1).get(i), i, 0, 1, 1);
                    weaponGrid.get(weaponGrid.size() - 1).setMargin(weaponList.get(weaponList.size() - 1).get(i), new javafx.geometry.Insets(0, 0, 0, 19 * scale));
                    weaponListZoom.get(weaponListZoom.size() - 1).add(getImageOfWeaponsInSquare(s).get(i));
                    itemWeaponZoom.get(itemWeaponZoom.size() - 1).add(new MenuItem());
                    itemWeaponZoom.get(itemWeaponZoom.size() - 1).get(i).setGraphic(weaponListZoom.get(weaponListZoom.size() - 1).get(i));
                    buttonWeaponZoom.get(buttonWeaponZoom.size() - 1).add(new MenuButton(" ", null, itemWeaponZoom.get(itemWeaponZoom.size() - 1).get(i)));
                    weaponGrid.get(weaponGrid.size() - 1).add(buttonWeaponZoom.get(buttonWeaponZoom.size() - 1).get(i), i, 0, 1, 1);
                    buttonWeaponZoom.get(buttonWeaponZoom.size() - 1).get(i).setStyle("-fx-background-color: transparent;");
                    buttonWeaponZoom.get(buttonWeaponZoom.size() - 1).get(i).setPrefHeight(160 * scale);
                    buttonWeaponZoom.get(buttonWeaponZoom.size() - 1).get(i).setPrefWidth(113 * scale);
                }
            }
        }
        weaponGrid.get(0).setTranslateX(540*scale);
        weaponGrid.get(1).setRotate(90);
        weaponGrid.get(1).setTranslateX(-98*scale);
        weaponGrid.get(1).setTranslateY(380*scale);
        weaponGrid.get(2).setRotate(270);
        weaponGrid.get(2).setTranslateX(803*scale);
        weaponGrid.get(2).setTranslateY(550*scale);

        return weaponGrid;
    }

        public List<ImageView> getImageOfWeaponsInSquare(ClientModel.SimpleSquare square){
            List<ImageView> weaponView = new ArrayList<>();
            int index=square.getId();
            List<ClientModel.SimpleWeapon> weaponList =(clientModel.getSquares().get(index)).getWeapons();
            //try{
            for(ClientModel.SimpleWeapon w : weaponList){
                String key= w.getName();
                InputStream weaponFile = this.getClass().getResourceAsStream("/images/cards/"+key.replace(" ","_")+".png");
                Image weaponImage = new Image(weaponFile);
                ImageView weaponImageView = new ImageView(weaponImage);
                weaponView.add(weaponImageView);
            }
            while (weaponView.size()<3){
                InputStream weaponFile = this.getClass().getResourceAsStream("/images/cards/wBack.png");
                Image weaponImage = new Image(weaponFile);
                ImageView weaponImageView = new ImageView(weaponImage);
                weaponView.add(weaponImageView);
                System.out.println("backOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOo");
            }

            return weaponView;
            //}catch (FileNotFoundException e){
            //    e.printStackTrace();
            //}
            //return null;
        }

        public Pane deckRenderer(Pane mapAndStuffAbove){
            InputStream pUDeckFile = this.getClass().getResourceAsStream("/images/cards/pUBack.png");
            Image pUDeckImage = new Image(pUDeckFile);
            ImageView pUDeckView = new ImageView(pUDeckImage);
            pUDeckView.setFitHeight(110*scale);
            pUDeckView.setPreserveRatio(true);
            Label cardsRemainingPU = new Label(Integer.toString(clientModel.getPowerUpCardsLeft()));
            InputStream weaponDeckFile = this.getClass().getResourceAsStream("/images/cards/wBack.png");
            Image weaponDeckImage = new Image(weaponDeckFile);
            ImageView weaponDeckView = new ImageView(weaponDeckImage);
            weaponDeckView.setFitHeight(160*scale);
            weaponDeckView.setPreserveRatio(true);
            Label cardsRemainingWeapons = new Label(Integer.toString(clientModel.getWeaponCardsLeft()));
            mapAndStuffAbove.getChildren().addAll(weaponDeckView,pUDeckView,cardsRemainingPU,cardsRemainingWeapons);
            pUDeckView.setTranslateX(945*scale);
            pUDeckView.setTranslateY(45*scale);
            weaponDeckView.setTranslateX(919*scale);
            weaponDeckView.setTranslateY(220*scale);
            cardsRemainingPU.setTranslateX(1000*scale);
            cardsRemainingPU.setTranslateY(45*scale);
            cardsRemainingPU.setTextFill(Color.web("#F8F8FF"));
            cardsRemainingWeapons.setTranslateX(1000*scale);
            cardsRemainingWeapons.setTranslateY(220*scale);
            cardsRemainingWeapons.setTextFill(Color.web("#F8F8FF"));
            return mapAndStuffAbove;
        }



    }

/*  Pane  emptyRoom2 = new Pane();
        Pane  emptyRoom3 = new Pane();
        Pane  emptyRoom4 = new Pane();
        Pane  emptyRoom5 = new Pane();
        List<Button> squareButton = new ArrayList<>();
        emptyRoom1.setMinSize(175*scale, 175*scale);
        emptyRoom2.setMinSize(175*scale, 175*scale);
        emptyRoom3.setMinSize(175*scale, 175*scale);
        emptyRoom4.setMinSize(175*scale, 175*scale);
        emptyRoom5.setMinSize(175*scale, 175*scale);
        List<ImageView> ammoView = new ArrayList<>();
        int column=0;
        int row=0;
        int spawningPoint=1;
        int buttonIndex=0;
        for(ClientModel.SimpleSquare s : squares) {
            if ((mapId == 1 || mapId == 2) && column == 3 && row == 0) {
                roomsGrid.add(emptyRoom1, column, row);
                row = 1;
                column = 0;
                roomsGrid.add(emptyRoom5,column,row);
                if(("Square "+Integer.toString(s.getId())).equals(inputButtons.get(buttonIndex).getText())){
                squareButton.add(new Button());
                roomsGrid.add(squareButton.get(squareButton.size()-1), column, row);
                squareButton.get(squareButton.size()-1).setPrefSize(150*scale,150*scale);
                squareButton.get(squareButton.size()-1).setTranslateY(-20*scale);
                squareButton.get(squareButton.size()-1).setStyle("-fx-background-color: transparent;");
                column++;
            } else if((mapId == 1 || mapId == 3) && column == 0 && row == 2){
                roomsGrid.add(emptyRoom2,column,row);
                column++;
                ammoView.add(getImageOfSquare(s));
                roomsGrid.add(ammoView.get(ammoView.size()-1), column, row);
                ammoView.get(ammoView.size()-1).setFitHeight(65*scale);
                ammoView.get(ammoView.size()-1).setPreserveRatio(true);
                roomsGrid.setMargin(ammoView.get(ammoView.size()-1), new javafx.geometry.Insets(55*scale, 55*scale, 55*scale, 55*scale));
                squareButton.add(new Button());
                roomsGrid.add(squareButton.get(squareButton.size()-1), column, row);
                squareButton.get(squareButton.size()-1).setPrefSize(150,150);
                squareButton.get(squareButton.size()-1).setTranslateY(-20);
                squareButton.get(squareButton.size()-1).setStyle("-fx-background-color: transparent;");
                column++;
            }else{
                if (!s.isSpawnPoint()) {
                    ammoView.add(getImageOfSquare(s));
                    roomsGrid.add(ammoView.get(ammoView.size()-1), column, row);
                    ammoView.get(ammoView.size()-1).setFitHeight(65*scale);
                    ammoView.get(ammoView.size()-1).setPreserveRatio(true);
                    roomsGrid.setMargin(ammoView.get(ammoView.size()-1), new javafx.geometry.Insets(55*scale, 55*scale, 55*scale, 55*scale));
                    squareButton.add(new Button());
                    roomsGrid.add(squareButton.get(squareButton.size()-1), column, row);
                    squareButton.get(squareButton.size()-1).setPrefSize(150,150);
                    squareButton.get(squareButton.size()-1).setTranslateY(-20);
                    squareButton.get(squareButton.size()-1).setStyle("-fx-background-color: transparent;");
                }
                else if (spawningPoint==1){
                    roomsGrid.add(emptyRoom3,column,row);
                    squareButton.add(new Button());
                    roomsGrid.add(squareButton.get(squareButton.size()-1), column, row);
                    squareButton.get(squareButton.size()-1).setPrefSize(150,150);
                    squareButton.get(squareButton.size()-1).setTranslateY(-20);
                    squareButton.get(squareButton.size()-1).setStyle("-fx-background-color: transparent;");
                    spawningPoint++;
                }
                else if (spawningPoint==2){
                    roomsGrid.add(emptyRoom4,column,row);
                    squareButton.add(new Button());
                    roomsGrid.add(squareButton.get(squareButton.size()-1), column, row);
                    squareButton.get(squareButton.size()-1).setPrefSize(150,150);
                    squareButton.get(squareButton.size()-1).setTranslateY(-20);
                    squareButton.get(squareButton.size()-1).setStyle("-fx-background-color: transparent;");
                    spawningPoint++;
                }
                if(column==3) {
                    column = 0;
                    row++;
                }else{
                    column++;}
            }
        }
        roomsGrid.setTranslateX(180*scale);
        roomsGrid.setTranslateY((200*scale));
        return roomsGrid;
    }*/