package it.polimi.ingsw.network.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface for RMI communication (serverside)
 *
 * @author marcobaga
 */
public interface RemoteController extends Remote{

    void ping() throws RemoteException;
}