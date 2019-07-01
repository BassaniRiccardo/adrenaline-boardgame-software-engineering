package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Player;
import it.polimi.ingsw.model.board.Square;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.exceptions.*;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

//TODO:  marcobaga: check and comment the tests at the beginning (before the weapon-specific).
//       Bassaniriccardo: remove yellow warnings .

/**
 * Tests all the weapons which can be created by the class WeaponFactory.
 *
 * @author BassaniRiccardo
 */

public class WeaponFactoryTest {


     @Test
     public void emptyWeaponTest() throws NoMoreCardsException, UnacceptableItemNumberException {
         Board b = BoardConfigurer.simulateScenario();
         WeaponFactory weaponFactory = new WeaponFactory(b);
         Weapon w = weaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE);
         assertEquals("Blue", w.getColor().toString());
     }

    /**
     * Creates the first weapon and checks that it is initialized correctly
     */
    @Test
    public void createWeapon() throws NoMoreCardsException, UnacceptableItemNumberException {
        Board b = BoardConfigurer.simulateScenario();
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon w = weaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE);
        assertSame(Weapon.WeaponName.LOCK_RIFLE, w.getWeaponName());

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
        assertEquals(Color.BLUE, w.getColor());
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
    }

    /**
     * Tests that the fire mode cost of a particular weapon is correct
     */
/*    @Test
    public void getFireModeCost() throws NoMoreCardsException, UnacceptableItemNumberException {
        Board b = BoardConfigurer.simulateScenario();
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon w = weaponFactory.createWeapon(Weapon.WeaponName.SHOCKWAVE);
        AmmoPack ammoPack = weaponFactory*.getFireModeCost(w.getWeaponName(), FireMode.FireModeName.SECONDARY);
        assertEquals(1,ammoPack.getYellowAmmo());
        assertEquals(0,ammoPack.getRedAmmo());
        assertEquals(0,ammoPack.getBlueAmmo());
    }
*/

    /**
     * Tests that all the weapon of the game can effectively be created
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     */
    @Test
    public void createAllWeapon() throws NoMoreCardsException, UnacceptableItemNumberException{
        Board b = BoardConfigurer.simulateScenario();
        WeaponFactory weaponFactory = new WeaponFactory(b);
        StringBuilder builder = new StringBuilder();
        for(Weapon.WeaponName weaponName : Weapon.WeaponName.values()){
            weaponFactory.createWeapon(weaponName);
            if (!builder.toString().isEmpty())
                builder.append(", ") ;
            builder.append(weaponName);
        }
        assertEquals("Lock Rifle, Machine Gun, Thor, Plasma Gun, Whisper, Electroscythe, Tractor Beam, Vortex Cannon, Furnace, Heatseeker, Hellion, Flamethrower, Grenade Launcher, Rocket Launcher, Railgun, Cyberblade, Zx2, Shotgun, Power Glove, Shockwave, Sledgehammer", builder.toString());
    }

    /*
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
    */


    /**
     * Tests the lock rifle in a game scenario, checking that targets and destinations are correct.
     *
     * @throws UnacceptableItemNumberException  if thrown by simulateScenario().
     * @throws NoMoreCardsException             if thrown by simulateScenario().
     * @throws NotAvailableAttributeException   if thrown by simulateScenario(), addWeapon(), findTagets() or findDestinations().
     */
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


    /**
     * Tests the machine gun in a game scenario, checking that targets and destinations are correct.
     *
     * @throws UnacceptableItemNumberException  if thrown by simulateScenario().
     * @throws NoMoreCardsException             if thrown by simulateScenario().
     * @throws NotAvailableAttributeException   if thrown by simulateScenario(), addWeapon(), findTagets() or findDestinations().
     */
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


    /**
     * Tests the T.H.O.R. in a game scenario, checking that targets and destinations are correct.
     *
     * @throws UnacceptableItemNumberException  if thrown by simulateScenario().
     * @throws NoMoreCardsException             if thrown by simulateScenario().
     * @throws NotAvailableAttributeException   if thrown by simulateScenario(), addWeapon(), findTagets() or findDestinations().
     */
    @Test
    public void thor() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException{

        Board b = BoardConfigurer.simulateScenario();
        Player shooter = b.getPlayers().get(0);
        Player banshee = b.getPlayers().get(1);
        Player dozer = b.getPlayers().get(2);
        Player violet = b.getPlayers().get(3);
        Player sprog = b.getPlayers().get(4);
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon thor = weaponFactory.createWeapon(Weapon.WeaponName.THOR);
        shooter.addWeapon(thor);
        //MAIN TARGETS
        assertEquals("[[Player 2 : anonymous(Banshee)], [Player 3 : anonymous(Dozer)]]",thor.getFireModeList().get(0).findTargets().toString());
        //MAIN DESTINATIONS
        assertTrue(thor.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(banshee))).isEmpty());
        //OPTION 1 TARGETS
        assertTrue(thor.getFireModeList().get(1).findTargets().isEmpty());
        shooter.addMainTarget(banshee);
        assertEquals("[[Player 3 : anonymous(Dozer)]]",thor.getFireModeList().get(1).findTargets().toString());
        //OPTION 1 DESTINATIONS
        assertTrue(thor.getFireModeList().get(1).findDestinations(new ArrayList<>(Arrays.asList(dozer))).isEmpty());
        //OPTION 2 TARGETS
        assertTrue(thor.getFireModeList().get(2).findTargets().isEmpty());
        shooter.addOptionalTarget(dozer);
        assertEquals("[[Player 4 : anonymous(Violet)], [Player 5 : anonymous(Sprog)]]",thor.getFireModeList().get(2).findTargets().toString());
        //OPTION 2 DESTINATIONS
        shooter.addOptionalTarget(sprog);
        assertTrue(thor.getFireModeList().get(2).findDestinations(new ArrayList<>(Arrays.asList(sprog))).isEmpty());

    }


    /**
     * Tests the plasma gun in a game scenario, checking that targets and destinations are correct.
     *
     * @throws UnacceptableItemNumberException  if thrown by simulateScenario().
     * @throws NoMoreCardsException             if thrown by simulateScenario().
     * @throws NotAvailableAttributeException   if thrown by simulateScenario(), addWeapon(), findTagets() or findDestinations().
     */
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


    /**
     * Tests the whisper in a game scenario, checking that targets and destinations are correct.
     *
     * @throws UnacceptableItemNumberException  if thrown by simulateScenario().
     * @throws NoMoreCardsException             if thrown by simulateScenario().
     * @throws NotAvailableAttributeException   if thrown by simulateScenario(), addWeapon(), findTagets() or findDestinations().
     */
    @Test
    public void whisper() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException{

        Board b = BoardConfigurer.simulateScenario();
        Player shooter = b.getPlayers().get(0);
        Player banshee = b.getPlayers().get(1);
        Player dozer = b.getPlayers().get(2);
        Player violet = b.getPlayers().get(3);
        Player sprog = b.getPlayers().get(4);
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon whisper = weaponFactory.createWeapon(Weapon.WeaponName.WHISPER);
        shooter.addWeapon(whisper);
        //MAIN TARGETS
        assertEquals("[[Player 3 : anonymous(Dozer)]]",whisper.getFireModeList().get(0).findTargets().toString());
        //MAIN DESTINATIONS
        assertTrue(whisper.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(dozer))).isEmpty());

    }

    /**
     * Tests the whisper in a game scenario, checking that targets and destinations are correct.
     *
     * @throws UnacceptableItemNumberException  if thrown by simulateScenario().
     * @throws NoMoreCardsException             if thrown by simulateScenario().
     * @throws NotAvailableAttributeException   if thrown by simulateScenario(), addWeapon(), findTagets() or findDestinations().
     */
    @Test
    public void whisper2() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException{

        Board b = BoardConfigurer.simulateScenario();
        Player shooter = b.getPlayers().get(0);
        Player banshee = b.getPlayers().get(1);
        Player dozer = b.getPlayers().get(2);
        Player violet = b.getPlayers().get(3);
        Player sprog = b.getPlayers().get(4);
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon whisper = weaponFactory.createWeapon(Weapon.WeaponName.WHISPER);
        dozer.addWeapon(whisper);

        //MAIN TARGETS
        assertEquals("[[Player 5 : anonymous(Sprog)]]",whisper.getFireModeList().get(0).findTargets().toString());
        //MAIN DESTINATIONS
        assertTrue(whisper.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(sprog))).isEmpty());

    }


    /**
     * Tests the electroscythe in a game scenario, checking that targets and destinations are correct.
     *
     * @throws UnacceptableItemNumberException  if thrown by simulateScenario().
     * @throws NoMoreCardsException             if thrown by simulateScenario().
     * @throws NotAvailableAttributeException   if thrown by simulateScenario(), addWeapon(), findTagets() or findDestinations().
     */
    @Test
    public void electroscythe() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException{

        Board b = BoardConfigurer.simulateScenario();
        Player shooter = b.getPlayers().get(0);
        Player banshee = b.getPlayers().get(1);
        Player dozer = b.getPlayers().get(2);
        Player violet = b.getPlayers().get(3);
        Player sprog = b.getPlayers().get(4);
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon electroscythe = weaponFactory.createWeapon(Weapon.WeaponName.ELECTROSCYTHE);
        shooter.addWeapon(electroscythe);
        //MAIN  AND SECONDARY TARGETS
        assertEquals("[[]]",electroscythe.getFireModeList().get(0).findTargets().toString());
        assertEquals("[[]]",electroscythe.getFireModeList().get(1).findTargets().toString());
        //MAIN AND SECONDARY DESTINATIONS
        assertTrue(electroscythe.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(dozer))).isEmpty());
        assertTrue(electroscythe.getFireModeList().get(1).findDestinations(new ArrayList<>(Arrays.asList(dozer))).isEmpty());

        shooter.setPosition(b.getMap().get(2));
        //MAIN AND SECONDARY TARGETS
        assertEquals("[[Player 3 : anonymous(Dozer)]]",electroscythe.getFireModeList().get(0).findTargets().toString());
        assertEquals("[[Player 3 : anonymous(Dozer)]]",electroscythe.getFireModeList().get(1).findTargets().toString());
        shooter.addMainTarget(dozer);
        //MAIN AND SECONDARY DESTINATIONS
        assertTrue(electroscythe.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(dozer))).isEmpty());
        assertTrue(electroscythe.getFireModeList().get(1).findDestinations(new ArrayList<>(Arrays.asList(dozer))).isEmpty());

    }


    /**
     * Tests the tractor beam in a game scenario, checking that targets and destinations are correct.
     *
     * @throws UnacceptableItemNumberException  if thrown by simulateScenario().
     * @throws NoMoreCardsException             if thrown by simulateScenario().
     * @throws NotAvailableAttributeException   if thrown by simulateScenario(), addWeapon(), findTagets() or findDestinations().
     */
    @Test
    public void tractorBeam() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException{

        Board b = BoardConfigurer.simulateScenario();
        Player shooter = b.getPlayers().get(0);
        Player banshee = b.getPlayers().get(1);
        Player dozer = b.getPlayers().get(2);
        Player violet = b.getPlayers().get(3);
        Player sprog = b.getPlayers().get(4);
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon tractorBeam = weaponFactory.createWeapon(Weapon.WeaponName.TRACTOR_BEAM);
        shooter.addWeapon(tractorBeam);
        //MAIN TARGETS
        assertEquals("[[Player 2 : anonymous(Banshee)], [Player 3 : anonymous(Dozer)], [Player 4 : anonymous(Violet)]]",tractorBeam.getFireModeList().get(0).findTargets().toString());
        //MAIN DESTINATIONS
        assertEquals("[Square 0, Square 1, Square 2, Square 4]", tractorBeam.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(banshee))).toString());
        assertEquals("[Square 0, Square 1, Square 2]", tractorBeam.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(dozer))).toString());
        assertEquals("[Square 1, Square 2]", tractorBeam.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(violet))).toString());
        //SECONDARY TARGETS
        assertEquals("[[Player 2 : anonymous(Banshee)], [Player 3 : anonymous(Dozer)]]",tractorBeam.getFireModeList().get(1).findTargets().toString());
        //SECONDAY DESTINATIONS
        assertEquals("[Square 0]", tractorBeam.getFireModeList().get(1).findDestinations(new ArrayList<>(Arrays.asList(banshee))).toString());
        assertEquals("[Square 0]", tractorBeam.getFireModeList().get(1).findDestinations(new ArrayList<>(Arrays.asList(dozer))).toString());        //SECONDARY TARGETS shared square with shooter, no targets
    }


    /**
     * Tests the vortex cannon in a game scenario, checking that targets and destinations are correct.
     *
     * @throws UnacceptableItemNumberException
     * @throws NoMoreCardsException
     * @throws NotAvailableAttributeException
     */
    @Test
    public void vortexCannon() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException{

        Board b = BoardConfigurer.simulateScenario();
        Player shooter = b.getPlayers().get(0);
        Player banshee = b.getPlayers().get(1);
        Player dozer = b.getPlayers().get(2);
        Player violet = b.getPlayers().get(3);
        Player sprog = b.getPlayers().get(4);
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon vortexCannon = weaponFactory.createWeapon(Weapon.WeaponName.VORTEX_CANNON);
        shooter.addWeapon(vortexCannon);
        //MAIN TARGETS
        assertEquals("[[Player 2 : anonymous(Banshee)], [Player 3 : anonymous(Dozer)], [Player 4 : anonymous(Violet)]]",vortexCannon.getFireModeList().get(0).findTargets().toString());
        //MAIN DESTINATIONS
        assertEquals("[Square 1, Square 2]", vortexCannon.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(banshee))).toString());
        assertEquals("[Square 1, Square 2]", vortexCannon.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(dozer))).toString());
        assertEquals("[Square 2]", vortexCannon.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(violet))).toString());        //OPTION 1 TARGETS
        //OPTION 1 TARGETS
        assertTrue(vortexCannon.getFireModeList().get(1).findTargets().isEmpty());
        vortexCannon.getFireModeList().get(0).applyEffects(Arrays.asList(dozer), b.getMap().get(2));
        shooter.addMainTarget(dozer);
        assertEquals("[[Player 2 : anonymous(Banshee), Player 4 : anonymous(Violet)], [Player 2 : anonymous(Banshee)], [Player 4 : anonymous(Violet)]]",vortexCannon.getFireModeList().get(1).findTargets().toString());
        //OPTION 1 DESTINATIONS
        assertEquals("[Square 2]", vortexCannon.getFireModeList().get(1).findDestinations(new ArrayList<>(Arrays.asList(banshee))).toString());        //OPTION 1 TARGETS
        assertEquals("[Square 2]", vortexCannon.getFireModeList().get(1).findDestinations(new ArrayList<>(Arrays.asList(violet))).toString());        //OPTION 1 TARGETS

    }


    /**
     * Tests the furnace in a game scenario, checking that targets and destinations are correct.
     *
     * @throws UnacceptableItemNumberException  if thrown by simulateScenario().
     * @throws NoMoreCardsException             if thrown by simulateScenario().
     * @throws NotAvailableAttributeException   if thrown by simulateScenario(), addWeapon(), findTagets() or findDestinations().
     */
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


    /**
     * Tests the heatseeker in a game scenario, checking that targets and destinations are correct.

     * @throws UnacceptableItemNumberException  if thrown by simulateScenario().
     * @throws NoMoreCardsException             if thrown by simulateScenario().
     * @throws NotAvailableAttributeException   if thrown by simulateScenario(), addWeapon(), findTagets() or findDestinations().
     */
    @Test
    public void heatseeker() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException{

        Board b = BoardConfigurer.simulateScenario();
        Player shooter = b.getPlayers().get(0);
        Player banshee = b.getPlayers().get(1);
        Player dozer = b.getPlayers().get(2);
        Player violet = b.getPlayers().get(3);
        Player sprog = b.getPlayers().get(4);
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon heatseeker = weaponFactory.createWeapon(Weapon.WeaponName.HEATSEEKER);
        shooter.addWeapon(heatseeker);
        //MAIN TARGETS
        assertEquals("[[Player 4 : anonymous(Violet)], [Player 5 : anonymous(Sprog)]]",heatseeker.getFireModeList().get(0).findTargets().toString());
        //MAIN DESTINATIONS
        assertTrue(heatseeker.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(violet))).isEmpty());
        assertTrue(heatseeker.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(sprog))).isEmpty());

    }


    /**
     * Tests the hellion in a game scenario, checking that targets and destinations are correct.
     *
     * @throws UnacceptableItemNumberException  if thrown by simulateScenario().
     * @throws NoMoreCardsException             if thrown by simulateScenario().
     * @throws NotAvailableAttributeException   if thrown by simulateScenario(), addWeapon(), findTagets() or findDestinations().
     */
    @Test
    public void hellion() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException{

        Board b = BoardConfigurer.simulateScenario();
        Player shooter = b.getPlayers().get(0);
        Player banshee = b.getPlayers().get(1);
        Player dozer = b.getPlayers().get(2);
        Player violet = b.getPlayers().get(3);
        Player sprog = b.getPlayers().get(4);
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon hellion = weaponFactory.createWeapon(Weapon.WeaponName.HELLION);
        shooter.addWeapon(hellion);
        //MAIN AND SECONDARY TARGETS
        assertEquals("[[Player 2 : anonymous(Banshee)], [Player 3 : anonymous(Dozer)]]",hellion.getFireModeList().get(0).findTargets().toString());
        assertEquals("[[Player 2 : anonymous(Banshee)], [Player 3 : anonymous(Dozer)]]",hellion.getFireModeList().get(1).findTargets().toString());
        //MAIN  AND SECONDARY DESTINATIONS
        assertTrue(hellion.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(banshee))).isEmpty());
        assertTrue(hellion.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(dozer))).isEmpty());
        assertTrue(hellion.getFireModeList().get(1).findDestinations(new ArrayList<>(Arrays.asList(banshee))).isEmpty());
        assertTrue(hellion.getFireModeList().get(1).findDestinations(new ArrayList<>(Arrays.asList(dozer))).isEmpty());
    }


    /**
     * Tests the flamethrower in a game scenario, checking that targets and destinations are correct.
     *
     * @throws UnacceptableItemNumberException  if thrown by simulateScenario().
     * @throws NoMoreCardsException             if thrown by simulateScenario().
     * @throws NotAvailableAttributeException   if thrown by simulateScenario(), addWeapon(), findTagets() or findDestinations().
     */

    @Test
    public void flamethrower() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException{

        Board b = BoardConfigurer.simulateScenario();
        Player shooter = b.getPlayers().get(0);
        Player banshee = b.getPlayers().get(1);
        Player dozer = b.getPlayers().get(2);
        Player violet = b.getPlayers().get(3);
        Player sprog = b.getPlayers().get(4);
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon flamethrower = weaponFactory.createWeapon(Weapon.WeaponName.FLAMETHROWER);
        shooter.addWeapon(flamethrower);
        sprog.setPosition(b.getMap().get(1));
        //MAIN TARGETS
        assertEquals("[[Player 2 : anonymous(Banshee)], [Player 5 : anonymous(Sprog)], [Player 3 : anonymous(Dozer)], [Player 2 : anonymous(Banshee), Player 3 : anonymous(Dozer)], [Player 5 : anonymous(Sprog), Player 3 : anonymous(Dozer)]]",flamethrower.getFireModeList().get(0).findTargets().toString());
        //MAIN DESTINATIONS
        assertTrue(flamethrower.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(banshee))).isEmpty());
        assertTrue(flamethrower.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(sprog))).isEmpty());
        assertTrue(flamethrower.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(dozer))).isEmpty());
        //SECONDARY TARGETS
        assertEquals("[[Player 2 : anonymous(Banshee), Player 5 : anonymous(Sprog), Player 3 : anonymous(Dozer)]]",flamethrower.getFireModeList().get(1).findTargets().toString());
        //SECONDAY DESTINATIONS
        assertTrue(flamethrower.getFireModeList().get(1).findDestinations(new ArrayList<>(Arrays.asList(banshee))).isEmpty());
        assertTrue(flamethrower.getFireModeList().get(1).findDestinations(new ArrayList<>(Arrays.asList(sprog))).isEmpty());
        assertTrue(flamethrower.getFireModeList().get(1).findDestinations(new ArrayList<>(Arrays.asList(dozer))).isEmpty());
    }

    /**
     * Tests the grenade launcher in a game scenario, checking that targets and destinations are correct.
     *
     * @throws UnacceptableItemNumberException  if thrown by simulateScenario().
     * @throws NoMoreCardsException             if thrown by simulateScenario().
     * @throws NotAvailableAttributeException   if thrown by simulateScenario(), addWeapon(), findTagets() or findDestinations().
     */
    @Test
    public void grenadeLauncher() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException{

        Board b = BoardConfigurer.simulateScenario();
        Player shooter = b.getPlayers().get(0);
        Player banshee = b.getPlayers().get(1);
        Player dozer = b.getPlayers().get(2);
        Player violet = b.getPlayers().get(3);
        Player sprog = b.getPlayers().get(4);
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon grenadeLauncher = weaponFactory.createWeapon(Weapon.WeaponName.GRENADE_LAUNCHER);
        shooter.addWeapon(grenadeLauncher);
        //MAIN TARGETS
        assertEquals("[[Player 2 : anonymous(Banshee)], [Player 3 : anonymous(Dozer)]]",grenadeLauncher.getFireModeList().get(0).findTargets().toString());
        //MAIN DESTINATIONS
        assertEquals("[Square 0, Square 1, Square 2, Square 5]", grenadeLauncher.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(banshee))).toString());
        assertEquals("[Square 1, Square 2, Square 3, Square 6]", grenadeLauncher.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(dozer))).toString());
        //OPTION 1 TARGETS
        shooter.addMainTarget(dozer);
        assertEquals("[[Player 2 : anonymous(Banshee)], [Player 3 : anonymous(Dozer)]]",grenadeLauncher.getFireModeList().get(1).findTargets().toString());
        dozer.setPosition(b.getMap().get(1));
        assertEquals("[[Player 2 : anonymous(Banshee), Player 3 : anonymous(Dozer)]]",grenadeLauncher.getFireModeList().get(1).findTargets().toString());
        //OPTION 1 DESTINATIONS
        assertTrue(grenadeLauncher.getFireModeList().get(1).findDestinations(new ArrayList<>(Arrays.asList(banshee))).isEmpty());        //OPTION 1 TARGETS
        assertTrue(grenadeLauncher.getFireModeList().get(1).findDestinations(new ArrayList<>(Arrays.asList(violet))).isEmpty());        //OPTION 1 TARGETS

    }




    /**
     * Tests the rocket launcher in a game scenario, checking that targets and destinations are correct.
     *
     * @throws UnacceptableItemNumberException  if thrown by simulateScenario().
     * @throws NoMoreCardsException             if thrown by simulateScenario().
     * @throws NotAvailableAttributeException   if thrown by simulateScenario(), addWeapon(), findTagets() or findDestinations().
     */
    @Test
    public void rocketLauncher() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException{

        Board b = BoardConfigurer.simulateScenario();
        Player shooter = b.getPlayers().get(0);
        Player banshee = b.getPlayers().get(1);
        Player dozer = b.getPlayers().get(2);
        Player violet = b.getPlayers().get(3);
        Player sprog = b.getPlayers().get(4);
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon rocketLauncher = weaponFactory.createWeapon(Weapon.WeaponName.ROCKET_LAUNCHER);
        shooter.addWeapon(rocketLauncher);
        sprog.setPosition(b.getMap().get(1));
        //MAIN TARGETS
        assertEquals("[[Player 2 : anonymous(Banshee)], [Player 5 : anonymous(Sprog)], [Player 3 : anonymous(Dozer)]]",rocketLauncher.getFireModeList().get(0).findTargets().toString());
        //MAIN DESTINATIONS
        assertEquals("[Square 0, Square 1, Square 2, Square 5]", rocketLauncher.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(banshee))).toString());
        assertEquals("[Square 0, Square 1, Square 2, Square 5]", rocketLauncher.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(sprog))).toString());
        assertEquals("[Square 1, Square 2, Square 3, Square 6]", rocketLauncher.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(dozer))).toString());
        //OPTION 1 TARGETS
        assertEquals("[[Player 1 : anonymous(D_struct_or)]]",rocketLauncher.getFireModeList().get(1).findTargets().toString());
        //OPTION 1 DESTINATIONS
        assertEquals("[Square 1, Square 2, Square 5]", rocketLauncher.getFireModeList().get(1).findDestinations(new ArrayList<>(Arrays.asList(shooter))).toString());
        shooter.addMainTarget(banshee);
        assertEquals("[Square 1, Square 2, Square 4, Square 5, Square 8]", rocketLauncher.getFireModeList().get(1).findDestinations(new ArrayList<>(Arrays.asList(shooter))).toString());
        //OPTION 2 TARGETS
        assertEquals("[[Player 2 : anonymous(Banshee), Player 5 : anonymous(Sprog)]]",rocketLauncher.getFireModeList().get(2).findTargets().toString());
        //OPTION 2 DESTINATIONS
        assertTrue(rocketLauncher.getFireModeList().get(2).findDestinations(new ArrayList<>(Arrays.asList(banshee))).isEmpty());
        assertTrue(rocketLauncher.getFireModeList().get(2).findDestinations(new ArrayList<>(Arrays.asList(sprog))).isEmpty());

    }


    /**
     * Tests the railgun in a game scenario, checking that targets and destinations are correct.
     *
     * @throws UnacceptableItemNumberException  if thrown by simulateScenario().
     * @throws NoMoreCardsException             if thrown by simulateScenario().
     * @throws NotAvailableAttributeException   if thrown by simulateScenario(), addWeapon(), findTagets() or findDestinations().
     */
    @Test
    public void railgun() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException{

        Board b = BoardConfigurer.simulateScenario();
        Player shooter = b.getPlayers().get(0);
        Player banshee = b.getPlayers().get(1);
        Player dozer = b.getPlayers().get(2);
        Player violet = b.getPlayers().get(3);
        Player sprog = b.getPlayers().get(4);
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon railgun = weaponFactory.createWeapon(Weapon.WeaponName.RAILGUN);
        shooter.addWeapon(railgun);
        shooter.setPosition(b.getMap().get(4));
        banshee.setPosition(b.getMap().get(5));
        dozer.setPosition(b.getMap().get(6));
        //MAIN TARGETS
        assertEquals("[[Player 2 : anonymous(Banshee)], [Player 3 : anonymous(Dozer)]]",railgun.getFireModeList().get(0).findTargets().toString());
        //MAIN DESTINATIONS
        assertTrue(railgun.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(banshee))).isEmpty());
        assertTrue(railgun.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(dozer))).isEmpty());
        //SECONDARY TARGETS
        assertEquals("[[Player 2 : anonymous(Banshee)], [Player 3 : anonymous(Dozer)], [Player 2 : anonymous(Banshee), Player 3 : anonymous(Dozer)]]",railgun.getFireModeList().get(1).findTargets().toString());
        //SECONDAY DESTINATIONS
        assertTrue(railgun.getFireModeList().get(1).findDestinations(new ArrayList<>(Arrays.asList(banshee))).isEmpty());
        assertTrue(railgun.getFireModeList().get(1).findDestinations(new ArrayList<>(Arrays.asList(dozer))).isEmpty());
    }


    /**
     * Tests the cyberblade in a game scenario, checking that targets and destinations are correct.
     *
     * @throws UnacceptableItemNumberException  if thrown by simulateScenario().
     * @throws NoMoreCardsException             if thrown by simulateScenario().
     * @throws NotAvailableAttributeException   if thrown by simulateScenario(), addWeapon(), findTagets() or findDestinations().
     */
    @Test
    public void cyberblade() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException{

        Board b = BoardConfigurer.simulateScenario();
        Player shooter = b.getPlayers().get(0);
        Player banshee = b.getPlayers().get(1);
        Player dozer = b.getPlayers().get(2);
        Player violet = b.getPlayers().get(3);
        Player sprog = b.getPlayers().get(4);
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon cyberblade = weaponFactory.createWeapon(Weapon.WeaponName.CYBERBLADE);
        shooter.addWeapon(cyberblade);
        sprog.setPosition(b.getMap().get(1));
        //OPTION 1 TARGETS
        assertEquals("[[Player 1 : anonymous(D_struct_or)]]",cyberblade.getFireModeList().get(1).findTargets().toString());
        //OPTION 1 DESTINATIONS
        assertEquals("[Square 1]", cyberblade.getFireModeList().get(1).findDestinations(new ArrayList<>(Arrays.asList(shooter))).toString());
        shooter.addMainTarget(banshee);
        assertEquals("[Square 1, Square 4]", cyberblade.getFireModeList().get(1).findDestinations(new ArrayList<>(Arrays.asList(shooter))).toString());
        shooter.setPosition(b.getMap().get(1));
        //MAIN TARGETS
        assertEquals("[[Player 2 : anonymous(Banshee)], [Player 5 : anonymous(Sprog)]]",cyberblade.getFireModeList().get(0).findTargets().toString());
        //MAIN DESTINATIONS
        assertTrue(cyberblade.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(banshee))).isEmpty());
        //OPTION 2 TARGETS
        assertEquals("[[Player 5 : anonymous(Sprog)]]",cyberblade.getFireModeList().get(2).findTargets().toString());
        //OPTION 2 DESTINATIONS
        assertTrue(cyberblade.getFireModeList().get(2).findDestinations(new ArrayList<>(Arrays.asList(sprog))).isEmpty());

    }


    /**
     * Tests the zx-2 in a game scenario, checking that targets and destinations are correct.
     *
     * @throws UnacceptableItemNumberException  if thrown by simulateScenario().
     * @throws NoMoreCardsException             if thrown by simulateScenario().
     * @throws NotAvailableAttributeException   if thrown by simulateScenario(), addWeapon(), findTagets() or findDestinations().
     */
    @Test
    public void zx2() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException{

        Board b = BoardConfigurer.simulateScenario();
        Player shooter = b.getPlayers().get(0);
        Player banshee = b.getPlayers().get(1);
        Player dozer = b.getPlayers().get(2);
        Player violet = b.getPlayers().get(3);
        Player sprog = b.getPlayers().get(4);
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon zx2 = weaponFactory.createWeapon(Weapon.WeaponName.ZX2);
        shooter.addWeapon(zx2);
        //MAIN TARGETS
        assertEquals("[[Player 2 : anonymous(Banshee)], [Player 3 : anonymous(Dozer)]]",zx2.getFireModeList().get(0).findTargets().toString());
        //MAIN DESTINATIONS
        assertTrue(zx2.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(banshee))).isEmpty());
        //SECONDARY TARGETS
        assertEquals("[[Player 2 : anonymous(Banshee)], [Player 3 : anonymous(Dozer)], [Player 2 : anonymous(Banshee), Player 3 : anonymous(Dozer)]]",zx2.getFireModeList().get(1).findTargets().toString());
        //SECONDAY DESTINATIONS
        assertTrue(zx2.getFireModeList().get(1).findDestinations(new ArrayList<>(Arrays.asList(banshee))).isEmpty());
        assertTrue(zx2.getFireModeList().get(1).findDestinations(new ArrayList<>(Arrays.asList(dozer))).isEmpty());
    }


    /**
     * Tests the shotgun in a game scenario, checking that targets and destinations are correct.
     *
     * @throws UnacceptableItemNumberException  if thrown by simulateScenario().
     * @throws NoMoreCardsException             if thrown by simulateScenario().
     * @throws NotAvailableAttributeException   if thrown by simulateScenario(), addWeapon(), findTagets() or findDestinations().
     */
    @Test
    public void shotgun() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException{

        Board b = BoardConfigurer.simulateScenario();
        Player shooter = b.getPlayers().get(0);
        Player banshee = b.getPlayers().get(1);
        Player dozer = b.getPlayers().get(2);
        Player violet = b.getPlayers().get(3);
        Player sprog = b.getPlayers().get(4);
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon shotgun = weaponFactory.createWeapon(Weapon.WeaponName.SHOTGUN);
        shooter.addWeapon(shotgun);
        shooter.setPosition(b.getMap().get(2));
        //MAIN TARGETS
        assertEquals("[[Player 3 : anonymous(Dozer)]]",shotgun.getFireModeList().get(0).findTargets().toString());
        //MAIN DESTINATIONS
        assertEquals("[Square 1, Square 2, Square 3, Square 6]", shotgun.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(shooter))).toString());
        //SECONDARY TARGETS
        assertEquals("[[Player 2 : anonymous(Banshee)], [Player 4 : anonymous(Violet)]]",shotgun.getFireModeList().get(1).findTargets().toString());
        //SECONDAY DESTINATIONS
        assertTrue(shotgun.getFireModeList().get(1).findDestinations(new ArrayList<>(Arrays.asList(banshee))).isEmpty());
        assertTrue(shotgun.getFireModeList().get(1).findDestinations(new ArrayList<>(Arrays.asList(violet))).isEmpty());
    }


    /**
     * Tests the power glove in a game scenario, checking that targets and destinations are correct.
     *
     * @throws UnacceptableItemNumberException  if thrown by simulateScenario().
     * @throws NoMoreCardsException             if thrown by simulateScenario().
     * @throws NotAvailableAttributeException   if thrown by simulateScenario(), addWeapon(), findTagets() or findDestinations().
     */
    @Test
    public void powerglove() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException{

        Board b = BoardConfigurer.simulateScenario();
        Player shooter = b.getPlayers().get(0);
        Player banshee = b.getPlayers().get(1);
        Player dozer = b.getPlayers().get(2);
        Player violet = b.getPlayers().get(3);
        Player sprog = b.getPlayers().get(4);
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon powerglove = weaponFactory.createWeapon(Weapon.WeaponName.POWER_GLOVE);
        shooter.addWeapon(powerglove);
        shooter.setPosition(b.getMap().get(2));
        sprog.setPosition(b.getMap().get(0));

        //MAIN TARGETS
        assertEquals("[[Player 2 : anonymous(Banshee)], [Player 4 : anonymous(Violet)]]", powerglove.getFireModeList().get(0).findTargets().toString());
        //MAIN DESTINATIONS
        assertFalse(powerglove.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(banshee))).isEmpty());
        assertFalse(powerglove.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(violet))).isEmpty());
        //SECONDARY TARGETS
        assertEquals("[[Player 4 : anonymous(Violet)], [Player 2 : anonymous(Banshee)], [Player 5 : anonymous(Sprog)], [Player 2 : anonymous(Banshee), Player 5 : anonymous(Sprog)]]",powerglove.getFireModeList().get(1).findTargets().toString());
        //SECONDAY DESTINATIONS
        assertFalse(powerglove.getFireModeList().get(1).findDestinations(new ArrayList<>(Arrays.asList(banshee))).isEmpty());
        assertFalse(powerglove.getFireModeList().get(1).findDestinations(new ArrayList<>(Arrays.asList(violet))).isEmpty());
        assertFalse(powerglove.getFireModeList().get(1).findDestinations(new ArrayList<>(Arrays.asList(sprog))).isEmpty());

    }


    /**
     * Tests the shockwave in a game scenario, checking that targets and destinations are correct.
     *
     * @throws UnacceptableItemNumberException  if thrown by simulateScenario().
     * @throws NoMoreCardsException             if thrown by simulateScenario().
     * @throws NotAvailableAttributeException   if thrown by simulateScenario(), addWeapon(), findTagets() or findDestinations().
     */
    @Test
    public void shockwave() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException{

        Board b = BoardConfigurer.simulateScenario();
        Player shooter = b.getPlayers().get(0);
        Player banshee = b.getPlayers().get(1);
        Player dozer = b.getPlayers().get(2);
        Player violet = b.getPlayers().get(3);
        Player sprog = b.getPlayers().get(4);
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon shockwave = weaponFactory.createWeapon(Weapon.WeaponName.SHOCKWAVE);
        shooter.addWeapon(shockwave);
        shooter.setPosition(b.getMap().get(6));
        banshee.setPosition(b.getMap().get(10));
        violet.setPosition(b.getMap().get(7));
        sprog.setPosition(b.getMap().get(7));
        //MAIN TARGETS
        assertEquals("[[Player 4 : anonymous(Violet)], [Player 5 : anonymous(Sprog)], [Player 4 : anonymous(Violet), Player 3 : anonymous(Dozer)], [Player 5 : anonymous(Sprog), Player 3 : anonymous(Dozer)], [Player 4 : anonymous(Violet), Player 3 : anonymous(Dozer), Player 2 : anonymous(Banshee)], [Player 5 : anonymous(Sprog), Player 3 : anonymous(Dozer), Player 2 : anonymous(Banshee)], [Player 4 : anonymous(Violet), Player 2 : anonymous(Banshee)], [Player 5 : anonymous(Sprog), Player 2 : anonymous(Banshee)], [Player 3 : anonymous(Dozer)], [Player 3 : anonymous(Dozer), Player 2 : anonymous(Banshee)], [Player 2 : anonymous(Banshee)]]",
                shockwave.getFireModeList().get(0).findTargets().toString());
        //MAIN DESTINATIONS
        assertTrue(shockwave.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(banshee))).isEmpty());
        assertTrue(shockwave.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(dozer))).isEmpty());
        assertTrue(shockwave.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(violet))).isEmpty());
        assertTrue(shockwave.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(sprog))).isEmpty());
        //SECONDARY TARGETS
        assertEquals("[[Player 3 : anonymous(Dozer), Player 4 : anonymous(Violet), Player 5 : anonymous(Sprog), Player 2 : anonymous(Banshee)]]",shockwave.getFireModeList().get(1).findTargets().toString());
        //SECONDAY DESTINATIONS
        assertTrue(shockwave.getFireModeList().get(1).findDestinations(new ArrayList<>(Arrays.asList(banshee))).isEmpty());
        assertTrue(shockwave.getFireModeList().get(1).findDestinations(new ArrayList<>(Arrays.asList(dozer))).isEmpty());
        assertTrue(shockwave.getFireModeList().get(1).findDestinations(new ArrayList<>(Arrays.asList(violet))).isEmpty());
        assertTrue(shockwave.getFireModeList().get(1).findDestinations(new ArrayList<>(Arrays.asList(sprog))).isEmpty());
    }


    /**
     * Tests the sledgehammer in a game scenario, checking that targets and destinations are correct.
     *
     * @throws UnacceptableItemNumberException  if thrown by simulateScenario().
     * @throws NoMoreCardsException             if thrown by simulateScenario().
     * @throws NotAvailableAttributeException   if thrown by simulateScenario(), addWeapon(), findTagets() or findDestinations().
     */
    @Test
    public void sledgehammer() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException{

        Board b = BoardConfigurer.simulateScenario();
        Player shooter = b.getPlayers().get(0);
        Player banshee = b.getPlayers().get(1);
        Player dozer = b.getPlayers().get(2);
        Player violet = b.getPlayers().get(3);
        Player sprog = b.getPlayers().get(4);
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon sledgehammer = weaponFactory.createWeapon(Weapon.WeaponName.SLEDGEHAMMER);
        shooter.addWeapon(sledgehammer);
        shooter.setPosition(b.getMap().get(1));
        //MAIN TARGETS
        assertEquals("[[Player 2 : anonymous(Banshee)]]", sledgehammer.getFireModeList().get(0).findTargets().toString());
        //MAIN DESTINATIONS
        assertTrue(sledgehammer.getFireModeList().get(0).findDestinations(new ArrayList<>(Arrays.asList(banshee))).isEmpty());
        //SECONDARY TARGETS
        assertEquals("[[Player 2 : anonymous(Banshee)]]", sledgehammer.getFireModeList().get(1).findTargets().toString());
        //SECONDAY DESTINATIONS
        assertEquals("[Square 1, Square 2, Square 3, Square 0, Square 5, Square 9]", sledgehammer.getFireModeList().get(1).findDestinations(new ArrayList<>(Arrays.asList(banshee))).toString());
    }
}