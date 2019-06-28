package it.polimi.ingsw.network.server;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.Updater;
import it.polimi.ingsw.network.client.RemoteView;
import it.polimi.ingsw.view.ClientModel;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Class responsible for communication with a client connected through a socket
 *
 * @author marcobaga
 */
public class RMIVirtualView extends VirtualView implements RemoteController {

//TODO:  an RMI player cannot try logging again if he picks a blocked name, but not always [BUG]
//TODO: implement shutdown (graceful)

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
     * Suspends the player if inactive for more than a value in millis read from server.properties (default 2000).
     * This method is periodically called by ServerMain
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
                    int i = remoteView.choose(type, msg, options.stream().map(x -> ((Object) x).toString()).collect(Collectors.toList()));
                    if(busy) {
                        busy=false;
                        notifyObservers(String.valueOf(i));
                    }
                } catch (RemoteException ex) {
                    suspend();
                }
            }
        );
    }

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
                        int i = remoteView.choose(type, msg, options.stream().map(x -> ((Object) x).toString()).collect(Collectors.toList()));
                        if(busy&&System.currentTimeMillis()<timestamp) {
                            busy=false;
                            notifyObservers(String.valueOf(i));
                        }
                    } catch (RemoteException ex) {
                        suspend();
                    }
                }
        );
    }

    @Override
    public void display(String msg){
        try{
            remoteView.display(msg);
        }catch(RemoteException ex){
            //suspend(); to avoid a loop
        }
    }

    @Override
    public String getInputNow(String msg, int max) {
        try {
            return remoteView.getInput(msg, max);
        } catch (RemoteException ex) {
            suspend();
        }
        return "";
    }

    @Override
    public int chooseNow(String type, String msg, List<?> options){
        try {
            return remoteView.choose(type, msg, options.stream().map(x -> ((Object) x).toString()).collect(Collectors.toList()));
        }catch(RemoteException ex){
            suspend();
        }
        return 0;
    }

    @Override
    public void update(JsonObject jsonObject){
        try {
            remoteView.update(jsonObject.toString());
        } catch (RemoteException ex) {
            LOGGER.log(Level.SEVERE, "Error while updating", ex);
            suspend();
        }
    }

    @Override
    public void shutdown(){
        executor.shutdownNow();
        try {
            UnicastRemoteObject.unexportObject(this, false);
        }catch(NoSuchObjectException ex){
            LOGGER.log(Level.SEVERE, "Could not unexport RemoteController", ex);
        }
    }

    @Override
    public void ping(){}

    @Override
    public void showSuspension(){
        try{
            remoteView.showSuspension();
        }catch(RemoteException ex){
            LOGGER.log(Level.SEVERE, "Unable to send disconnection message", ex);
        }
    }

    @Override
    public void showEnd(String message){
        try{
            remoteView.showEnd(message);
        }catch(RemoteException ex){
            LOGGER.log(Level.SEVERE, "Unable to send disconnection message", ex);
        }
    }
}
