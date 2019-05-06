package it.polimi.ingsw.network;

public interface Connection extends Runnable{

    void send(String message);
    void shutdown();
}
