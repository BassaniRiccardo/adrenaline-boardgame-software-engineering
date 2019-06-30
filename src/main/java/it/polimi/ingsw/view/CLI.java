package it.polimi.ingsw.view;

import it.polimi.ingsw.view.clirenderer.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static it.polimi.ingsw.controller.ServerMain.SLEEP_TIMEOUT;
import static it.polimi.ingsw.view.clirenderer.MainRenderer.CLEAR_CONSOLE;

/**
 * Simple command line interface for the client's I/O operations. Its methods are called by ClientMain or ClientUpdater.
 * It runs on a separate thread to continuously check for user input. It focuses on input management and uses
 * different classes for particular rendering tasks.
 *
 * @author  marcobaga
 */
public class CLI implements UI{

    private static final String QUIT_KEY = "q";
    private static final String INFO_KEY = "info";
    private static final String INVALID_INPUT_MSG = "Input not valid, try again.";
    private static final String INPUT_RECEIVED_MSG = "Input received. Please stand by.";
    private static final String LONG_ANSWER_1_MSG = "Your answer must be shorter than ";
    private static final String LONG_ANSWER_2_MSG = " characters, try again.";
    private static final String CHOICES_MSG = "\nHere are your choices: ";
    private static final String CHOOSE_ONE_MSG = "\nChoose one";
    private static final String WARNING_MSG = "\n Wait some more or press q to quit";
    private static final String DISCONNECTION_MSG = "You cannot reach the server. You can try starting another client and log in with the same username to resume. Press Enter to close the game";
    private static final String SUSPENSION_MSG = "You were suspended from the server because you were not able to finish your turn in time. Press Enter to close the game.";
    private static final String END_MSG = "\nPress any button to close the game.";
    private static final String SPLASHSCREEN_MSG = "      _/_/    _/_/_/    _/_/_/    _/_/_/_/  _/      _/    _/_/    _/        _/_/_/  _/      _/  _/_/_/_/\n" +
            "   _/    _/  _/    _/  _/    _/  _/        _/_/    _/  _/    _/  _/          _/    _/_/    _/  _/       \n" +
            "  _/_/_/_/  _/    _/  _/_/_/    _/_/_/    _/  _/  _/  _/_/_/_/  _/          _/    _/  _/  _/  _/_/_/    \n" +
            " _/    _/  _/    _/  _/    _/  _/        _/    _/_/  _/    _/  _/          _/    _/    _/_/  _/         \n" +
            "_/    _/  _/_/_/    _/    _/  _/_/_/_/  _/      _/  _/    _/  _/_/_/_/  _/_/_/  _/      _/  _/_/_/_/    \n" +
            "\na game by Philip Neduk, now loading";

    private static final int WARNING_SLEEP = 1500;
    private static final int SPLASHSCREEN_SLEEP = 1000;

    private ClientMain clientMain;
    private static final Logger LOGGER = Logger.getLogger("clientLogger");
    private String answer;          //contains the last user input
    private boolean receiving;      //true if a request has not been answered yet
    private boolean justReceived;   //true if the player has provided an input which has not been parsed yet
    private MainRenderer mainRenderer;
    private BufferedReader in;

    /**
     * Standard constructor, also showing a splash screen
     */
    public CLI(ClientMain clientMain) {
        this.clientMain = clientMain;
        this.receiving = false;
        this.justReceived = false;
        this.answer = "";
        this.mainRenderer = new MainRenderer(clientMain);
        this.in = new BufferedReader(new InputStreamReader(System.in));
        splashScreen();
    }

    /**
     * Main CLI loop checking for user input asynchronously from other threads.
     * Input can interpreted as an answer to a request, if a request was made, as a request for information about
     * a particular weapon or as a quitting request.
     */
    @Override
    public void run() {
        splashScreen();
        while(Thread.currentThread().isAlive()) {
            try{
                if (in.ready()) {
                    String msg = in.readLine();
                    if(msg.equalsIgnoreCase(QUIT_KEY)) {
                        handleQuitting();
                    }else if(msg.startsWith(INFO_KEY)){
                        handleInfo(msg.substring(INFO_KEY.length()+1));
                    }else if(receiving&&!justReceived) {
                        answer = msg;
                        justReceived = true;
                    }else{
                        displayWarning();
                    }
                }
            }catch(IOException e){
                LOGGER.log(Level.SEVERE, "Cannot retrieve input from keyboard, quitting");
                clientMain.showDisconnection();//show custom msg
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
     * Collects the user input when he chooses among a list of options and validates his choice. (BLOCKING)
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
                    display(INVALID_INPUT_MSG);
                }
            }catch(NumberFormatException ex){
                display(INVALID_INPUT_MSG);
            }
            justReceived = false;
            receiving = true;
        }
        receiving = false;
        mainRenderer.setCurrentRequest("");
        mainRenderer.setCurrentMessage(INPUT_RECEIVED_MSG);
        render();
        return answer;
    }


    /**
     * Collects and validates the user input, which can be any String up to a maximum length. (BLOCKING)
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
                display(LONG_ANSWER_1_MSG + max + LONG_ANSWER_2_MSG);
            }
            justReceived = false;
            receiving = true;
        }
        receiving = false;
        mainRenderer.setCurrentRequest("");
        mainRenderer.setCurrentMessage(INPUT_RECEIVED_MSG);
        render();
        return answer;
    }

    /**
     * Shows a message to the user
     *
     * @param message   message to be displayed
     */
    @Override
    public void display(String message) {
        mainRenderer.setCurrentMessage(message);
        render();
    }

    /**
     * Displays a request. This request will be visible until a new one arrives or the user answers.
     *
     * @param message   request to be displayed
     * @param max       max length of required input
     */
    @Override
    public void display(String message, String max) {
        mainRenderer.setCurrentRequest(message + "[max. " + max + " characters]");
        mainRenderer.setCurrentMessage("");
        render();
    }

    /**
     * Displays a choice request. This request will be visible until a new one arrives or the user answers.
     *
     * @param type      the type of request to be displayed
     * @param message   request to be displayed
     * @param options   options to be displayed
     */
    @Override
    public void display(String type, String message, List<String> options) {
        LOGGER.log(Level.INFO, "{0} request", type);
        StringBuilder bld = new StringBuilder();
        bld.append(message);
        bld.append(CHOICES_MSG);
        for(int i = 0; i<options.size(); i++){
            bld.append("\n");
            bld.append((i+1));
            bld.append(") ");
            bld.append(options.get(i));
        }
        bld.append(CHOOSE_ONE_MSG);
        mainRenderer.setCurrentRequest(bld.toString());
        mainRenderer.setCurrentMessage("");
        render();
    }

    /**
     * Calls the main rendering method in an external class.
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
        MainRenderer.showQuitScreen();
        try {
            if (in.readLine().equalsIgnoreCase("y")) {
                System.exit(0);
            } else {
                render();
            }
        }catch(IOException ex){
            LOGGER.log(Level.SEVERE, "Exception while trying to quit", ex);
        }
    }

    /**
     * Shows the info screen, delivering information about a weapon
     *
     * @param weaponName    weapon looked up
     */
    private void handleInfo(String weaponName){
        MainRenderer.showInfoScreen(weaponName);
        try {
            in.readLine();
        }catch(IOException ex){
            LOGGER.log(Level.SEVERE, "Exception while trying to quit", ex);
        }
        render();
    }

    /**
     * Displays warning when input is given at wrong times
     */
    private void displayWarning(){
        System.out.println( CLEAR_CONSOLE + WARNING_MSG);
        try {
            TimeUnit.MILLISECONDS.sleep(WARNING_SLEEP);
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
        System.out.println(CLEAR_CONSOLE + SPLASHSCREEN_MSG);
        try {
            Thread.sleep(SPLASHSCREEN_SLEEP);
        } catch (InterruptedException ex) {
            LOGGER.log(Level.INFO, "Skipped waiting time.", ex);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Displays a disconnection message. The player can press Enter to close the game.
     */
    public void displayDisconnection(){
        System.out.println(CLEAR_CONSOLE + DISCONNECTION_MSG);
        try {
            in.readLine();
        }catch(IOException ex){
            LOGGER.log(Level.SEVERE, "Exception while showing disconnect message", ex);
        }
    }

    /**
     * Displays a suspension message. The player can press Enter to close the game.
     */
    public void displaySuspension(){
        System.out.println(CLEAR_CONSOLE + SUSPENSION_MSG);
        try {
            in.readLine();
        }catch(IOException ex){
            LOGGER.log(Level.SEVERE, "Exception while showing disconnect message", ex);
        }
    }

    /**
     * Displays a message with the game results. The player can press Enter to close the game.
     */
    public void displayEnd(String message){
        System.out.print(CLEAR_CONSOLE);
        System.out.println(CLEAR_CONSOLE + message + END_MSG);
        try {
            in.readLine();
        }catch(IOException ex){
            LOGGER.log(Level.SEVERE, "Exception while showing disconnect message", ex);
        }
    }

    /**
     * Adds a message representing changes in the game state to a list.
     *
     * @param message   description of a change in the game state
     */
    public void addHistory(String message){
        mainRenderer.addMessage(message);
    }
}