package it.polimi.ingsw.view;

//TODO: implement

import java.util.ArrayList;
import java.util.List;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.Scene;
import javafx.stage.Stage;

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

        Platform.runLater( () -> {

            stage = primaryStage;
            stage.setTitle("Adrenaline");
            BorderPane pane = new BorderPane();
            pane.setBackground(new Background(new BackgroundFill(color, null, null)));
            Scene scene = new Scene(pane, 500, 50);
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
            StackPane layout = new StackPane();
            layout.setBackground(new Background(new BackgroundFill(color, null, null)));
            layout.getChildren().add(label);
            Scene scene = new Scene(layout, 500, 500, color);
            Stage msgStage = new Stage();
            stage.setScene(scene);
            stage.show();

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

            VBox request = new VBox();
            request.setBackground(new Background(new BackgroundFill(color, null, null)));
            Label label = new Label(question + " (max " + maxLength + " characters)");
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
                        dataSaver.answer = (textField.getText());
                        dataSaver.message = question;
                        dataSaver.update = true;
                        reqStage.close();
                    }
            );

            Scene info = new Scene(request, 400, 300, color);
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

            VBox options = new VBox();
            options.setBackground(new Background(new BackgroundFill(color, null, null)));
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
                    System.out.println("OPT " + (buttons.indexOf(b)+1) + ": you clicked me!");
                    dataSaver.message = message;
                    dataSaver.answer = Integer.toString(buttons.indexOf(b) + 1);
                    dataSaver.update = true;
                    optStage.close();
                });
            }
            optionList.getChildren().addAll(buttons);
            options.getChildren().add(optionList);

            Scene scene = new Scene(options, 600,600, color);
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
    @Override
    public void render() {

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