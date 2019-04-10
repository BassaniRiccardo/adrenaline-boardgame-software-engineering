package it.polimi.ingsw.model;

import org.junit.Test;

import static it.polimi.ingsw.model.Color.*;
import static it.polimi.ingsw.model.Player.HeroName.*;
import static org.junit.Assert.*;

/**
 * Tests all the methods of the class Square that are identical in WeaponSquare and AmmoSquare, covering all the instructions.
 * Weapon squares and ammo squares are instantiated interchangeably.
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
    public void addPlayerAmmoSquare() throws NoMoreCardsException, UnacceptableItemNumberException {

        //Simulate a scenario, select an ammo square and a player
        BoardConfigurer.getInstance().simulateScenario();
        AmmoSquare ammoSquare = (AmmoSquare) Board.getInstance().getMap().get(1);
        Player p = Board.getInstance().getPlayers().get(0);

        //checks that the ammo square does not contains the player
        assertFalse(ammoSquare.getPlayers().contains(p));

        //adds the player to the ammo square
        p.setPosition(ammoSquare);

        //checks that the ammo square contains the added player
        assertTrue(ammoSquare.getPlayers().contains(p));

    }

    /**
     * Tests the method addPlayer(), when an exception should be throw since a the player is already on the square.
     *
     * @throws NoMoreCardsException
     * @throws UnacceptableItemNumberException
     * @throws IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void addPlayerAlreadyPresentAmmoSquare() throws NoMoreCardsException, UnacceptableItemNumberException {

        //Simulate a scenario, select an ammo square and a player
        BoardConfigurer.getInstance().simulateScenario();
        AmmoSquare ammoSquare = (AmmoSquare) Board.getInstance().getMap().get(0);
        Player p = Board.getInstance().getPlayers().get(0);

        //checks that the ammo square contains the player
        assertTrue(ammoSquare.getPlayers().contains(p));

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
        WeaponSquare weaponSquare = new WeaponSquare(1, 1, 1, 1, RED);

        //adds a player to the weapon square
        Player p = new Player(3, VIOLET);
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
        WeaponSquare weaponSquare = new WeaponSquare(1, 1, 1, 1, RED);

        //creates a new player
        Player p = new Player(3, VIOLET);

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
        AmmoSquare ammoSquare = new AmmoSquare(1, 1, 1, 1, RED);

        //adds a player to the ammo square
        Player p = new Player(2, DOZER);
        ammoSquare.getPlayers().add(p);

        //checks that the ammo square contains the added player
        assertTrue(ammoSquare.containsPlayer(p));

        //removes the added player form the ammo square
        ammoSquare.getPlayers().remove(p);

        //checks that the ammo square does not contain the removed player
        assertFalse(ammoSquare.containsPlayer(p));

    }

}