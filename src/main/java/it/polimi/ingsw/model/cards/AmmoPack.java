package it.polimi.ingsw.model.cards;

/**
 * Represents a package of ammo of the three colors.
 * Contains as attributes the number of the 3 kinds of ammo.
 * Contains methods that addList and subtract the ammo of an AmmoPack to another.
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
     *
     * @throws      IllegalArgumentException
     */
    public AmmoPack(int r, int b, int y) {

        if (r<0 || r>3 || b<0 || b>3 || y<0 || y>3 ){
            throw new IllegalArgumentException("An ammo pack must contain between 0 and 3 ammo for each color.");
        }
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
     * @throws          IllegalArgumentException
     */
    public void subAmmoPack(AmmoPack aP) {

        if (aP.getBlueAmmo()>blueAmmo || aP.getRedAmmo()>redAmmo || aP.getYellowAmmo()>yellowAmmo) {
            throw new IllegalArgumentException("Not enough ammo to execute the subtraction");
        }
        this.redAmmo -= aP.redAmmo;
        this.blueAmmo -= aP.blueAmmo;
        this.yellowAmmo -= aP.yellowAmmo;

    }
}