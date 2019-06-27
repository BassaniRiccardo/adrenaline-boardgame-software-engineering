package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Player;
import it.polimi.ingsw.model.exceptions.NoMoreCardsException;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;
import it.polimi.ingsw.model.exceptions.UnacceptableItemNumberException;
import it.polimi.ingsw.network.server.TCPVirtualView;
import it.polimi.ingsw.network.server.VirtualView;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import static java.util.logging.Level.ALL;
import static java.util.logging.Level.SEVERE;
import static org.junit.Assert.*;

/**
 * Test the methods of ServerMain which can be tested without simulating a game.
 *
 * @author BassaniRiccardo
 */

public class ServerMainTest {

    /**
     * Tests the method untrackGame().
     *
     * @throws UnacceptableItemNumberException
     * @throws NoMoreCardsException
     */
    @Test
    public void untrackGame() throws UnacceptableItemNumberException, NoMoreCardsException {

        ServerMain sm = ServerMain.getInstance();
        Board b = BoardConfigurer.simulateScenario();

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.setBoard(b);
        for (int i=0; i<connections.size(); i++) {
            gameEngine.getPlayers().get(i).setPlayer(b.getPlayers().get(i));
        }

        sm.getCurrentGames().add(gameEngine);
        sm.untrackGame(gameEngine);
        assertTrue(sm.getCurrentGames().isEmpty());
    }


    /**
     * Tests the method addPlayer.
     */
    @Test
    public void addPlayer() {

        ServerMain sm = ServerMain.getInstance();
        sm.getPlayers().clear();
        sm.getWaitingPlayers().clear();
        VirtualView v1 = new TCPVirtualView(null);
        VirtualView v2 = new TCPVirtualView(null);

        sm.addPlayer(v1);
        sm.addPlayer(v2);
        assertEquals(Arrays.asList(v1, v2), sm.getPlayers());
        assertEquals(Arrays.asList(v1, v2), sm.getWaitingPlayers());

    }


    /**
     * Tests the method initializeLogger.
     */
    @Test
    public void initializeLogger() {
        ServerMain.initializeLogger();
        Logger LOGGER = Logger.getLogger("serverLogger");
        assertEquals(ALL, LOGGER.getLevel());
        LOGGER.setLevel(SEVERE);
    }


    /**
     * Tests the method canResume().
     *
     * @throws UnacceptableItemNumberException
     * @throws NoMoreCardsException
     */
    @Test
    public void canResume() throws UnacceptableItemNumberException, NoMoreCardsException {

        ServerMain sm = ServerMain.getInstance();
        sm.getPlayers().clear();
        sm.getWaitingPlayers().clear();
        Board b = BoardConfigurer.simulateScenario();

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.setBoard(b);
        for (int i=0; i<connections.size(); i++) {
            gameEngine.getPlayers().get(i).setPlayer(b.getPlayers().get(i));
        }

        b.getPlayers().get(0).setUsername("firstPlayer");
        connections.get(0).setName(b.getPlayers().get(0).getUsername());
        connections.get(0).setSuspended(true);
        b.getPlayers().get(1).setUsername("secondPlayer");
        connections.get(1).setName(b.getPlayers().get(1).getUsername());

        sm.addPlayer(connections.get(0));
        sm.addPlayer(connections.get(1));

        assertTrue(sm.canResume("firstPlayer"));
        assertFalse(sm.canResume("secondPlayer"));
        assertFalse(sm.canResume("thirdPlayer"));

    }


    /**
     * Tests the method removeSuspendedPlayers.
     *
     * @throws UnacceptableItemNumberException
     * @throws NoMoreCardsException
     */
    @Test
    public void removeSuspendedPlayers() throws UnacceptableItemNumberException, NoMoreCardsException {

        ServerMain sm = ServerMain.getInstance();
        sm.getPlayers().clear();
        sm.getWaitingPlayers().clear();
        Board b = BoardConfigurer.simulateScenario();

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));

        VirtualView v1 = connections.get(0);
        VirtualView v2 = connections.get(1);

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.setBoard(b);
        for (int i = 0; i < connections.size(); i++) {
            gameEngine.getPlayers().get(i).setPlayer(b.getPlayers().get(i));
        }

        b.getPlayers().get(0).setUsername("firstPlayer");
        v1.setName(b.getPlayers().get(0).getUsername());
        v1.setSuspended(true);
        b.getPlayers().get(1).setUsername("secondPlayer");
        v2.setName(b.getPlayers().get(1).getUsername());

        sm.addPlayer(v1);
        sm.addPlayer(v2);

        //assertEquals(Arrays.asList(v1, v2), sm.getPlayers());
        assertEquals(Arrays.asList(v1, v2), sm.getWaitingPlayers());

        sm.removeSuspendedPlayers();

        assertEquals(Collections.singletonList(v2), sm.getPlayers());
        assertEquals(Collections.singletonList(v2), sm.getWaitingPlayers());

    }


    /**
     * Tests the method getAlreadyConnected().
     *
     * @throws UnacceptableItemNumberException
     * @throws NoMoreCardsException
     */
    @Test
    public void getAlreadyConnected() throws UnacceptableItemNumberException, NoMoreCardsException {

        ServerMain sm = ServerMain.getInstance();
        sm.getPlayers().clear();
        sm.getWaitingPlayers().clear();
        Board b = BoardConfigurer.simulateScenario();

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));

        VirtualView v1 = connections.get(0);
        VirtualView v2 = connections.get(1);

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.setBoard(b);
        for (int i = 0; i < connections.size(); i++) {
            gameEngine.getPlayers().get(i).setPlayer(b.getPlayers().get(i));
        }

        b.getPlayers().get(0).setUsername("firstPlayer");
        v1.setName(b.getPlayers().get(0).getUsername());
        b.getPlayers().get(1).setUsername("secondPlayer");
        v2.setName(b.getPlayers().get(1).getUsername());

        sm.addPlayer(v1);
        sm.addPlayer(v2);

        assertEquals("Connected players:\n" +
                "\tfirstPlayer\n" +
                "\tsecondPlayer\n", sm.getAlreadyConnected());

    }

}