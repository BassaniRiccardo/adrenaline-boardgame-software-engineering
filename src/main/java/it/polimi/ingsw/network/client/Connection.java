package it.polimi.ingsw.network.client;

/**
 * Interface for connecting to the server
 * @author marcobaga
 */
public interface Connection extends Runnable{

    /**
     * Sends a message to the server
     *
     * @param message       message to send
     */
    void send(String message);

    /**
     * Closes the connection and cleans up
     */
    void shutdown();
}
