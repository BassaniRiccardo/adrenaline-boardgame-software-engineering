package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.Updater;
import it.polimi.ingsw.model.cards.AmmoTile;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Color;
import it.polimi.ingsw.model.exceptions.NoMoreCardsException;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;
import it.polimi.ingsw.model.exceptions.UnacceptableItemNumberException;
import it.polimi.ingsw.model.exceptions.WrongTimeException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Extends the class Square.
 * Can contain an ammo tile, that can be collected by a player.
 * Collected ammo tiles are replaced by drawing from the deck of ammo tiles.
 *
 * @author  BassaniRiccardo
 */

public class AmmoSquare extends Square  {

    private AmmoTile ammoTile;
    private static final Logger LOGGER = Logger.getLogger("serverLogger");

    /**
     * Constructs an AmmoSquare with a reference to the game board, an id, a room id, a row, a column, a color and an ammo tile.
     * Invokes the constructor of Square.
     *
     * @param board         the board the square belongs to.
     * @param id            the id of the square.
     * @param roomId        the id of the room the square is in.
     * @param row           the row of the square.
     * @param column        the column of the square.
     * @param color         the color of the square.
     * @param ammoTile      the ammo tile in the square.
     * @throws              IllegalArgumentException
     */
    public AmmoSquare(Board board, int id, int roomId, int row, int column, Color color, AmmoTile ammoTile) {

        super(board, id, roomId, row, column, color);
        this.ammoTile = ammoTile;

    }

    /**
     * Constructs an AmmoSquare with an id, a room id, a row, a column, and a color.
     * Invokes the constructor of Square.
     *
     * @param id            the id of the square.
     * @param roomId        the id of the room the square is in.
     * @param row           the row of the square.
     * @param column        the column of the square.
     * @param color         the color of the square.
     * @throws              IllegalArgumentException
     */
    public AmmoSquare(Board board, int id, int roomId, int row, int column, Color color) {

        super(board, id, roomId, row, column, color);
        this.ammoTile = null;

    }

    /**
     * Returns true if the ammo square does not contain an ammo tile.
     *
     * @return  true if the ammo square does not contain an ammo tile.
     *          false otherwise.
     */
    @Override
    public boolean isEmpty(){
        return !hasAmmoTile();
    }


    /**
     * Getter for ammoTile.
     *
     * @return      the ammo tile in the square.
     */
    public AmmoTile getAmmoTile() throws NotAvailableAttributeException {

        if (ammoTile==null) throw new NotAvailableAttributeException("Impossible to return the value: the square does not contain any ammo tile");
        return ammoTile;

    }


    /**
     * Removes an ammo tile from the square and returns it to the player who collected it.
     *
     * @return      the collected ammo tile.
     * @throws NoMoreCardsException
     */
    public Card removeCard(Card ammoTile) throws NoMoreCardsException{

        if (this.ammoTile == null) throw new NoMoreCardsException("Impossible to remove the card: the square does not contain any ammo tile");

        Card collected = this.ammoTile;
        this.ammoTile = null;
        board.addToUpdateQueue(Updater.get("removeAmmoTile", this));
        return collected;

    }


    /**
     * Add an ammo tile to the square.
     *
     * @throws NoMoreCardsException
     * @throws UnacceptableItemNumberException
     */
    public void addAllCards() throws NoMoreCardsException, UnacceptableItemNumberException {

        if (ammoTile!=null)  throw new UnacceptableItemNumberException("The square already contains an ammo tile.");

        if (this.getBoard().getAmmoDeck().getDrawable().isEmpty()) {
            try {
                this.getBoard().getAmmoDeck().regenerate();
            } catch (WrongTimeException e) {
                LOGGER.log(Level.SEVERE, "Exception thrown while regenerating the deck", e);
            }
        }

        this.ammoTile = (AmmoTile) this.getBoard().getAmmoDeck().drawCard();

    }

    /**
     * Getter for ammoTile.
     *
     * @return      the ammo tile in the square.
     */
    public boolean hasAmmoTile() {
        return (ammoTile!=null);
    }


    /**
     * Returns true if the compared objects are two weapon squares belonging to the same board with the same id.
     *
     * @param o    the ammo square to compare to the current ammo square.
     * @return     true if the compared objects are two weapon squares belonging to the same board with the same id.
     *             false otherwise.
     */
    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        // Check if o is an instance of AmmoSquare or not
        if (!(o instanceof AmmoSquare)) {
            return false;
        }

        // typecast o to AmmoSquare so that we can compare data members
        AmmoSquare s = (AmmoSquare) o;

        // Compare the data members and return accordingly
        return s.getId() == getId() && s.getBoard().equals(getBoard());

    }


    /**
     *Returns the hashCode of the ammo square.
     */
    @Override
    public int hashCode() {

        int result;
        result = getId() + getBoard().hashCode();
        return result;

    }
}