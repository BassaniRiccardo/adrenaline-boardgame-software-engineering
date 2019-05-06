package it.polimi.ingsw.controller;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.Unreferenced;

public interface RemotePlayerController extends Remote, Unreferenced {

    void answer(String message) throws RemoteException;
    String getMessage() throws RemoteException;

}