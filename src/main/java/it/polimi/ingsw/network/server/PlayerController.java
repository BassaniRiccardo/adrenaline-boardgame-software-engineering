package it.polimi.ingsw.network.server;
import it.polimi.ingsw.controller.GameEngine;
import it.polimi.ingsw.controller.ServerMain;
import it.polimi.ingsw.model.board.Player;

import java.util.List;
import java.util.Random;

/**
 * Abstract class responsible for the connection between server and client.
 */

//TODO: finish implementing
// it might be agood idea to hold a list of messages and a flag telling if they have been answered. Maybe answer can hold the question's id?

public abstract class PlayerController implements Runnable{ //oggetto remoto
    GameEngine game;
    String name;
    boolean suspended;
    Player model;

    public void run(){}

    public void refresh(){}

    public void send(String in){}

    public String receive () {
        return null;
    }

    /**
     * Sends a message to the client containing a question and some options.
     *
     * @param message           the text of the question.
     * @param options           the available options.
     */
    public void send(String message, List<String> options){
        //method to be called by GameController to ask the player to make a choice, providing the options.
        System.out.println(message + "\n\n");
        for (String s: options){
            System.out.println(s + "\t");
        }
        System.out.println("\n\n");

    }

    /**
     * Returns a random number between 1 and max.
     * To be substituted with a method that returns the user choice.
     *
     * @param max           the number of options.
     * @param timeout       the time given to make a choice.
     * @return              the user choice (now a random value).
     */
    public int receive (int max, int timeout){
        //method called by game controller to retrieve the answer to said question
        return (1 + (new Random()).nextInt(max));
    }

    //these next methods are remote and can be called by the client to retrieve the qeustions and provide answers
    public String getQuestion(){        //remote method
        return new String();
    }

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
        ServerMain.getInstance().removeIfWaiting(this);
        System.out.println("Player " + name + " was suspended");
    }

}
