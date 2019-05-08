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

//TODO: manage disconnections (graceful and not)
//TODO: adapt class for connections from different hosts


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
            RemoteServer stub = (RemoteServer) UnicastRemoteObject.exportObject(this, 0); //check port
            LocateRegistry.createRegistry(port);
            reg = LocateRegistry.getRegistry(port); //cambiare con ip+porta
            reg.bind("RMIServer", stub);
            System.out.println("RMIServer ready");
        }catch(RemoteException ex) {ex.printStackTrace(); System.out.println("Failed to retrieve RMI register for server binding");
        }catch (AlreadyBoundException ex) {System.out.println("RMI server binding failed");}
    }

    public synchronized String getPlayerController() { //maybe throw RemoteException
        System.out.println("Server: inizio costruzione PlayerController con ID " + id);
        String remoteName = "PC"+id;
        RMIPlayerController rmiPlayerController = new RMIPlayerController(remoteName);
        System.out.println("Creato nuovo PlayerController");
        try {
            RemotePlayerController stub = (RemotePlayerController) UnicastRemoteObject.exportObject(rmiPlayerController, 0);

            System.out.println("creato Stub");
            reg.bind(remoteName, stub);
            System.out.println("RMIPC bound");
        }catch(RemoteException ex) { ex.printStackTrace(); System.out.println("Failed to retrieve RMI register for server binding while creating PC");
        }catch (AlreadyBoundException ex) {System.out.println("RMI server binding failed");}
        executor.submit(rmiPlayerController);
        System.out.println("RMIPlayerController created");
        id++;
        return remoteName;
    }

    public void shutdown(){
        //also close TCP and RMI Player controllers
        try {
            reg.unbind("RMIServer");
            UnicastRemoteObject.unexportObject(this, true);
        }catch(Exception ex){ LOGGER.log(Level.SEVERE, "Caught while shuttind down RMIServer", ex);}

    }
}