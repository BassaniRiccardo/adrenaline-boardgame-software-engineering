package it.polimi.ingsw.model;

import java.util.*;

/**
 * Extends the class Square.
 * Contains up to tree weapons, that can be collected by a player.
 * Collected weapons are replaced drawing from the deck of weapons.
 * Exceptions written:
 *
 * -    removeCard(Weapon)
 * -    addAllCards()
 * -    addCard(Card)
 * -    addCard()
 *
 * @author  BassaniRiccardo
 */

public class WeaponSquare extends Square {

    private List<Weapon> weapons;


    /**
     * Constructs a WeaponSquare with the id, the room id, the row, the column, the color and the weapons.
     * Invokes the constructor of Square.
     *
     * @param id            the id of the square.
     * @param roomId        the id of the room the square is in.
     * @param row           the row of the square.
     * @param column        the column of the square.
     * @param color         the color of the square.
     * @param weapons       the weapons in the square.
     * @throws              IllegalArgumentException
     */
    public WeaponSquare(int id, int roomId, int row, int column, Color color, List<Weapon> weapons) {

        super(id, roomId, row, column, color);
        this.weapons = weapons;

    }

    /**
     * Constructs a WeaponSquare with the id, the room id, the row, the column and the color.
     * Invokes the constructor of Square.
     *
     * @param id            the id of the square.
     * @param roomId        the id of the room the square is in.
     * @param row           the row of the square.
     * @param column        the column of the square.
     * @param color         the color of the square.
     * @throws              IllegalArgumentException
     */
    public WeaponSquare(int id, int roomId, int row, int column, Color color) {

        super(id, roomId, row, column, color);
        this.weapons = new ArrayList<>();

    }

    /**
     * Getter for weapons.
     *
     * @return      the weapons in the square.
     */
    public List<Weapon> getWeapons() {
        return weapons;
    }


    /**
     * Removes a weapon from the square
     *
     * @param weapon        the weapon to remove.
     * @throws              NoMoreCardsException
     */
    public Card removeCard(Card weapon) throws NoMoreCardsException {

        if (weapons.isEmpty()) throw new NoMoreCardsException("Impossible to remove the card: the square does not contain weapons.");

        this.weapons.remove(weapon);

        return weapon;

    }


    /**
     * Adds three weapons from the deck to the weapon square.
     * It is called only at the beginning of the game, when all the squares are empty.
     *
     * @throws UnacceptableItemNumberException
     * @throws NoMoreCardsException
     */
    public void addAllCards() throws UnacceptableItemNumberException, NoMoreCardsException {

        if (!weapons.isEmpty()) throw new UnacceptableItemNumberException("The square already contains three weapons.");

        for (int i = 0; i < 3; i++) {
            this.weapons.add((Weapon) Board.getInstance().getWeaponDeck().drawCard());
        }

    }


    /**
     * Adds a weapon from the deck the to the weapon square.
     * It is called at the end of a turn if a collected weapon needs to be replaced.
     *
     * @throws UnacceptableItemNumberException
     * @throws NoMoreCardsException
     */
    public void addCard() throws UnacceptableItemNumberException, NoMoreCardsException {

        if (weapons.size() >= 3) throw new UnacceptableItemNumberException("No weapons left in the deck.");

        this.weapons.add((Weapon)Board.getInstance().getWeaponDeck().drawCard());

    }


    /**
     * Adds a particular weapon to the square.
     * It is called when a player discards one of his weapons.
     *
     * @param weapon        the discarded weapon.
     * @throws              UnacceptableItemNumberException
     */

    public void addCard(Weapon weapon) throws UnacceptableItemNumberException {

        if (weapons.size() >= 3) throw new UnacceptableItemNumberException("The square already contains three weapons.");

        this.weapons.add(weapon);

    }


    /**
     * Returns true if the two weapon squares have the same id.
     *
     * @param o    the weapon square to compare to the current weapon square.
     * @return     true if the two weapon squares have the same id.
     *             false otherwise.
     */
    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        // Check if o is an instance of WeaponSquare or not
        if (!(o instanceof WeaponSquare)) {
            return false;
        }

        // typecast o to WeaponSquare so that we can compare data members
        WeaponSquare s = (WeaponSquare) o;

        // Compare the data members and return accordingly
        return s.getId() == getId();
        
    }


    /**
     *Returns the hashCode of the weapon square.
     */
    @Override
    public int hashCode() {

        int result;
        result = getId();
        return result;

    }
}