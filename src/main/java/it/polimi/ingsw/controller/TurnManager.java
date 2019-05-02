package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.List;

//TODO: finish implementing

public class TurnManager implements Runnable{

    private Board board;
    private PlayerController currentPlayer;
    private List<PlayerController> players;
    private ModelTranslator translator;

    public TurnManager(Board board, PlayerController currentPlayer, List<PlayerController> players){
        this.board = board;
        this.currentPlayer = currentPlayer;
        this.players = players;
        this.translator = new ModelTranslator();
    }

    public void run() {

        Player player = currentPlayer.getPlayer();
        List<Player> deads = new ArrayList<>();

        //Extra part for the first turn
        if (!player.isInTheGame()) {
            joinTheBoard(player, 2);
        }

        //Normal turn actions
        int actionsLeft = player.getActionList().size();
        while (actionsLeft > 0) {
            //asks if the player wants to use a power up before making an action
            if (hasUsablePowerUp(player)) {
                usePowerUp(player);
            }
            executeAction(player);
            actionsLeft--;
            //watch out: the actual model needs to be updated only when the action is confirmed

        }

        //allows to use a power up, if possible
        if (hasUsablePowerUp(player)) {
            usePowerUp(player);
        }

        //handles the reloading process
        reload(player, 3);

        //checks who died, rewards killers, make the dead draw a power up and respawn
        for (Player dead: deads){
            try {
                dead.rewardKillers();
                joinTheBoard(dead, 1);
            } catch (WrongTimeException e) {e.printStackTrace();}
        }

        //replaces items
        replaceItems();


    }



    private void joinTheBoard (Player player, int powerUpToDraw) {

        //draw two powerUps
        for (int i = 0; i < powerUpToDraw; i++) {
            try {
                player.drawPowerUp();
            } catch (NoMoreCardsException e) {
                e.printStackTrace();
            } catch (UnacceptableItemNumberException e) {
                e.printStackTrace();
            }
        }

        //ASK: which one do you want to discard? (Display: player.getPowerUpList())
        currentPlayer.send("Which powerUp do you want to discard?", ModelTranslator.encode(player.getPowerUpList()));
        currentPlayer.receive();
        PowerUp discarded = null; //the discarded powerUp;

        //SHOW: it to the others (Display discarded)

        Color birthColor = discarded.getColor();
        player.discardPowerUp(discarded);

        //place the player on the board
        for (WeaponSquare s : board.getSpawnPoints()) {
            if (s.getColor() == birthColor) player.setPosition(s);
        }

        player.setInTheGame(true);

    }


    private boolean hasUsablePowerUp(Player player) {
        for (PowerUp p : player.getPowerUpList()) {
            if (p.getName() == PowerUp.PowerUpName.NEWTON || p.getName() == PowerUp.PowerUpName.TELEPORTER) return true;
        }
        return false;
    }


    private void usePowerUp(Player player){

        //ASK: Do you want to use a powerUp? (Display yes/no);
        boolean answer = false; //the  given answer;
        if (answer) {

            List<Player> targets = new ArrayList<>();


            // ASK: Which powerUp do you want to use? (Display player.getPowerUps());
            PowerUp powerUpToUse = null; // the selected powerUp;

            if (powerUpToUse.getName() == PowerUp.PowerUpName.NEWTON) {
                //ASK: Who do you want to choose as a target? (Display powerUpToUse.findTargets());
                targets = null; //the selected ones;
            }

            else { targets.add(player);}

            //ASK:choose a destination (Display powerUpToUse.findDestinations(targets));
            Square destination = null; // the selected one;

            try {
                powerUpToUse.applyEffects(targets, destination);
            } catch (NotAvailableAttributeException e) { e.printStackTrace();}
        }
    }


    private void executeAction(Player player){

        //ASK: which action you want to do? (Display player.getActionList)

        Action action = null; //the selected action;

        if (action.getSteps() > 0){
            //ASK: where do you wanna move? (Display player.getPosition().getReachable(action.getSteps()))
            Square dest = null; //the selected square
            player.setPosition(dest);
        }

        try {
            if (action.isCollect()){
                if (board.getSpawnPoints().contains(player.getPosition())) {
                    //ASK: which weapon do you want to collect? (Display ((WeaponSquare)player.getPosition()).getWeapons())
                    Weapon collectedWeapon = null; //the selected weapon
                    player.collect(collectedWeapon);
                }
                else {
                    player.collect(((AmmoSquare)player.getPosition()).getAmmoTile());
                }
            }
        } catch (NotAvailableAttributeException | NoMoreCardsException | UnacceptableItemNumberException e) {e.printStackTrace();}

        if (action.isReload()) {
            reload(player, 1);
        }

        if (action.isShoot()){
            handleShooting(player);
        }
    }

    private void handleShooting(Player player){

        List<Weapon> usableWeapons = player.getLoadedWeapons();
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
    }


    private void reload(Player player, int max) {

        while (!player.getReloadableWeapons().isEmpty() && max > 0) {
            //ASK: Which weapon do you want to reload?(Display player.getReloadableWeapons() + "none");
            boolean none = false; //whether none is selected
            if (none) break;
            Weapon weaponToReload = null; // the selected one;
            try {
                weaponToReload.reload();
                max--;
            } catch (NotAvailableAttributeException | WrongTimeException e) {e.printStackTrace();}
        }
    }


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
                        s.addAllCards();
                    } catch (UnacceptableItemNumberException | NoMoreCardsException e){e.printStackTrace();}
                }
            }
        }
    }


    private void chooseAction(){
        //getavailableactions
    }
    private void shoot(){
        //player.getavailableweapons()
        //playercontroller.ask()
        //getweapon
        //getavailablefiremodes()
        //playercontroller.ask()
        //selecttargets
        //select destinations()
        //shoot
    }
    private void move(){
        //playercontroller.ask(encode(getdestinations()));
        //player.move(decode(answer()));

    }

    private void pickup(){
        //playercontroller.ask(encode(getdestinations()));
        //player.move(decode(answer()));
        //player.pickup();
    }
    private void reload(){
        //playercontrolelr.ask(encode(getrechargeableweapons));
        //player.reload(decode(answer()));
    }

    private void executeaction() {
        //carries out the action selected
    }
}