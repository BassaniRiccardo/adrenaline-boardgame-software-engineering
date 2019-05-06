package it.polimi.ingsw.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;


//TODO: fully implement this class, add graphic functionality
public class CLI implements UI{

    private Scanner in;
    private ClientMain clientMain;

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
                        System.out.println("CLI: quitting");
                        clientMain.handleRequest(RequestFactory.toRequest("quit"));
                    }else{
                        System.out.println("Wait for your turn or press q to quit");
                    }
                }
            }catch(IOException e){
                System.out.println("Cannot retrieve input from keyboard");
                clientMain.handleRequest(RequestFactory.toRequest("quit"));
            }
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            }catch(InterruptedException ex){
                System.out.println("Skipped waiting time.");
                Thread.currentThread().interrupt();
            }
        }
    }
}
