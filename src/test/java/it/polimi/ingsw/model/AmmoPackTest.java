package it.polimi.ingsw.model;

import org.junit.Test;

import static org.junit.Assert.*;
/**
 * Tests all methods of the class AmmoPack, covering all the instructions.
 */

public class AmmoPackTest {


    /**
     * Tests if an exception is thrown when an AmmoPack tries to be constructed with bad parameters.
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructAmmoPack() {
        AmmoPack ammoPack = new AmmoPack(3,5,78);
    }

    /**
     * Tests addAmmoPack() without reaching the maximum of the ammo.
     */
    @Test
    public void addAmmoPack() {

        //instantiates 2 ammo packs
        AmmoPack ammoPack1=new AmmoPack(0,0,0);
        AmmoPack ammoPack2=new AmmoPack(1,2,3);
        AmmoPack ammoPack3=new AmmoPack(0,1,0);

        //calls addAmmoPack
        ammoPack1.addAmmoPack(ammoPack2);

        //checks that ammoPack1 contains the ammo of ammoPack2
        assertTrue(1==ammoPack1.getRedAmmo()&&
                2==ammoPack1.getBlueAmmo()&&
                3==ammoPack1.getYellowAmmo());

        //calls AddAmmoPack
        ammoPack1.addAmmoPack(ammoPack3);

        //checks that ammoPack1 contains the right amount of ammo
        assertTrue(1==ammoPack1.getRedAmmo()&&
                3==ammoPack1.getBlueAmmo()&&
                3==ammoPack1.getYellowAmmo());

    }

    /**
     * Tests addAmmoPack() reaching the maximum of the ammo.
     */
    @Test()
    public void addAmmoPackMaximum() {

        //instantiates 2 ammo packs
        AmmoPack ammoPack1=new AmmoPack(0,0,0);
        AmmoPack ammoPack2=new AmmoPack(1,2,2);
        AmmoPack ammoPack3=new AmmoPack(1,1,3);

        //calls addAmmoPack
        ammoPack1.addAmmoPack(ammoPack2);

        //checks that ammoPack1 contains the right amount of ammo
        assertEquals(1,ammoPack1.getRedAmmo());
        assertEquals(2,ammoPack1.getBlueAmmo());
        assertEquals(2,ammoPack1.getYellowAmmo());

        //calls AddAmmoPack
        ammoPack1.addAmmoPack(ammoPack3);

        //checks that ammoPack1 contains the right amounts of ammo
        assertTrue(2==ammoPack1.getRedAmmo()&&
                3==ammoPack1.getBlueAmmo()&&
                3==ammoPack1.getYellowAmmo());

    }

    /**
     * Tests subAmmoPack() without going below 0 ammo.
     */
    @Test
    public void subAmmoPack() {
        //instantiates 2 ammo packs
        AmmoPack ammoPack1=new AmmoPack(1,2,3);
        AmmoPack ammoPack2=new AmmoPack(0,1,3);

        //calls subAmmoPack
        ammoPack1.subAmmoPack(ammoPack2);

        //checks that ammoPack1 contains the right amount of ammo
        assertEquals(1, ammoPack1.getRedAmmo());
        assertEquals(1, ammoPack1.getBlueAmmo());
        assertEquals(0, ammoPack1.getYellowAmmo());

    }


    /**
     * Tests subAmmoPack() when an exception should be thrown since there are no enough ammo to subtract.
     */
    @Test(expected = IllegalArgumentException.class)
    public void subAmmoPackUnderZero() {
        //instantiates 2 ammo packs
        AmmoPack ammoPack1=new AmmoPack(1,2,3);
        AmmoPack ammoPack2=new AmmoPack(3,1,3);

        //calls subAmmoPack
        ammoPack1.subAmmoPack(ammoPack2);

        //checks that ammoPack1 contains initial amount of ammo
        assertEquals(1, ammoPack1.getRedAmmo());
        assertEquals(2, ammoPack1.getBlueAmmo());
        assertEquals(3, ammoPack1.getYellowAmmo());

    }


}