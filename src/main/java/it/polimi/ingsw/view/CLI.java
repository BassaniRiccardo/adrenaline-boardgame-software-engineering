package it.polimi.ingsw.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


//TODO: fully implement this class, add graphic functionality

/**
 * Simple command line interface for the client's I/O operations
 *
 * @author  marcobaga
 */
public class CLI implements UI{

    private Scanner in;
    private ClientMain clientMain;
    private static final Logger LOGGER = Logger.getLogger("clientLogger");

    /**
     * Standard constructor
     */
    public CLI(ClientMain clientMain) {
        this.in = new Scanner(System.in);
        this.clientMain = clientMain;
    }

    /**
     * Displays a certain message
     *
     * @param message       message to be displayed
     */
    public void display(String message) {System.out.println(message);}

    /**
     * Queries the user for input (blocking)
     *
     * @return              the user's input as a string
     */
    public String get(){
        return in.nextLine();
    }

    /**
     * Main CLI loop checking for user input asynchronously from other threads, in particular for closing the client while awaiting a message.
     */
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

    @Override
    public String get(List<String> list){
        boolean verified = false;
        String ans = "";
        do {
            ans = in.nextLine();
            if(Integer.parseInt(ans)<list.size()){
                verified = true;
            }
        }while(!verified);
        return ans;
    }

    public void display(List<String> list){
        System.out.println("Here are your choices:");
        for(int i = 0; i<list.size(); i++){
            System.out.println(i+") "+list.get(i));
        }
        list.forEach(System.out::println);
        System.out.println("Choose one");
    }
}