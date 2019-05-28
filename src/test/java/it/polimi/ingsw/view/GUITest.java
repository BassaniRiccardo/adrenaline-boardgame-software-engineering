package it.polimi.ingsw.view;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.ingsw.controller.BoardConfigurer;
import it.polimi.ingsw.model.Updater;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.exceptions.NoMoreCardsException;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;
import it.polimi.ingsw.model.exceptions.UnacceptableItemNumberException;
import org.junit.Test;


public class GUITest {


    @Test
    public void render() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {

         Board board = BoardConfigurer.simulateScenario();

        new Thread() {
            @Override
            public void run() {
                javafx.application.Application.launch(GUI.class);
            }
        }.start();
        UI ui = GUI.waitGUI();
        ClientMain clientMain = new ClientMain();
        ((GUI) ui).setClientMain(clientMain);

        JsonObject mod = new JsonParser().parse((Updater.getModel(board, board.getPlayers().get(0))).get("mod").getAsString()).getAsJsonObject();
        ((GUI)ui).getClientMain().setClientModel(new Gson().fromJson(mod, ClientModel.class));

        ((GUI) ui).render();
        try {
            Thread.sleep(2000);

        } catch (InterruptedException e){}



    }



    @Test
    public void display() {
        new Thread() {
            @Override
            public void run() {
                javafx.application.Application.launch(GUI.class);
            }
        }.start();
        GUI gui = GUI.waitGUI();
        gui.display("ciao");

    }
}