package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.cards.Color;
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
}