package it.polimi.ingsw.network.server;

import com.google.gson.JsonObject;
import it.polimi.ingsw.controller.GameEngine;
import it.polimi.ingsw.controller.ServerMain;
import it.polimi.ingsw.model.board.Player;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract class responsible for the connection to clients. Hides the variety of underlying implementations
 * and the network connection to the GameEngine. Can handle only one choose request at a time and ignores
 * all other requests in the meantime. Display commands are asynchronous and carried out at any time.
 * This class is observed by the Controller (GameEngine) by default and can be registered as an observer
 * to the Model (Board).
 *
 * @author marcobaga
 */

public abstract class VirtualView implements Runnable{

    /**
     * An enumeration for the option types.
     */
    public enum ChooseOptionsType{

        CHOOSE_WEAPON, CHOOSE_POWERUP, CHOOSE_SQUARE, CHOOSE_PLAYER, CHOOSE_STRING;

        /**
         * Returns a string representing the option type.
         *
         * @return a string representing the option type.
         */
        @Override
        public String toString() {
            return super.toString().toLowerCase().substring("CHOOSE_".length());
        }
    }

    protected GameEngine game;
    protected String name;
    boolean suspended;
    private boolean justSuspended;
    private Player model;
    static final Logger LOGGER = Logger.getLogger("serverLogger");
    private static final int MAX_LENGTH_BATTLECRY = 32;
    boolean busy;
    boolean timeout;
    long timestamp;
    private String battlecry;
    private static final String CHOOSE_NAME = "Select a name.";
    private static final String MALFORMED_NAME = "Your name should not be empty or contain commas. Try again.";
    private static final String CHOOSE_BATTLECRY = "Now, choose your battlecry!";
    private static final String CHOOSE_RESUME = "Do you want to resume?";
    private static final String ALREADY_RESUMED = "Somebody already resumed with your name.";
    private static final String ALREADY_TAKEN = "Name already taken. Try another one.";
    private static final String NAME_ACCEPTED = "Name accepted. About to join the game...";

    public VirtualView(){
        this.game = null;
        this.name = "";
        this.suspended = false;
        this.justSuspended = false;
        this.model = null;
        this.busy = false;
        this.timeout = false;
        this.timestamp = 0;
        this.battlecry = "";
    }

    /**
     * Manages the login procedure with the client. A VirtualView requires a separate thread until the player has
     * logged in to guarantee parallel login attempts.
     */
    public void run(){

        String playersAlreadyConnected = ServerMain.getInstance().getAlreadyConnected();
        name = getInputNow(playersAlreadyConnected+CHOOSE_NAME, 16);
        while(name.contains(",")||name.isEmpty()){
            name = getInputNow(playersAlreadyConnected+MALFORMED_NAME, 16);
        }
        battlecry = getInputNow(CHOOSE_BATTLECRY, MAX_LENGTH_BATTLECRY);
        LOGGER.log(Level.INFO, "Login procedure initiated for {0}", name);

        while(!ServerMain.getInstance().login(this)){
            if(ServerMain.getInstance().canResume(name)){
                int ans = chooseNow(ChooseOptionsType.CHOOSE_STRING.toString(), CHOOSE_RESUME, Arrays.asList("Yes", "No"));

                if(ans==1) {
                    if(ServerMain.getInstance().resume(this)){
                        break;
                    } else {
                        display(ALREADY_RESUMED);
                    }
                }
            }
            playersAlreadyConnected = ServerMain.getInstance().getAlreadyConnected();
            name=getInputNow(playersAlreadyConnected + ALREADY_TAKEN, 16);
            while(name.contains(",")||name.isEmpty()){
                name = getInputNow(playersAlreadyConnected+MALFORMED_NAME, 16);
            }
        }
        display(NAME_ACCEPTED);
    }

    //Getters and Setters

    public String getName() {
        return name;
    }

    public String getBattlecry(){ return battlecry; }

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

    public void setName(String name) {
        this.name = name;
    }


    /**
     * Checks for connection with client and forwards messages
     */
    public abstract void refresh();


    /**
     * Closes the connection to the client and the separate thread.
     */
    public abstract void shutdown();


    /**
     * Commands the client to show the suspension message and eventually shutdown.
     */
    public abstract void showSuspension();


    /**
     * Commands the client to show an ending message and eventually shutdown.
     *
     * @param message       the message to display
     */
    public abstract void showEnd(String message);


    /**
     * Suspends the player. This causes the client to shutdown, but the VirtualView is kept alive until the player
     * resumes or the game ends.
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
     * Class sending a message to the client asking him to choose among a list of options.
     *
     * @param type      type of the request
     * @param msg       message to be displayed
     * @param options   list of options to choose from
     */
    public abstract void choose(String type, String msg, List<?> options);


    /**
     * Class sending a message to the client asking him to choose among a list of options.
     * Saves a timestamp so that when the answer is received, it can be discarded if late.
     *
     * @param type      type of the request
     * @param msg       message to display
     * @param options   list of options to choose from
     * @param timeoutSec    maximum time given to the client to provide an answer
     */
    public abstract void choose(String type, String msg, List<?> options, int timeoutSec);


    /**
     * Queries the client to choose from a list of options. Only to be called before this VirtualView is
     * referenced by a GameEngine.
     *
     * @param type      the request's type
     * @param msg       message to display
     * @param options   options to choose from
     * @return          the client's answer
     */
    public abstract int chooseNow(String type, String msg, List<?> options);


    /**
     * Sends a message for the client to display
     *
     * @param msg       message to display
     */
    public abstract void display(String msg);

    /**
     * Fetches input, but waits for an answer. Only for use during login procedures.
     *
     * @param msg       message to display
     * @param max       max length of the answer
     * @return          the answer
     */
    public abstract String getInputNow(String msg, int max);


    /**
     * Sends a request for an update to the client
     *
     * @param jsonObject    encoded update
     */
    public abstract void update(JsonObject jsonObject);


    /**
     * Notifies the GameEngine of messages received from the client. The GameEngine is forced as an observer and does not
     * need to subscribe.
     *
     * @param ans   message received from the client
     */
    protected void notifyObservers(String ans){
        if(game!=null) {
            game.notify(this, ans);
        }
    }

    @Override
    public String toString() {
        return name + " connection";
    }

}
