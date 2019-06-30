package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Updater;
import it.polimi.ingsw.model.board.*;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.network.server.VirtualView;

import java.util.*;
import java.util.logging.*;


import static it.polimi.ingsw.model.cards.Color.*;
import static it.polimi.ingsw.model.cards.FireMode.FireModeName.*;
import static it.polimi.ingsw.network.server.VirtualView.ChooseOptionsType.*;

/**
 * Manages a turn, handling the exchange of messages with the user.
 * Every time the user has to take a decision, he is given the possibility to reset his action.
 * The extent of the resets varies depending on the game phase.
 * The user is asked for a confirmation after every action and after he is given the possibility to actually see the effects of his actions.
 * The exception are:
 * - the collecting of an ammotile: since a powerup could be drawn after this (and being it a random event). The user must confirm his action before collecting the ammotile.
 * - the use of a tagback grenade, to avoid that the interruption of the shooter's turn becomes too long. The hit player cannot change is mind about the use of grenades.
 *
 * @author BassaniRiccardo
 */


    class TurnManager {

    private Board board;
    private StatusSaver statusSaver;
    private List<VirtualView> playerConnections;
    private VirtualView currentPlayerConnection;
    private Player currentPlayer;
    private List<Integer> dead;
    private KillShotTrack killShotTrack;
    private GameEngine gameEngine;
    private Timer timer;

    private boolean frenzy;
    private int actionsLeft;

    private static final Logger LOGGER = Logger.getLogger("serverLogger");
    private static final String EX_CAN_USE_POWERUP ="NotAvailableAttributeException thrown while checking if the player can use a powerup";
    private static final String SELECT ="select";
    private static final String RESET ="Reset";
    private static final String NONE ="None";
    private static final String YES ="Yes";
    private static final String NO ="No";
    private static final String RESET_ACTION = " resets the action.";
    private static final String TURN_START = "\nIt's your turn!\n";
    private static final String TURN_END = "\nEnd of the turn\n";

    private static final String ASK_ENDPHASE_CONFIRMATION = "Do you confirm the ending phase?";
    private static final String ASK_SPAWNING_CONFIRMATION = "Do you confirm the spawning?";
    private static final String ASK_MOVEMENT_CONFIRMATION = "Do you confirm the movement?";
    private static final String ASK_COLLECTING_CONFIRMATION = "Do you confirm the collecting?";
    private static final String ASK_SHOOTING_CONFIRMATION = "Do you confirm the shooting action?";
    private static final String ASK_RELOADING_CONFIRMATION = "Do you confirm your choices in the reloading process?";

    private static final String SELECT_POWERUP_TO_DISCARD = "Which powerUp do you want to discard?";
    private static final String SELECT_POWERUP_TO_USE = "Which powerup do you want to use?";

    private static final String USE_POWERUP = "Use Powerup";
    private static final String ASKING_TARGETS_GRENADES = "Asking hit players if they want to use a tagback grenade.";
    private static final String SHOT_YOU = " shot you.";
    private static final String THE_TURN_OF ="The turn of ";
    private static final String TURN_OF ="Turn of ";
    private static final String CONTINUES = " continues.\n";
    private static final String YOUR_TURN ="Your turn";

    private static final String DEMAND_USE_POWERUP = "Do you want to use a powerup?";
    private static final String DEMAND_USE_TARGETING_SCOPE = "Do you want to use a targeting scope?";
    private static final String DEMAND_USE_TAGBACK_GRENADE = "Do you want to use a tagback grenade?";

    private static final String CHOOSE_ACTION = "What do you want to do?";
    private static final String SELECT_TARGETS = "Who do you want to choose as a target?";
    private static final String SELECT_DESTINATION = "Choose a destination";
    private static final String SELECT_WHERE_TO_MOVE = "Where do you wanna move?";
    private static final String SELECT_WEAPON_TO_COLLECT = "Which weapon do you want to collect?";
    private static final String SELECT_WEAPON_TO_DISCARD = "Which weapon do you want to discard?";
    private static final String SELECT_WEAPON_TO_SHOOT = "Choose your weapon";
    private static final String SELECT_FIREMODE_OPTIONAL = "If you want, select an additional firemode";
    private static final String SELECT_FIREMODE_MANDATORY ="Select a firemode in order to shoot";
    private static final String SELECT_WEAPON_TO_RELOAD_OPTIONAL = "Which weapon do you want to reload?";
    private static final String SELECT_WEAPON_TO_RELOAD_MANDATORY = "You have to reload one of these weapons to shoot. Which one do you choose?";
    private static final String SELECT_TARGETING_SCOPE = "Which targeting scope do you want to use?";
    private static final String SELECT_TAGBACK_GRENADE = "Which tagback grenade do you want to use?";
    private static final String OPTIONAL_CONVERSION = "Do you want to convert a powerup to gain an ammo to make the payment?";
    private static final String MANDATORY_CONVERSION = "You must convert one of this powerups to pay. Choose one.";

    private static final String COMMA = ", ";

    private static final int GRENADE_SHORT_TIMER = 10;
    private static final int GRENADE_LONG_TIMER = 15;

    /**
     * Constructs a turn manager with a reference to the board, the current player and the list of players.
     *
     * @param board                         the board of the game.
     * @param currentPlayerConnection       the current player PlayerController.
     */
    TurnManager(GameEngine gameEngine, Board board, VirtualView currentPlayerConnection, List<VirtualView> playerConnections, StatusSaver statusSaver,  boolean frenzy, Timer timer){

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
        this.timer = timer;
        LOGGER.setLevel(Level.SEVERE);
    }

    /**
     * Runs a turn.
     *
     * @throws NotEnoughPlayersException    if the number of connected players falls below three during the turn.
     * @throws SlowAnswerException          if the user do not complete the turn before the timer expires.
     */
    void runTurn() throws NotEnoughPlayersException, SlowAnswerException {

        currentPlayerConnection.display(TURN_START + (frenzy ? " FRENZY IS ACTIVE!" : ""));
        for (VirtualView p : playerConnections){
            if (!p.equals(currentPlayerConnection)){
                p.display(TURN_OF + currentPlayer.userToString() + (frenzy ? ". FRENZY IS ACTIVE!" : ""));
            }
        }

        dead.clear();
        updateAndSendModel();

        try {
            if (!currentPlayer.isInGame()) {
                joinBoard(currentPlayer, 2, false);
            }

            currentPlayer.refreshActionList();
            actionsLeft = 2;
            if (currentPlayer.getStatus() == Player.Status.FRENZY_2) actionsLeft--;
            while (actionsLeft > 0) {
                LOGGER.log(Level.FINE, "Actions left: {0} ", actionsLeft);
                if (executeAction()) {                  //confirm or go back------>checkpoint
                    actionsLeft--;
                    LOGGER.log(Level.FINE, "Action executed or reset:");
                    LOGGER.log(Level.FINE, "Actions left: {0}", actionsLeft);
                }

                LOGGER.log(Level.FINE, "All actions executed");
            }

            for (Player p : board.getPlayers()) {
                String msg = p + ": damages: " + p.getDamages().size();
                LOGGER.log(Level.FINEST, msg);
            }

            updateAndNotifyAll();

            boolean choice1 = handleUsingPowerUp();
            boolean choice3 = reload(3);

            if (choice1 || choice3) {
                while (!askConfirmation(ASK_ENDPHASE_CONFIRMATION)) {
                    LOGGER.log(Level.FINE, "{0} resets the action", currentPlayer);
                    statusSaver.restoreCheckpoint();
                    board.addToUpdateQueue(Updater.getModel(board, currentPlayer), currentPlayerConnection);
                    board.revertUpdates(currentPlayerConnection);
                    board.notifyObserver(currentPlayerConnection);
                    board.setReset(false);
                    handleUsingPowerUp();
                    reload(3);
                }
            }

        } catch(SlowAnswerException e){
            if (!statusSaver.getPlayersPositions().isEmpty()) {
                statusSaver.restoreCheckpoint();
            }
            replaceWeapons();
            replaceAmmoTiles();
            board.addToUpdateQueue(Updater.getModel(board, currentPlayer), currentPlayerConnection);
            board.revertUpdates(currentPlayerConnection);
            board.notifyObserver(currentPlayerConnection);
            throw new SlowAnswerException("Exception propagated from TurnManager");
        }

        handleDeaths();
        replaceWeapons();
        replaceAmmoTiles();

        updateAndNotifyAll();

        LOGGER.log(Level.FINE, () -> currentPlayer + " ends his turn.\n\n");
        for (Player p : board.getActivePlayers()) {
            LOGGER.log(Level.FINE, () -> p + ": \t\t" + p.getPoints() + " points \t\t" + p.getDamages().size() + " damages.");
        }

        currentPlayerConnection.display(TURN_END);

    }


    /**
     * Adds a player to the board, at the beginning of the game of after his death.
     * The player draws the specified number of powerups and is positioned on the powerup color spawn point.
     *
     * @param player                the player to addList to the board.
     * @param powerUpToDraw         the number of powerups the player has to draw.
     * @throws NotEnoughPlayersException    if the number of connected players falls below three during the turn.
     * @throws SlowAnswerException          if the user do not complete the turn before the timer expires.
     */
    private void joinBoard(Player player, int powerUpToDraw, boolean reborn) throws SlowAnswerException, NotEnoughPlayersException {

        player.setInGame(true);

        for (int i = 0; i < powerUpToDraw; i++) {
            try {
                player.drawPowerUp();
            } catch (NoMoreCardsException | UnacceptableItemNumberException | WrongTimeException e) {LOGGER.log(Level.SEVERE,"Exception thrown while drawing a powerup", e);}
        }

        statusSaver.updatePowerups();

        //it could give some problems since not all the attributes are available
        board.notifyObservers();

        //asks the player which powerup he wants to discard
        getVirtualView(player).choose(CHOOSE_POWERUP.toString(), SELECT_POWERUP_TO_DISCARD, player.getPowerUpList());
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

        //place the player on the board
        for (WeaponSquare s : board.getSpawnPoints()) {
            if (s.getColor() == birthColor) player.setPosition(s);
        }

        if (frenzy){
            if (player.getId() > gameEngine.getFrenzyActivator()){
                player.setStatus(Player.Status.FRENZY_1);
            }
            else player.setStatus(Player.Status.FRENZY_2);
        }
        else
            player.setStatus(Player.Status.BASIC);

        board.notifyObserver(getVirtualView(player));

        LOGGER.log(Level.FINE, () -> player  + " enters in the board in the " + discarded.getColor().toStringLowerCase() + " spawn point.");

        if (!askConfirmation(ASK_SPAWNING_CONFIRMATION, player)) resetJoinBoard(player, reborn);
        else updateAndNotifyAll();


    }


    /**
     * Interacts with the user showing him the actions he can make.
     * Also allows to use or convert a powerup if possible.
     *
     * @return      true if an actual action is performed by the user.
     *              false otherwise (if the user decides to use or convert a powerup).
     * @throws NotEnoughPlayersException    if the number of connected players falls below three during the turn.
     * @throws SlowAnswerException          if the user do not complete the turn before the timer expires.
     */
    private boolean executeAction() throws SlowAnswerException, NotEnoughPlayersException{

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

        if (canUSePowerUp){ options.add(USE_POWERUP); }

        currentPlayerConnection.choose(CHOOSE_STRING.toString(), CHOOSE_ACTION, options);

        int selected = Integer.parseInt(gameEngine.wait(currentPlayerConnection));

        if (selected == availableActions.size() + 1){
                usePowerUp();
            return false;
        }

        else {
            executeActualAction(availableActions.get(selected-1));
            return true;
        }

    }


    /**
     * Interacts with the user showing him the actual actions he can make.
     * @throws NotEnoughPlayersException    if the number of connected players falls below three during the turn.
     * @throws SlowAnswerException          if the user do not complete the turn before the timer expires.
     */
    private void executeActualAction(Action action) throws SlowAnswerException, NotEnoughPlayersException{

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
     * @throws NotEnoughPlayersException    if the number of connected players falls below three during the turn.
     * @throws SlowAnswerException          if the user do not complete the turn before the timer expires.
     */
    private boolean handleUsingPowerUp() throws SlowAnswerException, NotEnoughPlayersException{

        board.setReset(false);

        int answer = 1;
        boolean possible = false;
        try {
            possible = currentPlayer.hasUsableTeleporterOrNewton();
        } catch (NotAvailableAttributeException e){LOGGER.log(Level.SEVERE, EX_CAN_USE_POWERUP, e);}

        if (!possible) {
            return false;
        }

        while (possible && answer == 1 && !board.isReset()) {

            List<String> options = new ArrayList<>(Arrays.asList(YES, NO));

            currentPlayerConnection.choose(CHOOSE_STRING.toString(), DEMAND_USE_POWERUP, options);
            answer = Integer.parseInt(gameEngine.wait(currentPlayerConnection));
            if (answer == 2){
                LOGGER.log(Level.FINE, () -> currentPlayer  + " decides not to use a powerup." );
            }
            if (answer == 1) {
                usePowerUp();
            }
            try {
                possible = currentPlayer.hasUsableTeleporterOrNewton();
            } catch (NotAvailableAttributeException e){LOGGER.log(Level.SEVERE, EX_CAN_USE_POWERUP, e);}

        }

        return  true;
    }


    /**
     * Asks the user which powerup he wants to use and how.
     * Activates the effect of the selected powerup.
     * @throws NotEnoughPlayersException    if the number of connected players falls below three during the turn.
     * @throws SlowAnswerException          if the user do not complete the turn before the timer expires.
     */
    private void usePowerUp() throws SlowAnswerException, NotEnoughPlayersException {

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

        currentPlayerConnection.choose(CHOOSE_POWERUP.toString(), SELECT_POWERUP_TO_USE , optionsPowerUps);
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
                currentPlayerConnection.choose(CHOOSE_PLAYER.toString(), SELECT_TARGETS, optionsTargets);
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
            currentPlayerConnection.choose(CHOOSE_SQUARE.toString(), SELECT_DESTINATION, optionsDest);
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
            else {
                String msg = "He moves Player " + targets.get(0).getId() + " in " + destination.toString() + ".\n";
                LOGGER.log(Level.FINE, msg);
            }
        }catch (NotAvailableAttributeException e) {
            LOGGER.log(Level.SEVERE, "NotAvailableAttributeException thrown while searching for the powerup destinations", e);
        }

    }


    /**
     * Handles the process of moving.
     * @throws NotEnoughPlayersException    if the number of connected players falls below three during the turn.
     * @throws SlowAnswerException          if the user do not complete the turn before the timer expires.
     */
    private void handleMoving(Action action) throws SlowAnswerException, NotEnoughPlayersException{
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

            String msg = currentPlayer + " is in " + currentPlayer.getPosition();
            LOGGER.log(Level.FINE, msg);
            currentPlayerConnection.choose(CHOOSE_SQUARE.toString(), SELECT_WHERE_TO_MOVE, optionsDest);
            int selected = Integer.parseInt(gameEngine.wait(currentPlayerConnection));
            if (selected == optionsDest.size()){
                resetAction();
                return;
            }
            Square dest = possibleDestinations.get(selected - 1);
            currentPlayer.setPosition(dest);
            board.notifyObserver(currentPlayerConnection);
            String ms = currentPlayer + " moves in " + currentPlayer.getPosition() + ".";
            LOGGER.log(Level.FINE, ms);
            //update current player model
            if (!action.isShoot() && !action.isCollect()) {
                if (!askConfirmation(ASK_MOVEMENT_CONFIRMATION)) resetAction();
                else updateAndNotifyAll();

            }

        } catch (NotAvailableAttributeException e) {LOGGER.log(Level.SEVERE,"NotAvailableAttributeException thrown while handling the moving process", e);}
    }


    /**
     * Handles the process of collecting.
     * @throws NotEnoughPlayersException    if the number of connected players falls below three during the turn.
     * @throws SlowAnswerException          if the user do not complete the turn before the timer expires.
     */

    private void handleCollecting() throws SlowAnswerException, NotEnoughPlayersException {
        try {

            board.setReset(false);

            if (board.getSpawnPoints().contains(currentPlayer.getPosition())) {
                List<Weapon> collectible = currentPlayer.getCollectibleWeapons((WeaponSquare)currentPlayer.getPosition());
                List<String> optionsCollectible = toStringList(collectible);
                optionsCollectible.add(RESET);
                currentPlayerConnection.choose(CHOOSE_WEAPON.toString(), SELECT_WEAPON_TO_COLLECT, optionsCollectible);
                int selected = Integer.parseInt(gameEngine.wait(currentPlayerConnection));
                if (selected == optionsCollectible.size()){
                    resetAction();
                    return;
                }
                Weapon collectedWeapon = (collectible.get(selected-1));
                handlePayment(collectedWeapon.getReducedCost());
                currentPlayer.collect(collectedWeapon);
                board.notifyObserver(currentPlayerConnection);
                LOGGER.log(Level.FINE, () -> currentPlayer + " collects  " + collectedWeapon + ".");
                if (currentPlayer.getWeaponList().size()>3){
                    List<String> optionsToDiscard = toStringList(currentPlayer.getWeaponList());
                    optionsToDiscard.add(RESET);
                    currentPlayerConnection.choose(CHOOSE_WEAPON.toString(), SELECT_WEAPON_TO_DISCARD, optionsToDiscard);
                    selected = Integer.parseInt(gameEngine.wait(currentPlayerConnection));
                    if(selected == optionsToDiscard.size()){
                        resetAction();
                        return;
                    }
                    Weapon discardedWeapon = currentPlayer.getWeaponList().get(selected-1);
                    currentPlayer.discardWeapon(discardedWeapon);
                    discardedWeapon.setLoaded(false);
                    ((WeaponSquare) currentPlayer.getPosition()).addCard(discardedWeapon);
                    board.notifyObserver(currentPlayerConnection);
                    LOGGER.log(Level.FINE, () -> currentPlayer + " discards  " + discardedWeapon + ".");
                }
                if (!askConfirmation(ASK_COLLECTING_CONFIRMATION)){
                    resetAction();
                }
                else updateAndNotifyAll();

            }
            else {
                if (!askConfirmation(ASK_COLLECTING_CONFIRMATION)){
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
     * @throws NotEnoughPlayersException    if the number of connected players falls below three during the turn.
     * @throws SlowAnswerException          if the user do not complete the turn before the timer expires.
     */
    private void handleShooting() throws NotAvailableAttributeException, SlowAnswerException, NotEnoughPlayersException{

        currentPlayer.getMainTargets().clear();
        currentPlayer.getOptionalTargets().clear();

        List<Weapon> availableWeapons = new ArrayList<>(currentPlayer.getAvailableWeapons());
        List<String> optionsWeapons = toStringList(availableWeapons);
        optionsWeapons.add(RESET);

        currentPlayerConnection.choose(CHOOSE_WEAPON.toString(), SELECT_WEAPON_TO_SHOOT, optionsWeapons);
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
                    options.add(NONE);
                    currentPlayerConnection.choose(CHOOSE_STRING.toString(), SELECT_FIREMODE_OPTIONAL, options);
                }
                else currentPlayerConnection.choose(CHOOSE_STRING.toString(), SELECT_FIREMODE_MANDATORY, options);
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

        if (!askConfirmation(ASK_SHOOTING_CONFIRMATION)){
            resetAction();
            return;
        }

        else updateAndNotifyAll();

        currentPlayerConnection.display(ASKING_TARGETS_GRENADES);
        timer.pause();
        askTargetsForGrenade();
        for (VirtualView v : playerConnections){
            if (!v.equals(currentPlayerConnection))
                v.display(THE_TURN_OF + currentPlayer.userToString() + CONTINUES);
            v.getModel().setJustDamaged(false);
        }
        updateAndNotifyAll();
        timer.resume();
        currentPlayerConnection.display(YOUR_TURN + CONTINUES);

        updateDead();

    }


    /**
     * Handles the process of selecting targets and destinations for a specific firemode, asking the user what he wants
     * to do and applying the effects of the firemode according to his preferences.
     *
     * @param fireMode                          the selected firemode.
     * @throws NotAvailableAttributeException   if thrown by TargetFinder.find() or Firemode.applyEffects().
     * @throws SlowAnswerException              if the user do not complete the turn before the timer expires.
     * @throws NotEnoughPlayersException        if the number of connected players falls below three during the turn.
     */
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

        currentPlayerConnection.choose(CHOOSE_PLAYER.toString(), SELECT_TARGETS, optionsTarget);
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
            currentPlayerConnection.choose(CHOOSE_SQUARE.toString(), SELECT_DESTINATION, optionsDest);
            selected = Integer.parseInt(gameEngine.wait(currentPlayerConnection));
            if (selected == optionsDest.size()){
                resetAction();
                return;
            }
            destination = destinations.get(selected - 1);
            String msg = currentPlayer + " select " + destination + " as destination";
            LOGGER.log(Level.FINE, msg);

        }

        if (fireMode.getName() == MAIN || fireMode.getName() == SECONDARY) currentPlayer.addMainTargets(targets);
        else if (fireMode.getName() == OPTION1 || fireMode.getName() == OPTION2) currentPlayer.addOptionalTargets(targets);
        try {
            handlePayment(fireMode.getCost());
            fireMode.applyEffects(targets, destination);
            board.notifyObserver(currentPlayerConnection);
        } catch (IllegalArgumentException e){LOGGER.log(Level.SEVERE, "Error in shooting: " + fireMode);}

    }


    /**
     * Checks if the user can reload weapons and, while he can, offers him the chance to reload it.
     *
     * @param max       the maximun number the player can reload in the current game phase (1 or 3), ignoring his actual weapons.
     * @return          true if entering the method the player could use a powerup.
     *                  false otherwise.
     * @throws SlowAnswerException              if the user do not complete the turn before the timer expires.
     * @throws NotEnoughPlayersException        if the number of connected players falls below three during the turn.
     */
    private boolean reload(int max) throws SlowAnswerException, NotEnoughPlayersException{

        int left = max;

        board.setReset(false);
        if (currentPlayer.getReloadableWeapons().isEmpty()) {
            return false;
        }
        boolean none = false;
        while (!currentPlayer.getReloadableWeapons().isEmpty() && left > 0 && !none) {
            List<String> options = toStringList(currentPlayer.getReloadableWeapons());
            options.add(NONE);
            options.add(RESET);
            currentPlayerConnection.choose(CHOOSE_WEAPON.toString(), SELECT_WEAPON_TO_RELOAD_OPTIONAL, options);
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
                    handlePayment(weaponToReload.getFullCost());
                    weaponToReload.reload();
                    board.notifyObserver(currentPlayerConnection);
                    left--;
                    LOGGER.log(Level.FINE, () -> currentPlayer + " reloads " + weaponToReload + ".");
                } catch ( WrongTimeException e) {
                    LOGGER.log(Level.SEVERE,"Exception thrown while reloading", e);
                }
            }
        }
        if (max!=3) {
            if (!askConfirmation(ASK_RELOADING_CONFIRMATION)) resetAction();
            else updateAndNotifyAll();
        }
        return true;

    }


    /**
     * Checks if the user can reload weapons and, while he can, offers him the chance to reload it.
     * @throws SlowAnswerException              if the user do not complete the turn before the timer expires.
     * @throws NotEnoughPlayersException        if the number of connected players falls below three during the turn.
     */
    private void reloadMandatory() throws SlowAnswerException, NotEnoughPlayersException {

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
        currentPlayerConnection.choose(CHOOSE_WEAPON.toString(), SELECT_WEAPON_TO_RELOAD_MANDATORY, options);
        int selected = Integer.parseInt(gameEngine.wait(currentPlayerConnection));
        if (selected == options.size()){
            resetAction();
            return;
        }
        Weapon weaponToReload = reloadable.get(selected - 1);
        try {
            handlePayment(weaponToReload.getFullCost());
            weaponToReload.reload();
            board.notifyObserver(currentPlayerConnection);
            LOGGER.log(Level.FINE, () -> currentPlayer + " reloads " + weaponToReload + ".");
        } catch ( WrongTimeException e) { LOGGER.log(Level.SEVERE,"Exception thrown while reloading", e); }
        if (!askConfirmation(ASK_RELOADING_CONFIRMATION)) resetAction();
        else updateAndNotifyAll();
    }


    /**
     * Handles the game phase in which the shooter has to decide whether to use a targeting scope against one or more hit enemies.
     *
     * @param currentPlayer             the current player, hence the shooter.
     * @param targets                   the players who have been damaged by the last firemode.
     * @return                          true if the current player decides to use at least one targeting scope.
     *                                  false otherwise.
     * @throws SlowAnswerException              if the user do not complete the turn before the timer expires.
     * @throws NotEnoughPlayersException        if the number of connected players falls below three during the turn.
     */
    private boolean handleTargetingScope(Player currentPlayer, List<Player> targets ) throws SlowAnswerException, NotEnoughPlayersException{

        board.setReset(false);
        currentPlayerConnection.choose(CHOOSE_STRING.toString(), DEMAND_USE_TARGETING_SCOPE, new ArrayList<>(Arrays.asList(YES, NO)));
        int answer = Integer.parseInt(gameEngine.wait(currentPlayerConnection));
        if (answer == 1){
            List<String> optionsPowerup = toStringList(currentPlayer.getPowerUps(PowerUp.PowerUpName.TARGETING_SCOPE));
            optionsPowerup.add(RESET);
            currentPlayerConnection.choose(CHOOSE_POWERUP.toString(), SELECT_TARGETING_SCOPE, optionsPowerup);
            int selected = Integer.parseInt(gameEngine.wait(currentPlayerConnection));
            if (selected == optionsPowerup.size()){
                resetAction();
                return true;
            }
            PowerUp targetingScope = currentPlayer.getPowerUps(PowerUp.PowerUpName.TARGETING_SCOPE).get(selected-1);
            List<String> optionsTargets = toUserStringList(Arrays.asList(targets));
            optionsTargets.add(RESET);
            currentPlayerConnection.choose(CHOOSE_PLAYER.toString(), SELECT_TARGETS, optionsTargets);
            selected = Integer.parseInt(gameEngine.wait(currentPlayerConnection));
            if (selected == optionsTargets.size()){
                resetAction();
                return true;
            }
            Player target = targets.get(selected-1);

            try {
                targetingScope.applyEffects(new ArrayList<>(Arrays.asList(target)), board.getMap().get(0));
                currentPlayer.discardPowerUp(targetingScope);
                handlePayment(targetingScope.getCost());
                board.notifyObserver(currentPlayerConnection);
            } catch (NotAvailableAttributeException e) {LOGGER.log(Level.SEVERE, "NotAvailableAttributeException thrown while using the targeting scope", e);}

            return true;
        }

        return false;

    }


    /**
     * Checks who between the targets can use a tagback grenade.
     * Ask the suitable targets for their choices and execute them.
     *
     * @throws NotEnoughPlayersException    if the number of connected players falls below three during the turn.
     * @throws NotAvailableAttributeException          if thrown by Player.hasUsableTagbackGrenade().
     */
    private void askTargetsForGrenade() throws NotEnoughPlayersException, NotAvailableAttributeException {

        for (Player p : board.getActivePlayers()) {
            System.out.println("asking to " + p);
            if (!p.equals(currentPlayer) && p.hasUsableTagbackGrenade() && p.isJustDamaged()) {
                getVirtualView(p).display(currentPlayer.userToString() + SHOT_YOU);
                boolean handleAgain = true;
                while (!p.equals(currentPlayer) && p.hasUsableTagbackGrenade() && p.isJustDamaged() && handleAgain) {
                    System.out.println("effectively asking to " + p);
                    handleAgain = handleTagbackGrenade(p);
                }
            }
            System.out.println("finished asking to " + p);
        }
    }


    /**
     * Handles the game phase in which a damaged player has to decide whether to use a tagback grenade against the shooter.
     *
     * @param p         the damaged player.
     * @return          true if the damaged player decided to use at least one tagback grenade.
     *                  false otherwise.
     * @throws NotEnoughPlayersException        if the number of connected players falls below three during the turn.
     */
    private boolean handleTagbackGrenade(Player p) throws NotEnoughPlayersException{

        System.out.println("entering handleTagbackGrenade");
        VirtualView player = getVirtualView(p);
        player.choose(CHOOSE_STRING.toString(), DEMAND_USE_TAGBACK_GRENADE, new ArrayList(Arrays.asList(NO, YES)), GRENADE_LONG_TIMER);
        int answer = Integer.parseInt(gameEngine.waitShort(player,GRENADE_LONG_TIMER));
        if (answer == 2){
            System.out.println("chooses yes");
            LOGGER.log(Level.FINE, () -> p + "Decides to use a grenade" );
            List<String> optionsGrenade = toStringList(p.getPowerUps(PowerUp.PowerUpName.TAGBACK_GRENADE));
            player.choose(CHOOSE_POWERUP.toString(), SELECT_TAGBACK_GRENADE, optionsGrenade, GRENADE_SHORT_TIMER);
            int selected = Integer.parseInt(gameEngine.waitShort(player, GRENADE_SHORT_TIMER));
            PowerUp tagbackGrenade = p.getPowerUps(PowerUp.PowerUpName.TAGBACK_GRENADE).get(selected-1);

            try {
                tagbackGrenade.applyEffects(new ArrayList<>(Collections.singletonList(currentPlayer)), board.getMap().get(0));
                p.discardPowerUp(tagbackGrenade);
                board.notifyObserver(player);
            } catch (NotAvailableAttributeException e) {LOGGER.log(Level.SEVERE, "NotAvailableAttributeException thrown while using the tagback grenade", e);}
            System.out.println("exiting handleTagbackGrenade");
            return true;
        }
        System.out.println("chooses no");
        LOGGER.log(Level.FINE, () -> p + "Decides not to use a grenade");
        System.out.println("exiting handleTagbackGrenade");
        return false;
    }


    /**
     * Checks who died during the turn.
     * Rewards killers, makes the dead draw a power up and respawn.
     *
     * @throws SlowAnswerException          if the user do not complete the turn before the timer expires.
     * @throws NotEnoughPlayersException    if the number of connected players falls below three during the turn.
     */
    private void handleDeaths() throws SlowAnswerException, NotEnoughPlayersException{

        for (Player deadPlayer : board.getActivePlayers()) {
            if (dead.contains(deadPlayer.getId())) {
                try {
                    LOGGER.log(Level.FINE, "The killers are awarded for the death of {0}.", deadPlayer);
                    for (Player p : board.getPlayers()) {
                        String msg = p + ": damages: " + p.getDamages().size();
                        LOGGER.log(Level.FINEST, msg);
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

    }


    /**
     * Replaces all the weapons collected during the turn.
     * If the weapon deck is empty, the collected weapons are not replaced.
     */
    void replaceWeapons() {

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
    void replaceAmmoTiles(){

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
     * Updates the list of dead players.
     */
    void updateDead() {
        for (Player p : board.getActivePlayers()) {
            if (p.isDead() && !dead.contains(p.getId())) {
                dead.add(p.getId());
                LOGGER.log(Level.FINE, () -> currentPlayer + " is dead.");
                if (p.isOverkilled()) LOGGER.log(Level.FINE, "It is an overkill!!!");
                if (dead.size() > 1)
                    LOGGER.log(Level.FINE, () -> currentPlayer + "Multiple kill for Player " + currentPlayer.getId() + "!!!");
            }
        }
    }


    /**
     * Asks the current player whether he wants to confirm his last action.
     *
     * @param request           a string specifying which action must be confirmed or deleted.
     * @return                  true if the current player decides to confirm the action.
     * @throws SlowAnswerException          if the user do not complete the turn before the timer expires.
     * @throws NotEnoughPlayersException    if the number of connected players falls below three during the turn.
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
     * @throws SlowAnswerException          if the user do not complete the turn before the timer expires.
     * @throws NotEnoughPlayersException    if the number of connected players falls below three during the turn.
     */
    private boolean askConfirmation(String request, Player p) throws SlowAnswerException,NotEnoughPlayersException{

        getVirtualView(p).choose(CHOOSE_STRING.toString(), request, new ArrayList(Arrays.asList(YES, NO)));
        int answer = Integer.parseInt(gameEngine.wait(getVirtualView(p)));
        if (answer == 1){
            LOGGER.log(Level.FINE, "action confirmed");
            return true;
        }
        return false;

    }


    /**
     * Deletes the effects of the last joinBoard() call.
     *
     * @param p             the player who is joining the board or reborning.
     * @param reborn        true if the player is reborning.
     *                      false if the player is joining the board for the first time.
     *
     * @throws SlowAnswerException          if the user do not complete the turn before the timer expires.
     * @throws NotEnoughPlayersException    if the number of connected players falls below three during the turn.
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
     * If it is called during the ending phase, it restarts the ending phase by calling handleUsingPowerUp().
     *
     * @throws SlowAnswerException          if the user do not complete the turn before the timer expires.
     * @throws NotEnoughPlayersException    if the number of connected players falls below three during the turn.
     */
    private void resetPowerUp() throws SlowAnswerException, NotEnoughPlayersException{
        restoreAndNotify();
        if (actionsLeft  == 0){
            handleUsingPowerUp();
        }
    }


    /**
     * Resets the effects of the last action.
     * If it is called during the ending phase, it restarts the ending phase by calling handleUsingPowerUp(), convertPowerUp(), reload().
     * Increments the number of action left since the last one was annulled.
     *
     * @throws SlowAnswerException          if the user do not complete the turn before the timer expires.
     * @throws NotEnoughPlayersException    if the number of connected players falls below three during the turn.
     */
    private void resetAction() throws SlowAnswerException, NotEnoughPlayersException{
        LOGGER.log(Level.FINE, () -> currentPlayer + RESET_ACTION);

        statusSaver.restoreCheckpoint();

        board.addToUpdateQueue(Updater.getModel(board, currentPlayer), currentPlayerConnection);
        board.notifyObserver(currentPlayerConnection);
        if (actionsLeft == 0)
        {
            handleUsingPowerUp();
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
    static List<String> toStringList(List options){
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
    static List<String> toUserStringList(List<List<Player>> playerGroups){
        List<String> encoded = new ArrayList<>();
        for (List<Player> p : playerGroups ) {
            StringBuilder builder = new StringBuilder();
            for (Player player : p) {
                if (!builder.toString().isEmpty())
                    builder.append(COMMA);
                builder.append(player.userToString());
            }
            encoded.add(builder.toString());
        }
        return encoded;
    }


    /**
     * Saves the model and sends it to all the connected players.
     */
    private void updateAndSendModel(){
        statusSaver.updateCheckpoint();
        for(VirtualView p : playerConnections) {
            board.addToUpdateQueue(Updater.getModel(board, p.getModel()), p);
        }
        board.notifyObservers();
    }


    /**
     * Saves the model and notify all the observers with the updates they must receive.
     */
    private void updateAndNotifyAll(){
        statusSaver.updateCheckpoint();
        board.notifyObservers();
    }


    /**
     * Restores the model to the last saved checkpoint, notifies the current player with the restored models and
     * removes the annulled changes form the other players queues.
     */
    private void restoreAndNotify(){
        LOGGER.log(Level.FINE, () -> currentPlayer + RESET_ACTION);
        statusSaver.restoreCheckpoint();
        board.addToUpdateQueue(Updater.getModel(board, currentPlayer), currentPlayerConnection);
        board.revertUpdates(currentPlayerConnection);
        board.notifyObserver(currentPlayerConnection);
    }


    /**
     * Returns the connection of the specified player.
     *
     * @param p     the player whose connection must be returned.
     * @return      the connection of the specified player.
     */
    private VirtualView getVirtualView(Player p){
        return playerConnections.get(board.getPlayers().indexOf(p));
    }


    /**
     * Returns the IDs of the dead players. Only for testing.
     *
     * @return the IDs of the dead players.
     */
    List<Integer> getDead() {return dead;}


    /**
     * Handles the payment, asking the player if he wants to convert some powerups instead of using his ammo.
     * The player is forced to convert powerups when they are necessary to pay.
     *
     * @param originalCost      the ammo to pay
     * @throws SlowAnswerException          if the user do not complete the turn before the timer expires.
     * @throws NotEnoughPlayersException    if the number of connected players falls below three during the turn.
     */
    private void handlePayment(AmmoPack originalCost) throws SlowAnswerException, NotEnoughPlayersException {

        AmmoPack cost = new AmmoPack(originalCost.getRedAmmo(), originalCost.getBlueAmmo(), originalCost.getYellowAmmo());

        if (cost.isEmpty())
            return;

        AmmoPack toPay = cost.getNeededAmmo(currentPlayer.getAmmoPack());

        while (toPay.getRedAmmo() > 0) {
            mandatoryConversion(RED);
            toPay.subAmmo(RED);
            cost.subAmmo(RED);
        }
        while (toPay.getBlueAmmo() > 0) {
            mandatoryConversion(BLUE);
            toPay.subAmmo(BLUE);
            cost.subAmmo(BLUE);
        }
        while (toPay.getYellowAmmo() > 0) {
            mandatoryConversion(YELLOW);
            toPay.subAmmo(YELLOW);
            cost.subAmmo(YELLOW);
        }

        boolean askAgain = true;
        while (!cost.isEmpty() && askAgain) {
            List<PowerUp> optionsPowerUps = new ArrayList<>();
            if (cost.getRedAmmo() > 0)
                optionsPowerUps.addAll(currentPlayer.getPowerUps(RED));
            if (cost.getBlueAmmo() > 0)
                optionsPowerUps.addAll(currentPlayer.getPowerUps(BLUE));
            if (cost.getYellowAmmo() > 0)
                optionsPowerUps.addAll(currentPlayer.getPowerUps(YELLOW));
            if (!optionsPowerUps.isEmpty()) {
                List<String> options = toStringList(optionsPowerUps);
                options.add(NONE);
                currentPlayerConnection.choose(CHOOSE_POWERUP.toString(), OPTIONAL_CONVERSION, options);
                int selected = Integer.parseInt(gameEngine.wait(currentPlayerConnection));
                if (selected == options.size()) {
                    askAgain = false;
                } else {
                    PowerUp selectedPowerup = optionsPowerUps.get(selected - 1);
                    currentPlayer.discardPowerUp(selectedPowerup);
                    cost.subAmmo(selectedPowerup.getColor());
                }
            }
            else
                askAgain = false;
        }

        currentPlayer.useAmmo(cost);
        board.notifyObserver(currentPlayerConnection);

    }


    /**
     * Ask a player to choose which powerup to convert when a convertion is necessary in order to pay.
     *
     * @param color         the color of the powerup that must be converted in order to obtain an ammo
     * @throws SlowAnswerException          if the user do not complete the turn before the timer expires.
     * @throws NotEnoughPlayersException    if the number of connected players falls below three during the turn.
     */
    private void mandatoryConversion(Color color) throws SlowAnswerException, NotEnoughPlayersException{
        currentPlayerConnection.choose(CHOOSE_POWERUP.toString(), MANDATORY_CONVERSION, currentPlayer.getPowerUps(color));
        int selected = Integer.parseInt(gameEngine.wait(currentPlayerConnection));
        PowerUp selectedPowerup = currentPlayer.getPowerUps(color).get(selected-1);
        currentPlayer.discardPowerUp(selectedPowerup);
    }
}