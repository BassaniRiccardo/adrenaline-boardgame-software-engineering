package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests all methods of the class AmmoPack, covering all the instructions.
 */

class AmmoPackTest {

    /**
     * Tests addAmmoPack() without reaching the maximum of the ammos
     */
    @Test
    void addAmmoPack() {

        //instantiates 2 ammpPacks
        AmmoPack ammoPack1=new AmmoPack(0,0,0);
        AmmoPack ammoPack2=new AmmoPack(1,2,3);
        AmmoPack ammoPack3=new AmmoPack(0,1,0);

        //calls addAmmoPack
        ammoPack1.addAmmoPack(ammoPack2);

        //checks that ammoPack1 contains the ammos of ammoPack2
        assertTrue(1==ammoPack1.getRedAmmo()&&
                2==ammoPack1.getBlueAmmo()&&
                3==ammoPack1.getYellowAmmo());

        //calls AddAmmoPack
        ammoPack1.addAmmoPack(ammoPack3);

        //checks that ammoPack1 contains the right amount of ammos
        assertTrue(1==ammoPack1.getRedAmmo()&&
                3==ammoPack1.getBlueAmmo()&&
                3==ammoPack1.getYellowAmmo());

    }

    /**
     * Tests addAmmoPack() reaching the maximum of the ammos
     */
    @Test
    void addAmmoPackMaximum() {

        //instantiates 2 ammpPacks
        AmmoPack ammoPack1=new AmmoPack(0,0,0);
        AmmoPack ammoPack2=new AmmoPack(1,4,5);
        AmmoPack ammoPack3=new AmmoPack(1,0,3);

        //calls addAmmoPack
        ammoPack1.addAmmoPack(ammoPack2);

        //checks that ammoPack1 contains the right amount of ammos
        assertEquals(1,ammoPack1.getRedAmmo());
        assertEquals(3,ammoPack1.getBlueAmmo());
        assertEquals(3,ammoPack1.getYellowAmmo());

        //calls AddAmmoPack
        ammoPack1.addAmmoPack(ammoPack3);

        //checks that ammoPack1 contains the right amounts of ammos
        assertTrue(2==ammoPack1.getRedAmmo()&&
                3==ammoPack1.getBlueAmmo()&&
                3==ammoPack1.getYellowAmmo());

    }

    /**
     * Tests subAmmoPack() without going below 0 ammos
     */
    @Test
    void subAmmoPack() {
        //instantiates 2 ammpPacks
        AmmoPack ammoPack1=new AmmoPack(1,2,3);
        AmmoPack ammoPack2=new AmmoPack(0,1,3);

        //calls subAmmoPack
        ammoPack1.subAmmoPack(ammoPack2);

        //checks that ammoPack1 contains the right amount of ammos
        assertTrue(1==ammoPack1.getRedAmmo()&&
                1==ammoPack1.getBlueAmmo()&&
                0==ammoPack1.getYellowAmmo());

    }
}