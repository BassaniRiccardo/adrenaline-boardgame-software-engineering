package it.polimi.ingsw.controller;

import it.polimi.ingsw.network.server.VirtualView;
import it.polimi.ingsw.network.server.RMIServer;
import it.polimi.ingsw.network.server.TCPServer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

/**
 * Main class that starts the game server, manages connections and matchmaking.
 *
 * @author  marcobaga
 */
public class ServerMain {

    private static ServerMain instance;
    private List <VirtualView> players;
    private List <VirtualView> waitingPlayers;
    private List <GameEngine> currentGames;
    private TCPServer tcpServer;
    private RMIServer rmiServer;
    private Timer timer;
    private ExecutorService executor;
    private BufferedReader in;
    private boolean running;
    private String oldMessage;

    private static final Logger LOGGER = Logger.getLogger("serverLogger");
    public static final int SLEEP_TIMEOUT = 100;
    private static final String SERVER_LOG_FILENAME = "serverLog.txt";
    private static final String SERVER_PROPERTIES_FILENAME = "/server.properties";

    public static final int MAX_PLAYERS = 5;
    public static final int MIN_PLAYERS = 3;

    private static final String SETUP_COMPLETED_MESSAGE = "Setup completed, starting matchmaking, press q to quit";
    private static final String QUITTING_MESSAGE = "Quitting";
    private static final String QUIT_KEY = "q";
    private static final String QUITTING_PROMPT = "Press q to quit";
    private static final String GAME_STARTED_MESSAGE = "Game started with ";
    private static final String TIME_LEFT_MESSAGE = "Time left: ";
    private static final String STARTING_GAME_MESSAGE = "Game about to start!";
    private static final String WAITING_MESSAGE = "Waiting for more players";
    private static final String CONNECTED_LIST_MESSAGE = "Connected players:";

    private static final String ENTER = "\n";
    private static final String TAB = "\t";


    /**
     * Standard private constructor.
     */
    private ServerMain(){
        players = new ArrayList<>();
        waitingPlayers = new ArrayList<>();
        currentGames = new ArrayList<>();
        tcpServer = null;
        rmiServer = null;
        timer = null;
        executor = Executors.newCachedThreadPool();
        oldMessage = "";
    }


    /**
     * Returns an instance of the class, which is a Singleton.
     *
     * @return              the instance
     */
    public static ServerMain getInstance() {
        if (instance == null){
            instance = new ServerMain();
        }
        return instance;
    }


    /**
     * Getter for currentGames. Only for testing.
     *
     * @return the list of current games.
     */
    List<GameEngine> getCurrentGames() {
        return currentGames;
    }


    /**
     * Getter for waitingPlayers. Only for testing.
     *
     * @return the list of waiting players.
     */
    List<VirtualView> getWaitingPlayers() {
        return waitingPlayers;
    }


    /**
     * Getter for oldMessage. Only for testing.
     *
     * @return the value of oldMessage.
     */
    String getOldMessage() { return oldMessage; }

    /**
     * Main method instantiating TCP (on a different thread) and RMI servers. It runs a main loop checking for user input
     * from System.in to close the server, then manages incoming and outgoing messages from sockets being used. Finally,
     * it starts a new game if the number of players waiting and the timer respect given conditions.
     *
     * @param args  arguments
     */
    public static void main(String[] args){
        ServerMain  sm = getInstance();
        sm.setup();
        System.out.println(SETUP_COMPLETED_MESSAGE);
        while (sm.running){
            sm.manageInput();
            sm.refreshConnections();
            sm.removeSuspendedPlayers();
            sm.matchmaking();
            try {
                TimeUnit.MILLISECONDS.sleep(SLEEP_TIMEOUT);
            }catch(InterruptedException ex){
                LOGGER.log(Level.INFO,"Skipped waiting time.");
                Thread.currentThread().interrupt();
            }
        }
        System.exit(0);
    }


    /**
     * Initializes the logger, loads properties and creates a reader of System.in, RMI and TCP servers.
     */
    void setup(){
        this.initializeLogger();
        LOGGER.log(Level.FINE, "Logger initialized");

        Properties prop = this.loadConfig();
        LOGGER.log(Level.FINE, "Config read from file");

        System.setProperty("java.rmi.server.hostname",prop.getProperty("myIP", "localhost"));

        this.tcpServer = new TCPServer(Integer.parseInt(prop.getProperty("TCPPort", "4197")));
        this.executor.submit(this.tcpServer);
        this.rmiServer = new RMIServer(Integer.parseInt(prop.getProperty("RMIPort", "3993")));
        this.rmiServer.setup();
        LOGGER.log(Level.FINE, "TCPServer and RMIServer running");

        this.timer = new Timer(Integer.parseInt(prop.getProperty("matchmakingTime", "60")));
        this.timer.reset();
        LOGGER.log(Level.FINE, "Timer initialized");

        this.in = new BufferedReader(new InputStreamReader(System.in));
        this.running = true;
    }


    /**
     * Removes a game from tracked ones. Tracked games are games that are still being played.
     *
     * @param engine        the game to be removed
     */
    void untrackGame(GameEngine engine){
        currentGames.remove(engine);
        players.removeAll(engine.getPlayers());
    }


    /**
     * Adds a player to the waiting list. A waiting list exist at all times and, when it reach a sufficient sizes,
     * a set of players is removed from it and starts a game.
     *
     * @param p             the player to be added
     */
    void addPlayer(VirtualView p){
        waitingPlayers.add(p);
        players.add(p);
        LOGGER.log(Level.FINE, "Player added: " + p.getName());
    }


    /**
     * Checks if a player can be added to the waiting list and, if it can, adds it. His name must not be already taken.
     *
     * @param p             the player attempting to log in
     * @return              true if login was successful, false if somebody else logged in
     */
    public synchronized boolean login( VirtualView p){
        LOGGER.log(Level.FINE, "Someone is attempting to login as {0}.", p.getName());
        for(VirtualView pc : players){
            if(pc.getName().equals(p.getName())){
                LOGGER.log(Level.FINE, "Login unsuccessful for {0}.", p.getName());
                return false;
            }
        }
        addPlayer(p);
        LOGGER.log(Level.INFO,"{0} logged in", p.getName());
        return true;
    }


    /**
     * Checks if the player chose a name belonging to a suspended player and can therefore resume.
     *
     * @param name          the player's name
     * @return              true if the player can resume his game, else false
     */
    public synchronized boolean canResume(String name){
        for(VirtualView p : players){
            if (p.getName().equals(name) && p.isSuspended()){
                return true;
            }
        }
        return false;
    }


    /**
     * Resumes a player's game, given that he canResume().
     *
     * @param p             the player attempting to resume
     * @return              true if the operation was successful, else false
     */
    public synchronized boolean resume(VirtualView p) {
        for (GameEngine g : currentGames) {
            if (g.tryResuming(p)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Removes players who were suspended while still waiting for a game. There is no need to reserve their nicknames
     * as they have not made any progress in any running game.
     */
    synchronized void removeSuspendedPlayers(){
        for (VirtualView p : new ArrayList<>(waitingPlayers)){
            if(p.isSuspended()){
                players.remove(p);
                waitingPlayers.remove(p);
                LOGGER.log(Level.INFO, "{0} was removed", p.getName());
            }
        }
    }


    /**
     * Getter for the list of players.
     *
     * @return              the comprehensive list of all players, waiting or in a game
     */
    public synchronized List<VirtualView> getPlayers() {
        return players;
    }


    /**
     * Initializes the logger.
     */
    void initializeLogger(){
        try {
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.SEVERE);
            FileHandler fileHandler = new FileHandler(SERVER_LOG_FILENAME);
            LOGGER.setLevel(Level.SEVERE);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            LOGGER.addHandler(consoleHandler);
            LOGGER.setUseParentHandlers(false);
        }catch (IOException ex){LOGGER.log(Level.SEVERE, "IOException thrown while creating logger", ex);}
        LOGGER.setLevel(Level.SEVERE);
    }


    /**
     * Tries to load config from
     *      - a .properties file in the same folder as the jar
     *      - the .properties file inside the jar (if the previous option is fails)
     *
     * @return              the loaded properties or empty properties if failed
     */
    Properties loadConfig(){
        Properties prop = new Properties();
        try (InputStream input = new FileInputStream("server.properties")) {
            prop.load(input);
            return prop;
        } catch (IOException ex) {
            LOGGER.log(Level.INFO, "Cannot load client config from file", ex);
        }
        try {
            InputStream input = getClass().getResourceAsStream(SERVER_PROPERTIES_FILENAME);
            prop.load(input);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "IOException while loading config", ex);
        }
        return prop;
    }


    /**
     * Handles input from keyboard (currently only used to gracefully shutdown the server).
     */
    private synchronized void manageInput(){
        try{
            if (in.ready()) {
                if(in.readLine().equalsIgnoreCase(QUIT_KEY)){
                    System.out.println(QUITTING_MESSAGE);
                    running = false;
                    tcpServer.shutdown();
                    rmiServer.shutdown();
                    players.clear();
                    waitingPlayers.clear();
                    currentGames.clear();
                }else{
                    System.out.println(QUITTING_PROMPT);
                }
            }
        }catch(IOException e){
            LOGGER.log(Level.SEVERE, "IOException in main loop", e);
        }
    }


    /**
     * Refreshes connections: forwards TCP messages and checks for activity or client disconnection. This class mantains a list
     * of all connection to waiting, active and suspended players and checks it periodically. Since it runs on a separate thread
     * from all the GameEngines, this means that messages can be received asynchronously.
     */
    private void refreshConnections(){
        for (VirtualView p : new ArrayList<>(this.players)) {
            if(!p.isSuspended()){
                p.refresh();
            }
        }
    }


    /**
     * Start a game if certain conditions are satisfied (more than MAX_PLAYERS waiting or more than MIN_PLAYERS waiting for long enough.
     * It also handles the matchmaking timer and sends a list of waiting players to all waiting players.
     */
    synchronized void matchmaking(){
        List <VirtualView> selectedPlayers = new ArrayList<>();
        if (waitingPlayers.size() >= MAX_PLAYERS || (timer.isOver() && waitingPlayers.size() >= MIN_PLAYERS)) {
            for (int i = 0; i < waitingPlayers.size() && i < MAX_PLAYERS; i++) {
                selectedPlayers.add(waitingPlayers.get(i));
            }
            GameEngine current = new GameEngine(new ArrayList<>(selectedPlayers));
            executor.submit(current);
            currentGames.add(current);
            System.out.println(GAME_STARTED_MESSAGE + selectedPlayers.size() + " players");
            waitingPlayers.removeAll(selectedPlayers);
        } else if (waitingPlayers.size() < MIN_PLAYERS) {
            timer.stop();
        } else if (!timer.isRunning()) {
            timer.start();
        }

        //makes sure that the list is only sent if it changes
        String alreadyConnected = getAlreadyConnected();
        String fullMessage = alreadyConnected + TIME_LEFT_MESSAGE + timer.getTimeLeft() + ENTER + (timer.isRunning()? STARTING_GAME_MESSAGE:WAITING_MESSAGE);
        if(!alreadyConnected.isEmpty()&&!oldMessage.equals(fullMessage)) {
            for(VirtualView v : waitingPlayers){
                    v.display(fullMessage);
            }
        }
        oldMessage = fullMessage;
    }


    /**
     * Returns a list of waiting players formatted as a String.
     *
     * @return      a String containing all players already logged in
     */
    public String getAlreadyConnected(){
        if(waitingPlayers.isEmpty()){
            return "";
        }
        StringBuilder bld = new StringBuilder();
        bld.append(CONNECTED_LIST_MESSAGE);
        for(VirtualView v : waitingPlayers){
            bld.append(ENTER);
            bld.append(TAB);
            bld.append(v.getName());
        }
        bld.append(ENTER);
        return bld.toString();
    }
}