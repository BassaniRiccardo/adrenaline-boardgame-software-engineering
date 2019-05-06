//come utilizzare final?

package it.polimi.ingsw.model;
import java.util.ArrayList;
import java.util.List;

/**
 * Class modeling a weapon card.
 *
 * @author  marcobaga
 */

public class Weapon implements Card {

    public enum WeaponName{

        LOCK_RIFLE, MACHINE_GUN, THOR, PLASMA_GUN, WHISPER, ELECTROSCYTHE, TRACTOR_BEAM,
        VORTEX_CANNON, FURNACE,HEATSEEKER, HELLION, FLAMETHROWER, GRENADE_LAUNCHER, ROCKET_LAUNCHER,
        RAILGUN, CYBERBLADE, ZX2, SHOTGUN, POWER_GLOVE, SHOCKWAVE, SLEDGEHAMMER;

        @Override
        public String toString(){
            return (this.name().substring(0,1) + this.name().toLowerCase().substring(1)).replace('_', ' ');
        }

    }

    private final WeaponName weaponName;
    private boolean loaded;
    private final Color color;
    private final AmmoPack fullCost;
    private final AmmoPack reducedCost;
    private Player holder;
    private final List<FireMode> fireModeList;
    private FireMode currentFireMode;
    private List<Player> mainTargets;
    private List<Player> optionalTargets;
    private Board board;


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
        this.currentFireMode = null;
        this.mainTargets = new ArrayList<>();
        this.optionalTargets = new ArrayList<>();
        this.board = board;

    }


    /**
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

    public FireMode getCurrentFireMode() throws NotAvailableAttributeException {
        if (currentFireMode == null) throw new NotAvailableAttributeException("This weapon does not have a set firemode.");

        return currentFireMode;
    }

    public List<Player> getMainTargets() {
        return mainTargets;
    }

    public List<Player> getOptionalTargets() {
        return optionalTargets;
    }


    /**
     * Setters
     */

    public void setLoaded(boolean loaded) { this.loaded = loaded; }

    public void setHolder(Player holder) {
        if (!this.board.getPlayers().contains(holder)) throw new IllegalArgumentException("Only a player on the map can hold a weapon.");
        this.holder = holder;
    }

    public void removeHolder(){
        this.holder = null;
    }

    public void setCurrentFireMode(FireMode currentFireMode) {
        this.currentFireMode = currentFireMode;
    }


    public void setMainTargets(List<Player> mainTargets) {
        if(!this.board.getPlayers().containsAll(mainTargets)) throw new IllegalArgumentException("The targets must belong to the board.");
        this.mainTargets = mainTargets;
    }

    public void setOptionalTargets(List<Player> optionalTargets) {
        if(!this.board.getPlayers().containsAll(optionalTargets)) throw new IllegalArgumentException("The targets must belong to the board.");
        this.optionalTargets = optionalTargets;
    }


    /**
     *Lists the firemodes which can be used since they can hit a target
     *
     * @return      the list of available firemodes
     */
    public List<FireMode> listAvailableFireModes() throws NotAvailableAttributeException{

        List<FireMode> available = new ArrayList<>();
        for (FireMode f : fireModeList) {
            if (f.isAvailable() && (holder.hasEnoughAmmo(f.getCost()))) {
                available.add(f);
            }
        }
        return available;

    }


    /**
     *Reloads the current weapon.
     */
    public void reload() throws NotAvailableAttributeException, WrongTimeException {

        if (loaded){ throw new WrongTimeException("A weapon can be reloaded only if unloaded.");}
        this.loaded = true;
        this.getHolder().getAmmoPack().subAmmoPack(this.fullCost);

    }

    @Override
    public String toString(){
        return weaponName.toString();
    }

}
