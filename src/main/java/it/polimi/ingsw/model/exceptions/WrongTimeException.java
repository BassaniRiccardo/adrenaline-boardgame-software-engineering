package it.polimi.ingsw.model.exceptions;

/**
 * Exception thrown when an operation is performed in the wrong moment during the game.
 *
 * @author BassaniRiccardo.
 */

public class WrongTimeException extends Exception {

    public WrongTimeException() {}

    public WrongTimeException(String str ){ super(str);}

}
