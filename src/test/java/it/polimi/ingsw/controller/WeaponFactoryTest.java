package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.exceptions.*;
import org.junit.Test;

import static org.junit.Assert.*;

//This class needs more in-depth testing once it has been fully implemented: only the first weapon has been tested so far
public class WeaponFactoryTest {

    /**
     * Creates the first weapon and checks that it is initialized correctly
     */
    @Test
    public void createWeapon() throws NoMoreCardsException, UnacceptableItemNumberException {
        Board b = BoardConfigurer.simulateScenario();
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon w = weaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE);
        assertTrue(w.getWeaponName() == Weapon.WeaponName.LOCK_RIFLE);

        assertTrue(w.getFullCost().getRedAmmo()==0);
        assertTrue(w.getFullCost().getBlueAmmo()==2);
        assertTrue(w.getFullCost().getYellowAmmo()==0);
        assertEquals(false, w.getFireModeList().isEmpty());
        assertTrue(w.getFireModeList().size()==2);
        assertTrue(w.getReducedCost().getRedAmmo()==0);
        assertTrue(w.getReducedCost().getBlueAmmo()==1);
        assertTrue(w.getReducedCost().getYellowAmmo()==0);
        try {
            w.getHolder();
        }catch (NotAvailableAttributeException e){}
        assertFalse(w.isLoaded());
        assertTrue(w.getMainTargets().isEmpty());
        assertTrue(w.getOptionalTargets().isEmpty());
        try{
            w.getHolder();
        }catch (NotAvailableAttributeException e){}
        FireMode f = w.getFireModeList().get(0);
        assertTrue(f.getName() == FireMode.FireModeName.MAIN);
        assertTrue(f.getMaxTargets()==1);
        assertTrue(f.getCost().getRedAmmo() == 0);
        assertTrue(f.getCost().getBlueAmmo() == 0);
        assertTrue(f.getCost().getYellowAmmo() == 0);
        assertTrue(f.getDestinationFinder()!=null&&f.getTargetFinder()!=null&&f.getEffect()!=null);

        f = w.getFireModeList().get(1);
        assertTrue(f.getName() == FireMode.FireModeName.OPTION1);
        assertTrue(f.getMaxTargets()== 1);
        assertTrue(f.getCost().getRedAmmo() == 1);
        assertTrue(f.getCost().getBlueAmmo() == 0);
        assertTrue(f.getCost().getYellowAmmo() == 0);
        assertTrue(f.getDestinationFinder()!=null&&f.getTargetFinder()!=null&&f.getEffect()!=null);
    }

    /**
     * Tests that the color of a particular weapon is correct
     */
    @Test
    public void getColor()  throws NoMoreCardsException, UnacceptableItemNumberException {
        Board b = BoardConfigurer.simulateScenario();
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon w = weaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE);
        assertTrue(Color.BLUE==w.getColor());
    }

    /**
     * Tests that the full cost of a particular weapon is correct
     */
    @Test
    public void getFullCost() throws NoMoreCardsException, UnacceptableItemNumberException {
        Board b = BoardConfigurer.simulateScenario();
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon w = weaponFactory.createWeapon(Weapon.WeaponName.RAILGUN);
        assertEquals(0,w.getFullCost().getRedAmmo());
        assertEquals(1,w.getFullCost().getBlueAmmo());
        assertEquals(2,w.getFullCost().getYellowAmmo());
    }

    /**
     * Tests that the reduced cost of a particular weapon is correct
     */
    @Test
    public void getReducedCost() throws NoMoreCardsException, UnacceptableItemNumberException {
        Board b = BoardConfigurer.simulateScenario();
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon w = weaponFactory.createWeapon(Weapon.WeaponName.RAILGUN);
        assertEquals(0,w.getReducedCost().getRedAmmo());
        assertEquals(1,w.getReducedCost().getBlueAmmo());
        assertEquals(1,w.getReducedCost().getYellowAmmo());
    }

    /**
     * Tests that the name list of a particular weapon is correct
     */
    @Test
    public void getNameList() throws NoMoreCardsException, UnacceptableItemNumberException {
        Board b = BoardConfigurer.simulateScenario();
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon w = weaponFactory.createWeapon(Weapon.WeaponName.RAILGUN);
        FireMode f = w.getFireModeList().get(0);
        assertTrue(f.getName() == FireMode.FireModeName.MAIN);
        f = w.getFireModeList().get(1);
        assertTrue(f.getName() == FireMode.FireModeName.SECONDARY);
        assertEquals(2,w.getFireModeList().size());
    }

    /**
     * Tests that the target number of a particular weapon is correct
     */
    @Test
    public void getTargetNumber() throws NoMoreCardsException, UnacceptableItemNumberException {
        Board b = BoardConfigurer.simulateScenario();
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon w = weaponFactory.createWeapon(Weapon.WeaponName.RAILGUN);
        int tN = weaponFactory.getTargetNumber(w.getWeaponName(), FireMode.FireModeName.SECONDARY);
        assertEquals(2,tN);
    }

    /**
     * Tests that the fire mode cost of a particular weapon is correct
     */
    @Test
    public void getFireModeCost() throws NoMoreCardsException, UnacceptableItemNumberException {
        Board b = BoardConfigurer.simulateScenario();
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon w = weaponFactory.createWeapon(Weapon.WeaponName.SHOCKWAVE);
        AmmoPack ammoPack = weaponFactory.getFireModeCost(w.getWeaponName(), FireMode.FireModeName.SECONDARY);
        assertEquals(1,ammoPack.getYellowAmmo());
        assertEquals(0,ammoPack.getRedAmmo());
        assertEquals(0,ammoPack.getBlueAmmo());
    }
}