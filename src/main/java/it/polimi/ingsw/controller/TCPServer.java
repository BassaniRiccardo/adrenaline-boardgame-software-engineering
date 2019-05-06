package it.polimi.ingsw.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPServer implements Runnable {

    private int port;
    private ServerMain serverMain;
    private boolean running;

    public TCPServer(int port, ServerMain serverMain){
        this.port = port;
        this.serverMain = serverMain;
        this.running = false;
    }

    //accepts connections, creates PlayerController
    public void run(){

        running = true;
        ExecutorService executor = Executors.newCachedThreadPool();
        ServerSocket serverSocket;
        try{
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.println("TCPServer ready");

        while (running){
            try{
                Socket socket = serverSocket.accept();
                executor.submit(new TCPPlayerController(socket));
                System.out.println("Accepted new connection, passed it to TCPPlayerController");
            } catch(IOException e) {
                break;
            }
        }
        System.out.println("TCPServer shutting down");
    }

    public int getPort() {
        return port;
    }

    public void shutdown(){ this.running = false;}

}
