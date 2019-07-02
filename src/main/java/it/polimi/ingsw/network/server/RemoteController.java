package it.polimi.ingsw.network.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface for RMI communication (serverside). Refer to RMIVirtualView for the description of methods.
 *
 * @author marcobaga
 */
public interface RemoteController extends Remote{

    /**
     * Method called by the client to check connection status
     */
    void ping() throws RemoteException;
}