package it.polimi.ingsw.network.client;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Remote interface for RMI communication (clientside). Refer to RMIConnection for the description of methods.
 *
 * @author marcobaga
 */
public interface RemoteView extends Remote {

    int choose(String type, String msg, List<String> options) throws RemoteException;

    void display(String msg) throws RemoteException;

    String getInput(String msg, int max) throws RemoteException;

    void update(String json) throws RemoteException;

    void ping() throws RemoteException;

    void showEnd(String message) throws RemoteException;

    void showSuspension() throws RemoteException;

}
