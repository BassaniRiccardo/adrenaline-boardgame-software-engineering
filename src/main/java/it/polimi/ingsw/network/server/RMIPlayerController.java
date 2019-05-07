package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.ServerMain;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RMIPlayerController extends PlayerController implements RemotePlayerController{

//TODO: make this class anc TCPController inherit setup() from a common class
    //check instances are not garbage collected

    private final String remoteName;
    private List<String> incoming;
    private List<String> outgoing;

    public RMIPlayerController(String remoteName){
        this.remoteName = remoteName;
        this.incoming = new ArrayList<>();
        this.outgoing = new ArrayList<>();
    }

    public void run(){
        send("Select a name");
        System.out.println("Name request sent");
        name = receive();
        System.out.println("Name received: " + name);

        while(!ServerMain.getInstance().login(name, this)){
            if(ServerMain.getInstance().canResume(name)){
                send("Do you want to resume?");
                String ans = receive();
                if(ans.equals("yes")) {
                    if(ServerMain.getInstance().resume(name, this)){
                        break;
                    } else {
                        send("Somebody already resumed.");
                    }
                }
            }
            send("Name already taken. Try another one");
            name = receive();
        }
        send("Name accepted.");
    }

    @Override
    public void send(String in) {
        outgoing.add(in);
    }

    @Override
    public String receive() {
        while(incoming.isEmpty()){
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            }catch(InterruptedException ex){
                ex.printStackTrace();
            }
        }
        System.out.println("Incoming filled");
        return incoming.remove(0);
    }


    @Override
    public void answer(String message) throws RemoteException {
        System.out.println("remote method called");
        incoming.add(message);
    }

    @Override
    public String getMessage() throws RemoteException {
        return outgoing.remove(0);
    }
}
