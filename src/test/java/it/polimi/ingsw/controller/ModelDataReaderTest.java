package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.cards.AmmoPack;
import it.polimi.ingsw.model.cards.Color;
import it.polimi.ingsw.model.cards.FireMode;
import it.polimi.ingsw.model.cards.Weapon;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Tests the methods of the class ModelDataReader
 */
public class ModelDataReaderTest {

    @Test
    public void getIntBC() {
        ModelDataReader modelDataReader = new ModelDataReader();
        assertEquals(4,modelDataReader.getIntBC("columnsNumber"));
    }

    @Test
    public void getIntBC1() {
        ModelDataReader modelDataReader = new ModelDataReader();
        assertEquals(3,modelDataReader.getIntBC("wSNumber","boards",1));
    }

    @Test
    public void getBooleanBC() {
        ModelDataReader modelDataReader = new ModelDataReader();
        assertTrue(modelDataReader.getBooleanBC("wallT12","boards",1));
    }

    @Test
    public void getInt() {
        ModelDataReader modelDataReader = new ModelDataReader();
        assertEquals(3,modelDataReader.getInt("newtonMaxDistance"));
    }

    @Test
    public void getInt1() {
        ModelDataReader modelDataReader = new ModelDataReader();
        assertEquals(3,modelDataReader.getInt("numberOfActions","status",0));
    }

    @Test
    public void getBoolean() {
        ModelDataReader modelDataReader = new ModelDataReader();
        assertTrue(modelDataReader.getBoolean("collect2","status",2));
    }


    @Test
    public void getColorBC() {
        ModelDataReader modelDataReader = new ModelDataReader();
        assertEquals(Color.YELLOW,modelDataReader.getColorBC("aS9Color","boards",4));
    }


    /**
     * Tests the method getColor()
     */
    @Test
    public void getColor() {
        ModelDataReader modelDataReader = new ModelDataReader();
        Color out;
        out= modelDataReader.getColor(Weapon.WeaponName.LOCK_RIFLE);
        assertEquals(Color.BLUE,out);
    }

    /**
     * Tests the method getCostRed()
     */
    @Test
    public void getFullCostRed() {
        ModelDataReader modelDataReader = new ModelDataReader();
        AmmoPack out;
        out=modelDataReader.getFullCostRed(Weapon.WeaponName.MACHINE_GUN);
        assertEquals(1,out.getRedAmmo());
    }

    /**
     * Tests the method getCostBlue()
     */
    @Test
    public void getFullCostBlue() {
        ModelDataReader modelDataReader = new ModelDataReader();
        AmmoPack out;
        out=modelDataReader.getFullCostBlue(Weapon.WeaponName.LOCK_RIFLE);
        assertEquals(2,out.getBlueAmmo());
    }

    /**
     * Tests the method getCostYellow()
     */
    @Test
    public void getFullCostYellow() {
        ModelDataReader modelDataReader = new ModelDataReader();
        AmmoPack out;
        out=modelDataReader.getFullCostYellow(Weapon.WeaponName.MACHINE_GUN);
        assertEquals(0,out.getYellowAmmo());
    }

    /**
     * Tests the method getFireModeList()
     */
    @Test
    public void getNameList() {
        ModelDataReader modelDataReader = new ModelDataReader();
        int out;
        out=modelDataReader.getFireModeList(Weapon.WeaponName.THOR);
        assertEquals(4,out);
    }

    /**
     * Tests the method getTargetNumber()
     */
    @Test
    public void getTargetNumber() {
        ModelDataReader modelDataReader = new ModelDataReader();
        int out;
        out=modelDataReader.getTargetNumber(Weapon.WeaponName.THOR, FireMode.FireModeName.OPTION1);
        assertEquals(1,out);
    }

    /**
     * Tests the method getFireModeCostRed()
     */
    @Test
    public void getFireModeCostRed() {
        ModelDataReader modelDataReader = new ModelDataReader();
        int out;
        out=modelDataReader.getFireModeCostRed(Weapon.WeaponName.THOR, FireMode.FireModeName.OPTION1).getRedAmmo();
        assertEquals(0,out);
    }

    /**
     * Tests the method getFireModeCostBlue()
     */
    @Test
    public void getFireModeCostBlue() {
        ModelDataReader modelDataReader = new ModelDataReader();
        int out;
        out=modelDataReader.getFireModeCostBlue(Weapon.WeaponName.THOR, FireMode.FireModeName.OPTION1).getBlueAmmo();
        assertEquals(1,out);
    }

    /**
     * Tests the method getFireModeCostYellow()
     */
    @Test
    public void getFireModeCostYellow() {
        ModelDataReader modelDataReader = new ModelDataReader();
        int out;
        out=modelDataReader.getFireModeCostYellow(Weapon.WeaponName.THOR, FireMode.FireModeName.OPTION1).getYellowAmmo();
        assertEquals(0,out);
    }

    /**
     * Tests the method getMove()
     */
    @Test
    public void getMove() {
        ModelDataReader modelDataReader = new ModelDataReader();
        int out;
        out=modelDataReader.getMove(Weapon.WeaponName.VORTEX_CANNON, FireMode.FireModeName.MAIN);
        assertEquals(1,out);
    }

    /**
     * Tests the method getDmg()
     */
    @Test
    public void getDmg() {
        ModelDataReader modelDataReader = new ModelDataReader();
        int out;
        out=modelDataReader.getDmg(Weapon.WeaponName.FURNACE, FireMode.FireModeName.SECONDARY);
        assertEquals(1,out);
    }

    /**
     * Tests the method getMark()
     */
    @Test
    public void getMark() {
        ModelDataReader modelDataReader = new ModelDataReader();
        int out;
        out=modelDataReader.getMark(Weapon.WeaponName.FURNACE, FireMode.FireModeName.SECONDARY);
        assertEquals(1,out);
    }

    /**
     * Tests the method getEff()
     */
    @Test
    public void getEff() {
        ModelDataReader modelDataReader = new ModelDataReader();
        String out;
        out= modelDataReader.getEff(Weapon.WeaponName.VORTEX_CANNON, FireMode.FireModeName.OPTION1);
        assertEquals("moveDmg",out);
    }

    /**
     * Tests the method getWhere()
     */
    @Test
    public void getWhere() {
        ModelDataReader modelDataReader = new ModelDataReader();
        String out;
        out= modelDataReader.getWhere(Weapon.WeaponName.VORTEX_CANNON, FireMode.FireModeName.OPTION1);
        assertEquals("vortex2",out);
    }

    /**
     * Tests the method getMoveType()
     */
    @Test
    public void getMoveType() {
        ModelDataReader modelDataReader = new ModelDataReader();
        String out;
        out= modelDataReader.getMoveType(Weapon.WeaponName.VORTEX_CANNON, FireMode.FireModeName.OPTION1);
        assertEquals("vortex2",out);
    }



//-----Methods for BoardConfigurer-----------------------------------------------------------------------------------------------------------


}