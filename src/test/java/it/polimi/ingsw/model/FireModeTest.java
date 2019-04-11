package it.polimi.ingsw.model;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

public class FireModeTest {

    /**
     * Checks that a firemode is created correctly and available
     */
    @Test
    public void isAvailable() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        BoardConfigurer.getInstance().simulateScenario();
        Player p = Board.getInstance().getPlayers().get(0);
        p.addWeapon(WeaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE));
        FireMode f = p.getWeaponList().get(0).getFireModeList().get(0);
        assertTrue(f.getName()== FireMode.FireModeName.MAIN);
        assertTrue(p.getWeaponList().get(0).getFireModeList().get(1).getName() == FireMode.FireModeName.OPTION1);
        assertTrue(p.getWeaponList().get(0).getFireModeList().size()==2);
        assertTrue(f.isAvailable());
    }

    /**
     * Checks that a firemode is created correctly and not available
     */
    @Test
    public void isAvailable2() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        BoardConfigurer.getInstance().simulateScenario();
        Player p = Board.getInstance().getPlayers().get(4);
        p.addWeapon(WeaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE));
        FireMode f = p.getWeaponList().get(0).getFireModeList().get(0);
        assertTrue(f.getName() == FireMode.FireModeName.MAIN);
        assertTrue(p.getWeaponList().get(0).getFireModeList().get(1).getName() == FireMode.FireModeName.OPTION1);
        assertTrue(p.getWeaponList().get(0).getFireModeList().size()==2);
        assertFalse(f.isAvailable());
    }

    /**
     * Checks that the firemode applies its effect
     */
    @Test
    public void applyEffects() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        BoardConfigurer.getInstance().simulateScenario();
        Player p = Board.getInstance().getPlayers().get(4);
        p.addWeapon(WeaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE));
        FireMode f = p.getWeaponList().get(0).getFireModeList().get(0);
        f.applyEffects(Board.getInstance().getPlayers(), null);
        for (int i = 1; i<5;i++){
            assertTrue(Board.getInstance().getPlayers().get(i).isJustDamaged());
        }
    }

    /**
     * Checks that targets are selected correctly when there are some
     */
    @Test
    public void findTargets() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        BoardConfigurer.getInstance().simulateScenario();
        Board.getInstance().getPlayers().get(0).addWeapon(WeaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE));
        FireMode f = Board.getInstance().getPlayers().get(0).getWeaponList().get(0).getFireModeList().get(0);
        assertFalse(f.findTargets().isEmpty());
        List<Player> ap = f.findTargets().get(0);
        assertTrue(ap.contains(Board.getInstance().getPlayers().get(1)));
    }

    /**
     * Checks that targets are not selected when none is visible
     */
    @Test
    public void findTargets2() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        BoardConfigurer.getInstance().simulateScenario();
        Board.getInstance().getPlayers().get(4).addWeapon(WeaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE));
        FireMode f = Board.getInstance().getPlayers().get(4).getWeaponList().get(0).getFireModeList().get(0);
        assertTrue(f.findTargets().isEmpty());
    }

    /**
     * Checks that destinationFinder is working correctly (Note: the only weapon implemented so far always returns null as intended)
     */
    @Test
    public void findDestinations() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        BoardConfigurer.getInstance().simulateScenario();
        Board.getInstance().getPlayers().get(0).addWeapon(WeaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE));
        FireMode f = Board.getInstance().getPlayers().get(0).getWeaponList().get(0).getFireModeList().get(0);
        assertFalse(f.findTargets().isEmpty());
        List<Player> ap = f.findTargets().get(0);
        assertNull(f.findDestinations(ap));
    }
}