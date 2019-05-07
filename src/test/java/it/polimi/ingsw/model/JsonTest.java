package it.polimi.ingsw.model;

import it.polimi.ingsw.model.board.Json;
import it.polimi.ingsw.model.cards.FireMode;
import it.polimi.ingsw.model.cards.Weapon;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Tests the methods of the class Json
 */
public class JsonTest {

    /**
     * Tests the method getColor()
     */
    @Test
    public void getColor() {
        Json json = new Json();
        String out;
        out=json.getColor(Weapon.WeaponName.LOCK_RIFLE);
        assertEquals("blue",out);
    }

    /**
     * Tests the method getCostRed()
     */
    @Test
    public void getFullCostRed() {
        Json json = new Json();
        int out;
        out=Integer.parseInt(json.getFullCostRed(Weapon.WeaponName.MACHINE_GUN));
        assertEquals(1,out);
    }

    /**
     * Tests the method getCostBlue()
     */
    @Test
    public void getFullCostBlue() {
        Json json = new Json();
        int out;
        out=Integer.parseInt(json.getFullCostBlue(Weapon.WeaponName.LOCK_RIFLE));
        assertEquals(2,out);
    }

    /**
     * Tests the method getCostYellow()
     */
    @Test
    public void getFullCostYellow() {
        Json json = new Json();
        int out;
        out=Integer.parseInt(json.getFullCostYellow(Weapon.WeaponName.MACHINE_GUN));
        assertEquals(0,out);
    }

    /**
     * Tests the method getNameList()
     */
    @Test
    public void getNameList() {
        Json json = new Json();
        int out;
        out=Integer.parseInt(json.getNameList(Weapon.WeaponName.THOR));
        assertEquals(4,out);
    }

    /**
     * Tests the method getTargetNumber()
     */
    @Test
    public void getTargetNumber() {
        Json json = new Json();
        int out;
        out=Integer.parseInt(json.getTargetNumber(Weapon.WeaponName.THOR, FireMode.FireModeName.OPTION1));
        assertEquals(1,out);
    }

    /**
     * Tests the method getFireModeCostRed()
     */
    @Test
    public void getFireModeCostRed() {
        Json json = new Json();
        int out;
        out=Integer.parseInt(json.getFireModeCostRed(Weapon.WeaponName.THOR, FireMode.FireModeName.OPTION1));
        assertEquals(0,out);
    }

    /**
     * Tests the method getFireModeCostBlue()
     */
    @Test
    public void getFireModeCostBlue() {
        Json json = new Json();
        int out;
        out=Integer.parseInt(json.getFireModeCostBlue(Weapon.WeaponName.THOR, FireMode.FireModeName.OPTION1));
        assertEquals(1,out);
    }

    /**
     * Tests the method getFireModeCostYellow()
     */
    @Test
    public void getFireModeCostYellow() {
        Json json = new Json();
        int out;
        out=Integer.parseInt(json.getFireModeCostYellow(Weapon.WeaponName.THOR, FireMode.FireModeName.OPTION1));
        assertEquals(0,out);
    }

    /**
     * Tests the method getMove()
     */
    @Test
    public void getMove() {
        Json json = new Json();
        int out;
        out=Integer.parseInt(json.getMove(Weapon.WeaponName.VORTEX_CANNON, FireMode.FireModeName.MAIN));
        assertEquals(1,out);
    }

    /**
     * Tests the method getDmg()
     */
    @Test
    public void getDmg() {
        Json json = new Json();
        int out;
        out=Integer.parseInt(json.getDmg(Weapon.WeaponName.FURNACE, FireMode.FireModeName.SECONDARY));
        assertEquals(1,out);
    }

    /**
     * Tests the method getMark()
     */
    @Test
    public void getMark() {
        Json json = new Json();
        int out;
        out=Integer.parseInt(json.getMark(Weapon.WeaponName.FURNACE, FireMode.FireModeName.SECONDARY));
        assertEquals(1,out);
    }

    /**
     * Tests the method getEff()
     */
    @Test
    public void getEff() {
        Json json = new Json();
        String out;
        out=json.getEff(Weapon.WeaponName.VORTEX_CANNON, FireMode.FireModeName.OPTION1);
        assertEquals("moveDmg",out);
    }

    /**
     * Tests the method getWhere()
     */
    @Test
    public void getWhere() {
        Json json = new Json();
        String out;
        out=json.getWhere(Weapon.WeaponName.VORTEX_CANNON, FireMode.FireModeName.OPTION1);
        assertEquals("vortex2",out);
    }

    /**
     * Tests the method getMoveType()
     */
    @Test
    public void getMoveType() {
        Json json = new Json();
        String out;
        out=json.getMoveType(Weapon.WeaponName.VORTEX_CANNON, FireMode.FireModeName.OPTION1);
        assertEquals("vortex2",out);
    }
}