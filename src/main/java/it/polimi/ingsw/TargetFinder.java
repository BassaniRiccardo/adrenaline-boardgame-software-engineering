package it.polimi.ingsw.model;

import java.util.List;

/**
 * Functional interface that finds groups of targets that player can hit.
 *
 * @author  marcobaga
 */

interface TargetFinder{

    List<List<Player>> find(Player shooter);

}
