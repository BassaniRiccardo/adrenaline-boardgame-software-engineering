package it.polimi.ingsw.view;

import com.google.gson.*;
import it.polimi.ingsw.network.client.RMIConnection;
import it.polimi.ingsw.network.client.TCPConnection;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static it.polimi.ingsw.model.Updater.*;
import static it.polimi.ingsw.network.server.VirtualView.ChooseOptionsType.CHOOSE_STRING;

/**
/**
 * Main client class, containing most of the client's logic and structures
 *
 * @author  marcobaga
 */
public class ClientMain {

    private UI ui;
    private Runnable connection;
    private ExecutorService executor;
    private static final Logger LOGGER = Logger.getLogger("clientLogger");
    private ClientModel clientModel;
    private ClientUpdater clientUpdater;
    private boolean gameOver;

    /**
     * Constructor
     */
    public ClientMain() {
        executor = Executors.newCachedThreadPool();
        clientModel = null;
        clientUpdater = new ClientUpdater();
        gameOver = false;
    }

    /**
     * Main method, instantiates the class and initiates setup
     *
     * @param args arguments
     */
    public static void main(String[] args) {
        ClientMain clientMain = new ClientMain();
        clientMain.initializeLogger();
        clientMain.setup(args);
        //LOGGER.log(Level.INFO, "Setup finished");
    }

    /**
     * Initializes a logger for all the classes used by the client.
     */
    private void initializeLogger() {
        try {
            FileHandler fileHandler = new FileHandler("clientLog.txt");
            fileHandler.setLevel(Level.SEVERE);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "IOException thrown while creating logger", ex);
        }
        LOGGER.setLevel(Level.SEVERE);
    }

    /**
     * Loads server IP and port from args or client.properties
     */
    private Properties loadConfig(String[] args) {
        Properties prop = new Properties();

        if (args.length == 3) {     //test reading from args
            prop.put("serverIP", args[0]);
            prop.put("RMIPort", args[1]);
            prop.put("TCPPort", args[1]);
            prop.put("myIP", args[2]);
        } else {
            try{
                InputStream input = getClass().getResourceAsStream("/client.properties");
                prop.load(input);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Cannot load client config from file", ex);
            }
        }
        return prop;
    }

    /**
     * Method that initializes ui and connection as chosen by the user. Both are executed in a different thread.
     *
     * @param args the server's ip and port
     */
    private void setup(String[] args) {

        Properties prop = loadConfig(args);

        Scanner in = new Scanner(System.in);
        System.out.println("Client started. Which interface do you want to use (GUI/CLI)?");
        String buff = in.nextLine();
        while (!(buff.equals("GUI") || buff.equals("CLI"))) {
            System.out.println("Invalid choice. Try again.");
            buff = in.nextLine();
        }
        String selectedInterface;
        if(buff.equals("GUI")){
            new Thread() {
                @Override
                public void run() {
                    javafx.application.Application.launch(GUI.class);
                }
            }.start();
            ui = GUI.waitGUI();
            ((GUI)ui).setClientMain(this);
            selectedInterface = "GUI selected.";
        }else{
            ui = new CLI(this);
            selectedInterface = "CLI selected.";
        }
        executor.submit(ui);
        ui.display(selectedInterface);

        ui.display(CHOOSE_STRING.toString(), "Which type of connection do you want to use?\n(if unsure, choose 1)", new ArrayList<>(Arrays.asList("Socket", "RMI")));
        buff = ui.get(new ArrayList<>(Arrays.asList("Socket", "RMI")));
        while (!(buff.equals("1") || buff.equals("2"))) {
            ui.display("Invalid choice. Try again.");
            buff = ui.get(new ArrayList<>(Arrays.asList("Socket", "RMI")));
        }
        if (buff.equals("2")) {
            System.setProperty("java.rmi.server.hostname", prop.getProperty("myIP", "localhost"));
            connection = new RMIConnection(this, prop.getProperty("serverIP", "localhost"), Integer.parseInt(prop.getProperty("RMIPort", "3994")));
        } else {
            connection = new TCPConnection(this, prop.getProperty("serverIP", "localhost"), Integer.parseInt(prop.getProperty("TCPPort", "4198")));
        }
        executor.submit(connection);
    }

    /**
     * Prompts the UI to choose from a list of options
     * @param msg       message to be displayed
     * @param options   options available
     * @return          int corresponding to the option chosen
     */
    public int choose(String type, String msg, List<String> options) {
        ui.display(type, msg, options);
        return Integer.parseInt(ui.get(options));

    }

    /**
     * Calls UI display method
     * @param msg   message to display
     */
    public void display(String msg) {
        ui.display(msg);
    }

    /**
     * Prompts the UI for a string
     *
     * @param msg   message to display
     * @param max   maximum length accepted
     * @return      user's input
     */
    public String getInput(String msg, int max){
        ui.display(msg, Integer.toString(max));
        return ui.get(Integer.toString(max));
    }

    /**
     * Applies changes to the ClientModel according to the update message received and displays them
     * @param j     serialized update
     */
    public void update(JsonObject j) {

        LOGGER.log(Level.INFO, "Update received: " + j.get(TYPE_PROP).getAsString());
        clientUpdater.update(j, clientModel, this, ui);
    }

    /**
     * Getters and setters
     */

    public ClientModel getClientModel() {
        return clientModel;
    }

    public void setClientModel(ClientModel clientModel) {
        this.clientModel = clientModel;
    }

    public void showSuspension(){
        gameOver = true;
        executor.submit(() ->{
            ui.displaySuspension();
            System.exit(0);
        });
    }

    public void showDisconnection(){
        if(!gameOver) {
            ui.displayDisconnection();
            System.exit(0);
        }
    }

    public void showEnd(String message){
        gameOver = true;
        executor.submit(() ->{
            ui.displayEnd(message);
            System.exit(0);
        });
    }
}