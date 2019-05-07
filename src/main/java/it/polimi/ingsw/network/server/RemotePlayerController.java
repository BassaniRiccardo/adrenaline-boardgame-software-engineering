package it.polimi.ingsw.network.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

//TODO: extend Unreferenced

public interface RemotePlayerController extends Remote{

    void answer(String message) throws RemoteException;
    String getMessage() throws RemoteException;
}