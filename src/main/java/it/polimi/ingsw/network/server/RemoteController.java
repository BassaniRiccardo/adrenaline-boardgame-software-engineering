package it.polimi.ingsw.network.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface for RMI communication
 *
 * @author marcobaga
 */
public interface RemoteController extends Remote{

    void notifyObservers(String msg) throws RemoteException;

    void ping() throws RemoteException;
}