package it.polimi.ingsw.view;

import it.polimi.ingsw.network.Connection;

//represents a request from the server (such as "choose your target anomgst these" or "update your model according to these modifications") or from System.in (such as close)
public interface Request {
    void manage(ClientMain clientMain, UI ui, Connection connection);
}