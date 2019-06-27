package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Updater;
import it.polimi.ingsw.model.board.*;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.network.server.VirtualView;

import java.util.*;
import java.util.logging.*;


import static it.polimi.ingsw.model.cards.FireMode.FireModeName.*;
import static it.polimi.ingsw.network.server.VirtualView.ChooseOptionsType.*;

/**
 * Manages a turn, displaying the main events on the console.
 * Every time the user has to take a decision, he is given the possibility to reset his action.
 * The extent of the resets varies depending on the game phase.
 * The user is asked for a confirmation after every action and after he is given the possibility to actually see the effects of his actions.
 * The only exception is the collecting of an ammotile: since a powerup could be drawn after this (and being it a random event)
 * the user must confirm his action before collecting the ammotile.
 *
 * @author BassaniRiccardo
 */

// TODO: - finish implementing handleShooting() and properly modify the code depending on the shooting process.
//        the code is now implemented but commented out since some changes need to be done in weapon factory
//        before it can perform properly
//      - Implement the connection with the server.
//      - Finish testing.
//      - Use a logger (also in other classes).
//      - WaitAll for grenade
//      - TOO MANY UPDATES ARE SENT (3 for each movement), whole model is sent multiple times in the same turn
//      - discuss confirmations


public class TurnManager {

    private Board board;
    private StatusSaver statusSaver;
    private List<VirtualView> playerConnections;
    private VirtualView currentPlayerConnection;
    private Player currentPlayer;
    private List<Integer> dead;
    private KillShotTrack killShotTrack;
    private GameEngine gameEngine;

    private boolean frenzy;
    private int actionsLeft;

    private static final Logger LOGGER = Logger.getLogger("serverLogger");
    private static final String EX_CAN_USE_POWERUP ="NotAvailableAttributeException thrown while checking if the player can use a powerup";
    private static final String SELECT ="select";
    private static final String RESET ="Reset";
    private static final String RESET_ACTION = " resets the action";


    /**
     * Constructs a turn manager with a reference to the board, the current player and the list of players.
     *
     * @param board                         the board of the game.
     * @param currentPlayerConnection       the current player PlayerController.
     */
    public TurnManager(GameEngine gameEngine, Board board, VirtualView currentPlayerConnection, List<VirtualView> playerConnections, StatusSaver statusSaver,  boolean frenzy){
        this.gameEngine = gameEngine;
        this.board = board;
        this.statusSaver = statusSaver;
        this.playerConnections = playerConnections;
        this.currentPlayerConnection = currentPlayerConnection;
        this.currentPlayer = this.currentPlayerConnection.getModel();
        this.dead = new ArrayList<>();
        try {
            this.killShotTrack = this.board.getKillShotTrack();
        } catch (NotAvailableAttributeException e){ LOGGER.log(Level.SEVERE,"NotAvailableAttributeException thrown while setting the kill shot track", e);}
        this.frenzy = frenzy;
        this.actionsLeft=2;
        LOGGER.setLevel(Level.ALL);
    }

    /**
     * Runs a turn.
     */
    public void runTurn() throws NotEnoughPlayersException, SlowAnswerException {

        System.out.println("inside turnManager");
        currentPlayerConnection.display("\nIt's your turn!\n");

        dead.clear();
        updateAndSendModel();

        try {
            if (!currentPlayer.isInGame()) {
                joinBoard(currentPlayer, 2, false);
            }

            for (Player p : board.getActivePlayers()) {
                p.refreshActionList();
            }

            actionsLeft = 2;
            if (currentPlayer.getStatus() == Player.Status.FRENZY_2) actionsLeft--;
            while (actionsLeft > 0) {
                currentPlayer.refreshActionList();
                LOGGER.log(Level.FINE, "Actions left: {0} ", actionsLeft);
                if (executeAction()) {                  //confirm or go back------>checkpoint
                    actionsLeft--;
                    LOGGER.log(Level.FINE, "Action executed or reset:");
                    LOGGER.log(Level.FINE, "Actions left: {0}", actionsLeft);
                }

                LOGGER.log(Level.FINE, "All actions executed");
            }

            for (Player p : board.getPlayers()) {
                LOGGER.log(Level.FINEST, p + ": damages: " + p.getDamages().size());
            }

            //------>checkpoint
            updateAndNotifyAll();

            boolean choice1 = handleUsingPowerUp();
            boolean choice2 = convertPowerUp(false);
            boolean choice3 = reload(3);

            if (choice1 || choice2 || choice3) {
                while (!askConfirmation("Do you confirm the ending phase?")) {
                    LOGGER.log(Level.FINE, "{0} resets the action", currentPlayer);
                    statusSaver.restoreCheckpoint();
                    board.addToUpdateQueue(Updater.getModel(board, currentPlayer), currentPlayerConnection);
                    board.revertUpdates(currentPlayerConnection);
                    board.notifyObserver(currentPlayerConnection);
                    board.setReset(false);
                    handleUsingPowerUp();
                    convertPowerUp(false);
                    reload(3);
                }
            }

        } catch(SlowAnswerException e){
            statusSaver.restoreCheckpoint();
            replaceWeapons();
            replaceAmmoTiles();
            board.addToUpdateQueue(Updater.getModel(board, currentPlayer), currentPlayerConnection);
            board.revertUpdates(currentPlayerConnection);
            board.notifyObserver(currentPlayerConnection);
            throw new SlowAnswerException("Exception propagted from TurnManager");
        }

        //checks who died, rewards killers, make the dead draw a power up and respawn
        for (Player deadPlayer : board.getActivePlayers()) {
            if (dead.contains(deadPlayer.getId())) {
                try {
                    LOGGER.log(Level.FINE, "The killers are awarded for the death of {0}.", deadPlayer);
                    for (Player p : board.getPlayers()) {
                        LOGGER.log(Level.FINEST, p + ": damages: " + p.getDamages().size());
                    }
                    deadPlayer.rewardKillers();
                    killShotTrack.registerKill(currentPlayer, deadPlayer, deadPlayer.getDamages().size() > 11);

                    //necessary otherwise a reset would give back the damages to the dead
                    updateAndNotifyAll();
                    if (!(killShotTrack.getSkullsLeft()==0 && !frenzy || gameEngine.isLastFrenzyPlayer()))
                        joinBoard(deadPlayer, 1, true);
                    if (frenzy) {
                        deadPlayer.setFlipped(true);
                        deadPlayer.setPointsToGive(2);
                    }
                } catch (WrongTimeException | UnacceptableItemNumberException e) {
                    LOGGER.log(Level.SEVERE, "Exception thrown while resolving the deaths", e);
                }
            }
        }
        if (dead.size() > 1) {
            currentPlayer.addPoints(1);
            LOGGER.log(Level.FINE, () -> currentPlayer + " gets an extra point for the multiple kill!!!");
        }

        replaceWeapons();
        replaceAmmoTiles();

        updateAndNotifyAll();

        LOGGER.log(Level.FINE, () -> currentPlayer + " ends his turn.\n\n");
        for (Player p : board.getActivePlayers()) {
            LOGGER.log(Level.FINE, () -> p + ": \t\t" + p.getPoints() + " points \t\t" + p.getDamages().size() + " damages.");
        }

        currentPlayerConnection.display("\nEnd of the turn\n");

    }


    /**
     * Adds a player to the board, at the beginning of the game of after his death.
     * The player draws the specified number of powerups and is positioned on the powerup color spawn point.
     *
     * @param player                the player to addList to the board.
     * @param powerUpToDraw         the number of powerups the player has to draw.
     */
    public void joinBoard(Player player, int powerUpToDraw, boolean reborn) throws SlowAnswerException, NotEnoughPlayersException {

        //the user draws two powerups
        for (int i = 0; i < powerUpToDraw; i++) {
            try {
                player.drawPowerUp();
            } catch (NoMoreCardsException | UnacceptableItemNumberException | WrongTimeException e) {LOGGER.log(Level.SEVERE,"Exception thrown while drawing a powerup", e);}
        }

        player.setInGame(true);
        statusSaver.updatePowerups();

        //it could give some problems since not all the attributes are available
        board.notifyObservers();

        //asks the user which powerup he wants to discard
        getVirtualView(player).choose(CHOOSE_POWERUP.toString(), "Which powerUp do you want to discard?", player.getPowerUpList());
        int selected = Integer.parseInt(gameEngine.wait(getVirtualView(player)));
        PowerUp discarded = player.getPowerUpList().get(selected-1);

        if (powerUpToDraw == 2)
            LOGGER.log(Level.FINE, () -> player + " draws two powerups and discards a " + discarded.toString() + ".");
        else if (powerUpToDraw == 1)
            LOGGER.log(Level.FINE, () -> player  + " draws a powerup and discards a " + discarded.toString() + ".");
        else if (powerUpToDraw == 0)
            LOGGER.log(Level.FINE, () -> player  + " discards a " + discarded.toString() + ".");
        Color birthColor = discarded.getColor();
        player.discardPowerUp(discarded);

        board.notifyObserver(getVirtualView(player));

        //place the player on the board
        for (WeaponSquare s : board.getSpawnPoints()) {
            if (s.getColor() == birthColor) player.setPosition(s);
        }

        player.refreshActionList();

        board.notifyObserver(getVirtualView(player));

        LOGGER.log(Level.FINE, () -> player  + " enters in the board in the " + discarded.getColor().toStringLowerCase() + " spawn point.");

        if (!askConfirmation("Do you confirm the spawning?", player)) resetJoinBoard(player, reborn);
        else updateAndNotifyAll();


    }


    /**
     * Interacts with the user showing him the actions he can make.
     * Also allows to use or convert a powerup if possible.
     *
     * @return      true if an actual action is performed by the user.
     *              false otherwise (if the user decides to use or convert a powerup).
     */
    public boolean executeAction() throws SlowAnswerException, NotEnoughPlayersException{

        board.setReset(false);

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

        currentPlayerConnection.choose(CHOOSE_STRING.toString(), "What do you want to do?", options);

        int selected = Integer.parseInt(gameEngine.wait(currentPlayerConnection));

        if (selected == availableActions.size() + 1){
            if (canUSePowerUp){
                //handleUsingPowerUp();
                usePowerUp();
            }
            else convertPowerUp(true);
            return false;
        }

        else if (selected == availableActions.size() + 2){
            convertPowerUp(true);
            return false;
        }

        else {
            executeActualAction(availableActions.get(selected-1));
            return true;
        }

    }


    /**
     * Interacts with the user showing him the actual actions he can make.
     *
     */
    public void executeActualAction(Action action) throws SlowAnswerException, NotEnoughPlayersException{

        LOGGER.log(Level.FINE, () -> currentPlayer  + " chooses the action: " + action);

        if (action.getSteps() > 0) {
            handleMoving(action);
        }
        if (board.isReset()) return;
        if (action.isCollect()) {
            handleCollecting();
        }
        if (board.isReset()) return;
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
        if (board.isReset()) return;
        if (action.isShoot()) {
            try {
                handleShooting();
            } catch (NotAvailableAttributeException e){LOGGER.log(Level.SEVERE, "NotAvailableAttributeException thrown while shooting", e);}
        }

    }


    /**
     * Checks if the user has a usable newton or a teleporte and, while he has one, offers him the chance to use it.
     *
     * @return      true if entering the method the player could use a powerup.
     *              false otherwise.
     */
    public boolean handleUsingPowerUp() throws SlowAnswerException, NotEnoughPlayersException{

        System.out.println("entering handleUsingPowerUp");

        board.setReset(false);

        int answer = 1;
        boolean possible = false;
        try {
            possible = currentPlayer.hasUsableTeleporterOrNewton();
        } catch (NotAvailableAttributeException e){LOGGER.log(Level.SEVERE, EX_CAN_USE_POWERUP, e);}

        if (!possible) {
            System.out.println("No usable powerUps: return false");
            return false;
        }

        while (possible && answer == 1 && !board.isReset()) {

            System.out.println("entering while");

            List<String> options = new ArrayList<>(Arrays.asList("yes", "no"));

            currentPlayerConnection.choose(CHOOSE_STRING.toString(), "Do you want to use a powerup?", options);
            answer = Integer.parseInt(gameEngine.wait(currentPlayerConnection));
            if (answer == 2){
                System.out.println("answer no");
                LOGGER.log(Level.FINE, () -> currentPlayer  + " decides not to use a powerup." );
            }
            if (answer == 1) {
                System.out.println("answer yes");
                usePowerUp();
            }
            try {
                possible = currentPlayer.hasUsableTeleporterOrNewton();
            } catch (NotAvailableAttributeException e){LOGGER.log(Level.SEVERE, EX_CAN_USE_POWERUP, e);}

        }

        System.out.println("exiting handle usingpowerup");

        return  true;
    }


    /**
     * Asks the user which powerup he wants to use and how.
     * Activates the effect of the selected powerup.
     */
    public void usePowerUp() throws SlowAnswerException, NotEnoughPlayersException {

        board.setReset(false);

        LOGGER.log(Level.FINE, () -> currentPlayer  + " decides to use a powerup." );

        List<Player> targets = new ArrayList<>();

        List<PowerUp> usablePowerUps = new ArrayList<>(currentPlayer.getPowerUpList());
        List<PowerUp> toRemove = new ArrayList<>();
        for (PowerUp p: usablePowerUps){
            if (p.getName() == PowerUp.PowerUpName.TARGETING_SCOPE || p.getName() == PowerUp.PowerUpName.TAGBACK_GRENADE) {
                toRemove.add(p);
            }
        }
        usablePowerUps.removeAll(toRemove);

        List<String> optionsPowerUps = toStringList(usablePowerUps);
        optionsPowerUps.add(RESET);

        currentPlayerConnection.choose(CHOOSE_POWERUP.toString(), "Which powerup do you want to use?", optionsPowerUps);
        int selected = Integer.parseInt(gameEngine.wait(currentPlayerConnection));

        if (selected == optionsPowerUps.size()){
            resetPowerUp();
            return;
        }
        PowerUp powerUpToUse = usablePowerUps.get(selected - 1);
        LOGGER.log(Level.FINE, () -> currentPlayer + " decides to use a " + powerUpToUse.getName().toString() + ".");
        if (powerUpToUse.getName() == PowerUp.PowerUpName.NEWTON) {
            try {
                List<String> optionsTargets = toUserStringList(powerUpToUse.findTargets());
                optionsTargets.add(RESET);
                currentPlayerConnection.choose(CHOOSE_PLAYER.toString(), "Who do you want to choose as a target?", optionsTargets);
                selected = Integer.parseInt(gameEngine.wait(currentPlayerConnection));
                if (selected == optionsTargets.size()){
                    resetPowerUp();
                    return;
                }
                targets = powerUpToUse.findTargets().get(selected - 1);
            } catch (NotAvailableAttributeException e) {
                LOGGER.log(Level.SEVERE, "NotAvailableAttributeException thrown while searching for the powerup targets", e);
            }
        } else targets.add(currentPlayer);

        try {
            List<String> optionsDest = toStringList(powerUpToUse.findDestinations(targets));
            optionsDest.add(RESET);
            currentPlayerConnection.choose(CHOOSE_SQUARE.toString(), "Choose a destination", optionsDest);
            selected = Integer.parseInt(gameEngine.wait(currentPlayerConnection));
            if (selected == optionsDest.size()) {
                resetPowerUp();
                return;
            }
            Square destination = powerUpToUse.findDestinations(targets).get(selected - 1);
            powerUpToUse.applyEffects(targets, destination);
            currentPlayer.discardPowerUp(powerUpToUse);
            board.notifyObserver(currentPlayerConnection);

            if (targets.contains(currentPlayer))
                LOGGER.log(Level.FINE, () -> "He moves in " + destination.toString() + ".");
            else
                LOGGER.log(Level.FINE, "He moves Player " + targets.get(0).getId() + " in " + destination.toString() + ".\n");
        }catch (NotAvailableAttributeException e) {
            LOGGER.log(Level.SEVERE, "NotAvailableAttributeException thrown while searching for the powerup destinations", e);
        }

    }


    /**
     * Checks if the user has a powerup and, while he has one, offers him the chance to convert it to gain an ammo.
     *
     * @return      true if entering the method the player could use a powerup.
     *              false otherwise.
     */
    public boolean convertPowerUp(boolean inActions) throws SlowAnswerException, NotEnoughPlayersException {

        board.setReset(false);


        int answer = 1;
        if (currentPlayer.getPowerUpList().isEmpty()){
            return false;
        }

        while (!currentPlayer.getPowerUpList().isEmpty() && answer == 1 && !board.isReset()) {

            if (!inActions) {

                List<String> options = new ArrayList<>(Arrays.asList("yes", "no"));

                currentPlayerConnection.choose(CHOOSE_STRING.toString(), "Do you want to convert a powerup?", options);
                answer = Integer.parseInt(gameEngine.wait(currentPlayerConnection));
            }

            if (!inActions && answer == 2){
                LOGGER.log(Level.FINE, () -> currentPlayer + " decides not to convert a powerup.");
            }

            if (inActions || answer == 1) {

                LOGGER.log(Level.FINE, () -> currentPlayer + " decides to convert a powerup.");

                List<String> optionsConvert = toStringList(currentPlayer.getPowerUpList());
                optionsConvert.add(RESET);

                currentPlayerConnection.choose(CHOOSE_POWERUP.toString(), "Which powerup do you want to convert?", optionsConvert);
                int selected = Integer.parseInt(gameEngine.wait(currentPlayerConnection));
                if (selected==optionsConvert.size()) {
                    return resetConvert();
                }
                PowerUp powerUpToConvert = currentPlayer.getPowerUpList().get(selected - 1);
                currentPlayer.useAsAmmo(powerUpToConvert);
                board.notifyObserver(currentPlayerConnection);

                LOGGER.log(Level.FINE, () -> currentPlayer + " converts a  " + powerUpToConvert.toString() + " into an ammo.");
                inActions = false;
            }

        }
        return true;

    }


    /**
     * Handles the process of moving.
     */
    public void handleMoving(Action action) throws SlowAnswerException, NotEnoughPlayersException{
        try {

            board.setReset(false);

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

            List<String> optionsDest = toStringList(possibleDestinations);
            optionsDest.add(RESET);

            LOGGER.log(Level.FINE, currentPlayer + " is in " + currentPlayer.getPosition());
            currentPlayerConnection.choose(CHOOSE_SQUARE.toString(), "Where do you wanna move?", optionsDest);
            int selected = Integer.parseInt(gameEngine.wait(currentPlayerConnection));
            if (selected == optionsDest.size()){
                resetAction();
                return;
            }
            Square dest = possibleDestinations.get(selected - 1);
            currentPlayer.setPosition(dest);
            board.notifyObserver(currentPlayerConnection);
            LOGGER.log(Level.FINE, currentPlayer + " moves in " + currentPlayer.getPosition() + ".");
            //update current player model
            if (!action.isShoot() && !action.isCollect()) {
                if (!askConfirmation("Do you confirm the movement?")) resetAction();
                else updateAndNotifyAll();

            }

        } catch (NotAvailableAttributeException e) {LOGGER.log(Level.SEVERE,"NotAvailableAttributeException thrown while handling the moving process", e);}
    }

    /**
     * Handles the process of collecting.
     */

    public void handleCollecting() throws SlowAnswerException, NotEnoughPlayersException {
        try {

            board.setReset(false);

            if (board.getSpawnPoints().contains(currentPlayer.getPosition())) {
                List<Weapon> collectible = currentPlayer.getCollectibleWeapons((WeaponSquare)currentPlayer.getPosition());
                List<String> optionsCollectible = toStringList(collectible);
                optionsCollectible.add(RESET);
                currentPlayerConnection.choose(CHOOSE_WEAPON.toString(), "Which weapon do you want to collect?", optionsCollectible);
                int selected = Integer.parseInt(gameEngine.wait(currentPlayerConnection));
                if (selected == optionsCollectible.size()){
                    resetAction();
                    return;
                }
                Weapon collectedWeapon = (collectible.get(selected-1));
                currentPlayer.collect(collectedWeapon);
                board.notifyObserver(currentPlayerConnection);
                LOGGER.log(Level.FINE, () -> currentPlayer + " collects  " + collectedWeapon + ".");
                if (currentPlayer.getWeaponList().size()>3){
                    List<String> optionsToDiscard = toStringList(currentPlayer.getWeaponList());
                    optionsToDiscard.add(RESET);
                    currentPlayerConnection.choose(CHOOSE_WEAPON.toString(), "Which weapon do you want to discard?", optionsToDiscard);
                    selected = Integer.parseInt(gameEngine.wait(currentPlayerConnection));
                    if(selected == optionsToDiscard.size()){
                        resetAction();
                        return;
                    }
                    Weapon discardedWeapon = currentPlayer.getWeaponList().get(selected-1);
                    currentPlayer.discardWeapon(discardedWeapon);
                    //todo maybe move to model
                    discardedWeapon.setLoaded(false);
                    ((WeaponSquare) currentPlayer.getPosition()).addCard(discardedWeapon);
                    board.notifyObserver(currentPlayerConnection);
                    LOGGER.log(Level.FINE, () -> currentPlayer + " discards  " + discardedWeapon + ".");
                }
                if (!askConfirmation("Do you confirm the collecting?")){
                    resetAction();
                }
                else updateAndNotifyAll();

            }
            else {
                if (!askConfirmation("Do you confirm the collecting?")){
                    resetAction();
                    return;
                }
                AmmoTile toCollect = ((AmmoSquare) currentPlayer.getPosition()).getAmmoTile();
                boolean tooManyPowerUps = !currentPlayer.collect(toCollect);
                board.notifyObserver(currentPlayerConnection);
                LOGGER.log(Level.FINE, () -> currentPlayer + " collects an ammo tile.");
                if (toCollect.hasPowerUp()){
                    if (tooManyPowerUps) LOGGER.log(Level.FINE, "It would him to draw a power up, but he already has three.");
                    else LOGGER.log(Level.FINE, "It allows him to draw a power up.");
                }
                updateAndNotifyAll();
            }
        }
        catch (NotAvailableAttributeException | NoMoreCardsException | UnacceptableItemNumberException | WrongTimeException e) {
            LOGGER.log(Level.SEVERE,"Exception thrown while handling the collecting process", e);
        }

    }


    /**
     * Handles the process of shooting.
     */

    //TODO Must be modified implementing a real method instead of a random generator of damage.
    // Tagback_grenade and targeting scope must be considered too.

    public void handleShooting() throws NotAvailableAttributeException, SlowAnswerException, NotEnoughPlayersException{

        currentPlayer.getMainTargets().clear();
        currentPlayer.getOptionalTargets().clear();

        List<Weapon> availableWeapons = new ArrayList<>(currentPlayer.getAvailableWeapons());
        List<String> optionsWeapons = toStringList(availableWeapons);
        optionsWeapons.add(RESET);

        currentPlayerConnection.choose(CHOOSE_WEAPON.toString(), "Choose your weapon", optionsWeapons);
        int selected1 = Integer.parseInt(gameEngine.wait(currentPlayerConnection));
        if (selected1 == optionsWeapons.size()){
            resetAction();
            return;
        }
        Weapon selectedWeapon = availableWeapons.get(selected1 - 1);
        LOGGER.log(Level.FINE, () -> currentPlayer + " chooses " + selectedWeapon );

        boolean stop = false;
        boolean canStop = false;
        List<FireMode.FireModeName> usedFireModes = new ArrayList<>();
        while (!stop) {
            List<Integer> oldDamages = getDamagesList();
            //get the usable firemodes
            List<FireMode> remainingFiremodes = new ArrayList<>(selectedWeapon.listAvailableFireModes());
            List<FireMode> toRemove = new ArrayList<>();
            for (FireMode f : remainingFiremodes ){
                if (usedFireModes.contains(f.getName()) || usedFireModes.contains(MAIN) && f.getName()==SECONDARY || usedFireModes.contains(SECONDARY) && f.getName()==MAIN ){
                    toRemove.add(f);
                }
            }
            remainingFiremodes.removeAll(toRemove);

            List<String> options = toStringList(remainingFiremodes);
            if (!options.isEmpty()){
                options.add(RESET);
                if (canStop){
                    options.add("none");
                    currentPlayerConnection.choose(CHOOSE_STRING.toString(), "If you want, select an additional firemode", options);
                }
                else currentPlayerConnection.choose(CHOOSE_STRING.toString(), "Select a firemode in order to shoot", options);
                int selected2 = Integer.parseInt(gameEngine.wait(currentPlayerConnection));
                if (selected2 == remainingFiremodes.size() + 1){
                    resetAction();
                    return;
                }
                if (selected2 == remainingFiremodes.size() + 2){
                    LOGGER.log(Level.FINE, () -> currentPlayer + " decides not to add another firemode" );
                    stop = true;
                }
                else {
                    FireMode selectedFireMode = remainingFiremodes.get(selected2 - 1);
                    if (selectedFireMode.getName() == MAIN || selectedFireMode.getName() == SECONDARY) canStop = true;
                    LOGGER.log(Level.FINE, () -> currentPlayer + SELECT + selectedFireMode + " as firemode");
                    usedFireModes.add(selectedFireMode.getName());
                    applyFireMode(selectedFireMode);

                    if (board.isReset()) return;

                    //targeting scope
                    List<Integer> newDamages = getDamagesList();
                    List<Player> targets = new ArrayList<>();
                    for (Player p : board.getActivePlayers()) {
                        if (!newDamages.get(board.getActivePlayers().indexOf(p)).equals(oldDamages.get(board.getActivePlayers().indexOf(p)))) {
                            targets.add(p);
                        }
                    }
                    while (currentPlayer.hasUsableTargetingScope() && !targets.isEmpty()) {
                        //if he choose not to use it stop asking
                        if (!handleTargetingScope(currentPlayer, targets)) break;
                        if (board.isReset()) return;
                    }
                }
            }
            else break;
        }

        selectedWeapon.setLoaded(false);
        board.notifyObserver(currentPlayerConnection);

        if (!askConfirmation("Do you confirm the shooting action?")){
            resetAction();
            return;
        }

        else updateAndNotifyAll();

        currentPlayerConnection.display("Asking hit players if they want to use a tagback grenade.");

        //grenade
        for (Player p : board.getActivePlayers()){
            System.out.println(p);
            System.out.println(p.hasUsableTagbackGrenade());
            System.out.println(p.isJustDamaged());
            if (!p.equals(currentPlayer) && p.hasUsableTagbackGrenade() && p.isJustDamaged())
                getVirtualView(p).display(currentPlayer.userToString() + " shot you.");
            while (!p.equals(currentPlayer) && p.hasUsableTagbackGrenade() && p.isJustDamaged()){
                System.out.println("entering while");
                if (!handleTagbackGrenade(p)) break;
            }
        }

        currentPlayerConnection.display("Your turn continues.\n");


        //update deaths
        for (Player p: board.getActivePlayers()) {
            //p.refreshActionList();
            if (p.isDead() && !dead.contains(p.getId())) {
                dead.add(p.getId());
                LOGGER.log(Level.FINE, () -> currentPlayer + " is dead.");
                if (p.isOverkilled()) LOGGER.log(Level.FINE, "It is an overkill!!!");
                if (dead.size() > 1) LOGGER.log(Level.FINE, () -> currentPlayer + "Multiple kill for Player " + currentPlayer.getId() + "!!!");
            }
        }

    }

    private void applyFireMode(FireMode fireMode) throws NotAvailableAttributeException, SlowAnswerException, NotEnoughPlayersException {

        board.setReset(false);

        List<List<Player>> targetsList = new ArrayList<>(fireMode.getTargetFinder().find(currentPlayer));
        List<List<Player>> toRemove = new ArrayList<>();
        for (List<Player> l : targetsList) {
            if (l.isEmpty()) toRemove.add(l);
        }
        for (List<Player> l : toRemove){
            targetsList.remove(l);
        }

        List<String> optionsTarget = toUserStringList(targetsList);
        optionsTarget.add(RESET);

        currentPlayerConnection.choose(CHOOSE_PLAYER.toString(), "Choose targets", optionsTarget);
        int selected = Integer.parseInt(gameEngine.wait(currentPlayerConnection));
        if (selected == optionsTarget.size()){
            resetAction();
            return;
        }
        List<Player> targets = targetsList.get(selected - 1);
        LOGGER.log(Level.FINE, () -> currentPlayer + " select " + targets + " as target");


        List<Square> destinations = fireMode.getDestinationFinder().find(currentPlayer, targets);
        Square destination = board.getMap().get(0);
        if (!destinations.isEmpty()) {
            List<String> optionsDest = toStringList(destinations);
            optionsDest.add(RESET);
            currentPlayerConnection.choose(CHOOSE_SQUARE.toString(), "Choose a destination", optionsDest);
            selected = Integer.parseInt(gameEngine.wait(currentPlayerConnection));
            if (selected == optionsDest.size()){
                resetAction();
                return;
            }
            destination = destinations.get(selected - 1);
            LOGGER.log(Level.FINE, currentPlayer + " select " + destination + " as destination");

        }

        if (fireMode.getName() == MAIN || fireMode.getName() == SECONDARY) currentPlayer.addMainTargets(targets);
        else if (fireMode.getName() == OPTION1 || fireMode.getName() == OPTION2) currentPlayer.addOptionalTargets(targets);
        try {
            System.out.println("before applying firemode");
            fireMode.applyEffects(targets, destination);
            System.out.println("after applying firemode");
            board.notifyObserver(currentPlayerConnection);
            System.out.println("after notifying applying firemode");
        } catch (IllegalArgumentException e){LOGGER.log(Level.SEVERE, "Error in shooting: " + fireMode);}

    }


    /**
     * Checks if the user can reload weapons and, while he can, offers him the chance to reload it.
     *
     * @param max       the maximun number the player can reload in the current game phase (1 or 3), ignoring his actual weapons.
     * @return          true if entering the method the player could use a powerup.
     *                  false otherwise.
     */
    public boolean reload(int max) throws SlowAnswerException, NotEnoughPlayersException{
        
        int left = max;

        board.setReset(false);
        if (currentPlayer.getReloadableWeapons().isEmpty()) {
            return false;
        }
        boolean none = false;
        while (!currentPlayer.getReloadableWeapons().isEmpty() && left > 0 && !none) {
            List<String> options = toStringList(currentPlayer.getReloadableWeapons());
            options.add("None");
            options.add(RESET);
            currentPlayerConnection.choose(CHOOSE_WEAPON.toString(), "Which weapon do you want to reload?", options);
            int selected = Integer.parseInt(gameEngine.wait(currentPlayerConnection));
            if (selected == currentPlayer.getReloadableWeapons().size() + 2) {
                resetAction();
                return true;
            }
            if (selected == currentPlayer.getReloadableWeapons().size() + 1){
                none = true;
            }
            else {
                Weapon weaponToReload = currentPlayer.getReloadableWeapons().get(selected - 1);
                try {
                    weaponToReload.reload();
                    board.notifyObserver(currentPlayerConnection);
                    left--;
                    LOGGER.log(Level.FINE, () -> currentPlayer + " reloads " + weaponToReload + ".");
                } catch (NotAvailableAttributeException | WrongTimeException e) {
                    LOGGER.log(Level.SEVERE,"Exception thrown while reloading", e);
                }
            }
        }
        if (max!=3) {
            if (!askConfirmation("Do you confirm your choices in the reloading process?")) resetAction();
            else updateAndNotifyAll();
        }
        return true;

    }


    /**
     * Checks if the user can reload weapons and, while he can, offers him the chance to reload it.
     */
    public void reloadMandatory() throws SlowAnswerException, NotEnoughPlayersException {

        board.setReset(false);

        List<Weapon> reloadable = new ArrayList<>();
        for (Weapon w : currentPlayer.getReloadableWeapons()) {
            try {
                if (!currentPlayer.getShootingSquares(0, new ArrayList<>(Arrays.asList(w))).isEmpty()) {
                    reloadable.add(w);
                }
            } catch (NotAvailableAttributeException e) {LOGGER.log(Level.SEVERE,"NotAvailableAttributeException thrown while reloading", e);}
        }
        List<String> options = toStringList(reloadable);
        options.add(RESET);
        currentPlayerConnection.choose(CHOOSE_WEAPON.toString(), "You have to reload one of these weapons to shoot. Which one do you choose?", options);
        int selected = Integer.parseInt(gameEngine.wait(currentPlayerConnection));
        if (selected == options.size()){
            resetAction();
            return;
        }
        Weapon weaponToReload = reloadable.get(selected - 1);
        try {
            weaponToReload.reload();
            board.notifyObserver(currentPlayerConnection);
            LOGGER.log(Level.FINE, () -> currentPlayer + " reloads " + weaponToReload + ".");
        } catch (NotAvailableAttributeException | WrongTimeException e) { LOGGER.log(Level.SEVERE,"Exception thrown while reloading", e); }
        if (!askConfirmation("Do you confirm the reloading?")) resetAction();
        else updateAndNotifyAll();
    }


    /**
     * Replaces all the weapons collected during the turn.
     * If the weapon deck is empty, the collected weapons are not replaced.
     */
    private void replaceWeapons() {

        for (WeaponSquare s : board.getSpawnPoints()) {
            if (!board.getWeaponDeck().getDrawable().isEmpty()) {
                while (s.getWeapons().size() < 3) {
                    if (!board.getWeaponDeck().getDrawable().isEmpty()) {
                        try {
                            s.addCard();
                        } catch (UnacceptableItemNumberException | NoMoreCardsException e) {
                            LOGGER.log(Level.SEVERE, "No more cards in the weapon deck. No new weapons will be introduced in the game.", e);
                        }
                    }
                    else break;
                }
            }
        }
    }


    /**
     * Replaces all the ammo tiles collected during the turn.
     */
    private void replaceAmmoTiles(){

        for (Square s : board.getMap()){
            if (!board.getSpawnPoints().contains(s) && !((AmmoSquare)s).hasAmmoTile()){
                try {
                    s.addAllCards();
                } catch (UnacceptableItemNumberException | NoMoreCardsException e){LOGGER.log(Level.SEVERE,"Exception thrown while replacing ammo tiles", e);}
            }
        }
    }


    /**
     * Returns a list containing the number of damages of every player.
     *
     * @return     a list containing the number of damages of every player.
     */
    private List<Integer> getDamagesList(){

        List<Integer> damages = new ArrayList<>();
        for (Player p : board.getPlayers()){
            damages.add(p.getDamages().size());
        }
        return damages;
    }


    /**
     * Handles the game phase in which the shooter has to decide whether to use a targeting scope against one or more hit enemies.
     *
     * @param currentPlayer             the current player, hence the shooter.
     * @param targets                   the players who have been damaged by the last firemode.
     * @return                          true if the current player decides to use at least one targeting scope.
     *                                  false otherwise.
     */
    public boolean handleTargetingScope(Player currentPlayer, List<Player> targets ) throws SlowAnswerException, NotEnoughPlayersException{

        board.setReset(false);
        currentPlayerConnection.choose(CHOOSE_STRING.toString(), "Do you want to use a targeting scope?", new ArrayList<>(Arrays.asList("yes", "no")));
        int answer = Integer.parseInt(gameEngine.wait(currentPlayerConnection));
        if (answer == 1){
            List<String> optionsPowerup = toStringList(currentPlayer.getPowerUps(PowerUp.PowerUpName.TARGETING_SCOPE));
            optionsPowerup.add(RESET);
            currentPlayerConnection.choose(CHOOSE_POWERUP.toString(), "Which targeting scope do you want to use?", optionsPowerup);
            int selected = Integer.parseInt(gameEngine.wait(currentPlayerConnection));
            if (selected == optionsPowerup.size()){
                resetAction();
                return true;
            }
            PowerUp targetingScope = currentPlayer.getPowerUps(PowerUp.PowerUpName.TARGETING_SCOPE).get(selected-1);

            List<String> optionsTargets = toUserStringList(Arrays.asList(targets));
            optionsTargets.add(RESET);
            currentPlayerConnection.choose(CHOOSE_PLAYER.toString(), "Who do you want to target?", optionsTargets);
            selected = Integer.parseInt(gameEngine.wait(currentPlayerConnection));
            if (selected == optionsTargets.size()){
                resetAction();
                return true;
            }
            Player target = targets.get(selected-1);

            try {
                targetingScope.applyEffects(new ArrayList<>(Arrays.asList(target)), board.getMap().get(0));
                currentPlayer.discardPowerUp(targetingScope);
                board.notifyObserver(currentPlayerConnection);
            } catch (NotAvailableAttributeException e) {LOGGER.log(Level.SEVERE, "NotAvailableAttributeException thrown while using the targeting scope", e);}

            return true;
        }

        return false;

    }


    /**
     * Handles the game phase in which a damaged player has to decide whether to use a tagback grenade against the shooter.
     *
     * @param p         the damaged player.
     * @return          true if the damaged player decided to use at least one tagback grenade.
     *                  false otherwise.
     */
    private boolean handleTagbackGrenade(Player p) throws SlowAnswerException, NotEnoughPlayersException{

        System.out.println("entering handleGrenade");

        VirtualView player = getVirtualView(p);
        player.choose(CHOOSE_STRING.toString(), "Do you want to use a tagback grenade?", new ArrayList(Arrays.asList("yes", "no")) );
        int answer = Integer.parseInt(gameEngine.wait(player));
        if (answer == 1){
            LOGGER.log(Level.FINE, () -> p + "Decides to use a grenade" );
            List<String> optionsGrenade = toStringList(p.getPowerUps(PowerUp.PowerUpName.TAGBACK_GRENADE));
            optionsGrenade.add(RESET);
            player.choose(CHOOSE_POWERUP.toString(), "Which tagback grenade do you want to use?", optionsGrenade);
            int selected = Integer.parseInt(gameEngine.wait(player));
            if (selected == optionsGrenade.size()){
                return (handleTagbackGrenade(p));
            }
            PowerUp tagbackGrenade = p.getPowerUps(PowerUp.PowerUpName.TAGBACK_GRENADE).get(selected-1);

            try {
                tagbackGrenade.applyEffects(new ArrayList<>(Arrays.asList(currentPlayer)), board.getMap().get(0));
                p.discardPowerUp(tagbackGrenade);
                board.notifyObserver(player);
            } catch (NotAvailableAttributeException e) {LOGGER.log(Level.SEVERE, "NotAvailableAttributeException thrown while using the tagback grenade", e);}
            if (!askConfirmation("Do you confirm your decisions about grenades?", p)){
                statusSaver.restoreCheckpoint();
                board.addToUpdateQueue(Updater.getModel(board, p), getVirtualView(p));
                board.notifyObserver(getVirtualView(p));
                return(handleTagbackGrenade(p));
            }
            else {
                System.out.println("entering in else");
                try {
                    System.out.println("entering in try");
                    System.out.println(!p.hasUsableTagbackGrenade());
                    if (!p.hasUsableTagbackGrenade())
                        getVirtualView(p).display("The turn of " + currentPlayer + "continues.\n");
                } catch (NotAvailableAttributeException e){LOGGER.log(Level.SEVERE, "NotAvailableAttributeException thrown while looking for player's grenade", e);}
                updateAndNotifyAll();
            }
            return true;
        }
        LOGGER.log(Level.FINE, () -> p + "Decides not to use a grenade" );
        if (!askConfirmation("Do you confirm your decisions about grenades?", p)){
            statusSaver.restoreCheckpoint();
            board.notifyObserver(getVirtualView(p));
            return(handleTagbackGrenade(p));
        }

        else {
            getVirtualView(p).display("The turn of " + currentPlayer.userToString() + " continues.\n");
            updateAndNotifyAll();
        }


        System.out.println("exiting handleGrenade");

        return false;
    }


    /**
     * Asks the current player whether he wants to confirm his last action.
     *
     * @param request           a string specifying which action must be confirmed or deleted.
     * @return                  true if the current player decides to confirm the action.
     */
    private boolean askConfirmation(String request) throws SlowAnswerException, NotEnoughPlayersException{
        return askConfirmation(request, currentPlayer);
    }


    /**
     * Asks a specific player whether he wants to confirm his last action.
     *
     * @param request           a string specifying which action must be confirmed or deleted.
     * @param p                 the player the question is asked.
     * @return                  true if the player decides to confirm the action.
     */
    private boolean askConfirmation(String request, Player p) throws SlowAnswerException,NotEnoughPlayersException{

        getVirtualView(p).choose(CHOOSE_STRING.toString(), request, new ArrayList(Arrays.asList("yes", "no")));
        int answer = Integer.parseInt(gameEngine.wait(getVirtualView(p)));
        if (answer == 1){
            LOGGER.log(Level.FINE, "action confirmed");
            return true;
        }
        return false;

    }

//TODO check if the probelms that occurred with the powerup is solved now that setInGame is below restorepowerUps


    /**
     * Deletes the effects of the last joinBoard() call.
     *
     * @param p             the player who is joining the board or reborning.
     * @param reborn        true if the player is reborning.
     *                      false if the player is joining the board for the first time.
     */
    private void resetJoinBoard(Player p, boolean reborn) throws SlowAnswerException, NotEnoughPlayersException{
        LOGGER.log(Level.FINE, () -> p + RESET_ACTION);
        // if the player is entering the board for the first time only powerups can and must be restored
        // the player must also be set as out of the game (graphically he is removed form the map)
        if (!reborn) {
            statusSaver.restorePowerUps();
            p.setInGame(false);
        }
        //if the player is reborning, everything must be restored:  powerups        positions           isDead
        else statusSaver.restoreCheckpoint();
        board.addToUpdateQueue(Updater.getModel(board, p), getVirtualView(p));
        board.revertUpdates(getVirtualView(p));
        board.notifyObservers();
        joinBoard(p, 0, reborn);
    }


    /**
     * Resets all players powerups.
     */
    private void resetPowerUp() throws SlowAnswerException, NotEnoughPlayersException{
        LOGGER.log(Level.FINE, () -> currentPlayer + RESET_ACTION);
        statusSaver.restoreCheckpoint();
        board.addToUpdateQueue(Updater.getModel(board, currentPlayer), currentPlayerConnection);
        board.revertUpdates(currentPlayerConnection);
        board.notifyObserver(currentPlayerConnection);
        if (actionsLeft  == 0){
            handleUsingPowerUp();
        }
    }


    /**
     * Resets the effects of the last convertPowerUp call.
     */
    private boolean resetConvert() throws SlowAnswerException, NotEnoughPlayersException{
        LOGGER.log(Level.FINE, () -> currentPlayer + RESET_ACTION);
        System.out.println("AmmoPack before revert");
        System.out.println("red " + currentPlayer.getAmmoPack().getRedAmmo());
        System.out.println("blue" + currentPlayer.getAmmoPack().getBlueAmmo());
        System.out.println("yellow" + currentPlayer.getAmmoPack().getYellowAmmo());
        statusSaver.restoreCheckpoint();
        System.out.println("AmmoPack after revert");
        System.out.println("red " + currentPlayer.getAmmoPack().getRedAmmo());
        System.out.println("blue" + currentPlayer.getAmmoPack().getBlueAmmo());
        System.out.println("yellow" + currentPlayer.getAmmoPack().getYellowAmmo());
        board.addToUpdateQueue(Updater.getModel(board, currentPlayer), currentPlayerConnection);
        board.revertUpdates(currentPlayerConnection);
        board.notifyObserver(currentPlayerConnection);
        if (actionsLeft  == 0){
            boolean use1 = handleUsingPowerUp();
            boolean use2 = convertPowerUp(false);
            return (use1 || use2);
        }
        return true;
    }


    /**
     * Resets the effects of the last action.
     */
    private void resetAction() throws SlowAnswerException, NotEnoughPlayersException{
        LOGGER.log(Level.FINE, () -> currentPlayer + RESET_ACTION);

        System.out.println("AmmoPack before revert");
        System.out.println("red " + currentPlayer.getAmmoPack().getRedAmmo());
        System.out.println("blue" + currentPlayer.getAmmoPack().getBlueAmmo());
        System.out.println("yellow" + currentPlayer.getAmmoPack().getYellowAmmo());

        statusSaver.restoreCheckpoint();

        System.out.println("AmmoPack before revert");
        System.out.println("red " + currentPlayer.getAmmoPack().getRedAmmo());
        System.out.println("blue" + currentPlayer.getAmmoPack().getBlueAmmo());
        System.out.println("yellow" + currentPlayer.getAmmoPack().getYellowAmmo());

        board.addToUpdateQueue(Updater.getModel(board, currentPlayer), currentPlayerConnection);
        board.notifyObserver(currentPlayerConnection);
        if (actionsLeft == 0)
        {
            handleUsingPowerUp();
            convertPowerUp(false);
            reload(3);
        }
        else actionsLeft++;
        board.revertUpdates(currentPlayerConnection);
        board.notifyObservers();//notifyobserver could be enough
    }

    /**
     * Returns a list of string given a generic list.
     *
     * @param options       the generic list to transform.
     * @return              the String list.
     */
    public static List<String> toStringList(List options){
        List<String> encoded = new ArrayList<>();
        for (Object p : options ){
            encoded.add(p.toString());
        }
        return encoded;
    }

    /**
     * Returns a list of string given a generic list.
     *
     * @param playerGroups  the list of players to transform.
     * @return              the String list.
     */
    public static List<String> toUserStringList(List<List<Player>> playerGroups){
        List<String> encoded = new ArrayList<>();
        for (List<Player> p : playerGroups ) {
            StringBuilder builder = new StringBuilder();
            for (Player player : p) {
                if (!builder.toString().isEmpty())
                builder.append(", ");
                builder.append(player.userToString());
            }
            encoded.add(builder.toString());
        }
        return encoded;
    }


    public void updateAndSendModel(){
        statusSaver.updateCheckpoint();
        for(VirtualView p : playerConnections) {
            board.addToUpdateQueue(Updater.getModel(board, p.getModel()), p);
        }
        board.notifyObservers();
    }

    public void updateAndNotifyAll(){
        statusSaver.updateCheckpoint();
        board.notifyObservers();
    }


    public VirtualView getVirtualView(Player p){
        return playerConnections.get(board.getPlayers().indexOf(p));
    }
}