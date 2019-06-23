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

    /**
     * Tests the method getIntBC(String)
     */
    @Test
    public void getIntBC() {
        ModelDataReader modelDataReader = new ModelDataReader();
        assertEquals(4,modelDataReader.getIntBC("columnsNumber"));
    }

    /**
     * Tests the method getBC(String,String,int)
     */
    @Test
    public void getIntBC1() {
        ModelDataReader modelDataReader = new ModelDataReader();
        assertEquals(3,modelDataReader.getIntBC("wSNumber","boards",1));
    }

    /**
     * Tests the method getBooleanBC(String,String,int)
     */
    @Test
    public void getBooleanBC() {
        ModelDataReader modelDataReader = new ModelDataReader();
        assertTrue(modelDataReader.getBooleanBC("wallT12","boards",1));
    }

    /**
     * Tests the method getInt(String)
     */
    @Test
    public void getInt() {
        ModelDataReader modelDataReader = new ModelDataReader();
        assertEquals(3,modelDataReader.getInt("newtonMaxDistance"));
    }

    /**
     * Tests the method getInt(String,String,int)
     */
    @Test
    public void getInt1() {
        ModelDataReader modelDataReader = new ModelDataReader();
        assertEquals(3,modelDataReader.getInt("numberOfActions","status",0));
    }

    /**
     * Tests the method getBoolean(String,String,int)
     */
    @Test
    public void getBoolean() {
        ModelDataReader modelDataReader = new ModelDataReader();
        assertTrue(modelDataReader.getBoolean("collect2","status",2));
    }

    /**
     * Tests the method getColorBC(String,String,int)
     */
    @Test
    public void getColorBC() {
        ModelDataReader modelDataReader = new ModelDataReader();
        assertEquals(Color.YELLOW,modelDataReader.getColorBC("aS9Color","boards",4));
    }


    /**
     * Tests the method getColor(Weapon.WeaponName)
     */
    @Test
    public void getColor() {
        ModelDataReader modelDataReader = new ModelDataReader();
        Color out;
        out= modelDataReader.getColor(Weapon.WeaponName.LOCK_RIFLE);
        assertEquals(Color.BLUE,out);
    }

    /**
     * Tests the method getCostRed(Weapon.WeaponName)
     */
    @Test
    public void getFullCostRed() {
        ModelDataReader modelDataReader = new ModelDataReader();
        AmmoPack out;
        out=modelDataReader.getFullCostRed(Weapon.WeaponName.MACHINE_GUN);
        assertEquals(1,out.getRedAmmo());
    }

    /**
     * Tests the method getCostBlue(Weapon.WeaponName)
     */
    @Test
    public void getFullCostBlue() {
        ModelDataReader modelDataReader = new ModelDataReader();
        AmmoPack out;
        out=modelDataReader.getFullCostBlue(Weapon.WeaponName.LOCK_RIFLE);
        assertEquals(2,out.getBlueAmmo());
    }

    /**
     * Tests the method getCostYellow(Weapon.WeaponName)
     */
    @Test
    public void getFullCostYellow() {
        ModelDataReader modelDataReader = new ModelDataReader();
        AmmoPack out;
        out=modelDataReader.getFullCostYellow(Weapon.WeaponName.MACHINE_GUN);
        assertEquals(0,out.getYellowAmmo());
    }

    /**
     * Tests the method getFireModeList(Weapon.WeaponName)
     */
    @Test
    public void getNameList() {
        ModelDataReader modelDataReader = new ModelDataReader();
        int out;
        out=modelDataReader.getFireModeList(Weapon.WeaponName.THOR);
        assertEquals(4,out);
    }

    /**
     * Tests the method getFireModeCostRed(Weapon.WeaponName, FireMode.FireModeName)
     */
    @Test
    public void getFireModeCostRed() {
        ModelDataReader modelDataReader = new ModelDataReader();
        int out;
        out=modelDataReader.getFireModeCostRed(Weapon.WeaponName.THOR, FireMode.FireModeName.OPTION1).getRedAmmo();
        assertEquals(0,out);
    }

    /**
     * Tests the method getFireModeCostBlue(Weapon.WeaponName, FireMode.FireModeName)
     */
    @Test
    public void getFireModeCostBlue() {
        ModelDataReader modelDataReader = new ModelDataReader();
        int out;
        out=modelDataReader.getFireModeCostBlue(Weapon.WeaponName.THOR, FireMode.FireModeName.OPTION1).getBlueAmmo();
        assertEquals(1,out);
    }

    /**
     * Tests the method getFireModeCostYellow(Weapon.WeaponName, FireMode.FireModeName)
     */
    @Test
    public void getFireModeCostYellow() {
        ModelDataReader modelDataReader = new ModelDataReader();
        int out;
        out=modelDataReader.getFireModeCostYellow(Weapon.WeaponName.THOR, FireMode.FireModeName.OPTION1).getYellowAmmo();
        assertEquals(0,out);
    }

    /**
     * Tests the method getMove(Weapon.WeaponName, FireMode.FireModeName)
     */
    @Test
    public void getMove() {
        ModelDataReader modelDataReader = new ModelDataReader();
        int out;
        out=modelDataReader.getMove(Weapon.WeaponName.VORTEX_CANNON, FireMode.FireModeName.MAIN);
        assertEquals(1,out);
    }

    /**
     * Tests the method getDmg(Weapon.WeaponName, FireMode.FireModeName)
     */
    @Test
    public void getDmg() {
        ModelDataReader modelDataReader = new ModelDataReader();
        int out;
        out=modelDataReader.getDmg(Weapon.WeaponName.FURNACE, FireMode.FireModeName.SECONDARY);
        assertEquals(1,out);
    }

    /**
     * Tests the method getMark(Weapon.WeaponName, FireMode.FireModeName)
     */
    @Test
    public void getMark() {
        ModelDataReader modelDataReader = new ModelDataReader();
        int out;
        out=modelDataReader.getMark(Weapon.WeaponName.FURNACE, FireMode.FireModeName.SECONDARY);
        assertEquals(1,out);
    }

    /**
     * Tests the method getEff(Weapon.WeaponName, FireMode.FireModeName)
     */
    @Test
    public void getEff() {
        ModelDataReader modelDataReader = new ModelDataReader();
        String out;
        out= modelDataReader.getEff(Weapon.WeaponName.VORTEX_CANNON, FireMode.FireModeName.OPTION1);
        assertEquals("moveDmg",out);
    }

    /**
     * Tests the method getWhere(Weapon.WeaponName, FireMode.FireModeName)
     */
    @Test
    public void getWhere() {
        ModelDataReader modelDataReader = new ModelDataReader();
        String out;
        out= modelDataReader.getWhere(Weapon.WeaponName.VORTEX_CANNON, FireMode.FireModeName.OPTION1);
        assertEquals("vortex2",out);
    }

    /**
     * Tests the method getMoveType(Weapon.WeaponName, FireMode.FireModeName)
     */
    @Test
    public void getMoveType() {
        ModelDataReader modelDataReader = new ModelDataReader();
        String out;
        out= modelDataReader.getMoveType(Weapon.WeaponName.VORTEX_CANNON, FireMode.FireModeName.OPTION1);
        assertEquals("vortex2",out);
    }

}