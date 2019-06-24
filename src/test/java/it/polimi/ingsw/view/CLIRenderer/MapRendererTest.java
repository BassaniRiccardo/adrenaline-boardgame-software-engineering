package it.polimi.ingsw.view.CLIRenderer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.tools.javac.Main;
import it.polimi.ingsw.controller.BoardConfigurer;
import it.polimi.ingsw.model.Updater;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.AmmoPack;
import it.polimi.ingsw.model.exceptions.NoMoreCardsException;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;
import it.polimi.ingsw.model.exceptions.UnacceptableItemNumberException;
import it.polimi.ingsw.model.exceptions.WrongTimeException;
import it.polimi.ingsw.view.ClientMain;
import it.polimi.ingsw.view.ClientModel;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class MapRendererTest {

    @Test
    public void getMap() {
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

        MapRenderer mapRenderer = new MapRenderer();

        MainRenderer.drawModel(mapRenderer.getMap(clientMain.getClientModel()));
    }

    @Test
    public void placeSquareOnMap() {
        String[][] map = new String[18][55];
        for(int i=0; i<18; i++){
            for(int j = 0; j<55; j++){
                map[i][j] = " ";
            }
        }
        List<String> ammo = new ArrayList<>();
        ammo.add("x");
        ammo.add("y");
        ammo.add("z");
        List<String> players = new ArrayList<>();
        players.add("o");
        SquareRenderer square = new SquareRenderer(1, ammo, 4, players);
        MapRenderer.placeSquareOnMap(map, square, 1, 1);
        MainRenderer.drawModel(map);
    }

    @Test
    public void loadMap() {
        MapRenderer mapRenderer = new MapRenderer();
        String[][] s1 = mapRenderer.loadMap(1);
        String[][] s2 = mapRenderer.loadMap(5);
        String[][] s3 = mapRenderer.loadMap(2);
        for(int i=0; i<18; i++) {
            for(int j=0; j<55; j++) {
                assertTrue(s1[i][j].equals(s2[i][j]));
                assertTrue(s2[i][j].equals(s3[i][j]));
            }
        }
    }
}