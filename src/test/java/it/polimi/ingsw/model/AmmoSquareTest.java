package it.polimi.ingsw.model;

import org.junit.Test;

import static it.polimi.ingsw.model.Color.*;
import static it.polimi.ingsw.model.Player.HeroName.*;
import static org.junit.Assert.*;


/**
 * Tests all methods of the class AmmoSquare, covering all the instructions.
 */

public class AmmoSquareTest {

    /**
     * Tests the method addPlayer(), covering all the instructions.
     */
    @Test
    public void addPlayer() {

        //creates a new ammo square with no ammo tiles and no players
        AmmoSquare ammoSquare = new AmmoSquare(1, 1, 1, 1, RED);

        //creates a new player
        Player p = new Player(2, DOZER);

        //checks that the ammo square does not contains the player
        assertFalse(ammoSquare.getPlayers().contains(p));

        //adds the player to the ammo square
        ammoSquare.addPlayer(p);

        //checks that the ammo square contains the added player
        assertTrue(ammoSquare.getPlayers().contains(p));

    }

    /**
     * Tests the method removePlayer(), covering all the instructions.
     */
    @Test
    public void removePlayer() {

        //creates a new ammo square with no ammo tiles and no players
        AmmoSquare ammoSquare = new AmmoSquare(1, 1, 1, 1, RED);

        //adds a player to the ammo square
        Player p = new Player(2, DOZER);
        ammoSquare.getPlayers().add(p);

        //checks that the ammo square contains the added player
        assertTrue(ammoSquare.getPlayers().contains(p));

        //removes the added player form the ammo square
        ammoSquare.removePlayer(p);

        //checks that the ammo square does not contain the removed player
        assertFalse(ammoSquare.getPlayers().contains(p));

    }

    /**
     * Tests the method containsPlayer(), covering all the instructions.
     */
    @Test
    public void containsPlayer() {

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

    /**
     * Tests the method removeCard(), covering all the instructions.
     */
    @Test
    public void removeAmmo() {

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

        //checks that the ammo square does not have an ammo tile anymore
        assertNull(ammoSquare.getAmmoTile());
    }

    /**
     * Tests the method addAllCards(), covering all the instructions.
     */
    @Test
    public void addAmmo() {

        //creates a new ammo square with no ammo tiles and no players
        AmmoSquare ammoSquare = new AmmoSquare(1, 1, 1, 1, RED);

        //checks that the ammo square does not contain an ammo tile
        assertNull(ammoSquare.getAmmoTile());

        //adds an ammo tile to the ammo square
        AmmoTile drawn = (AmmoTile)Board.getInstance().getAmmoDeck().getDrawable().get(0);
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