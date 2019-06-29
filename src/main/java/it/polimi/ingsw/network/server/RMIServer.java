package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.client.RemoteView;

import java.rmi.AlreadyBoundException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Server implementation for RMI based communication. This is the class receiving and handling
 * RMI connection "requests".
 *
 * @author marcobaga
 */
public class RMIServer implements RemoteServer {

    private ExecutorService executor;
    private Registry reg;
    private int id;
    private int port;
    private static final Logger LOGGER = Logger.getLogger("serverLogger");

    /**
     * Standard constructor
     *
     * @param port              the RMIRegistry's port (default 1420)
     */
    public RMIServer(int port){
        this.port = port;
        id = 0;
        executor = Executors.newCachedThreadPool();
    }

    /**
     * Carries out binding procedures of this class
     */
    public void setup() {
        try {
            RemoteServer stub = (RemoteServer) UnicastRemoteObject.exportObject(this, 0);
            LocateRegistry.createRegistry(port);
            reg = LocateRegistry.getRegistry(port);
            reg.bind("RMIServer", stub);
            LOGGER.log(Level.INFO, "RMIServer ready on port "+ port);
        }catch(RemoteException ex) {LOGGER.log(Level.SEVERE, "Failed to retrieve RMI register for server binding", ex); //try again?
        }catch (AlreadyBoundException ex) {LOGGER.log(Level.SEVERE, "RMI server binding failed", ex);}
    }

    /**
     * Called by a client, it instantiates a new VirtualView accessible via RMI and returns the String associated in the registry.
     *
     * @return                  the name bound to the new VirtualView
     */
    public synchronized String getPlayerController(RemoteView view) {
        LOGGER.log(Level.FINE, "Constructing a VirtualView with ID {0}", id);
        String remoteName = "PC"+id;
        RMIVirtualView rmiPlayerController = new RMIVirtualView(view);
        LOGGER.log(Level.FINE, "New VirtualView created");
        try {
            RemoteController stub = (RemoteController) UnicastRemoteObject.exportObject(rmiPlayerController, 0);
            reg.bind(remoteName, stub);
        }catch(RemoteException ex) {
            LOGGER.log(Level.SEVERE, "Failed to retrieve RMI register for server binding while creating PC", ex);
        }catch (AlreadyBoundException ex) {LOGGER.log(Level.SEVERE, "PC binding failed");}
        executor.submit(rmiPlayerController);
        LOGGER.log(Level.FINE, "RMIVirtualView created");
        id++;
        return remoteName;
    }

    /**
     * Shuts down and cleans up
     */
    public void shutdown(){
        try {
            reg.unbind("RMIServer");
            UnicastRemoteObject.unexportObject(this, true);
        }catch(Exception ex){ LOGGER.log(Level.SEVERE, "Exception caught while shutting down RMIServer", ex);}
    }
}