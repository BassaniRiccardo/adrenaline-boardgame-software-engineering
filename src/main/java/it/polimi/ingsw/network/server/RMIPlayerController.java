package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.ServerMain;
import java.rmi.RemoteException;

/**
 * Class responsible for communication with a client connected through a socket
 *
 * @author marcobaga
 */
public class RMIPlayerController extends PlayerController implements RemotePlayerController{

//TODO:  an RMI player cannot try logging again if he picks a blocked name, but not always [BUG]
//check instances are not garbage collected
// implement shutdown

    private long timeStamp;
    private final int timeout;

    /**
     * Standard constructor
     */
    RMIPlayerController(){
        super();
        this.timeout = Integer.parseInt(ServerMain.loadConfig().getProperty("RMITimeoutMillis", "2000"));
        this.timeStamp = System.currentTimeMillis();
    }

    /**
     * Remote method called by the client to provide answers to the server's messages
     *
     * @param message           message sent by the client
     * @throws RemoteException
     */
    @Override
    public void answer(String message) throws RemoteException {
        incoming.add(message);
    }

    /**
     * Remote method called by the client to collect messages from the server
     * @return                  messages collected by the client
     * @throws RemoteException
     */
    @Override
    public String getMessage() throws RemoteException {
        if(outgoing.isEmpty()){
            return "";
        }
        return outgoing.remove(0);
    }

    /**
     * Suspends the player if inactive for more than a value in millis read from server.properties (default 2000).
     * This method is periodically called by ServerMain
     */
    @Override
    public void refresh(){
        if(!suspended&&System.currentTimeMillis() > timeStamp + timeout){
            suspend();
        }
    }

    /**
     * Remote method called by the client to confirm his connection
     */
    @Override
    public void ping() throws RemoteException{
        timeStamp = System.currentTimeMillis();
    }
}
