package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//TODO: finish implementing
//TODO: check that multithreading does not cause issues
//TODO: manage exceptions
//TODO: testing
//TODO: read config from file

//Main class that istantiates a GameEngine for every game starting

public class ServerMain {

    private static ServerMain instance;
    private List <PlayerController> waitingPlayers;
    private List <PlayerController> selectedPlayers;
    private List <GameEngine> currentGames;
    private TCPServer server;
    private Scanner scanner;
    private List<PlayerController> players;
    private MatchmakingTimer timer;

    private ServerMain(){
        waitingPlayers = new ArrayList<>();
        selectedPlayers = new ArrayList<>();
        players = new ArrayList<>();
        currentGames = new ArrayList<>();
        server = null;
        scanner = new Scanner(System.in);
        timer = null;
    }

    public static ServerMain getInstance() {
        if (instance == null){
            instance = new ServerMain();
        }
        return instance;
    }

    public static void main(String[] args){

        ServerMain  sm = getInstance();

        System.out.println("Main method started");

        sm.timer = new MatchmakingTimer(60);

        ExecutorService executor = Executors.newCachedThreadPool(); //can it be used?
        sm.server = new TCPServer(5000, sm);
        executor.submit(sm.server);

        System.out.println("TCPServer running, press q to quit");

        sm.timer.reset();

        while (true){
            if(sm.scanner.hasNextLine()){
                if(sm.scanner.nextLine()=="q") {
                    break;
                }
                else{
                    System.out.println("Press q to quit");
                }
            }//might throw exception
            if(sm.waitingPlayers.size()>4||(sm.timer.isOver()&&sm.waitingPlayers.size()>2)){
                sm.selectedPlayers.clear();
                for (int i = 0; i<sm.waitingPlayers.size()&&i<5; i++){
                    sm.selectedPlayers.add(sm.waitingPlayers.get(i));
                }
                System.out.println("Starting a" + sm.selectedPlayers.size() + "-player game");
                GameEngine current = new GameEngine(sm.selectedPlayers);
                executor.submit(current);
                sm.currentGames.add(current);
                System.out.println("Game started");
                sm.waitingPlayers.removeAll(sm.selectedPlayers); //toglie i primi n
            }else if (sm.waitingPlayers.size()<3){
                sm.timer.stop();
            } else if (sm.waitingPlayers.size()>2 && !sm.timer.isRunning()) {
                sm.timer.start();
            }
        }
    }

    public void untrackGame(GameEngine engine){
        currentGames.remove(engine);
    }

    public void addPlayer(PlayerController p){
        waitingPlayers.add(p);
    }

    public boolean login(String name, PlayerController p){      //this method needs to be synchronized most likely
        System.out.println("Someone is attempting to login" + name + p);
        for(PlayerController pc : players){
            if(pc.getName()==name){
                System.out.println("Login unsuccessful");
                return false;
            }
        }
        System.out.println("Login should be successful");
        addPlayer(p);
        System.out.println("Login successful");
        return true;
    }

    public static boolean login(String name){
        //this is a remote function to be called by RMI clients to join a game
        //this class creates a playercontroller and calls addPlayer().
        //this class returns a remote playercontroller that the client can communicate to? or else the client must set an additional parameter so that it can be identified by the server at every call
        return false;
    }

    public boolean canResume(String name){
        for(PlayerController p : players){
            if (p.getName().equals(name)&&p.isSuspended()){
                return true;
            }
        }
        return false;
    }

    public boolean resume(String name, PlayerController p) {
        for (GameEngine g : currentGames) {
            for (PlayerController old : g.getPlayers()) {
                if (old.getName().equals(name) && old.isSuspended()) {
                    g.setPlayers(g.getPlayers().indexOf(old), p);
                    return true;
                }
            }
        }
        return false;
    }
}