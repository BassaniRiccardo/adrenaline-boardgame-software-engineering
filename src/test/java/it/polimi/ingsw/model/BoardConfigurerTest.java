package it.polimi.ingsw.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests the correct behaviour of the class BoardConfigurer.
 * The methods of BoardConfigurer are simply aggregations of setters of the class Board.
 * It is therefore opportune to test, instead of every single method, the method simulateScenario(),
 * to check that the configuration of the board in its whole  is correct.
 */
public class BoardConfigurerTest {


    /**
     * Simulates a predefined scenario with the method simulateScenario().
     */
    @Before
    public void setup() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {

        BoardConfigurer.getInstance().simulateScenario();
    }


    /**
     * Tests if the size of the map is correctly set.
     */
    @Test
    public void simulateScenarioMapSize() {

        assertEquals(12, Board.getInstance().getMap().size());

    }


    /**
     * Tests if the number of players is correctly set.
     */
    @Test
    public void simulateScenarioNumberOfPlayers() {

        assertEquals(5, Board.getInstance().getPlayerNumber());

    }


    /**
     * Tests if the players are effectively located to the board.
     */
    @Test
    public void simulateScenarioPlayers() {

        assertEquals(5, Board.getInstance().getPlayers().size());
        for (Player  p: Board.getInstance().getPlayers()){
            assertNotNull(p.getPosition());
        }

    }


    /**
     * Tests if the size of the decks are correct, considering that some cards
     * have already been placed on the board.
     */
    @Test
    public void simulateScenarioDecksSize() {

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
        assertEquals(numberOfWeapons, Board.getInstance().getWeaponDeck().getDrawable().size());
        assertEquals(numberOfPowerUps, Board.getInstance().getPowerUpDeck().getDrawable().size());
        assertEquals(numberOfAmmoTiles, Board.getInstance().getAmmoDeck().getDrawable().size());

    }


    /**
     * Tests if the killshot track is correctly set.
     */
    @Test
    public void simulateScenarioKillShotTrack() throws NotAvailableAttributeException{

        assertEquals(8, Board.getInstance().getKillShotTrack().getSkullsLeft());

    }


    /**
     * Tests if the sizes of the unused maps are correctly set.
     */
    @Test
    public void unusedMaps() {

        BoardConfigurer.getInstance().configureMap(2);
        assertEquals(11, Board.getInstance().getMap().size());

        BoardConfigurer.getInstance().configureMap(3);
        assertEquals(11, Board.getInstance().getMap().size());
    }



}