package it.polimi.ingsw.model;

import org.junit.Test;
import static org.junit.Assert.*;

//TODO try and catch exceptions when the return value is null

public class PowerUpFactoryTest {

    /**
     * Creates the first powerup and checks that it is initialized correctly
     */
    @Test
    public void createPowerUp() {
        PowerUp p = PowerUpFactory.createPowerUp(PowerUp.PowerUpName.TARGETING_SCOPE, Color.BLUE);
        assertTrue(p.getName() == PowerUp.PowerUpName.TARGETING_SCOPE);
        assertTrue(p.getCost().getRedAmmo()==0);
        assertTrue(p.getCost().getBlueAmmo()==0);
        assertTrue(p.getCost().getYellowAmmo()==0);
        assertFalse(p.getEffect()==null);
        assertFalse(p.getTargetFinder()==null);
        assertFalse(p.getDestinationFinder()==null);
        try {
            p.getHolder();
        } catch (NotAvailableAttributeException e){}
        assertTrue(p.getColor() == Color.BLUE);
    }
}