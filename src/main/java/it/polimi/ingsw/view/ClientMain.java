package it.polimi.ingsw.view;

import com.google.gson.*;
import it.polimi.ingsw.network.client.RMIConnection;
import it.polimi.ingsw.network.client.TCPConnection;

import java.io.FileInputStream;
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
 * Main client class, containing most of the client's logic and structures. It handles requests and messages
 * coming from the server and received by TCPConnection or RMIConnection by updating the ClientModel (through
 * ClientUpdater) or by selecting what the UI should show. It can query the UI for user input.
 *
 * @author  marcobaga
 */
public class ClientMain {

    private static final String CHOOSE_UI_MSG = "Client started. Which interface do you want to use (GUI/CLI)?";
    private static final String INVALID_CHOICE_MSG = "Invalid choice. Try again.";
    private static final String CHOOSE_CONNECTION_MSG = "Which type of connection do you want to use?\n(if unsure, choose 1)";
    private static final String SELECTED_MSG = " selected.";
    private static final String ERROR_GUI = "GUI not available, rerun the client and try again. If the problem persists select CLI";

    private UI ui;
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
     * @param args  command line arguments
     */
    public static void main(String[] args) {
        ClientMain clientMain = new ClientMain();
        clientMain.initializeLogger();
        clientMain.setup(args);
        LOGGER.log(Level.INFO, "Setup finished");
    }

    /**
     * Initializes a logger for all the classes instantiated in the client.
     */
    private void initializeLogger() {
        try {
            FileHandler fileHandler = new FileHandler("clientLog.txt");
            fileHandler.setLevel(Level.SEVERE);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Exception thrown while creating logger", ex);
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
            try (InputStream input = new FileInputStream("client.properties")) {
                prop.load(input);
                return prop;
            } catch (IOException ex) {
                //LOGGER.log(Level.SEVERE, "Cannot load client config from file", ex);
            }
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
     * Initializes UI and connection as chosen by the user.
     *
     * @param args  the server's ip and port
     */
    private void setup(String[] args) {

        Properties prop = loadConfig(args);
        Runnable connection;

        Scanner in = new Scanner(System.in);
        System.out.println(CHOOSE_UI_MSG);
        String buff = in.nextLine();
        while (!(buff.equalsIgnoreCase("GUI") || buff.equalsIgnoreCase("CLI"))) {
            System.out.println(INVALID_CHOICE_MSG);
            buff = in.nextLine();
        }
        if(buff.equals("GUI")){
            new Thread() {
                @Override
                public void run() {
                    javafx.application.Application.launch(GUI.class);
                }
            }.start();
            try {
                ui = GUI.waitGUI();
            } catch (InterruptedException e) {
                System.out.println(ERROR_GUI);
                Thread.currentThread().interrupt();
                System.exit(0);
            }
            ((GUI)ui).setClientMain(this);
        }else{
            ui = new CLI(this);
        }
        executor.submit(ui);
        ui.display(buff.toUpperCase() + SELECTED_MSG);

        ui.display(CHOOSE_STRING.toString(), CHOOSE_CONNECTION_MSG, new ArrayList<>(Arrays.asList("Socket", "RMI")));
        buff = ui.get(new ArrayList<>(Arrays.asList("Socket", "RMI")));
        while (!(buff.equals("1") || buff.equals("2"))) {
            ui.display(INVALID_CHOICE_MSG);
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
     * Prompts the UI to choose from a list of options through two separate calls to UI functions.
     * The first call displays the request, while the second returns the user's input.
     *
     * @param msg       message to display
     * @param options   options available
     * @return          int corresponding to the option chosen
     */
    public int choose(String type, String msg, List<String> options) {
        ui.display(type, msg, options);
        return Integer.parseInt(ui.get(options));

    }

    /**
     * Displays a message to the user
     *
     * @param msg   message to display
     */
    public void display(String msg) {
        ui.display(msg);
    }

    /**
     * Retrieves a String from the user by calling two UI methods: the first one displays the request
     * while the second one retrieves the input.
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
     * Passes an update message to the ClientUpdater which will apply and possibly display it
     *
     * @param j     serialized update
     */
    public void update(JsonObject j) {
        LOGGER.log(Level.INFO, "Update received: " + j.get(TYPE_PROP).getAsString());
        clientUpdater.update(j, clientModel, this, ui);
    }

    /*
     * Getters and setters
     */

    public ClientModel getClientModel() {
        return clientModel;
    }

    void setClientModel(ClientModel clientModel) {
        this.clientModel = clientModel;
    }

    /**
     * Closes the game when the player gets suspended. Execution on a parallel thread is needed to return without
     * delays.
     */
    public void showSuspension(){
        gameOver = true;
        executor.submit(() ->{
            ui.displaySuspension();
            System.exit(0);
        });
    }

    /**
     * Closes the game when connection with the server is lost.
     */
    public void showDisconnection(){
        if(!gameOver) {
            ui.displayDisconnection();
            System.exit(0);
        }
    }

    /**
     * Closes the game when it is over.
     *
     * @param message   information about the game's result
     */
    public void showEnd(String message){
        gameOver = true;
        executor.submit(() ->{
            ui.displayEnd(message);
            System.exit(0);
        });
    }
}