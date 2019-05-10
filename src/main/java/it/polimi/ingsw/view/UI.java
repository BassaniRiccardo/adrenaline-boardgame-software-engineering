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
    void display(String message);

    /**
     * Returns the user's input
     *
     * @return          the string typed or chosen by the user
     */
    String get();

    String get(List<String> list);
}
