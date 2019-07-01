package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.BoardConfigurer;
import it.polimi.ingsw.model.board.AmmoSquare;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.AmmoPack;
import it.polimi.ingsw.model.cards.AmmoTile;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.exceptions.NoMoreCardsException;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;
import it.polimi.ingsw.model.exceptions.UnacceptableItemNumberException;
import org.junit.Test;

import static it.polimi.ingsw.model.cards.Color.*;
import static org.junit.Assert.*;


/**
 * Tests all methods of the class AmmoSquare.
 * The method shared with the class WeaponSquare are tested in SquareTest.
 *
 * @author BassaniRiccardo
 */

public class AmmoSquareTest {


    /**
     * Tests the method removeCard(), covering all the instructions apart from the exception.
     *
     * @throws NoMoreCardsException                 if thrown by getAmmoTile().
     * @throws NotAvailableAttributeException       if thrown by getAmmoTile().
     */
    @Test(expected = NotAvailableAttributeException.class)
    public void removeAmmo()  throws NoMoreCardsException, NotAvailableAttributeException {

        Board board1 = BoardConfigurer.configureMap(1);

        //creates a new ammo tile
        AmmoTile ammoTile = new AmmoTile(true, new AmmoPack(1,1,1));

        //creates a new ammo square with the created ammo tile and no players
        AmmoSquare ammoSquare = new AmmoSquare(board1, 1, 1, 1, 1, RED, ammoTile);

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
     * @throws NoMoreCardsException                     if thrown by getAmmoTile().
     * @throws NotAvailableAttributeException           if thrown by removeCard().
     */
    @Test(expected = NotAvailableAttributeException.class)
    public void removeAmmoEmptySquare()  throws NoMoreCardsException, NotAvailableAttributeException {

        Board board1 = BoardConfigurer.configureMap(1);

        //creates a new ammo tile
        AmmoTile ammoTile = new AmmoTile(true, new AmmoPack(1,1,1));

        //creates a new ammo square with the created ammo tile and no players
        AmmoSquare ammoSquare = new AmmoSquare(board1, 1, 1, 1, 1, RED, ammoTile);

        //checks that the ammo square contains the created ammo tile
        assertEquals(ammoTile, ammoSquare.getAmmoTile());

        //removes the ammo tile from the ammo square
        ammoSquare.removeCard(ammoSquare.getAmmoTile());

        //tries to remove another ammo tile: exception thrown
        ammoSquare.removeCard(ammoSquare.getAmmoTile());

    }


    /**
     * Tests the method addAllCards(), covering all the instructions apart form the exceptions.
     *
     * @throws UnacceptableItemNumberException  if thrown by simulateScenario() or addAllCards().
     * @throws NoMoreCardsException             if thrown by simulateScenario() or addAllCards().
     * @throws NotAvailableAttributeException   if thrown by getAmmoTile().
     */
    @Test
    public void addAmmo() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {

        //initializes board
        Board b = BoardConfigurer.simulateScenario();

        //creates a new ammo square with no ammo tiles and no players
        AmmoSquare ammoSquare = new AmmoSquare(b, 1, 1, 1, 1, RED);

        //adds an ammo tile to the ammo square
        AmmoTile drawn = (AmmoTile)b.getAmmoDeck().getDrawable().get(0);
        ammoSquare.addAllCards();

        //checks that the ammo square contains the added ammo tile
        assertEquals(drawn, ammoSquare.getAmmoTile());
    }


    /**
     * Tests the method addAllCards(), when an exception should be thrown since the square already contains an ammo tile.
     *
     * @throws UnacceptableItemNumberException  if thrown by simulateScenario() or addAllCards().
     * @throws NoMoreCardsException             if thrown by simulateScenario() or addAllCards().
     * @throws NotAvailableAttributeException   if thrown by getAmmoTile().
     */
    @Test(expected = UnacceptableItemNumberException.class)
    public void addAmmoFullSquare() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {

        //initializes board
        Board b = BoardConfigurer.simulateScenario();

        //creates a new ammo square with no ammo tiles and no players
        AmmoSquare ammoSquare = new AmmoSquare(b, 1, 1, 1, 1, RED);

        //adds an ammo tile to the ammo square
        AmmoTile drawn = (AmmoTile)b.getAmmoDeck().getDrawable().get(0);
        ammoSquare.addAllCards();

        //tries to addList another ammo tile
        ammoSquare.addAllCards();

        //checks that the ammo square contains the added ammo tile
        assertEquals(drawn, ammoSquare.getAmmoTile());
    }


    /**
     * Tests the method equals().
     */
    @Test
    public void equalsOverride() {

        Board b1 = new Board();
        Board b2 = new Board();
        AmmoSquare ammoSquare1 = new AmmoSquare(b1, 1, 2, 3, 3, BLUE);
        AmmoSquare ammoSquare2 = new AmmoSquare(b1, 1, 2, 3, 3, BLUE);
        AmmoSquare ammoSquare3 = new AmmoSquare(b2, 1, 2, 3, 3, BLUE);
        AmmoSquare ammoSquare4 = new AmmoSquare(b1, 2, 3, 2 , 2, RED);

        //same id and board
        assertEquals(ammoSquare1,ammoSquare2);
        //same id, different boards
        assertNotEquals(ammoSquare1,ammoSquare3);
        //same board, different IDs
        assertNotEquals(ammoSquare1, ammoSquare4);

    }


    /**
     * Tests the method hashCode().
     */
    @Test
    public void hashCodeOverride() {

        Board b1 = new Board();
        Board b2 = new Board();
        AmmoSquare ammoSquare1 = new AmmoSquare(b1, 1, 2, 3, 3, BLUE);
        AmmoSquare ammoSquare2 = new AmmoSquare(b1, 1, 2, 3, 3, BLUE);
        AmmoSquare ammoSquare3 = new AmmoSquare(b2, 1, 2, 3, 3, BLUE);
        AmmoSquare ammoSquare4 = new AmmoSquare(b1, 2, 3, 2 , 2, RED);

        //same id and board
        assertEquals(ammoSquare1.hashCode(),ammoSquare2.hashCode());
        //same id, different boards
        assertNotEquals(ammoSquare1.hashCode(),ammoSquare3.hashCode());
        //same board, different IDs
        assertNotEquals(ammoSquare1.hashCode(), ammoSquare4.hashCode());

    }

}