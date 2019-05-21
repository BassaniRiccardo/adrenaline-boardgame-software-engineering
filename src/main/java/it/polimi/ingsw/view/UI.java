package it.polimi.ingsw.view;

import java.util.List;

/**
 * Interface for user interface and managing client's input/output
 *
 * @author  marcobaga
 */
public interface UI extends Runnable {

    /**
     * Displays a message to the user
     *
     * @param message   message to be displayed
     */
    void displayMessage(String message);

    /**
     * Displays a request to the user
     *
     * @param message   message to be displayed
     */
    void displayRequest(String message);


    /**
     * Returns the user's input
     *
     * @return          the string typed or chosen by the user
     */
    String get();

    String get(List<String> list);

    void displayOptions(String message, List<String> list);
}


/*

La UI è una sorgente di eventi a ccui qualcuno si è registrato ("se succede qualcosa, avvisami e spiegamelo, io lo gestisco"
Un evento è una classe/scatola
I listener ascoltano particolarei eventi



 */