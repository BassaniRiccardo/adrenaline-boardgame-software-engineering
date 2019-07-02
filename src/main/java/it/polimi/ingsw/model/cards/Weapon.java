package it.polimi.ingsw.model.cards;
import it.polimi.ingsw.model.Updater;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Player;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;
import it.polimi.ingsw.model.exceptions.WrongTimeException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class modeling a weapon card.
 *
 * @author  marcobaga
 */

public class Weapon implements Card {

    /**
     * An enumeration for the powerups names.
     */
    public enum WeaponName{

        LOCK_RIFLE, MACHINE_GUN, THOR, PLASMA_GUN, WHISPER, ELECTROSCYTHE, TRACTOR_BEAM,
        VORTEX_CANNON, FURNACE,HEATSEEKER, HELLION, FLAMETHROWER, GRENADE_LAUNCHER, ROCKET_LAUNCHER,
        RAILGUN, CYBERBLADE, ZX2, SHOTGUN, POWER_GLOVE, SHOCKWAVE, SLEDGEHAMMER;

        /**
         * Returns a string representing the weapon name.
         *
         * @return a string representing the weapon name.
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

    private final WeaponName weaponName;
    private boolean loaded;
    private final Color color;
    private final AmmoPack fullCost;
    private final AmmoPack reducedCost;
    private Player holder;
    private final List<FireMode> fireModeList;
    private List<Player> mainTargets;
    private List<Player> optionalTargets;
    private Board board;

    private static final Logger LOGGER = Logger.getLogger("serverLogger");


    /**
     * Constructs a weapon with the weapon name, the weapon color, the cost to reload the weapon after it is been used,
     * the cost to reload the weapon after it is been collected, the list of the weapon firemodes and a reference to the game board..
     *
     * @param weaponName        the weapon name.
     * @param color             the weapon color.
     * @param fullCost          the cost to reload the weapon after it is been used.
     * @param reducedCost       the cost to reload the weapon after it is been collected.
     * @param fireModeList      the list of firemodes.
     * @param board `           the board of the game.
     */
    public Weapon(WeaponName weaponName, Color color, AmmoPack fullCost, AmmoPack reducedCost, List<FireMode> fireModeList, Board board) {

        this.weaponName = weaponName;
        this.loaded = false;
        this.color = color;
        this.fullCost = fullCost;
        this.reducedCost = reducedCost;
        this.holder = null;
        this.fireModeList = fireModeList;
        this.mainTargets = new ArrayList<>();
        this.optionalTargets = new ArrayList<>();
        this.board = board;

    }


    /*
     * Getters
     */

    public WeaponName getWeaponName() { return weaponName; }

    public boolean isLoaded() { return loaded; }

    public Color getColor(){ return this.color; }

    public AmmoPack getFullCost() {
        return fullCost;
    }

    public AmmoPack getReducedCost() {
        return reducedCost;
    }

    public Player getHolder() throws NotAvailableAttributeException {
        if (holder == null) throw new NotAvailableAttributeException("This weapon does not have an holder.");
        return holder;
    }

    public List<FireMode> getFireModeList() { return fireModeList; }

    public List<Player> getMainTargets() {
        return mainTargets;
    }

    public List<Player> getOptionalTargets() {
        return optionalTargets;
    }


    /*
     * Setters
     */

    public void setLoaded(boolean loaded) { this.loaded = loaded;
        board.addToUpdateQueue(Updater.get(Updater.RELOAD_UPD, this, loaded));
    }

    public void setHolder(Player holder) {
        this.holder = holder;
    }

    public void removeHolder(){
        this.holder = null;
    }


    /**
     *Lists the firemodes which can be used since they can hit a target
     *
     * @return      the list of available firemodes
     * @throws NotAvailableAttributeException if the weapon does not have an holder
     */
    public List<FireMode> listAvailableFireModes() throws NotAvailableAttributeException{

        List<FireMode> available = new ArrayList<>();
        for (FireMode f : fireModeList) {
            if (f.isAvailable() && (holder.canPay(f.getCost()))) {
                available.add(f);
            }
        }
        return available;

    }


    /**
     *Checks if this weapon can fire (is loaded and valid targets)
     *
     * @return      true if shooting is possible, else false
     * @throws NotAvailableAttributeException if the weapon does not have an holder
     */
    public boolean canFire(){
        try {
            return !listAvailableFireModes().isEmpty();
        }catch(NotAvailableAttributeException ex){
            LOGGER.log(Level.SEVERE, "NotAvailableAttribute thrown while checking if it is possible to shoot");
            return false;
        }
    }


    /**
     *Reloads the current weapon.
     *
     * @throws WrongTimeException if the weapon is already loaded.
     */
    public void reload() throws WrongTimeException {

        if (loaded){ throw new WrongTimeException("A weapon can be reloaded only if unloaded.");}
        this.setLoaded(true);
    }


    /**
     * Returns a string representing the weapon.
     *
     * @return      the description of the weapon.
     */
    @Override
    public String toString(){
        return weaponName.toString();
    }

}
