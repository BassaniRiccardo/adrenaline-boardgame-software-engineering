package it.polimi.ingsw.view;

//TODO: implement
public class GUI implements UI, Runnable {

    private boolean running;
    private ClientMain clientMain;

    public GUI(ClientMain clientMain){
        this.running = false;
        this.clientMain = clientMain;
    }

    @Override
    public void display(String message) { }

    public void run(){}

    @Override
    public String get() {
        return null;
    }
}
