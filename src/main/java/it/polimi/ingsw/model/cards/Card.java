package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.board.Player;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;

/**
 * Represent a generic card.
 * It can have a holder and a color.
 *
 * @author  BassaniRiccardo
 */

public interface Card {


    /**
     * Returns the holder of the card, null if absent.
     *
     * @return      the holder of the card, null if absent.
     * @throws NotAvailableAttributeException if the card does not have a holder.
     */
    Player getHolder() throws NotAvailableAttributeException;


    /**
     * Sets the holder of the card, null if absent.
     *
     * @throws NotAvailableAttributeException if the card should not have a holder.
     */

    void setHolder(Player holder) throws NotAvailableAttributeException;


    /**
     * Returns the color of the card, null if absent.
     *
     * @return      the color of the card, null if absent.
     * @throws NotAvailableAttributeException if the card does not have a holder.
     */
    Color getColor() throws NotAvailableAttributeException;

}
