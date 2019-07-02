package it.polimi.ingsw.model.exceptions;

/**
 * Exception thrown when someone tries to access an attribute that has not been set.
 * It avoid the return of a null value.
 *
 * @author BassaniRiccardo.
 */

public class NotAvailableAttributeException extends Exception{

    public NotAvailableAttributeException(String str) { super(str); }

}
