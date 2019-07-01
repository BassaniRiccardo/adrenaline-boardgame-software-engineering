package it.polimi.ingsw.model.cards;

import static it.polimi.ingsw.model.cards.Color.*;

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
    public static final int MAX_AMMO_AMOUNT = 3;
    private static final int MIN_AMMO_AMOUNT = 0;

    /**
     * Constructor.
     *
     * @param r     amount of red ammo.
     * @param b     amount of yellow ammo.
     * @param y     amount of blue ammo.
     *
     * @throws      IllegalArgumentException if an illegal amount of ammo is given as parameter.
     */
    public AmmoPack(int r, int b, int y) {

        if (r < MIN_AMMO_AMOUNT || r > MAX_AMMO_AMOUNT || b < MIN_AMMO_AMOUNT || b > MAX_AMMO_AMOUNT || y < MIN_AMMO_AMOUNT || y > MAX_AMMO_AMOUNT ){
            throw new IllegalArgumentException("An ammo pack must contain between 0 and 3 ammo for each color.");
        }
        this.redAmmo = r;
        this.blueAmmo = b;
        this.yellowAmmo = y;
    }


    /**
     * Getter for redAmmo
     *
     * @return the number of red ammo.
     */
    public int getRedAmmo() { return redAmmo; }

    /**
     * Getter for blueAmmo
     *
     * @return the number of blue ammo.
     */
    public int getBlueAmmo() { return blueAmmo; }

    /**
     * Getter for yellowAmmo
     *
     * @return the number of yellow ammo.
     */
    public int getYellowAmmo() { return yellowAmmo; }


    /**
     * Adds the amount of ammo of an ammo pack.
     *
     * @param aP        the added ammo pack.
     */
    public void addAmmoPack(AmmoPack aP){
        this.redAmmo = Math.min(MAX_AMMO_AMOUNT, this.redAmmo + aP.getRedAmmo() ) ;
        this.blueAmmo = Math.min(MAX_AMMO_AMOUNT, this.blueAmmo + aP.getBlueAmmo() ) ;
        this.yellowAmmo = Math.min(MAX_AMMO_AMOUNT, this.yellowAmmo + aP.getYellowAmmo() ) ;
    }


    /**
     * Subtracts the amount of ammo of an ammo pack.
     *
     * @param aP        AmmoPack subtracted.
     * @throws          IllegalArgumentException if it is impossible to subtract the ammo.
     */
    public void subAmmoPack(AmmoPack aP) {

        if (aP.getBlueAmmo()>blueAmmo || aP.getRedAmmo()>redAmmo || aP.getYellowAmmo()>yellowAmmo) {
            throw new IllegalArgumentException("Not enough ammo to execute the subtraction");
        }
        this.redAmmo -= aP.redAmmo;
        this.blueAmmo -= aP.blueAmmo;
        this.yellowAmmo -= aP.yellowAmmo;

    }


    /**
     * Returns the ammo pack needed to pay this ammo pack when a specified ammo pack is available.
     *
     * @param available     the available ammo pack.
     * @return              the needed ammo pack.
     */
    public AmmoPack getNeededAmmo(AmmoPack available){
        int neededRed = Math.max(0, this.getRedAmmo() - available.getRedAmmo());
        int neededBlue = Math.max(0, this.getBlueAmmo() - available.getBlueAmmo());
        int neededYellow = Math.max(0, this.getYellowAmmo() - available.getYellowAmmo());
        return new AmmoPack(neededRed, neededBlue, neededYellow);
    }


    /**
     * Returns whether the ammo pack does not contain ammo.
     *
     * @return  true if the ammo pack has 0 red ammo, 0 blue ammo and 0 yellow ammo.
     *          false otherwise
     */
    public boolean isEmpty(){
        return (redAmmo==0 && blueAmmo == 0 && yellowAmmo == 0);
    }


    /**
     * Subtract form an ammo pack an ammo of the specified color.
     *
     * @param color     the color of the ammo to subtract.
     */
    public void subAmmo(Color color){
        if (color.equals(RED))
            this.subAmmoPack(new AmmoPack(1,0,0));
        if (color.equals(BLUE))
            this.subAmmoPack(new AmmoPack(0,1,0));
        if (color.equals(YELLOW))
            this.subAmmoPack(new AmmoPack(0,0,1));
    }


    /**
     * Returns a string representing the ammo pack.
     *
     * @return      the description of the ammopack.
     */
    @Override
    public String toString() {
        return "r" + redAmmo + " b" + blueAmmo + " y" + yellowAmmo;
    }
}