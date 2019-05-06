package it.polimi.ingsw.view;

public interface Connection extends Runnable{

    void send(String message);
    void shutdown();
}
