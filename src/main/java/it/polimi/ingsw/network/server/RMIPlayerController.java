package it.polimi.ingsw.network.server;

import it.polimi.ingsw.controller.ServerMain;
import java.rmi.RemoteException;

public class RMIPlayerController extends PlayerController implements RemotePlayerController{

//TODO:  an RMI player cannot try logging again if he picks a blocked name, but not always [BUG]
//check instances are not garbage collected
    //implemente shutdown

    private long timeStamp;
    private final int timeout;

    RMIPlayerController(){
        super();
        this.timeout = Integer.parseInt(ServerMain.loadConfig().getProperty("RMITimeoutMillis", "2000"));
        this.timeStamp = System.currentTimeMillis();
    }

    @Override
    public void answer(String message) throws RemoteException {
        incoming.add(message);
    }

    @Override
    public String getMessage() throws RemoteException {
        if(outgoing.isEmpty()){
            return "";
        }
        return outgoing.remove(0);
    }

    @Override
    public void refresh(){
        if(!suspended&&System.currentTimeMillis() > timeStamp + timeout){
            suspend();
        }
    }

    @Override
    public void ping(){
        timeStamp = System.currentTimeMillis();
    }
}
