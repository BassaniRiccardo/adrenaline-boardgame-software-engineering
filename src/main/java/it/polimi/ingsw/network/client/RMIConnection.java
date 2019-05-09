package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.server.RemotePlayerController;
import it.polimi.ingsw.network.server.RemoteServer;
import it.polimi.ingsw.view.ClientMain;
import it.polimi.ingsw.view.RequestFactory;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

//TODO: implement shutdown, handle exceptions properly, test

public class RMIConnection extends Connection {

    private RemotePlayerController playerStub;

    public RMIConnection(ClientMain clientMain, String address, int port){
        this.clientMain = clientMain;
        try {
            Registry reg = LocateRegistry.getRegistry(address, port);
            RemoteServer serverStub = (RemoteServer) reg.lookup("RMIServer");
            String pcLookup = serverStub.getPlayerController();
            LOGGER.log(Level.SEVERE, "Name received for RMI PC lookup: " + pcLookup);
            playerStub = (RemotePlayerController) reg.lookup(pcLookup);

            ExecutorService executor = Executors.newCachedThreadPool();
            executor.submit(new Runnable(){
                @Override
                public void run(){
                    while(Thread.currentThread().isAlive()){
                        try {
                            playerStub.ping();
                        }catch(RemoteException ex){
                            LOGGER.log(Level.SEVERE, "Unable to ping RMI server", ex);
                        }
                        try {
                            TimeUnit.MILLISECONDS.sleep(100);
                        }catch(InterruptedException ex){
                            LOGGER.log(Level.INFO,"Skipped waiting time.");
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            });
        }catch(NotBoundException ex){
            LOGGER.log(Level.SEVERE, "Could not find stubs in registry", ex);
        }catch(RemoteException ex){
            LOGGER.log(Level.SEVERE, "RMI server disconnected, shutting down", ex);
            clientMain.handleRequest(RequestFactory.toRequest("quit"));
        }
    }

    @Override
    String receive(){
        String message = "";
        try {
            message = playerStub.getMessage();
        }catch(RemoteException ex){
            LOGGER.log(Level.SEVERE, "RMI server disconnected, shutting down", ex);
            clientMain.handleRequest(RequestFactory.toRequest("quit"));
        }
        return message;
    }

    @Override
    public void send(String message) {
        try {
            playerStub.answer(message);
            LOGGER.log(Level.FINE, "Sending message to RMI server");
        }catch (RemoteException ex){
            LOGGER.log(Level.SEVERE, "RMI server disconnected, shutting down", ex);
            clientMain.handleRequest(RequestFactory.toRequest("quit"));
        }
    }

    @Override
    public void shutdown(){
        //implement shutdown
    }

}
