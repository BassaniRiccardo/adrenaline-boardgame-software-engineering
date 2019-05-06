package it.polimi.ingsw.view;

public class RMIConnection implements Connection {

    private ClientMain clientMain;

    public RMIConnection(ClientMain clientMain){
            this.clientMain = clientMain;
    }

    @Override
    public void run() {    }

    @Override
    public void send(String message) {    }

    public void shutdown(){}

}
