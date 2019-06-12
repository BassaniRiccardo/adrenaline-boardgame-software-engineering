package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;
import it.polimi.ingsw.network.server.TCPVirtualView;
import it.polimi.ingsw.network.server.VirtualView;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


//Tests temporarily removed while switching protocol
public class TurnManagerTest {

    /**
     *  Tests the method run(), simulating the first turn of the first player and displaying the main events on the console.
     */
    @Test
    public void run() {



    }

    /**
     * Tests the method joinBoard() when it is called at the beginning of the game.
     *
     * @throws NotAvailableAttributeException
     */
    @Test
    public void joinBoardBegin() throws NotAvailableAttributeException {
/*
        List<VirtualView> connections = new ArrayList<>();
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));

        GameEngine gameEngine = new GameEngine(connections);

        System.out.println("\nSetup:");

        gameEngine.setup();

        TurnManager turnManager = new TurnManager(gameEngine.getBoard(), gameEngine.getCurrentPlayer(), false);

        System.out.println("Player 1: JoinBoard()");

        turnManager.joinBoard(gameEngine.getCurrentPlayer().getModel(), 2);

        //checks if the player is in the spawn point of the color of the powerup he discarded
        assertTrue(gameEngine.getBoard().getSpawnPoints().contains(gameEngine.getCurrentPlayer().getModel().getPosition()));
        assertEquals(gameEngine.getBoard().getPowerUpDeck().getDiscarded().get(0).getColor(), gameEngine.getCurrentPlayer().getModel().getPosition().getColor());
*/
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