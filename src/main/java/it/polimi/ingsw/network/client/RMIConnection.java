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

//TODO: implement shutdown, handle exceptions properly, test

public class RMIConnection implements Connection {

    private ClientMain clientMain;
    private RemoteServer serverStub;
    private RemotePlayerController playerStub;
    private Registry reg;

    public RMIConnection(ClientMain clientMain){
        this.clientMain = clientMain;
        try {
            reg = LocateRegistry.getRegistry(1420);
            try {
                serverStub = (RemoteServer) reg.lookup("RMIServer");
                String pcLookup = serverStub.getPlayerController();
                System.out.println("Nome ricevuto per RMI: " + pcLookup);

                System.out.print("RMI registry bindings: ");
                String[] e = reg.list();
                for (int i=0; i<e.length; i++)
                    System.out.println(e[i]);

                playerStub = (RemotePlayerController) reg.lookup(pcLookup);
            }catch(NotBoundException ex){
                ex.printStackTrace();
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
            System.out.println("answer sent");
        }catch (RemoteException ex){
            System.out.println("Issues with answering RMI messages");
        }
    }

    @Override
    public void shutdown(){
        //implement shutdown
    }

}
