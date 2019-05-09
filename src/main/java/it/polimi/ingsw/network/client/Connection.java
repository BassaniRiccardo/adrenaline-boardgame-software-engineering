package it.polimi.ingsw.network.client;

import it.polimi.ingsw.view.ClientMain;
import it.polimi.ingsw.view.RequestFactory;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Interface for connecting to the server
 * @author marcobaga
 */
public abstract class Connection implements Runnable{

    ClientMain clientMain;
    static final Logger LOGGER = Logger.getLogger("clientLogger");

    public void run(){
        while(Thread.currentThread().isAlive()){
            String message = receive();
            System.out.println("loop");
            if(!message.equals("")) {
                LOGGER.log(Level.INFO,"Received message: " + message);
                clientMain.handleRequest(RequestFactory.toRequest(message));
            }
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException ex) {
                LOGGER.log(Level.SEVERE, "Skipped waiting time");
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Sends a message to the server
     *
     * @param message       message to send
     */
    public abstract void send(String message);

    /**
     * Closes the connection and cleans up
     */
    public abstract void shutdown();

    abstract String receive();
}