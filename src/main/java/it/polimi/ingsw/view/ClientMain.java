package it.polimi.ingsw.view;

import it.polimi.ingsw.network.Connection;
import it.polimi.ingsw.network.RMIConnection;
import it.polimi.ingsw.network.TCPConnection;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        clientMain.setup();
        System.out.println("Setup finished");
    }

    /**
     * Method that initializes ui and connection as chosen by the user. Both are executed in a different thread.
     */
    private void setup(){
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
            connection = new RMIConnection(this);
            ui.display("RMI selezionata.");
        } else {
            connection = new TCPConnection(this);
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

}