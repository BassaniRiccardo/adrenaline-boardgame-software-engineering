package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.network.server.PlayerController;
import it.polimi.ingsw.network.server.TCPPlayerController;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

//Tests temporarily removed while switching protocol
public class GameEngineTest {


    /**
     * Tests the method GameEngine.run().
     * Simulates a 5 player game.
     * Displays the main events of the game on the console, showing how the game develops according to the rules.
     */
    @Test
    public void run() {

        List<PlayerController> connections = new ArrayList<>();
        connections.add(new TCPPlayerController(null));
        connections.add(new TCPPlayerController(null));
        connections.add(new TCPPlayerController(null));
        connections.add(new TCPPlayerController(null));
        connections.add(new TCPPlayerController(null));

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.run();

    }


    /**
     * Tests the method GameEngine.setup(), by displaying the operations performed on the console.
     * Consequently tests the methods used by setup().
     */
    @Test
    public void setup() {
/*
        List<PlayerController> connections = new ArrayList<>();
        connections.add(new TCPPlayerController(null));
        connections.add(new TCPPlayerController(null));
        connections.add(new TCPPlayerController(null));
        connections.add(new TCPPlayerController(null));
        connections.add(new TCPPlayerController(null));

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.setup();

 */
    }


    /**
     * Tests the method GameEngine.resolveWinner(), by displaying the operation performed on the console.
     * An arbitrary number of points is assigned to the players.
     * Tests the method in the event that a single player is the winner.
     */
    @Test
    public void resolveWinner() {
/*
        List<PlayerController> connections = new ArrayList<>();
        connections.add(new TCPPlayerController(null));
        connections.add(new TCPPlayerController(null));
        connections.add(new TCPPlayerController(null));
        connections.add(new TCPPlayerController(null));
        connections.add(new TCPPlayerController(null));

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.setup();

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
*/
    }


    /**
     * Tests the method GameEngine.resolveWinner(), by displaying the operation performed on the console.
     * An arbitrary number of points is assigned to the players.
     * Tests the method in the event of a draw.
     */
    @Test
    public void resolveDraw() {
/*
        List<PlayerController> connections = new ArrayList<>();
        connections.add(new TCPPlayerController(null));
        connections.add(new TCPPlayerController(null));
        connections.add(new TCPPlayerController(null));
        connections.add(new TCPPlayerController(null));
        connections.add(new TCPPlayerController(null));

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.setup();

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
*/
    }


    /**
     * Test if the next player is correctly calculated in a standard case.
     */
    @Test
    public void getNextPlayerStandard(){


        Board b = BoardConfigurer.configureMap(1);

        List<PlayerController> connections = new ArrayList<>();
        connections.add(new TCPPlayerController(null));
        connections.add(new TCPPlayerController(null));
        connections.add(new TCPPlayerController(null));
        connections.add(new TCPPlayerController(null));
        connections.add(new TCPPlayerController(null));

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

        List<PlayerController> connections = new ArrayList<>();
        connections.add(new TCPPlayerController(null));
        connections.add(new TCPPlayerController(null));
        connections.add(new TCPPlayerController(null));
        connections.add(new TCPPlayerController(null));
        connections.add(new TCPPlayerController(null));

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

        List<PlayerController> connections = new ArrayList<>();
        connections.add(new TCPPlayerController(null));
        connections.add(new TCPPlayerController(null));
        connections.add(new TCPPlayerController(null));
        connections.add(new TCPPlayerController(null));
        connections.add(new TCPPlayerController(null));

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.setBoard(b);

        gameEngine.setCurrentPlayer(connections.get(0));
        connections.get(1).suspend();
        gameEngine.setCurrentPlayer(gameEngine.getNextPlayer());
        assertEquals(connections.get(2), gameEngine.getCurrentPlayer());
    }


}