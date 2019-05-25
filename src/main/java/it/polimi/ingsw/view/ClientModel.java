package it.polimi.ingsw.view;

//TODO: reduce size and complexity to the minimum

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.List;
import java.util.Map;

/**
 * Class managing the client's model
 *
 * @author  marcobaga
 */
public class ClientModel {

    public class SimpleSquare {

        public SimpleSquare(List<SimpleWeapon> weapons, int id, int blueAmmo, int redAmmo, int yellowAmmo, boolean powerup) {
            this.weapons = weapons;
            this.id = id;
            this.blueAmmo = blueAmmo;
            this.redAmmo = redAmmo;
            this.yellowAmmo = yellowAmmo;
            this.powerup = powerup;
        }

        List<SimpleWeapon> weapons;
        int id;
        int blueAmmo;
        int redAmmo;
        int yellowAmmo;
        boolean powerup;

        public List<SimpleWeapon> getWeapons() {
            return weapons;
        }

        public void setWeapons(List<SimpleWeapon> weapons) {
            this.weapons = weapons;
        }

        public int getBlueAmmo() {
            return blueAmmo;
        }

        public void setBlueAmmo(int blueAmmo) {
            this.blueAmmo = blueAmmo;
        }

        public int getRedAmmo() {
            return redAmmo;
        }

        public void setRedAmmo(int redAmmo) {
            this.redAmmo = redAmmo;
        }

        public int getYellowAmmo() {
            return yellowAmmo;
        }

        public void setYellowAmmo(int yellowAmmo) {
            this.yellowAmmo = yellowAmmo;
        }

        public boolean isPowerup() {
            return powerup;
        }

        public void setPowerup(boolean powerup) {
            this.powerup = powerup;
        }

        public void SetId(int id) {
            this.id = id;
        }

        public int getId(){
            return id;
        }

        public void removeWeapon(String name){
            for(SimpleWeapon w : weapons){
                if(w.getName()==name){
                    weapons.remove(w);
                    return;
                }
            }
        }
    }

    public class SimplePlayer{
        private int id;
        private String color;
        private int cardNumber;
        private List<SimplePlayer> damage;
        private List<SimplePlayer> marks;
        private List<SimpleWeapon> weapons;
        private SimpleSquare position;
        private String name;
        private int blueAmmo;
        private int redAmmo;
        private int yellowAmmo;
        private boolean flipped;

        public SimplePlayer(int id, String color, int cardNumber, List<SimplePlayer> damage, List<SimplePlayer> marks, List<SimpleWeapon> weapons, SimpleSquare position, String name, int blueAmmo, int redAmmo, int yellowAmmo) {
            this.id = id;
            this.color = color;
            this.cardNumber = cardNumber;
            this.damage = damage;
            this.marks = marks;
            this.weapons = weapons;
            this.position = position;
            this.name = name;
            this.blueAmmo = blueAmmo;
            this.redAmmo = redAmmo;
            this.yellowAmmo = yellowAmmo;
        }

        public void flip(){
            this.flipped = !flipped;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public int getCardNumber() {
            return cardNumber;
        }

        public void setCardNumber(int cardNumber) {
            this.cardNumber = cardNumber;
        }

        public List<SimplePlayer> getDamage() {
            return damage;
        }

        public void setDamage(List<SimplePlayer> damage) {
            this.damage = damage;
        }

        public List<SimplePlayer> getMarks() {
            return marks;
        }

        public void setMarks(List<SimplePlayer> marks) {
            this.marks = marks;
        }

        public List<SimpleWeapon> getWeapons() {
            return weapons;
        }

        public void setWeapons(List<SimpleWeapon> weapons) {
            this.weapons = weapons;
        }

        public SimpleSquare getPosition() {
            return position;
        }

        public void setPosition(SimpleSquare position) {
            this.position = position;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void pickUpWeapon(String name){
            for(SimpleWeapon w : this.position.getWeapons()){
                if(w.getName().equals(name)){
                    this.position.getWeapons().remove(w);
                    this.weapons.add(w);
                    return;
                }
            }
        }

        public void discardWeapon(String name){
            for(SimpleWeapon w : this.position.getWeapons()){
                if(w.getName().equals(name)){
                    this.position.getWeapons().add(w);
                    this.weapons.remove(w);
                    return;
                }
            }
        }

        public void addAmmo(int b, int r, int y){
            blueAmmo=blueAmmo+b;
            redAmmo=redAmmo+r;
            yellowAmmo=yellowAmmo+y;
        }

        public void subAmmo(int b, int r, int y){
            blueAmmo=blueAmmo-b;
            redAmmo=redAmmo-r;
            yellowAmmo=yellowAmmo-y;
        }

        public void setAmmo(int b, int r, int y){
            blueAmmo=b;
            redAmmo=r;
            yellowAmmo=y;
        }

        public SimpleWeapon getWeapon(String name){
            for(SimpleWeapon s : weapons){
                if(s.getName()==name){
                    return s;
                }
            }
            return new SimpleWeapon("error", false);
        }
    }

    public class SimpleWeapon{
        String name;
        boolean loaded;

        public SimpleWeapon(String name, boolean loaded) {
            this.name = name;
            this.loaded = loaded;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isLoaded() {
            return loaded;
        }

        public void setLoaded(boolean loaded) {
            this.loaded = loaded;
        }
    }

    private List<SimpleSquare> squares;
    private List<SimplePlayer> players;

    private int weaponCardsLeft;
    private int powerUpCardsLeft;
    private int ammoTilesLeft;

    private int mapID;
    private SimplePlayer currentPlayer;
    private List<SimplePlayer> killShotTrack;
    private List<String> powerUpInHand;


    public List<SimpleSquare> getSquares() {
        return squares;
    }

    public void setSquares(List<SimpleSquare> squares) {
        this.squares = squares;
    }

    public List<SimplePlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<SimplePlayer> players) {
        this.players = players;
    }

    public int getWeaponCardsLeft() {
        return weaponCardsLeft;
    }

    public void setWeaponCardsLeft(int weaponCardsLeft) {
        this.weaponCardsLeft = weaponCardsLeft;
    }

    public int getPowerUpCardsLeft() {
        return powerUpCardsLeft;
    }

    public void setPowerUpCardsLeft(int powerUpCardsLeft) {
        this.powerUpCardsLeft = powerUpCardsLeft;
    }

    public int getAmmoTilesLeft() {
        return ammoTilesLeft;
    }

    public void setAmmoTilesLeft(int ammoTilesLeft) {
        this.ammoTilesLeft = ammoTilesLeft;
    }

    public int getMapID() {
        return mapID;
    }

    public void setMapID(int mapID) {
        this.mapID = mapID;
    }

    public SimplePlayer getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(SimplePlayer currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public List<SimplePlayer> getKillShotTrack() {
        return killShotTrack;
    }

    public void setKillShotTrack(List<SimplePlayer> killShotTrack) {
        this.killShotTrack = killShotTrack;
    }

    public List<String> getPowerUpInHand() {
        return powerUpInHand;
    }

    public void setPowerUpInHand(List<String> powerUpInHand) {
        this.powerUpInHand = powerUpInHand;
    }

    public void removeSkulls(int n){
        //do something with killshottrack
    }

    public void moveTo(int player, int square) {
        SimplePlayer p = getPlayer(player);
        SimpleSquare s = getSquare(square);
        p.setPosition(s);
    }

    public void flip(int player) {
        getPlayer(player).flip();
    }

    public void damage(int player, JsonArray damage){
        List<SimplePlayer> list = getPlayer(player).getDamage();
        list.clear();
        for(JsonElement j : damage){
            list.add(getPlayer(j.getAsInt()));
        }
    }

    public void mark(int player, JsonArray marks){
        List<SimplePlayer> list = getPlayer(player).getMarks();
        list.clear();
        for(JsonElement j : marks){
            list.add(getPlayer(j.getAsInt()));
        }
    }

    public SimplePlayer getPlayer(int id){
        for(SimplePlayer s : players){
            if(s.getId() == id){
                return s;
            }
        }
        return players.get(0);
        //watch out
    }

    public SimpleSquare getSquare(int id){
        for(SimpleSquare s : squares){
            if(s.getId()==id){
                return s;
            }
        }
        return squares.get(0);
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