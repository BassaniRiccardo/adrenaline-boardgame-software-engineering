package it.polimi.ingsw.view;

import java.awt.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.polimi.ingsw.view.guirenderer.MapBoardRenderer;
import it.polimi.ingsw.view.guirenderer.PlayerBoardRenderer;
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

import static it.polimi.ingsw.network.server.VirtualView.ChooseOptionsType.*;


/**
 * Graphical user interface for I/O operations
 *
 * @author  BassaniRiccardo, davidealde
 */


public class GUI extends Application implements UI, Runnable, EventHandler {

    private ClientMain clientMain;
    private Color color = Color.rgb(255, 255,255);
    private Stage stage;
    private Scene scene;
    private BorderPane root;
    private DataSaver dataSaver = new DataSaver();
    private static final CountDownLatch latch = new CountDownLatch(1);
    private static GUI gui = null;
    private ClientModel clientModel;
    private static final double DEVELOPER_WIDTH_RESOLUTION =1536;
    private static final double DEVELOPER_HEIGHT_RESOLUTION =864;
    private static final double DEVELOPER_PLAYER_BOARD_WIDTH = 486;
    private double scalePB;
    private double userPlayerBoardWidth;
    private Pane messagePanel;
    private ImageView welcomeView;
    private List<Integer> justDamaged;
    private double userWidthResolution;
    private double userHeightResolution;
    private double scale;
    private MapBoardRenderer mapBoardRenderer;
    private PlayerBoardRenderer playerBoardRenderer;
    private String mapBoardRenderInstruction;
    private String playerBoardRenderInstruction;
    private boolean setColor;
    private String historyMessage;
    private boolean newHistoryMessage;
    private VBox historyPanel;


    private static final Logger LOGGER = Logger.getLogger("clientLogger");
    private static final String CLOSE = "CLOSE";
    private static final String DISCONNECTION_MSG = "You cannot reach the server.\nYou can try starting another client and\nlog in with the same username to resume.";
    private static final String SUSPENSION_MSG = "You were suspended from the server\nbecause you were not able to\nfinish your turn in time.\nYou can try starting another client and\nlog in with the same username to resume.";
    private static final int CHECK_INPUT_TIME = 10;
    private static final int WAIT_FOR_STAGE_TIME = 10;
    private static final int FINAL_DISPLAY_TIME = 20000;

    private static final float MAXIMUM_BOARDPANE_WIDTH= 1050;
    private static final float WELCOME_VIEW_TY= 30;
    private static final float WELCOME_VIEW_H = 500;
    private static final float SCENE_W= 1000;
    private static final float SCENE_H= 800;
    private static final float OPT_SPACING=40 ;
    private static final float OPTIONLIST1_SPACING=10 ;
    private static final float OPTIONLIST2_SPACING=10 ;
    private static final float SCENE1_W=500 ;
    private static final float SCENE1_H=250 ;
    private static final float MSG_SPACING=40 ;
    private static final float QUEST_SPACING=10 ;
    private static final float TEXTFIELD_MAXSIZE_W=200 ;
    private static final float TEXTFIELD_MAXSIZE_H=50 ;
    private static final float REQ_SPACING=40 ;
    private static final float PLAYERAMMOGRID_TX=400 ;
    private static final float PLAYERBOARDS_TX=300 ;
    private static final float PLAYERBOARDS_TY=740 ;
    private static final float MESSAGEBOX_SPACING=40 ;
    private static final float BOARDPANE_WIDTH=1050 ;
    private static final String NORMAL_RENDERINSTRUCTION="Normal" ;
    private static final String WEAPON_RENDERINSTRUCTION="Weapon" ;
    private static final String INTERRUPRING_METHOD_MESSAGE="Interrupting method";


    /**
     * Class storing the values the get() method must return.
     */
    class DataSaver{
        String answer;
        String message;
        boolean update;
    }

    /**
     * Returns a static reference to the GUI itself, after having waited for its configuration.
     *
     * @return the GUI itself.
     */
    static GUI waitGUI() throws InterruptedException{
        latch.await();
        return gui;
    }


    /**
     * Sets the attribute "gui" and makes it available.
     *
     * @param guyToSet the GUI itself, to set as a static attribute "gui".
     */
    private static void setGui(GUI guyToSet) {
       gui = guyToSet;
       latch.countDown();
    }


    /**
     * Constructor
     */
    public GUI() {
        setGui(this);
        clientModel = null;
        messagePanel = new Pane();
        justDamaged = new ArrayList<>();
        mapBoardRenderInstruction = NORMAL_RENDERINSTRUCTION;
        playerBoardRenderInstruction = NORMAL_RENDERINSTRUCTION;
        this.setColor = true;
        historyMessage="";
        newHistoryMessage = false;
    }


    /**
     * Sets the ClientMain and the ClientModel.
     *
     * @param clientMain    the given ClientMain.
     */
    void setClientMain(ClientMain clientMain) {
        this.clientMain = clientMain;
        this.clientModel=clientMain.getClientModel();
    }


    /**
     * The main entry point for the JavaFX application.
     * Calculates the scale factor of the board, instantiates necessary classes,
     * builds the first screen and shows the stage.
     *
     * @param primaryStage the primary stage for this application.
     */
    @Override
    public void start(Stage primaryStage) {

        Platform.runLater( () -> {

            Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
            userWidthResolution = screenSize.getWidth();
            userHeightResolution = screenSize.getHeight();
            double fakeScale;
            if(userWidthResolution/userHeightResolution>= DEVELOPER_WIDTH_RESOLUTION / DEVELOPER_HEIGHT_RESOLUTION)
                fakeScale = userHeightResolution/ DEVELOPER_HEIGHT_RESOLUTION;
            else
                fakeScale = userWidthResolution/ DEVELOPER_WIDTH_RESOLUTION;
            if(MAXIMUM_BOARDPANE_WIDTH*fakeScale>userWidthResolution*3/4)
                fakeScale = userWidthResolution*3/(4* DEVELOPER_WIDTH_RESOLUTION); //sets at 3/4 of the screen width
            scale=fakeScale; //needed for lambda necessities
            mapBoardRenderer = new MapBoardRenderer(scale, clientModel);
            playerBoardRenderer = new PlayerBoardRenderer(scalePB, clientModel);

            stage = primaryStage;
            stage.setOnCloseRequest(e -> System.exit(0));
            stage.setTitle("Adrenaline");
            messagePanel = new VBox();
            messagePanel.setBackground(new Background(new BackgroundFill(color, null, null)));
            Label label = new Label("Entering the configuration phase...");
            messagePanel.getChildren().add(label);
            Image welcomeImage = new Image(getClass().getResourceAsStream("/images/miscellaneous/welcome.jpg"));
            welcomeView = new ImageView(welcomeImage);
            configureRoot();
            scene = new Scene(root, SCENE_W, SCENE_H);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();
        });
    }


    /**
     * Builds the setup screen.
     */
    private void configureRoot(){
        root = new BorderPane();
        root.setTop(welcomeView);
        root.setAlignment(welcomeView, Pos.CENTER);
        root.setStyle("-fx-background-color: #000000");
        welcomeView.setTranslateY(WELCOME_VIEW_TY*scale);
        welcomeView.setFitHeight(WELCOME_VIEW_H * scale);
        welcomeView.setPreserveRatio(true);
        root.setBottom(messagePanel);
    }


    /**
     * Prints the root configured.
     */
    private void printer(){
        configureRoot();
        scene.setRoot(root);
    }


    /**
     * Displays a OPT message, hence a message and a list of options to choose among.
     * Configures the message panel and calls printer() or render() if the game is started
     * Standard options are put in buttons and the buttons are put in the message panel.
     * Options that can be selected by clicking on images are instead passed to MapBoardRenderer or PlayerBoardRender
     * with information about the type of options.
     *
     * @param type      the type of options (buttons in message panels, weapons, powerups, players, squares).
     * @param message   the message to be displayed.
     * @param list      the list of options.
     */
    public void display(String type, String message, List<String> list) {

        Platform.runLater( () -> {

            LOGGER.log(Level.INFO, "asking \"{0}\"", message);
            List<String> modifiedList = new ArrayList<>();
            for (String opt : list) {
                modifiedList.add(removeEscapeCode(type, opt));
            }
            VBox opt = new VBox();
            opt.setBackground(new Background(new BackgroundFill(color, null, null)));
            Label label = new Label(message);
            opt.getChildren().add(label);
            opt.setAlignment(Pos.CENTER);
            opt.setSpacing(OPT_SPACING);
            List<String> labelButton = new ArrayList<>();
            List<Button> inputButtons = new ArrayList<>();
            boolean interactiveInput;
            interactiveInput =  type.equals(CHOOSE_SQUARE.toString()) ||  type.equals(CHOOSE_WEAPON.toString()) ||
                    type.equals(CHOOSE_PLAYER.toString()) || type.equals(CHOOSE_POWERUP.toString());

            HBox optionList1 = new HBox();
            VBox optionList2 = new VBox();
            optionList1.setAlignment(Pos.CENTER);
            optionList1.setSpacing(OPTIONLIST1_SPACING / modifiedList.size());
            optionList2.setAlignment(Pos.CENTER);
            optionList2.setSpacing(OPTIONLIST2_SPACING / modifiedList.size());

            for (String item : modifiedList) {
                Button b = new Button();
                if(interactiveInput && (item.equals("Reset") || item.equals("None"))){   //reset and none buttons are always needed in the message panel
                    b.setText(item);
                    inputButtons.add(b);
                    labelButton.add(item);
                    optionList2.getChildren().add(b);
                    b.setOnAction(e -> {
                        LOGGER.log(Level.INFO, "opt: button {0} clicked", (inputButtons.indexOf(b)+1));
                        dataSaver.message = message;
                        dataSaver.answer = Integer.toString(inputButtons.indexOf(b)+1);
                        dataSaver.update = true;
                    });
                    break;
                }else{
                    if(interactiveInput) {
                        b.setText(" ");
                        inputButtons.add(b);
                        labelButton.add(item);

                    }else {
                        b.setText(item);
                        inputButtons.add(b);
                        if(clientModel==null){
                            optionList1.getChildren().add(inputButtons.get(modifiedList.indexOf(item)));
                        }else {
                            optionList2.getChildren().add(inputButtons.get(modifiedList.indexOf(item)));
                        }
                    }
                }
                b.setOnAction(e -> {
                    dataSaver.message = message;
                    dataSaver.answer = Integer.toString(inputButtons.indexOf(b) + 1);
                    LOGGER.log(Level.INFO, "answer \"{0}\"", dataSaver.answer);
                    dataSaver.update = true;
                });


            }
            if(clientModel==null){
                opt.getChildren().add(optionList1);
            }else {
                opt.getChildren().add(optionList2);
            }

            if(type.equals(CHOOSE_SQUARE.toString()))
                mapBoardRenderInstruction = "Square";
            else if(type.equals(CHOOSE_WEAPON.toString())){
                if( ! clientModel.getCurrentPlayer().getWeapons().isEmpty()) {
                    //verifies if the weapons are in the player hand or on the board
                    boolean inPlayerHand = false;
                    for (int i=0; i < clientModel.getCurrentPlayer().getWeapons().size(); i++ ){
                        if (modifiedList.get(0).equals(clientModel.getCurrentPlayer().getWeapons().get(i).getName())) {
                            inPlayerHand = true;
                            break;
                        }
                    }
                    if (inPlayerHand) playerBoardRenderInstruction = WEAPON_RENDERINSTRUCTION;
                    else mapBoardRenderInstruction = WEAPON_RENDERINSTRUCTION;
                }
                else{
                    mapBoardRenderInstruction = WEAPON_RENDERINSTRUCTION;}

            }else if(type.equals(CHOOSE_POWERUP.toString()))
                playerBoardRenderInstruction="PowerUp";
            else if(type.equals(CHOOSE_PLAYER.toString()))
                mapBoardRenderInstruction="Player";
            else{

                mapBoardRenderInstruction = NORMAL_RENDERINSTRUCTION;
                playerBoardRenderInstruction = NORMAL_RENDERINSTRUCTION;
            }
            mapBoardRenderer.setInputButtons(inputButtons);
            mapBoardRenderer.setLabelButton(labelButton);
            playerBoardRenderer.setInputButtons(inputButtons);
            playerBoardRenderer.setLabelButton(labelButton);
            messagePanel = opt;

            if(clientModel==null){
                printer();
            }else {
                render();
            }
        });

    }

    /**
     * Displays a MSG message, hence a simple message.
     * In the case of a message notifying the user of opponents's disconnection a new window is opened.
     * The user can read the message and close the window by pressing a button.
     *
     * @param message   message to be displayed
     */
    public void display(String message) {

        while (stage==null){
            try {
                Thread.sleep(WAIT_FOR_STAGE_TIME);
            }catch (InterruptedException e){

                LOGGER.log(Level.INFO, INTERRUPRING_METHOD_MESSAGE );
                Thread.currentThread().interrupt();
            }
        }

        Platform.runLater( () -> {

            String mes = removeEscapeCode("display", message) + "\n";
            LOGGER.log(Level.INFO, "displaying \"{0}\"", mes);
            if      (mes.contains("Banshee") && setColor) {
                this.color = Color.BLUE;
                setColor = false;
            }
            else if (mes.contains("Sprog") && setColor) {
                this.color = Color.GREEN;
                setColor = false;
            }
            else if (mes.contains("Violet") && setColor){
                this.color = Color.PURPLE;
                setColor = false;
            }
            else if (mes.contains("Dozer") && setColor){
                this.color = Color.GREY;
                setColor = false;
            }
            else if (mes.contains("D_struct_or") && setColor){
                this.color = Color.YELLOW;
                setColor = false;
            }

            Label label = new Label(mes);
            VBox msg = new VBox();
            msg.setBackground(new Background(new BackgroundFill(color, null, null)));
            msg.getChildren().add(label);
            msg.setAlignment(Pos.CENTER);
            Scene scene1 = new Scene(msg, SCENE1_W, SCENE1_H, color);
            Stage msgStage = new Stage();

            if (mes.contains("disconnected")){
                msgStage.setScene(scene1);
                msgStage.show();
                Button close = new Button("ok");
                close.setAlignment(Pos.CENTER);
                msg.getChildren().add(close);
                msg.setAlignment(Pos.CENTER);
                msg.setSpacing(MSG_SPACING);
                close.setOnAction(e -> msgStage.close());
            }

            else {
                messagePanel = msg;
                if(clientModel==null){
                    printer();
                } else
                    render();
            }
        });
    }

    /**
     * Displays a REQ message, hence a message requiring for a text input and specifying the maximum length allowed
     * for the answer.
     *
     * @param question       text to be displayed.
     * @param maxLength      maximum length allowed for the answer.
     */
    public void display(String question, String maxLength) {

        Platform.runLater( () -> {

            LOGGER.log(Level.INFO, "asking \"{0}\"", question);
            VBox req = new VBox();
            req.setBackground(new Background(new BackgroundFill(color, null, null)));
            VBox quest = new VBox();
            quest.setSpacing(QUEST_SPACING);
            quest.setAlignment(Pos.CENTER);
            Label label1 = new Label(question);
            Label label2 = new Label("(max " + maxLength + " characters)");
            quest.getChildren().addAll(label1, label2);
            req.getChildren().add(quest);

            TextField textField = new TextField();
            textField.setAlignment(Pos.CENTER);
            textField.setMaxSize(TEXTFIELD_MAXSIZE_W, TEXTFIELD_MAXSIZE_H);
            req.getChildren().add(textField);

            Button requestButton = new Button("confirm");
            requestButton.setAlignment(Pos.CENTER);
            req.getChildren().add(requestButton);
            req.setAlignment(Pos.CENTER);
            req.setSpacing(REQ_SPACING);
            Stage reqStage = new Stage();

            requestButton.setOnAction(e ->
                    {
                        LOGGER.log(Level.INFO, "request confirmation button clicked");
                        dataSaver.answer = (textField.getText());
                        LOGGER.log(Level.INFO, "answer \"{0}\"", dataSaver.answer);
                        dataSaver.message = question;
                        dataSaver.update = true;
                        reqStage.close();
                    }
            );

            messagePanel = req;
            if(clientModel==null){
                printer();
            }
            else
                render();
        });

    }

    /**
     * Makes the thread wait until the input arrives from the user.
     */
    private void waitForInput(){
        while (!dataSaver.update){
            try {
                Thread.sleep(CHECK_INPUT_TIME);
            }catch (InterruptedException e){

                LOGGER.log(Level.INFO, INTERRUPRING_METHOD_MESSAGE );
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Queries the user for input, when a maximum length is allowed for the answer.
     *
     * @param maxLength     the maximum length allowed for the answer.
     * @return              the user's input.
     */
    public String get(String maxLength) {
        waitForInput();
        while (dataSaver.answer.length() > Integer.parseInt(maxLength)){
            dataSaver.update=false;
            display("Max length exceeded, retry: " + dataSaver.message, maxLength);
            get(maxLength);
        }
        dataSaver.update = false;
        return dataSaver.answer;
    }

    /**
     * Queries the user for input.
     *
     * @param list      the list of option to choose among.
     * @return          the user's input.
     */
    public String get(List<String> list) {
        waitForInput();
        dataSaver.update = false;
        if (Integer.parseInt(dataSaver.answer) < 1)
            return "1";
        return dataSaver.answer;
    }

    /**
     * Displays a simplified model containing all the information the user needs.
     */
    @Override
    public void render() {

        Platform.runLater( () -> {
            clientModel=clientMain.getClientModel();
            mapBoardRenderer.setClientModel(clientModel);
            mapBoardRenderer.setRenderInstruction(mapBoardRenderInstruction);

            //map
            HBox map = mapBoardRenderer.mapRenderer();
            //skullsKillShotTrack
            int skullNumber=clientModel.getSkullsLeft();
            GridPane skullsGrid = mapBoardRenderer.killShotTrackRenderer(skullNumber);
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
                playerView.add(getBoardOfPlayer(p));
                playerView.get(playerIndex).setFitWidth(userWidthResolution-BOARDPANE_WIDTH);
                playerView.get(playerIndex).fitWidthProperty().bind(playerBoards.get(playerIndex).minWidthProperty());
                playerView.get(playerIndex).fitWidthProperty().bind(playerBoards.get(playerIndex).maxWidthProperty());
                playerView.get(playerIndex).setPreserveRatio(true);
                playerIndex++;
            }

            for(int i=0; i<players.size(); i++) {
                playerBoards.add(new Pane());
                playerBoards.get(i).getChildren().add(playerView.get(i));
                playerBoards.get(i).setMaxWidth(userWidthResolution-BOARDPANE_WIDTH*scale); //minimum
                playerBoards.get(i).setMinWidth(userWidthResolution/4);
            }

            userPlayerBoardWidth = playerView.get(0).getFitWidth();
            scalePB= userPlayerBoardWidth/ DEVELOPER_PLAYER_BOARD_WIDTH;
            playerBoardRenderer.setScalePB(scalePB);
            playerBoardRenderer.setPlayers(players);
            playerBoardRenderer.setClientModel(clientModel);
            playerBoardRenderer.setRenderInstruction(playerBoardRenderInstruction);


            //icons
            List<ImageView> icons = mapBoardRenderer.iconsRenderer();
            int column;
            int row;
            for(ClientModel.SimplePlayer p : players){
                if(p.getInGame() && p.getPosition() != null) {
                    column = mapBoardRenderer.columnFinder(p.getPosition());
                    row = mapBoardRenderer.rowFinder(p.getPosition());
                    roomsGrid.add(icons.get(players.indexOf(p)), column, row);
                }
            }
            
            //ammo
            List<GridPane> playerAmmoGrid = playerBoardRenderer.ammoRender();
            //damages
            List<GridPane> damageGrid = playerBoardRenderer.damagesRenderer();
            //marks
            List<GridPane> marksGrid = playerBoardRenderer.marksRenderer();
            //hand
            List<MenuButton> handButtons = playerBoardRenderer.handRenderer();
            //points
            GridPane pointGrid = playerBoardRenderer.pointsRenderer();
            //skullsPlayer
            List<Integer> deathsNumber = new ArrayList<>();
            for(ClientModel.SimplePlayer p : players){
                deathsNumber.add(p.getDeaths());
            }
            List<GridPane> skullGrid = playerBoardRenderer.skullsPlayerRenderer(deathsNumber);

            for(int i=0; i<players.size(); i++) {
                playerBoards.add(new StackPane());
                playerBoards.get(i).getChildren().addAll(playerAmmoGrid.get(i), damageGrid.get(i), marksGrid.get(i),
                        skullGrid.get(i), handButtons.get(i));
            }

            VBox playerSection = new VBox();

            StackPane playerBoardAndStuffAbove = new StackPane();
            playerBoardAndStuffAbove.getChildren().add(playerSection);
            for(GridPane g : playerAmmoGrid)
                g.setTranslateX(PLAYERAMMOGRID_TX*scalePB);

            //layout
            Pane mapAndStuffAbove = new Pane();
            mapAndStuffAbove.getChildren().addAll(map,skullsGrid,weaponGrid.get(0),weaponGrid.get(1),weaponGrid.get(2),roomsGrid);
            //decks
            mapAndStuffAbove = mapBoardRenderer.deckRenderer(mapAndStuffAbove);

            for(ClientModel.SimplePlayer p : players) {
                if (p.getId()!=clientModel.getPlayerID())
                    playerSection.getChildren().add(playerBoards.get(players.indexOf(p)));
                else {
                    playerBoards.get(players.indexOf(p)).getChildren().add(pointGrid);
                    mapAndStuffAbove.getChildren().add((playerBoards.get(players.indexOf(p))));//current player is added at the mapboard and translated at the bottom
                    playerBoards.get(players.indexOf(p)).setTranslateX(PLAYERBOARDS_TX*scale);
                    playerBoards.get(players.indexOf(p)).setTranslateY(PLAYERBOARDS_TY*scale);
                }
            }
            if( newHistoryMessage) {
                historyPanel = playerBoardRenderer.historyRenderer(historyMessage);
                newHistoryMessage = false;
            }
            if(historyPanel!=null)
                playerSection.getChildren().add(historyPanel);
            playerSection.getChildren().add(messagePanel);
            HBox board = new HBox();
            board.getChildren().addAll(mapAndStuffAbove, playerBoardAndStuffAbove);

            board.setStyle("-fx-background-color: #000000");
            scene.setRoot(board);
           if(mapBoardRenderer.getRenderNeeded()){
                mapBoardRenderer.setRenderNeeded(false);
                render();
            }
        });
    }

    /**
     * Called after the end of the game.
     * Keeps screen open for twenty seconds or until the user close the game.
     */
    private void closeAfterDisplay(){
        while (stage.isShowing()){
            try {
                Thread.sleep(FINAL_DISPLAY_TIME);
            }catch (InterruptedException e){

                LOGGER.log(Level.INFO, INTERRUPRING_METHOD_MESSAGE );
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     *Shows a screen when the player is disconnected
     */
      @Override
    public void displayDisconnection(){
        finalPrinter(DISCONNECTION_MSG);
        closeAfterDisplay();
    }

    /**
     *Shows a screen when the player is suspended
     */
    @Override
    public void displaySuspension(){
        finalPrinter(SUSPENSION_MSG);
        closeAfterDisplay();
    }

    /**
     *Shows the screen of the end of the game with the ranking
     */
    @Override
    public void displayEnd(String message){
        finalPrinter(message);
        closeAfterDisplay();
    }

    /**
     *Configures the message panel for disconnection, suspension and the end of the game
     */
    private void finalPrinter(String message){
        VBox messageBox = new VBox();
        messageBox.setAlignment(Pos.CENTER);
        messageBox.setSpacing(MESSAGEBOX_SPACING);

        Label onlyLabel = new Label(message);
        onlyLabel.setAlignment(Pos.CENTER);
        messageBox.getChildren().add(onlyLabel);

        Button exitButton = new Button(CLOSE);
        exitButton.setOnAction(e -> stage.close());
        exitButton.setAlignment(Pos.CENTER);
        messageBox.getChildren().add(exitButton);

        messageBox.setBackground(new Background(new BackgroundFill(color, null, null)));
        messagePanel = messageBox;

        if(clientModel==null){
            printer();
        } else {
            render();
        }
    }

    /**
     *
     */
    private ImageView getBoardOfPlayer(ClientModel.SimplePlayer player){
        String key;
        String playerColor;
        playerColor=player.getColor();
        switch (playerColor){
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

        InputStream playerFile = this.getClass().getResourceAsStream("/images/miscellaneous/"+key+".png");
        Image playerImage = new Image(playerFile);
        return new ImageView(playerImage);
    }



    /**
     *Removes the escape codes for the colored font from a string
     */
    private String removeEscapeCode(String type, String message){
        if (message.contains("0m")){
            if (type.equals(CHOOSE_POWERUP.toString())){
                message = message.replace("[31m", "Red ");
                message = message.replace("[33m", "Yellow ");
                message = message.replace("[34m", "Blue ");
            }
            else {
                message = message.replace("[31m", "");
                message = message.replace("[33m", "");
                message = message.replace("[34m", "");
            }
            message = message.replace("u001b", "");
            message = message.replace("[30m", "");
            message = message.replace("[32m", "");
            message = message.replace("[35m", "");
            message = message.replace("[36m", "");
            message = message.replace("[37m", "");
            message = message.replace("[0m", "");
            message = message.replace("", "");

        }
        return message;
    }

    /**
     * Called when a change in the game state is done to inform all the players ad keep trace of the previous moves
     *
     * @param message   description of a change in the game state
     */
    public void addHistory(String message){
        historyPanel = new VBox();
        historyMessage = message;
        newHistoryMessage = true;
    }

    public  void run(){
        //unnecessary
    }

    public void handle(Event event) {
        //unnecessary
    }

}