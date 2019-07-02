package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.board.Player;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;

import java.util.List;

/**
 * Functional interface that finds groups of targets that the player can hit.
 *
 * @author  marcobaga
 */

public interface TargetFinder {

    /**
     * Returns the groups of targets that the player can hit.
     *
     * @param shooter       the shooting player.
     * @return              the groups of targets that the player can hit.
     * @throws NotAvailableAttributeException if an attribute of one of the involved players has not been initialized when the methods is called.
     */
    List<List<Player>> find(Player shooter) throws NotAvailableAttributeException;

}
