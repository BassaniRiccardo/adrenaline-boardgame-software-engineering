package it.polimi.ingsw.model;

import org.junit.Test;

import static it.polimi.ingsw.model.Color.*;
import static it.polimi.ingsw.model.Weapon.WeaponName.*;
import static org.junit.Assert.*;

/**
 * Tests all methods of the class WeaponSquare, covering all the instructions apart from the first constructor.
 * The method shared with the class AmmoSquare are tested in SquareTest.
 *
 * @author BassaniRiccardo
 */

public class WeaponSquareTest {


    /**
     * Tests the method removeCard(), covering all the instructions apart from the exception.
     *
     * @throws NoMoreCardsException
     * @throws UnacceptableItemNumberException
     */
    @Test
    public void removeWeapon() throws NoMoreCardsException, UnacceptableItemNumberException {

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
     * Tests the method removeCard(), when an exception should be thrown since the square is empty.
     *
     * @throws NoMoreCardsException
     */
    @Test(expected = NoMoreCardsException.class )

    public void removeWeaponEmptySquare() throws NoMoreCardsException {

        //creates a new weapon square with no weapons
        WeaponSquare weaponSquare = new WeaponSquare(1, 1, 1, 1, RED);

        //creates the weapons that the player wants to collect
        Weapon electroscythe = WeaponFactory.createWeapon(ELECTROSCYTHE);

        //tries to remove the weapon from the weapon square
        weaponSquare.removeCard(electroscythe);

        //checks that the weapon square is still empty
        assertEquals(0, weaponSquare.getWeapons().size());
    }


    /**
     * Tests the method addCard(Weapon), covering all the instructions, apart form the exception.
     * Testing the exception is not necessary since there are no relevant
     * differences with the test addSingleWeaponFromDeckFullSquare.
     *
     * @throws UnacceptableItemNumberException
     */
    @Test
    public void addSingleWeapon() throws UnacceptableItemNumberException {

        //creates a new weapon square with no weapons
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
     * Tests the method addCard(), covering all the instructions apart from the exception.
     *
     * @throws UnacceptableItemNumberException
     * @throws NoMoreCardsException
     */
    @Test
    public void addSingleWeaponFromDeck() throws UnacceptableItemNumberException, NoMoreCardsException {

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
        try {
            weaponSquare.addCard();
        }
        catch (UnacceptableItemNumberException e) {
            e.printStackTrace();
        }

        //checks that the square contains three weapons and that the added weapon has been drawn from the deck
        assertEquals(3, weaponSquare.getWeapons().size());
        assertEquals(drawn, weaponSquare.getWeapons().get(2));

    }


    /**
     * Tests the method addCard() when an exception should be thrown since the square already contains three weapons.
     *
     * @throws UnacceptableItemNumberException
     * @throws NoMoreCardsException
     */
    @Test(expected = UnacceptableItemNumberException.class)

    public void addSingleWeaponFromDeckFullSquare() throws UnacceptableItemNumberException, NoMoreCardsException {

        //configures the map and the decks
        BoardConfigurer.getInstance().configureMap(1);
        BoardConfigurer.getInstance().configureDecks();

        //creates a new weapon square with no weapons
        WeaponSquare weaponSquare = Board.getInstance().getSpawnPoints().get(0);

        //creates three weapons and adds them to the list of weapons
        Weapon lockRifle = WeaponFactory.createWeapon(LOCK_RIFLE);
        Weapon tractorBeam = WeaponFactory.createWeapon(TRACTOR_BEAM);
        Weapon rocketLauncher = WeaponFactory.createWeapon(ROCKET_LAUNCHER);
        weaponSquare.addCard(lockRifle);
        weaponSquare.addCard(tractorBeam);
        weaponSquare.addCard(rocketLauncher);

        //checks that the square contains two weapons
        assertEquals(3, weaponSquare.getWeapons().size());

        //adds the first weapon of the weapon deck to the weapon square
        Weapon drawn = (Weapon) Board.getInstance().getWeaponDeck().getDrawable().get(0);
        weaponSquare.addCard();

        //checks that the square contains three weapons and that the added weapon has been drawn from the deck
        assertEquals(3, weaponSquare.getWeapons().size());
        assertFalse(weaponSquare.getWeapons().contains(drawn));

    }


    /**
     * Tests the method addAllCards(), covering all the instructions apart form the exceptions.
     *
     * @throws UnacceptableItemNumberException
     * @throws NoMoreCardsException
     */
    @Test
    public void addStartingWeaponsFromDeck() throws UnacceptableItemNumberException, NoMoreCardsException {

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
     * Tests the method addAllCards(), when an exception should be thrown since it is not called after the setup phase.
     *
     * @throws UnacceptableItemNumberException
     * @throws NoMoreCardsException
     */
    @Test(expected = UnacceptableItemNumberException.class)
    public void addStartingWeaponsFromDeckNotSetup() throws UnacceptableItemNumberException, NoMoreCardsException {

        //configures the map and the decks
        BoardConfigurer.getInstance().configureMap(1);
        BoardConfigurer.getInstance().configureDecks();

        //creates a new weapon square with no weapons
        WeaponSquare weaponSquare = Board.getInstance().getSpawnPoints().get(0);

        //adds a weapon to the square
        Weapon weapon = WeaponFactory.createWeapon(POWER_GLOVE);
        weaponSquare.getWeapons().add(weapon);

        //adds three weapons from the deck to the weapon square
        weaponSquare.addAllCards();

        //checks that the square only contains the weapon it already contained
        assertEquals(1, weaponSquare.getWeapons().size());

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