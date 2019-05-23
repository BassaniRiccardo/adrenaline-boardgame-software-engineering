package it.polimi.ingsw.view;

//TODO: implement

import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


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
    private Stage stage;
    DataSaver dataSaver = new DataSaver();
    private static final Logger LOGGER = Logger.getLogger("clientLogger");
    public static final CountDownLatch latch = new CountDownLatch(1);
    public static GUI GUI = null;

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

    public void setClientMain(ClientMain clientMain) { this.clientMain = clientMain; }


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

        stage = primaryStage;
        Platform.runLater( () -> {
            stage.setTitle("Adrenaline");
            BorderPane pane = new BorderPane();
            Scene scene = new Scene(pane, 500, 500);
            stage.setScene(scene);
            Label label = new Label("Entering the configuration phase...");
            pane.setCenter(label);

            stage.show();

        });

    }


    /**
     * Displays a MSG message
     *
     * @param message   message to be displayed
     */
    public void display(String message) {

        Platform.runLater( () -> {
            Label label = new Label(message);
            StackPane layout = new StackPane();
            layout.getChildren().add(label);
            Scene scene = new Scene(layout, 500, 500);
            Stage msgStage = new Stage();
            msgStage.setScene(scene);
            msgStage.show();

        });

    }


    /**
     * Displays a REQ message
     *
     * @param question       text to be displayed
     * @param maxLength      maximum length allowed for the answer
     */
    public void display(String question, String maxLength) {

        Platform.runLater( () -> {

            VBox request = new VBox();
            Label label = new Label(question);
            request.getChildren().add(label);

            TextField textField = new TextField();
            textField.setAlignment(Pos.CENTER);
            textField.setMaxSize(200, 50);
            request.getChildren().add(textField);

            Button requestButton = new Button("confirm");
            requestButton.setAlignment(Pos.CENTER);
            request.getChildren().add(requestButton);
            request.setAlignment(Pos.CENTER);
            request.setSpacing(40);
            Stage reqStage = new Stage();


            requestButton.setOnAction(e ->
                    {
                        System.out.println("REQ: you clicked me!");
                        dataSaver.requestAnswer = (textField.getText());
                        dataSaver.update = true;
                        reqStage.close();
                    }
            );

            Scene info = new Scene(request, 400, 300);
            reqStage.setScene(info);
            reqStage.show();

        });

    }


    /**
     * Displays a OPT message

     * @param message   message to be displayed
     * @param list      the option the user can choose among
     */
    public void display(String message, List<String> list) {

        Platform.runLater( () -> {

            VBox options = new VBox();
            Label label = new Label(message);
            options.getChildren().add(label);
            options.setAlignment(Pos.CENTER);
            options.setSpacing(40);

            HBox optionList = new HBox();
            optionList.setAlignment(Pos.CENTER);
            optionList.setSpacing(40 / list.size());
            Stage optStage = new Stage();

            List<Button> buttons = new ArrayList<>();
            for (String item : list) {
                Button b = new Button(item);
                buttons.add(b);
                b.setOnAction(e -> {
                    System.out.println("OPT " + buttons.indexOf(b) + ": you clicked me!");
                    dataSaver.requestAnswer = Integer.toString(buttons.indexOf(b));
                    dataSaver.update = true;
                    optStage.close();
                });
            }
            optionList.getChildren().addAll(buttons);
            options.getChildren().add(optionList);

            Scene scene = new Scene(options, 300,250);
            optStage.setScene(scene);

            optStage.show();

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
        dataSaver.update=false;
        return dataSaver.requestAnswer;

    }


    /**
     * Queries the user for input
     *
     * @param list      the list of option to choose among
     * @return          the user's input
     */
    public String get(List<String> list) {
        return get(Integer.toString(list.size()));
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
    @Override
    public void render() {

    }


    /**
     * Class storing the values the get() method must return.
     */
    class DataSaver{
        String requestAnswer;
        boolean update;
    }


}