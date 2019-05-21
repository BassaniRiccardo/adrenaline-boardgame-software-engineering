package it.polimi.ingsw.view;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.polimi.ingsw.network.client.Connection;

import java.util.ArrayList;
import java.util.List;

//TODO: finish implementing

/**
 * Static class translating incoming messages from the server to self-managing requests such as input requests or model updates
 *
 * @author  marcobaga
 */
public class RequestFactory {

    private RequestFactory(){}

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

    public static Request toRequest(JsonObject jMessage){

        String head = jMessage.get("head").getAsString();

        if(head.equals("MSG")) {
            return (ClientMain clientMain, UI ui, Connection connection) -> {
                ui.display(jMessage.get("text").getAsString());
            };
        }
        if (head.equals("REQ")) {
            return (ClientMain clientMain, UI ui, Connection connection) -> {
                ui.display(jMessage.get("text").getAsString());
                connection.send(ui.get(jMessage.get("length").getAsString()));
                connection.send(ui.get());
            };
        }
        if (head.equals("OPT")){
            return (ClientMain clientMain, UI ui, Connection connection) -> {
                JsonArray arr = jMessage.getAsJsonArray("options");
                List<String> list = new ArrayList<>();
                for(int i = 0; i<arr.size(); i++){
                    list.add(arr.get(i).getAsString());
                }
                ui.display(jMessage.get("text").getAsString(), list);
                connection.send(ui.get(list));
            };
        }
        if (head.equals("UPD")){
            return (ClientMain clientMain, UI ui, Connection connection) -> {
                JsonObject mod = jMessage.getAsJsonObject("mod");
                Gson gson = new Gson();
                ClientModel clientModel = gson.fromJson(mod.toString(), ClientModel.class);

                clientMain.setClientModel(clientModel);
                ui.render();
            };
        }

        else {
            return (ClientMain clientMain, UI ui, Connection connection) -> {
                ui.display("error: received a message with header" + jMessage.get("head").getAsString());
            };
        }
    }
}