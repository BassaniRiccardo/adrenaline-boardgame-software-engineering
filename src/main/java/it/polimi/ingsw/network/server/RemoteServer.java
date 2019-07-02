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

    /**
     * Provides the name to use to lookup a newly-built remote object the client can use to ping the server.
     *
     * @param view      the client requesting the remote object
     * @return          the String to use for lookup
     * @throws RemoteException      if connection problems subsist
     */
    String getPlayerController(RemoteView view) throws RemoteException;

}
