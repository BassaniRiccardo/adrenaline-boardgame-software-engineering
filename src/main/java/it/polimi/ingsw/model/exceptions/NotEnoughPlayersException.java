package it.polimi.ingsw.model.exceptions;

/**
 * Exception thrown when there are not enough players to continue the game.
 *
 * @author marcobaga.
 */


public class NotEnoughPlayersException extends Exception {

    public NotEnoughPlayersException(String str){super(str);}

}
