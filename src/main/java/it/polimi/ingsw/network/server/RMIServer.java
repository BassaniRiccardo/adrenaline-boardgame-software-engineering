package it.polimi.ingsw.network.server;

import java.rmi.AlreadyBoundException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RMIServer implements RemoteServer {

    private ExecutorService executor;
    private Registry reg;
    private int id;
    private int port;
    private static final Logger LOGGER = Logger.getLogger("serverLogger");

    public RMIServer(int port){
        this.port = port;
        id = 0;
        executor = Executors.newCachedThreadPool();
    }

    public void setup() {
        try {
            RemoteServer stub = (RemoteServer) UnicastRemoteObject.exportObject(this, 0);
            LocateRegistry.createRegistry(port);
            reg = LocateRegistry.getRegistry(port);

            reg.bind("RMIServer", stub);
            LOGGER.log(Level.INFO, "RMIServer ready");
        }catch(RemoteException ex) {LOGGER.log(Level.SEVERE, "Failed to retrieve RMI register for server binding", ex); //try again?
        }catch (AlreadyBoundException ex) {LOGGER.log(Level.SEVERE, "RMI server binding failed", ex);}
    }

    public synchronized String getPlayerController() {
        LOGGER.log(Level.FINE, "Constructing a PlayerController with ID {0}", id);
        String remoteName = "PC"+id;
        RMIPlayerController rmiPlayerController = new RMIPlayerController();
        LOGGER.log(Level.FINE, "New PlayerController created");
        try {
            RemotePlayerController stub = (RemotePlayerController) UnicastRemoteObject.exportObject(rmiPlayerController, 0);
            reg.bind(remoteName, stub);
        }catch(RemoteException ex) {
            LOGGER.log(Level.SEVERE, "Failed to retrieve RMI register for server binding while creating PC", ex);
        }catch (AlreadyBoundException ex) {LOGGER.log(Level.SEVERE, "PC binding failed");}
        executor.submit(rmiPlayerController);
        LOGGER.log(Level.FINE, "RMIPlayerController created");
        id++;
        return remoteName;
    }

    public void shutdown(){
        try {
            reg.unbind("RMIServer");
            UnicastRemoteObject.unexportObject(this, true);
        }catch(Exception ex){ LOGGER.log(Level.SEVERE, "Exception caught while shutting down RMIServer", ex);}
    }
}