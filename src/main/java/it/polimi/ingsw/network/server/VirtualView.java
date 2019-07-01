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

    public enum ChooseOptionsType{

        CHOOSE_WEAPON, CHOOSE_POWERUP, CHOOSE_SQUARE, CHOOSE_PLAYER, CHOOSE_STRING;

        @Override
        public String toString() {
            return super.toString().toLowerCase().substring("CHOOSE_".length());
        }
    }

    protected GameEngine game;
    protected String name;
    boolean suspended;
    boolean justSuspended;
    private Player model;
    static final Logger LOGGER = Logger.getLogger("serverLogger");
    private static final int MAX_LENGTH_BATTLECRY = 32;
    boolean busy;
    boolean timeout;
    long timestamp;
    private String battlecry;

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
        name = getInputNow(playersAlreadyConnected+"\nSelect a name.", 16);
        battlecry = getInputNow("Now, choose your battlecry!", MAX_LENGTH_BATTLECRY);
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
        display("Name accepted. Waiting for the game to start...");
    }

    /**
     * Checks for connection with client and forwards messages
     */
    public abstract void refresh();

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

    public abstract void shutdown();

    public abstract void showSuspension();

    public abstract void showEnd(String message);

    /**
     * Suspends the player. This causes the client to shutdown, but the VirtualView is kept alive untile the player
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
     * Asks a player to choose one among options (sends a request and returns immediately)
     *
     * @param msg       message to be displayed
     * @param options   list of options to choose from
     */
    public abstract void choose(String type, String msg, List<?> options);

    public abstract void choose(String type, String msg, List<?> options, int timeoutSec);

    /**
     * Displays a message to the player
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
     * Asks a player to choose one among options and waits for an answer. Only for use during login procedures.
     * @param msg       message to display
     * @param options   options to choose from
     * @return          the player's choice as the index of the list of options
     */
    public abstract int chooseNow(String type, String msg, List<?> options);

    /**
     * Sends a request for an update to the client
     *
     * @param jsonObject    encoded update
     */
    public abstract void update(JsonObject jsonObject);

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
