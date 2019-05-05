package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.NotAvailableAttributeException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TurnManagerTest {

    /**
     *  Tests the method run(), simulating the first turn of the first player and displaying the main events on the console.
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
        System.out.println("\nSetup:");
        gameEngine.setup();

        TurnManager turnManager = new TurnManager(gameEngine.getBoard(), gameEngine.getCurrentPlayer(), connections, false);
        System.out.println("Player 1: first turn.");
        turnManager.run();

    }

    /**
     * Tests the method joinBoard() when it is called at the beginning of the game.
     *
     * @throws NotAvailableAttributeException
     */
    @Test
    public void joinBoardBegin() throws NotAvailableAttributeException {

        List<PlayerController> connections = new ArrayList<>();
        connections.add(new TCPPlayerController(null));
        connections.add(new TCPPlayerController(null));
        connections.add(new TCPPlayerController(null));
        connections.add(new TCPPlayerController(null));
        connections.add(new TCPPlayerController(null));

        GameEngine gameEngine = new GameEngine(connections);

        System.out.println("\nSetup:");

        gameEngine.setup();

        TurnManager turnManager = new TurnManager(gameEngine.getBoard(), gameEngine.getCurrentPlayer(), connections, false);

        System.out.println("Player 1: JoinBoard()");

        turnManager.joinBoard(gameEngine.getCurrentPlayer().getPlayer(), 2);

        //checks if the player is in the spawn point of the color of the powerup he discarded
        assertTrue(gameEngine.getBoard().getSpawnPoints().contains(gameEngine.getCurrentPlayer().getPlayer().getPosition()));
        assertEquals(gameEngine.getBoard().getPowerUpDeck().getDiscarded().get(0).getColor(), gameEngine.getCurrentPlayer().getPlayer().getPosition().getColor());

    }

    @Test
    public void executeAction() {
    }

    @Test
    public void usePowerUp() {
    }

    @Test
    public void handleShooting() {
    }

    @Test
    public void reload() {
    }
}