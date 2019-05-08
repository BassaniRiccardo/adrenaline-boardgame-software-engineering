package it.polimi.ingsw.view;

import it.polimi.ingsw.network.client.Connection;
import it.polimi.ingsw.network.client.RMIConnection;
import it.polimi.ingsw.network.client.TCPConnection;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

//TODO: test
/**
 * Main client class, containing most of the client's logic and structures
 *
 * @author  marcobaga
 */
public class ClientMain {

    private UI ui;
    private Connection connection;
    private ExecutorService executor;
    private static final Logger LOGGER = Logger.getLogger("clientLogger");
    //requests alter the player model. calling setters of the model generates a list of operations to revert the changes. if "reset move" is caleld, those changes are applied
    //once the player confirms its move, updates are sent as a request to all other clients

    /**
     * Constructor
     */
    private ClientMain(){
        executor = Executors.newCachedThreadPool();
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
        System.out.println("Setup finished");
    }

    /**
     * Method that initializes ui and connection as chosen by the user. Both are executed in a different thread.
     */
    private void setup(String[] args){

        ////////////////
        Properties prop = new Properties();

        if(args.length==2){     //test reading from args
            prop.put("serverIP", args[0]);
            prop.put("RMIPort", args[1]);
            prop.put("TCPPort", args[1]);
        }else {
            try (InputStream input = new FileInputStream("client.properties")) {
                prop.load(input);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
/////////////////////////////


        Scanner in = new Scanner(System.in);
        System.out.println("Client avviato. Che interfaccia grafica vuoi utilizzare (GUI/CLI)?");
        String buff = in.nextLine();
        if(buff.equals("GUI")){
            ui = new GUI(this);
            ui.display("GUI selezionata.");
        }
        else{
            ui = new CLI(this);
            ui.display("CLI selezionata.");
        }
        executor.submit(ui);

        ui.display("Che tipo di connessione vuoi utilizzare? (Socket/RMI)");
        buff = ui.get();
        if(buff.equals("RMI")){
            connection = new RMIConnection(this, prop.getProperty("serverIP", "localhost"), Integer.parseInt(prop.getProperty("RMIPort", "1420")));
            ui.display("RMI selezionata.");
        } else {
            connection = new TCPConnection(this, prop.getProperty("serverIP", "localhost"), Integer.parseInt(prop.getProperty("TCPPort", "5000")));

            ui.display("Socket selezionata.");
        }
        executor.submit(connection);
    }

    /**
     * Wrapper class for request handling
     *
     * @param request    request to be managed
     */
    public void handleRequest(Request request){
        request.manage(this, ui, connection);
    }

    public void initializeLogger(){
        try {
            FileHandler FILEHANDLER = new FileHandler("clientLog.txt");
            FILEHANDLER.setLevel(Level.ALL);
            FILEHANDLER.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(FILEHANDLER);
        }catch (IOException ex){LOGGER.log(Level.SEVERE, "IOException thrown while creating logger", ex);}
        LOGGER.setLevel(Level.ALL);
    }
}