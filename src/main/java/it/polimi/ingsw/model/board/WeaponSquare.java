package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.Updater;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Color;
import it.polimi.ingsw.model.cards.Weapon;
import it.polimi.ingsw.model.exceptions.NoMoreCardsException;
import it.polimi.ingsw.model.exceptions.UnacceptableItemNumberException;

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
    private static final int MAX_WEAPONS_ON_SQUARE = 3;



    /**
     * Constructs a WeaponSquare with a reference to the game board, an id, a room id, a row, a column, a color and some weapons.
     * Invokes the constructor of Square.
     *
     * @param board         the board the square belongs to.
     * @param id            the id of the square.
     * @param roomId        the id of the room the square is in.
     * @param row           the row of the square.
     * @param column        the column of the square.
     * @param color         the color of the square.
     * @param weapons       the weapons in the square.
     * @throws IllegalArgumentException      if parameters does not respect the constrains.
     */
    public WeaponSquare(Board board, int id, int roomId, int row, int column, Color color, List<Weapon> weapons) {

        super(board, id, roomId, row, column, color);
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
     * @throws IllegalArgumentException      if parameters does not respect the constrains.
     */
    public WeaponSquare(Board board, int id, int roomId, int row, int column, Color color) {

        super(board, id, roomId, row, column, color);
        this.weapons = new ArrayList<>();

    }


    /**
     * Setter for weapons.
     *
     * @param weapons       the value to assign to weapons.
     */
    public void setWeapons(List<Weapon> weapons) {
        this.weapons = weapons;
    }

    /**
     * Returns true if the weapon square does not contain a weapon.
     *
     * @return  true if the weapon square does not contain a weapon.
     *          false otherwise.
     */
    @Override
    public boolean isEmpty(){
        return getWeapons().isEmpty();
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
     * @throws NoMoreCardsException     if the square does not contain weapons.
     * @throws IllegalArgumentException if the weapon does not belong to the square.
     */
    public Card removeCard(Card weapon) throws NoMoreCardsException {

        if (weapons.isEmpty()) throw new NoMoreCardsException("Impossible to remove the card: the square does not contain weapons.");
        if (!weapons.contains(weapon)) throw new IllegalArgumentException("Impossible to remove the weapon since it is not in the square.");
        this.weapons.remove(weapon);

        board.addToUpdateQueue(Updater.get(Updater.REMOVE_WEAPON_UPD, this, (Weapon)weapon));

        return weapon;
    }


    /**
     * Adds three weapons from the deck to the weapon square.
     * It is called only at the beginning of the game, when all the squares are empty.
     *
     * @throws UnacceptableItemNumberException      if the square is not empty.
     * @throws NoMoreCardsException                 if no drawable cards are present in the weapon deck.
     */
    public void addAllCards() throws UnacceptableItemNumberException, NoMoreCardsException {

        if (!weapons.isEmpty()) throw new UnacceptableItemNumberException("The square already contains some weapons.");

        for (int i = 0; i < MAX_WEAPONS_ON_SQUARE; i++) {
            this.weapons.add((Weapon) this.getBoard().getWeaponDeck().drawCard());
        }

    }


    /**
     * Adds a weapon from the deck the to the weapon square.
     * It is called at the end of a turn if a collected weapon needs to be replaced.
     *
     * @throws UnacceptableItemNumberException      if the square already contains three weapons.
     * @throws NoMoreCardsException                 if no drawable cards are present in the weapon deck.
     */
    public void addCard() throws UnacceptableItemNumberException, NoMoreCardsException {

        if (weapons.size() >= MAX_WEAPONS_ON_SQUARE) throw new UnacceptableItemNumberException("A weapon square can not contain mare than " + MAX_WEAPONS_ON_SQUARE + " weapons.");
        this.weapons.add((Weapon)this.getBoard().getWeaponDeck().drawCard());

    }


    /**
     * Adds a particular weapon to the square.
     * It is called when a player discards one of his weapons.
     *
     * @param weapon        the discarded weapon.
     * @throws              UnacceptableItemNumberException     if the square already contains three weapons.
     */

    public void addCard(Weapon weapon) throws UnacceptableItemNumberException {

        if (weapons.size() >= MAX_WEAPONS_ON_SQUARE) throw new UnacceptableItemNumberException("The square already contains " + MAX_WEAPONS_ON_SQUARE + " weapons.");
        this.weapons.add(weapon);

    }


    /**
     * Returns true if the compared objects are two weapon squares belonging to the same board with the same id.
     *
     * @param o    the weapon square to compare to the current weapon square.
     * @return     true if the compared objects are two weapon squares belonging to the same board with the same id.
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
        return s.getId() == getId() && s.getBoard().equals(getBoard());
        
    }


    /**
     *Returns the hashCode of the weapon square.
     */
    @Override
    public int hashCode() {

        int result;
        result = getId() + getBoard().hashCode();
        return result;

    }
}