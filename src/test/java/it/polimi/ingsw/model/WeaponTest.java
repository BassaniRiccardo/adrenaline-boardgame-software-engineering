package it.polimi.ingsw.model;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class WeaponTest {

    /**
     * Tests listAvailableFireModes() when all firemodes are available
     */
    @Test
    public void listAvailableFireModes() throws UnacceptableItemNumberException, NoMoreCardsException {
        BoardConfigurer.getInstance().simulateScenario();
        Board b = Board.getInstance();
        b.getPlayers().get(0).addWeapon(WeaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE));
        Weapon w = b.getPlayers().get(0).getWeaponList().get(0);
        b.getPlayers().get(0).addAmmoPack(new AmmoPack(3,3,3));
        b.getPlayers().get(0).addMainTarget(b.getPlayers().get(1));
        ArrayList<FireMode> f = (ArrayList<FireMode>)w.listAvailableFireModes();
        assertTrue(f.size() == 2);
        assertTrue(f.containsAll(w.getFireModeList()));
        assertTrue(w.getFireModeList().containsAll(f));
    }

    /**
     * Tests listAvailableFireModes() when the player has no ammo and only the main firemode is available
     */
    @Test
    public void listAvailableFireModes2() throws UnacceptableItemNumberException, NoMoreCardsException {
        BoardConfigurer.getInstance().simulateScenario();
        Board b = Board.getInstance();
        b.getPlayers().get(0).addWeapon(WeaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE));
        Weapon w = b.getPlayers().get(0).getWeaponList().get(0);
        b.getPlayers().get(0).addAmmoPack(new AmmoPack(0,0,0));
        b.getPlayers().get(0).addMainTarget(b.getPlayers().get(1));
        ArrayList<FireMode> f = (ArrayList<FireMode>)w.listAvailableFireModes();
        assertTrue(f.size() == 1);
        assertFalse(f.containsAll(w.getFireModeList()));
        assertTrue(w.getFireModeList().containsAll(f));
    }

    /**
     * Tests listAvailableFireModes() when only the main firemode is available due to lack of targets (it
     * can only hit targets that are contained by mainTargets, which is empty by default)
     */
    @Test
    public void listAvailableFireModes3() throws UnacceptableItemNumberException, NoMoreCardsException {
        BoardConfigurer.getInstance().simulateScenario();
        Board b = Board.getInstance();
        b.getPlayers().get(0).addWeapon(WeaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE));
        Weapon w = b.getPlayers().get(0).getWeaponList().get(0);
        b.getPlayers().get(0).addAmmoPack(new AmmoPack(0,0,0));
        ArrayList<FireMode> f = (ArrayList<FireMode>)w.listAvailableFireModes();
        assertTrue(f.size() == 1);
        assertFalse(f.containsAll(w.getFireModeList()));
        assertTrue(w.getFireModeList().containsAll(f));
    }

    /**
     * Tests listAvailableFireModes() when no firemodes are available
     */
    @Test
    public void listAvailableFireModes4() throws UnacceptableItemNumberException, NoMoreCardsException {
        BoardConfigurer.getInstance().simulateScenario();
        Board b = Board.getInstance();
        b.getPlayers().get(4).addWeapon(WeaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE));
        Weapon w = b.getPlayers().get(4).getWeaponList().get(0);
        b.getPlayers().get(4).addAmmoPack(new AmmoPack(0,0,0));
        ArrayList<FireMode> f = (ArrayList<FireMode>)w.listAvailableFireModes();
        assertTrue(f.size() == 0);
    }

    /**
     * Tests reload(), checks for a weapon to be unloaded by default, for the reload method to work and return the correct value
     */
    @Test
    public void reload() {
        Weapon w = WeaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE);
        assertFalse(w.isLoaded());
        w.reload();
        assertTrue(w.isLoaded());
        assertFalse(w.reload());
    }
}
