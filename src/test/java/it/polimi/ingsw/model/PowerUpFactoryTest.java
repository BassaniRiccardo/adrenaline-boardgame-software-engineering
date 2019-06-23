package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.BoardConfigurer;
import it.polimi.ingsw.controller.PowerUpFactory;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.Color;
import it.polimi.ingsw.model.cards.PowerUp;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;
import org.junit.Test;
import static org.junit.Assert.*;

//TODO try and catch exceptions when the return value is null

public class PowerUpFactoryTest {

    /**
     * Creates the first power up and checks that it is initialized correctly
     */
    @Test
    public void createPowerUp() {
        Board board1 = BoardConfigurer.getInstance().configureMap(1);
        PowerUpFactory powerUpFactory = new PowerUpFactory(board1);
        PowerUp p = powerUpFactory.createPowerUp(PowerUp.PowerUpName.TARGETING_SCOPE, Color.BLUE);
        assertTrue(p.getName() == PowerUp.PowerUpName.TARGETING_SCOPE);
        assertFalse(p.getEffect()==null);
        assertFalse(p.getTargetFinder()==null);
        assertFalse(p.getDestinationFinder()==null);
        try {
            p.getHolder();
        } catch (NotAvailableAttributeException e){}
        assertTrue(p.getColor() == Color.BLUE);
    }
}