package it.polimi.ingsw.model;

public class NoMoreCardsException extends Exception{

        NoMoreCardsException(){};

        NoMoreCardsException(String str)
        {
            super(str);
        }

}



