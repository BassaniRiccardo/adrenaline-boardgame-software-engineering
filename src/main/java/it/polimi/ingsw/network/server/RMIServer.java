package it.polimi.ingsw.network.server;

import java.rmi.AlreadyBoundException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RMIServer implements RemoteServer {

    private ExecutorService executor;
    private Registry reg;
    private int id;

    public RMIServer(){
        id = 0;
        executor = Executors.newCachedThreadPool();
    }

    public void setup() {
        RMIServer remoteServer = new RMIServer();
        try {
            RemoteServer stub = (RemoteServer) UnicastRemoteObject.exportObject(remoteServer, 0); //check port
            reg = LocateRegistry.getRegistry();
                reg.bind("RMIServer", stub);
                System.out.println("RMIServer ready");
        }catch(RemoteException ex) {System.out.println("Failed to retrieve RMI register for server binding");
        }catch (AlreadyBoundException ex) {System.out.println("RMI server binding failed");}
    }

    public synchronized String getPlayerController() { //maybe throw RemoteException
        id++;
        String remoteName = "PC"+id;
        RMIPlayerController rmiPlayerController = new RMIPlayerController(remoteName);
        try {
            RemotePlayerController stub = (RemotePlayerController) UnicastRemoteObject.exportObject(rmiPlayerController, 0);
            reg.bind("remoteName", stub);
        }catch(RemoteException ex) {System.out.println("Failed to retrieve RMI register for server binding");
        }catch (AlreadyBoundException ex) {System.out.println("RMI server binding failed");}
        executor.submit(rmiPlayerController);
        System.out.println("RMIPlayerController created");
        return remoteName;
    }
}