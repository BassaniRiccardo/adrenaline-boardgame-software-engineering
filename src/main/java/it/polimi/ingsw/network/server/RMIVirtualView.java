package it.polimi.ingsw.network.server;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.Updater;
import it.polimi.ingsw.network.client.RemoteView;
import it.polimi.ingsw.view.ClientModel;

import java.rmi.RemoteException;
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
//check instances are not garbage collected
// implement shutdown

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

    public void choose(String msg, List<?> options){
        try {
            game.getNotifications().remove(this);
        }catch(NullPointerException ex){
            LOGGER.log(Level.FINEST, "No old notifications to remove", ex);
        }
        executor.submit(
            ()-> {
                try {
                    int i = remoteView.choose(msg, options.stream().map(x -> ((Object) x).toString()).collect(Collectors.toList()));
                    //if timer has not run out
                    notifyObservers(String.valueOf(i));
                } catch (RemoteException ex) {
                    suspend();
                }
            }
        );
    }

    public void display(String msg){
        try{
            remoteView.display(msg);
        }catch(RemoteException ex){
            suspend();
        }
    }

    public void getInput(String msg, int max){
        try {
            game.getNotifications().remove(this);
        }catch(NullPointerException ex){
            LOGGER.log(Level.FINEST, "No old notifications to remove", ex);
        }
        executor.submit(()-> {
                    try {
                        String ans = remoteView.getInput(msg, max);
                        notifyObservers(ans);
                    } catch (RemoteException ex) {
                        suspend();
                    }
                });
    }

    public String getInputNow(String msg, int max) {
        try {
            game.getNotifications().remove(this);
        }catch(NullPointerException ex){
            LOGGER.log(Level.FINEST, "No old notifications to remove", ex);
        }        try {
            return remoteView.getInput(msg, max);
        } catch (RemoteException ex) {
            suspend();
        }
        return "";
    }

    public int chooseNow(String msg, List<?> options){
        try {
            game.getNotifications().remove(this);
        }catch(NullPointerException ex){
            LOGGER.log(Level.FINEST, "No old notifications to remove", ex);
        }
        try {
            return remoteView.choose(msg, options.stream().map(x -> ((Object) x).toString()).collect(Collectors.toList()));
        }catch(RemoteException ex){
            suspend();
        }
        return 0;
    }

    public void notifyObservers(String msg) throws RemoteException{
        if(game!=null) {
            game.notify(this, msg);
        }
    }

    public void update(JsonObject jsonObject){
        System.out.print("almost calling remote update");
        try {
            System.out.print("calling remote update");
            remoteView.update(jsonObject.toString());
        } catch (RemoteException ex) {
            ex.printStackTrace();
            suspend();
        }
    }

    public void ping(){}

}
