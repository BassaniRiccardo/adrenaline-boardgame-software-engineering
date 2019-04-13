package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A functional interface capable of selecting the squares in which a movement can be made.
 *
 * @author  marcobaga
 */

interface DestinationFinder{

    /**
     * Returns an ArrayList containing Square objects which can be
     * selected as a destination by the player.
     * @requires !isEmpty(targets)
     *
     * @param  shooter      the Player who is taking action in this turn
     * @param  targets      the ArrayList of players selected as targets by the current player
     * @return              the set of possible destination Square objects
     */
    List<Square> find(Player shooter, List<Player> targets) throws NotAvailableAttributeException;
}
