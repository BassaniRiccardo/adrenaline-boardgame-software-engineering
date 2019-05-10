package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.board.*;
import it.polimi.ingsw.model.cards.AmmoTile;
import it.polimi.ingsw.model.cards.Color;
import it.polimi.ingsw.model.cards.PowerUp;
import it.polimi.ingsw.model.cards.Weapon;
import it.polimi.ingsw.model.exceptions.NoMoreCardsException;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;
import it.polimi.ingsw.model.exceptions.UnacceptableItemNumberException;
import it.polimi.ingsw.model.exceptions.WrongTimeException;
import it.polimi.ingsw.network.server.PlayerController;

import java.util.*;
import java.util.logging.*;


import static it.polimi.ingsw.controller.Encoder.*;

/**
 * Manages a turn, displaying the main events on the console.
 *
 * @author BassaniRiccardo
 */

//TODO: finish implementing handleShooting() and properly modify the code depending on the shooting process.
// Implement the connection with the server.
// The actual model needs to be updated only when the action is confirmed.
// Finish testing.
// Use a logger (also in other classes).

public class TurnManager implements Runnable{

    private Board board;
    private PlayerController currentPlayerConnection;
    private Player currentPlayer;
    private List<Player> dead;
    private KillShotTrack killShotTrack;
    private boolean frenzy;

    private static final Logger LOGGER = Logger.getLogger("turnManagerLogger");
    private static final String P = "Player ";
    private static final String EX_CAN_USE_POWERUP ="NotAvailableAttributeException thrown while checking if the player can use a powerup";

    /**
     * Constructs a turn manager with a reference to the board, the current player and the list of players.
     *
     * @param board                         the board of the game.
     * @param currentPlayerConnection       the current player PlayerController.
     */
    public TurnManager(Board board, PlayerController currentPlayerConnection, boolean frenzy){
        this.board = board;
        this.currentPlayerConnection = currentPlayerConnection;
        this.currentPlayer = this.currentPlayerConnection.getModel();
        this.dead = new ArrayList<>();
        try {
            this.killShotTrack = this.board.getKillShotTrack();
        } catch (NotAvailableAttributeException e){ LOGGER.log(Level.SEVERE,"NotAvailableAttributeException thrown while setting the kill shot track", e);}
        this.frenzy = frenzy;
    }

    /**
     * Runs a turn.
     */
    public void run() {

        dead.clear();

        if (!currentPlayer.isInGame()) {
            joinBoard(currentPlayer, 2);
        }

        int actionsLeft = 2;
        if (currentPlayer.getStatus()== Player.Status.FRENZY_2) actionsLeft--;
        while (actionsLeft > 0) {
            if (executeAction()) {
                actionsLeft--;
            }
        }

        handleUsingPowerUp();
        convertPowerUp();
        reload(3);

        //checks who died, rewards killers, make the dead draw a power up and respawn
        for (Player deadPlayer: dead){
            try {
                System.out.println("The killers are awarded for the death of Player " + deadPlayer.getId() + "." );
                deadPlayer.rewardKillers();
                killShotTrack.registerKill(currentPlayer, deadPlayer, deadPlayer.getDamages().size() > 11);
                joinBoard(deadPlayer, 1);
                if (frenzy){
                    deadPlayer.setFlipped(true);
                    deadPlayer.setPointsToGive(2);
                }
            } catch (WrongTimeException | UnacceptableItemNumberException e) {LOGGER.log(Level.SEVERE,"Exception thrown while resolving the deaths", e);}
        }
        if (dead.size() > 1) {
            currentPlayer.addPoints(1);
            System.out.println(P + currentPlayer.getId() + " gets an extra point for the multiple kill!!!");
        }

        replaceWeapons();
        replaceAmmoTiles();

        System.out.println(P + currentPlayer.getId() + " ends his turn.\n\n");
        for (Player p: board.getActivePlayers()){
            System.out.println(P + p.getId() + ": \t\t" + p.getPoints() + " points \t\t"+ p.getDamages().size() + " damages.");
        }

        System.out.println("\n\n\n");

    }


    /**
     * Adds a player to the board, at the beginning of the game of after his death.
     * The player draws the specified number of powerups and is positioned on the powerup color spawn point.
     *
     * @param player                the player to addList to the board.
     * @param powerUpToDraw         the number of powerups the player has to draw.
     */
    public void joinBoard(Player player, int powerUpToDraw) {

        //the user draws two powerups
        for (int i = 0; i < powerUpToDraw; i++) {
            try {
                player.drawPowerUp();
            } catch (NoMoreCardsException | UnacceptableItemNumberException | WrongTimeException e) {LOGGER.log(Level.SEVERE,"Exception thrown while drawing a powerup", e);}
        }

        //asks the user which powerup he wants to discard
        currentPlayerConnection.send(encode("Which powerUp do you want to discard?", player.getPowerUpList()) );
        int selected = currentPlayerConnection.receive(player.getPowerUpList().size(), 10);
        PowerUp discarded = player.getPowerUpList().get(selected-1);

        if (powerUpToDraw == 2) System.out.println(P + player.getId() + " draws two powerups and discards a " + discarded.toStringLowerCase() + ".");
        else System.out.println(P + player.getId() + " draws a powerup and discards a " + discarded.toStringLowerCase() + ".");

        Color birthColor = discarded.getColor();
        player.discardPowerUp(discarded);

        //place the player on the board
        for (WeaponSquare s : board.getSpawnPoints()) {
            if (s.getColor() == birthColor) player.setPosition(s);
        }

        player.setInGame(true);
        player.refreshActionList();

        System.out.println(P + player.getId() + " enters in the board in the " + discarded.getColor().toStringLowerCase() + " spawn point.");

    }


    /**
     * Interacts with the user offering him the actions he can make.
     * Also allows to use or convert a powerup if possible.
     *
     * @return      true if an actual action is performed by the user.
     *              false otherwise.
     */
    public boolean executeAction(){

        boolean canUSePowerUp = false;
        List<Action> availableActions = new ArrayList<>();

        try {
            availableActions = currentPlayer.getAvailableActions();
        } catch (NotAvailableAttributeException e) {LOGGER.log(Level.SEVERE, "NotAvailableAttributeException thrown while getting the available actions", e);}

        try {
            canUSePowerUp = currentPlayer.hasUsableTeleporterOrNewton();
        }catch (NotAvailableAttributeException e){ LOGGER.log(Level.SEVERE,EX_CAN_USE_POWERUP, e);}

        List<String> options = toStringList(availableActions);

        if (canUSePowerUp){ options.add("Use Powerup"); }
        if (!currentPlayer.getPowerUpList().isEmpty()){ options.add("Convert Powerup"); }

        currentPlayerConnection.send(encode("What do you want to do?", options));

        int selected = currentPlayerConnection.receive(options.size(), 10);

        if (selected == availableActions.size() + 1){
            if (canUSePowerUp){
                handleUsingPowerUp();
            }
            else convertPowerUp();
            return false;
        }

        else if (selected == availableActions.size() + 2){
            convertPowerUp();
            return false;
        }

        else {
            executeActualAction(availableActions.get(selected-1));
            return true;
        }

    }


    /**
     * Interacts with the user offering him the actual actions he can make, depending on his status.
     *
     */
    public void executeActualAction(Action action){

        System.out.println(P + currentPlayer.getId() + " chooses the action: " + action);

        if (action.getSteps() > 0) {
            handleMoving(action);
        }
        if (action.isCollect()) {
            handleCollecting();
        }
        if (action.isReload()) {
            try {
                if (currentPlayer.getShootingSquares(0, currentPlayer.getLoadedWeapons()).isEmpty()) {
                    reloadMandatory();
                }
                else {
                   reload(1);
                }
            }  catch (NotAvailableAttributeException e){LOGGER.log(Level.SEVERE, "NotAvailableAttributeException thrown while reloading", e);}

        }
        if (action.isShoot()) {
            handleShooting();
        }
    }


    /**
     * Checks if the user has a teleporter or a newton and, while he has one, offers him the chance to use it.
     * Ends when the user refuses.
     */
    public void handleUsingPowerUp(){

        int answer = 1;
        boolean possible = false;
        try {
            possible = currentPlayer.hasUsableTeleporterOrNewton();
        } catch (NotAvailableAttributeException e){LOGGER.log(Level.SEVERE, EX_CAN_USE_POWERUP, e);}

        while (possible && answer == 1) {

            List<String> options = new ArrayList<>(Arrays.asList("yes", "no"));

            currentPlayerConnection.send(encode("Do you want to use a powerup?", options));
            answer = currentPlayerConnection.receive(2, 10);
            if (answer == 2){
                System.out.println(P + currentPlayer.getId() + " decides not to use a powerup." );
            }
            if (answer == 1) {
                usePowerUp();
            }
            try {
                possible = currentPlayer.hasUsableTeleporterOrNewton();
            } catch (NotAvailableAttributeException e){LOGGER.log(Level.SEVERE, EX_CAN_USE_POWERUP, e);}

        }
    }


    /**
     * Asks the user which powerup he wants to use and how.
     * Activates the effect of the selected powerup.
     */
    public void usePowerUp(){

        System.out.println(P + currentPlayer.getId() + " decides to use a powerup." );

        List<Player> targets = new ArrayList<>();

        List<PowerUp> usablePowerUps = currentPlayer.getPowerUpList();
        List<PowerUp> toRemove = new ArrayList<>();
        for (PowerUp p: usablePowerUps){
            if (p.getName() == PowerUp.PowerUpName.TARGETING_SCOPE || p.getName() == PowerUp.PowerUpName.TAGBACK_GRENADE) {
                toRemove.add(p);
            }
        }
        usablePowerUps.removeAll(toRemove);

        currentPlayerConnection.send(encode("Which powerup do you want to use?", usablePowerUps));
        int selected = currentPlayerConnection.receive(currentPlayer.getPowerUpList().size(), 10);
        PowerUp powerUpToUse = currentPlayer.getPowerUpList().get(selected-1);

        System.out.println(P + currentPlayer.getId() + " decides to use a " + powerUpToUse.getName().toString() + ".\n");

        if (powerUpToUse.getName() == PowerUp.PowerUpName.NEWTON) {

            try{
                currentPlayerConnection.send(encode("Who do you want to choose as a target?", powerUpToUse.findTargets()));
                selected = currentPlayerConnection.receive(powerUpToUse.findTargets().size(), 10);
                targets = powerUpToUse.findTargets().get(selected-1);
            } catch(NotAvailableAttributeException e){LOGGER.log(Level.SEVERE,"NotAvailableAttributeException thrown while searching for the powerup targets", e);}
        }
        else {
            targets.add(currentPlayer);
        }

        try {
            currentPlayerConnection.send(encode("Choose a destination", powerUpToUse.findDestinations(targets)));
            selected = currentPlayerConnection.receive(powerUpToUse.findDestinations(targets).size(), 10);
            Square destination = powerUpToUse.findDestinations(targets).get(selected - 1);
            powerUpToUse.applyEffects(targets, destination);
            currentPlayer.discardPowerUp(powerUpToUse);
            if (targets.contains(currentPlayer))  System.out.println("He moves in " + destination.toString() + ".\n");
            else  System.out.println("He moves Player " + targets.get(0).getId() + " in " + destination.toString() + ".\n");
        } catch (NotAvailableAttributeException e) {LOGGER.log(Level.SEVERE,"NotAvailableAttributeException thrown while searching for the powerup destinations", e);}
    }


    /**
     * Checks if the user has a powerup and, while he has one, offers him the chance to convert it to gain an ammo.
     * Ends when the user refuses.
     */
    public void convertPowerUp() {

        int answer = 1;

        while (!currentPlayer.getPowerUpList().isEmpty() && answer == 1) {

            List<String> options = new ArrayList<>(Arrays.asList("yes", "no"));

            currentPlayerConnection.send(encode("Do you want to convert a powerup?", options));
            answer = currentPlayerConnection.receive(2, 10);

            if (answer == 2){
                System.out.println(P + currentPlayer.getId() + " decides not to convert a powerup.");
            }

            if (answer == 1) {

                System.out.println(P + currentPlayer.getId() + " decides to convert a powerup.");

                currentPlayerConnection.send(encode("Which powerup do you want to convert?", currentPlayer.getPowerUpList()));
                int selected = currentPlayerConnection.receive(currentPlayer.getPowerUpList().size(), 10);
                PowerUp powerUpToConvert = currentPlayer.getPowerUpList().get(selected - 1);
                currentPlayer.useAsAmmo(powerUpToConvert);

                System.out.println(P + currentPlayer.getId() + " converts a  " + powerUpToConvert.toStringLowerCase() + " into an ammo.");
            }

        }

    }


    /**
     * Handles the process of moving.
     */
    public void handleMoving(Action action){
        try {
                List<Square> possibleDestinations = board.getReachable(currentPlayer.getPosition(), (action.getSteps()));
                if (!action.isCollect() && !action.isShoot()){
                    possibleDestinations.remove(currentPlayer.getPosition());
                }
                else if (action.isCollect()){
                    List<Square> toRemove = new ArrayList<>();
                    for (Square square: possibleDestinations){
                        if (square.isEmpty() || (board.getSpawnPoints().contains(square) && currentPlayer.getCollectibleWeapons((WeaponSquare)square).isEmpty())) {
                            toRemove.add(square);
                        }
                    }
                    possibleDestinations.removeAll(toRemove);

                }
                else {
                    if (action.isReload()) {
                        List<Weapon> weapons = new ArrayList<>();
                        weapons.addAll(currentPlayer.getLoadedWeapons());
                        weapons.addAll(currentPlayer.getReloadableWeapons());
                        possibleDestinations = currentPlayer.getShootingSquares(action.getSteps(),weapons );
                    }
                    else possibleDestinations = currentPlayer.getShootingSquares(action.getSteps(), currentPlayer.getLoadedWeapons());
                }

                currentPlayerConnection.send(encode ("Where do you wanna move?", possibleDestinations));
                int selected = currentPlayerConnection.receive(possibleDestinations.size(), 10);
                Square dest = possibleDestinations.get(selected-1);
                if (!dest.equals(currentPlayer.getPosition())){
                    currentPlayer.setPosition(dest);
                    System.out.println(P + currentPlayer.getId() + " moves in " + currentPlayer.getPosition() + ".");
                }
                else System.out.println(P + currentPlayer.getId() + " stays in " + currentPlayer.getPosition() + ".");

        } catch (NotAvailableAttributeException e) {LOGGER.log(Level.SEVERE,"NotAvailableAttributeException thrown while handling the moving process", e);}
    }

    /**
     * Handles the process of collecting.
     */

    public void handleCollecting(){
        try {
                if (board.getSpawnPoints().contains(currentPlayer.getPosition())) {
                    List<Weapon> collectible = currentPlayer.getCollectibleWeapons((WeaponSquare)currentPlayer.getPosition());
                    currentPlayerConnection.send(encode("Which weapon do you want to collect?", collectible ));
                    int selected = currentPlayerConnection.receive(collectible.size(), 10);
                    Weapon collectedWeapon = (collectible.get(selected-1));
                    currentPlayer.collect(collectedWeapon);
                    System.out.println(P + currentPlayer.getId() + " collects  " + collectedWeapon + ".");
                    if (currentPlayer.getWeaponList().size()>3){
                        currentPlayerConnection.send(encode("Which weapon do you want to discard?", currentPlayer.getWeaponList()));
                        selected = currentPlayerConnection.receive(currentPlayer.getWeaponList().size(), 10);
                        Weapon discardedWeapon = currentPlayer.getWeaponList().get(selected-1);
                        currentPlayer.discardWeapon(discardedWeapon);
                        discardedWeapon.setLoaded(false);
                        ((WeaponSquare) currentPlayer.getPosition()).addCard(discardedWeapon);
                        System.out.println(P + currentPlayer.getId() + " discards  " + discardedWeapon + ".");

                    }

                } else {
                    AmmoTile toCollect = ((AmmoSquare) currentPlayer.getPosition()).getAmmoTile();
                    currentPlayer.collect(toCollect);
                    System.out.println(P + currentPlayer.getId() + " collects an ammo tile.");
                    if (toCollect.hasPowerUp()){
                        System.out.println("It allows him to draw a power up.");
                    }

                }
        } catch (NotAvailableAttributeException | NoMoreCardsException | UnacceptableItemNumberException | WrongTimeException e) {
            LOGGER.log(Level.SEVERE,"Exception thrown while handling the collecting process", e);
        }
    }


    /**
     * Handles the process of shooting.
     */

    //TODO Must be modified implementing a real method instead of a random generator of damage.
    // Tagback_grenade and targeting scope must be considered too.

    public void handleShooting(){

        System.out.println(P + currentPlayer.getId() + " shoots.");

        for (Player p: board.getActivePlayers()) {
            if (p != currentPlayer) {
                int damage = 1 + (new Random()).nextInt(3);
                p.sufferDamage((damage), currentPlayer);
                System.out.println("Damages done to Player " + p.getId() + ": " + damage + ".");
            }
            p.refreshActionList();
            if (p.isDead()&&!dead.contains(p)){
                dead.add(p);
                System.out.println(P + p.getId() + " is dead.");
                if (p.isOverkilled()) System.out.println("It is an overkill!!!");
                if (dead.size()>1) System.out.println("Multiple kill for Player " + currentPlayer.getId() + "!!!");

            }
        }

    /*  List<Weapon> usableWeapons = currentPlayer.getLoadedWeapons();
        if (usableWeapons.isEmpty()){
            //ASK: nothing (Display: "No weapons available")
        }
        else {
            //ASK: which weapon do you want to use? (Display: usableWeapons)
            Weapon shootingWeapon = null; //the selectedWeapon;

            List<FireMode> availableFiremodes = new ArrayList<>();
            for (FireMode f : shootingWeapon.getFireModeList()) {
                try {
                    if (f.isAvailable()) availableFiremodes.addList(f);
                } catch (NotAvailableAttributeException e) {
                    e.printStackTrace();
                }
            }
            //ASK: which firemode do you want to use? (Display: availableFiremode )
            FireMode fireMode = null; //the selected firemode;

            //ASK: which players do you want to select as target? (Display: firemode.findTargets())
            List<Player> targets = null; //the selected players;
        }
    */
    }


    /**
     * Checks if the user can reload weapons and, while he can, offers him the chance to reload it.
     * Ends when the user refuses.
     */
    public void reload(int max) {

        boolean none = false;

        while (!currentPlayer.getReloadableWeapons().isEmpty() && max > 0 && !none) {
            List<String> options = toStringList(currentPlayer.getReloadableWeapons());
            options.add("None");
            currentPlayerConnection.send(encode("Which weapon do you want to reload?", options));
            int selected = currentPlayerConnection.receive(options.size(), 10);
            if (selected == currentPlayer.getReloadableWeapons().size() + 1){
                none = true;
            }
            else {
                Weapon weaponToReload = currentPlayer.getReloadableWeapons().get(selected - 1);
                try {
                    weaponToReload.reload();
                    max--;
                    System.out.println(P + currentPlayer.getId() + " reloads " + weaponToReload + ".");
                } catch (NotAvailableAttributeException | WrongTimeException e) {
                    LOGGER.log(Level.SEVERE,"Exception thrown while reloading", e);
                }
            }
        }

    }

    /**
     * Checks if the user can reload weapons and, while he can, offers him the chance to reload it.
     * Ends when the user refuses.
     */
    public void reloadMandatory() {

        List<Weapon> options = new ArrayList<>();
        for (Weapon w : currentPlayer.getReloadableWeapons()) {
            try {
                if (!currentPlayer.getShootingSquares(0, new ArrayList<>(Arrays.asList(w))).isEmpty()) {
                    options.add(w);
                }
            } catch (NotAvailableAttributeException e) {LOGGER.log(Level.SEVERE,"NotAvailableAttributeException thrown while reloading", e);}
        }
        currentPlayerConnection.send(encode("You have to reload one of these weapons to shoot. Which one do you choose?", options));
        int selected = currentPlayerConnection.receive(options.size(), 10);
        Weapon weaponToReload = options.get(selected - 1);
        try {
            weaponToReload.reload();
            System.out.println(P + currentPlayer.getId() + " reloads " + weaponToReload + ".");
        } catch (NotAvailableAttributeException | WrongTimeException e) { LOGGER.log(Level.SEVERE,"Exception thrown while reloading", e); }

    }


    /**
     * Replaces all the items collected during the turn.
     * If the weapon deck is empty, the collected weapons are not replaced.
     */
    private void replaceWeapons() {

        for (WeaponSquare s : board.getSpawnPoints()) {
            if (!board.getWeaponDeck().getDrawable().isEmpty()) {
                while (s.getWeapons().size() < 3) {
                    try {
                        s.addCard();
                    } catch (UnacceptableItemNumberException | NoMoreCardsException e) {
                        LOGGER.log(Level.SEVERE, "No more cards in the weapon deck. No new weapons will be introduced in the game.", e);
                    }
                }
            }
        }
    }

    private void replaceAmmoTiles(){

        for (Square s : board.getMap()){
            if (!board.getSpawnPoints().contains(s) && !((AmmoSquare)s).hasAmmoTile()){
                try {
                    s.addAllCards();
                } catch (UnacceptableItemNumberException | NoMoreCardsException e){LOGGER.log(Level.SEVERE,"Exception thrown while replacing ammo tiles", e);}
            }
        }
    }

}