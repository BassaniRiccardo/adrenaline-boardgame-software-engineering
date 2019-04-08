package it.polimi.ingsw.model;

/**
 * Represents the quantity of ammos that every player own and the amount of ammo
 * involved in every operation involving ammo.
 * Contains as attributes the number of the 3 kinds of ammo.
 * Contains methods that add and subtract the ammo of an AmmoPack to another.
 * Every player owns an AmmoPack that represents his reserve of ammo.
 * Every player can have at maximum 3 ammo of the same color.
 *
 * @author  davidealde
 */

public class AmmoPack {

    private int redAmmo;
    private int blueAmmo;
    private int yellowAmmo;

    /**
     * Constructor.
     *
     * @param r     amount of red ammo.
     * @param b     amount of yellow ammo.
     * @param y     amount of blue ammo.
     */
    public AmmoPack(int r, int b, int y) {
        this.redAmmo = r;
        this.blueAmmo = b;
        this.yellowAmmo = y;
    }


    /**
     * Getters
     */
    public int getRedAmmo() { return redAmmo; }

    public int getBlueAmmo() { return blueAmmo; }

    public int getYellowAmmo() { return yellowAmmo; }


    /**
     * Adds the amount of ammo of an ammo pack.
     *
     * @param aP        the added ammo pack.
     */
    public void addAmmoPack(AmmoPack aP){

        if(this.redAmmo + aP.getRedAmmo() < 4) {
            this.redAmmo += aP.getRedAmmo();
        }else {
            this.redAmmo=3;
        }
        if(this.blueAmmo + aP.getBlueAmmo()<4) {
            this.blueAmmo += aP.getBlueAmmo();
        }else {
            this.blueAmmo=3;
        }
        if(this.yellowAmmo + aP.getYellowAmmo()<4) {
            this.yellowAmmo += aP.getYellowAmmo();
        }else {
            this.yellowAmmo=3;
        }

    }


    /**
     * Subtracts the amount of ammo of an ammo pack.
     *
     * @param aP        AmmoPack subtracted.
     */
    public void subAmmoPack(AmmoPack aP){

        this.redAmmo -= aP.redAmmo;
        this.blueAmmo -= aP.blueAmmo;
        this.yellowAmmo -= aP.yellowAmmo;

    }
}