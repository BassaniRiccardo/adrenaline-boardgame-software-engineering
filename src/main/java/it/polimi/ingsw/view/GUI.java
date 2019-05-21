package it.polimi.ingsw.view;

//TODO: implement

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * Graphical user interface for I/O operations
 *
 * @author  marcobaga
 */

public class GUI extends Application implements UI, Runnable, EventHandler {

    //niente hardcode, tenere presente le dimensioni della finestra
    //i file png o jpeg delle carte vanno nella cartella resources (come i json)

    private ClientMain clientMain;
    private Stage stage = null;
    private String requestAnswer;

    private static final Logger LOGGER = Logger.getLogger("clientLogger");

    public void setClientMain(ClientMain clientMain) { this.clientMain = clientMain; }


    /*public static void main(String[] args){
        launch(args);
    }*/

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Adrenaline");
        displayRequest("che connessione vuoi usare?");
        //displayRequest("Come vuoi chiamarti?");
        //displayOptions("Che mappa vorresti?", new ArrayList<>(Arrays.asList("1","2","3","4")));
    }


    /**
     * Displays content
     * @param message   message to be displayed
     */
    @Override
    public void displayMessage(String message) {

        Label label = new Label(message);
        StackPane layout = new StackPane();
        layout.getChildren().add(label);
        Scene scene = new Scene(layout, 500, 500);
        stage.setScene(scene);
        stage.show();

    }


    @Override
    public void displayRequest(String request) {

        BorderPane layout = new BorderPane();

        HBox question = new HBox();
        Label label = new Label(request);
        question.getChildren().add(label);
        question.autosize();
        layout.setTop(question);

        HBox answer = new HBox();
        TextField textField = new TextField();
        textField.setAlignment(Pos.CENTER);
        textField.setMaxSize(200, 50);
        answer.getChildren().add(textField);
        answer.autosize();
        layout.setCenter(answer);

        HBox confirm = new HBox();
        Button requestButton = new Button("confirm");
        requestButton.setAlignment(Pos.CENTER);
        confirm.getChildren().add(requestButton);
        confirm.autosize();
        layout.setBottom(confirm);

        requestAnswer = "";
        requestButton.setOnAction(e ->
        {
            requestAnswer = textField.getText();
            //get();
        });

        Scene info = new Scene(layout, 400, 300);
        stage.setScene(info);
        stage.show();


    }


    @Override
    public void displayOptions(String message, List<String> list) {

        BorderPane layout = new BorderPane();

        HBox text = new HBox();
        Label label = new Label(message);
        text.getChildren().add(label);
        layout.setTop(text);

        HBox options = new HBox();
        List<Button> buttons = new ArrayList<>();
        for (String item : list){
            Button b = new Button(item);
            buttons.add(b);
        }
        options.getChildren().addAll(buttons);
        layout.setCenter(options);

        Scene scene = new Scene(layout, 300,250);
        stage.setScene(scene);
        stage.show();

    }


    /**
     * Queries the user for input
     * @return      the user's input
     */
    @Override
    public String get() {
        System.out.println(requestAnswer);
        return requestAnswer;
    }

    @Override
    public String get(List<String> list) {

        return "";
    }

    /**
     * Main GUI loop
     */
    public void run(){
        start(null);
    }

    public void displayMessage(List<String> list){

    }

    @Override
    public void handle(Event event) {

    }

}