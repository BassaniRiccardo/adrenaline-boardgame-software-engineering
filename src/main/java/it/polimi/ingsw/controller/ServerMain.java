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
            }
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            }catch(InterruptedException ex){
                System.out.println("Skipped waiting time.");
                Thread.currentThread().interrupt();
            }
        }
        System.exit(0);
    }

    private void setup(){
        this.initializeLogger();
        LOGGER.log(Level.INFO,"Main method started");
        LOGGER.log(Level.FINE, "Logger initialized");

        Properties prop = this.loadConfig();
        LOGGER.log(Level.FINE, "Config read from file");

        this.tcpServer = new TCPServer(Integer.parseInt(prop.getProperty("TCPPort", "5000")), this);
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
    public void addPlayer(PlayerController p){
        waitingPlayers.add(p);
        players.add(p);
    }

    /**
     * Checks if a player can be added to the waiting list and, if it can, adds it.
     *
     * @param name          the player's name
     * @param p             the player attempting to log in
     */
    public synchronized boolean login(String name, PlayerController p){      //this method needs to be synchronized most likely
        LOGGER.log(Level.INFO, "Someone is attempting to login: {0}", p);
        for(PlayerController pc : players){
            if(pc.getName().equals(name)){
                System.out.println("Login unsuccessful");
                return false;
            }
        }
        System.out.println("Login should be successful");
        addPlayer(p);
        System.out.println("Login successful");
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
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Resumes players if they disconnected while still waiting
     *
     * @param p             the player who disconnected
     */
    public synchronized void removeIfWaiting(PlayerController p){
        for (GameEngine g : currentGames) {
            for (PlayerController old : g.getPlayers()) {
                if (old==p) {
                    return;
                }
            }
        }
        players.remove(p);
        waitingPlayers.remove(p);
    }

    /**
     * Getter for the list of players
     *
     * @return              the comprehensive list of players
     */
    public synchronized List<PlayerController> getPlayers() {
        return players;
    }

    public void initializeLogger(){
        try {
            FileHandler FILEHANDLER = new FileHandler("serverLog.txt");
            FILEHANDLER.setLevel(Level.ALL);
            FILEHANDLER.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(FILEHANDLER);
        }catch (IOException ex){LOGGER.log(Level.SEVERE, "IOException thrown while creating logger", ex);}
        LOGGER.setLevel(Level.ALL);
    }

    private Properties loadConfig(){
        Properties prop = new Properties();
        try (InputStream input = new FileInputStream("server.properties")) {
            prop.load(input);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "IOException while loading config", ex);
        }
        return prop;
    }

    private void manageInput(){
        try{
            if (in.ready()) {
                if(in.readLine().equals("q")){
                    System.out.println("Quitting");
                    running = false;
                    this.tcpServer.shutdown();
                    //this.rmiServer.shutdown();
                }else{
                    System.out.println("Press q to quit");
                }
            }
        }catch(IOException e){
            LOGGER.log(Level.SEVERE, "IOException in main loop", e);
        }
    }

    private void refreshConnections(){
        for (PlayerController p : new ArrayList<>(this.players)) {
            p.refresh();
        }
    }

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