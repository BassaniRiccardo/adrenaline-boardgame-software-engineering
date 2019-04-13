package it.polimi.ingsw.model;

import java.util.List;


/**
 * Interface shared by FireMode and PowerUp. Has methods to select potential targets (Players and Squares)
 * Has a cost and can check its own availability.app.
 *
 * @author  marcobaga
 */

//TODO overloading?

public interface Targeted {

    void applyEffects(List<Player> targets, Square destination) throws NotAvailableAttributeException;

    List<List<Player>> findTargets() throws NotAvailableAttributeException;

    List<Square> findDestinations(List<Player> targets) throws NotAvailableAttributeException;

    boolean isAvailable() throws NotAvailableAttributeException;

    AmmoPack getCost() ;

}