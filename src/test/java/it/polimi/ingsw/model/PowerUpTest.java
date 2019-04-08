package it.polimi.ingsw.model;


import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;
public class PowerUpTest {

    /**
     * Checks that a powerup is correctly marked as available
     */
    @Test
    public void isAvailable() {
        BoardConfigurer.getInstance().simulateScenario();
        Board.getInstance().getPlayers().get(0).drawPowerUp();
        PowerUp p = Board.getInstance().getPlayers().get(0).getPowerUpList().get(0);
        Board.getInstance().getPlayers().get(1).setJustDamaged(true);
        assertTrue(p.isAvailable());
    }

    /**
     * Checks that a powerup is not available when no targets can be seen
     */
    @Test
    public void isAvailable2() {
        BoardConfigurer.getInstance().simulateScenario();
        Board.getInstance().getPlayers().get(4).drawPowerUp();
        PowerUp p = Board.getInstance().getPlayers().get(4).getPowerUpList().get(0);
        assertFalse(p.isAvailable());
    }

    /**
     * Checks that a powerup is not available when players are visibile but cannot be selected
     */
    @Test
    public void isAvailable3() {
        BoardConfigurer.getInstance().simulateScenario();
        Board.getInstance().getPlayers().get(0).drawPowerUp();
        PowerUp p = Board.getInstance().getPlayers().get(0).getPowerUpList().get(0);
        assertFalse(p.isAvailable());
    }

    /**
     * Checks that the powerup applies its effect
     */
    @Test
    public void applyEffects() {
        BoardConfigurer.getInstance().simulateScenario();
        Board.getInstance().getPlayers().get(0).drawPowerUp();
        PowerUp p = Board.getInstance().getPlayers().get(0).getPowerUpList().get(0);
        p.applyEffects(Board.getInstance().getPlayers(), null);
        assertTrue(Board.getInstance().getPlayers().get(1).isJustDamaged());
    }

    /**
     * Checks that targets are selected when there are some
     */
    @Test
    public void findTargets() {
        BoardConfigurer.getInstance().simulateScenario();
        Board.getInstance().getPlayers().get(0).drawPowerUp();
        PowerUp p = Board.getInstance().getPlayers().get(0).getPowerUpList().get(0);
        Board.getInstance().getPlayers().get(1).setJustDamaged(true);
        assertFalse(p.findTargets().isEmpty());
        List<Player> ap = p.findTargets().get(0);
        assertTrue(ap.contains(Board.getInstance().getPlayers().get(1)));
    }

    /**
     * Checks that targets are selected when there are none
     */
    @Test
    public void findTargets2() {
        BoardConfigurer.getInstance().simulateScenario();
        Board.getInstance().getPlayers().get(0).drawPowerUp();
        PowerUp p = Board.getInstance().getPlayers().get(0).getPowerUpList().get(0);
        assertTrue(p.findTargets().isEmpty());
    }

    /**
     * Checks that destinationFinder is working correctly (Note: the only powerup implemented so far always returns null as intended)
     */
    @Test
    public void findDestinations(){
        BoardConfigurer.getInstance().simulateScenario();
        Board.getInstance().getPlayers().get(0).drawPowerUp();
        PowerUp p = Board.getInstance().getPlayers().get(0).getPowerUpList().get(0);
        assertTrue(p.findDestinations(Board.getInstance().getPlayers())==null);
    }
}