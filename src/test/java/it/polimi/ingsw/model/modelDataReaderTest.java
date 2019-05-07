package it.polimi.ingsw.model;

import it.polimi.ingsw.model.board.modelDataReader;
import it.polimi.ingsw.model.cards.FireMode;
import it.polimi.ingsw.model.cards.Weapon;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Tests the methods of the class modelDataReader
 */
public class modelDataReaderTest {

    /**
     * Tests the method getColor()
     */
    @Test
    public void getColor() {
        modelDataReader modelDataReader = new modelDataReader();
        String out;
        out= modelDataReader.getColor(Weapon.WeaponName.LOCK_RIFLE);
        assertEquals("blue",out);
    }

    /**
     * Tests the method getCostRed()
     */
    @Test
    public void getFullCostRed() {
        modelDataReader modelDataReader = new modelDataReader();
        int out;
        out=Integer.parseInt(modelDataReader.getFullCostRed(Weapon.WeaponName.MACHINE_GUN));
        assertEquals(1,out);
    }

    /**
     * Tests the method getCostBlue()
     */
    @Test
    public void getFullCostBlue() {
        modelDataReader modelDataReader = new modelDataReader();
        int out;
        out=Integer.parseInt(modelDataReader.getFullCostBlue(Weapon.WeaponName.LOCK_RIFLE));
        assertEquals(2,out);
    }

    /**
     * Tests the method getCostYellow()
     */
    @Test
    public void getFullCostYellow() {
        modelDataReader modelDataReader = new modelDataReader();
        int out;
        out=Integer.parseInt(modelDataReader.getFullCostYellow(Weapon.WeaponName.MACHINE_GUN));
        assertEquals(0,out);
    }

    /**
     * Tests the method getNameList()
     */
    @Test
    public void getNameList() {
        modelDataReader modelDataReader = new modelDataReader();
        int out;
        out=Integer.parseInt(modelDataReader.getNameList(Weapon.WeaponName.THOR));
        assertEquals(4,out);
    }

    /**
     * Tests the method getTargetNumber()
     */
    @Test
    public void getTargetNumber() {
        modelDataReader modelDataReader = new modelDataReader();
        int out;
        out=Integer.parseInt(modelDataReader.getTargetNumber(Weapon.WeaponName.THOR, FireMode.FireModeName.OPTION1));
        assertEquals(1,out);
    }

    /**
     * Tests the method getFireModeCostRed()
     */
    @Test
    public void getFireModeCostRed() {
        modelDataReader modelDataReader = new modelDataReader();
        int out;
        out=Integer.parseInt(modelDataReader.getFireModeCostRed(Weapon.WeaponName.THOR, FireMode.FireModeName.OPTION1));
        assertEquals(0,out);
    }

    /**
     * Tests the method getFireModeCostBlue()
     */
    @Test
    public void getFireModeCostBlue() {
        modelDataReader modelDataReader = new modelDataReader();
        int out;
        out=Integer.parseInt(modelDataReader.getFireModeCostBlue(Weapon.WeaponName.THOR, FireMode.FireModeName.OPTION1));
        assertEquals(1,out);
    }

    /**
     * Tests the method getFireModeCostYellow()
     */
    @Test
    public void getFireModeCostYellow() {
        modelDataReader modelDataReader = new modelDataReader();
        int out;
        out=Integer.parseInt(modelDataReader.getFireModeCostYellow(Weapon.WeaponName.THOR, FireMode.FireModeName.OPTION1));
        assertEquals(0,out);
    }

    /**
     * Tests the method getMove()
     */
    @Test
    public void getMove() {
        modelDataReader modelDataReader = new modelDataReader();
        int out;
        out=Integer.parseInt(modelDataReader.getMove(Weapon.WeaponName.VORTEX_CANNON, FireMode.FireModeName.MAIN));
        assertEquals(1,out);
    }

    /**
     * Tests the method getDmg()
     */
    @Test
    public void getDmg() {
        modelDataReader modelDataReader = new modelDataReader();
        int out;
        out=Integer.parseInt(modelDataReader.getDmg(Weapon.WeaponName.FURNACE, FireMode.FireModeName.SECONDARY));
        assertEquals(1,out);
    }

    /**
     * Tests the method getMark()
     */
    @Test
    public void getMark() {
        modelDataReader modelDataReader = new modelDataReader();
        int out;
        out=Integer.parseInt(modelDataReader.getMark(Weapon.WeaponName.FURNACE, FireMode.FireModeName.SECONDARY));
        assertEquals(1,out);
    }

    /**
     * Tests the method getEff()
     */
    @Test
    public void getEff() {
        modelDataReader modelDataReader = new modelDataReader();
        String out;
        out= modelDataReader.getEff(Weapon.WeaponName.VORTEX_CANNON, FireMode.FireModeName.OPTION1);
        assertEquals("moveDmg",out);
    }

    /**
     * Tests the method getWhere()
     */
    @Test
    public void getWhere() {
        modelDataReader modelDataReader = new modelDataReader();
        String out;
        out= modelDataReader.getWhere(Weapon.WeaponName.VORTEX_CANNON, FireMode.FireModeName.OPTION1);
        assertEquals("vortex2",out);
    }

    /**
     * Tests the method getMoveType()
     */
    @Test
    public void getMoveType() {
        modelDataReader modelDataReader = new modelDataReader();
        String out;
        out= modelDataReader.getMoveType(Weapon.WeaponName.VORTEX_CANNON, FireMode.FireModeName.OPTION1);
        assertEquals("vortex2",out);
    }
}