package it.polimi.ingsw.network.server;

import com.google.gson.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
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
    private boolean waiting;
    private String answer;

    /**
     * Constructor for TCPVirtualView.
     *
     * @param socket the socket of the virtual view.
     */
    public TCPVirtualView(Socket socket){
        super();
        this.socket = socket;
        this.waiting = false;
        this.answer = "default";
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
                    if (message.equals("PING")){
                        pinged=true;
                        lastPing = System.currentTimeMillis();
                        return;
                    }
                    if(!busy) return;
                    busy = false;
                    if(waiting){
                        answer = message;
                        waiting = false;
                        return;
                    }
                    if(timeout){
                        timeout = false;
                        if(System.currentTimeMillis()>timestamp) {
                            display("Your answer was too slow! Wait for the next prompt and be quick next time!");
                            return;
                        }
                    }
                    notifyObservers(message);
                }
            } catch (SocketTimeoutException ex) {
                //this is not significant as we expect to seldom receive messages
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Cannot reach client", ex);
                suspend();
            }
            if(pinged&&System.currentTimeMillis()-lastPing>PING_TIMEOUT_MILLIS){
                suspend();
            }
        }
    }


    /**
     * Closes the connection to the client and the separate thread.
     */
    @Override
    public void shutdown(){
        try {
            socket.close();
        }catch (IOException ex){
            LOGGER.log(Level.SEVERE, "Error while closing connection", ex);
        }
    }


    /**
     * Commands the client to show the suspension message and eventually shutdown.
     */
    @Override
    public void showSuspension(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("head", "SUSP");
        send(jsonObject);
    }


    /**
     * Commands the client to show an ending mesage and eventually shutdown.
     *
     * @param message       the message to display
     */
    @Override
    public void showEnd(String message){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("head", "END");
        jsonObject.addProperty("msg", message);
        send(jsonObject);
    }


    /**
     * Class sending a message to the client asking him to choose among a list of options.
     *
     * @param type      type of the request
     * @param msg       message to be displayed
     * @param options   list of options to choose from
     */
    @Override
    public void choose(String type, String msg, List<?> options){
        try {
            synchronized (game.getNotifications()){
                game.getNotifications().remove(this);
            }
        }catch(NullPointerException ex){
            LOGGER.log(Level.SEVERE, "No old notifications to remove", ex);
        }
        if(busy||suspended){
            return;
        }
        busy = true;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("head", "OPT");
        jsonObject.addProperty("type", type);
        jsonObject.addProperty("text", msg);

        JsonArray array = new JsonArray();
        for (Object o : options) {
            array.add(new JsonPrimitive(o.toString()));
        }
        jsonObject.add("options", array);

        send(jsonObject);
    }


    /**
     * Class sending a message to the client asking him to choose among a list of options.
     * Saves a timestamp so that when the answer is received, it can be discarded if late.
     *
     * @param type      type of the request
     * @param msg       message to display
     * @param options   list of options to choose from
     * @param timeoutSec    maximum time given to the client to provide an answer
     */
    @Override
    public void choose(String type, String msg, List<?> options, int timeoutSec){
        choose(type, msg, options);
        if(suspended||busy) return;
        timeout = true;
        timestamp = timeoutSec*1000 + System.currentTimeMillis();
    }


    /**
     * Queries the client to choose from a list of options. Only to be called before this VirtualView is
     * referenced by a GameEngine.
     *
     * @param type      the request's type
     * @param msg       message to display
     * @param options   options to choose from
     * @return          the client's answer
     */
    @Override
    public int chooseNow(String type, String msg, List<?> options){
        choose(type, msg, options);
        if(suspended||busy) return 1;
        waiting = true;
        return Integer.parseInt(receive());
    }


    /**
     * Sends a message for the client to display
     *
     * @param msg       message to display
     */
    @Override
    public void display(String msg){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("head", "MSG");
        jsonObject.addProperty("text", msg);
        send(jsonObject);
    }


    /**
     * Queries the client for input. Only to be called before this VirtualView is referred by a GameEngine.
     *
     * @param msg       message to display
     * @param max       max length of the answer
     * @return          client's answer
     */
    @Override
    public String getInputNow(String msg, int max){
        if(busy||suspended) return "";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("head", "REQ");
        jsonObject.addProperty("text", msg);
        jsonObject.addProperty("length", String.valueOf(max));
        send(jsonObject);
        busy = true;
        waiting = true;
        return receive();
    }


    /**
     * Commands the client to update its model or to render his UI.
     *
     * @param jsonObject    encoded update
     */
    @Override
    public void update (JsonObject jsonObject){
        send(jsonObject);
    }


    /**
     * Receives a message from the client (BLOCKING)
     *
     * @return              the message received
     */
    private String receive() {
        while(waiting){
            refresh();
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException ex) {
                LOGGER.log(Level.INFO, "Skipped waiting time.");
                Thread.currentThread().interrupt();
            }
        }
        return answer;
    }

    /**
     * Sends a message serialized as a JsonObject through the socket
     *
     * @param jmessage  message to send
     */
    private void send (JsonObject jmessage){
        if(suspended) return;
        out.println(jmessage.toString());
        out.flush();
        LOGGER.log(Level.FINE, "Sending a message over TCP connection");
    }
}