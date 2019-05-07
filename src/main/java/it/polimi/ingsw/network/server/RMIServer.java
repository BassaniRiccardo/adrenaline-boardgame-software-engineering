package it.polimi.ingsw.network.server;

import java.rmi.AlreadyBoundException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//TODO: manage disconnections (graceful and not)
//TODO: adapt class for connections from different hosts


public class RMIServer implements RemoteServer {

    private ExecutorService executor;
    private Registry reg;
    private int id;

    public RMIServer(){
        id = 0;
        executor = Executors.newCachedThreadPool();
    }

    public void setup() {
        try {
            RemoteServer stub = (RemoteServer) UnicastRemoteObject.exportObject(this, 0); //check port
            LocateRegistry.createRegistry(1420);
            reg = LocateRegistry.getRegistry(1420); //cambiare con ip+porta
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
}