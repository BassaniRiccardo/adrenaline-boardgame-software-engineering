package it.polimi.ingsw.network.client;

import com.google.gson.JsonObject;
import it.polimi.ingsw.view.ClientMain;
import it.polimi.ingsw.view.RequestFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;

/**
 * Implementation of Socket connection to server
 *
 * @author marcobaga
 */
public class TCPConnection extends Connection {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    /**
     * Constructor establishing a TCP connection
     *
     * @param clientMain        reference to the main class
     * @param address           IP to connect to
     * @param port              port to connect to
     */
    public TCPConnection(ClientMain clientMain, String address, int port){
        this.clientMain = clientMain;
        LOGGER.log(Level.INFO, "Starting TCP connection");
        try {
            socket = new Socket(address, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            socket.setSoTimeout(100);
            LOGGER.log(Level.INFO, "Connected to TCP server");
        }catch (ConnectException ex){
            LOGGER.log(Level.INFO, "Cannot connect to server. Closing");
            System.exit(0);
        }catch (IOException ex) {
            LOGGER.log(Level.INFO, "Cannot read or write to connection. Closing");
            clientMain.handleRequest(RequestFactory.toRequest("quit"));
            //try again?
        }
    }

    /**
     * Sends a message through the socket (NOT blocking)
     *
     * @param message       message to send
     */
    @Override
    public void send(String message){
        out.println(message);
        out.flush();
        LOGGER.log(Level.FINE, "Message sent to TCP server: {0}", message);
    }

    /**
     * Checks the socket input stream for new messages (NOT blocking)
     *
     * @return          the string received (empty if no message arrived)
     */
    @Override
    String receive(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("head", "PING");
        String message = jsonObject.toString();
        try {
            message = in.readLine();
            if (message == null) {
                LOGGER.log(Level.INFO, "TCPConnection: server disconnected, shutting down");
                clientMain.handleRequest(RequestFactory.toRequest("quit"));
            }
        }catch(SocketTimeoutException ex) {
            LOGGER.log(Level.FINEST, "No incoming message from TCPPlayerController", ex);
        }catch(IOException ex) {
            LOGGER.log(Level.INFO, "TCPConnection: server disconnected, shutting down");
            clientMain.handleRequest(RequestFactory.toRequest("quit"));
        }
        return message;
    }

    /**
     * Shuts down and cleans up
     */
    @Override
    public void shutdown() {
        try {
            socket.close();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "TCPConnection: socket was already closed");
        }
    }
}
