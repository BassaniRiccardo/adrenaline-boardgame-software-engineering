package it.polimi.ingsw.model;

import java.util.List;

/**
 * Class modeling a powerup card.
 *
 * @author  marcobaga
 */

public class PowerUp implements Targeted, Card {

    public enum PowerUpName{

        TARGETING_SCOPE, NEWTON, TAGBACK_GRENADE, TELEPORTER;

    }

    private PowerUpName name;
    private AmmoPack cost;
    private Player holder;
    private Color color;
    private Effect effect;
    private TargetFinder targetFinder;
    private DestinationFinder destinationFinder;


    /**
     * Constructs a power up, with a name, the cost in ammo packs,
     * the related destination finder, target finder and effect and the color.
     *
     * @param powerUpName           the power up name.
     * @param cost                  the cost in ammo packs.
     * @param destinationFinder     the related destination finder.
     * @param targetFinder          the related target finder.
     * @param effect                the related effect.
     * @param color                 the color of the power up.
     */
    public PowerUp(PowerUpName powerUpName, AmmoPack cost, DestinationFinder destinationFinder, TargetFinder targetFinder, Effect effect, Color color){

        this.name = powerUpName;
        this.cost = cost;
        this.holder = null;
        this.color = color;
        this.destinationFinder = destinationFinder;
        this.targetFinder = targetFinder;
        this.effect = effect;

    }

    /**
     * Getters
     */

    public Effect getEffect() {
        return effect;
    }

    public TargetFinder getTargetFinder() {
        return targetFinder;
    }

    public DestinationFinder getDestinationFinder() {
        return destinationFinder;
    }

    public void setHolder(Player holder){
        this.holder = holder;
    }

    public PowerUpName getName() {
        return name;
    }

    public Player getHolder() throws NotAvailableAttributeException {
        if (holder==null) throw new NotAvailableAttributeException("This power up does not have an holder.");
        return holder;
    }

    public Color getColor() {
        return color;
    }

    public AmmoPack getCost(){ return cost;}


    /**
     *Applies the effects of this powerup to targets chosen.
     *
     * @param  playerList  the ArrayList of players being targeted
     * @param  destination the Square players are moved to, if relevant
     */
    public void applyEffects(List<Player> playerList, Square destination) throws NotAvailableAttributeException{

        if (!Board.getInstance().getPlayers().containsAll(playerList)) throw new IllegalArgumentException("The effects can be applied only on players on the board.");
        if (!(Board.getInstance().getMap().contains(destination) || destination == null)) throw new IllegalArgumentException("The players can be moved only in squares that belong to the board.");
        for(Player p : playerList){
            effect.apply(holder, p, destination);
        }
        holder.useAmmo(cost);
    }

    /**
     *Finds players that can be chosen as targets
     *
     * @return      an ArrayList containing sets of targets to be chosen, each saved as an ArrayList
     */
    public List<List<Player>> findTargets() throws NotAvailableAttributeException{
        return targetFinder.find(holder);
    }


    /**
     *Finds Squares that can be chosen as destination. if relevant
     *
     * @param  targets the ArrayList of already selected targets
     * @return      the set of possible destination Square objects
     */
    public List<Square> findDestinations(List<Player> targets) throws NotAvailableAttributeException{

        if (!Board.getInstance().getPlayers().containsAll(targets)) throw new IllegalArgumentException("Only on players on the board can be moved.");
        return destinationFinder.find(holder, targets);
    }


    /**
     *Establishes if this powerup can be used according to the current board state
     *
     * @return      true if and only if  this powerup cna be used
     */
    public boolean isAvailable() throws NotAvailableAttributeException{
        return !(findTargets().isEmpty())&&!(name==PowerUpName.TARGETING_SCOPE&&holder.getAmmoPack().equals(new AmmoPack(0,0,0)));

    }

}