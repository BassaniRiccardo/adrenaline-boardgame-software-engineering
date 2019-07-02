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
     * Returns an ArrayList containing Square objects which can be selected as a destination by the player (either for his
     * own movement or as a destination for other players he is causing to move). A custom DestinationFinder exists for most
     * PowerUps and FireModes and selects Squares from the Board according to the effect of the card.
     * A DestinationFinder requires to know information about the current player and about his targets (the players he is shooting at).
     *
     * @param  shooter      the Player who is taking action in this turn
     * @param  targets      the ArrayList of players selected as targets by the current player
     * @return              the set of possible destination Square objects
     */
    List<Square> find(Player shooter, List<Player> targets) throws NotAvailableAttributeException;
}
