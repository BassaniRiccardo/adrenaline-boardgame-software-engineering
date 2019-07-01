package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.BoardConfigurer;
import it.polimi.ingsw.model.board.AmmoSquare;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Player;
import it.polimi.ingsw.model.cards.PowerUp;
import it.polimi.ingsw.model.cards.Weapon;
import it.polimi.ingsw.model.exceptions.NoMoreCardsException;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;
import it.polimi.ingsw.model.exceptions.UnacceptableItemNumberException;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests the correct behaviour of the class BoardConfigurer.
 * The methods of BoardConfigurer are simply aggregations of setters of the class Board.
 * It is therefore opportune to test, instead of every single method, the method simulateScenario(),
 * to check that the configuration of the board in its whole  is correct.
 *
 * @author BassaniRiccardo
 */

public class BoardConfigurerTest {


    /**
     * Tests if the size of the map is correctly set.
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     */
    @Test
    public void simulateScenarioMapSize() throws NoMoreCardsException, UnacceptableItemNumberException {

        Board b = BoardConfigurer.simulateScenario();
        assertEquals(12, b.getMap().size());

    }


    /**
     * Tests if the number of players is correctly set.
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     */
    @Test
    public void simulateScenarioNumberOfPlayers() throws NoMoreCardsException, UnacceptableItemNumberException {

        Board b = BoardConfigurer.simulateScenario();
        assertEquals(5, b.getPlayerNumber());

    }


    /**
     * Tests if the players are effectively located to the board.
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     * @throws NotAvailableAttributeException       if thrown by getPosition().
     */
    @Test
    public void simulateScenarioPlayers() throws NoMoreCardsException, UnacceptableItemNumberException, NotAvailableAttributeException {

        Board b = BoardConfigurer.simulateScenario();
        assertEquals(5, b.getPlayers().size());
        for (Player p: b.getPlayers()){
            assertNotNull(p.getPosition());
        }

    }


    /**
     * Tests if the size of the decks are correct, considering that some cards
     * have already been placed on the board.
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     */
    @Test
    public void simulateScenarioDecksSize() throws NoMoreCardsException, UnacceptableItemNumberException{

        Board b = BoardConfigurer.simulateScenario();

        //the expected decks sizes

        //9 out of the 36 total weapons are placed on the board
        int numberOfWeapons = Weapon.WeaponName.values().length - 9;
        //9 out of the 36 total ammo tiles are placed on the board
        int numberOfAmmoTiles = 36 - 9;
        //The deck contains 2 copies of all the combinations of color(3 possible colors) and type of power up
        int numberOfColor = 3;
        int numberOfCopy = 2;
        int numberOfPowerUps = PowerUp.PowerUpName.values().length * numberOfColor * numberOfCopy;

        //checks that the decks sizes are correct
        assertEquals(numberOfWeapons, b.getWeaponDeck().getDrawable().size());
        assertEquals(numberOfPowerUps, b.getPowerUpDeck().getDrawable().size());
        assertEquals(numberOfAmmoTiles, b.getAmmoDeck().getDrawable().size());

    }


    /**
     * Tests if the kill shot track is correctly set.
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     * @throws NotAvailableAttributeException       if thrown by getKillShotTrack().
     */
    @Test
    public void simulateScenarioKillShotTrack() throws NoMoreCardsException, UnacceptableItemNumberException, NotAvailableAttributeException{

        Board b = BoardConfigurer.simulateScenario();

        assertEquals(8, b.getKillShotTrack().getSkullsLeft());

    }


    /**
     * Tests if the sizes of the unused maps are correctly set.
     */
    @Test
    public void unusedMaps() {

        Board board2 = BoardConfigurer.configureMap(2);
        assertEquals(11, board2.getMap().size());

        Board board3 = BoardConfigurer.configureMap(3);
        assertEquals(11, board3.getMap().size());
    }


    /**
     * Tests if all the ammo tiles are correctly set on the board.
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     */
    @Test
    public void simulateScenarioSetAmmoTiles() throws UnacceptableItemNumberException, NoMoreCardsException{
        Board b = BoardConfigurer.simulateScenario();
        for(AmmoSquare s: b.getAmmoSquares()){
                s.hasAmmoTile();
            }
    }

}