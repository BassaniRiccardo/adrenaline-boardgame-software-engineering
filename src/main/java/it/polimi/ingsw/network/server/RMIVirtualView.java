package it.polimi.ingsw.network.server;

import com.google.gson.JsonObject;
import it.polimi.ingsw.network.client.RemoteView;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.*;
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
        if(suspended) return;
        if(pinged&&System.currentTimeMillis()-lastPing>PING_TIMEOUT_MILLIS){
            suspend();
        }
        try{
            Future<Void> future = executor.submit(new Callable<Void>() {
                public Void call() throws Exception {
                    remoteView.ping();
                    return null;
                }
            });
            future.get(PING_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        }catch (Exception ex){
            suspend();
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
            LOGGER.log(Level.INFO, "Could not unexport RemoteController", ex);
        }
    }


    /**
     * Method called by the client to check connection status
     */
    @Override
    public void ping(){
        pinged = true;
        lastPing = System.currentTimeMillis();
    }


    /**
     * Commands the client to show the suspension message and eventually shutdown.
     */
    @Override
    public void showSuspension(){
        if(suspended) return;
        try{
            Future<Void> future = executor.submit(new Callable<Void>() {
                public Void call() throws Exception {
                    remoteView.showSuspension();
                    return null;
                }
            });
            future.get(PING_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        }catch (Exception ex){
            //suspend();
        }
    }

    /**
     * Commands the client to show an ending mesage and eventually shutdown.
     *
     * @param message       the message to display
     */
    @Override
    public void showEnd(String message){
        if(suspended) return;

        try{
            Future<Void> future = executor.submit(new Callable<Void>() {
                public Void call() throws Exception {
                    remoteView.showEnd(message);
                    return null;
                }
            });
            future.get(PING_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        }catch (Exception ex){
            //suspend();
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
        try {
            synchronized (game.getNotifications()){
                game.getNotifications().remove(this);
            }
        }catch(NullPointerException ex){
            LOGGER.log(Level.FINEST, "No old notifications to remove", ex);
        }
        if(busy||suspended) return;
        busy=true;
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
        try {
            synchronized (game.getNotifications()){
                game.getNotifications().remove(this);
            }
        }catch(NullPointerException ex){
            LOGGER.log(Level.FINEST, "No old notifications to remove", ex);
        }
        if(busy||suspended) return;
        busy=true;
        long timestamp = System.currentTimeMillis() + timeoutSec*1000;
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
        if(busy||suspended) return 1;
        try {
            busy = true;
            int answer = remoteView.choose(type, msg, options.stream().map(x -> (x).toString()).collect(Collectors.toList()));
            busy = false;
            return answer;
        } catch (RemoteException ex) {
            busy = false;
            suspend();
        }
        return 1;
    }


    /**
     * Sends a message for the client to display
     *
     * @param msg       message to display
     */
    @Override
    public void display(String msg){
        if(suspended) return;
        try{
            Future<Void> future = executor.submit(new Callable<Void>() {
                public Void call() throws Exception {
                    remoteView.display(msg);
                    return null;
                }
            });
            future.get(PING_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        }catch (Exception ex){
            //suspend();
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
        if(busy||suspended) return "";
        try {
            busy = true;
            String answer = remoteView.getInput(msg, max);
            busy = false;
            return answer;
        } catch (RemoteException ex) {
            busy = false;
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
        if(suspended) return;

        try{
            Future<Void> future = executor.submit(new Callable<Void>() {
                public Void call() throws Exception {
                    remoteView.update(jsonObject.toString());
                    return null;
                }
            });
            future.get(PING_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        }catch (Exception ex){
            suspend();
        }
    }
}
