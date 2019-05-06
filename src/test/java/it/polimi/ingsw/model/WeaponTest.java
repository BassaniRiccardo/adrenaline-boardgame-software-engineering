package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.BoardConfigurer;
import it.polimi.ingsw.controller.WeaponFactory;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.AmmoPack;
import it.polimi.ingsw.model.cards.FireMode;
import it.polimi.ingsw.model.cards.Weapon;
import it.polimi.ingsw.model.exceptions.NoMoreCardsException;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;
import it.polimi.ingsw.model.exceptions.UnacceptableItemNumberException;
import it.polimi.ingsw.model.exceptions.WrongTimeException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class WeaponTest {

    /**
     * Tests listAvailableFireModes() when all firemodes are available
     */
    @Test
    public void listAvailableFireModes() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {

        Board b = BoardConfigurer.getInstance().simulateScenario();
        WeaponFactory weaponFactory = new WeaponFactory(b);
        b.getPlayers().get(0).addWeapon(weaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE));
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
    public void listAvailableFireModes2() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        Board b = BoardConfigurer.getInstance().simulateScenario();
        WeaponFactory weaponFactory = new WeaponFactory(b);
        b.getPlayers().get(0).addWeapon(weaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE));
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
    public void listAvailableFireModes3() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        Board b = BoardConfigurer.getInstance().simulateScenario();
        WeaponFactory weaponFactory = new WeaponFactory(b);
        b.getPlayers().get(0).addWeapon(weaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE));
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
    public void listAvailableFireModes4() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        Board b = BoardConfigurer.getInstance().simulateScenario();
        WeaponFactory weaponFactory = new WeaponFactory(b);
        b.getPlayers().get(4).addWeapon(weaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE));
        Weapon w = b.getPlayers().get(4).getWeaponList().get(0);
       // b.getPlayers().get(4).addAmmoPack(new AmmoPack(0,0,0));
        List<FireMode> f = w.listAvailableFireModes();
        assertEquals(0, f.size());
    }

    /**
     * Tests reload(), checks for a weapon to be unloaded by default, for the reload method to work and return the correct value
     */
    @Test
    public void reload() throws NoMoreCardsException, UnacceptableItemNumberException, WrongTimeException, NotAvailableAttributeException {
        Board b = BoardConfigurer.getInstance().simulateScenario();
        b.getPlayers().get(0).getAmmoPack().addAmmoPack(new AmmoPack(3,3,3));
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon w = weaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE);
        w.setHolder(b.getPlayers().get(0));
        assertFalse(w.isLoaded());
        w.reload();
        assertTrue(w.isLoaded());
    }

    /**
     * Tests the method toString() of the enumeration WeaponName.
     */
    @Test
    public void heroNameToString(){

        Weapon.WeaponName weaponName = Weapon.WeaponName.LOCK_RIFLE;
        assertEquals("Lock rifle", weaponName.toString());

    }

    /**
     * Tests the method toString() of the class Weapon.
     */
    @Test
    public void weaponToString(){

        WeaponFactory weaponFactory = new WeaponFactory(new Board());
        Weapon weapon = weaponFactory.createWeapon(Weapon.WeaponName.ELECTROSCYTHE);

        assertEquals("Electroscythe", weapon.toString());

    }
}
