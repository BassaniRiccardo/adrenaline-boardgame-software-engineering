package it.polimi.ingsw.view;

//TODO: implement

import java.util.List;
import java.util.Map;

/**
 * Class managing the client's model
 *
 * @author  marcobaga
 */
public final class ClientModel {

    private final int weaponCardsLeft;
    private final int powerUpCardsLeft;
    private final int ammoTilesLeft;

    private final Map<Integer, List<String>> weaponsOnGround;
    private final Map<Integer, Integer> blueAmmoOnGround;
    private final Map<Integer, Integer> redAmmoOnGround;
    private final Map<Integer, Integer> yellowAmmoOnGround;
    private final Map<Integer, Boolean> powerUpOnGround;

    private final int mapID;
    private final int currentPlayer;
    private final List<Integer> players;
    private final Map<Integer, String> playerColor;

    private final Map<Integer, Integer> cardNumber;
    private final Map<Integer, List<Integer>> damage;
    private final Map<Integer, List<Integer>> marks;
    private final Map<Integer, List<String>> equippedWeapons;
    private final Map<String, Boolean> loaded;
    private final Map<Integer, Integer> playerPosition;
    private final Map<Integer, String> playerName;

    private final List<String> powerUpinHand;
    private final List<Integer> killShotTrack;

    public ClientModel(int weaponCardsLeft, int powerUpCardsLeft, int ammoTilesLeft, Map<Integer, List<String>> weaponsOnGround, Map<Integer, Integer> blueAmmoOnGround, Map<Integer, Integer> redAmmoOnGround, Map<Integer, Integer> yellowAmmoOnGround, Map<Integer, Boolean> powerUpOnGround, int mapID, int currentPlayer, List<Integer> players, Map<Integer, String> playerColor, Map<Integer, Integer> cardNumber, Map<Integer, List<Integer>> damage, Map<Integer, List<Integer>> marks, Map<Integer, List<String>> equippedWeapons, Map<String, Boolean> loaded, Map<Integer, Integer> playerPosition, Map<Integer, String> playerName, List<String> powerUpInHand, List<Integer> killShotTrack) {
        this.weaponCardsLeft = weaponCardsLeft;
        this.powerUpCardsLeft = powerUpCardsLeft;
        this.ammoTilesLeft = ammoTilesLeft;
        this.weaponsOnGround = weaponsOnGround;
        this.blueAmmoOnGround = blueAmmoOnGround;
        this.redAmmoOnGround = redAmmoOnGround;
        this.yellowAmmoOnGround = yellowAmmoOnGround;
        this.powerUpOnGround = powerUpOnGround;
        this.mapID = mapID;
        this.currentPlayer = currentPlayer;
        this.players = players;
        this.playerColor = playerColor;
        this.cardNumber = cardNumber;
        this.damage = damage;
        this.marks = marks;
        this.equippedWeapons = equippedWeapons;
        this.loaded = loaded;
        this.playerPosition = playerPosition;
        this.playerName = playerName;
        this.powerUpinHand = powerUpInHand;
        this.killShotTrack = killShotTrack;
    }

    public int getWeaponCardsLeft() {
        return weaponCardsLeft;
    }

    public int getPowerUpCardsLeft() {
        return powerUpCardsLeft;
    }

    public int getAmmoTilesLeft() {
        return ammoTilesLeft;
    }

    public Map<Integer, List<String>> getWeaponsOnGround() {
        return weaponsOnGround;
    }

    public Map<Integer, Integer> getBlueAmmoOnGround() {
        return blueAmmoOnGround;
    }

    public Map<Integer, Integer> getRedAmmoOnGround() {
        return redAmmoOnGround;
    }

    public Map<Integer, Integer> getYellowAmmoOnGround() {
        return yellowAmmoOnGround;
    }

    public Map<Integer, Boolean> getPowerUpOnGround() {
        return powerUpOnGround;
    }

    public int getMapID() {
        return mapID;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public List<Integer> getPlayers() {
        return players;
    }

    public Map<Integer, String> getPlayerColor() {
        return playerColor;
    }

    public Map<Integer, Integer> getCardNumber() {
        return cardNumber;
    }

    public Map<Integer, List<Integer>> getDamage() {
        return damage;
    }

    public Map<Integer, List<Integer>> getMarks() {
        return marks;
    }

    public Map<Integer, List<String>> getEquippedWeapons() {
        return equippedWeapons;
    }

    public Map<String, Boolean> getLoaded() {
        return loaded;
    }

    public Map<Integer, Integer> getPlayerPosition() {
        return playerPosition;
    }

    public Map<Integer, String> getPlayerName() {
        return playerName;
    }

    public List<Integer> getKillShotTrack() {
        return killShotTrack;
    }

    public List<String> getPowerUpinHand() {
        return powerUpinHand;
    }

    public static String getEscapeCode(String color){
        if(color==null){
            return "\u001b[35m";
        }
        switch(color){
            case "black": return "\u001b[30m";
            case "red": return "\u001b[31m";
            case "green": return "\u001b[32m";
            case "yellow": return "\u001b[33m";
            case "blue": return "\u001b[34m";
            case "magenta": return "\u001b[35m";
            case "cyan": return "\u001b[36m";
            case "white": return "\u001b[37m";
            case "reset": return "\u001b[0m";
            default: return "";
        }
    }
}