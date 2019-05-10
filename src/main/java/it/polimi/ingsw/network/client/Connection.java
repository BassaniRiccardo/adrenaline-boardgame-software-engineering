package it.polimi.ingsw.network.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.ingsw.view.ClientMain;
import it.polimi.ingsw.view.RequestFactory;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Interface for connecting to the server
 *
 * @author marcobaga
 */
public abstract class Connection implements Runnable{

    ClientMain clientMain;
    static final Logger LOGGER = Logger.getLogger("clientLogger");

    /**
     * Main loop for checking incoming messages and passing them to other classes
     */
    public void run(){
        JsonParser jsonParser = new JsonParser();
        while(Thread.currentThread().isAlive()){
            String message = receive();
            try {
                JsonObject jMessage = (JsonObject) jsonParser.parse(message);
                if(!jMessage.get("head").getAsString().equals("PING")) {
                    LOGGER.log(Level.INFO,"Received message: " + jMessage.get("head").getAsString());
                    clientMain.handleRequest(RequestFactory.toRequest(jMessage));
                }
            }catch(Exception ex){
                LOGGER.log(Level.SEVERE, "Received string cannot be parsed to Json", ex);
                JsonObject jMessage = new JsonObject();
                jMessage.addProperty("head", "MALFORMED");
                clientMain.handleRequest(RequestFactory.toRequest(jMessage));
            }
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException ex) {
                LOGGER.log(Level.SEVERE, "Skipped waiting time");
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Sends a message to the server
     *
     * @param message       message to send
     */
    public abstract void send(String message);

    /**
     * Closes the connection and cleans up
     */
    public abstract void shutdown();

    /**
     * Hides the implementation of methods to receive messages from server
     *
     * @return          message received
     */
    abstract String receive();
}