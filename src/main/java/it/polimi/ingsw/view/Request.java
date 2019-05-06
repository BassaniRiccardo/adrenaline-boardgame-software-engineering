package it.polimi.ingsw.view;

import it.polimi.ingsw.network.Connection;

/**
 * Functional interface representing requests from server
 *
 * @author  marcobaga
 */
public interface Request {

    /**
     * Contains the behaviour the client must follow to fulfill the request
     *
     * @param clientMain    the target client
     * @param ui            the client's ui
     * @param connection    the client's connection
     */
    void manage(ClientMain clientMain, UI ui, Connection connection);
}