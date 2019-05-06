package it.polimi.ingsw.view;

import it.polimi.ingsw.network.Connection;
import it.polimi.ingsw.network.RMIConnection;
import it.polimi.ingsw.network.TCPConnection;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientMain {

    private UI ui;
    private Connection connection;
    private ExecutorService executor;
    //requests alter the player model. calling setters of the model generates a list of operations to revert the changes. if "reset move" is caleld, those changes are applied
    //once the player confirms its move, updates are sent as a request to all other clients

    private ClientMain(){
        executor = Executors.newCachedThreadPool();
    }

    public static void main(String[] args) {
        ClientMain clientMain = new ClientMain();
        clientMain.setup();
        System.out.println("Setup finished");
    }

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

    public void handleRequest(Request request){
        request.manage(this, ui, connection);
    }

}