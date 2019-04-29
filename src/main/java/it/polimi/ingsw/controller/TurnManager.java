package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Board;
import java.util.List;

//TODO: finish implementing
public class TurnManager implements Runnable{

    private Board board;
    private PlayerController currentPlayer;
    private List<PlayerController> players;
    private ModelTranslator translator;

    public TurnManager(Board board, PlayerController currentPlayer, List<PlayerController> players){
        this.board = board;
        this.currentPlayer = currentPlayer;
        this.players = players;
        this.translator = new ModelTranslator();
    }

    public void run(){
        //if player is dead
            //respawn: discards a card, places figurine
        //while there are ations left
            chooseaction();
            executeaction();
            //watch out: the actual model needs to be updated only when the action is confirmed
        }

    private void chooseaction(){
        //getavailableactions
    }
    private void shoot(){
        //player.getavailableweapons()
        //playercontroller.ask()
        //getweapon
        //getavailablefiremodes()
        //playercontroller.ask()
        //selecttargets
        //select destinations()
        //shoot
    }
    private void move(){
        //playercontroller.ask(encode(getdestinations()));
        //player.move(decode(answer()));

    }

    private void pickup(){
        //playercontroller.ask(encode(getdestinations()));
        //player.move(decode(answer()));
        //player.pickup();
    }
    private void reload(){
        //playercontrolelr.ask(encode(getrechargeableweapons));
        //player.reload(decode(answer()));
    }

    private void executeaction() {
        //carries out the action selected
    }
}