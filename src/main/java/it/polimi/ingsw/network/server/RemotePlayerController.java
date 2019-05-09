package it.polimi.ingsw.network.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemotePlayerController extends Remote{

    void answer(String message) throws RemoteException;
    String getMessage() throws RemoteException;
    void ping() throws RemoteException;
}