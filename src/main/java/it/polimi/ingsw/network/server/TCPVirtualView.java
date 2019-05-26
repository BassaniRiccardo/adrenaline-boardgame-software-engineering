package it.polimi.ingsw.network.server;

import com.google.gson.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Implementation of VirtualView communicating through a socket
 *
 * @author marcobaga
 */
public class TCPVirtualView extends VirtualView {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private List<String> incoming;
    private List<String> outgoing;


    public TCPVirtualView(Socket socket){
        super();
        this.socket = socket;
        this.incoming = new ArrayList<>();
        this.outgoing = new ArrayList<>();
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
            LOGGER.log(Level.SEVERE, "Cannot create TCPVirtualView", ex);
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
                    notifyObservers(message);
                }
            } catch (SocketTimeoutException ex) {
                LOGGER.log(Level.FINEST, "No incoming message from TCPVirtualView", ex);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Cannot reach client", ex);
                suspend();
            }
        }
    }

    public void choose(String msg, List<?> options){

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("head", "OPT");
        jsonObject.addProperty("text", msg);

        JsonArray array = new JsonArray();
        for (Object o : options) {
            array.add(new JsonPrimitive(o.toString()));
        }
        jsonObject.add("options", array);

        send(jsonObject);
    }


    public int chooseNow(String msg, List<?> options){
        choose(msg, options);
        return Integer.parseInt(receive());
    }

    public void display(String msg){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("head", "MSG");
        jsonObject.addProperty("text", msg);
        send(jsonObject);
    }

    public void getInput(String msg, int max){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("head", "REQ");
        jsonObject.addProperty("text", msg);
        jsonObject.addProperty("length", String.valueOf(max));
        send(jsonObject);
    }

    public String getInputNow(String msg, int max){
        getInput(msg, max);
        return receive();
    }

    public void notifyObservers(String ans){
        if(game!=null) {
            game.notify(this, ans);
        }
    }

    /**
     * Receives a message from the client (BLOCKING)
     *
     * @return              the message received
     */
    private String receive() {
        while (true) {
            try {
                String message = in.readLine();
                if (message == null) {
                    suspend();
                } else {
                    LOGGER.log(Level.FINE, "Received a message over TCP connection");
                    return message;
                }
            } catch (SocketTimeoutException ex) {
                LOGGER.log(Level.FINEST, "No incoming message from TCPVirtualView", ex);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Cannot reach client", ex);
                suspend();
            }
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException ex) {
                LOGGER.log(Level.INFO, "Skipped waiting time.");
                Thread.currentThread().interrupt();
            }
        }
    }

    private void send (JsonObject jmessage){
        //game.getNotifications().remove(this);
        out.println(jmessage.toString());
        out.flush();
        LOGGER.log(Level.FINE, "Sending a message over TCP connection");    }

    public void update (JsonObject jsonObject){
        System.out.println("updateTCPVirtualView");
        send(jsonObject);
    }
}