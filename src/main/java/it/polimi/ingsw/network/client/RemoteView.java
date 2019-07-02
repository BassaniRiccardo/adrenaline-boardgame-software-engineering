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


    /**
     * Asks clientMain to carry out a decision
     *
     * @param type      the type of options
     * @param msg       message to display
     * @param options   options between which to choose
     * @return          int corresponding to the choice
     * @throws RemoteException according to RMI principles
     */
    int choose(String type, String msg, List<String> options) throws RemoteException;


    /**
     * Asks clientMain to display a message
     *
     * @param msg           message to display
     * @throws RemoteException according to RMI principles
     */
    void display(String msg) throws RemoteException;


    /**
     * Asks clientMain to provide a String
     *
     * @param msg       message to display
     * @param max       max length of the answer
     * @return          clientMain's response
     * @throws RemoteException according to RMI principles
     */
    String getInput(String msg, int max) throws RemoteException;


    /**
     * Remote method to be called by the server to assert the state of the connection
     * @throws RemoteException according to RMI principles
     */
    void ping() throws RemoteException;


    /**
     * Passes an update to clientMain
     *
     * @param jsonObject        encoded update
     * @throws RemoteException according to RMI principles
     */
    void update(String jsonObject) throws RemoteException;


    /**
     * Asks the ClientMain to show the suspension screen and eventually terminate.
     *
     * @throws RemoteException according to RMI principles
     */
    void showSuspension() throws RemoteException;


    /**
     * Asks the ClientMain to show the end screen and eventually terminate
     *
     * @param message           message to display at the end
     * @throws RemoteException according to RMI principles
     */
    void showEnd(String message) throws RemoteException;



}
