package it.polimi.ingsw.model;

import org.junit.Test;

import static it.polimi.ingsw.model.Color.*;
import static org.junit.Assert.*;


/**
 * Tests all methods of the class AmmoSquare, covering all the instructions.
 * The method shared with the class WeaponSquare are tested in SquareTest.
 *
 * @author BassaniRiccardo
 */

public class AmmoSquareTest {


    /**
     * Tests the method removeCard(), covering all the instructions apart from the exception.
     *
     * @throws NoMoreCardsException
     * @throws NotAvailableAttributeException
     */
    @Test(expected = NotAvailableAttributeException.class)
    public void removeAmmo() throws NoMoreCardsException, NotAvailableAttributeException {

        //creates a new ammo tile
        AmmoTile ammoTile = new AmmoTile(true, new AmmoPack(1,1,1));

        //creates a new ammo square with the created ammo tile and no players
        AmmoSquare ammoSquare = new AmmoSquare(1, 1, 1, 1, RED, ammoTile);

        //checks that the ammo square contains the created ammo tile
        assertEquals(ammoTile, ammoSquare.getAmmoTile());

        //removes the ammo tile from the ammo square
        Card collected = ammoSquare.removeCard(ammoSquare.getAmmoTile());

        //checks that the collected ammo tile is the one that was in the ammo square
        assertEquals(ammoTile, collected);

        //checks that the ammo square does not have an ammo tile anymore: exception thrown
        ammoSquare.getAmmoTile();
    }


    /**
     * Tests the method removeCard(), when an exception should be thrown since the square is empty.
     *
     * @throws NoMoreCardsException
     * @throws NotAvailableAttributeException
     */
    @Test
    public void removeAmmoEmptySquare() throws NoMoreCardsException, NotAvailableAttributeException {

        //creates a new ammo tile
        AmmoTile ammoTile = new AmmoTile(true, new AmmoPack(1,1,1));

        //creates a new ammo square with the created ammo tile and no players
        AmmoSquare ammoSquare = new AmmoSquare(1, 1, 1, 1, RED, ammoTile);

        //checks that the ammo square contains the created ammo tile
        assertEquals(ammoTile, ammoSquare.getAmmoTile());

        //removes the ammo tile from the ammo square
        ammoSquare.removeCard(ammoSquare.getAmmoTile());

        try{
            //tries to remove another ammo tile
            ammoSquare.removeCard(ammoSquare.getAmmoTile());
        }
        catch (NotAvailableAttributeException notAvailableAttributeException) {
        }
        catch (NoMoreCardsException noMoreCardsException) {
        }



    }


    /**
     * Tests the method addAllCards(), covering all the instructions apart form the exceptions.
     *
     * @throws UnacceptableItemNumberException
     * @throws NoMoreCardsException
     * @throws NotAvailableAttributeException
     */
    @Test
    public void addAmmo() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {

        //initializes board
        BoardConfigurer.getInstance().simulateScenario();

        //creates a new ammo square with no ammo tiles and no players
        AmmoSquare ammoSquare = new AmmoSquare(1, 1, 1, 1, RED);

        //adds an ammo tile to the ammo square
        AmmoTile drawn = (AmmoTile)Board.getInstance().getAmmoDeck().getDrawable().get(0);
        ammoSquare.addAllCards();

        //checks that the ammo square contains the added ammo tile
        assertEquals(drawn, ammoSquare.getAmmoTile());
    }

    /**
     * Tests the method addAllCards(), when an exception should be thrown since the square already contains an ammo tile.
     *
     * @throws UnacceptableItemNumberException
     * @throws NoMoreCardsException
     * @throws NotAvailableAttributeException
     */
    @Test(expected = UnacceptableItemNumberException.class)
    public void addAmmoFullSquare() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {

        //initializes board
        BoardConfigurer.getInstance().simulateScenario();

        //creates a new ammo square with no ammo tiles and no players
        AmmoSquare ammoSquare = new AmmoSquare(1, 1, 1, 1, RED);

        //adds an ammo tile to the ammo square
        AmmoTile drawn = (AmmoTile)Board.getInstance().getAmmoDeck().getDrawable().get(0);
        ammoSquare.addAllCards();

        //tries to add another ammo tile
        ammoSquare.addAllCards();

        //checks that the ammo square contains the added ammo tile
        assertEquals(drawn, ammoSquare.getAmmoTile());
    }


    /**
     * Tests the method equals(), covering all the instructions.
     */
    @Test
    public void equalsOverride() {

        //creates two identical ammoSquares
        //since the id identifies the square, the other fields are set equals too
        //the method equals() only checks the id
        AmmoSquare ammoSquare1 = new AmmoSquare(1, 1, 1, 1, RED);
        AmmoSquare ammoSquare2 = new AmmoSquare(1, 1, 1, 1, RED);
        assertTrue(ammoSquare1.equals(ammoSquare2));

    }

    /**
     * Tests the method hashCode(), covering all the instructions.
     */
    @Test
    public void hashCodeOverride() {

        //creates two identical ammoSquares
        //since the id identifies the square, the other fields are set equals too
        //the method equals() only checks the id
        AmmoSquare ammoSquare1 = new AmmoSquare(1, 1, 1, 1, RED);
        AmmoSquare ammoSquare2 = new AmmoSquare(1, 1, 1, 1, RED);
        assertEquals(ammoSquare1.hashCode(), ammoSquare2.hashCode());

    }

}