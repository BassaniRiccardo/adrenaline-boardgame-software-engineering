package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.AmmoPack;
import org.junit.Test;

import static it.polimi.ingsw.model.cards.Color.*;
import static org.junit.Assert.*;
/**
 * Tests all methods of the class AmmoPack.
 *
 * @author davidealde, BassaniRiccardo
 */

public class AmmoPackTest {


    /**
     * Tests if an exception is thrown when an AmmoPack tries to be constructed with bad parameters.
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructAmmoPack() {
        new AmmoPack(3,5,78);
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

        //calls addAmmoPack()
        ammoPack1.addAmmoPack(ammoPack2);

        //checks that ammoPack1 contains the right amount of ammo
        assertEquals(1,ammoPack1.getRedAmmo());
        assertEquals(2,ammoPack1.getBlueAmmo());
        assertEquals(2,ammoPack1.getYellowAmmo());

        //calls AddAmmoPack
        ammoPack1.addAmmoPack(ammoPack3);

        //checks that ammoPack1 contains the right amount of ammo
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


    /**
     * Tests subAmmo().
     */
    @Test()
    public void subAmmo() {

        AmmoPack ammoPack = new AmmoPack(1,2,3);

        ammoPack.subAmmo(RED);
        assertEquals (0, ammoPack.getRedAmmo());
        assertEquals (2, ammoPack.getBlueAmmo());
        assertEquals (3, ammoPack.getYellowAmmo());


        ammoPack.subAmmo(BLUE);
        assertEquals (0, ammoPack.getRedAmmo());
        assertEquals (1, ammoPack.getBlueAmmo());
        assertEquals (3, ammoPack.getYellowAmmo());

        ammoPack.subAmmo(YELLOW);
        assertEquals (0, ammoPack.getRedAmmo());
        assertEquals (1, ammoPack.getBlueAmmo());
        assertEquals (2, ammoPack.getYellowAmmo());
    }


    /**
     * Tests isEmpty().
     */
    @Test()
    public void isEmpty() {

        AmmoPack ammoPack1 = new AmmoPack(1,2,3);
        AmmoPack ammoPack2 = new AmmoPack(0,0,0);

        assertFalse (ammoPack1.isEmpty());
        assertTrue (ammoPack2.isEmpty());

    }


    /**
     * Tests getNeededAmmo().
     */
    @Test()
    public void getNeededAmmo() {

        AmmoPack available = new AmmoPack(1,2,3);
        AmmoPack toPay1 = new AmmoPack(3,0,2);
        AmmoPack toPay2 = new AmmoPack(0,0,0);
        AmmoPack toPay3 = new AmmoPack(3,3,3);

        assertEquals (2, toPay1.getNeededAmmo(available).getRedAmmo());
        assertEquals (0, toPay1.getNeededAmmo(available).getBlueAmmo());
        assertEquals (0, toPay1.getNeededAmmo(available).getYellowAmmo());

        assertEquals (0, toPay2.getNeededAmmo(available).getRedAmmo());
        assertEquals (0, toPay2.getNeededAmmo(available).getBlueAmmo());
        assertEquals (0, toPay2.getNeededAmmo(available).getYellowAmmo());

        assertEquals (3, toPay3.getNeededAmmo(toPay2).getRedAmmo());
        assertEquals (3, toPay3.getNeededAmmo(toPay2).getBlueAmmo());
        assertEquals (3, toPay3.getNeededAmmo(toPay2).getYellowAmmo());
    }



}