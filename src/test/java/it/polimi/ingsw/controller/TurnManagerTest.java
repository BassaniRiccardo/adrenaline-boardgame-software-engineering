package it.polimi.ingsw.controller;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.board.AmmoSquare;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Player;
import it.polimi.ingsw.model.board.Square;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.network.server.TCPVirtualView;
import it.polimi.ingsw.network.server.VirtualView;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static it.polimi.ingsw.controller.TurnManager.toStringList;
import static it.polimi.ingsw.controller.TurnManager.toUserStringList;
import static org.junit.Assert.*;

/**
 * Test the methods of TurnManager which can be tested without simulating a game.
 *
 * @author BassaniRiccardo
 */

public class TurnManagerTest {


    /**
     * Tests the method replaceWeapons(), checking that the drawn weapons are replace after it is called.
     *
     * @throws NoMoreCardsException
     */
    @Test
    public void replaceWeapons() throws NoMoreCardsException, NotEnoughPlayersException {

        class dummyVirtualView extends VirtualView {

            @Override
            public String getInputNow(String msg, int max) {
                return null;
            }

            @Override
            public int chooseNow(String type, String msg, List<?> options) {
                return 0;
            }

            @Override
            public void refresh() {

            }

            @Override
            public void shutdown() {

            }

            @Override
            public void showSuspension() {

            }

            @Override
            public void showEnd(String message) {

            }

            @Override
            public void choose(String type, String msg, List<?> options) {

            }

            @Override
            public void choose(String type, String msg, List<?> options, int timeoutSec) {

            }

            @Override
            public void update(JsonObject jsonObject) {
            }

            @Override
            public void display(String message){}

        }

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new dummyVirtualView());
        connections.add(new dummyVirtualView());
        connections.add(new dummyVirtualView());
        connections.add(new dummyVirtualView());
        connections.add(new dummyVirtualView());

        GameEngine gameEngine = new GameEngine(connections);
        StatusSaver statusSaver = new StatusSaver(gameEngine.getBoard());

        gameEngine.fakeSetup();

        TurnManager turnManager = new TurnManager(gameEngine, gameEngine.getBoard(), gameEngine.getCurrentPlayer(), connections, statusSaver, false);

        assertEquals(3, gameEngine.getBoard().getSpawnPoints().get(0).getWeapons().size());

        gameEngine.getBoard().getSpawnPoints().get(0).removeCard(gameEngine.getBoard().getSpawnPoints().get(0).getWeapons().get(0));
        assertEquals(2, gameEngine.getBoard().getSpawnPoints().get(0).getWeapons().size());

        turnManager.replaceWeapons();
        assertEquals(3, gameEngine.getBoard().getSpawnPoints().get(0).getWeapons().size());

    }

    /**
     * Tests the method replaceAmmoTiles.
     *
     * @throws NoMoreCardsException
     * @throws NotAvailableAttributeException
     */
    @Test()
    public void ReplaceAmmoTiles() throws NoMoreCardsException, NotAvailableAttributeException {

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        GameEngine gameEngine = new GameEngine(connections);
        StatusSaver statusSaver = new StatusSaver(gameEngine.getBoard());
        gameEngine.fakeSetup();
        TurnManager turnManager = new TurnManager(gameEngine, gameEngine.getBoard(), gameEngine.getCurrentPlayer(), connections, statusSaver, false);
        AmmoSquare as = gameEngine.getBoard().getAmmoSquares().get(0);
        assertTrue(as.hasAmmoTile());
        as.removeCard(as.getAmmoTile());
        assertFalse(as.hasAmmoTile());
        turnManager.replaceAmmoTiles();
        assertTrue(as.hasAmmoTile());
    }


    /**
     * Tests the method toStringList().
     */
    @Test
    public void toStringListTest() {

        Board b = BoardConfigurer.configureMap(1);
        List<Square> squareList = b.getMap();
        List<String> stringList = Arrays.asList("Square 0", "Square 1", "Square 2", "Square 3", "Square 4", "Square 5", "Square 6", "Square 7", "Square 8", "Square 9");
        assertEquals(stringList, toStringList(squareList));

    }


    /**
     * Tests the method userToStringList().
     *
     * @throws UnacceptableItemNumberException
     * @throws NoMoreCardsException
     */
    @Test
    public void userToStringListTest() throws UnacceptableItemNumberException, NoMoreCardsException{

        Board b = BoardConfigurer.simulateScenario();
        List<List<Player>> playerGroupsList = new ArrayList<>();
        Player p1 = b.getPlayers().get(0);
        p1.setUsername("Alberto");
        Player p2 = b.getPlayers().get(1);
        p2.setUsername("Barbara");
        Player p3 = b.getPlayers().get(2);
        p3.setUsername("Carlotta");
        playerGroupsList.add(Arrays.asList(p1,p2));
        playerGroupsList.add(Collections.singletonList(p3));

        //checks the generic list is transformed in a list of strings
        List<String> stringList = toUserStringList(playerGroupsList);

        //the name and the color are correct
        System.out.println("\nTesting TurnManager.userToStringList().\nAlberto is yellow. Barbara is blue. Carlotta is grey.\nThe output is printed to console since it is the better way to check the color of a string.\n" );
        //it is shown through a println() since it is not possible to check the color of a string in another way
        System.out.println(stringList);

    }


    @Test
    public void updateDeadTest() throws UnacceptableItemNumberException, NoMoreCardsException{

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.fakeSetup();
        Board b = gameEngine.getBoard();
        StatusSaver statusSaver = new StatusSaver(b);
        TurnManager turnManager = new TurnManager(gameEngine, gameEngine.getBoard(), gameEngine.getCurrentPlayer(), connections, statusSaver, false);
        for (int i=0; i<connections.size(); i++) {
            gameEngine.getPlayers().get(i).setPlayer(b.getPlayers().get(i));
        }

        //nobody is dead
        assertTrue(turnManager.getDead().isEmpty());
        b.getPlayers().get(0).setInGame(true);
        b.getPlayers().get(0).setDead(true);
        turnManager.updateDead();

        //the first player is dead and updateDead has been called
        assertEquals(Collections.singletonList(1), turnManager.getDead());
        b.getPlayers().get(1).setInGame(true);
        b.getPlayers().get(1).setDead(true);

        //the second player is dead but updateDead has not been called
        assertEquals(Collections.singletonList(1), turnManager.getDead());
        turnManager.updateDead();

        //update dead has been called
        assertEquals(Arrays.asList(1,2), turnManager.getDead());


    }

}