package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.board.Player;
import it.polimi.ingsw.model.board.Square;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;

import java.util.List;


/**
 * Interface shared by FireMode and PowerUp. Has methods to select potential targets (Players and Squares)
 * Has a cost and can check its own availability.app.
 *
 * @author  marcobaga
 */

public interface Targeted {

    /**
     * Applies the effects of the FireMode of PowerUp by modifying the state of the game.
     *
     * @param targets   players selected as targets
     * @param destination   destination that the user or other players may be moved to
     * @throws NotAvailableAttributeException
     */
    void applyEffects(List<Player> targets, Square destination) throws NotAvailableAttributeException;

    /**
     * Simple method finding targets (other players)
     *
     * @return  list of possible target groups
     * @throws NotAvailableAttributeException
     */
    List<List<Player>> findTargets() throws NotAvailableAttributeException;

    /**
     * Simple method finding destinations (squares the user or the targets may be moved to)
     *
     * @param targets   set of targets selected
     * @return          list of possible destinations
     * @throws NotAvailableAttributeException
     */
    List<Square> findDestinations(List<Player> targets) throws NotAvailableAttributeException;

    /**
     * Checks if the FireMode or PowerUp can be used.
     *
     * @return      true if it can be used, else false
     * @throws NotAvailableAttributeException
     */
    boolean isAvailable() throws NotAvailableAttributeException;

}