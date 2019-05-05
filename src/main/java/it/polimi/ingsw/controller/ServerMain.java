package it.polimi.ingsw.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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

    public void main(){

        getInstance();

        System.out.println("Main method started");

        timer = new MatchmakingTimer(60);

        ExecutorService executor = Executors.newCachedThreadPool(); //can it be used?
        server = new TCPServer(1234, this);
        executor.submit(server);

        System.out.println("TCPServer running, press q to quit");

        timer.reset();

        while (true){
            if(scanner.hasNextLine()){
                if(scanner.nextLine()=="q") {
                    break;
                }
                else{
                    System.out.println("Press q to quit");
                }
            }//might throw exception
            if(waitingPlayers.size()>4||(timer.isOver()&&waitingPlayers.size()>2)){
                selectedPlayers.clear();
                for (int i = 0; i<waitingPlayers.size()&&i<5; i++){
                    selectedPlayers.add(waitingPlayers.get(i));
                }
                System.out.println("Starting a" + selectedPlayers.size() + "-player game");
                GameEngine current = new GameEngine(selectedPlayers);
                executor.submit(current);
                currentGames.add(current);
                System.out.println("Game started");
                waitingPlayers.removeAll(selectedPlayers); //toglie i primi n
            }else if (waitingPlayers.size()<3){
                timer.stop();
            } else if (waitingPlayers.size()>2 && !timer.isRunning()) {
                timer.start();
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
        if(players.stream().filter(x->x.getName().equals(name)).collect(Collectors.toList()).isEmpty()){
            addPlayer(p);
            return true;
        }
        return false;
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
                    g.setPlayer(g.getPlayers().indexOf(old), p);
                    return true;
                }
            }
        }
        return false;
    }
}