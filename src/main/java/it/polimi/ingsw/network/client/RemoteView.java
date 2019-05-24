package it.polimi.ingsw.network.client;

import it.polimi.ingsw.view.ClientModel;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RemoteView extends Remote {


    int choose(String msg, List<String> options) throws RemoteException;

    void display(String msg) throws RemoteException;

    String getInput(String msg, int max) throws RemoteException;

    void updateModel(ClientModel clientModel) throws RemoteException;

    void ping() throws RemoteException;

}
