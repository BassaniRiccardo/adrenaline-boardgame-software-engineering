package it.polimi.ingsw.network.server;
import com.google.gson.JsonObject;
import it.polimi.ingsw.controller.GameEngine;
import it.polimi.ingsw.controller.ServerMain;
import it.polimi.ingsw.model.board.Player;
import it.polimi.ingsw.model.exceptions.SlowAnswerException;
import it.polimi.ingsw.view.ClientModel;

import java.util.ArrayList;
import java.util.Arrays;
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

public abstract class VirtualView implements Runnable{

    public enum ChooseOptionsType{

        CHOOSE_WEAPON, CHOOSE_POWERUP, CHOOSE_SQUARE, CHOOSE_PLAYER, CHOOSE_STRING;

        @Override
        public String toString() {
            return super.toString().toLowerCase().substring("CHOOSE_".length());
        }
    }

    /*
    public static final String CHOOSE_WEAPON = "weapon";
    public static final String CHOOSE_POWERUP = "powerup";
    public static final String CHOOSE_SQUARE = "square";
    public static final String CHOOSE_PLAYER = "player";
    public static final String CHOOSE_STRING = "string";
    */


    protected GameEngine game;
    protected String name;
    boolean suspended;
    boolean justSuspended;
    private Player model;
    static final Logger LOGGER = Logger.getLogger("serverLogger");
    boolean busy;
    boolean timeout;
    long timestamp;

    VirtualView(){
        this.game = null;
        this.name = null;
        this.suspended = false;
        this.justSuspended = false;
        this.model = null;
        this.busy = false;
        this.timeout = false;
        this.timestamp = 0;
    }

    /**
     * Manages login communication with the client
     */
    public void run(){

        String playersAlreadyConnected = ServerMain.getInstance().getAlreadyConnected();
        name = getInputNow(playersAlreadyConnected+"\nSelect a name.", 16);
        LOGGER.log(Level.INFO, "Login procedure initiated for {0}", name);

        while(!ServerMain.getInstance().login(this)){
            if(ServerMain.getInstance().canResume(name)){
                int ans = chooseNow(ChooseOptionsType.CHOOSE_STRING.toString(), "Do you want to resume?", Arrays.asList("yes", "no"));

                if(ans==1) {
                    if(ServerMain.getInstance().resume(this)){
                        break;
                    } else {
                        display("Somebody already resumed.");
                    }
                }
            }
            playersAlreadyConnected = ServerMain.getInstance().getAlreadyConnected();
            name=getInputNow("Name already taken. Try another one.\n"+playersAlreadyConnected, 16);
        }
        display("Name accepted. Waiting for the voting to start...");
    }

    /**
     * Checks for connection with client and forwards messages
     */
    abstract public void refresh();

    /**
     * Getters and Setters
     */

    public String getName() {
        return name;
    }

    public GameEngine getGame() { return game;  }

    public void setGame(GameEngine game){this.game = game;}

    public Player getModel() {return model; }

    public boolean isSuspended() {
        return suspended;
    }

    public void setPlayer(Player model) {
        this.model = model;
    }

    public boolean isJustSuspended(){
        return justSuspended;
    }

    public void setJustSuspended(boolean justSuspended){
        this.justSuspended = justSuspended;
    }

    public void setSuspended(boolean suspended){
        this.suspended = suspended;
    }

    //only for testing
    public void setName(String name) {
        this.name = name;
    }

    public abstract void shutdown();

    public abstract void showSuspension();

    public abstract void showEnd(String message);

    /**
     * Suspends related player
     */
    public void suspend() {
        showSuspension();
        shutdown();
        busy=false;
        if(!suspended) {
            this.suspended = true;
            this.justSuspended = true;
            LOGGER.log(Level.INFO, "Player {0} was suspended", name);
        }
    }

    /**
     * Asks a player to choose one among options (sends a request and returns immediately)
     *
     * @param msg       message to be displayed
     * @param options   list of options to choose from
     */
    abstract public void choose(String type, String msg, List<?> options);

    abstract public void choose(String type, String msg, List<?> options, int timeoutSec);

    /**
     * Displays a message to the player
     *
     * @param msg       message to display
     */
    abstract public void display(String msg);

    /**
     * Fetches input, but waits for an answer
     *
     * @param msg       message to display
     * @param max       max length of the answer
     * @return          the answer
     */
    abstract public String getInputNow(String msg, int max);


    /**
     * Asks a player to choose one among options and waits for an answer
     * @param msg       message to display
     * @param options   options to choose from
     * @return          the player's choice as the index of the list of options
     */
    abstract int chooseNow(String type, String msg, List<?> options);

    /**
     * Sends a request for an update to the client
     *
     * @param jsonObject    encoded update
     */
    abstract public void update(JsonObject jsonObject);

    public void notifyObservers(String ans){
        if(game!=null) {
            game.notify(this, ans);
        }
    }

    @Override
    public String toString() {
        return name + " connection";
    }

}
