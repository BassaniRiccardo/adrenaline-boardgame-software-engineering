package it.polimi.ingsw.view.CLIRenderer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.ingsw.controller.BoardConfigurer;
import it.polimi.ingsw.model.Updater;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.AmmoPack;
import it.polimi.ingsw.model.exceptions.NoMoreCardsException;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;
import it.polimi.ingsw.model.exceptions.UnacceptableItemNumberException;
import it.polimi.ingsw.model.exceptions.WrongTimeException;
import it.polimi.ingsw.view.CLI;
import it.polimi.ingsw.view.ClientMain;
import it.polimi.ingsw.view.ClientModel;
import org.junit.Test;

import static org.junit.Assert.*;

public class HandRendererTest {

    @Test
    public void get() {
        Board board = new Board();
        try {
            board = BoardConfigurer.simulateScenario();
            board.getPlayers().get(0).sufferDamage(3, board.getPlayers().get(2));
            board.getPlayers().get(2).sufferDamage(12, board.getPlayers().get(1));
            board.getPlayers().get(0).addMarks(2, board.getPlayers().get(3));
            board.getPlayers().get(2).setDead(true);
            board.getKillShotTrack().registerKill(board.getPlayers().get(0), board.getPlayers().get(2), false);
            board.getPlayers().get(2).setAmmoPack(new AmmoPack(1, 2, 3));
        }catch (UnacceptableItemNumberException ex){
            assertTrue(false); }
        catch (NoMoreCardsException ex){
            assertTrue(false);}
        catch (NotAvailableAttributeException ex){
            assertTrue(false);}
        catch (WrongTimeException ex){
            assertTrue(false);}

        ClientMain clientMain = new ClientMain();

        JsonObject mod = new JsonParser().parse((Updater.getModel(board, board.getPlayers().get(0))).get("mod").getAsString()).getAsJsonObject();
        clientMain.setClientModel(new Gson().fromJson(mod, ClientModel.class));
        clientMain.getClientModel().setCurrentPlayerId(2);

        MainRenderer.drawModel(HandRenderer.get(clientMain.getClientModel()));
    }
}