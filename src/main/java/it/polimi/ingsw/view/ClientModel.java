package it.polimi.ingsw.view;

//TODO: reduce size and complexity to the minimum

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import it.polimi.ingsw.model.board.AmmoSquare;
import it.polimi.ingsw.model.board.WeaponSquare;
import it.polimi.ingsw.model.cards.AmmoTile;
import it.polimi.ingsw.model.cards.Weapon;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class managing the client's model
 *
 * @author  marcobaga
 */
public class ClientModel {

    //TODO righe&Colonne

    private List<SimpleSquare> squares;
    private List<SimplePlayer> players;

    private int weaponCardsLeft;
    private int powerUpCardsLeft;
    private int ammoTilesLeft;

    private int mapID;
    private SimplePlayer currentPlayer;
    private List<SimplePlayer> killShotTrack;
    private int skullsLeft;
    private List<String> powerUpInHand;

    private static final Logger LOGGER = Logger.getLogger("clientLogger");


    /**
     * A simplified version of Square, containing only the things the user should see.
     */
    public abstract class SimpleSquare {


        public SimpleSquare(int id) {
            this.id = id;
        }

        int id;
        public void SetId(int id) {
            this.id = id;
        }
        public int getId(){
            return id;
        }

    }


    /**
     * A simplified version of WeaponSquare, containing only the things the user should see.
     */
    public class SimpleWeaponSquare extends SimpleSquare{

        public SimpleWeaponSquare(int id, List<SimpleWeapon> weapons) {
            super(id);
            this.weapons = weapons;

        }

        List<SimpleWeapon> weapons;

        public List<SimpleWeapon> getWeapons() {
            return weapons;
        }

        public void setWeapons(List<SimpleWeapon> weapons) {
            this.weapons = weapons;
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


    /**
     * A simplified version of AmmpSquare, containing only the things the user should see.
     */
    public class SimpleAmmoSquare extends SimpleSquare{

        public SimpleAmmoSquare(int id, int blueAmmo, int redAmmo, int yellowAmmo, boolean powerup) {
            super(id);
            this.blueAmmo = blueAmmo;
            this.redAmmo = redAmmo;
            this.yellowAmmo = yellowAmmo;
            this.powerup = powerup;

        }

        int blueAmmo;
        int redAmmo;
        int yellowAmmo;
        boolean powerup;

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

    }


    /**
     * A simplified version of Player, containing only the other players information that the user should see.
     */
    public class SimplePlayer{
        private int id;
        private String color;
        private int cardNumber;
        private List<Integer> damage;
        private List<Integer> marks;
        private List<SimpleWeapon> weapons;
        private SimpleSquare position;
        private String username;
        private int blueAmmo;
        private int redAmmo;
        private int yellowAmmo;
        private boolean flipped;
        private boolean inGame;

        public SimplePlayer(int id, String color, int cardNumber, List<Integer> damage, List<Integer> marks, List<SimpleWeapon> weapons, SimpleSquare position, String username, int blueAmmo, int redAmmo, int yellowAmmo, boolean inGame, boolean flipped) {
            this.id = id;
            this.color = color;
            this.cardNumber = cardNumber;
            this.damage = damage;
            this.marks = marks;
            this.weapons = weapons;
            this.position = position;
            this.username = username;
            this.blueAmmo = blueAmmo;
            this.redAmmo = redAmmo;
            this.yellowAmmo = yellowAmmo;
            this.inGame = inGame;
            this.flipped = flipped;
        }

        public void flip(){
            this.flipped = !flipped;
        }

        public void setInGame(boolean inGame){this.inGame = inGame;}

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

        public List<Integer> getDamage() {
            return damage;
        }

        public void setDamage(List<Integer> damage) {
            this.damage = damage;
        }

        public List<Integer> getMarks() {
            return marks;
        }

        public void setMarks(List<Integer> marks) {
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

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void pickUpWeapon(String name){
            for(SimpleWeapon w : ((SimpleWeaponSquare)this.position).getWeapons()){
                if(w.getName().equals(name)){
                    ((SimpleWeaponSquare)this.position).getWeapons().remove(w);
                    this.weapons.add(w);
                    return;
                }
            }
        }

        public void discardWeapon(String name){
            for(SimpleWeapon w : ((SimpleWeaponSquare)this.position).getWeapons()){
                if(w.getName().equals(name)){
                    ((SimpleWeaponSquare)this.position).getWeapons().add(w);
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


    /**
     * A simplified version of Square, containing only the things the user should see.
     */
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

    public int getSkullsLeft() {return skullsLeft;}

    public void setSkullsLeft(int skullsLeft) {this.skullsLeft = skullsLeft;}

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
        List<Integer> list = getPlayer(player).getDamage();
        list.clear();
        for(JsonElement j : damage){
            list.add((j.getAsInt()));
        }
    }

    public void mark(int player, JsonArray marks){
        List<Integer> list = getPlayer(player).getMarks();
        list.clear();
        for(JsonElement j : marks){
            list.add((j.getAsInt()));
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

    public static SimpleWeapon toSimpleWeapon(Weapon weapon){
        return new ClientModel().new SimpleWeapon(weapon.toString(), weapon.isLoaded());
    }

    public static SimpleSquare toSimpleWeaponSquare(WeaponSquare square){
        List<SimpleWeapon> weapons = new ArrayList<>();
        for (Weapon weapon : square.getWeapons()){
            weapons.add(toSimpleWeapon(weapon));
        }
        return new ClientModel().new SimpleWeaponSquare(square.getId(), weapons);
    }

    public static SimpleSquare toSimpleAmmoSquare(AmmoSquare square){
        int redAmmo =0;
        int blueAmmo =0;
        int yellowAmmo =0;
        boolean powerUp = false;
        try {
            AmmoTile ammoTile = square.getAmmoTile();
            redAmmo = ammoTile.getAmmoPack().getRedAmmo();
            blueAmmo = ammoTile.getAmmoPack().getBlueAmmo();
            yellowAmmo = ammoTile.getAmmoPack().getYellowAmmo();
            powerUp = ammoTile.hasPowerUp();

        } catch (NotAvailableAttributeException e){
            LOGGER.log(Level.FINE, "No ammotile on the square: 0,0,0, false is displayed.");
        }
        return new ClientModel().new SimpleAmmoSquare(square.getId(), redAmmo, blueAmmo, yellowAmmo, powerUp);
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