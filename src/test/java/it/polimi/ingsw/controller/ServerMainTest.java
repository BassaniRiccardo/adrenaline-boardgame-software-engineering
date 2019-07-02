package it.polimi.ingsw.controller;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.exceptions.NoMoreCardsException;
import it.polimi.ingsw.model.exceptions.UnacceptableItemNumberException;
import it.polimi.ingsw.network.server.TCPVirtualView;
import it.polimi.ingsw.network.server.VirtualView;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import static java.util.logging.Level.SEVERE;
import static org.junit.Assert.*;

/**
 * Test the methods of ServerMain which can be tested without simulating a game.
 *
 * @author BassaniRiccardo
 */

public class ServerMainTest {


    /**
     * A subclass of VirtualView simulating players answers.
     */
    class DummyVirtualView extends VirtualView{
        @Override
        public void refresh() {        }

        @Override
        public void shutdown() {        }

        @Override
        public void showSuspension() {        }

        @Override
        public void showEnd(String message) {      }

        @Override
        public void choose(String type, String msg, List<?> options) {
            notifyObservers("1");
        }

        @Override
        public void choose(String type, String msg, List<?> options, int timeoutSec) {
            notifyObservers("1");
        }

        @Override
        public void display(String msg) { }

        @Override
        public String getInputNow(String msg, int max) {
            return "1";
        }

        @Override
        public int chooseNow(String type, String msg, List<?> options) {
            return 1;
        }

        @Override
        public void update(JsonObject jsonObject) {        }
    }


    /**
     * Tests the method untrackGame().
     *
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     * @throws NoMoreCardsException                 if thrown by simulateScenario().
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
     * Tests the method addPlayer().
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
     * Tests the method initializeLogger().
     */
    @Test
    public void initializeLogger() {

        ServerMain.getInstance().initializeLogger();
        Logger LOGGER = Logger.getLogger("serverLogger");
        assertEquals(SEVERE, LOGGER.getLevel());
    }


    /**
     * Tests the method canResume().
     *
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     * @throws NoMoreCardsException                 if thrown by simulateScenario().
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
     * Tests the method removeSuspendedPlayers().
     *
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     * @throws NoMoreCardsException                 if thrown by simulateScenario().
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
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     * @throws NoMoreCardsException                 if thrown by simulateScenario().
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




    /**
     * Tests the method matchMaking(), in the case nobody is connected.
     */
    /*@Test
    public void matchMakingNoPlayers() {

        ServerMain sm = ServerMain.getInstance();
        sm.getPlayers().clear();
        sm.getWaitingPlayers().clear();
        sm.setup();
        sm.matchmaking();

        assertEquals("Time left: 4\n" + "Waiting for more players", sm.getOldMessage());

    }


    /**
     * Tests the method matchMaking(), in the case a player is connected.
     */
    /*@Test
    public void matchMaking1() {

        ServerMain sm = ServerMain.getInstance();
        sm.getPlayers().clear();
        sm.getWaitingPlayers().clear();
        sm.setup();
        sm.addPlayer(new DummyVirtualView() { });
        sm.getWaitingPlayers().get(0).setName("first player");
        sm.matchmaking();

        assertEquals(   "Connected players:\n" +
                                "\tfirst player\n" +
                                "Time left: 4\n" +
                                "Waiting for more players", sm.getOldMessage());

    }



    /**
     * Tests the method matchMaking(), in the case three players are connected and the game starts.
     */
    /*@Test
    public void matchMaking3() {

        ServerMain sm = ServerMain.getInstance();
        sm.getPlayers().clear();
        sm.getWaitingPlayers().clear();
        sm.setup();
        sm.addPlayer(new DummyVirtualView() { });
        sm.getWaitingPlayers().get(0).setName("first player");
        sm.addPlayer(new DummyVirtualView() { });
        sm.getWaitingPlayers().get(1).setName("second player");
        sm.addPlayer(new DummyVirtualView() { });
        sm.getWaitingPlayers().get(2).setName("third player");
        sm.matchmaking();

        assertEquals(   "Connected players:\n" +
                "\tfirst player\n" +
                "\tsecond player\n" +
                "\tthird player\n" +
                "Time left: 4\n" +
                "Game about to start!", sm.getOldMessage());

    }

    */
}