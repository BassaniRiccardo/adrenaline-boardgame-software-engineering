package it.polimi.ingsw.view;

//TODO: create a package for gui classes

import java.awt.*;
import java.io.InputStream;
import java.util.ArrayList;
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
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;


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
    private Color color = Color.rgb(255, 255,255);
    private Stage stage;
    private Scene scene;
    private BorderPane root;
    private DataSaver dataSaver = new DataSaver();
    private static final Logger LOGGER = Logger.getLogger("clientLogger");
    public static final CountDownLatch latch = new CountDownLatch(1);
    public static GUI GUI = null;
    private ClientModel clientModel;
    private static final int DEVELOPER_WIDTH_RESOLUTION =1536;
    private static final int DEVELOPER_HEIGHT_RESOLUTION =864;
    private static final double DEVELOPER_PLAYER_BOARD_WIDTH = 486;
    private double scalePB;
    private double userPlayerBoardWidth;
    private Stage mapStage;
    private Pane messagePanel;
    private boolean started;
    private ImageView welcomeView;
    private List<Integer> justDamaged;
    private double userWidthResolution;
    private double userHeightResolution;
    private double scale;
    private MapBoardRenderer mapBoardRenderer;
    private String renderMessage; //andrà cancellato credo
    private boolean renderAlreadyLaunched;
  //  private BorderPane pane;


    public ClientMain getClientMain() {
        return clientMain;
    }

    @Override
    public void displayDisconnection(){}

    @Override
    public void displaySuspension(){}

    @Override
    public void displayEnd(String message){}

    @Override
    public void addHistory(String message){}

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
        clientModel = null;
        renderMessage = null;
        renderAlreadyLaunched = false;
        //pane = new BorderPane();
    }

    public void setClientMain(ClientMain clientMain) {
        this.clientMain = clientMain;
        this.clientModel=clientMain.getClientModel();   //potrebbe forse essere cancellato
    }

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

            Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
            userWidthResolution = screenSize.getWidth();
            userHeightResolution = screenSize.getHeight();
            double fakeScale;
            if(userWidthResolution/userHeightResolution>= DEVELOPER_WIDTH_RESOLUTION / DEVELOPER_HEIGHT_RESOLUTION)
                fakeScale = userHeightResolution/ DEVELOPER_HEIGHT_RESOLUTION;
            else
                fakeScale = userWidthResolution/ DEVELOPER_WIDTH_RESOLUTION;
            if(1050*fakeScale>userWidthResolution*3/4)
                fakeScale = userWidthResolution*3/(4* DEVELOPER_WIDTH_RESOLUTION); //sets at 3/4 of the screen width
            scale=fakeScale; //needed for lamba necessities
            mapBoardRenderer = new MapBoardRenderer(scale, clientModel);

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
            root = new BorderPane();
            root.setCenter(welcomeView);
            welcomeView.setFitHeight(600*scale);
            welcomeView.setPreserveRatio(true);
            root.setStyle("-fx-background-color: #000000");
            root.setBottom(messagePanel);
            scene = new Scene(root, 1000, 800);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();
        });
    }

    private void printer(){
        root = new BorderPane();
        root.setCenter(welcomeView);
        welcomeView.setFitHeight(600*scale);
        welcomeView.setPreserveRatio(true);
        root.setStyle("-fx-background-color: #000000");
        root.setBottom(messagePanel);
        scene.setRoot(root);
    }
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
            //try {

            clientModel=clientMain.getClientModel(); //penso sia utile solo per il test
            mapBoardRenderer.setClientModel(clientModel);
            mapBoardRenderer.setRenderInstruction(renderMessage);

            Animations animation = new Animations();

         //map
            HBox map = mapBoardRenderer.mapRenderer();
         //skullsKillShotTrack
            int skullNumber=clientModel.getSkullsLeft();
            GridPane skullsGrid = mapBoardRenderer.killShotTrackRender(skullNumber);
         //rooms
            GridPane roomsGrid = mapBoardRenderer.roomRenderer();
         //weapons
            List<GridPane> weaponGrid = mapBoardRenderer.weaponRenderer();

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
            scalePB= userPlayerBoardWidth/ DEVELOPER_PLAYER_BOARD_WIDTH;
            PlayerBoardRenderer playerBoardRenderer = new PlayerBoardRenderer(scalePB, players, clientModel);

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
                //System.out.println(p.)
            }
            List<GridPane> skullGrid = playerBoardRenderer.skullsPlayerRenderer(deathsNumber);

            for(int i=0; i<players.size(); i++) {
                playerBoards.add(new StackPane());
                playerBoards.get(i).getChildren().addAll(playerAmmoGrid.get(i), damageGrid.get(i), marksGrid.get(i),
                        skullGrid.get(i), handButtons.get(i), pointGrid.get(i));
            }

            VBox playerSection = new VBox();

            StackPane playerBoardAndStuffAbove = new StackPane();
            playerBoardAndStuffAbove.getChildren().add(playerSection);
            for(GridPane g : playerAmmoGrid)
                g.setTranslateX(400*scalePB);

            //layout
                Pane mapAndStuffAbove = new Pane();
            mapAndStuffAbove.getChildren().addAll(map,skullsGrid,weaponGrid.get(0),weaponGrid.get(1),weaponGrid.get(2),roomsGrid);
            //decks
            mapAndStuffAbove = mapBoardRenderer.deckRenderer(mapAndStuffAbove);

                for(ClientModel.SimplePlayer p : players) {
                    if (p.getId()!=clientModel.getPlayerID())
                        playerSection.getChildren().add(playerBoards.get(players.indexOf(p)));
                    else {
                        mapAndStuffAbove.getChildren().add((playerBoards.get(players.indexOf(p))));//current player is added at the mapboard and translated at the bottom
                        playerBoards.get(players.indexOf(p)).setTranslateX(300*scale);
                        playerBoards.get(players.indexOf(p)).setTranslateY(740*scale);
                      }
                }
                playerSection.getChildren().add(messagePanel);
                HBox board = new HBox();
                board.getChildren().addAll(mapAndStuffAbove, playerBoardAndStuffAbove);

            board.setStyle("-fx-background-color: #000000");
            scene.setRoot(board);
           /* Scene sceneMap = new Scene(board, 1500*scale, 900*scale);
                mapStage.setScene(sceneMap);
                mapStage.setFullScreen(true);
                if (!started){
                    mapStage.show();
                    started = true;
                }*/
            renderMessage=null;
            //}catch (FileNotFoundException e){
            //    e.printStackTrace();
            //}
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
            List<String> labelButton = new ArrayList<>();
            List<Button> inputButtons = new ArrayList<>();

            if(list.get(0).contains("Square")){
                System.out.println("SQUARE è GiuSTO");
                Stage optStage = new Stage();
                for (String item : list) {
                    Button b = new Button(item);
                    inputButtons.add(b);
                    labelButton.add(item);
                    b.setOnAction(e -> {
                        System.out.println("OPT " + (inputButtons.indexOf(b)+1) + ": you clicked me!");
                        dataSaver.message = message;
                        dataSaver.answer = Integer.toString(inputButtons.indexOf(b) + 1);
                        dataSaver.update = true;
                        optStage.close();
                    });
                }

                System.out.println(inputButtons);
                System.out.println(labelButton);
                mapBoardRenderer.setInputButtons(inputButtons);
                mapBoardRenderer.setLabelButton(labelButton);
                mapBoardRenderer.setRenderInstruction("Square");
                renderMessage="Square";

            } else {
                HBox optionList = new HBox();
                optionList.setAlignment(Pos.CENTER);
                optionList.setSpacing(40 / list.size());
                Stage optStage = new Stage();

                List<Button> buttons = new ArrayList<>();
                for (String item : list) {
                    Button b = new Button(item);
                    buttons.add(b);
                    b.setOnAction(e -> {
                        System.out.println("OPT " + (buttons.indexOf(b) + 1) + ": you clicked me!");
                        dataSaver.message = message;
                        dataSaver.answer = Integer.toString(buttons.indexOf(b) + 1);
                        dataSaver.update = true;
                        optStage.close();
                    });
                }
                optionList.getChildren().addAll(buttons);
                opt.getChildren().add(optionList);
            }
            messagePanel = opt;
          /* if(renderAlreadyLaunched)
                render();
            else {
                clientModel = clientMain.getClientModel();
                if (clientModel != null) {
                    renderAlreadyLaunched = true;
                    mapBoardRenderer = new MapBoardRenderer(scale, clientModel);
                } else
                    printer();
            }
*/
            if(clientModel==null){
                printer();
            }else {
                render();
            }
            //da togliere
          //  Scene scene = new Scene(opt, 500,250, color);
           // stage.setScene(scene);
            //stage.show();

        });

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
                if(clientModel==null){
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
            if(clientModel==null){
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
    public  void run(){ }
    /**
     * Handles complex events
     *
     * @param event
     */
    @Override
    public void handle(Event event) { }
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

}