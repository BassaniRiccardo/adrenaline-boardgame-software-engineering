package it.polimi.ingsw.network.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface for RMIServer implementation
 *
 * @author marcobaga
 */
public interface RemoteServer extends Remote {

    String getPlayerController() throws RemoteException;
}
