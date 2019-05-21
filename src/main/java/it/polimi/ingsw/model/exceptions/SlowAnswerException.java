package it.polimi.ingsw.model.exceptions;

public class SlowAnswerException extends Exception{

    public SlowAnswerException(){}

    public SlowAnswerException(String str)
    {
        super(str);
    }

}