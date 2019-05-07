package it.polimi.ingsw.controller;

import it.polimi.ingsw.network.server.PlayerController;
import it.polimi.ingsw.network.server.RMIServer;
import it.polimi.ingsw.network.server.TCPServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

//TODO: check synchronization
//TODO: in depth testing
//TODO: read config from file

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
    public static void main(String[] args){ //leggere da config ip e porta
        ServerMain  sm = getInstance();
        System.out.println("Main method started");
        sm.timer = new MatchmakingTimer(60);

        sm.tcpServer = new TCPServer(5000, sm);
        sm.executor.submit(sm.tcpServer);

        sm.rmiServer = new RMIServer();
        sm.rmiServer.setup();

        System.out.println("TCPServer and RMIServer running, press q to quit");

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        Boolean running = true;

        sm.timer.reset();

        while (running){
            try{
                if (in.ready()) {
                    if(in.readLine().equals("q")){
                        System.out.println("Quitting");
                        running = false;
                        sm.tcpServer.shutdown();
                        //shutdwn rmiserver as well
                    }else{
                        System.out.println("Press q to quit");
                    }
                }
            }catch(IOException e){
                System.out.println("Cannot reach keyboard");
            }
            synchronized (sm) {
                ///new part for refreshing connections
                for (PlayerController p : new ArrayList<PlayerController>(sm.players)) {
                    p.refresh();
                }
                ///
                if (sm.waitingPlayers.size() > 4 || (sm.timer.isOver() && sm.waitingPlayers.size() > 2)) {
                    sm.selectedPlayers.clear();
                    for (int i = 0; i < sm.waitingPlayers.size() && i < 5; i++) {
                        sm.selectedPlayers.add(sm.waitingPlayers.get(i));
                    }
                    System.out.println("Starting a" + sm.selectedPlayers.size() + "-player game");
                    GameEngine current = new GameEngine(sm.selectedPlayers);
                    sm.executor.submit(current);
                    sm.currentGames.add(current);
                    System.out.println("Game started");
                    sm.waitingPlayers.removeAll(sm.selectedPlayers); //toglie i primi n
                } else if (sm.waitingPlayers.size() < 3) {
                    sm.timer.stop();
                } else if (sm.waitingPlayers.size() > 2 && !sm.timer.isRunning()) {
                    sm.timer.start();
                }
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
        System.out.println("Someone is attempting to login" + name + p);
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
}