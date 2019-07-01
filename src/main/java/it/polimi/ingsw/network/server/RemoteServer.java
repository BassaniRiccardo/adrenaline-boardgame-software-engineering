package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.client.RemoteView;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface for RMIServer implementation (serverside). Refer to RMIServer for the description of methods.
 *
 * @author marcobaga
 */
public interface RemoteServer extends Remote {

    String getPlayerController(RemoteView view) throws RemoteException;

}
