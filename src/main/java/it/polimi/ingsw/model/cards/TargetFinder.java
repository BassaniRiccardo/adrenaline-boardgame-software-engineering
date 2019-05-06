package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.board.Player;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;

import java.util.List;

/**
 * Functional interface that finds groups of targets that player can hit.
 *
 * @author  marcobaga
 */

public interface TargetFinder {

    List<List<Player>> find(Player shooter) throws NotAvailableAttributeException;

}
