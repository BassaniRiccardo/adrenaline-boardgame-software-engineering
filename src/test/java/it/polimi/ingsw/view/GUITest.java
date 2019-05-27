package it.polimi.ingsw.view;

import com.google.gson.Gson;
import it.polimi.ingsw.controller.BoardConfigurer;
import it.polimi.ingsw.controller.PowerUpFactory;
import it.polimi.ingsw.controller.WeaponFactory;
import it.polimi.ingsw.model.Updater;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Deck;
import it.polimi.ingsw.model.board.KillShotTrack;
import it.polimi.ingsw.model.board.Player;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.exceptions.NoMoreCardsException;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;
import it.polimi.ingsw.model.exceptions.UnacceptableItemNumberException;
import org.junit.Test;


public class GUITest {


    @Test
    public void render() throws UnacceptableItemNumberException, NoMoreCardsException {

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
        ((GUI)ui).getClientMain().setClientModel(Updater.getModelObject(board, board.getPlayers().get(0)));
        ((GUI) ui).render();
        try {
            Thread.sleep(10000);
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