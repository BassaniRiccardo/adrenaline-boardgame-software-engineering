package it.polimi.ingsw.network.server;

import com.google.gson.JsonObject;
import it.polimi.ingsw.controller.ServerMain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;

/**
 * Implementation of PlayerController communicating through a socket
 *
 * @author marcobaga
 */
public class TCPPlayerController extends PlayerController{

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public TCPPlayerController(Socket socket){
        super();
        this.socket = socket;
    }

   /**
     * Initializes the connection (this happens on a separate thread from the main one so that the server can manage multiple requests in short time)
     */
    @Override
    public void run (){
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            socket.setSoTimeout(100);
            super.run();
        }catch(Exception ex){
            LOGGER.log(Level.SEVERE, "Cannot create TCPPlayerController", ex);
            suspend();
        }
    }

    /**
     * Forwards the messages through the connection and receives them. Detects disconnected clients and allows for non-blocking send and receive methods.
     */
    @Override
    public synchronized void refresh() {
        if(!suspended) {
            try {
                String message = in.readLine();
                if (message == null) {
                    suspend();
                } else {
                    LOGGER.log(Level.FINE, "Received a message over TCP connection");
                    incoming.add(message);
                }
            } catch (SocketTimeoutException ex) {
                LOGGER.log(Level.FINEST, "No incoming message from TCPPlayerController", ex);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Cannot reach client", ex);
                suspend();
            }
            if (!outgoing.isEmpty()) {
                out.println(outgoing.remove(0));
                out.flush();
                LOGGER.log(Level.FINE, "Sending a message over TCP connection");
            }
        }
    }

    @Override
    public void send(JsonObject encodedMessage) { }


}