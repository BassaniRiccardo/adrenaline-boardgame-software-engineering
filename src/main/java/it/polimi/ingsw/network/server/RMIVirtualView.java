package it.polimi.ingsw.network.server;

import com.google.gson.JsonObject;
import it.polimi.ingsw.network.client.RemoteView;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Class extending VirtualView and managing lower level issues related to the connection with a specific client.
 * This class handles a single choose request at a time, ignoring other ones arriving in the meantime.
 * Calls to other methods can be asynchronous.
 *
 * @author marcobaga
 */
public class RMIVirtualView extends VirtualView implements RemoteController {


    private RemoteView remoteView;
    private ExecutorService executor = Executors.newCachedThreadPool();


    /**
     * Standard constructor
     */
    RMIVirtualView(RemoteView remoteView){
        super();
        this.remoteView = remoteView;
    }


    /**
     * This method is periodically called by ServerMain and checks if it is possible to call the client's remote functions.
     */
    @Override
    public void refresh(){
        if(!isSuspended()){
            try{
                remoteView.ping();
            }catch (RemoteException ex){
                suspend();
            }
        }
    }


    /**
     * Closes the connection to the client and the separate thread.
     */
    @Override
    public void shutdown(){
        executor.shutdownNow();
        try {
            UnicastRemoteObject.unexportObject(this, false);
        }catch(NoSuchObjectException ex){
            LOGGER.log(Level.SEVERE, "Could not unexport RemoteController", ex);
        }
    }


    /**
     * Method called by the client to check connection status
     */
    @Override
    public void ping(){
        //empty because it only needs to be called to survey connection status
    }


    /**
     * Commands the client to show the suspension message and eventually shutdown.
     */
    @Override
    public void showSuspension(){
        try{
            remoteView.showSuspension();
        }catch(RemoteException ex){
            LOGGER.log(Level.SEVERE, "Unable to send disconnection message", ex);
        }
    }

    /**
     * Commands the client to show an ending mesage and eventually shutdown.
     *
     * @param message       the message to display
     */
    @Override
    public void showEnd(String message){
        try{
            remoteView.showEnd(message);
        }catch(RemoteException ex){
            LOGGER.log(Level.SEVERE, "Unable to send disconnection message", ex);
        }
    }


    /**
     * Class calling a remote client function to let him choose among a list of options.
     * A separate thread is needed not to block the caller of this function (usually the TurnManager).
     *
     * @param type      type of the request
     * @param msg       message to be displayed
     * @param options   list of options to choose from
     */
    @Override
    public void choose(String type, String msg, List<?> options){
        if(busy) return;
        busy=true;
        synchronized (game.getNotifications()) {
            game.getNotifications().remove(this);
        }
        executor.submit(
            ()-> {
                try {
                    int i = remoteView.choose(type, msg, options.stream().map(x -> (x).toString()).collect(Collectors.toList()));
                    if(busy) {
                        notifyObservers(String.valueOf(i));
                    }
                    busy=false;
                } catch (RemoteException ex) {
                    suspend();
                }
            }
        );
    }

    /**
     * Class calling a remote client function to let him choose among a list of options.
     * A separate thread is needed not to block the caller of this function (usually the TurnManager).
     * The client's answer is ignored after a maximum time.
     *
     * @param type      type of the request
     * @param msg       message to display
     * @param options   list of options to choose from
     * @param timeoutSec    maximum time given to the client to provide an answer
     */
    @Override
    public void choose(String type, String msg, List<?> options, int timeoutSec){
        if(busy) return;
        busy=true;
        long timestamp = System.currentTimeMillis() + timeoutSec*1000;
        synchronized (game.getNotifications()) {
            game.getNotifications().remove(this);
        }
        executor.submit(
                ()-> {
                    try {
                        int i = remoteView.choose(type, msg, options.stream().map(x -> (x).toString()).collect(Collectors.toList()));
                        if(busy&&System.currentTimeMillis()<timestamp) {
                            notifyObservers(String.valueOf(i));
                        } else {
                            display("Your answer was too slow! Wait for the next prompt and be quick next time!");
                        }
                        busy=false;
                    } catch (RemoteException ex) {
                        suspend();
                    }
                }
        );
    }


    /**
     * Queries the client to choose from a list of options. Only to be called before this VirtualView is
     * referenced by a GameEngine.
     *
     * @param type      the request's type
     * @param msg       message to display
     * @param options   options to choose from
     * @return          the client's answer
     */
    @Override
    public int chooseNow(String type, String msg, List<?> options){
        try {
            return remoteView.choose(type, msg, options.stream().map(x -> (x).toString()).collect(Collectors.toList()));
        }catch(RemoteException ex){
            suspend();
        }
        return 0;
    }


    /**
     * Sends a message for the client to display
     *
     * @param msg       message to display
     */
    @Override
    public void display(String msg){
        try{
            remoteView.display(msg);
        }catch(RemoteException ex){
            //not necessary to suspend the player in this case
            LOGGER.log(Level.SEVERE, "Unable to call remote function", ex);
        }
    }


    /**
     * Queries the client for input. Only to be called before this VirtualView is referred by a GameEngine.
     *
     * @param msg       message to display
     * @param max       max length of the answer
     * @return          client's answer
     */
    @Override
    public String getInputNow(String msg, int max) {
        try {
            return remoteView.getInput(msg, max);
        } catch (RemoteException ex) {
            suspend();
        }
        return "";
    }


    /**
     * Commands the client to update its model or to render his UI.
     *
     * @param jsonObject    encoded update
     */
    @Override
    public void update(JsonObject jsonObject){
        try {
            remoteView.update(jsonObject.toString());
        } catch (RemoteException ex) {
            LOGGER.log(Level.SEVERE, "Error while updating", ex);
            suspend();
        }
    }


}
