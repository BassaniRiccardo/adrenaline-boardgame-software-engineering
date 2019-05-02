package it.polimi.ingsw.controller;

import javax.print.attribute.standard.RequestingUserName;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPServer implements Runnable {

    private int port;
    private ServerMain serverMain;

    public TCPServer(int port, ServerMain serverMain){
        this.port = port;
        this.serverMain = serverMain;
    }


    //accepts connections, creates PlayerController
    public void run(){

        ExecutorService executor = Executors.newCachedThreadPool();
        ServerSocket serverSocket;
        try{
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return;
        }

        System.out.println("TCPServer ready");

        while (true){
            try{
                Socket socket = serverSocket.accept();
                executor.submit(new TCPPlayerController(socket));
                System.out.println("Accepted new connection, passed it to TCPPlayerController");
            } catch(IOException e) {
                break;
            }
        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
