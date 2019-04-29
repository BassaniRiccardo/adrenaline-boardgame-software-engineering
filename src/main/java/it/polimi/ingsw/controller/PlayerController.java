package it.polimi.ingsw.controller;
import it.polimi.ingsw.model.Player;

//TODO: finish implementing
//it might be agood idea to hold a list of messages and a flag telling if they have been answered. maybe answer can hold the question's id?

public abstract class PlayerController{ //oggetto remoto
    GameEngine game;
    String playerName;
    boolean suspended;
    Player model;


    public void send(String in){
        //method to be called by GameController to ask the player to make a choice, providing the options
    }

    public String receive (){
        //method called by gamecontroller to retrieve the answer to said question
        return new String();
    }

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

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public boolean isSuspended() {
        return suspended;
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
