package it.polimi.ingsw.model;

import org.junit.Test;

import static it.polimi.ingsw.model.Color.*;
import static it.polimi.ingsw.model.Weapon.WeaponName.*;
import static it.polimi.ingsw.model.Player.HeroName.*;
import static org.junit.Assert.*;

/**
 * Tests all methods of the class WeaponSquare, covering all the instructions.
 */

public class WeaponSquareTest {

    /**
     * Tests the method addPlayer(), covering all the instructions.
     */
    @Test
    public void addPlayer() {

        //creates a new weapon square with no weapons
        //the first constructor is tested in this test too
        WeaponSquare weaponSquare = new WeaponSquare(1, 1, 1, 1, RED, null);

        //creates a new player
        Player p = new Player(3, VIOLET);

        //checks that the weapon square does not contain the created player
        assertFalse(weaponSquare.containsPlayer(p));

        //adds the player to the weapon square
        weaponSquare.addPlayer(p);

        //checks that the weapon square contains the added player
        assertTrue(weaponSquare.containsPlayer(p));

    }

    /**
     * Tests the method removePlayer(), covering all the instructions.
     */
    @Test
    public void removePlayer() {

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
     * Tests the method containsPlayer(), covering all the instructions.
     */
    @Test
    public void containsPlayer() {

        //creates a new weapon square with no weapons
        WeaponSquare weaponSquare = new WeaponSquare(1, 1, 1, 1, RED);

        //adds a player to the weapon square
        Player p = new Player(3, VIOLET);
        weaponSquare.getPlayers().add(p);

        //checks that the weapon square contains the added player
        assertTrue(weaponSquare.containsPlayer(p));

        //removes the added player form the weapon square
        weaponSquare.getPlayers().remove(p);

        //checks that the weapon square does not contain the removed player
        assertFalse(weaponSquare.containsPlayer(p));


    }


    /**
     * Tests the method removeCard(), covering all the instructions.
     */
    @Test
    public void removeWeapon() {

        //creates a new weapon square with no weapons
        WeaponSquare weaponSquare = new WeaponSquare(1, 1, 1, 1, RED);

        //creates three weapons and adds them to the list of weapons
        Weapon lockRifle = WeaponFactory.createWeapon(LOCK_RIFLE);
        Weapon electroscythe = WeaponFactory.createWeapon(ELECTROSCYTHE);
        Weapon tractorBeam = WeaponFactory.createWeapon(TRACTOR_BEAM);
        weaponSquare.addCard(lockRifle);
        weaponSquare.addCard(electroscythe);
        weaponSquare.addCard(tractorBeam);

        //removes a weapon from the weapon square
        weaponSquare.removeCard(electroscythe);

        //checks that the weapon square does not contain the removed weapon
        assertFalse(weaponSquare.getWeapons().contains(electroscythe));
    }


    /**
     * Tests the method addCard(Weapon), covering all the instructions.
     */
    @Test
    public void addSingleWeapon() /*throws Exception */ {

        //creates a new weapon square with no wepaons
        WeaponSquare weaponSquare = new WeaponSquare(1, 1, 1, 1, RED);

        //creates three weapons and adds two of them to the list of weapons
        Weapon lockRifle = WeaponFactory.createWeapon(LOCK_RIFLE);
        Weapon electroscythe = WeaponFactory.createWeapon(ELECTROSCYTHE);
        Weapon tractorBeam = WeaponFactory.createWeapon(TRACTOR_BEAM);
        weaponSquare.addCard(lockRifle);
        weaponSquare.addCard(tractorBeam);

        //checks that the square does not contain the electroscythe
        assertFalse(weaponSquare.getWeapons().contains(electroscythe));

        //adds the remaining weapon to the weapon square
        weaponSquare.addCard(electroscythe);

        //checks that the square contains the added weapon
        assertTrue(weaponSquare.getWeapons().contains(electroscythe));
    }


    /**
     * Tests the method addCard(), covering all the instructions.
     */
    @Test
    public void addSingleWeaponFromDeck() /*throws Exception */ {

        //configures the map and the decks
        BoardConfigurer.getInstance().configureMap(1);
        BoardConfigurer.getInstance().configureDecks();

        //creates a new weapon square with no weapons
        WeaponSquare weaponSquare = Board.getInstance().getSpawnPoints().get(0);

        //creates two weapons and adds them to the list of weapons
        Weapon lockRifle = WeaponFactory.createWeapon(LOCK_RIFLE);
        Weapon tractorBeam = WeaponFactory.createWeapon(TRACTOR_BEAM);
        weaponSquare.addCard(lockRifle);
        weaponSquare.addCard(tractorBeam);

        //checks that the square contains two weapons
        assertEquals(2, weaponSquare.getWeapons().size());

        //adds the first weapon of the weapon deck to the weapon square
        Weapon drawn = (Weapon) Board.getInstance().getWeaponDeck().getDrawable().get(0);
        weaponSquare.addCard();

        //checks that the square contains three weapons and that the added weapon has been drawn from the deck
        assertEquals(3, weaponSquare.getWeapons().size());
        assertEquals(drawn, weaponSquare.getWeapons().get(2));

    }


    /**
     * Tests the method addAllCards(), covering all the instructions.
     */
    @Test
    public void addStartingWeaponsFromDeck() /*throws Exception */ {

        //configures the map and the decks
        BoardConfigurer.getInstance().configureMap(1);
        BoardConfigurer.getInstance().configureDecks();

        //creates a new weapon square with no weapons
        WeaponSquare weaponSquare = Board.getInstance().getSpawnPoints().get(0);

        //checks that the square contains no weapons
        assertEquals(0, weaponSquare.getWeapons().size());

        //adds three weapons from the deck to the weapon square
        Weapon drawn1 = (Weapon) Board.getInstance().getWeaponDeck().getDrawable().get(0);
        Weapon drawn2 = (Weapon) Board.getInstance().getWeaponDeck().getDrawable().get(1);
        Weapon drawn3 = (Weapon) Board.getInstance().getWeaponDeck().getDrawable().get(2);
        weaponSquare.addAllCards();

        //checks that the square contains three weapons and that they have been correctly drawn from the deck
        assertEquals(3, weaponSquare.getWeapons().size());
        assertEquals(drawn1, weaponSquare.getWeapons().get(0));
        assertEquals(drawn2, weaponSquare.getWeapons().get(1));
        assertEquals(drawn3, weaponSquare.getWeapons().get(2));


    }


    /**
     * Tests the method equals(), covering all the instructions.
     */
    @Test
    public void equalsOverride() {

        //creates two identical weapon squares
        //since the id identifies the square, the other fields are set equals too
        //the method equals() only checks the id
        WeaponSquare weaponSquare1 = new WeaponSquare(2, 1, 1, 1, RED);
        WeaponSquare weaponSquare2 = new WeaponSquare(2, 1, 1, 1, RED);
        assertTrue(weaponSquare1.equals(weaponSquare2));

    }


    /**
     * Tests the method hashCode(), covering all the instructions.
     */
    @Test
    public void hashCodeOverride() {

        //creates two identical weapon squares
        //since the id identifies the square, the other fields are set equals too
        //the method equals() only checks the id
        WeaponSquare weaponSquare1 = new WeaponSquare(2, 1, 1, 1, RED);
        WeaponSquare weaponSquare2 = new WeaponSquare(2, 1, 1, 1, RED);
        assertEquals(weaponSquare1.hashCode(), weaponSquare2.hashCode());

    }

}