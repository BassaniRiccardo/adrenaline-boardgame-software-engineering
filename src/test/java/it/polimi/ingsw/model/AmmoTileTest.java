package it.polimi.ingsw.model;

/**
 * Tests all the methods of the class AmmoTile.
 *
 * @author  BassaniRiccardo
 */

import it.polimi.ingsw.controller.BoardConfigurer;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Player;
import it.polimi.ingsw.model.cards.AmmoPack;
import it.polimi.ingsw.model.cards.AmmoTile;
import it.polimi.ingsw.model.cards.Color;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;
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

        Board board1 = BoardConfigurer.getInstance().configureMap(1);
        AmmoTile ammoTile = new AmmoTile(true, new AmmoPack(0,1,2));
        ammoTile.setHolder(new Player(1, Player.HeroName.VIOLET, board1 ));
    }

}