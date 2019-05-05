package it.polimi.ingsw.controller;
import it.polimi.ingsw.model.Player;

import java.util.List;
import java.util.Random;

/**
 * Abstract class responsible for the connection between server and client.
 */

//TODO: finish implementing
// it might be agood idea to hold a list of messages and a flag telling if they have been answered. Maybe answer can hold the question's id?

public abstract class PlayerController{ //oggetto remoto

    private GameEngine game;
    private String playerName;
    private boolean suspended;
    private Player player;


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

    //?????
    public String receive (int timeout){
        //method called by gamecontroller to retrieve the answer to said question
        //needs to check if the answer is valid ( or maybe input validation can be left to the client so that not more than 1 message need to be sent
        return new String();
    }


    //these next methods are remote and can be called by the client to retrieve the qeustions and provide answers
    public String getQuestion(){        //remote method
        return new String();
    }

    public void setAnswer(String answer){   }


    public String getName() {
        return playerName;
    }


    public GameEngine getGame() { return game;  }

    public String getPlayerName() {return playerName; }

    public Player getPlayer() {return player; }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void suspend() {
        this.suspended = true;
    }

    public void unsuspend() {
        this.suspended = false;
    }

    public void sendModel(){
        //this method is used for updating the client's view
    }

}
