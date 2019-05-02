package it.polimi.ingsw.view;

import java.util.Scanner;

public class ClientMain {

    private UI userInterface;
    private Connection connection;

    public static void main(String[] args){

        ClientMain cm = new ClientMain();

        Scanner in = new Scanner(System.in);
        System.out.println("Client avviato. Che interfaccia grafica vuoi utilizzare (GUI/CLI)?");
        String buff = in.nextLine();
        if(buff=="GUI"){
            cm.userInterface = new GUI();
            System.out.println("GUI selezionata.");

        }
        else{
            cm.userInterface = new CLI();
            System.out.println("CLI selezionata.");
        }

        System.out.println("Che tipo di connessione vuoi utilizzare? (Socket/RMI)");     //sostituito da userInterface.showMessage()
        buff = in.nextLine();       //sostituito da userInterface.getInput()
        if(buff=="RMI") {
            //
        } else {
            cm.connection = new TCPConnection(cm);
        }
        cm.connection.connect();
    }


    public void manage(String message){
        System.out.println("ClientMain ha ricevuto un messaggio");
        System.out.println(message);
        Scanner in = new Scanner(System.in);
        connection.send(in.nextLine());
        System.out.println("ClientMain ha inoltrato il messaggio alla connessione");
    }
}
