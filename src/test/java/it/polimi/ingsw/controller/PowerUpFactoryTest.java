package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.BoardConfigurer;
import it.polimi.ingsw.controller.PowerUpFactory;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.Color;
import it.polimi.ingsw.model.cards.PowerUp;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests PowerUpFactory, checking that it correctly initializes a powerup.
 *
 * @author marcobaga
 */

public class PowerUpFactoryTest {

    /**
     * Creates the first power up and checks that it is initialized correctly.
     *
     * @throws NotAvailableAttributeException since the powerup does not have an holder.
     */
    @Test(expected = NotAvailableAttributeException.class)
    public void createPowerUp() throws NotAvailableAttributeException {
        Board board1 = BoardConfigurer.configureMap(1);
        PowerUpFactory powerUpFactory = new PowerUpFactory(board1);
        PowerUp p = powerUpFactory.createPowerUp(PowerUp.PowerUpName.TARGETING_SCOPE, Color.BLUE);
        assertSame(PowerUp.PowerUpName.TARGETING_SCOPE, p.getName());
        assertNotNull(p.getEffect());
        assertNotNull(p.getTargetFinder());
        assertNotNull(p.getDestinationFinder());
        p.getHolder();
        assertSame(Color.BLUE, p.getColor());
    }
}