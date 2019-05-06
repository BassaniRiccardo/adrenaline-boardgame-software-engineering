package it.polimi.ingsw.controller;
import it.polimi.ingsw.model.Player;

//TODO: finish implementing
//it might be agood idea to hold a list of messages and a flag telling if they have been answered. maybe answer can hold the question's id?

public abstract class PlayerController implements Runnable{ //oggetto remoto
    GameEngine game;
    String name;
    boolean suspended;
    Player model;

    public void run(){}

    public void refresh(){}

    public void send(String in){}

    public String receive (){
        return null;
    }


    public String getName() {
        return name;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void suspend() {
        this.suspended = true;
        ServerMain.getInstance().removeIfWaiting(this);
        System.out.println("Player " + name + " was suspended");
    }
}
