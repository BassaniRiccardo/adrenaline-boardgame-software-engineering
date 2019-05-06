package it.polimi.ingsw.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class TCPConnection implements Connection {

    private ClientMain clientMain;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public TCPConnection(ClientMain clientMain){
        this.clientMain = clientMain;
        System.out.println("Starting connection");
        try {
            socket = new Socket("localhost", 5000);
            System.out.println("Connected");
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
        }catch (ConnectException ex){
            System.out.println("Cannot connect to server. Closing.");
            System.exit(0);
        }catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void send(String message){
        out.println(message);
        out.flush();
        System.out.println("Answer sent by connection.");
    }


    @Override
    public void run() {

        while(Thread.currentThread().isAlive())
        try{
            if (in.ready()) {
                String message = in.readLine();
                if(!message.equals("heartbeat")) {
                    System.out.println("Receiving a message from the connection.");
                    clientMain.handleRequest(RequestFactory.toRequest(message));
                }
            }
        }catch(IOException ex){}
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        }catch(InterruptedException ex){ex.printStackTrace();Thread.currentThread().interrupt();};
    }

    public void shutdown(){
    try {
        socket.close();
    }catch(Exception ex){}
    }
}
