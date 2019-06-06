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
 * @author  BassaniRiccardo, davidealde
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
    private Stage mapStage;
    private Pane messagePanel;
    private boolean started;
    private ImageView welcomeView;
    private List<Integer> justDamaged;
    private String previousMsg;
  //  private BorderPane pane;


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
        messagePanel = new Pane();
        justDamaged = new ArrayList<>();
        previousMsg = null;
        //pane = new BorderPane();
    }

    public void setClientMain(ClientMain clientMain) {
        this.clientMain = clientMain;
        this.clientModel=clientMain.getClientModel();}

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
            mapStage = new Stage();
            stage = primaryStage;
            stage.setOnCloseRequest(e -> {System.exit(0);});
            stage.setTitle("Adrenaline");
            messagePanel = new VBox();
            messagePanel.setBackground(new Background(new BackgroundFill(color, null, null)));
            Label label = new Label("Entering the configuration phase...");
            messagePanel.getChildren().add(label);
            Image welcomeImage = new Image(getClass().getResourceAsStream("/images/miscellaneous/welcome.jpg"));
            welcomeView = new ImageView(welcomeImage);
            printer();
        });
    }

    private void printer(){
        BorderPane pane = new BorderPane();
        pane.setCenter(welcomeView);
        welcomeView.setFitHeight(600);
        welcomeView.setPreserveRatio(true);
        pane.setStyle("-fx-background-color: #000000");
        pane.setBottom(messagePanel);
        Scene scene = new Scene(pane, 500, 250);
        stage.setScene(scene);
        //stage.setFullScreen(true);
        stage.show();
    }
    /**
     * Displays a simplified model containing all the information the user needs.
     */
    @Override

    public void render() {

        Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        double userWidthResolution = screenSize.getWidth();
        double userHeightResolution = screenSize.getHeight();
        double scale;
        double fakeScale;
        if(userWidthResolution/userHeightResolution>=developerWidthResolution/developerHeightResolution)
            fakeScale = userHeightResolution/developerHeightResolution;
        else
            fakeScale = userWidthResolution/developerWidthResolution;
        if(1050*fakeScale>userWidthResolution*3/4)
            fakeScale = userWidthResolution*3/(4*developerWidthResolution); //sets at 3/4 of the screen width
        scale=fakeScale; //needed for lamba necessities
        while (stage==null){
            try {
                Thread.sleep(2000);
            }catch (InterruptedException e){e.printStackTrace();}
        }

        Platform.runLater( () -> {
            //try {

                clientModel = clientMain.getClientModel();
            MapBoardRenderer mapBoardRenderer = new MapBoardRenderer(scale, clientModel);
            Animations animation = new Animations();

         //map
            HBox map = mapBoardRenderer.mapRenderer();
         //skullsKillShotTrack
            int skullNumber=clientModel.getSkullsLeft();
            GridPane skullsGrid = mapBoardRenderer.killShotTrackRender(skullNumber);
         //rooms
            GridPane roomsGrid = mapBoardRenderer.roomRenderer();
         //weapons  DOVRà RESTITUIRE UNA LISTA DI 3 GRIDPANE---------------------------------------------
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

            List<ClientModel.SimpleSquare> squares = clientModel.getSquares();
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
            PlayerBoardRenderer playerBoardRenderer = new PlayerBoardRenderer(scalePB, players);

            //icons
            List<ImageView> icons = mapBoardRenderer.iconsRenderer();
            int column;
            int row;
            for(ClientModel.SimplePlayer p : players){
                if(p.getPosition()!=null) {
                    column = mapBoardRenderer.columnFinder(p.getPosition());
                    row = mapBoardRenderer.rowFinder(p.getPosition());
                    roomsGrid.add(icons.get(players.indexOf(p)), column, row);
                }
            }
            //icons flashing when damaged
            if(justDamaged.isEmpty()) {
                for (ClientModel.SimplePlayer p : players)
                    justDamaged.add(0);
            }else
                for (ClientModel.SimplePlayer p : players)
                    if(justDamaged.get(players.indexOf(p))!=p.getDamageID().size())
                        animation.flash(icons.get(players.indexOf(p)));

            //ammo
            List<GridPane> playerAmmoGrid = playerBoardRenderer.ammoRender();
            //damages
            List<GridPane> damageGrid = playerBoardRenderer.damagesRenderer();
            //marks
            List<GridPane> marksGrid = playerBoardRenderer.marksRenderer();
            //hand
            List<MenuButton> handButtons = playerBoardRenderer.handRenderer();
            //points
            List<GridPane> pointGrid = playerBoardRenderer.pointsRenderer();
            //skullsPlayer
            List<Integer> deathsNumber = new ArrayList<>();
            for(ClientModel.SimplePlayer p : players){
                deathsNumber.add(p.getDeaths());
                System.out.println(deathsNumber.get(players.indexOf(p)));
                //System.out.println(p.)
            }
            System.out.println(clientModel.getKillShotTrack());
            List<GridPane> skullGrid = playerBoardRenderer.skullsPlayerRenderer(deathsNumber);

            for(int i=0; i<players.size(); i++) {
                playerBoards.add(new StackPane());
                playerBoards.get(i).getChildren().addAll(playerAmmoGrid.get(i), damageGrid.get(i), marksGrid.get(i),
                        skullGrid.get(i), handButtons.get(i), pointGrid.get(i));
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
                for(ClientModel.SimplePlayer p : players) {
                    if (p.getId()!=clientModel.getPlayerID())
                        playerSection.getChildren().add(playerBoards.get(players.indexOf(p)));
                    else {
                        mapAndStuffAbove.getChildren().add((playerBoards.get(players.indexOf(p))));//current player is added at the mapboard and translated at the bottom
                        playerBoards.get(players.indexOf(p)).setTranslateX(300*scale);
                        playerBoards.get(players.indexOf(p)).setTranslateY(740*scale);
                        System.out.println(p.getId());
                      }
                }
                playerSection.getChildren().add(messagePanel);
                HBox board = new HBox();
                board.getChildren().addAll(mapAndStuffAbove, playerBoardAndStuffAbove);


                weaponsGrid1.setTranslateX(540*scale);
                weaponsGrid2.setRotate(270);
                weaponsGrid2.setTranslateX(803*scale);
                weaponsGrid2.setTranslateY(550*scale);
                weaponsGrid3.setRotate(90);
                weaponsGrid3.setTranslateX(-98*scale);
                weaponsGrid3.setTranslateY(380*scale);

            board.setStyle("-fx-background-color: #000000");
            Scene sceneMap = new Scene(board, 1200*scale, 800*scale);
                mapStage.setScene(sceneMap);
                mapStage.setFullScreen(true);
                if (!started){
                    mapStage.show();
                    started = true;
                }

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
                messagePanel = msg;
                if(clientModel==null && message != previousMsg){
                    previousMsg = message;
                    printer();
                } else
                    render();
                //da togliere
               // stage.setScene(scene);
                //stage.show();

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
            messagePanel = req;
            if(clientModel==null && question != previousMsg){
                previousMsg = question;
                printer();
            }
            else
                render();
            //da togliere
          //  Scene info = new Scene(req, 500, 250, color);
            //stage.setScene(info);
            //stage.show();

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
            messagePanel = opt;
            if(clientModel==null && message != previousMsg){
                previousMsg = message;
                printer();
            }else
                render();
            //da togliere
          //  Scene scene = new Scene(opt, 500,250, color);
           // stage.setScene(scene);
            //stage.show();

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