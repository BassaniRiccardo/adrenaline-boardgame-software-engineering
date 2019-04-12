package it.polimi.ingsw.model;

import java.util.List;


/**
 * Interface shared by FireMode and PowerUp. Has methods to select potential targets (Players and Squares)
 * Has a cost and can check its own availability.app.
 *
 * @author  marcobaga
 */

//TODO overloading

public interface Targeted {

    void applyEffects(List<Player> targets, Square destination);

    List<List<Player>> findTargets();

    List<Square> findDestinations(List<Player> targets);

    boolean isAvailable();

    AmmoPack getCost();

}