package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.BoardConfigurer;
import it.polimi.ingsw.model.board.AmmoSquare;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Player;
import it.polimi.ingsw.model.board.WeaponSquare;
import it.polimi.ingsw.model.cards.Color;
import it.polimi.ingsw.model.exceptions.NoMoreCardsException;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;
import it.polimi.ingsw.model.exceptions.UnacceptableItemNumberException;
import org.junit.Test;

import static it.polimi.ingsw.model.cards.Color.*;
import static it.polimi.ingsw.model.board.Player.HeroName.*;
import static org.junit.Assert.*;

/**
 * Tests all the methods of the class Square that are identical in WeaponSquare and AmmoSquare.
 * Weapon squares and ammo squares are instantiated interchangeably.
 * The methods equals() and hashcode() are tested in the subclasses.
 * The method toString of Color is tested too.
 *
 * @author BassaniRiccardo
 */


public class SquareTest {

    /**
     * Tests the method addPlayer(), covering all the instructions apart from the exception.
     *
     * @throws NoMoreCardsException
     * @throws UnacceptableItemNumberException
     * @throws IllegalArgumentException
     */
    @Test
    public void addPlayerAmmoSquare() throws NoMoreCardsException, UnacceptableItemNumberException, NotAvailableAttributeException {

        //Simulate a scenario, select an ammo square and a player
        Board b = BoardConfigurer.getInstance().simulateScenario();
        AmmoSquare ammoSquare = (AmmoSquare) b.getMap().get(1);
        Player p = b.getPlayers().get(0);

        //checks that the ammo square does not contains the player
        assertFalse(ammoSquare.getPlayers().contains(p));

        //adds the player to the ammo square
        p.setPosition(ammoSquare);

        //checks that the ammo square contains the added player
        assertTrue(ammoSquare.getPlayers().contains(p));

    }


    /**
     * Tests the method removePlayer(), covering all the instructions apart form the  exception.
     *
     * @throws  IllegalArgumentException
     */
    @Test
    public void removePlayerWeaponSquare() {

        //creates a new weapon square with no weapons
        WeaponSquare weaponSquare = new WeaponSquare(new Board(), 1, 1, 1, 1, RED);

        //adds a player to the weapon square
        Player p = new Player(3, VIOLET, new Board());
        weaponSquare.getPlayers().add(p);

        //checks that the weapon square contains the removed player
        assertTrue(weaponSquare.getPlayers().contains(p));

        //removes the added player form the weapon square
        weaponSquare.removePlayer(p);

        //checks that the weapon square does not contain the removed player
        assertFalse(weaponSquare.getPlayers().contains(p));

    }


    /**
     * Tests the method removePlayer(), when an exception should be thrown since the player is not in the square.
     *
     * @throws  IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void removePlayerNotInWeaponSquare()  {

        //creates a new weapon square with no weapons
        WeaponSquare weaponSquare = new WeaponSquare(new Board(), 1, 1, 1, 1, RED);

        //creates a new player
        Player p = new Player(3, VIOLET, new Board());

        //checks that the weapon square does not contain the removed player
        assertFalse(weaponSquare.getPlayers().contains(p));

        //tries to remove the added player form the weapon square
        weaponSquare.removePlayer(p);


    }


    /**
     * Tests the method containsPlayer(), covering all the instructions apart from the exception.
     *
     * @throws IllegalArgumentException
     */
    @Test
    public void containsPlayerAmmoSquare() {

        //creates a new ammo square with no ammo tiles and no players
        AmmoSquare ammoSquare = new AmmoSquare(new Board(), 1, 1, 1, 1, RED);

        //adds a player to the ammo square
        Player p = new Player(2, DOZER, new Board());
        ammoSquare.getPlayers().add(p);

        //checks that the ammo square contains the added player
        assertTrue(ammoSquare.containsPlayer(p));

        //removes the added player form the ammo square
        ammoSquare.getPlayers().remove(p);

        //checks that the ammo square does not contain the removed player
        assertFalse(ammoSquare.containsPlayer(p));

    }


    /**
     * Tests the method isEmpty().
     */
    @Test
    public void isEmpty(){

        //creates a new ammo square with no ammo tiles and no players
        AmmoSquare ammoSquare = new AmmoSquare(new Board(), 1, 1, 1, 1, RED);

        assertTrue(ammoSquare.isEmpty());

    }


    /**
     * Tests the method toString() of the enumeration Color.
     * Test included in this class since Square and Color are strongly linked.
     */
    @Test
    public void ColorToString(){

        Color color = RED;
        assertEquals("Red", color.toString());

    }

}