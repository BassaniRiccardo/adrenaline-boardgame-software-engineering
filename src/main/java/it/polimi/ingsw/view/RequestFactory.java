package it.polimi.ingsw.view;

import it.polimi.ingsw.network.client.Connection;

//TODO: finish implementing

/**
 * Static class translating incoming messages from the server to self-managing requests such as input requests or model updates
 *
 * @author  marcobaga
 */
public class RequestFactory {

    /**
     * Translator method
     *
     * @param message   message to be translated
     * @return          resulting request
     */
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