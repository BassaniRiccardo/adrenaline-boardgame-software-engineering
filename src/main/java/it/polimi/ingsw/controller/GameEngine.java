package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.controller.ModelTranslator;
import it.polimi.ingsw.model.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

import static it.polimi.ingsw.model.Player.HeroName.*;


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
        HashMap<Player.HeroName, PlayerController> statues = new HashMap();
        List<Integer> mapVotes = new ArrayList<>();
        List<Integer> skullVotes = new ArrayList<>();
        List<Player.HeroName> chosenHeroes = new ArrayList<>();
        List<Player.HeroName> HeroList = new ArrayList<Player.HeroName>();
        HeroList.add(D_STRUCT_OR);
        HeroList.add(BANSHEE);
        HeroList.add(DOZER);
        HeroList.add(VIOLET);
        HeroList.add(SPROG);

        for (PlayerController p : players) {
/*           p.send("What board?");
            p.send("How many skulls?");
            p.send("What hero in hero list?");
            mapVotes.add(p.receive());
            skullVotes.add(p.receive());
            Player.HeroName name = p.receive();
            statues.put(name, p);
            HeroList.remove(name);
            chosenHeroes.add(name);*/
        }
        //creagitteboard
        //bind players
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
