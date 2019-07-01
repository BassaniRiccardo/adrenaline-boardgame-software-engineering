package it.polimi.ingsw.controller;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Player;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.network.server.TCPVirtualView;
import it.polimi.ingsw.network.server.VirtualView;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test the methods of GameEngine which can be tested without simulating a game.
 *
 * @author BassaniRiccardo
 */
public class GameEngineTest {

    class DummyVirtualView extends VirtualView{
        @Override
        public void refresh() {        }

        @Override
        public void shutdown() {        }

        @Override
        public void showSuspension() {        }

        @Override
        public void showEnd(String message) {        }

        @Override
        public void choose(String type, String msg, List<?> options) {
            notifyObservers("1");
        }

        @Override
        public void choose(String type, String msg, List<?> options, int timeoutSec) {
            notifyObservers("1");
        }

        @Override
        public void display(String msg) {        }

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
     * Tests the method GameEngine.setup(), by displaying the operations performed on the console.
     * Consequently tests the methods used by setup().
     */
    @Test
    public void setup() {
        List<VirtualView> connections = new ArrayList<>();
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());

        GameEngine gameEngine = new GameEngine(connections);
        try {
            gameEngine.setup();
        }catch(NotEnoughPlayersException e){
            //
        }

    }


    /**
     * Tests the method GameEngine.resolveWinner(), by displaying the operation performed on the console.
     * An arbitrary number of points is assigned to the players.
     * Tests the method in the event that a single player is the winner.
     */
    @Test
    public void resolveWinner() {

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());

        GameEngine gameEngine = new GameEngine(connections);
        try {
            gameEngine.setup();
        }catch(NotEnoughPlayersException e){
            //
        }

        connections.get(0).getModel().setPoints(31);
        connections.get(1).getModel().setPoints(11);
        connections.get(2).getModel().setPoints(34);
        connections.get(3).getModel().setPoints(31);
        connections.get(4).getModel().setPoints(17);

        try {
            gameEngine.getBoard().getKillShotTrack().getKillers().add(connections.get(0).getModel());
            gameEngine.getBoard().getKillShotTrack().getKillers().add(connections.get(0).getModel());
            gameEngine.getBoard().getKillShotTrack().getKillers().add(connections.get(1).getModel());
            gameEngine.getBoard().getKillShotTrack().getKillers().add(connections.get(2).getModel());
            gameEngine.getBoard().getKillShotTrack().getKillers().add(connections.get(3).getModel());
            gameEngine.getBoard().getKillShotTrack().getKillers().add(connections.get(0).getModel());
        } catch (NotAvailableAttributeException e){e.printStackTrace();}

        gameEngine.resolve();

    }


    /**
     * Tests the method GameEngine.resolveWinner(), by displaying the operation performed on the console.
     * An arbitrary number of points is assigned to the players.
     * Tests the method in the event of a draw.
     */
    @Test
    public void resolveDraw() {

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());

        GameEngine gameEngine = new GameEngine(connections);
        try {
            gameEngine.setup();
        }catch(NotEnoughPlayersException e){
            //
        }

        connections.get(0).getModel().setPoints(34);
        connections.get(1).getModel().setPoints(11);
        connections.get(2).getModel().setPoints(34);
        connections.get(3).getModel().setPoints(31);
        connections.get(4).getModel().setPoints(17);

        try {
            gameEngine.getBoard().getKillShotTrack().getKillers().add(connections.get(1).getModel());
            gameEngine.getBoard().getKillShotTrack().getKillers().add(connections.get(3).getModel());
        } catch (NotAvailableAttributeException e){e.printStackTrace();}

        gameEngine.resolve();

    }


    /**
     * Test if the next player is correctly calculated in a standard case.
     */
    @Test
    public void getNextPlayerStandard(){

        Board b = BoardConfigurer.configureMap(1);

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.setBoard(b);

        gameEngine.setCurrentPlayer(connections.get(0));
        gameEngine.setCurrentPlayer(gameEngine.getNextPlayer());
        assertEquals(connections.get(1), gameEngine.getCurrentPlayer());
    }


    /**
     * Tests if the next player is correctly calculated when the current player is the last one in the connection list.
     */
    @Test
    public void getNextPlayerAfterLast(){

        Board b = BoardConfigurer.configureMap(1);

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.setBoard(b);
        gameEngine.setCurrentPlayer(connections.get(4));
        gameEngine.setCurrentPlayer(gameEngine.getNextPlayer());
        assertEquals(connections.get(0), gameEngine.getCurrentPlayer());
    }


    /**
     * Tests if the next player is correctly calculated when the player whose turn should come is suspended.
     */
    @Test
    public void getNextPlayerWithSuspendedPlayers(){

        Board b = BoardConfigurer.configureMap(1);

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.setBoard(b);

        gameEngine.setCurrentPlayer(connections.get(0));
        connections.get(1).setSuspended(true);
        gameEngine.setCurrentPlayer(gameEngine.getNextPlayer());
        assertEquals(connections.get(2), gameEngine.getCurrentPlayer());
    }

    /**
     * Tests if the next player is correctly calculated when the player whose turn should come is suspended.
     */
    @Test
    public void changePlayerWhenNotEnoughPlayers(){

        Board b = BoardConfigurer.configureMap(1);

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.setBoard(b);

        gameEngine.setCurrentPlayer(connections.get(0));
        connections.get(1).setSuspended(true);
        connections.get(2).setSuspended(true);
        connections.get(3).setSuspended(true);

        assertFalse(gameEngine.isGameOver());
        gameEngine.changePlayer();
        assertTrue(gameEngine.isGameOver());
    }

    /**
     * Tests the method addLeaderboard().
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     */
    @Test
    public void addLeaderboard() throws UnacceptableItemNumberException, NoMoreCardsException {

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

        assertEquals("The leaderBoard is coming:\n" +
                "\n" +
                "Leaderboard:\n" +
                "anonymous: 0 points\n" +
                "anonymous: 0 points\n" +
                "anonymous: 0 points\n" +
                "anonymous: 0 points\n" +
                "anonymous: 0 points\n", gameEngine.addLeaderboard("The leaderBoard is coming:"));
    }


    /**
     * Tests the method simulateTillEndPhase().
     *
     * @throws NotAvailableAttributeException       if thrown by simulateTillEndPhase, getPosition() or getKillShotTrack().
     * @throws NoMoreCardsException                 if thrown by simulateScenario() or simulateTillEndPhase.
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario() or simulateTillEndPhase.
     * @throws WrongTimeException                   if thrown by simulateTillEndPhase.
     */
    @Test
    public void simulateTillEndPhase() throws UnacceptableItemNumberException, WrongTimeException, NotAvailableAttributeException, NoMoreCardsException{

        Board b = BoardConfigurer.simulateScenario();

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.setBoard(b);
        for (int i=0; i<connections.size(); i++) {
            gameEngine.getPlayers().get(i).setPlayer(b.getPlayers().get(i));
        }

        gameEngine.simulateTillEndphase();

        Player p1 = b.getPlayers().get(0);
        Player p2 = b.getPlayers().get(1);
        Player p3 = b.getPlayers().get(2);

        assertTrue(p1.isInGame());
        assertTrue(p2.isInGame());
        assertTrue(p3.isInGame());

        assertEquals(b.getSpawnPoints().get(0), p1.getPosition());
        assertEquals(b.getSpawnPoints().get(1), p2.getPosition());
        assertEquals(b.getSpawnPoints().get(2), p3.getPosition());

        assertEquals(1, b.getKillShotTrack().getSkullsLeft());
        assertEquals(Arrays.asList(p2, p3, p1, p2, p1), b.getKillShotTrack().getKillers());

        assertEquals(2, p1.getDeaths());
        assertEquals(1, p2.getDeaths());
        assertEquals(2, p3.getDeaths());

        assertEquals(23, p1.getPoints());
        assertEquals(27, p2.getPoints());
        assertEquals(17, p3.getPoints());

        assertEquals(4, p1.getPointsToGive());
        assertEquals(6, p2.getPointsToGive());
        assertEquals(4, p3.getPointsToGive());

        assertEquals(Arrays.asList(p2, p2), p1.getDamages());
        assertEquals(Arrays.asList(p1, p1, p1, p1, p3, p3, p3, p3, p3, p3), p2.getDamages());
        assertEquals(Arrays.asList(p2, p2, p2, p2, p1, p2), p3.getDamages());

        assertEquals(Player.Status.BASIC, p1.getStatus());
        assertEquals(Player.Status.ADRENALINE_2, p2.getStatus());
        assertEquals(Player.Status.ADRENALINE_1, p3.getStatus());

    }


}