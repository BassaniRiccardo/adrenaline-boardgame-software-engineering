package it.polimi.ingsw.controller;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteInterface extends Remote {
    void print() throws RemoteException;
}
