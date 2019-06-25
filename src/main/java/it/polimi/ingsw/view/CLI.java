package it.polimi.ingsw.view;

import it.polimi.ingsw.view.CLIRenderer.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static it.polimi.ingsw.controller.ServerMain.SLEEP_TIMEOUT;

/**
 * Simple command line interface for the client's I/O operations
 *
 * @author  marcobaga
 */
public class CLI implements UI{

    private ClientMain clientMain;
    private static final Logger LOGGER = Logger.getLogger("clientLogger");
    private String answer;
    private boolean receiving;
    private boolean justReceived;
    private MainRenderer mainRenderer;
    private BufferedReader in;


    /**
     * Standard constructor
     */
    public CLI(ClientMain clientMain) {
        this.clientMain = clientMain;
        this.receiving = false;
        this.justReceived = false;
        this.answer = "";
        this.mainRenderer = new MainRenderer(clientMain);
        this.in = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * Main CLI loop checking for user input asynchronously from other threads, in particular for closing the client while awaiting a message.
     */
    @Override
    public void run() {
        splashScreen();
        while(Thread.currentThread().isAlive()) {
            try{
                if (in.ready()) {
                    String msg = in.readLine();
                    if(msg.equals("q")) {
                        handleQuitting();
                    }else if(msg.startsWith("info")){
                        handleInfo(msg.substring(5));
                    }else if(receiving&&!justReceived) {
                        answer = msg;
                        justReceived = true;
                    }else{
                        displayWarning();
                    }
                }
            }catch(IOException e){
                LOGGER.log(Level.SEVERE, "Cannot retrieve input from keyboard, quitting");
                System.exit(0);
            }
            try {
                TimeUnit.MILLISECONDS.sleep(SLEEP_TIMEOUT);
            }catch(InterruptedException ex){
                LOGGER.log(Level.INFO,"Skipped waiting time.");
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Queries the user to choose amonga list of options
     *
     * @param list  list of options
     * @return      a string containing the number of the option chosen
     */
    @Override
    public String get(List<String> list){
        receiving = true;
        boolean verified = false;
        while(!verified) {
            while (!justReceived) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException ex) {
                    LOGGER.log(Level.INFO, "Skipped waiting time.");
                    Thread.currentThread().interrupt();
                }
            }
            try {
                if (Integer.parseInt(answer) <= list.size() && Integer.parseInt(answer) > 0) {
                    verified = true;
                } else {
                    display("Input not valid, try again.");
                }
            }catch(NumberFormatException ex){
                display("Input not valid, try again.");
            }
            justReceived = false;
            receiving = true;
        }
        receiving = false;
        mainRenderer.setCurrentRequest("");
        return answer;
    }


    /**
     * Queries the user for input
     *
     * @param m     max length of the input, as a string
     * @return      user input
     */
    @Override
    public String get(String m){
        receiving = true;
        boolean verified = false;
        int max = Integer.parseInt(m);
        while(!verified) {
            while (!justReceived) {
                try {
                    TimeUnit.MILLISECONDS.sleep(SLEEP_TIMEOUT);
                } catch (InterruptedException ex) {
                    LOGGER.log(Level.INFO, "Skipped waiting time.");
                    Thread.currentThread().interrupt();
                }
            }
            if (answer.length()<max) {
                verified = true;
            } else{
                display("Your answer must be shorter than " + max + " characters, try again.");
            }
            justReceived = false;
            receiving = true;
        }
        receiving = false;
        mainRenderer.setCurrentRequest("");
        return answer;
    }

    /**
     * Adds a message to the list of mesasges to be rendered
     *
     * @param message message to be displayed
     */
    @Override
    public void display(String message) {
        mainRenderer.addMessage(message);
        render();
    }

    /**
     * Displays a request until another one arrives
     *
     * @param message request to be displayed
     * @param max   max length of required input
     */
    @Override
    public void display(String message, String max) {
        mainRenderer.setCurrentRequest(message + "[max. " + max + " characters]");
        render();
    }

    /**
     * Displays a choice request
     *
     * @param message request to be displayed
     * @param options options to be displayed
     */
    @Override
    public void display(String message, List<String> options) {
        StringBuilder bld = new StringBuilder();
        bld.append(message);
        bld.append("\nHere are your choices: ");
        for(int i = 0; i<options.size(); i++){
            bld.append("\n" + (i+1) +") "+options.get(i));
        }
        bld.append("\nChoose one");
        mainRenderer.setCurrentRequest(bld.toString());
        render();
    }

    /**
     * Calls the main rendering method.
     */
    @Override
    public void render() {
        try {
            mainRenderer.render();
        }catch (Exception ex){
            LOGGER.log(Level.SEVERE, "Error in rendering CLI", ex);
        }
    }

    /**
     * Shows the quitting screen and allows the player to quit
     */
    private void handleQuitting(){
        mainRenderer.showQuitScreen();
        try {
            if (in.readLine().equalsIgnoreCase("y")) {
                System.exit(0);
                //TODO: implement graceful shutdown;
            } else {
                render();
            }
        }catch(IOException ex){
            LOGGER.log(Level.SEVERE, "Exception while trying to quit", ex);
        }
    }

    /**
     * Shows the info screen
     * @param weaponName    weapon looked up
     */
    private void handleInfo(String weaponName){
        mainRenderer.showInfoScreen(weaponName);
        try {
            in.readLine();
        }catch(IOException ex){
            LOGGER.log(Level.SEVERE, "Exception while trying to quit", ex);
        }
        render();
    }

    /**
     * Sets the maximum number of messages to be displayed
     * @param n
     */
    @Override
    public void setMessageMemory(int n){
        mainRenderer.setMessageMemory(n);
    }

    /**
     * Displays warning when input is given at wrong times
     */
    private void displayWarning(){
        System.out.print("\033[H\033[2J");
        System.out.println("\n Wait for your turn or press q to quit");
        try {
            TimeUnit.MILLISECONDS.sleep(1000);
        }catch(InterruptedException ex){
            LOGGER.log(Level.INFO,"Skipped waiting time.");
            Thread.currentThread().interrupt();
        }
        render();
    }

    /**
     * Displays a splash screen while the game loads
     */
    private void splashScreen(){
        System.out.print("\033[H\033[2J");
        System.out.println(
                "      _/_/    _/_/_/    _/_/_/    _/_/_/_/  _/      _/    _/_/    _/        _/_/_/  _/      _/  _/_/_/_/\n" +
                "   _/    _/  _/    _/  _/    _/  _/        _/_/    _/  _/    _/  _/          _/    _/_/    _/  _/       \n" +
                "  _/_/_/_/  _/    _/  _/_/_/    _/_/_/    _/  _/  _/  _/_/_/_/  _/          _/    _/  _/  _/  _/_/_/    \n" +
                " _/    _/  _/    _/  _/    _/  _/        _/    _/_/  _/    _/  _/          _/    _/    _/_/  _/         \n" +
                "_/    _/  _/_/_/    _/    _/  _/_/_/_/  _/      _/  _/    _/  _/_/_/_/  _/_/_/  _/      _/  _/_/_/_/    \n" +
                "\na game by Philip Neduk, now loading");
    }

    public void showDCScreen(){
        System.out.print("\033[H\033[2J");
        System.out.print("You have been suspended. Most likely your turn timer has run out or you were disconnected from the server. You can start another client and log in with the same username to resume. Closing game now.");
    }
}