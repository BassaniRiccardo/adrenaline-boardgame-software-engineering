package it.polimi.ingsw.view;

//TODO: implement

import java.awt.*;
import java.awt.ScrollPane;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;


import it.polimi.ingsw.model.board.Player;

import it.polimi.ingsw.model.board.Square;
import it.polimi.ingsw.model.cards.Weapon;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;



/**
 * Graphical user interface for I/O operations
 *
 * @author  BassaniRiccardo
 */

//TODO
// Implement render()
// Think about which methods to keep (a single get() would be sufficient, for instance...)
// Interface UI to check and modify
// No hardcode, keep in mind window dimension.
// png and jpeg files (card, other graphics) must be in resources (same as json)


public class GUI extends Application implements UI, Runnable, EventHandler {

    private ClientMain clientMain;
    private Color color = Color.rgb(new Random().nextInt(256),new Random().nextInt(256),new Random().nextInt(256));
    private Stage stage;
    DataSaver dataSaver = new DataSaver();
    private static final Logger LOGGER = Logger.getLogger("clientLogger");
    public static final CountDownLatch latch = new CountDownLatch(1);
    public static GUI GUI = null;
    private ClientModel clientModel;
    private VBox messagePanel;


    public ClientMain getClientMain() {
        return clientMain;
    }

    /**
     *
     * @return
     */
    public static GUI waitGUI() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return GUI;
    }

    /**
     *
     * @param GUI0
     */
    public static void setGUI(GUI GUI0) {
        GUI = GUI0;
        latch.countDown();
    }

    /**
     *
     */
    public GUI() {
        setGUI(this);
    }

    public void setClientMain(ClientMain clientMain) {
        this.clientMain = clientMain;
        this.clientModel=clientMain.getClientModel();}

    public void setClientModel(ClientModel cm) { this.clientModel=cm;}

    /*
    public static void main(String[] args){
        launch(args);
    }
    */

    /**
     *
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        Platform.runLater( () -> {

            //da cancellare
            stage = primaryStage;
            stage.setOnCloseRequest(e -> {System.exit(0);});
            stage.setTitle("Adrenaline");
            BorderPane pane = new BorderPane();
            pane.setBackground(new Background(new BackgroundFill(color, null, null)));
            Scene scene = new Scene(pane, 500, 250);
            stage.setScene(scene);
            Label label = new Label("Entering the configuration phase...");
            pane.setCenter(label);
            stage.show();

            /*
            lo stage deve essere fullScreen fin da qui e composto da:
            -   messagePanel, per mostrare i messaggi
            -   il resto dello schermo va riempito in modo appropriato fin da ora (immagine e/o scritta+colore)
                e andrá poi rimepito con la mappa dal render
             */

            /*
            Qui la parte con cui sostituire la parte sopra. Manca l'inizializzazione dello stage come sopra descritto

            stage = primaryStage;
            stage.setOnCloseRequest(e -> {System.exit(0);});
            stage.setTitle("Adrenaline");
            messagePanel.setBackground(new Background(new BackgroundFill(color, null, null)));
            Label label = new Label("Entering the configuration phase...");
            messagePanel.getChildren().add(label);
            */


        });
    }

    /*

    @Override
    public void render(){
        Platform.runLater( () -> {
                    try {
                        FileInputStream mapLeft;
                        FileInputStream mapRight;
                        mapLeft = new FileInputStream("src/main/resources/images/miscellaneous/mapLeft2.png");
                        mapRight = new FileInputStream("src/main/resources/images/miscellaneous/mapRight2.png");
                        Image imageMapLeft = new Image(mapLeft);
                        Image imageMapRight = new Image(mapRight);
                        ImageView viewMapLeft = new ImageView(imageMapLeft);
                        ImageView viewMapRight = new ImageView(imageMapRight);
                        viewMapLeft.setFitHeight(800);
                        viewMapRight.setFitHeight(800);
                        viewMapLeft.setPreserveRatio(true);
                        viewMapRight.setPreserveRatio(true);
                        List<ImageView> playerView = new ArrayList<>();
                        FileInputStream player = new FileInputStream("src/main/resources/images/miscellaneous/Banshee.png");
                        Image imagePlayer = new Image(player);
                        playerView.add(new ImageView(imagePlayer));
                        playerView.get(0).setFitWidth(500);
                        playerView.get(0).setPreserveRatio(true);
                        VBox playerBoard = new VBox();
                        playerBoard.getChildren().addAll(playerView);

                        List<ImageView> skulls = new ArrayList<>();
                        FileInputStream skullFile = new FileInputStream("src/main/resources/images/miscellaneous/skull.png");
                        int skullNumber=5;
                        Image skull = new Image(skullFile);
                        skulls.add(new ImageView(skull));
                        skulls.add(new ImageView(skull));
                        skulls.add(new ImageView(skull));
                        skulls.add(new ImageView(skull));
                        skulls.add(new ImageView(skull));

                        if(skullNumber>5){
                            skulls.add(new ImageView(skull));
                        }
                        if(skullNumber>6){
                            skulls.add(new ImageView(skull));
                        }
                        if(skullNumber>7){
                            skulls.add(new ImageView(skull));
                        }

                        for(ImageView s : skulls){
                            s.setFitWidth(44.5);
                            s.setPreserveRatio(true);
                        }
                        GridPane skullsGrid = new GridPane();
                        List<ColumnConstraints> columnConstraints = new ArrayList<>();
                        for(int i=0; i<8-skullNumber;i++){
                            columnConstraints.add(new ColumnConstraints(44.5));
                            skullsGrid.getColumnConstraints().add(columnConstraints.get(i));
                        }
                        for(int i=8-skullNumber; i<8;i++)
                            skullsGrid.add(skulls.get(i-(8-skullNumber)),i,0,1,1);

                        //weapons
                        FileInputStream weaponFile = new FileInputStream("src/main/resources/images/cards/Furnace.png");
                        Image weaponImage = new Image(weaponFile);

                        GridPane weaponsGrid1 = new GridPane();
                        GridPane weaponsGrid2 = new GridPane();
                        GridPane weaponsGrid3 = new GridPane();
                        List<ImageView> weaponsList1 = new ArrayList<>();
                        List<ImageView> weaponsList2 = new ArrayList<>();
                        List<ImageView> weaponsList3 = new ArrayList<>();
                        for(int i=0; i<3; i++){
                            //1 top spawning point
                            weaponsList1.add(new ImageView(weaponImage));
                            weaponsList1.get(i).setFitHeight(160);
                            weaponsList1.get(i).setPreserveRatio(true);
                            weaponsGrid1.add(weaponsList1.get(i),i,0,1,1);
                            weaponsGrid1.setMargin(weaponsList1.get(i),new javafx.geometry.Insets(0,0,0,19));
                            //2 right spawning point
                            weaponsList2.add(new ImageView(weaponImage));
                            weaponsList2.get(i).setFitHeight(160);
                            weaponsList2.get(i).setPreserveRatio(true);
                            weaponsGrid2.add(weaponsList2.get(i),i,0,1,1);
                            weaponsGrid2.setMargin(weaponsList2.get(i),new javafx.geometry.Insets(0,0,0,19));
                            //3 left spawning point
                            weaponsList3.add(new ImageView(weaponImage));
                            weaponsList3.get(i).setFitHeight(160);
                            weaponsList3.get(i).setPreserveRatio(true);
                            weaponsGrid3.add(weaponsList3.get(i),i,0,1,1);
                            weaponsGrid3.setMargin(weaponsList3.get(i),new javafx.geometry.Insets(0,0,0,19));
                        }

                        //rooms
                        int mapId = 1;

                        FileInputStream ammoBackFile = new FileInputStream("src/main/resources/images/ammo/ammoBack.png");
                        Image ammoBackImage = new Image(ammoBackFile);
                        GridPane roomsGrid = new GridPane();
                        List<ImageView> ammoBackList = new ArrayList<>();
                        int k=0;
                        columnConstraints.add(new ColumnConstraints(44.5));
                        ColumnConstraints emptyRoom1 = new ColumnConstraints(120);
                        ColumnConstraints emptyRoom2 = new ColumnConstraints(120);
                        if(mapId==1||mapId==3)
                            roomsGrid.getColumnConstraints().add(emptyRoom1);

                        for(int i=0;i<4;i++)
                            for(int j=0;j<3;j++){
                                ammoBackList.add(new ImageView(ammoBackImage));
                                ammoBackList.get(k).setFitWidth(65);
                                ammoBackList.get(k).setPreserveRatio(true);
                                roomsGrid.add(ammoBackList.get(k),i,j);
                                roomsGrid.setMargin(ammoBackList.get(k),new javafx.geometry.Insets(55,55,55,55));
                                k++;
                            }

                        //decks
                        FileInputStream pUDeckFile = new FileInputStream("src/main/resources/images/cards/pUBack.png");
                        Image pUDeckImage = new Image(pUDeckFile);
                        ImageView  pUDeckView = new ImageView(pUDeckImage);
                        pUDeckView.setFitHeight(110);
                        pUDeckView.setPreserveRatio(true);
                        Label cardsRemainingPU = new Label("10");
                        FileInputStream weaponDeckFile = new FileInputStream("src/main/resources/images/cards/wBack.png");
                        Image weaponDeckImage = new Image(weaponDeckFile);
                        ImageView weaponDeckView = new ImageView(weaponDeckImage);
                        weaponDeckView.setFitHeight(160);
                        weaponDeckView.setPreserveRatio(true);
                        Label cardsRemainingWeapons = new Label("10");

                        //ammos
                        GridPane playerAmmo = new GridPane();
                        FileInputStream redAmmoFile=new FileInputStream("src/main/resources/images/miscellaneous/redAmmo.png");
                        FileInputStream blueAmmoFile=new FileInputStream("src/main/resources/images/miscellaneous/blueAmmo.png");
                        FileInputStream yellowAmmoFile=new FileInputStream("src/main/resources/images/miscellaneous/yellowAmmo.png");
                        Image redAmmoImage=new Image(redAmmoFile);
                        List<ImageView> redAmmoView = new ArrayList<>();
                        Image blueAmmoImage=new Image(blueAmmoFile);
                        List<ImageView> blueAmmoView = new ArrayList<>();
                        Image yellowAmmoImage=new Image(yellowAmmoFile);
                        List<ImageView> yellowAmmoView = new ArrayList<>();
                        for(int i=0;i<3;i++){
                            redAmmoView.add(new ImageView(redAmmoImage));
                            redAmmoView.get(i).setFitWidth(20);
                            redAmmoView.get(i).setPreserveRatio(true);
                            playerAmmo.add(redAmmoView.get(i),i,0);
                            playerAmmo.setMargin(redAmmoView.get(i),new Insets(0,0,5,5));
                            blueAmmoView.add(new ImageView(blueAmmoImage));
                            blueAmmoView.get(i).setFitWidth(20);
                            blueAmmoView.get(i).setPreserveRatio(true);
                            playerAmmo.add(blueAmmoView.get(i),i,1);
                            playerAmmo.setMargin(blueAmmoView.get(i),new Insets(0,0,5,5));
                            yellowAmmoView.add(new ImageView(yellowAmmoImage));
                            yellowAmmoView.get(i).setFitWidth(20);
                            yellowAmmoView.get(i).setPreserveRatio(true);
                            playerAmmo.add(yellowAmmoView.get(i),i,2);
                            playerAmmo.setMargin(yellowAmmoView.get(i),new Insets(0,0,5,5));
                            }

                        //button hands
                        Button handButton = new Button("CARTE");
                        handButton.setOnAction(actionEvent ->  {
                            Stage handStage = new Stage();
                            handStage.setTitle("CARTE IN MANO");
                            BorderPane handPane = new BorderPane();
                        });
                        stage = primaryStage;
                        stage.setTitle("Adrenaline");
                        BorderPane pane = new BorderPane();
                        pane.setBackground(new Background(new BackgroundFill(color, null, null)));
                        Scene scene = new Scene(pane, 500, 160);
                        stage.setScene(scene);
                        Label label = new Label("Entering the configuration phase...");
                        pane.setCenter(label);

                        stage.show();

                        StackPane playerBoardAndStuffAbove = new StackPane();
                        playerBoardAndStuffAbove.getChildren().addAll(playerBoard,playerAmmo,handButton);
                        playerAmmo.setTranslateX(400);
                        HBox map =new HBox();
                        map.getChildren().addAll(viewMapLeft, viewMapRight);
                        StackPane mapAndStuffAbove = new StackPane();
                        mapAndStuffAbove.getChildren().addAll(map,skullsGrid,weaponsGrid1,weaponsGrid2,weaponsGrid3,roomsGrid,
                                weaponDeckView,pUDeckView,cardsRemainingPU,cardsRemainingWeapons);
                        pUDeckView.setTranslateX(453);
                        pUDeckView.setTranslateY(-332);
                        weaponDeckView.setTranslateX(440);
                        weaponDeckView.setTranslateY(-132);
                        cardsRemainingPU.setTranslateX(450);
                        cardsRemainingPU.setTranslateY(-330);
                        cardsRemainingPU.setTextFill(Color.web("#F8F8FF"));
                        cardsRemainingWeapons.setTranslateX(440);
                        cardsRemainingWeapons.setTranslateY(-130);
                        cardsRemainingWeapons.setTextFill(Color.web("#F8F8FF"));

                        HBox board = new HBox();
                        board.getChildren().addAll(mapAndStuffAbove, playerBoardAndStuffAbove);

                        skullsGrid.setTranslateX(70);
                        skullsGrid.setTranslateY(50);
                        weaponsGrid1.setTranslateX(540);
                        weaponsGrid2.setRotate(270);
                        weaponsGrid2.setTranslateX(800);
                        weaponsGrid2.setTranslateY(-160);
                        weaponsGrid3.setRotate(90);
                        weaponsGrid3.setTranslateX(-800);
                        weaponsGrid3.setTranslateY(370);
                        roomsGrid.setTranslateX(180);
                        roomsGrid.setTranslateY((200));
                        Scene sceneMap = new Scene(board, 1200, 800);
                        stage.setScene(sceneMap);
                        stage.setFullScreen(true);
                        stage.show();


                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
        });
    }

    */
    /**
     * Displays a simplified model containing all the information the user needs.
     */
    @Override
    public void render() {
        while (stage==null){
            try {
                Thread.sleep(2000);
            }catch (InterruptedException e){e.printStackTrace();}
        }

        Platform.runLater( () -> {
            try {

                clientModel = clientMain.getClientModel();
                FileInputStream mapLeft;
                FileInputStream mapRight;
                if(clientModel.getMapID()==1||clientModel.getMapID()==3){
                    mapLeft = new FileInputStream("src/main/resources/images/miscellaneous/mapLeft1.png");
                }else {
                    mapLeft = new FileInputStream("src/main/resources/images/miscellaneous/mapLeft2.png");
                }
                if(clientModel.getMapID()==3||clientModel.getMapID()==4){
                    mapRight = new FileInputStream("src/main/resources/images/miscellaneous/mapRight2.png");
                }else{
                    mapRight = new FileInputStream("src/main/resources/images/miscellaneous/mapRight1.png");}
                Image imageMapLeft = new Image(mapLeft);
                Image imageMapRight = new Image(mapRight);
                ImageView viewMapLeft = new ImageView(imageMapLeft);
                ImageView viewMapRight = new ImageView(imageMapRight);
                viewMapLeft.setFitHeight(800);
                viewMapRight.setFitHeight(800);
                viewMapLeft.setPreserveRatio(true);
                viewMapRight.setPreserveRatio(true);

                //rooms
                int mapId = clientModel.getMapID();
                List<ClientModel.SimpleSquare> squares = clientModel.getSquares();
                GridPane roomsGrid = new GridPane();
                ColumnConstraints emptyRoom1 = new ColumnConstraints(120);
                ColumnConstraints emptyRoom2 = new ColumnConstraints(120);
                ColumnConstraints emptyRoom3 = new ColumnConstraints(120);
                ColumnConstraints emptyRoom4 = new ColumnConstraints(120);
                ColumnConstraints emptyRoom5 = new ColumnConstraints(120);
                List<ImageView> ammoView = new ArrayList<>();
                int k=0;
                int column=0;
                int row=0;
                int spawningPoint=1;
                int ammoViewIndex=0;
                for(ClientModel.SimpleSquare s : squares) {
                    if ((mapId == 1 || mapId == 3) && column == 0 && row == 0) {
                        roomsGrid.getColumnConstraints().add(emptyRoom1);
                    }else if (!((clientModel.getSquares().get(k)).isSpawnPoint())) {
                        ammoView.add(getImageOfSquare(s));
                        roomsGrid.add(ammoView.get(ammoViewIndex), column, row);
                        ammoView.get(ammoViewIndex).setFitWidth(65);
                        ammoView.get(ammoViewIndex).setPreserveRatio(true);
                        roomsGrid.setMargin(ammoView.get(ammoViewIndex), new javafx.geometry.Insets(55, 55, 55, 55));
                        ammoViewIndex++;
                    }
                    else if (spawningPoint==1){
                        roomsGrid.getColumnConstraints().add(emptyRoom3);
                        spawningPoint++;
                    }
                    else if (spawningPoint==2){
                        roomsGrid.getColumnConstraints().add(emptyRoom4);
                        spawningPoint++;
                    }else
                        roomsGrid.getColumnConstraints().add(emptyRoom5);
                    if(column==3)
                        column = 0;
                    else
                        column++;
                    if(row==2)
                        row = 0;
                    else
                        row++;
                    k++;
                }
                if (mapId == 1 || mapId == 2)
                    roomsGrid.getColumnConstraints().add(emptyRoom2);

                //decks
                FileInputStream pUDeckFile = new FileInputStream("src/main/resources/images/cards/pUBack.png");
                Image pUDeckImage = new Image(pUDeckFile);
                ImageView pUDeckView = new ImageView(pUDeckImage);
                pUDeckView.setFitHeight(110);
                pUDeckView.setPreserveRatio(true);
                Label cardsRemainingPU = new Label(Integer.toString(clientModel.getPowerUpCardsLeft()));
                FileInputStream weaponDeckFile = new FileInputStream("src/main/resources/images/cards/wBack.png");
                Image weaponDeckImage = new Image(weaponDeckFile);
                ImageView weaponDeckView = new ImageView(weaponDeckImage);
                weaponDeckView.setFitHeight(160);
                weaponDeckView.setPreserveRatio(true);
                Label cardsRemainingWeapons = new Label(Integer.toString(clientModel.getWeaponCardsLeft()));

                //players
                List<ImageView> playerView = new ArrayList<>();
                List<ClientModel.SimplePlayer> players = clientModel.getPlayers();
                int playerIndex = 0;
                for (ClientModel.SimplePlayer p : players) {
                    playerView.add(getImageOfPlayer(p));
                    playerView.get(playerIndex).setFitWidth(500);
                    playerView.get(playerIndex).setPreserveRatio(true);
                    playerIndex++;
                }
                VBox playerBoard = new VBox();
                playerBoard.getChildren().addAll(playerView);

                //skulls
                List<ImageView> skulls = new ArrayList<>();
                FileInputStream skullFile = new FileInputStream("src/main/resources/images/miscellaneous/skull.png");
                int skullNumber=clientModel.getSkullsLeft();
                Image skullImage = new Image(skullFile);
                for(int i=0; i<skullNumber;i++)
                    skulls.add(new ImageView(skullImage));

                for(ImageView s : skulls){
                    s.setFitWidth(44.5);
                    s.setPreserveRatio(true);
                }
                GridPane skullsGrid = new GridPane();
                List<ColumnConstraints> columnConstraints = new ArrayList<>();
                for(int i=0; i<8-skullNumber;i++){
                    columnConstraints.add(new ColumnConstraints(44.5));
                    skullsGrid.getColumnConstraints().add(columnConstraints.get(i));
                }
                for(int i=8-skullNumber; i<8;i++)
                    skullsGrid.add(skulls.get(i-(8-skullNumber)),i,0,1,1);

                //ammos
                List<GridPane> playerAmmoGrid = new ArrayList<>();
                FileInputStream redAmmoFile=new FileInputStream("src/main/resources/images/miscellaneous/redAmmo.png");
                FileInputStream blueAmmoFile=new FileInputStream("src/main/resources/images/miscellaneous/blueAmmo.png");
                FileInputStream yellowAmmoFile=new FileInputStream("src/main/resources/images/miscellaneous/yellowAmmo.png");
                Image redAmmoImage=new Image(redAmmoFile);
                List<ArrayList<ImageView>> redAmmoView = new ArrayList<>(); //doppie liste
                Image blueAmmoImage=new Image(blueAmmoFile);
                List<ArrayList<ImageView>> blueAmmoView = new ArrayList<>();
                Image yellowAmmoImage=new Image(yellowAmmoFile);
                List<ArrayList<ImageView>> yellowAmmoView = new ArrayList<>();
                int gridIndex=0;
                for(ClientModel.SimplePlayer p : players) {
                    playerAmmoGrid.add(new GridPane());
                    redAmmoView.add(new ArrayList<>());
                    int rAmmo = p.getRedAmmo();
                    for (int i = 0; i < rAmmo; i++) {
                        redAmmoView.get(gridIndex).add(new ImageView(redAmmoImage));
                        redAmmoView.get(gridIndex).get(i).setFitWidth(20);
                        redAmmoView.get(gridIndex).get(i).setPreserveRatio(true);
                        playerAmmoGrid.get(gridIndex).add(redAmmoView.get(gridIndex).get(i), i, 0);
                        playerAmmoGrid.get(gridIndex).setMargin(redAmmoView.get(gridIndex).get(i),new Insets(0,0,5,5));
                    }
                    blueAmmoView.add(new ArrayList<>());
                    int bAmmo = p.getBlueAmmo();
                    for (int i = 0; i < bAmmo; i++) {
                        blueAmmoView.get(gridIndex).add(new ImageView(blueAmmoImage));
                        blueAmmoView.get(gridIndex).get(i).setFitWidth(20);
                        blueAmmoView.get(gridIndex).get(i).setPreserveRatio(true);
                        playerAmmoGrid.get(gridIndex).add(blueAmmoView.get(gridIndex).get(i), i, 0);
                        playerAmmoGrid.get(gridIndex).setMargin(blueAmmoView.get(gridIndex).get(i),new Insets(0,0,5,5));
                    }
                    yellowAmmoView.add(new ArrayList<>());
                    int yAmmo = p.getYellowAmmo();
                    for (int i = 0; i < yAmmo; i++) {
                        yellowAmmoView.get(gridIndex).add(new ImageView(yellowAmmoImage));
                        yellowAmmoView.get(gridIndex).get(i).setFitWidth(20);
                        yellowAmmoView.get(gridIndex).get(i).setPreserveRatio(true);
                        playerAmmoGrid.get(gridIndex).add(yellowAmmoView.get(gridIndex).get(i), i, 0);
                        playerAmmoGrid.get(gridIndex).setMargin(yellowAmmoView.get(gridIndex).get(i),new Insets(0,0,5,5));
                    }
                }

                //weapons
                FileInputStream weaponFile = new FileInputStream("src/main/resources/images/cards/Furnace.png");
                Image weaponImage = new Image(weaponFile);

                GridPane weaponsGrid1 = new GridPane();
                GridPane weaponsGrid2 = new GridPane();
                GridPane weaponsGrid3 = new GridPane();
                List<ImageView> weaponsList1 = new ArrayList<>();
                List<ImageView> weaponsList2 = new ArrayList<>();
                List<ImageView> weaponsList3 = new ArrayList<>();
                int spawnpointIndex=1;
                for(ClientModel.SimpleSquare s : squares){
                    if(s.isSpawnPoint()) {
                        if (spawnpointIndex == 1) {
                            for (int i = 0; i < 3; i++) {
                                weaponsList1.add(getImageOfWeaponsInSquare(s).get(i));
                                weaponsList1.get(i).setFitHeight(160);
                                weaponsList1.get(i).setPreserveRatio(true);
                                weaponsGrid1.add(weaponsList1.get(i),i,0,1,1);
                                weaponsGrid1.setMargin(weaponsList1.get(i),new javafx.geometry.Insets(0,0,0,19));
                            }
                        } else if (spawningPoint == 2) {
                            for (int i = 0; i < 3; i++) {
                                weaponsList2.add(getImageOfWeaponsInSquare(s).get(i));
                                weaponsList2.get(i).setFitHeight(160);
                                weaponsList2.get(i).setPreserveRatio(true);
                                weaponsGrid2.add(weaponsList2.get(i),i,0,1,1);
                                weaponsGrid2.setMargin(weaponsList2.get(i),new javafx.geometry.Insets(0,0,0,19));
                            }
                        }else{
                            for (int i = 0; i < 3; i++) {
                                weaponsList3.add(getImageOfWeaponsInSquare(s).get(i));
                                weaponsList3.get(i).setFitHeight(160);
                                weaponsList3.get(i).setPreserveRatio(true);
                                weaponsGrid3.add(weaponsList3.get(i),i,0,1,1);
                                weaponsGrid3.setMargin(weaponsList3.get(i),new javafx.geometry.Insets(0,0,0,19));
                            }
                        }
                    }
                }

                //layout
                StackPane playerBoardAndStuffAbove = new StackPane();
               // playerBoardAndStuffAbove.getChildren().addAll(playerBoard, playerAmmoGrid);
                for(GridPane g : playerAmmoGrid)
                    g.setTranslateX(400);
                HBox map =new HBox();
                map.getChildren().addAll(viewMapLeft, viewMapRight);
                StackPane mapAndStuffAbove = new StackPane();
                mapAndStuffAbove.getChildren().addAll(map,skullsGrid,weaponsGrid1,weaponsGrid2,weaponsGrid3,roomsGrid,
                        weaponDeckView,pUDeckView,cardsRemainingPU,cardsRemainingWeapons);
                pUDeckView.setTranslateX(453);
                pUDeckView.setTranslateY(-332);
                weaponDeckView.setTranslateX(440);
                weaponDeckView.setTranslateY(-132);
                cardsRemainingPU.setTranslateX(450);
                cardsRemainingPU.setTranslateY(-330);
                cardsRemainingPU.setTextFill(Color.web("#F8F8FF"));
                cardsRemainingWeapons.setTranslateX(440);
                cardsRemainingWeapons.setTranslateY(-130);
                cardsRemainingWeapons.setTextFill(Color.web("#F8F8FF"));

                HBox board = new HBox();
                board.getChildren().addAll(mapAndStuffAbove, playerBoardAndStuffAbove);

                skullsGrid.setTranslateX(70);
                skullsGrid.setTranslateY(50);
                weaponsGrid1.setTranslateX(540);
                weaponsGrid2.setRotate(270);
                weaponsGrid2.setTranslateX(800);
                weaponsGrid2.setTranslateY(-160);
                weaponsGrid3.setRotate(90);
                weaponsGrid3.setTranslateX(-800);
                weaponsGrid3.setTranslateY(370);
                roomsGrid.setTranslateX(180);
                roomsGrid.setTranslateY((200));
                Scene sceneMap = new Scene(board, 1200, 800);
                stage.setScene(sceneMap);
                stage.setFullScreen(true);
                stage.show();

                }catch (FileNotFoundException e){
                e.printStackTrace();
            }
        });
    }

    private List<ImageView> getImageOfWeaponsInSquare(ClientModel.SimpleSquare square){
        List<ImageView> weaponView = new ArrayList<>();
        int o=square.getId();
        List<ClientModel.SimpleWeapon> weaponList =(clientModel.getSquares().get(o)).getWeapons();
        try{
            for(ClientModel.SimpleWeapon w : weaponList){
                String key= w.getName();
                FileInputStream weaponFile = new FileInputStream("src/main/resources/images/miscellaneous/ammo"+key+".png");
                Image weaponImage = new Image(weaponFile);
                ImageView weaponImageView = new ImageView(weaponImage);
                weaponView.add(weaponImageView);
            }

            return weaponView;
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }

    private ImageView getImageOfSquare(ClientModel.SimpleSquare square){
        int r,b,y;
        boolean pU;
        String key="";
        int o=square.getId();
        r=(clientModel.getSquares().get(o)).getRedAmmo();
        b=(clientModel.getSquares().get(o)).getBlueAmmo();
        y=(clientModel.getSquares().get(o)).getYellowAmmo();
        pU=(clientModel.getSquares().get(o)).isPowerup();
        if(pU)
            key=key+"P";
        if(r==1)
            key=key+"R";
        if(r==2)
            key=key+"R";
        if(b==1)
            key=key+"B";
        if(b==2)
            key=key+"B";
        if(y==1)
            key=key+"Y";
        if(y==2)
            key=key+"Y";
        try{
            FileInputStream ammoFile = new FileInputStream("src/main/resources/images/ammo/ammo"+key+".png");
            Image ammoImage = new Image(ammoFile);
            ImageView ammoView = new ImageView(ammoImage);
            return ammoView;
        }catch (FileNotFoundException e){
        e.printStackTrace();
        }
        return null;
    }

    private ImageView getImageOfPlayer(ClientModel.SimplePlayer player){
        String key,color;
        color=player.getColor();
        if(color=="Green")
            key="Sprog";
        if(color=="Grey")
            key="Dozer";
        if(color=="Yellow")
            key="D_struct_or";
        if(color=="Blue")
            key="Banshee";
        else
            key="Violet";

        try{
            FileInputStream playerFile = new FileInputStream("src/main/resources/images/miscellaneous/"+key+".png");
            Image playerImage = new Image(playerFile);
            ImageView playerView = new ImageView(playerImage);
            return playerView;
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }
    /**
     * Displays a MSG message
     *
     * @param message   message to be displayed
     */
    public void display(String message) {

        while (stage==null){
            try {
                Thread.sleep(2000);
            }catch (InterruptedException e){e.printStackTrace();}
        }

        Platform.runLater( () -> {

            if      (message.contains("Banshee"))       this.color = Color.BLUE;
            else if (message.contains("Sprog"))         this.color = Color.GREEN;
            else if (message.contains("Violet"))        this.color = Color.PURPLE;
            else if (message.contains("Dozer"))         this.color = Color.GREY;
            else if (message.contains("D_struct_or"))   this.color = Color.YELLOW;

            Label label = new Label(message);
            VBox msg = new VBox();
            msg.setBackground(new Background(new BackgroundFill(color, null, null)));
            msg.getChildren().add(label);
            msg.setAlignment(Pos.CENTER);
            Scene scene = new Scene(msg, 500, 250, color);
            Stage msgStage = new Stage();

            if (message.contains("disconnected")){
                msgStage.setScene(scene);
                msgStage.show();
                Button close = new Button("ok");
                close.setAlignment(Pos.CENTER);
                msg.getChildren().add(close);
                msg.setAlignment(Pos.CENTER);
                msg.setSpacing(40);
                close.setOnAction(e -> msgStage.close());
            }

            else {

                //da aggiungere
                //messagePanel = msg;

                //da togliere
                stage.setScene(scene);
                stage.show();

            }

        });

    }


    /**
     * Displays a REQ message
     *
     * @param question       text to be displayed
     * @param maxLength      maximum length allowed for the answer
     */
    public void display(String question, String maxLength) {

        while (stage==null){
            try {
                Thread.sleep(2000);
            }catch (InterruptedException e){e.printStackTrace();}
        }

        Platform.runLater( () -> {

            VBox req = new VBox();
            req.setBackground(new Background(new BackgroundFill(color, null, null)));
            VBox quest = new VBox();
            quest.setSpacing(10);
            quest.setAlignment(Pos.CENTER);
            Label label1 = new Label(question);
            Label label2 = new Label("(max " + maxLength + " characters)");
            quest.getChildren().addAll(label1, label2);
            req.getChildren().add(quest);

            TextField textField = new TextField();
            textField.setAlignment(Pos.CENTER);
            textField.setMaxSize(200, 50);
            req.getChildren().add(textField);

            Button requestButton = new Button("confirm");
            requestButton.setAlignment(Pos.CENTER);
            req.getChildren().add(requestButton);
            req.setAlignment(Pos.CENTER);
            req.setSpacing(40);
            Stage reqStage = new Stage();

            requestButton.setOnAction(e ->
                    {
                        System.out.println("REQ: you clicked me!");
                        dataSaver.answer = (textField.getText());
                        dataSaver.message = question;
                        dataSaver.update = true;
                        reqStage.close();
                    }
            );

            //da aggiungere
            //messagePanel = req;

            //da togliere
            Scene info = new Scene(req, 500, 250, color);
            stage.setScene(info);
            stage.show();

        });

    }


    /**
     * Displays a OPT message

     * @param message   message to be displayed
     * @param list      the option the user can choose among
     */
    public void display(String message, List<String> list) {

        while (stage==null){
            try {
                Thread.sleep(2000);
            }catch (InterruptedException e){e.printStackTrace();}
        }

        Platform.runLater( () -> {

            VBox opt = new VBox();
            opt.setBackground(new Background(new BackgroundFill(color, null, null)));
            Label label = new Label(message);
            opt.getChildren().add(label);
            opt.setAlignment(Pos.CENTER);
            opt.setSpacing(40);

            HBox optionList = new HBox();
            optionList.setAlignment(Pos.CENTER);
            optionList.setSpacing(40 / list.size());
            Stage optStage = new Stage();

            List<Button> buttons = new ArrayList<>();
            for (String item : list) {
                Button b = new Button(item);
                buttons.add(b);
                b.setOnAction(e -> {
                    System.out.println("OPT " + (buttons.indexOf(b)+1) + ": you clicked me!");
                    dataSaver.message = message;
                    dataSaver.answer = Integer.toString(buttons.indexOf(b) + 1);
                    dataSaver.update = true;
                    optStage.close();
                });
            }
            optionList.getChildren().addAll(buttons);
            opt.getChildren().add(optionList);

            //da aggiungere
            //messagePanel = opt;

            //da togliere
            Scene scene = new Scene(opt, 500,250, color);
            stage.setScene(scene);

            stage.show();

        });

    }


    /**
     * Queries the user for input
     *
     * @param maxLength     the maximum length allowed for the answer
     * @return              the user's input
     */
    public String get(String maxLength) {

        while (!dataSaver.update){
            try {
                Thread.sleep(2000);
            }catch (InterruptedException e){e.printStackTrace();}
        }

        while (dataSaver.answer.length() > Integer.parseInt(maxLength)){
            dataSaver.update=false;
            display("Max length exceeded, retry: " + dataSaver.message, maxLength);
            get(maxLength);
        }
        dataSaver.update=false;

        display("Wait for your turn...");

        return dataSaver.answer;

    }


    /**
     * Queries the user for input
     *
     * @param list      the list of option to choose among
     * @return          the user's input
     */
    public String get(List<String> list) {

        /*boolean verified = false;
        for(int i = 1; i<=list.size(); i++) {
            if (String.valueOf(i).equals(dataSaver.answer)) {
                verified = true;
            }
        }
        while (!verified){
            display(dataSaver.message, list);
            for(int i = 1; i<=list.size(); i++) {
                if (String.valueOf(i).equals(dataSaver.answer)) {
                    verified = true;
                }
            }
        }
        */

        while (!dataSaver.update){
            try {
                Thread.sleep(2000);
            }catch (InterruptedException e){e.printStackTrace();}
        }

        dataSaver.update=false;

        display("Wait for your turn...");

        return dataSaver.answer;
    }

    /**
     * Main GUI loop
     */
    public  void run(){
    }


    /**
     * Handles complex events
     *
     * @param event
     */
    @Override
    public void handle(Event event) {

    }


    /**
     * Displays a simplified model containing all the information the user needs.
     */
    public void renderTest() {

            display("MAPPA DISEGNATA");
        //ClientModel cm = clientMain.getClientModel();
        System.out.println(clientMain.getClientModel().getSquares());
        System.out.println(clientMain.getClientModel().getPlayers());

    }


    /**
     * Class storing the values the get() method must return.
     */
    class DataSaver{
        String answer;
        String message;
        boolean update;
    }

    //to delete
    public String get(){
        System.out.println("questo metodo non va chiamato");
        return "";
    }


}