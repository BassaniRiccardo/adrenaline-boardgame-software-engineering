package it.polimi.ingsw.view;

//TODO: implement

import java.awt.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.event.*;
import javafx.scene.control.*;
import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.scene.control.*;


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
    private final int developerWidthResolution=1536;
    private final int developerHeightResolution=864;
    private final double developerPlayerBoardWidth = 486;
    double scalePB;
    double userPlayerBoardWidth;


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

           /* stage = primaryStage;
            stage.setOnCloseRequest(e -> {System.exit(0);});
            stage.setTitle("Adrenaline");
            BorderPane pane = new BorderPane();
            pane.setBackground(new Background(new BackgroundFill(color, null, null)));
            Scene scene = new Scene(pane, 500, 250);
            stage.setScene(scene);
            Label label = new Label("Entering the configuration phase...");
            pane.setCenter(label);
            stage.show();*/

/*
            lo stage deve essere fullScreen fin da qui e composto da:
            -   messagePanel, per mostrare i messaggi
            -   il resto dello schermo va riempito in modo appropriato fin da ora (immagine e/o scritta+colore)
                e andrá poi rimepito con la mappa dal render
             */


            //Qui la parte con cui sostituire la parte sopra. Manca l'inizializzazione dello stage come sopra descritto

            stage = primaryStage;
            stage.setOnCloseRequest(e -> {System.exit(0);});
            stage.setTitle("Adrenaline");
            HBox messagePanel = new HBox();
            messagePanel.setBackground(new Background(new BackgroundFill(color, null, null)));
            Label label = new Label("Entering the configuration phase...");
            messagePanel.getChildren().add(label);
            messagePanel.setAlignment(Pos.CENTER_RIGHT);
            Image welcomeImage = new Image(getClass().getResourceAsStream("/images/miscellaneous/welcome.jpg"));
            ImageView welcomeView = new ImageView(welcomeImage);
            BorderPane pane = new BorderPane();
            pane.getChildren().addAll(welcomeView);
            pane.setBottom(messagePanel);
            pane.setStyle("-fx-background-color: #000000");
            Scene scene = new Scene(pane, 500, 250);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();
        });
    }

   /* @Override
    public void render(){
        Platform.runLater( () -> {
                    try {
                        Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
                        double wScale = screenSize.getWidth() / widthDef;
                        double hScale = screenSize.getHeight() / heightDef;
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
                        Stage popupStage = new Stage();
                        Popup popup = new Popup();
                        popup.setX(300);
                        popup.setY(200);
                        Button hide = new Button("CHIUDI");
                        Button show = new Button("CARTE");
                        popup.getContent().addAll(new Circle(25, 25, 50, Color.AQUAMARINE),hide);
                        show.setOnAction(new EventHandler<ActionEvent>() {
                            @Override public void handle(ActionEvent event) {
                                popup.show(popupStage);
                            }
                        });

                        hide.setOnAction(new EventHandler<ActionEvent>() {
                            @Override public void handle(ActionEvent event) {
                                popup.hide();
                            }
                        });

                        StackPane playerBoardAndStuffAbove = new StackPane();
                        playerBoardAndStuffAbove.getChildren().addAll(playerBoard,playerAmmo,show);
                        playerAmmo.setTranslateX(400);
                        show.setTranslateX(190);
                        show.setTranslateY(-330);
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
                        Scene sceneMap = new Scene(board, 1920, 1080);
                        stage.setScene(sceneMap);
                        stage.setFullScreen(true);
                        stage.show();


                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
        });

    }*/

    /**
     * Displays a simplified model containing all the information the user needs.
     */
    @Override

    public void render() {

        Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        double userWidthResolution = screenSize.getWidth();
        double userHeightResolution = screenSize.getHeight();
        double scale,fakeScale;
        if(userWidthResolution/userHeightResolution>=developerWidthResolution/developerHeightResolution)
            fakeScale = userHeightResolution/developerHeightResolution;
        else
            fakeScale = userWidthResolution/developerWidthResolution;
        if(1050*fakeScale>userWidthResolution*3/4)
            fakeScale = userWidthResolution*3/(4*developerWidthResolution); //sets at 3/4 of the screen
        scale=fakeScale; //needed for lamba necessities
        System.out.println(screenSize.getWidth());
        System.out.println(screenSize.getHeight());
        System.out.println(scale);
        while (stage==null){
            try {
                Thread.sleep(2000);
            }catch (InterruptedException e){e.printStackTrace();}
        }

        Platform.runLater( () -> {
            //try {

                clientModel = clientMain.getClientModel();
                InputStream mapLeft;
                InputStream mapRight;
                if(clientModel.getMapID()==1||clientModel.getMapID()==3){
                    mapLeft = this.getClass().getResourceAsStream("/images/miscellaneous/mapLeft1.png");
                }else {
                    mapLeft = this.getClass().getResourceAsStream("/images/miscellaneous/mapLeft2.png");
                }
                if(clientModel.getMapID()==3||clientModel.getMapID()==4){
                    mapRight = this.getClass().getResourceAsStream("/images/miscellaneous/mapRight2.png");
                }else{
                    mapRight = this.getClass().getResourceAsStream("/images/miscellaneous/mapRight1.png");}
                Image imageMapLeft = new Image(mapLeft);
                Image imageMapRight = new Image(mapRight);
                ImageView mapLeftView = new ImageView(imageMapLeft);
                ImageView mapRightView = new ImageView(imageMapRight);
                mapLeftView.setFitHeight(800*scale);
                mapRightView.setFitHeight(800*scale);


            mapLeftView.setPreserveRatio(true);
                mapRightView.setPreserveRatio(true);
            HBox map =new HBox();
            map.getChildren().addAll(mapLeftView, mapRightView);

                //rooms
                int mapId = clientModel.getMapID();
                List<ClientModel.SimpleSquare> squares = clientModel.getSquares();
                GridPane roomsGrid = new GridPane();
                Pane  emptyRoom1 = new Pane();
                Pane  emptyRoom2 = new Pane();
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

                for(ClientModel.SimpleSquare s : squares) {
                    if ((mapId == 1 || mapId == 2) && column == 3 && row == 0) {
                        roomsGrid.add(emptyRoom1, column, row);
                        row = 1;
                        column = 0;
                        roomsGrid.add(emptyRoom5,column,row);
                        squareButton.add(new Button());
                        roomsGrid.add(squareButton.get(squareButton.size()-1), column, row);
                        squareButton.get(squareButton.size()-1).setPrefSize(150,150);
                        squareButton.get(squareButton.size()-1).setTranslateY(-20);
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



                //players
            List<Pane> playerBoards = new ArrayList<>(); //every element will contain the image of the player board and all the tokens and the cards of it

            List<ImageView> playerView = new ArrayList<>();
                List<ClientModel.SimplePlayer> players = clientModel.getPlayers();
                int playerIndex = 0;
                for (ClientModel.SimplePlayer p : players) {
                    playerBoards.add(new Pane());
                    playerView.add(getImageOfPlayer(p));
                    playerView.get(playerIndex).setFitWidth(userWidthResolution-1050);
                   // playerView.get(playerIndex).maxWidth(userWidthResolution-1100);
                    playerView.get(playerIndex).fitWidthProperty().bind(playerBoards.get(playerIndex).minWidthProperty());
                    playerView.get(playerIndex).fitWidthProperty().bind(playerBoards.get(playerIndex).maxWidthProperty());
                    playerView.get(playerIndex).setPreserveRatio(true);
                    playerIndex++;
                }

                for(int i=0; i<players.size(); i++) {
                    playerBoards.add(new Pane());
                    playerBoards.get(i).getChildren().add(playerView.get(i));
                    playerBoards.get(i).setMaxWidth(userWidthResolution-1050*scale); //minimum
                    playerBoards.get(i).setMinWidth(userWidthResolution/4);
                }

            userPlayerBoardWidth = playerView.get(0).getFitWidth();
            scalePB= userPlayerBoardWidth/developerPlayerBoardWidth;
            GUIRenderer guiRenderer = new GUIRenderer(clientModel, scalePB);

            //ammo
            List<GridPane> playerAmmoGrid = guiRenderer.ammoRender(players, scalePB);
            //damages
            List<GridPane> damageGrid = guiRenderer.damagesRenderer(players, scalePB);
            //marks
            List<GridPane> marksGrid = guiRenderer.marksRenderer(players, scalePB);
            //skullsPlayer
            int deathsNumber[] = new int[5];
            for(ClientModel.SimplePlayer p : players){
                deathsNumber[players.indexOf(p)] = Collections.frequency(clientModel.getKillShotTrack(),p);
                System.out.println(deathsNumber[players.indexOf(p)]);}
            List<GridPane> skullGrid = guiRenderer.skullsPlayerRenderer(players, scalePB, deathsNumber);

            for(int i=0; i<players.size(); i++) {
                playerBoards.add(new StackPane());
                playerBoards.get(i).getChildren().addAll(playerAmmoGrid.get(i),damageGrid.get(i),marksGrid.get(i),skullGrid.get(i));
            }

            //skullsKillShotTrack
            int skullNumber=clientModel.getSkullsLeft();
            GridPane skullsGrid = guiRenderer.killShotTrackRender(skullNumber, scale);

            //example popup
            /*Image damageImage = new Image(getClass().getResourceAsStream("/images/miscellaneous/blood.png"));
            ImageView testView = new ImageView(damageImage);
            MenuItem wizPopup = new MenuItem();
            wizPopup.setGraphic(testView);
            MenuButton popupButton = new MenuButton("frobozz",null, wizPopup);*/
            //weapons
               GridPane weaponsGrid1 = new GridPane();
                GridPane weaponsGrid2 = new GridPane();
                GridPane weaponsGrid3 = new GridPane();
                List<ImageView> weaponsList1 = new ArrayList<>();
                List<ImageView> weaponsList2 = new ArrayList<>();
                List<ImageView> weaponsList3 = new ArrayList<>();
            List<ImageView> weaponsList1zoom = new ArrayList<>();   //popups
            List<ImageView> weaponsList2zoom = new ArrayList<>();
            List<ImageView> weaponsList3zoom = new ArrayList<>();
            List<MenuItem> itemWeaponZoom1 = new ArrayList<>();
            List<MenuItem> itemWeaponZoom2 = new ArrayList<>();
            List<MenuItem> itemWeaponZoom3 = new ArrayList<>();
            List<MenuButton> buttonWeaponZoom1 = new ArrayList<>();
            List<MenuButton> buttonWeaponZoom2 = new ArrayList<>();
            List<MenuButton> buttonWeaponZoom3 = new ArrayList<>();

            int spawnPointIndex=1;
                for(ClientModel.SimpleSquare s : squares){
                    if(s.isSpawnPoint()) {
                        if (spawnPointIndex == 1) {
                            for (int i = 0; i < 3; i++) {
                                weaponsList1.add(getImageOfWeaponsInSquare(s).get(i));
                                weaponsList1.get(i).setFitHeight(160*scale);
                                weaponsList1.get(i).setPreserveRatio(true);
                                weaponsGrid1.add(weaponsList1.get(i),i,0,1,1);
                                weaponsGrid1.setMargin(weaponsList1.get(i),new javafx.geometry.Insets(0,0,0,19*scale));
                                weaponsList1zoom.add(getImageOfWeaponsInSquare(s).get(i));
                                itemWeaponZoom1.add(new MenuItem());
                                itemWeaponZoom1.get(i).setGraphic(weaponsList1zoom.get(i));
                                buttonWeaponZoom1.add(new MenuButton(" ",null,itemWeaponZoom1.get(i)));
                                weaponsGrid1.add(buttonWeaponZoom1.get(i),i,0,1,1);
                                buttonWeaponZoom1.get(i).setStyle("-fx-background-color: transparent;");
                                buttonWeaponZoom1.get(i).setPrefHeight(160*scale);
                                buttonWeaponZoom1.get(i).setPrefWidth(113*scale);
                            }spawnPointIndex++;
                        } else if (spawnPointIndex == 2) {
                            for (int i = 0; i < 3; i++) {
                                weaponsList2.add(getImageOfWeaponsInSquare(s).get(i));
                                weaponsList2.get(i).setFitHeight(160*scale);
                                weaponsList2.get(i).setPreserveRatio(true);
                                weaponsGrid2.add(weaponsList2.get(i),i,0,1,1);
                                weaponsGrid2.setMargin(weaponsList2.get(i),new javafx.geometry.Insets(0,0,0,19*scale));
                                weaponsList2zoom.add(getImageOfWeaponsInSquare(s).get(i));
                                itemWeaponZoom2.add(new MenuItem());
                                itemWeaponZoom2.get(i).setGraphic(weaponsList2zoom.get(i));
                                buttonWeaponZoom2.add(new MenuButton(" ",null,itemWeaponZoom2.get(i)));
                                weaponsGrid2.add(buttonWeaponZoom2.get(i),i,0,1,1);
                                buttonWeaponZoom2.get(i).setStyle("-fx-background-color: transparent;");
                                buttonWeaponZoom2.get(i).setPrefHeight(160*scale);
                                buttonWeaponZoom2.get(i).setPrefWidth(113*scale);                            }spawnPointIndex++;
                        }else{
                            for (int i = 0; i < 3; i++) {
                                weaponsList3.add(getImageOfWeaponsInSquare(s).get(i));
                                weaponsList3.get(i).setFitHeight(160*scale);
                                weaponsList3.get(i).setPreserveRatio(true);
                                weaponsGrid3.add(weaponsList3.get(i),i,0,1,1);
                                weaponsGrid3.setMargin(weaponsList3.get(i),new javafx.geometry.Insets(0,0,0,19*scale));
                                weaponsList3zoom.add(getImageOfWeaponsInSquare(s).get(i));
                                itemWeaponZoom3.add(new MenuItem());
                                itemWeaponZoom3.get(i).setGraphic(weaponsList3zoom.get(i));
                                buttonWeaponZoom3.add(new MenuButton(" ",null,itemWeaponZoom3.get(i)));
                                weaponsGrid3.add(buttonWeaponZoom3.get(i),i,0,1,1);
                                buttonWeaponZoom3.get(i).setStyle("-fx-background-color: transparent;");
                                buttonWeaponZoom3.get(i).setPrefHeight(160*scale);
                                buttonWeaponZoom3.get(i).setPrefWidth(113*scale);
                            }
                        }
                    }
                }

        //decks
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


            VBox playerSection = new VBox();

                StackPane playerBoardAndStuffAbove = new StackPane();
                playerBoardAndStuffAbove.getChildren().addAll(playerSection);
                for(GridPane g : playerAmmoGrid)
                    g.setTranslateX(400*scalePB);

            //layout
                Pane mapAndStuffAbove = new Pane();
            mapAndStuffAbove.getChildren().addAll(map,skullsGrid,weaponsGrid1,weaponsGrid2,weaponsGrid3,roomsGrid, //mettere virgola
                      weaponDeckView,pUDeckView,cardsRemainingPU,cardsRemainingWeapons);
                  pUDeckView.setTranslateX(453*scale);
                pUDeckView.setTranslateY(-332*scale);
                weaponDeckView.setTranslateX(440*scale);
                weaponDeckView.setTranslateY(-132*scale);
                cardsRemainingPU.setTranslateX(450*scale);
                cardsRemainingPU.setTranslateY(-330*scale);
                cardsRemainingPU.setTextFill(Color.web("#F8F8FF"));
                cardsRemainingWeapons.setTranslateX(440*scale);
                cardsRemainingWeapons.setTranslateY(-130*scale);
                cardsRemainingWeapons.setTextFill(Color.web("#F8F8FF"));
                for(ClientModel.SimplePlayer p : players) {
                    if (!p.equals(clientModel.getCurrentPlayer()))
                        playerSection.getChildren().add(playerBoards.get(players.indexOf(p)));
                    else {
                        mapAndStuffAbove.getChildren().add((playerBoards.get(players.indexOf(p))));//current player is added at the mapboard and translated at the bottom
                        playerBoards.get(players.indexOf(p)).setTranslateX(300*scale);
                        playerBoards.get(players.indexOf(p)).setTranslateY(740*scale);
                      }
                }
                HBox board = new HBox();
                board.getChildren().addAll(mapAndStuffAbove, playerBoardAndStuffAbove);

                skullsGrid.setTranslateX(70*scale);
                skullsGrid.setTranslateY(50*scale);
                weaponsGrid1.setTranslateX(540*scale);
                weaponsGrid2.setRotate(270);
                weaponsGrid2.setTranslateX(803*scale);
                weaponsGrid2.setTranslateY(550*scale);
                weaponsGrid3.setRotate(90);
                weaponsGrid3.setTranslateX(-98*scale);
                weaponsGrid3.setTranslateY(380*scale);
                roomsGrid.setTranslateX(180*scale);
                roomsGrid.setTranslateY((200*scale));
            board.setStyle("-fx-background-color: #000000");
            Scene sceneMap = new Scene(board, 1200*scale, 800*scale);
                Stage mapStage = new Stage();
                mapStage.setScene(sceneMap);
                mapStage.setFullScreen(true);
                mapStage.show();

            //}catch (FileNotFoundException e){
            //    e.printStackTrace();
            //}
        });
    }

    public List<ImageView> getImageOfWeaponsInSquare(ClientModel.SimpleSquare square){
        List<ImageView> weaponView = new ArrayList<>();
        int index=square.getId();
       // System.out.println(index);
        List<ClientModel.SimpleWeapon> weaponList =(clientModel.getSquares().get(index)).getWeapons();
        //try{
        for(ClientModel.SimpleWeapon w : weaponList){
            String key= w.getName();
            InputStream weaponFile = this.getClass().getResourceAsStream("/images/cards/"+key.replace(" ","_")+".png");
            Image weaponImage = new Image(weaponFile);
            ImageView weaponImageView = new ImageView(weaponImage);
            weaponView.add(weaponImageView);
        }

        return weaponView;
        //}catch (FileNotFoundException e){
        //    e.printStackTrace();
        //}
        //return null;
    }

    public ImageView getImageOfSquare(ClientModel.SimpleSquare square){
        int r,b,y;
        boolean pU;
        String k1="",k2="",k3="",k4="",k5="",k6="",k7="";
        int o=square.getId();

        r = square.getRedAmmo();
        b = square.getBlueAmmo();
        y = square.getYellowAmmo();
        pU = square.isPowerup();
        if(pU)
            k1="P";
        if(r>=1)
            k2="R";
        if(r==2)
            k3="R";
        if(b>=1)
            k4="B";
        if(b==2)
            k5="B";
        if(y>=1)
            k6="Y";
        if(y==2)
            k7="Y";
        //try{
        InputStream ammoFile = this.getClass().getResourceAsStream("/images/ammo/ammo"+k1+k2+k3+k4+k5+k6+k7+".png");
        Image ammoImage = new Image(ammoFile);
        ImageView ammoView = new ImageView(ammoImage);
        return ammoView;
        //}catch (FileNotFoundException e){
        //    e.printStackTrace();
        //}
        //return null;
    }

    public ImageView getImageOfPlayer(ClientModel.SimplePlayer player){
        String key,color;
        color=player.getColor();
        switch (color){
            case "green":
                key="Sprog";
                break;
            case "grey":
                key="Dozer";
                break;
            case "yellow":
                key="D_struct_or";
                break;
            case"blue":
                key="Banshee";
                break;
            default:
                key="Violet";
        }

        if(player.isFlipped())
            key=key+"Flipped";

        //try{
        InputStream playerFile = this.getClass().getResourceAsStream("/images/miscellaneous/"+key+".png");
        Image playerImage = new Image(playerFile);
        ImageView playerView = new ImageView(playerImage);
        return playerView;
        //}catch (FileNotFoundException e){
        //    e.printStackTrace();
        //}
        //return null;
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