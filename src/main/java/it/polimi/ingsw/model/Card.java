package it.polimi.ingsw.model;

/**Represent a generic card.
 * It can have a holder and a color.
 *
 * @author  BassaniRiccardo
 */

public interface Card {

    /**
     * Returns the holder of the card, null if absent.
     *
     * @return      the holder of the card, null if absent.
     */
    Player getHolder() throws NotAvailableAttributeException;

    /**
     * Sets the holder of the card, null if absent.
     *
     * @return      the value to assign to the holder of the card, null if absent.
     */

    void setHolder(Player holder) throws NotAvailableAttributeException;

    /**
     * Returns the color of the card, null if absent.
     *
     * @return      the color of the card, null if absent.
     */
    Color getColor() throws NotAvailableAttributeException;

}
