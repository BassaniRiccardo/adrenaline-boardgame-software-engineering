package it.polimi.ingsw.controller;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.board.*;
import it.polimi.ingsw.model.cards.AmmoPack;
import it.polimi.ingsw.model.cards.PowerUp;
import it.polimi.ingsw.model.cards.Weapon;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.network.server.VirtualView;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static it.polimi.ingsw.controller.TurnManager.toStringList;
import static it.polimi.ingsw.controller.TurnManager.toUserStringList;
import static it.polimi.ingsw.model.cards.Color.BLUE;
import static it.polimi.ingsw.model.cards.Color.RED;
import static org.junit.Assert.*;

/**
 * Test the methods of TurnManager which can be tested without simulating a game.
 *
 * @author BassaniRiccardo
 */

//TODO: Tests all the methods

public class TurnManagerTest {

    /**
     * A subclass of VirtualView simulating players answers.
     */
    class DummyVirtualView extends VirtualView{

        @Override
        public void refresh() {        }

        @Override
        public void shutdown() {        }

        @Override
        public void showSuspension() {        }

        @Override
        public void showEnd(String message) {        }

        @Override
        public void choose(String type, String msg, List<?> options) {
            notifyObservers("1");
        }

        @Override
        public void choose(String type, String msg, List<?> options, int timeoutSec) {
            notifyObservers("1");
        }

        @Override
        public void display(String msg) {        }

        @Override
        public String getInputNow(String msg, int max) {
            return "1";
        }

        @Override
        public int chooseNow(String type, String msg, List<?> options) {
            return 1;
        }

        @Override
        public void update(JsonObject jsonObject) {        }
    }


    /**
     * Tests the method runTurn() for the first turn of the first player and in the case he only makes movement actions.
     *
     * @throws NotEnoughPlayersException        if thrown by runTurn().
     * @throws NotEnoughPlayersException        if thrown by runTurn().
     */
    @Test()
    public void run() throws SlowAnswerException, NotEnoughPlayersException {

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.setup();
        VirtualView distructor = connections.get(0);
        VirtualView banshee = connections.get(1);
        VirtualView dozer = connections.get(2);
        VirtualView violet = connections.get(3);
        VirtualView sprog = connections.get(4);
        StatusSaver statusSaver = new StatusSaver(gameEngine.getBoard());
        TurnManager turnManager = new TurnManager(gameEngine, gameEngine.getBoard(), gameEngine.getCurrentPlayer(), connections, statusSaver, false, new Timer(60));

        turnManager.runTurn();

        //check if only the first player is on the board
        assertTrue(distructor.getModel().isInGame());
        assertFalse(banshee.getModel().isInGame());
        assertFalse(dozer.getModel().isInGame());
        assertFalse(violet.getModel().isInGame());
        assertFalse(sprog.getModel().isInGame());

    }

    /**
     * Tests the method handleCollecting() for the first turn of the first player
     * and in the case he collects a weapon.
     * Since the answer is always yes the collecting is confirmed.
     *
     * @throws NotEnoughPlayersException        if thrown by runTurn().
     * @throws NotEnoughPlayersException        if thrown by runTurn().
     */
    @Test()
    public void hanldeCollectingWeapon() throws SlowAnswerException, NotEnoughPlayersException, NotAvailableAttributeException {

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.setup();
        VirtualView distructor = connections.get(0);
        StatusSaver statusSaver = new StatusSaver(gameEngine.getBoard());
        TurnManager turnManager = new TurnManager(gameEngine, gameEngine.getBoard(), gameEngine.getCurrentPlayer(), connections, statusSaver, false, new Timer(60));
        turnManager.joinBoard(distructor.getModel(), 2, false);

        //check the players collected a weapon
        assertEquals(0, distructor.getModel().getWeaponList().size());
        assertEquals(3, ((WeaponSquare)distructor.getModel().getPosition()).getWeapons().size());
        turnManager.handleCollecting();
        assertEquals(1, distructor.getModel().getWeaponList().size());
        assertEquals(2, ((WeaponSquare)distructor.getModel().getPosition()).getWeapons().size());

    }


    /**
     * Tests the method handleCollecting() for the first turn of the first player
     * and in the case he collects a ammoTile.
     * Since the answer is always yes the collecting is confirmed.
     *
     * @throws NotEnoughPlayersException         if thrown by setup(), joinBoard() or handleCollecting().
     * @throws SlowAnswerException               if thrown by joinBoard() or handleCollecting().
     * @throws NotAvailableAttributeException    if thrown by getPosition().
     */
    @Test()
    public void hanldeCollectingAmmoTile() throws SlowAnswerException, NotEnoughPlayersException, NotAvailableAttributeException {

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.setup();
        VirtualView distructor = connections.get(0);
        StatusSaver statusSaver = new StatusSaver(gameEngine.getBoard());
        TurnManager turnManager = new TurnManager(gameEngine, gameEngine.getBoard(), gameEngine.getCurrentPlayer(), connections, statusSaver, false, new Timer(60));
        turnManager.joinBoard(distructor.getModel(), 2, false);
        distructor.getModel().setPosition(gameEngine.getBoard().getAmmoSquares().get(0));

        //check the players collected an ammoTile
        assertEquals(1, distructor.getModel().getAmmoPack().getRedAmmo());
        assertEquals(1, distructor.getModel().getAmmoPack().getBlueAmmo());
        assertEquals(1, distructor.getModel().getAmmoPack().getYellowAmmo());
        assertTrue(((AmmoSquare)distructor.getModel().getPosition()).hasAmmoTile());

        turnManager.handleCollecting();
        assertFalse(((AmmoSquare)distructor.getModel().getPosition()).hasAmmoTile());
        AmmoPack newAmmoPcke = distructor.getModel().getAmmoPack();
        assertTrue(newAmmoPcke.getRedAmmo() + newAmmoPcke.getBlueAmmo() + newAmmoPcke.getYellowAmmo() == 5 || newAmmoPcke.getRedAmmo() + newAmmoPcke.getBlueAmmo() + newAmmoPcke.getYellowAmmo() == 6);
    }


    /**
     * Tests the method usePowerUp() for the first turn of the first player.
     * Since the answer is always yes the action is confirmed.
     *
     * @throws NotEnoughPlayersException         if thrown by setup(), joinBoard() or usePowerUp().
     * @throws SlowAnswerException               if thrown by joinBoard() or usePowerUp().
     * @throws NotAvailableAttributeException    if thrown by getPosition().
     */
    @Test()
    public void usePowerUp() throws SlowAnswerException, NotEnoughPlayersException, NotAvailableAttributeException {

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.setup();

        VirtualView distructor = connections.get(0);

        StatusSaver statusSaver = new StatusSaver(gameEngine.getBoard());
        TurnManager turnManager = new TurnManager(gameEngine, gameEngine.getBoard(), gameEngine.getCurrentPlayer(), connections, statusSaver, false, new Timer(60));
        turnManager.joinBoard(distructor.getModel(), 2, false);
        distructor.getModel().setPosition(gameEngine.getBoard().getMap().get(4));

        PowerUpFactory powerUpFactory = new PowerUpFactory(gameEngine.getBoard());
        PowerUp newton = powerUpFactory.createPowerUp(PowerUp.PowerUpName.NEWTON, BLUE);
        PowerUp teleporter = powerUpFactory.createPowerUp(PowerUp.PowerUpName.TELEPORTER, BLUE);
        distructor.getModel().getPowerUpList().clear();

        //gives the player a newton. He can't use it.
        distructor.getModel().getPowerUpList().add(newton);
        newton.setHolder(distructor.getModel());

        //gives the player a newton. He can use it.
        distructor.getModel().getPowerUpList().add(teleporter);
        teleporter.setHolder(distructor.getModel());

        //check the players has two powerups
        assertEquals(2, distructor.getModel().getPowerUpList().size());

        //the player will use the teleporter since he cannot use the newton
        turnManager.usePowerUp();

        //check the players has now only the newton and he teleported to square zero (the first in the option list).
        assertEquals(Collections.singletonList(newton), distructor.getModel().getPowerUpList());
        assertEquals(0, distructor.getModel().getPosition().getId());
    }


    /**
     * Tests the method executeActualAction() for the action "Move up to 1 squares. Reload. Shoot."
     * The method handleMoving(), reloadMandatory(), handleShooting() are tested as well.
     * Since the answer is always yes the action is confirmed.
     *
     * @throws NotEnoughPlayersException         if thrown by setup(), joinBoard() or executeActualAction().
     * @throws SlowAnswerException               if thrown by joinBoard() or executeActualAction().
     */
    @Test()
    public void moveReloadShoot() throws SlowAnswerException, NotEnoughPlayersException {

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.setup();

        VirtualView distructor = connections.get(0);
        VirtualView banshee = connections.get(1);

        StatusSaver statusSaver = new StatusSaver(gameEngine.getBoard());
        TurnManager turnManager = new TurnManager(gameEngine, gameEngine.getBoard(), gameEngine.getCurrentPlayer(), connections, statusSaver, false, new Timer(60));
        turnManager.joinBoard(distructor.getModel(), 2, false);
        distructor.getModel().setPosition(gameEngine.getBoard().getMap().get(4));
        distructor.getModel().setStatus(Player.Status.FRENZY_1);
        distructor.getModel().refreshActionList();
        WeaponFactory weaponFactory = new WeaponFactory(gameEngine.getBoard());
        Weapon lockrifle = weaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE);
        distructor.getModel().getWeaponList().add(lockrifle);
        lockrifle.setHolder(distructor.getModel());

        turnManager.joinBoard(banshee.getModel(), 2, false);
        banshee.getModel().setPosition(gameEngine.getBoard().getMap().get(5));

        //unload the first player weapon
        distructor.getModel().getWeaponList().get(0).setLoaded(false);
        //to be sure he can reload
        distructor.getModel().addAmmoPack(new AmmoPack(2,2,2));
        //to be sure that distructor does not have a targeting scope and banshee does not have a grenade
        distructor.getModel().getPowerUpList().clear();
        banshee.getModel().getPowerUpList().clear();

        //he can reload after the movement, he will choose yes
        System.out.println(distructor.getModel().getActionList().get(2));
        turnManager.executeActualAction(distructor.getModel().getActionList().get(2));

        //check that distructor reloaded his weapon and shot banshee
        assertEquals(Arrays.asList(distructor.getModel(), distructor.getModel()),banshee.getModel().getDamages());
    }


    /**
     * Tests the method executeActualAction() for the action "Move up to 1 squares. Reload. Shoot."
     * The method handleMoving(), reloadMandatory(), handleShooting() are tested as well.
     * The shooter is given a targeting scope and the target a grenade.
     * The method handleTargetingScope() is testd in the case the shooter decides to use the scope.
     * The methods askTargetsForGrenade() and handleTagbackGrenade() are tested in the case only a target holds a grenade and he decides not to use it,
     * Since the answer is always yes the action is confirmed.
     *
     * @throws NotEnoughPlayersException         if thrown by setup(), joinBoard() or executeActualAction().
     * @throws SlowAnswerException               if thrown by joinBoard() or executeActualAction().
     */
    @Test()
    public void shootingScopeGrenade() throws SlowAnswerException, NotEnoughPlayersException {

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.setup();

        VirtualView destructor = connections.get(0);
        VirtualView banshee = connections.get(1);

        StatusSaver statusSaver = new StatusSaver(gameEngine.getBoard());
        TurnManager turnManager = new TurnManager(gameEngine, gameEngine.getBoard(), gameEngine.getCurrentPlayer(), connections, statusSaver, false, new Timer(60));
        turnManager.joinBoard(destructor.getModel(), 2, false);
        destructor.getModel().setPosition(gameEngine.getBoard().getMap().get(4));
        destructor.getModel().setStatus(Player.Status.FRENZY_1);
        destructor.getModel().refreshActionList();
        PowerUpFactory powerUpFactory = new PowerUpFactory(gameEngine.getBoard());
        WeaponFactory weaponFactory = new WeaponFactory(gameEngine.getBoard());
        Weapon lockrifle = weaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE);
        destructor.getModel().getWeaponList().add(lockrifle);
        lockrifle.setHolder(destructor.getModel());

        turnManager.joinBoard(banshee.getModel(), 2, false);
        banshee.getModel().setPosition(gameEngine.getBoard().getMap().get(5));

        //unload the first player weapon
        destructor.getModel().getWeaponList().get(0).setLoaded(false);
        //to be sure he can reload
        destructor.getModel().addAmmoPack(new AmmoPack(2,2,2));

        //gives destructor a targeting scope
        destructor.getModel().getPowerUpList().clear();
        //the targeting scope cannot be blue, otherwise destructor would use it to pay the reloading
        PowerUp scope = powerUpFactory.createPowerUp(PowerUp.PowerUpName.TARGETING_SCOPE, RED);
        destructor.getModel().getPowerUpList().add(scope);
        scope.setHolder(destructor.getModel());

        //gives banshee a tagback grenade
        banshee.getModel().getPowerUpList().clear();
        PowerUp grenade = powerUpFactory.createPowerUp(PowerUp.PowerUpName.TAGBACK_GRENADE, BLUE);
        banshee.getModel().getPowerUpList().add(grenade);
        grenade.setHolder(banshee.getModel());

        //he can reload after the movement, he will choose yes
        turnManager.executeActualAction(destructor.getModel().getActionList().get(2));

        //check that destructor reloaded his weapon and shot banshee
        assertEquals(Arrays.asList(destructor.getModel(), destructor.getModel(), destructor.getModel()),banshee.getModel().getDamages());
        assertTrue(destructor.getModel().getMarks().isEmpty());

    }



    /**
     * Tests the method reload() when the conversion of a powerup is needed in order to reload.
     * The method mandatoryConversion() is tested as well.
     *
     * @throws NotEnoughPlayersException         if thrown by setup(), joinBoard() or reload().
     * @throws SlowAnswerException               if thrown by joinBoard() or reload().
     */
    @Test()
    public void reloadMandatoryConversion() throws SlowAnswerException, NotEnoughPlayersException {

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.setup();

        VirtualView destructor = connections.get(0);
        VirtualView banshee = connections.get(1);

        StatusSaver statusSaver = new StatusSaver(gameEngine.getBoard());
        TurnManager turnManager = new TurnManager(gameEngine, gameEngine.getBoard(), gameEngine.getCurrentPlayer(), connections, statusSaver, false, new Timer(60));
        turnManager.joinBoard(destructor.getModel(), 2, false);
        destructor.getModel().setPosition(gameEngine.getBoard().getMap().get(4));
        destructor.getModel().setStatus(Player.Status.FRENZY_1);
        destructor.getModel().refreshActionList();
        PowerUpFactory powerUpFactory = new PowerUpFactory(gameEngine.getBoard());
        WeaponFactory weaponFactory = new WeaponFactory(gameEngine.getBoard());
        Weapon lockrifle = weaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE);
        destructor.getModel().getWeaponList().add(lockrifle);
        lockrifle.setHolder(destructor.getModel());

        turnManager.joinBoard(banshee.getModel(), 2, false);
        banshee.getModel().setPosition(gameEngine.getBoard().getMap().get(5));

        //unload the first player weapon
        destructor.getModel().getWeaponList().get(0).setLoaded(false);

        destructor.getModel().getPowerUpList().clear();
        //gives destructor a targeting scope
        //the targeting scope must be blue, otherwise destructor could not use it to pay the reloading
        PowerUp scope = powerUpFactory.createPowerUp(PowerUp.PowerUpName.TARGETING_SCOPE, BLUE);
        destructor.getModel().getPowerUpList().add(scope);
        scope.setHolder(destructor.getModel());

        turnManager.reload(3);

        //check that destructor reloaded his weapon
        assertTrue(lockrifle.isLoaded());
        assertTrue(destructor.getModel().getPowerUpList().isEmpty());

    }





    /**
     * Tests the method replaceWeapons(), checking that the drawn weapons are replace after it is called.
     *
     * @throws NoMoreCardsException         if thrown by removeCard().
     * @throws NotEnoughPlayersException    if thrown by GameEngine.setup().
     */
    @Test
    public void replaceWeapons() throws NoMoreCardsException, NotEnoughPlayersException {

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());

        GameEngine gameEngine = new GameEngine(connections);
        StatusSaver statusSaver = new StatusSaver(gameEngine.getBoard());

        gameEngine.setup();

        TurnManager turnManager = new TurnManager(gameEngine, gameEngine.getBoard(), gameEngine.getCurrentPlayer(), connections, statusSaver, false, new Timer(60));

        assertEquals(3, gameEngine.getBoard().getSpawnPoints().get(0).getWeapons().size());

        gameEngine.getBoard().getSpawnPoints().get(0).removeCard(gameEngine.getBoard().getSpawnPoints().get(0).getWeapons().get(0));
        assertEquals(2, gameEngine.getBoard().getSpawnPoints().get(0).getWeapons().size());

        turnManager.replaceWeapons();
        assertEquals(3, gameEngine.getBoard().getSpawnPoints().get(0).getWeapons().size());

    }

    /**
     * Tests the method replaceAmmoTiles.
     *
     * @throws NoMoreCardsException             if thrown by removeCard().
     * @throws NotAvailableAttributeException   if thrown by getAmmoTile().
     * @throws NotEnoughPlayersException        if thrown by GameEngine.setup().
     */
    @Test()
    public void ReplaceAmmoTiles() throws NoMoreCardsException, NotAvailableAttributeException, NotEnoughPlayersException {

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());

        GameEngine gameEngine = new GameEngine(connections);
        StatusSaver statusSaver = new StatusSaver(gameEngine.getBoard());
        gameEngine.setup();
        TurnManager turnManager = new TurnManager(gameEngine, gameEngine.getBoard(), gameEngine.getCurrentPlayer(), connections, statusSaver, false, new Timer(60));

        AmmoSquare as = gameEngine.getBoard().getAmmoSquares().get(0);
        assertTrue(as.hasAmmoTile());
        as.removeCard(as.getAmmoTile());
        assertFalse(as.hasAmmoTile());
        turnManager.replaceAmmoTiles();
        assertTrue(as.hasAmmoTile());
    }


    /**
     * Tests the method toStringList().
     */
    @Test
    public void toStringListTest() {

        Board b = BoardConfigurer.configureMap(1);
        List<Square> squareList = b.getMap();
        List<String> stringList = Arrays.asList("Square 0", "Square 1", "Square 2", "Square 3", "Square 4", "Square 5", "Square 6", "Square 7", "Square 8", "Square 9");
        assertEquals(stringList, toStringList(squareList));

    }


    /**
     * Tests the method userToStringList().
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     */
    @Test
    public void userToStringListTest() throws UnacceptableItemNumberException, NoMoreCardsException{

        Board b = BoardConfigurer.simulateScenario();
        List<List<Player>> playerGroupsList = new ArrayList<>();
        Player p1 = b.getPlayers().get(0);
        p1.setUsername("Alberto");
        Player p2 = b.getPlayers().get(1);
        p2.setUsername("Barbara");
        Player p3 = b.getPlayers().get(2);
        p3.setUsername("Carlotta");
        playerGroupsList.add(Arrays.asList(p1,p2));
        playerGroupsList.add(Collections.singletonList(p3));

        //checks the generic list is transformed in a list of strings
        List<String> stringList = toUserStringList(playerGroupsList);

        //the name and the color are correct
        System.out.println("\nTesting TurnManager.userToStringList().\nAlberto is yellow. Barbara is blue. Carlotta is grey.\nThe output is printed to console since it is the better way to check the color of a string.\n" );
        //it is shown through a println() since it is not possible to check the color of a string in another way
        System.out.println(stringList);

    }


    /**
     * Tests the method updateDead().
     *
     * @throws NotEnoughPlayersException        if thrown by GameEngine.setup().
     */
    @Test
    public void updateDeadTest() throws NotEnoughPlayersException{

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.setup();
        Board b = gameEngine.getBoard();
        StatusSaver statusSaver = new StatusSaver(b);
        TurnManager turnManager = new TurnManager(gameEngine, gameEngine.getBoard(), gameEngine.getCurrentPlayer(), connections, statusSaver, false, new Timer(60));
        for (int i=0; i<connections.size(); i++) {
            gameEngine.getPlayers().get(i).setPlayer(b.getPlayers().get(i));
        }

        //nobody is dead
        assertTrue(turnManager.getDead().isEmpty());
        b.getPlayers().get(0).setInGame(true);
        b.getPlayers().get(0).setDead(true);
        turnManager.updateDead();

        //the first player is dead and updateDead has been called
        assertEquals(Collections.singletonList(1), turnManager.getDead());
        b.getPlayers().get(1).setInGame(true);
        b.getPlayers().get(1).setDead(true);

        //the second player is dead but updateDead has not been called
        assertEquals(Collections.singletonList(1), turnManager.getDead());
        turnManager.updateDead();

        //update dead has been called
        assertEquals(Arrays.asList(1,2), turnManager.getDead());


    }

}