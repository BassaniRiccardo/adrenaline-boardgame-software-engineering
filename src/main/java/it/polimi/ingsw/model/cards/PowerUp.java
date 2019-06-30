package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Player;
import it.polimi.ingsw.model.board.Square;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;
import it.polimi.ingsw.view.ClientModel;

import java.util.List;

import static it.polimi.ingsw.model.cards.Color.*;

/**
 * Class modeling a power up card.
 *
 * @author  marcobaga
 */

public class PowerUp implements Targeted, Card {

    /**
     * An enumeration for the powerups names.
     */
    public enum PowerUpName{

        TARGETING_SCOPE, NEWTON, TAGBACK_GRENADE, TELEPORTER;

        /**
         * Returns a string representing the powerup name.
         *
         * @return a string representing the powerup name.
         */
        @Override
        public String toString(){
            String name = (this.name().substring(0,1) + this.name().toLowerCase().substring(1)).replace('_', ' ');
            String res = name;
            for(int i=0; i<name.length(); i++){
                if(name.charAt(i)==' '&&i<name.length()-1){
                    res = res.substring(0,i+1) + res.toUpperCase().substring(i+1, i+2) + res.substring(i+2);
                }
            }
            return res;
        }

    }

    private PowerUpName name;
    private Player holder;
    private Color color;
    private Effect effect;
    private TargetFinder targetFinder;
    private DestinationFinder destinationFinder;
    private Board board;
    private static final String RESET = "\u001b[0m";


    /**
     * Constructs a power up, with a name, the related destination finder, target finder and effect, the color and a reference to the game board.
     *
     * @param powerUpName           the power up name.
     * @param destinationFinder     the related destination finder.
     * @param targetFinder          the related target finder.
     * @param effect                the related effect.
     * @param color                 the color of the power up.
     * @param board                 the board of the game.

     */
    public PowerUp(PowerUpName powerUpName, DestinationFinder destinationFinder, TargetFinder targetFinder, Effect effect, Color color, Board board){

        this.name = powerUpName;
        this.holder = null;
        this.color = color;
        this.destinationFinder = destinationFinder;
        this.targetFinder = targetFinder;
        this.effect = effect;
        this.board = board;
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


    /**
     * Applies the effects of this power up to targets chosen.
     *
     * @param  playerList  the ArrayList of players being targeted
     * @param  destination the Square players are moved to, if relevant
     */
    public void applyEffects(List<Player> playerList, Square destination) throws NotAvailableAttributeException{

        if (!this.board.getPlayers().containsAll(playerList)) throw new IllegalArgumentException("The effects can be applied only on players on the board.");
        if (!(this.board.getMap().contains(destination) || destination == null)) throw new IllegalArgumentException("The players can be moved only in squares that belong to the board.");
        for(Player p : playerList){
            effect.apply(holder, p, destination);
        }
    }

    /**
     * Finds players that can be chosen as targets
     *
     * @return      an ArrayList containing sets of targets to be chosen, each saved as an ArrayList
     */
    public List<List<Player>> findTargets() throws NotAvailableAttributeException{
        return targetFinder.find(holder);
    }


    /**
     * Finds Squares that can be chosen as destination, if relevant
     *
     * @param  targets the ArrayList of already selected targets
     * @return      the set of possible destination Square objects
     */
    public List<Square> findDestinations(List<Player> targets) throws NotAvailableAttributeException{

        if (!this.board.getPlayers().containsAll(targets)) throw new IllegalArgumentException("Only on players on the board can be moved.");
        return destinationFinder.find(holder, targets);
    }


    /**
     *Establishes if this power up can be used according to the current board state
     *
     * @return      true if and only if  this power up cna be used
     */
    public boolean isAvailable() throws NotAvailableAttributeException{
        return !(findTargets().isEmpty())&&!(name==PowerUpName.TARGETING_SCOPE&&holder.getAmmoPack().equals(new AmmoPack(0,0,0)));
    }


    /**
     * Returns a string representing the powerup.
     *
     * @return      the description of the powerup.
     */
    @Override
    public String toString(){
        return  ClientModel.getEscapeCode(getColor().toStringLowerCase()) + name.toString() + RESET;
    }


    /**
     * Returns the cost of the powerup, in ammo.
     *
     * @return the cost of the powerup.
     */
    public AmmoPack getCost(){
        if (name == PowerUpName.TARGETING_SCOPE){
            if (color == RED)
                return new AmmoPack(1, 0, 0);
            else if (color == BLUE)
                return new AmmoPack(0, 1, 0);
            else if (color == YELLOW)
                return new AmmoPack(0, 0, 1);
        }
        return new AmmoPack(0,0,0);
    }
}