package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static it.polimi.ingsw.controller.Encoder.*;

/**
 * Manages a turn, displaying the main events on the console.
 *
 */

//TODO: finish implementing handleShooting() and properly modify the code depending on the shooting process.
// Implement the connection with the server.
// The actual model needs to be updated only when the action is confirmed.
// Finish testing.
// Use a logger (also in other classes).
// Correct the code reading SonarLint issues.

public class TurnManager implements Runnable{

    private Board board;
    private PlayerController currentPlayerConnection;
    private List<PlayerController> playerConnections;
    private Player currentPlayer;
    private List<Player> dead;
    private KillShotTrack killShotTrack;
    private boolean frenzy;

    /**
     * Constructs a turn manager with a reference to the board, the current player and the list of players.
     *
     * @param board                         the board of the game.
     * @param currentPlayerConnection       the current player PlayerController.
     * @param playerConnections             the list of PlayerController of the players in the game.
     */
    public TurnManager(Board board, PlayerController currentPlayerConnection, List<PlayerController> playerConnections, boolean frenzy){
        this.board = board;
        this.currentPlayerConnection = currentPlayerConnection;
        this.playerConnections = playerConnections;
        this.currentPlayer = this.currentPlayerConnection.getModel();
        this.dead = new ArrayList<>();
        try {
            this.killShotTrack = this.board.getKillShotTrack();
        } catch (NotAvailableAttributeException e){ e.printStackTrace();}
        this.frenzy = frenzy;
    }

    /**
     * Runs a turn.
     */
    public void run() {

        dead.clear();

        //Extra part for the first turn
        if (!currentPlayer.isInGame()) {
            joinBoard(currentPlayer, 2);
        }

        //Normal turn actions
        int actionsLeft = 2;
        while (actionsLeft > 0) {

            if (executeAction()) {
                actionsLeft--;
            }

        }

        //allows to use or convert a power up, if possible
        usePowerUp();
        convertPowerUp();

        //handles the reloading process
        reload(3);

        //checks who died, rewards killers, make the dead draw a power up and respawn
        for (Player deadPlayer: dead){
            try {
                System.out.println("The killers are awarded for the death of Player " + deadPlayer.getId() + "." );
                deadPlayer.rewardKillers();
                if (deadPlayer.getDamages().size() == 11){
                    killShotTrack.registerKill(currentPlayer, deadPlayer, false);
                }
                else killShotTrack.registerKill(currentPlayer, deadPlayer, true);
                joinBoard(deadPlayer, 1);
                if (frenzy){
                    deadPlayer.setFlipped(true);
                    deadPlayer.setPointsToGive(2);
                }
            } catch (WrongTimeException | UnacceptableItemNumberException e) {e.printStackTrace();}
        }
        if (dead.size() > 1) {
            currentPlayer.addPoints(1);
            System.out.println("Player " + currentPlayer.getId() + " gets an extra point for the multiple kill!!!");
        }


        //replaces items
        replaceItems();

        System.out.println("Player " + currentPlayer.getId() + " ends his turn.\n\n");
        for (Player p: board.getActivePlayers()){
            System.out.println("Player " + p.getId() + ": \t\t" + p.getPoints() + " points \t\t"+ p.getDamages().size() + " damages.");
        }

        System.out.println("\n\n\n");


    }


    /**
     * Adds a player to the board, at the beginning of the game of after his death.
     * The player draws the specified number of powerups and is positioned on the powerup color spawn point.
     *
     * @param player                the player to add to the board.
     * @param powerUpToDraw         the number of powerups the player has to draw.
     */
    public void joinBoard(Player player, int powerUpToDraw) {

        //draw two powerUps
        for (int i = 0; i < powerUpToDraw; i++) {
            try {
                player.drawPowerUp();
            } catch (NoMoreCardsException | UnacceptableItemNumberException e) {e.printStackTrace();}
        }

        //ASK: which one do you want to discard? (Display: player.getPowerUpList())
        currentPlayerConnection.send("Which powerUp do you want to discard?", encode(player.getPowerUpList()) );
        int selected = currentPlayerConnection.receive(player.getPowerUpList().size(), 10);
        PowerUp discarded = player.getPowerUpList().get(selected-1);

        if (powerUpToDraw == 2) System.out.println("Player " + player.getId() + " draws two powerups and discards a " + discarded.toStringLowerCase() + ".");
        else System.out.println("Player " + player.getId() + " draws a powerup and discards a " + discarded.toStringLowerCase() + ".");

        Color birthColor = discarded.getColor();
        player.discardPowerUp(discarded);

        //place the player on the board
        for (WeaponSquare s : board.getSpawnPoints()) {
            if (s.getColor() == birthColor) player.setPosition(s);
        }

        player.setInGame(true);
        player.refreshActionList();

        System.out.println("Player " + player.getId() + " enters in the board in the " + discarded.getColor().toStringLowerCase() + " spawn point.");

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

        try {
            canUSePowerUp = currentPlayer.hasUsableTeleporterOrNewton();
        }catch (NotAvailableAttributeException e){e.printStackTrace();}

        List<String> options = encode(currentPlayer.getActionList());
        if (canUSePowerUp){
            options.add("Use Powerup");
        }
        if (!currentPlayer.getPowerUpList().isEmpty()){
            options.add("Convert Powerup");
        }

        currentPlayerConnection.send("What do you want to do?", options);

        int selected = currentPlayerConnection.receive(options.size(), 10);

        if (selected == currentPlayer.getActionList().size() + 1){
            if (canUSePowerUp){
                usePowerUp();
            }
            else convertPowerUp();
            return false;
        }

        else if (selected == currentPlayer.getActionList().size() + 2){
            convertPowerUp();
            return false;
        }

        else {
            executeActualAction(selected);
            return true;
        }

    }


    /**
     * Checks if the user has a teleporter or a newton and, while he has one, offers him the chance to use it.
     * Ends when the user refuses.
     */
    public void usePowerUp(){

        int answer = 1;
        boolean possible = false;
        try {
            possible = currentPlayer.hasUsableTeleporterOrNewton();
        } catch (NotAvailableAttributeException e){e.printStackTrace();}

        while (possible && answer == 1) {

            List<String> options = new ArrayList<>();
            options.addAll(Arrays.asList("yes", "no"));

            currentPlayerConnection.send("Do you want to use a powerup?", options);
            answer = currentPlayerConnection.receive(2, 10);

            if (answer == 1) {

                System.out.println("Player " + currentPlayer.getId() + " decides to use a powerup." );

                List<Player> targets = new ArrayList<>();

                List<PowerUp> usablePowerUps = currentPlayer.getPowerUpList();
                List<PowerUp> toRemove = new ArrayList<>();
                for (PowerUp p: usablePowerUps){
                    if (p.getName() == PowerUp.PowerUpName.TARGETING_SCOPE || p.getName() == PowerUp.PowerUpName.TAGBACK_GRENADE) {
                        toRemove.add(p);
                    }
                }
                usablePowerUps.removeAll(toRemove);

                currentPlayerConnection.send("Which powerup do you want to use?", encode(usablePowerUps));
                int selected = currentPlayerConnection.receive(currentPlayer.getPowerUpList().size(), 10);
                PowerUp powerUpToUse = currentPlayer.getPowerUpList().get(selected-1);

                System.out.printf("Player " + currentPlayer.getId() + " decides to use a " + powerUpToUse.getName().toString() + ".\nHe moves");

                if (powerUpToUse.getName() == PowerUp.PowerUpName.NEWTON) {

                    try{
                        currentPlayerConnection.send("Who do you want to choose as a target?",  encode(powerUpToUse.findTargets()));
                        selected = currentPlayerConnection.receive(powerUpToUse.findTargets().size(), 10);
                        targets = powerUpToUse.findTargets().get(selected-1);
                        System.out.printf(" Player " + targets.get(0).getId());
                    } catch(NotAvailableAttributeException e){e.printStackTrace();}
                } else {
                    targets.add(currentPlayer);
                }

                try {
                    currentPlayerConnection.send("Choose a destination", encode(powerUpToUse.findDestinations(targets)));
                    selected = currentPlayerConnection.receive(powerUpToUse.findDestinations(targets).size(), 10);
                    Square destination = powerUpToUse.findDestinations(targets).get(selected - 1);
                    powerUpToUse.applyEffects(targets, destination);
                    currentPlayer.discardPowerUp(powerUpToUse);
                    System.out.printf(" in " + destination.toString() + ".\n");


                } catch (NotAvailableAttributeException e) {e.printStackTrace();}

            }

            try {
                possible = currentPlayer.hasUsableTeleporterOrNewton();
            } catch (NotAvailableAttributeException e){e.printStackTrace();}

        }
    }


    /**
     * Checks if the user has a powerup and, while he has one, offers him the chance to convert it to gain an ammo.
     * Ends when the user refuses.
     */
    public void convertPowerUp() {

        int answer = 1;

        while (!currentPlayer.getPowerUpList().isEmpty() && answer == 1) {

            List<String> options = new ArrayList<>();
            options.addAll(Arrays.asList("yes", "no"));

            currentPlayerConnection.send("Do you want to convert a powerup?", options);
            answer = currentPlayerConnection.receive(2, 10);

            if (answer == 1) {

                System.out.println("Player " + currentPlayer.getId() + " decides to convert a powerup.");
            }

            currentPlayerConnection.send("Which powerup do you want to convert?", encode(currentPlayer.getPowerUpList()));
            int selected = currentPlayerConnection.receive(currentPlayer.getPowerUpList().size(), 10);
            PowerUp powerUpToConvert = currentPlayer.getPowerUpList().get(selected - 1);
            currentPlayer.useAsAmmo(powerUpToConvert);

            System.out.println("Player " + currentPlayer.getId() + " converts a  " + powerUpToConvert.toStringLowerCase() + " into an ammo.");
        }

    }


    /**
     * Handles the process of shooting.
     * Must be modified implementing a real method instead of a random generator of damage.
     */
    public void handleShooting(){

        System.out.println("Player " + currentPlayer.getId() + " shoots.");

        for (Player p: board.getActivePlayers()) {
            if (p != currentPlayer) {
                int damage = (new Random()).nextInt(4);
                p.sufferDamage((damage), currentPlayer);
                if (damage == 1) System.out.println("He does " + damage + " damage to Player " + p.getId() + ".");
                else System.out.println("He does " + damage + " damages to Player " + p.getId() + ".");
            }
            p.refreshActionList();
            if (p.isDead()&&!dead.contains(p)){
                dead.add(p);
                System.out.println("Player " + p.getId() + " is dead.");
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
                    if (f.isAvailable()) availableFiremodes.add(f);
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
            List<String> options = encode(currentPlayer.getReloadableWeapons());
            options.add("None");
            currentPlayerConnection.send("Which weapon do you want to reload?", options);
            int selected = currentPlayerConnection.receive(options.size(), 10);
            if (selected == currentPlayer.getReloadableWeapons().size() + 1){
                none = true;
            }
            else {
                Weapon weaponToReload = currentPlayer.getReloadableWeapons().get(selected - 1);
                try {
                    weaponToReload.reload();
                    max--;
                    System.out.println("Player " + currentPlayer.getId() + " reloads " + weaponToReload + ".");
                } catch (NotAvailableAttributeException | WrongTimeException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * Replaces all the items collected during the turn.
     * If the weapon deck is empty, the collected weapons are not replaced.
     */
    private void replaceItems(){
        for (WeaponSquare s : board.getSpawnPoints()){
            while (s.getWeapons().size() < 3){
                try {
                    s.addCard();
                } catch (UnacceptableItemNumberException |NoMoreCardsException e){e.printStackTrace();}
            }
        }

        for (Square s : board.getMap()){
            if (!board.getSpawnPoints().contains(s)){
                if (!((AmmoSquare)s).hasAmmoTile()){
                    try {
                        ((AmmoSquare)s).addAllCards();
                    } catch (UnacceptableItemNumberException | NoMoreCardsException e){e.printStackTrace();}
                }
            }
        }

    }

    public void executeActualAction(int selected){

        Action action = currentPlayer.getActionList().get(selected-1);

        System.out.println("Player " + currentPlayer.getId() + " chooses the action: " + action);

        try {
            if (action.getSteps() > 0) {
                List<Square> possibleDestinations = board.getReachable(currentPlayer.getPosition(), (action.getSteps()));
                if (!action.isCollect() && !action.isShoot()){
                    possibleDestinations.remove(currentPlayer.getPosition());
                }
                else if (action.isCollect()){
                    List<Square> toRemove = new ArrayList<>();
                    for (Square square: possibleDestinations){
                        if (square.isEmpty()){
                            toRemove.add(square);
                        }
                    }
                    possibleDestinations.removeAll(toRemove);

                }
                List<String> destOptions = encode(possibleDestinations);
                currentPlayerConnection.send("Where do you wanna move?", destOptions);
                selected = currentPlayerConnection.receive(destOptions.size(), 10);
                Square dest = possibleDestinations.get(selected-1);
                if (!dest.equals(currentPlayer.getPosition())){
                    currentPlayer.setPosition(dest);
                    System.out.println("Player " + currentPlayer.getId() + " moves in " + currentPlayer.getPosition() + ".");
                }

            }
        } catch (NotAvailableAttributeException e) {e.printStackTrace();}

        try {
            if (action.isCollect()) {
                if (board.getSpawnPoints().contains(currentPlayer.getPosition())) {
                    currentPlayerConnection.send("Which weapon do you want to collect?", encode(((WeaponSquare)currentPlayer.getPosition()).getWeapons()));
                    selected = currentPlayerConnection.receive((((WeaponSquare)currentPlayer.getPosition()).getWeapons()).size(), 10);
                    Weapon collectedWeapon = ((WeaponSquare)currentPlayer.getPosition()).getWeapons().get(selected-1);
                    currentPlayer.collect(collectedWeapon);
                    System.out.println("Player " + currentPlayer.getId() + " collects  " + collectedWeapon + ".");

                } else {
                    AmmoTile toCollect = ((AmmoSquare) currentPlayer.getPosition()).getAmmoTile();
                    currentPlayer.collect(toCollect);
                    System.out.println("Player " + currentPlayer.getId() + " collects an ammo tile.");
                    if (toCollect.hasPowerUp()){
                        System.out.println("It allows him to draw a power up.");
                    }

                }

            }
        } catch (NotAvailableAttributeException | NoMoreCardsException | UnacceptableItemNumberException e) {
            e.printStackTrace();
        }

        if (action.isReload()) {
            reload(1);
        }

        if (action.isShoot()) {
            handleShooting();
        }
    }


}