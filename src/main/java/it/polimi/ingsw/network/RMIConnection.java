package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.RemotePlayerController;
import it.polimi.ingsw.controller.RemoteServer;
import it.polimi.ingsw.view.ClientMain;
import it.polimi.ingsw.view.RequestFactory;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.TimeUnit;

//TODO: implement shutdown, handle exceptions properly, test

public class RMIConnection implements Connection {

    private ClientMain clientMain;
    private RemoteServer serverStub;
    private RemotePlayerController playerStub;
    private Registry reg;

    public RMIConnection(ClientMain clientMain){
        this.clientMain = clientMain;
        try {
            reg = LocateRegistry.getRegistry(null);
            try {
                serverStub = (RemoteServer) reg.lookup("RMIServer");
                playerStub = (RemotePlayerController) reg.lookup(serverStub.getPlayerController());
            }catch(NotBoundException ex){
                System.out.println("Could not find stubs in registry");
            }
        }catch(RemoteException ex){System.out.println("RMI connection terminated");}
    }

    @Override
    public void run() {


        while(Thread.currentThread().isAlive()){
            try {
                String message = playerStub.getMessage();
                if (message != "empty") {
                    clientMain.handleRequest(RequestFactory.toRequest(message));
                }
            }catch(RemoteException ex){}
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException ex) {
                System.out.println("Skipped waiting time.");
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void send(String message) {
        try {
            playerStub.answer(message);
        }catch (RemoteException ex){}
    }

    @Override
    public void shutdown(){
        //implement shutdown
    }

}
