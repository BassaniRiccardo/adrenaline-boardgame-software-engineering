package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.board.Player;
import it.polimi.ingsw.model.board.Square;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;

/**
 * A functional interface that dictates changes to a player state.
 *
 * @author  marcobaga
 */

public interface Effect{

    /**
     * Applies the outcomes of the usage of a weapon or power up.
     *
     * @param  shooter      the Player who is taking action in this turn
     * @param  target       the Player the effect is applied on
     * @param  destination  the Square players are moved to, if relevant
     */
    void apply(Player shooter, Player target, Square destination) throws NotAvailableAttributeException;

}
