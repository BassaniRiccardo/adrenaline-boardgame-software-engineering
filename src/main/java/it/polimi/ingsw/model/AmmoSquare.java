package it.polimi.ingsw.model;

/**
 * Extends the class Square.
 * Can contain an ammo tile, that can be collected by a player.
 * Collected ammo tiles are replaced by drawing from the deck of ammo tiles.
 *
 * @author  BassaniRiccardo
 */

public class AmmoSquare extends Square  {

    private AmmoTile ammoTile;

    /**
     * Constructs an AmmoSquare with an id, a room id, a row, a column, a color and an ammo tile.
     * Invokes the constructor of Square.
     *
     * @param id            the id of the square.
     * @param roomId        the id of the room the square is in.
     * @param row           the row of the square.
     * @param column        the column of the square.
     * @param color         the color of the square.
     * @param ammoTile      the ammo tile in the square.
     * @throws              IllegalArgumentException
     */
    public AmmoSquare(int id, int roomId, int row, int column, Color color, AmmoTile ammoTile) {

        super(id, roomId, row, column, color);
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
    public AmmoSquare(int id, int roomId, int row, int column, Color color) {

        super(id, roomId, row, column, color);
        this.ammoTile = null;

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
     * @throws      NoMoreCardsException
     */
    public Card removeCard(Card ammoTile) throws NoMoreCardsException{

        if (this.ammoTile == null) throw new NoMoreCardsException("Impossible to remove the card: the square does not contain any ammo tile");

        Card collected = this.ammoTile;
        this.ammoTile = null;
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

        this.ammoTile = (AmmoTile) Board.getInstance().getAmmoDeck().drawCard();

    }


    /**
     * Returns true if the two ammo squares have the same id.
     *
     * @param o    the ammo square to compare to the current ammo square.
     * @return     true if the two ammo squares have the same id.
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
        return s.getId() == getId();

    }


    /**
     *Returns the hashCode of the ammo square.
     */
    @Override
    public int hashCode() {

        int result = 0;
        result = getId();
        return result;

    }
}