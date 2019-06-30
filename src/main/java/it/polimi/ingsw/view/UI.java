package it.polimi.ingsw.view;

import java.util.List;

/**
 * Interface for graphical representation and client input management
 *
 * @author  marcobaga
 */
public interface UI extends Runnable {

    /**
     * Displays a message to the user.
     *
     * @param message   message to display
     */
    void display(String message);

    /**
     * Displays a request to the user with a character limit for his input.
     *
     * @param message   message to display
     */
    void display(String message, String max);

    /**
     * Displays a message and a list of options to the user.
     *
     * @param message   message to display
     * @param options   options to display
     */
    void display(String type, String message, List<String> options);


    /**
     * Returns the user's input. Is usually called after display(String message).
     *
     * @return      the string typed or chosen by the user
     */
    String get(String max);

    /**
     * Returns the user's choice among a list.
     *
     * @param list  options to choose from
     * @return      a String containing the number of the option chosen
     */
    String get(List<String> list);

    /**
     * Redraws the interface.
     */
    void render();

    /**
     * Displays a message signaling disconnection from the server.
     */
    void displayDisconnection();

    /**
     * Displays a message signaling that the user was suspended by the server.
     */
    void displaySuspension();

    /**
     * Displays a message describing the results of the game.
     *
     * @param message   information abount the result of the game
     */
    void displayEnd(String message);

    /**
     * Adds a message representing a change in the game state to the list of past events, that can be displayed
     * by the UI.
     *
     * @param message   description of a change in the game state
     */
    void addHistory(String message);
}