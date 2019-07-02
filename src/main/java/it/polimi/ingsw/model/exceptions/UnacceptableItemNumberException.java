package it.polimi.ingsw.model.exceptions;

/**
 * Exception thrown when an operation would lead to an unacceptable number of items of a specific type.
 *
 * @author BassaniRiccardo.
 */

public class UnacceptableItemNumberException extends Exception {

    public UnacceptableItemNumberException(String str)
    {
        super(str);
    }

}
