package it.polimi.ingsw.controller;

import it.polimi.ingsw.network.server.PlayerController;
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

//TODO: check synchronization
//TODO: in depth testing
//FIX: if an rmi player disconnects, its name is kept locked
//TODO: ask the tutors if it is okay for the view not to observe anything, nut to be notified by the controller when it need to be updated

/**
 * Main class that manages connections and matchmaking.
 *
 * @author  marcobaga
 */
public class ServerMain {

    private static ServerMain instance;
    private List <PlayerController> players;
    private List <PlayerController> waitingPlayers;
    private List <PlayerController> selectedPlayers;
    private List <GameEngine> currentGames;
    private TCPServer tcpServer;
    private RMIServer rmiServer;
    private MatchmakingTimer timer;
    private ExecutorService executor;
    private BufferedReader in;
    private boolean running;
    private static final Logger LOGGER = Logger.getLogger("serverLogger");


    /**
     * Standard private constructor
     */
    private ServerMain(){
        players = new ArrayList<>();
        waitingPlayers = new ArrayList<>();
        selectedPlayers = new ArrayList<>();
        currentGames = new ArrayList<>();
        tcpServer = null;
        rmiServer = null;
        timer = null;
        executor = Executors.newCachedThreadPool();
    }

    /**
     * Returns an instance of the class, which is a Singleton
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
     * Main method instantiating TCP (on a different thread) and RMI servers. It runs a main loop checking for user input
     * from System.in to close the server, then manages incoming and outgoing messages from sockets being used. Finally,
     * it starts a new game if the number of players waiting and the timer respect given conditions.
     *
     * @param args  arguments
     */
    public static void main(String[] args){

        ServerMain  sm = getInstance();
        sm.setup();
        System.out.println("Setup completed, starting matchmaking, press q to quit");
        while (sm.running){
            sm.manageInput();
            synchronized (instance) {
                sm.refreshConnections();
                sm.matchmaking();
                sm.removeSuspendedPlayers();
            }
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            }catch(InterruptedException ex){
                LOGGER.log(Level.INFO,"Skipped waiting time.");
                Thread.currentThread().interrupt();
            }
        }
        System.exit(0);
    }

    /**
     * Initializes logger, a reader of System.in, RMI and TCP servers
     */
    private void setup(){
        this.initializeLogger();
        LOGGER.log(Level.INFO,"Main method started");
        LOGGER.log(Level.FINE, "Logger initialized");

        Properties prop = ServerMain.loadConfig();
        LOGGER.log(Level.FINE, "Config read from file");

        this.tcpServer = new TCPServer(Integer.parseInt(prop.getProperty("TCPPort", "5000")));
        this.executor.submit(this.tcpServer);
        this.rmiServer = new RMIServer(Integer.parseInt(prop.getProperty("RMIPort", "1420")));
        this.rmiServer.setup();
        LOGGER.log(Level.FINE, "TCPServer and RMIServer running, press q to quit");

        this.timer = new MatchmakingTimer(Integer.parseInt(prop.getProperty("matchmakingTime", "60")));
        this.timer.reset();
        LOGGER.log(Level.FINE, "MatchmakingTimer initialized");

        this.in = new BufferedReader(new InputStreamReader(System.in));
        this.running = true;
    }

    /**
     * Removes a game from tracked ones.
     *
     * @param engine        the game to be removed
     */
    public void untrackGame(GameEngine engine){
        currentGames.remove(engine);
    }

    /**
     * Adds a player to the waiting list
     *
     * @param p             the player to be added
     */
    private void addPlayer(PlayerController p){
        waitingPlayers.add(p);
        players.add(p);
        LOGGER.log(Level.FINE, "Player added: " + p.getName());
    }

    /**
     * Checks if a player can be added to the waiting list and, if it can, adds it.
     *
     * @param name          the player's name
     * @param p             the player attempting to log in
     */
    public synchronized boolean login(String name, PlayerController p){      //this method needs to be synchronized most likely
        LOGGER.log(Level.FINE, "Someone is attempting to login as {0}", name);
        for(PlayerController pc : players){
            if(pc.getName().equals(name)){
                LOGGER.log(Level.FINE, "Login unsuccessful");
                return false;
            }
        }
        addPlayer(p);
        LOGGER.log(Level.INFO,"{0} logged in", name);
        return true;
    }

    /**
     * Checks if the player chose a name belogning to a suspended player and can therefore resume
     *
     * @param name          the player's name
     * @return              true is the player can resume his game, else false
     */
    public synchronized boolean canResume(String name){
        for(PlayerController p : players){
            if (p.getName().equals(name)&&p.isSuspended()){
                return true;
            }
        }
        return false;
    }

    /**
     * Resumes a player's game, given that he canResume()
     *
     * @param name          the player's name
     * @param p             the player attempting to resume
     * @return              true if the operation was successful, else false
     */
    public synchronized boolean resume(String name, PlayerController p) {        //sychronize this
        for (GameEngine g : currentGames) {
            for (PlayerController old : g.getPlayers()) {
                if (old.getName().equals(name) && old.isSuspended()) {
                    g.setPlayer(g.getPlayers().indexOf(old), p);
                    LOGGER.log(Level.INFO,"{0} resumed", name);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Removes players who were suspended while till waiting for a game
     *
     */
    private synchronized void removeSuspendedPlayers(){
        for (PlayerController p : new ArrayList<>(waitingPlayers)){
            if(p.isSuspended()){
                players.remove(p);
                waitingPlayers.remove(p);
                LOGGER.log(Level.INFO, "{0} was removed", p.getName());
            }
        }
    }

    /**
     * Getter for the list of players
     *
     * @return              the comprehensive list of players
     */
    public synchronized List<PlayerController> getPlayers() {
        return players;
    }

    /**
     * Initializes the logger so that it writes to a txt file
     */
    private void initializeLogger(){
        try {
            FileHandler fileHandler = new FileHandler("serverLog.txt");
            fileHandler.setLevel(Level.FINE);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
        }catch (IOException ex){LOGGER.log(Level.SEVERE, "IOException thrown while creating logger", ex);}
        LOGGER.setLevel(Level.ALL);
    }

    /**
     * Loads config from file if possible
     *
     * @return              the loaded properties or empty properties if failed
     */
    public static Properties loadConfig(){
        Properties prop = new Properties();
        try (InputStream input = new FileInputStream("server.properties")) {
            prop.load(input);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "IOException while loading config", ex);
        }
        return prop;
    }

    /**
     * Handles input from keyboard (currently the only way to shutdown the server)
     */
    private void manageInput(){
        try{
            if (in.ready()) {
                if(in.readLine().equals("q")){
                    System.out.println("Quitting");
                    running = false;
                    tcpServer.shutdown();
                    rmiServer.shutdown();
                    players.clear();
                    waitingPlayers.clear();
                    selectedPlayers.clear();
                    currentGames.clear();
                }else{
                    System.out.println("Press q to quit");
                }
            }
        }catch(IOException e){
            LOGGER.log(Level.SEVERE, "IOException in main loop", e);
        }
    }

    /**
     * Refreshes connections: forwards TCP messages and checks for activity or client disconnection
     */
    private void refreshConnections(){
        for (PlayerController p : new ArrayList<>(this.players)) {
            p.refresh();
        }
    }

    /**
     * Start a game if certain conditions are satisfied
     */
    private void matchmaking(){
        if (waitingPlayers.size() > 4 || (timer.isOver() && waitingPlayers.size() > 2)) {
            selectedPlayers.clear();
            for (int i = 0; i < waitingPlayers.size() && i < 5; i++) {
                selectedPlayers.add(waitingPlayers.get(i));
            }
            GameEngine current = new GameEngine(selectedPlayers);
            executor.submit(current);
            currentGames.add(current);
            System.out.println("Game started with " + selectedPlayers.size() + " players");
            waitingPlayers.removeAll(selectedPlayers); //toglie i primi n
        } else if (waitingPlayers.size() < 3) {
            timer.stop();
        } else if (waitingPlayers.size() > 2 && !timer.isRunning()) {
            timer.start();
        }
    }
}