package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.server.RemoteController;
import it.polimi.ingsw.network.server.RemoteServer;
import it.polimi.ingsw.view.ClientMain;
import it.polimi.ingsw.view.ClientModel;
import it.polimi.ingsw.view.UI;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of RMI connection to server
 *
 * @author marcobaga
 */
public class RMIConnection implements Runnable, RemoteView {

    private RemoteController playerStub;
    private ClientMain clientMain;
    static final Logger LOGGER = Logger.getLogger("clientLogger");


    /**
     * Constructor retrieving remote objects
     *
     * @param clientMain        reference to the main class
     * @param address           IP to connect to
     * @param port              port to connect to
     */
    public RMIConnection(ClientMain clientMain, String address, int port){
        System.out.println("address: " + address + "\nport: " + port);
        this.clientMain = clientMain;
        try {
            Registry reg = LocateRegistry.getRegistry(address, port);
            RemoteServer serverStub = (RemoteServer) reg.lookup("RMIServer");
            String pcLookup = serverStub.getPlayerController((RemoteView) UnicastRemoteObject.exportObject(this, 0));
            LOGGER.log(Level.SEVERE, "Name received for RMI PC lookup: " + pcLookup);
            playerStub = (RemoteController) reg.lookup(pcLookup);

            ExecutorService executor = Executors.newCachedThreadPool();
            executor.submit(()->{
                while(Thread.currentThread().isAlive()){
                    try {
                        playerStub.ping();
                    }catch(RemoteException ex){
                        LOGGER.log(Level.SEVERE, "Unable to ping RMI server", ex);
                        clientMain.shutdown();
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                    }catch(InterruptedException ex){
                        LOGGER.log(Level.INFO,"Skipped waiting time.");
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }catch(NotBoundException ex){
            LOGGER.log(Level.SEVERE, "Could not find stubs in registry", ex);
        }catch(RemoteException ex){
            LOGGER.log(Level.SEVERE, "RMI server disconnected, shutting down", ex);
            System.exit(0);
        }
    }

    public void run(){
        System.out.println("RMIConnection running");
    }

    public int choose(String msg, List<String> options) throws RemoteException{
        return clientMain.choose(msg, options);
    }

    public void display(String msg) throws RemoteException{
        clientMain.display(msg);
    }

    public String getInput(String msg, int max) throws RemoteException{
        return clientMain.getInput(msg, max);
    }

    public void ping(){}

    public void updateModel(ClientModel clientModel){
        clientMain.setClientModel(clientModel);
    }

}
