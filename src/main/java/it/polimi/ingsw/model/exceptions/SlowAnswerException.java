package it.polimi.ingsw.model.exceptions;

/**
 * Exception thrown when the player's answer does not arrive before the timer expires.
 *
 * @author marcobaga.
 */


public class SlowAnswerException extends Exception{

    public SlowAnswerException(String str)
    {
        super(str);
    }

}