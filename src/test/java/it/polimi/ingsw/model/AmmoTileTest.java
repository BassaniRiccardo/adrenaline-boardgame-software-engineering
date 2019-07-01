package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.BoardConfigurer;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Player;
import it.polimi.ingsw.model.cards.AmmoPack;
import it.polimi.ingsw.model.cards.AmmoTile;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;
import org.junit.Test;


/**
 * Tests all the methods of the class AmmoTile.
 *
 * @author  BassaniRiccardo
 */

public class AmmoTileTest {


    /**
     * Tests if an exception is thrown when the method getHolder() is called.
     *
     * @throws NotAvailableAttributeException   since an ammoTile is asked for its holder.
     */
    @Test(expected = NotAvailableAttributeException.class)
    public void getHolder() throws NotAvailableAttributeException {

        AmmoTile ammoTile = new AmmoTile(true, new AmmoPack(0,1,2));
        ammoTile.getHolder();
    }


    /**
     * Tests if an exception is thrown when the method getColor() is called.
     *
     * @throws NotAvailableAttributeException   since an ammoTile is asked for its color.
     */
    @Test(expected = NotAvailableAttributeException.class)
    public void getColor() throws NotAvailableAttributeException {

        AmmoTile ammoTile = new AmmoTile(true, new AmmoPack(0,1,2));
        ammoTile.getColor();
    }


    /**
     * Tests if an exception is thrown when the method setHolder() is called.
     *
     * @throws NotAvailableAttributeException   since someone tries to set the holder of an ammoTile.
     */
    @Test(expected = NotAvailableAttributeException.class)
    public void setHolder() throws NotAvailableAttributeException {

        Board board1 = BoardConfigurer.configureMap(1);
        AmmoTile ammoTile = new AmmoTile(true, new AmmoPack(0,1,2));
        ammoTile.setHolder(new Player(1, Player.HeroName.VIOLET, board1 ));
    }

}