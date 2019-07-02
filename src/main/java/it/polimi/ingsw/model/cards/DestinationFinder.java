package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.board.Player;
import it.polimi.ingsw.model.board.Square;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;

import java.util.List;

/**
 * A functional interface capable of selecting the squares in which a movement can be made.
 *
 * @author  marcobaga
 */

public interface DestinationFinder{

    /**
     * Returns an ArrayList containing Square objects which can be
     * selected as a destination by the player.
     *
     * @param  shooter      the Player who is taking action in this turn
     * @param  targets      the ArrayList of players selected as targets by the current player
     * @return              the set of possible destination Square objects
     */
    List<Square> find(Player shooter, List<Player> targets) throws NotAvailableAttributeException;
}
