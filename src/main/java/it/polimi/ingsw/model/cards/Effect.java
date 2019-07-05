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
     * An Effect is an attribute of PowerUps or FireModes and implements the modifications these cards cause to the game state.
     * In order to function, an effect must have a reference to the player it is being applied on, to the player that is acting
     * and to the square it might move somebody in.
     *
     * @param  shooter      the Player who is taking action in this turn
     * @param  target       the Player the effect is applied on
     * @param  destination  the Square players are moved to, if relevant
     * @throws NotAvailableAttributeException if the board is malformed
     */
    void apply(Player shooter, Player target, Square destination) throws NotAvailableAttributeException;

}