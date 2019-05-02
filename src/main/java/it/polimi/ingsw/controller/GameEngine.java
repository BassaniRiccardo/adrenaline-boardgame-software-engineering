package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Board;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


//TODO: finish implementing
public class GameEngine implements Runnable{

    private List<PlayerController> players;
    private PlayerController currentPlayer;
    private Board board;
    private boolean gameOver;

    public GameEngine(List<PlayerController> players){
        this.players = players;
    }

    //this method requires players to contain between 3 and 5 players
    public void run(){

        setup();

        ExecutorService executor = Executors.newCachedThreadPool(); //can it be used?

        while (!gameOver){

            Future future = executor.submit(new TurnManager(board, currentPlayer, players));

            try {
                future.get(1, TimeUnit.MINUTES); // use future
            } catch (TimeoutException ex) { currentPlayer.suspend();
            } catch (Exception ex) { ex.printStackTrace();} //proper handling to be implemented

            currentPlayer = getNextPlayer();
            long playerCount = players.stream().filter(x->!x.isSuspended()).count();
            if(playerCount<3){
                gameOver = true;
            }
        }
        resolve();
    }

    public void setup(){
        List<String> HeroList = new ArrayList<>();
        for (PlayerController p : players) {
            p.send("what hero do you want?", null);
            //sethero
        }
        //configure board
    }

    public void resolve(){

        //select winner and so on

    }

    private PlayerController getNextPlayer(){   //must be robust for an empty list of player
        int ind = players.indexOf(currentPlayer);
        PlayerController res;
        if (ind<players.size()-1) {
            res = players.get(ind);
        }
        res = players.get(0);
        while(res.isSuspended()){
            ind++;
            if(ind>players.size()-1){
                ind = 0;
            }
            res = players.get(ind);
        }
        return res;
    }

    public List<PlayerController> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerController> players) {
        this.players = players;
    }

    public void setPlayers(int index, PlayerController p) {
        this.players.set(index, p);     //this and other methods need to be synchronized
    }
}
