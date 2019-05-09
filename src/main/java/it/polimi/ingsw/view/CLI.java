package it.polimi.ingsw.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


//TODO: fully implement this class, addList graphic functionality
public class CLI implements UI{

    private Scanner in;
    private ClientMain clientMain;
    private static final Logger LOGGER = Logger.getLogger("clientLogger");

    public CLI(ClientMain clientMain){
        this.in = new Scanner(System.in);
        this.clientMain = clientMain;
    }

    public void display(String message) {System.out.println(message);}

    public String get(){
        return in.nextLine();
    }

    @Override
    public void run() {

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        while(Thread.currentThread().isAlive()) {
            try{
                if (in.ready()) {
                    if(in.readLine().equals("q")){
                        display("CLI: quitting");
                        clientMain.handleRequest(RequestFactory.toRequest("quit"));
                    }else{
                        display("Wait for your turn or press q to quit");
                    }
                }
            }catch(IOException e){
                LOGGER.log(Level.SEVERE, "Cannot retrieve input from keyboard, quitting");
                clientMain.handleRequest(RequestFactory.toRequest("quit"));
            }
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            }catch(InterruptedException ex){
                LOGGER.log(Level.INFO,"Skipped waiting time.");
                Thread.currentThread().interrupt();
            }
        }
    }
}
