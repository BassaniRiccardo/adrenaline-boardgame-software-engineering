package it.polimi.ingsw.network.client;

import it.polimi.ingsw.view.ClientMain;
import it.polimi.ingsw.view.RequestFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.logging.Level;

//TODO: in-depth testing
//implement reaction to server disconnect

public class TCPConnection extends Connection {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public TCPConnection(ClientMain clientMain, String address, int port){
        this.clientMain = clientMain;
        LOGGER.log(Level.INFO, "Starting TCP connection");
        try {
            socket = new Socket(address, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            LOGGER.log(Level.INFO, "Connected to TCP server");
        }catch (ConnectException ex){
            LOGGER.log(Level.INFO, "Cannot connect to server. Closing");
            clientMain.handleRequest(RequestFactory.toRequest("quit"));
        }catch (IOException ex) {
            LOGGER.log(Level.INFO, "Cannot read or write to connection. Closing");
            clientMain.handleRequest(RequestFactory.toRequest("quit"));
            //try again?
        }
    }

    @Override
    public void send(String message){
        out.println(message);
        out.flush();
        LOGGER.log(Level.FINE, "Message sent to TCP server: {0}", message);
    }


    @Override
    String receive(){
        String message = "";
        try {
            if (in.ready()) {
                message = in.readLine();
            }
        }catch(IOException ex) {
            LOGGER.log(Level.INFO, "TCPConnection: server disconnected, shutting down");
            clientMain.handleRequest(RequestFactory.toRequest("quit"));
        }
        return message;
    }

    @Override
    public void shutdown() {
        try {
            socket.close();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "TCPConnection: socket was already closed");
        }
    }
}
