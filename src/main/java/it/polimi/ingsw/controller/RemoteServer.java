package it.polimi.ingsw.controller;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteServer extends Remote {

    String getPlayerController() throws RemoteException;
}
