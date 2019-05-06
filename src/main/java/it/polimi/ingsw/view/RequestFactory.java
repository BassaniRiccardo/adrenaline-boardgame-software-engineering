package it.polimi.ingsw.view;

import it.polimi.ingsw.network.Connection;

//TODO: carry on implementing
public class RequestFactory {

    public static Request toRequest(String message){
        //translates network messages to self-managing requests
        //the request queries the user to answer a question: "revert turn" is a possible answer

        if(message.equals("quit")){
            return (ClientMain clientMain, UI ui, Connection connection)->{connection.shutdown(); System.exit(0);};
        }

        return (ClientMain clientMain, UI ui, Connection connection)->{
            ui.display(message);
            connection.send(ui.get());
        };
    }
}