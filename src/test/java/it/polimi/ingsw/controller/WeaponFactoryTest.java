package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Player;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.exceptions.*;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

//This class needs more in-depth testing once it has been fully implemented: only the first weapon has been tested so far
public class WeaponFactoryTest {

    /**
     * Creates the first weapon and checks that it is initialized correctly
     */
    @Test
    public void createWeapon() throws NoMoreCardsException, UnacceptableItemNumberException {
        Board b = BoardConfigurer.simulateScenario();
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon w = weaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE);
        assertTrue(w.getWeaponName() == Weapon.WeaponName.LOCK_RIFLE);

        assertTrue(w.getFullCost().getRedAmmo()==0);
        assertTrue(w.getFullCost().getBlueAmmo()==2);
        assertTrue(w.getFullCost().getYellowAmmo()==0);
        assertEquals(false, w.getFireModeList().isEmpty());
        assertTrue(w.getFireModeList().size()==2);
        assertTrue(w.getReducedCost().getRedAmmo()==0);
        assertTrue(w.getReducedCost().getBlueAmmo()==1);
        assertTrue(w.getReducedCost().getYellowAmmo()==0);
        try {
            w.getHolder();
        }catch (NotAvailableAttributeException e){}
        assertFalse(w.isLoaded());
        assertTrue(w.getMainTargets().isEmpty());
        assertTrue(w.getOptionalTargets().isEmpty());
        try{
            w.getHolder();
        }catch (NotAvailableAttributeException e){}
        FireMode f = w.getFireModeList().get(0);
      /*  assertTrue(f.getUsername() == FireMode.FireModeName.MAIN);
        assertTrue(f.getMaxTargets()==1);
        assertTrue(f.getCost().getRedAmmo() == 0);
        assertTrue(f.getCost().getBlueAmmo() == 0);
        assertTrue(f.getCost().getYellowAmmo() == 0);
        assertTrue(f.getDestinationFinder()!=null&&f.getTargetFinder()!=null&&f.getEffect()!=null);

        f = w.getFireModeList().get(1);
        assertTrue(f.getUsername() == FireMode.FireModeName.OPTION1);
        assertTrue(f.getMaxTargets()== 1);
        assertTrue(f.getCost().getRedAmmo() == 1);
        assertTrue(f.getCost().getBlueAmmo() == 0);
        assertTrue(f.getCost().getYellowAmmo() == 0);
        assertTrue(f.getDestinationFinder()!=null&&f.getTargetFinder()!=null&&f.getEffect()!=null);
    */}

    /**
     * Tests that the color of a particular weapon is correct
     */
    @Test
    public void getColor()  throws NoMoreCardsException, UnacceptableItemNumberException {
        Board b = BoardConfigurer.simulateScenario();
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon w = weaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE);
        assertTrue(Color.BLUE==w.getColor());
    }

    /**
     * Tests that the full cost of a particular weapon is correct
     */
    @Test
    public void getFullCost() throws NoMoreCardsException, UnacceptableItemNumberException {
        Board b = BoardConfigurer.simulateScenario();
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon w = weaponFactory.createWeapon(Weapon.WeaponName.RAILGUN);
        assertEquals(0,w.getFullCost().getRedAmmo());
        assertEquals(1,w.getFullCost().getBlueAmmo());
        assertEquals(2,w.getFullCost().getYellowAmmo());
    }

    /**
     * Tests that the reduced cost of a particular weapon is correct
     */
    @Test
    public void getReducedCost() throws NoMoreCardsException, UnacceptableItemNumberException {
        Board b = BoardConfigurer.simulateScenario();
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon w = weaponFactory.createWeapon(Weapon.WeaponName.RAILGUN);
        assertEquals(0,w.getReducedCost().getRedAmmo());
        assertEquals(1,w.getReducedCost().getBlueAmmo());
        assertEquals(1,w.getReducedCost().getYellowAmmo());
    }

    /**
     * Tests that the name list of a particular weapon is correct
     */
   /* @Test
    public void getFireModeList() throws NoMoreCardsException, UnacceptableItemNumberException {
        Board b = BoardConfigurer.simulateScenario();
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon w = weaponFactory.createWeapon(Weapon.WeaponName.RAILGUN);
        FireMode f = w.getFireModeList().get(0);
        assertTrue(f.getUsername() == FireMode.FireModeName.MAIN);
        f = w.getFireModeList().get(1);
        assertTrue(f.getUsername() == FireMode.FireModeName.SECONDARY);
        assertEquals(2,w.getFireModeList().size());
    }*/

    /**
     * Tests that the target number of a particular weapon is correct
     */
    @Test
    public void getTargetNumber() throws NoMoreCardsException, UnacceptableItemNumberException {
        Board b = BoardConfigurer.simulateScenario();
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon w = weaponFactory.createWeapon(Weapon.WeaponName.RAILGUN);
        int tN = weaponFactory.getTargetNumber(w.getWeaponName(), FireMode.FireModeName.SECONDARY);
        assertEquals(2,tN);
    }

    /**
     * Tests that the fire mode cost of a particular weapon is correct
     */
    @Test
    public void getFireModeCost() throws NoMoreCardsException, UnacceptableItemNumberException {
        Board b = BoardConfigurer.simulateScenario();
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon w = weaponFactory.createWeapon(Weapon.WeaponName.SHOCKWAVE);
        AmmoPack ammoPack = weaponFactory.getFireModeCost(w.getWeaponName(), FireMode.FireModeName.SECONDARY);
        assertEquals(1,ammoPack.getYellowAmmo());
        assertEquals(0,ammoPack.getRedAmmo());
        assertEquals(0,ammoPack.getBlueAmmo());
    }

    @Test
    public void createAllWeapon() throws NoMoreCardsException, UnacceptableItemNumberException{
        Board b = BoardConfigurer.simulateScenario();
        WeaponFactory weaponFactory = new WeaponFactory(b);
        for(Weapon.WeaponName weaponName : Weapon.WeaponName.values()){
            weaponFactory.createWeapon(weaponName);
            System.out.println(weaponName);
        }
    }

    @Test
    public void pintAllTargetFinder() throws NoMoreCardsException, UnacceptableItemNumberException{
        Board b = BoardConfigurer.simulateScenario();
        WeaponFactory weaponFactory = new WeaponFactory(b);
        for(Weapon.WeaponName weaponName : Weapon.WeaponName.values()){
            weaponFactory.createWeapon(weaponName);
            System.out.println(weaponName);
            //for (FireMode.FireModeName fireModeName : weaponName.getFireModeList)
        }
    }

    @Test
    public void lockRifle() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException{

        Board b = BoardConfigurer.simulateScenario();
        Player shooter = b.getPlayers().get(0);
        Player banshee = b.getPlayers().get(1);
        Player dozer = b.getPlayers().get(2);
        Player violet = b.getPlayers().get(3);
        Player sprog = b.getPlayers().get(4);
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon lockRifle = weaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE);
        shooter.addWeapon(lockRifle);
        //MAIN TARGETS
        assertEquals("[[Player 2 : anonymous(Banshee)], [Player 3 : anonymous(Dozer)]]",lockRifle.getFireModeList().get(0).findTargets().toString());
        //MAIN DESTINATIONS
        assertTrue(lockRifle.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(banshee))).isEmpty());
        //OPTION 1 TARGETS
        assertTrue(lockRifle.getFireModeList().get(1).findTargets().isEmpty());
        shooter.addMainTarget(banshee);
        assertEquals("[[Player 3 : anonymous(Dozer)]]",lockRifle.getFireModeList().get(1).findTargets().toString());
        //OPTION 1 DESTINATIONS
        assertTrue(lockRifle.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(banshee))).isEmpty());

    }


    @Test
    public void machineGun() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException{

        Board b = BoardConfigurer.simulateScenario();
        Player shooter = b.getPlayers().get(0);
        Player banshee = b.getPlayers().get(1);
        Player dozer = b.getPlayers().get(2);
        Player violet = b.getPlayers().get(3);
        Player sprog = b.getPlayers().get(4);
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon machineGun = weaponFactory.createWeapon(Weapon.WeaponName.MACHINE_GUN);
        shooter.addWeapon(machineGun);
        //MAIN TARGETS
        assertEquals("[[Player 2 : anonymous(Banshee)], [Player 3 : anonymous(Dozer)], [Player 2 : anonymous(Banshee), Player 3 : anonymous(Dozer)]]",machineGun.getFireModeList().get(0).findTargets().toString());
        //MAIN DESTINATIONS
        assertTrue(machineGun.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(banshee))).isEmpty());
        //OPTION 1 TARGETS
        assertTrue(machineGun.getFireModeList().get(1).findTargets().isEmpty());
        shooter.addMainTarget(banshee);
        assertEquals("[[Player 2 : anonymous(Banshee)]]",machineGun.getFireModeList().get(1).findTargets().toString());
        shooter.addMainTarget(dozer);
        assertEquals("[[Player 2 : anonymous(Banshee)], [Player 3 : anonymous(Dozer)]]",machineGun.getFireModeList().get(1).findTargets().toString());
        //OPTION 1 DESTINATIONS
        assertTrue(machineGun.getFireModeList().get(1).findDestinations(new ArrayList<>(Arrays.asList(banshee))).isEmpty());
        //OPTION 2 TARGETS
        shooter.addOptionalTarget(banshee);
        shooter.getMainTargets().remove(dozer);
        assertEquals("[[Player 3 : anonymous(Dozer)]]",machineGun.getFireModeList().get(2).findTargets().toString());
        //OPTION 2 DESTINATIONS
        assertTrue(machineGun.getFireModeList().get(2).findDestinations(new ArrayList<>(Arrays.asList(banshee))).isEmpty());

    }

    @Test
    public void furnace() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException{

        Board b = BoardConfigurer.simulateScenario();
        Player shooter = b.getPlayers().get(0);
        Player banshee = b.getPlayers().get(1);
        Player dozer = b.getPlayers().get(2);
        Player violet = b.getPlayers().get(3);
        Player sprog = b.getPlayers().get(4);
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon furnace = weaponFactory.createWeapon(Weapon.WeaponName.FURNACE);
        shooter.addWeapon(furnace);
        //MAIN TARGETS
        assertEquals("[[Player 2 : anonymous(Banshee), Player 3 : anonymous(Dozer)]]",furnace.getFireModeList().get(0).findTargets().toString());
        //MAIN DESTINATIONS
        assertTrue(furnace.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(banshee))).isEmpty());
        //SECONDARY TARGETS
        assertEquals("[[Player 2 : anonymous(Banshee)]]",furnace.getFireModeList().get(1).findTargets().toString());
        //SECONDAY DESTINATIONS
        assertTrue(furnace.getFireModeList().get(1).findDestinations(new ArrayList<>(Arrays.asList(banshee))).isEmpty());
        //SECONDARY TARGETS shared square with shooter, no targets
        banshee.setPosition(shooter.getPosition());
        assertTrue(furnace.getFireModeList().get(1).findTargets().isEmpty());
        //SECONDARY TARGETS two targets in an adiacent square, one target in another adiacent square
        banshee.setPosition(b.getMap().get(1));
        dozer.setPosition(b.getMap().get(1));
        violet.setPosition(b.getMap().get(4));
        assertEquals("[[Player 2 : anonymous(Banshee), Player 3 : anonymous(Dozer)], [Player 4 : anonymous(Violet)]]",furnace.getFireModeList().get(1).findTargets().toString());
    }

    //plasma gun, rocket_launcher, grenade launcher

    @Test
    public void plasmaGun() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException{

        Board b = BoardConfigurer.simulateScenario();
        Player shooter = b.getPlayers().get(0);
        Player banshee = b.getPlayers().get(1);
        Player dozer = b.getPlayers().get(2);
        Player violet = b.getPlayers().get(3);
        Player sprog = b.getPlayers().get(4);
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon plasmaGun = weaponFactory.createWeapon(Weapon.WeaponName.PLASMA_GUN);
        shooter.addWeapon(plasmaGun);
        //MAIN TARGETS
        assertEquals("[[Player 2 : anonymous(Banshee)], [Player 3 : anonymous(Dozer)]]",plasmaGun.getFireModeList().get(0).findTargets().toString());
        //MAIN DESTINATIONS
        assertTrue(plasmaGun.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(banshee))).isEmpty());
        //OPTION 1 TARGETS
        assertEquals("[[Player 1 : anonymous(D_struct_or)]]",plasmaGun.getFireModeList().get(1).findTargets().toString());
        //OPTION 1 DESTINATIONS
        assertEquals(new ArrayList<>(Arrays.asList(b.getMap().get(1),b.getMap().get(2),b.getMap().get(5))),plasmaGun.getFireModeList().get(1).findDestinations(new ArrayList<>(Arrays.asList(shooter))));
        shooter.addMainTarget(banshee);
        assertEquals(new ArrayList<>(Arrays.asList(b.getMap().get(1),b.getMap().get(2),b.getMap().get(4),b.getMap().get(5),b.getMap().get(8))),plasmaGun.getFireModeList().get(1).findDestinations(new ArrayList<>(Arrays.asList(shooter))));
        //OPTION 2 TARGETS
        shooter.getMainTargets().remove(banshee);
        assertTrue(plasmaGun.getFireModeList().get(2).findTargets().isEmpty());
        shooter.addMainTarget(banshee);
        assertEquals("[[Player 2 : anonymous(Banshee)]]", plasmaGun.getFireModeList().get(2).findTargets().toString());
        //OPTION 2 DESTINATIONS
        assertTrue(plasmaGun.getFireModeList().get(2).findDestinations(new ArrayList<>(Arrays.asList(banshee))).isEmpty());

    }


}