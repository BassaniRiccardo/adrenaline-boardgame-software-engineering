//TODO add a flag to each firemode signaling whether it can be used before and/or after the main firemode

package it.polimi.ingsw.model;
import java.util.List;

/**
 * Class modeling a single firemode of a weapon.
 *
 * @author marcobaga
 */

public class FireMode implements Targeted {

    public enum FireModeName {
        MAIN, SECONDARY, OPTION1, OPTION2
    }

    private final FireModeName name;
    private final int maxTargets;
    private final AmmoPack cost;
    private Weapon weapon;

    private final Effect effect;
    private final TargetFinder targetFinder;
    private final DestinationFinder destinationFinder;

    /**
     * Constructs a firemode, with a name, the number of targets, the cost in ammo packs,
     * the related destination finder, target finder and effect.
     *
     * @param name                  the firemode name.
     * @param targetNumber          the number of targets.
     * @param cost                  the cost in ammo packs.
     * @param destinationFinder     the related destination finder.
     * @param targetFinder          the related target finder.
     * @param effect                the related effect.
     */
    public FireMode(FireModeName name, int targetNumber, AmmoPack cost, DestinationFinder destinationFinder, TargetFinder targetFinder, Effect effect){

        this.name = name;
        this.maxTargets = targetNumber;
        this.cost = cost;
        this.destinationFinder = destinationFinder;
        this.targetFinder = targetFinder;
        this.effect = effect;

    }

    /**
     * Getters
     */

    public int getMaxTargets(){ return maxTargets; }

    public AmmoPack getCost() { return cost; }

    public FireModeName getName() { return name; }

    public Effect getEffect() { return effect; }

    public TargetFinder getTargetFinder() { return targetFinder; }

    public DestinationFinder getDestinationFinder() { return destinationFinder; }

    public Weapon getWeapon() { return weapon; }

    /**
     * Setters.
     */
    public void setWeapon(Weapon weapon){ this.weapon = weapon; }

    /**
     *Applies the effects of this firemode to targets chosen.
     *
     * @param  targets  the ArrayList of players being targeted
     * @param  destination the Square players are moved to, if relevant
     */
    public void applyEffects(List<Player> targets, Square destination){

        if(targets == null){
            throw new IllegalArgumentException();
        }
        for(Player p : targets){
            effect.apply(weapon.getHolder(), p, destination);
        }
        weapon.getHolder().useAmmo(cost);

    }

    /**
     *Finds players that can be chosen as targets
     *
     * @return      an ArrayList containing sets of targets to be chosen, each saved as an ArrayList
     */
    public List<List<Player>> findTargets(){

        return targetFinder.find(weapon.getHolder());

    }

    /**
     *Finds Squares that can be chosen as destination. if relevant
     *
     * @param  targets the ArrayList of already selected targets
     * @return      the set of possible destination Square objects
     */
    public List<Square> findDestinations(List<Player> targets)throws NullPointerException{
        if(weapon.getHolder()==null||targets==null){
            throw new NullPointerException();
        }
        return destinationFinder.find(weapon.getHolder(), targets);
    }

    /**
     *Establishes if this firemode can be selected according to the current board state
     *
     * @return      true is this FireMode can be used
     */
    public boolean isAvailable() {
        return !(findTargets().isEmpty());
    }

}