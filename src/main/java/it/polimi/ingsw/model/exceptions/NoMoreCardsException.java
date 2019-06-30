package it.polimi.ingsw.model.exceptions;

/**
 * Exception thrown when someone tries to draw a card form an empty deck or to collect it from an empty square.
 *
 * @author BassaniRiccardo.
 */

public class NoMoreCardsException extends Exception{

        public NoMoreCardsException(){}

        public NoMoreCardsException(String str)
        {
            super(str);
        }

}



