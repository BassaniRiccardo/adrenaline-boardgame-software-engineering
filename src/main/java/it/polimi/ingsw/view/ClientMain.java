package it.polimi.ingsw.view;

import javafx.application.Application;
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

    /**
     * Constructor
     */
    public ClientMain(){
        executor = Executors.newCachedThreadPool();clientModel = null;
    }

    /**
     * Main method, instantiates the class and initiates setup
     *
     * @param args    arguments
     */
    public static void main(String[] args) {
        ClientMain clientMain = new ClientMain();
        clientMain.initializeLogger();
        clientMain.setup(args);
        LOGGER.log(Level.INFO, "Setup finished");
    }

    /**
     * Initializes a logger for all the classes used by the client.
     */
    private void initializeLogger(){
        try {
            FileHandler fileHandler = new FileHandler("clientLog.txt");
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
        }catch (IOException ex){LOGGER.log(Level.SEVERE, "IOException thrown while creating logger", ex);}
        LOGGER.setLevel(Level.ALL);
    }

    /**
     * Loads server IP and port from args or client.properties
     */
    private Properties loadConfig(String[] args) {
        Properties prop = new Properties();

        if (args.length == 2) {     //test reading from args
            prop.put("serverIP", args[0]);
            prop.put("RMIPort", args[1]);
            prop.put("TCPPort", args[1]);
        } else {
            try (InputStream input = new FileInputStream("client.properties")) {
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
     * @param args      the server's ip and port
     */
    private void setup(String[] args){

        Properties prop = loadConfig(args);

        Scanner in = new Scanner(System.in);
        System.out.println("Client avviato. Che interfaccia grafica vuoi utilizzare (GUI/CLI)?");
        String buff = in.nextLine();
        while(!(buff.equals("GUI")||buff.equals("CLI"))){
            System.out.println("Scelta non valida. Riprovare.");
            buff = in.nextLine();
        }
        if(buff.equals("GUI")){
            new Thread() {
                @Override
                public void run() {
                    javafx.application.Application.launch(GUI.class);
                }
            }.start();
            GUI gui = GUI.waitGUI();
            ui = gui;
            ((GUI) ui).setClientMain(this);
            ui.display("GUI selezionata.");
        }
        else{
            ui = new CLI(this);
            ui.display("CLI selezionata.");
        }
        executor.submit(ui);

        ui.display("Che tipo di connessione vuoi utilizzare?", new ArrayList<>(Arrays.asList("Socket", "RMI")));
        buff = ui.get(new ArrayList<>(Arrays.asList("Socket", "RMI")));
        while(!(buff.equals("1")|| buff.equals("2"))){
            ui.display("Scelta non valida. Riprovare.");
            buff = ui.get(new ArrayList<>(Arrays.asList("Socket", "RMI")));
        }
        if(buff.equals("2")){
            connection = new RMIConnection(this, prop.getProperty("serverIP", "localhost"), Integer.parseInt(prop.getProperty("RMIPort", "1420")));
            ui.display("RMI selezionata.");
        } else {
            connection = new TCPConnection(this, prop.getProperty("serverIP", "localhost"), Integer.parseInt(prop.getProperty("TCPPort", "5000")));
            ui.display("Socket selezionata.");
        }
        executor.submit(connection);
    }


    public int choose(String msg, List<String> options){
        ui.display(msg, options);
        return Integer.parseInt(ui.get(options));
    }

    public void display(String msg){
        ui.display(msg);

    }

    public String getInput(String msg, int max){
        System.out.println(msg);
        return ui.get();
    }

    public ClientModel getClientModel(){
        return clientModel;
    }

    public void setClientModel(ClientModel clientModel){ this.clientModel = clientModel;}

    public void shutdown(){
        //graceful shutdown
        System.exit(0);
    }

}