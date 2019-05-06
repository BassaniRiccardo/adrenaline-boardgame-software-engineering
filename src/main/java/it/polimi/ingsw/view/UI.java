package it.polimi.ingsw.view;

public interface UI extends Runnable {

    void display(String message);
    String get();
}
