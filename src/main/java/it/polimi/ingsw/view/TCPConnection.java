package it.polimi.ingsw.view;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TCPConnection implements Connection {

    private ClientMain clientMain;
    private Socket socket;
    private PrintWriter out;

    public TCPConnection(ClientMain clientMain){
        this.clientMain = clientMain;
    }

    public void send(String message){
        out.println(message);
        out.flush();
    }

    public void connect() {
        try {
            System.out.println("Starting connection");
            socket = new Socket("localhost", 5000);
            System.out.println("Connected");
            Scanner in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream());
            Scanner stdin = new Scanner(System.in);
            while (true) {
                String message = in.nextLine();
                System.out.println("La connessione ha ricevuto un messaggio");
                clientMain.manage(message);
            }
        } catch (Exception ex) {
            return;
        }
    }
}