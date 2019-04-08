package it.polimi.ingsw.model;

import org.junit.Test;
import static org.junit.Assert.*;

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
        assertTrue(p.getHolder() == null);
        assertTrue(p.getColor() == Color.BLUE);
    }
}