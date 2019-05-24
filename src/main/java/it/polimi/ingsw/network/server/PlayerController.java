package it.polimi.ingsw.network.server;
import com.google.gson.JsonObject;
import it.polimi.ingsw.controller.GameEngine;
import it.polimi.ingsw.controller.ServerMain;
import it.polimi.ingsw.model.board.Player;
import it.polimi.ingsw.model.exceptions.SlowAnswerException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract class responsible for the connection between to clients.
 *
 * @author marcobaga
 */

public abstract class PlayerController implements Runnable{
    private GameEngine game;
    protected String name;
    boolean suspended;
    private Player model;
    static final Logger LOGGER = Logger.getLogger("serverLogger");
    List<String> incoming;
    List<String> outgoing;

    PlayerController(){
        this.game = null;
        this.name = null;
        this.suspended = false;
        this.model = null;
        this.incoming = new ArrayList<>();
        this.outgoing = new ArrayList<>();
    }

    /**
     * Manages login communication with the client
     */
    public void run(){
        sendReq("Select a name", 16);
        LOGGER.log(Level.FINE, "Name request sent");
        name = receive();
        LOGGER.log(Level.INFO, "Login procedure initiated for {0}", name);

        while(!ServerMain.getInstance().login(name, this)){
            if(ServerMain.getInstance().canResume(name)){
                sendReq("Do you want to resume?", 4);
                String ans = receive();
                if(ans.equals("yes")) {
                    if(ServerMain.getInstance().resume(name, this)){
                        break;
                    } else {
                        send("Somebody already resumed.");
                    }
                }
            }
            sendReq("Name already taken. Try another one", 16);
            name = receive();
        }
        send("Name accepted.");
    }

    /**
     * Checks for connection with client and forwards messages
     */
    public void refresh(){}

    /**
     * Sends a message through the connection to the client
     *
     * @param in            the message to be sent
     */
    public void send(String in){
        LOGGER.log(Level.FINE, "Message added to outgoing: {0}", in);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("head", "MSG");
        jsonObject.addProperty("message", in);
        outgoing.add(jsonObject.toString());
        refresh();
    }

    private void sendReq(String in, int length){
        LOGGER.log(Level.FINE, "Message added to outgoing: {0}", in);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("head", "REQ");
        jsonObject.addProperty("message", in);
        jsonObject.addProperty("length", length);
        outgoing.add(jsonObject.toString());
        refresh();
    }

    /**
     * Receives a message from the client (BLOCKING)
     *
     * @return              the message received
     */
    public String receive() {
        while(incoming.isEmpty()){
            try {
                refresh();
                TimeUnit.MILLISECONDS.sleep(100);
            }catch(InterruptedException ex){
                LOGGER.log(Level.INFO,"Skipped waiting time.");
                Thread.currentThread().interrupt();
            }
        }
        String message = incoming.remove(0);
        LOGGER.log(Level.FINE, "Message received: {0}", message);
        return message;
    }

    public String receive(int timeout) throws SlowAnswerException{
        int i = 0;
        while(incoming.isEmpty()){
            try {
                refresh();
                TimeUnit.MILLISECONDS.sleep(100);
                i++;
                if(i>10*timeout){
                    throw new SlowAnswerException("Player took more than "+ timeout + " seconds to answer");
                }
            }catch(InterruptedException ex){
                LOGGER.log(Level.INFO,"Skipped waiting time.");
                Thread.currentThread().interrupt();
            }
        }
        String message = incoming.remove(0);
        LOGGER.log(Level.FINE, "Message received: {0}", message);
        return message;
    }


    /**
     * Returns a random number between 1 and max.
     * To be substituted with a method that returns the user choice.
     *
     * @param max           the number of options.
     * @param timeout       the time given to make a choice.
     * @return              the user choice (now a random value).
     *//*
    public int receive (int max, int timeout){
        //method called by game controller to retrieve the answer to said question
        int ans = (1 + (new Random()).nextInt(max));
        return ans;
        //something like this???
        //return Integer.parseInt(incoming.get(incoming.size()-1)) + 1;
    }*/

    /**
     * Getters and Setters
     */

    public String getName() {
        return name;
    }

    public GameEngine getGame() { return game;  }

    public Player getModel() {return model; }

    public boolean isSuspended() {
        return suspended;
    }

    public void setPlayer(Player model) {
        this.model = model;
    }

    public void suspend() {
        this.suspended = true;
        LOGGER.log(Level.INFO,"Player {0} was suspended", name);
    }

    public void send (JsonObject jmessage){
        LOGGER.log(Level.FINE, "JMessage added to outgoing: {0}", jmessage.get("head").getAsString());
        outgoing.add(jmessage.toString());
        refresh();
    }
}
