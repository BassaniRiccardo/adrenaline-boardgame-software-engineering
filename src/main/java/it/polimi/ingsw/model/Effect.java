package it.polimi.ingsw.model;

/**
 * A functional interface that dictates changes to a player state.
 *
 * @author  marcobaga
 */

//TODO empty interface Effect, extend it by having different implementation of apply (2 or 3 parameters)

interface Effect{

    /**
     * Applies the outcomes of the usage of a weapon or power up.
     *
     * @param  shooter      the Player who is taking action in this turn
     * @param  target       the Player the effect is applied on
     * @param  destination  the Square players are moved to, if relevant
     */
    void apply(Player shooter, Player target, Square destination);

}
