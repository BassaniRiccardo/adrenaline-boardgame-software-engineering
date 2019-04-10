package it.polimi.ingsw.model;

/**
 * Tests the class AmmoTile
 *
 * @author  BassaniRiccardo
 */

import org.junit.Test;

public class AmmoTileTest {


    /**
     * Tests if an exception is thrown when the method getHolder() is called.
     *
     * @throws NotAvailableAttributeException
     */
    @Test(expected = NotAvailableAttributeException.class)
    public void getHolder() throws NotAvailableAttributeException {

        AmmoTile ammoTile = new AmmoTile(true, new AmmoPack(0,1,2));
        Player holder = ammoTile.getHolder();
    }


    /**
     * Tests if an exception is thrown when the method getColor() is called.
     *
     * @throws NotAvailableAttributeException
     */
    @Test(expected = NotAvailableAttributeException.class)
    public void getColor() throws NotAvailableAttributeException {

        AmmoTile ammoTile = new AmmoTile(true, new AmmoPack(0,1,2));
        Color color = ammoTile.getColor();
    }


    /**
     * Tests if an exception is thrown when the method setHolder() is called.
     * @throws NotAvailableAttributeException
     */
    @Test(expected = NotAvailableAttributeException.class)
    public void setHolder() throws NotAvailableAttributeException {

        AmmoTile ammoTile = new AmmoTile(true, new AmmoPack(0,1,2));
        ammoTile.setHolder(new Player(1, Player.HeroName.VIOLET ));
    }

}