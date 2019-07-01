package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.BoardConfigurer;
import it.polimi.ingsw.controller.WeaponFactory;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Player;
import it.polimi.ingsw.model.cards.FireMode;
import it.polimi.ingsw.model.cards.Weapon;
import it.polimi.ingsw.model.exceptions.NoMoreCardsException;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;
import it.polimi.ingsw.model.exceptions.UnacceptableItemNumberException;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

/**
 * Tests the methods of the class FireMode.
 *
 * @author marcobaga
 */

public class FireModeTest {


    /**
     * Checks that a firemode is created correctly and available
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario() or addWeapon().
     * @throws NotAvailableAttributeException       if thrown by Firemode.isAvailable().
     */
    @Test
    public void isAvailable() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {

        Board b = BoardConfigurer.simulateScenario();
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Player p = b.getPlayers().get(0);
        p.addWeapon(weaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE));
        FireMode f = p.getWeaponList().get(0).getFireModeList().get(0);
        assertSame(FireMode.FireModeName.MAIN, f.getName());
        assertSame(FireMode.FireModeName.OPTION1, p.getWeaponList().get(0).getFireModeList().get(1).getName());
        assertEquals(2, p.getWeaponList().get(0).getFireModeList().size());
        assertTrue(f.isAvailable());
    }


    /**
     * Checks that a firemode is created correctly and not available.
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario() or addWeapon().
     * @throws NotAvailableAttributeException       if thrown by Firemode.isAvailable().
     */
    @Test
    public void isAvailable2() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        Board b = BoardConfigurer.simulateScenario();
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Player p = b.getPlayers().get(4);
        p.addWeapon(weaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE));
        FireMode f = p.getWeaponList().get(0).getFireModeList().get(0);
        assertSame( FireMode.FireModeName.MAIN, f.getName());
        assertSame(FireMode.FireModeName.OPTION1, p.getWeaponList().get(0).getFireModeList().get(1).getName());
        assertEquals(2, p.getWeaponList().get(0).getFireModeList().size());
        assertFalse(f.isAvailable());
    }


    /**
     * Checks that the firemode applies its effect.
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario() or addWeapon().
     * @throws NotAvailableAttributeException       if thrown by Firemode.applyEffects().
     */
    @Test
    public void applyEffects() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        Board b = BoardConfigurer.simulateScenario();
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Player p = b.getPlayers().get(4);
        p.addWeapon(weaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE));
        FireMode f = p.getWeaponList().get(0).getFireModeList().get(0);
        List<Player> otherPlayers = b.getPlayers();
        otherPlayers.remove(p);
        f.applyEffects(otherPlayers, null);
        for (int i = 1; i<4;i++){
            assertTrue(b.getPlayers().get(i).isJustDamaged());
        }
    }


    /**
     * Checks that targets are selected correctly when there are some.
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario() or addWeapon().
     * @throws NotAvailableAttributeException       if thrown by Firemode.findTargets().
     */
    @Test
    public void findTargets() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        Board b = BoardConfigurer.simulateScenario();
        WeaponFactory weaponFactory = new WeaponFactory(b);
        b.getPlayers().get(0).addWeapon(weaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE));
        FireMode f = b.getPlayers().get(0).getWeaponList().get(0).getFireModeList().get(0);
        assertFalse(f.findTargets().isEmpty());
        List<Player> ap = f.findTargets().get(0);
        assertTrue(ap.contains(b.getPlayers().get(1)));
    }


    /**
     * Checks that targets are not selected when none is visible
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario() or addWeapon().
     * @throws NotAvailableAttributeException       if thrown by Firemode.findTargets().
     */
    @Test
    public void findTargets2() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        Board b = BoardConfigurer.simulateScenario();
        WeaponFactory weaponFactory = new WeaponFactory(b);
        b.getPlayers().get(4).addWeapon(weaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE));
        FireMode f = b.getPlayers().get(4).getWeaponList().get(0).getFireModeList().get(0);
        assertTrue(f.findTargets().isEmpty());
    }


    /**
     * Checks that destinationFinder is working correctly (Note: the only weapon implemented so far always returns null as intended).
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario() or addWeapon().
     * @throws NotAvailableAttributeException       if thrown by Firemode.findTargets() or Firemode.findDestinations().
     */
    @Test
    public void findDestinations() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        Board b = BoardConfigurer.simulateScenario();
        WeaponFactory weaponFactory = new WeaponFactory(b);
        b.getPlayers().get(0).addWeapon(weaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE));
        FireMode f = b.getPlayers().get(0).getWeaponList().get(0).getFireModeList().get(0);
        assertFalse(f.findTargets().isEmpty());
        List<Player> ap = f.findTargets().get(0);
        assertTrue(f.findDestinations(ap).isEmpty());
    }


    /**
     * Checks that destinationFinder is working correctly (Note: the only weapon implemented so far always returns null as intended)
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario() or addWeapon().
     * @throws NotAvailableAttributeException       if thrown by Firemode.findDestinations().
     */
    @Test(expected = NotAvailableAttributeException.class)
    public void findDestinationsBadArguments() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        Board b = BoardConfigurer.simulateScenario();
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon weapon = weaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE);
        b.getPlayers().get(0).addWeapon(weapon);
        FireMode f = b.getPlayers().get(0).getWeaponList().get(0).getFireModeList().get(0);
        weapon.removeHolder();
        f.findDestinations(b.getPlayers());
    }

}