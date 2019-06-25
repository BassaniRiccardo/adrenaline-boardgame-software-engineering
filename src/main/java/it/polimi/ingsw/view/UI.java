package it.polimi.ingsw.view;

import java.util.List;

//TODO: localize language

/**
 * Interface for user interface and managing client's input/output
 *
 * @author  marcobaga
 */
public interface UI extends Runnable {

    /**
     * Displays a message to the user
     *
     * @param message message to be displayed
     */
    void display(String message);

    /**
     * Displays a request to the user
     *
     * @param message message to be displayed
     */
    void display(String message, String max);

    /**
     * Displays a message and a list of options to the user
     *
     * @param message message to be displayed
     * @param options options to be displayed
     */
    void display(String message, List<String> options);


    /**
     * Returns the user's input
     *
     * @return the string typed or chosen by the user
     */
    //String get();

    String get(String max);

    String get(List<String> list);

    void render();

    void displayDisconnection();

    void displaySuspension();

    void displayEnd(String message);

    void addHistory(String message);
}



/*
La UI è una sorgente di eventi a cui qualcuno si è registrato ("se succede qualcosa, avvisami e spiegamelo, io lo gestisco"
Un evento è una classe/scatola
I listener ascoltano particolarei eventi.
 */