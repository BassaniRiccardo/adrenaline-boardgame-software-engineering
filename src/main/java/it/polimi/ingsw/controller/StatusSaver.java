package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Player;
import it.polimi.ingsw.model.board.Square;
import it.polimi.ingsw.model.board.WeaponSquare;
import it.polimi.ingsw.model.cards.AmmoPack;
import it.polimi.ingsw.model.cards.PowerUp;
import it.polimi.ingsw.model.cards.Weapon;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Contains methods to save the status of the game and to restore the last saved status.
 *
 * @author BassaniRiccardo
 */

class StatusSaver {

    private Board board;
    private List<Square> playersPositions;
    private List<List<Player>> playersDamages;
    private List<List<Player>> playersMarks;
    private List<List<PowerUp>> playersPowerups;
    private List<AmmoPack> playersAmmoPacks;

    private List<Weapon> currentPlayerWeapons;
    private List<Boolean> currentPlayerLoadedWeapons;

    private List<List<Weapon>> squareWeapons;

    private static final Logger LOGGER = Logger.getLogger("serverLogger");


    /*
     * Getters
     */

    List<Square> getPlayersPositions() {
        return playersPositions;
    }

    List<List<Player>> getPlayersDamages() {
        return playersDamages;
    }

    List<List<Player>> getPlayersMarks() {
        return playersMarks;
    }

    List<List<PowerUp>> getPlayersPowerups() {
        return playersPowerups;
    }

    List<AmmoPack> getPlayersAmmoPacks() {
        return playersAmmoPacks;
    }

    List<Weapon> getCurrentPlayerWeapons() {
        return currentPlayerWeapons;
    }

    List<Boolean> getCurrentPlayerLoadedWeapons() {
        return currentPlayerLoadedWeapons;
    }

    List<List<Weapon>> getSquareWeapons() {
        return squareWeapons;
    }


    /**
     * Constructs a StatusSaver with a reference to the board.
     *
     * @param board         the board of which status needs to be saved and restored.
     */
    StatusSaver(Board board) {

        this.board = board;
        playersPositions = new ArrayList<>();
        playersDamages = new ArrayList<>();
        playersMarks = new ArrayList<>();
        playersPowerups = new ArrayList<>();
        playersAmmoPacks = new ArrayList<>();

        currentPlayerWeapons = new ArrayList<>();
        currentPlayerLoadedWeapons = new ArrayList<>();

        squareWeapons = new ArrayList<>();

    }


    /**
     * Updates the last checkpoint which will be restored by the method restoreCheckpoint().
     */
    void updateCheckpoint(){

        LOGGER.log(Level.FINE, () -> "playersPowerups saved: " + playersPowerups);

            //attributes shared by all players
                playersPositions.clear();
                playersDamages.clear();
                playersMarks.clear();
                playersPowerups.clear();
                playersAmmoPacks.clear();
            for (Player p : board.getActivePlayers()) {
                try {
                    playersPositions.add(p.getPosition());
                } catch (NotAvailableAttributeException e) { LOGGER.log(Level.SEVERE, "updating the psition of a player with no position", e);}
                    playersDamages.add(new ArrayList<>(p.getDamages()));
                    playersMarks.add(new ArrayList<>(p.getMarks()));
                    playersPowerups.add(new ArrayList<>(p.getPowerUpList()));
                    playersAmmoPacks.add(new AmmoPack(p.getAmmoPack().getRedAmmo(), p.getAmmoPack().getBlueAmmo(), p.getAmmoPack().getYellowAmmo()));
            }
            //current player
            currentPlayerWeapons = new ArrayList<>(board.getCurrentPlayer().getWeaponList());
            currentPlayerLoadedWeapons.clear();
            for (Weapon w : board.getCurrentPlayer().getWeaponList()){
                currentPlayerLoadedWeapons.add(w.isLoaded());
            }
            //squares
            squareWeapons.clear();
            for (WeaponSquare s : board.getSpawnPoints()) {
                List<Weapon> lw = new ArrayList<>(s.getWeapons());
                squareWeapons.add(lw);
            }
        LOGGER.log(Level.FINE, "updating checkpoint");
        LOGGER.log(Level.FINE, () -> "playersPowerups saved: " + playersPowerups);



    }


    /**
     * Updates the lists of power ups which will be restored by the method restorePowerUps().
     */
    void updatePowerups(){
        LOGGER.log(Level.FINE, () -> "playersPowerups: " + playersPowerups);
        playersPowerups.clear();
        for (Player p : board.getActivePlayers()) {
            playersPowerups.add(new ArrayList<>(p.getPowerUpList()));
        }
        LOGGER.log(Level.FINE, "updating powerUps");
        LOGGER.log(Level.FINE, () -> "playersPowerups: " + playersPowerups);

    }


    /**
     * Restores the last checkpoint saved by the method updateCheckpoint().
     */
    void restoreCheckpoint(){

        board.setReset(true);
        int i;
        //attributes shared by all players
        for (Player p : board.getActivePlayers()) {
            i = board.getActivePlayers().indexOf(p);
            if (playersPositions.size() > i) {
                p.setPosition(playersPositions.get(i));
                p.setDamages(new ArrayList<>(playersDamages.get(i)));
                p.setMarks(new ArrayList<>(playersMarks.get(i)));
                p.setDead(playersDamages.get(i).size() >= 11);
                p.setPowerUpList(new ArrayList<>(playersPowerups.get(i)));
                AmmoPack ap = new AmmoPack(playersAmmoPacks.get(i).getRedAmmo(), playersAmmoPacks.get(i).getBlueAmmo(), playersAmmoPacks.get(i).getYellowAmmo());
                p.setAmmoPack(ap);
            }
        }
        //current player
        board.getCurrentPlayer().setWeaponList(new ArrayList<>(currentPlayerWeapons));
        for (Weapon w : board.getCurrentPlayer().getWeaponList()){
            w.setLoaded(currentPlayerLoadedWeapons.get(board.getCurrentPlayer().getWeaponList().indexOf(w)));
            w.setHolder(board.getCurrentPlayer());
        }
        board.getCurrentPlayer().getMainTargets().clear();
        board.getCurrentPlayer().getOptionalTargets().clear();
        //squares
        for (Square s : board.getSpawnPoints()) {
            ((WeaponSquare)s).setWeapons(new ArrayList<>(squareWeapons.get(board.getSpawnPoints().indexOf(s))));
        }
        LOGGER.log(Level.FINE, "Restoring checkpoint");

    }


    /**
     * Restores the lists of power ups saved by the method updateCheckpoint().
     */
    void restorePowerUps(){

        board.setReset(true);
        for (Player p : board.getActivePlayers()) {
            p.setPowerUpList(new ArrayList<>(playersPowerups.get(board.getActivePlayers().indexOf(p))));
        }
    }

}
