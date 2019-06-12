package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Player;
import it.polimi.ingsw.model.board.Square;
import it.polimi.ingsw.model.board.WeaponSquare;
import it.polimi.ingsw.model.cards.AmmoPack;
import it.polimi.ingsw.model.cards.PowerUp;
import it.polimi.ingsw.model.cards.Weapon;
import it.polimi.ingsw.model.exceptions.NoMoreCardsException;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;
import it.polimi.ingsw.model.exceptions.UnacceptableItemNumberException;
import it.polimi.ingsw.model.exceptions.WrongTimeException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class StatusSaverTest {


    /**
     * Tests the method updateCheckpoint, checking whether the player positions are correctly saved.
     *
     * @throws UnacceptableItemNumberException
     * @throws NoMoreCardsException
     * @throws NotAvailableAttributeException
     */
    @Test
    public void updateCheckpointPositions() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        Board b = BoardConfigurer.simulateScenario();
        StatusSaver ss = new StatusSaver(b);
        ss.updateCheckpoint();

        List<Square> inModel = new ArrayList<>();
        for (Player p : b.getActivePlayers()){
            inModel.add(p.getPosition());
        }
        assertEquals(inModel, ss.getPlayersPositions());
    }

    /**
     * Tests the method updateCheckpoint, checking whether the players' damages are correctly saved.
     *
     * @throws UnacceptableItemNumberException
     * @throws NoMoreCardsException
     */
    @Test
    public void updateCheckpointDamages() throws UnacceptableItemNumberException, NoMoreCardsException {
        Board b = BoardConfigurer.simulateScenario();
        StatusSaver ss = new StatusSaver(b);
        b.getPlayers().get(0).sufferDamage(4, b.getPlayers().get(3));
        ss.updateCheckpoint();

        List<List<Player>> inModel = new ArrayList<>();
        for (Player p : b.getActivePlayers()){
            inModel.add(p.getDamages());
        }
        assertEquals(inModel, ss.getPlayersDamages());
    }

    /**
     * Tests the method updateCheckpoint, checking whether the players' powerups are correctly saved.
     *
     * @throws UnacceptableItemNumberException
     * @throws NoMoreCardsException
     * @throws WrongTimeException
     */
    @Test
    public void updateCheckpointPowerUps() throws UnacceptableItemNumberException, NoMoreCardsException, WrongTimeException {
        Board b = BoardConfigurer.simulateScenario();
        StatusSaver ss = new StatusSaver(b);
        b.getPlayers().get(2).drawPowerUp();
        ss.updateCheckpoint();

        List<List<PowerUp>> inModel = new ArrayList<>();
        for (Player p : b.getActivePlayers()){
            inModel.add(p.getPowerUpList());
        }
        assertEquals(inModel, ss.getPlayersPowerups());
    }

    /**
     * Tests the method updateCheckpoint, checking whether the players' ammo are correctly saved.
     *
     * @throws UnacceptableItemNumberException
     * @throws NoMoreCardsException
     */
    @Test
    public void updateCheckpointAmmo() throws UnacceptableItemNumberException, NoMoreCardsException {
        Board b = BoardConfigurer.simulateScenario();
        StatusSaver ss = new StatusSaver(b);
        b.getPlayers().get(0).useAmmo(new AmmoPack(1, 0, 0));
        ss.updateCheckpoint();

        for (Player p : b.getActivePlayers()) {
            assertEquals(p.getAmmoPack().getRedAmmo(), ss.getPlayersAmmoPacks().get(b.getActivePlayers().indexOf(p)).getRedAmmo());
            assertEquals(p.getAmmoPack().getBlueAmmo(), ss.getPlayersAmmoPacks().get(b.getActivePlayers().indexOf(p)).getBlueAmmo());
            assertEquals(p.getAmmoPack().getYellowAmmo(), ss.getPlayersAmmoPacks().get(b.getActivePlayers().indexOf(p)).getYellowAmmo());

        }

    }

    /**
     * Tests the method updateCheckpoint, checking whether the current player's weapons are correctly saved.
     *
     * @throws UnacceptableItemNumberException
     * @throws NoMoreCardsException
     * @throws WrongTimeException
     */
    @Test
    public void updateCheckpointCurrentWeapons() throws UnacceptableItemNumberException, NoMoreCardsException, WrongTimeException {
        Board b = BoardConfigurer.simulateScenario();
        StatusSaver ss = new StatusSaver(b);
        b.getPlayers().get(1).setPosition(b.getSpawnPoints().get(1));
        b.getPlayers().get(1).collect(b.getSpawnPoints().get(1).getWeapons().get(0));
        ss.updateCheckpoint();

        List<Weapon> inModel = new ArrayList<>();
        inModel.addAll(b.getCurrentPlayer().getWeaponList());
        assertEquals(inModel, ss.getCurrentPlayerWeapons());
    }


    /**
     * Tests the method updateCheckpoint, checking whether the current player's loaded weapons are correctly saved.
     *
     * @throws UnacceptableItemNumberException
     * @throws NoMoreCardsException
     */
    @Test
    public void updateCheckpointCurrentLoadedWeapons() throws UnacceptableItemNumberException, NoMoreCardsException {
        Board b = BoardConfigurer.simulateScenario();
        StatusSaver ss = new StatusSaver(b);
        WeaponFactory wf =  new WeaponFactory(b);
        Player p1 = b.getPlayers().get(0);
        Weapon lockRifle = wf.createWeapon(Weapon.WeaponName.LOCK_RIFLE);
        lockRifle.setLoaded(true);
        p1.addWeapon(lockRifle);
        Weapon electroscythe = wf.createWeapon(Weapon.WeaponName.ELECTROSCYTHE);
        electroscythe.setLoaded(false);
        p1.addWeapon(electroscythe);
        ss.updateCheckpoint();

        List<Boolean> inModel = new ArrayList<>();
        for (Weapon w : b.getCurrentPlayer().getWeaponList()) inModel.add(w.isLoaded());
        assertEquals(inModel, ss.getCurrentPlayerLoadedWeapons());
    }

    /**
     * Tests the method updateCheckpoint, checking whether the weapons in the spawn points are correctly saved.
     *
     * @throws UnacceptableItemNumberException
     * @throws NoMoreCardsException
     * @throws WrongTimeException
     */
    @Test
    public void updateCheckpointSquareWeapons() throws UnacceptableItemNumberException, NoMoreCardsException, WrongTimeException {
        Board b = BoardConfigurer.simulateScenario();
        StatusSaver ss = new StatusSaver(b);
        Player p1 = b.getPlayers().get(0);
        WeaponSquare sp = b.getSpawnPoints().get(0);
        p1.setPosition(sp);
        Weapon w3 = sp.getWeapons().get(2);
        p1.collect(w3);
        ss.updateCheckpoint();

        List<List<Weapon>> inModel = new ArrayList<>();
        for (WeaponSquare w : b.getSpawnPoints()){
            inModel.add(w.getWeapons());
        }
        assertEquals(inModel, ss.getSquareWeapons());
    }


    /**
     * Tests the method updatePowerups
     *
     * @throws UnacceptableItemNumberException
     * @throws NoMoreCardsException
     */
    @Test
    public void updatePowerups() throws UnacceptableItemNumberException, NoMoreCardsException {
        Board b = BoardConfigurer.simulateScenario();
        StatusSaver ss = new StatusSaver(b);
        ss.updatePowerups();

        List<List<PowerUp>> inModel = new ArrayList<>();
        for (Player p : b.getActivePlayers()){
            inModel.add(p.getPowerUpList());
        }
        assertEquals(inModel, ss.getPlayersPowerups());
    }

    /**
     * Tests the methods restoreCheckpoint, checking whether the players' positions are correctly restored.
     *
     * @throws UnacceptableItemNumberException
     * @throws NoMoreCardsException
     * @throws NotAvailableAttributeException
     */
    @Test
    public void restoreCheckpointPositions() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException{
        Board b = BoardConfigurer.simulateScenario();
        StatusSaver ss = new StatusSaver(b);
        ss.updateCheckpoint();
        Player p1 = b.getPlayers().get(0);
        Square s5 = b.getMap().get(5);
        p1.setPosition(s5);

        List<Square> inModel = new ArrayList<>();
        for (Player p : b.getActivePlayers()){
            inModel.add(p.getPosition());
        }
        assertNotEquals(inModel, ss.getPlayersPositions());

        ss.restoreCheckpoint();
        List<Square> inModelRestored = new ArrayList<>();
        for (Player p : b.getActivePlayers()){
            inModelRestored.add(p.getPosition());
        }
        assertEquals(ss.getPlayersPositions(), inModelRestored);
    }


    /**
     * Tests the methods restoreCheckpoint, checking whether the players' damages are correctly restored.
     *
     * @throws UnacceptableItemNumberException
     * @throws NoMoreCardsException
     */
    @Test
    public void restoreCheckpointDamages() throws UnacceptableItemNumberException, NoMoreCardsException{
        Board b = BoardConfigurer.simulateScenario();
        StatusSaver ss = new StatusSaver(b);
        ss.updateCheckpoint();
        Player p1 = b.getPlayers().get(0);
        Player p2 = b.getPlayers().get(1);
        p1.sufferDamage(3, p2);

        assertEquals(3, p1.getDamages().size());

        ss.restoreCheckpoint();
        assertEquals(0, p1.getDamages().size());
    }

    /**
     * Tests the methods restoreCheckpoint, checking whether the players' powerups are correctly restored.
     *
     * @throws UnacceptableItemNumberException
     * @throws NoMoreCardsException
     * @throws WrongTimeException
     */
    @Test
    public void restoreCheckpointPowerups() throws UnacceptableItemNumberException, NoMoreCardsException, WrongTimeException {
        Board b = BoardConfigurer.simulateScenario();
        StatusSaver ss = new StatusSaver(b);
        Player p1 = b.getPlayers().get(0);

        p1.drawPowerUp();
        ss.updateCheckpoint();
        p1.drawPowerUp();
        assertEquals(2, p1.getPowerUpList().size());

        ss.restoreCheckpoint();
        assertEquals(1, p1.getPowerUpList().size());
    }

    /**
     * Tests the methods restoreCheckpoint, checking whether the players' ammo are correctly restored.
     *
     * @throws UnacceptableItemNumberException
     * @throws NoMoreCardsException
     */
    @Test
    public void restoreCheckpointAmmo() throws UnacceptableItemNumberException, NoMoreCardsException {
        Board b = BoardConfigurer.simulateScenario();
        StatusSaver ss = new StatusSaver(b);
        ss.updateCheckpoint();
        Player p1 = b.getPlayers().get(0);

        p1.useAmmo(new AmmoPack(1, 1, 0));
        assertEquals(0, p1.getAmmoPack().getRedAmmo());
        assertEquals(0, p1.getAmmoPack().getBlueAmmo());
        assertEquals(1, p1.getAmmoPack().getYellowAmmo());
        b.getPlayers().get(1).useAmmo(new AmmoPack(0, 0 , 1));

        ss.restoreCheckpoint();
        assertEquals(1, p1.getAmmoPack().getRedAmmo());
        assertEquals(1, p1.getAmmoPack().getBlueAmmo());
        assertEquals(1, p1.getAmmoPack().getYellowAmmo());
    }


    /**
     * Tests the methods restoreCheckpoint, checking whether the current player's weapons are correctly restored.
     *
     * @throws UnacceptableItemNumberException
     * @throws NoMoreCardsException
     */
    @Test
    public void restoreCheckpointCurrentWeapons() throws UnacceptableItemNumberException, NoMoreCardsException {
        Board b = BoardConfigurer.simulateScenario();
        WeaponFactory wf =  new WeaponFactory(b);
        StatusSaver ss = new StatusSaver(b);
        Player p1 = b.getPlayers().get(0);
        Weapon lockRifle = wf.createWeapon(Weapon.WeaponName.LOCK_RIFLE);
        p1.addWeapon(lockRifle);
        ss.updateCheckpoint();
        Weapon electroscythe = wf.createWeapon(Weapon.WeaponName.ELECTROSCYTHE);
        p1.addWeapon(electroscythe);
        assertEquals(Arrays.asList(lockRifle, electroscythe), p1.getWeaponList());

        ss.restoreCheckpoint();
        assertEquals(Arrays.asList(lockRifle), p1.getWeaponList());

    }

    /**
     * Tests the methods restoreCheckpoint, checking whether the current player's loaded weapons are correctly restored.
     *
     * @throws UnacceptableItemNumberException
     * @throws NoMoreCardsException
     */
    @Test
    public void restoreCheckpointCurrentLoadedWeapons() throws UnacceptableItemNumberException, NoMoreCardsException {
        Board b = BoardConfigurer.simulateScenario();
        WeaponFactory wf =  new WeaponFactory(b);
        StatusSaver ss = new StatusSaver(b);
        Player p1 = b.getPlayers().get(0);
        Weapon lockRifle = wf.createWeapon(Weapon.WeaponName.LOCK_RIFLE);
        lockRifle.setLoaded(true);
        p1.addWeapon(lockRifle);
        Weapon electroscythe = wf.createWeapon(Weapon.WeaponName.ELECTROSCYTHE);
        electroscythe.setLoaded(false);
        p1.addWeapon(electroscythe);
        ss.updateCheckpoint();
        assertEquals(Arrays.asList(lockRifle), p1.getLoadedWeapons());
        electroscythe.setLoaded(true);
        assertEquals(Arrays.asList(lockRifle,electroscythe), p1.getLoadedWeapons());

        ss.restoreCheckpoint();
        assertEquals(Arrays.asList(lockRifle), p1.getLoadedWeapons());

    }

    /**
     * Tests the methods restoreCheckpoint, checking whether the weapons in the spawn points are correctly restored.
     *
     * @throws UnacceptableItemNumberException
     * @throws NoMoreCardsException
     * @throws WrongTimeException
     */
    @Test
    public void restoreCheckpointSquareWeapons() throws UnacceptableItemNumberException, NoMoreCardsException, WrongTimeException {
        Board b = BoardConfigurer.simulateScenario();
        StatusSaver ss = new StatusSaver(b);
        Player p1 = b.getPlayers().get(0);
        WeaponSquare sp = b.getSpawnPoints().get(0);
        p1.setPosition(sp);
        Weapon w1 = sp.getWeapons().get(0);
        Weapon w2 = sp.getWeapons().get(1);
        Weapon w3 = sp.getWeapons().get(2);
        ss.updateCheckpoint();

        p1.collect(w3);
        assertEquals(Arrays.asList(w1, w2), sp.getWeapons());

        ss.restoreCheckpoint();
        assertEquals(Arrays.asList(w1, w2, w3), sp.getWeapons());

    }




    @Test
    public void restorePowerUps() throws UnacceptableItemNumberException, NoMoreCardsException, WrongTimeException{
        Board b = BoardConfigurer.simulateScenario();
        StatusSaver ss = new StatusSaver(b);
        Player p1 = b.getPlayers().get(0);

        p1.drawPowerUp();
        ss.updatePowerups();
        p1.drawPowerUp();
        assertEquals(2, p1.getPowerUpList().size());

        ss.restorePowerUps();
        assertEquals(1, p1.getPowerUpList().size());
    }
}