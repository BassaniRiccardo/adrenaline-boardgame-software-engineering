package it.polimi.ingsw.model;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.polimi.ingsw.model.board.*;
import it.polimi.ingsw.model.cards.AmmoPack;
import it.polimi.ingsw.model.cards.PowerUp;
import it.polimi.ingsw.model.cards.Weapon;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;
import it.polimi.ingsw.view.ClientModel;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;



public class Updater {

    private static final Logger LOGGER = Logger.getLogger("serverLogger");

    public static JsonObject get(String msg, Weapon w, boolean loaded) {
        JsonObject j = getFreshUpdate(msg);
        j.addProperty("weapon", w.getWeaponName().toString());
        j.addProperty("loaded", loaded);
        //loaded
        return j;
    }

    public static JsonObject get(String msg, int quantity) {
        JsonObject j = getFreshUpdate(msg);
        j.addProperty("number", quantity);
        //pDeckRegen
        return j;
    }

    public static JsonObject get(String msg, int quantity, Player p, boolean ok) {
        JsonObject j = getFreshUpdate(msg);
        j.addProperty("number", quantity);
        j.addProperty("killer", p.getId());
        j.addProperty("overkill", ok);
        return j;
    }

    public static JsonObject get(String s, Player p, PowerUp powerUp) {
        JsonObject j = getFreshUpdate(s);
        j.addProperty("player", p.getId());
        j.addProperty("powerup", powerUp.getName().toString());
        return j;
        //drawPowerUp and discardPowerup
    }

    public static JsonObject get(String s, Player p, Weapon w) {
        JsonObject j = getFreshUpdate(s);
        j.addProperty("player", p.getId());
        j.addProperty("weapon", w.getWeaponName().toString());
        return j;
        //discardWeapon and pickUpWeapon
    }


    public static JsonObject get(String s, Player p, AmmoPack a) {
        JsonObject j = getFreshUpdate(s);
        j.addProperty("player", p.getId());
        j.addProperty("redAmmo", a.getRedAmmo());
        j.addProperty("blueAmmo", a.getBlueAmmo());
        j.addProperty("yellowAmmo", a.getYellowAmmo());
        return j;
        //useAmmo, addAmmo
    }

    public static JsonObject get(String s, Player p, Square square) {
        JsonObject j = getFreshUpdate(s);
        j.addProperty("player", p.getId());
        j.addProperty("square", square.getId());
        return j;
        //move
    }

    public static JsonObject get(String s, Player p) {
        JsonObject j = getFreshUpdate(s);
        j.addProperty("player", p.getId());
        return j;
        //flip, addDeath
    }

    public static JsonObject get(String s, Player p, List<Player> l) {
        JsonObject j = getFreshUpdate(s);
        j.addProperty("player", p.getId());
        JsonArray array = new JsonArray();
        for (Player shooter : l) {
            array.add(shooter.getId());
        }
        j.add("list", array);
        return j;
        //damaged, marked
    }

    public static JsonObject get(String s, Square sq, Weapon w) {
        JsonObject j = getFreshUpdate(s);
        j.addProperty("square", sq.getId());
        j.addProperty("weapon", w.toString());
        return j;
        //weaponRemoved & addWeapon
    }

    public static JsonObject get(String s, Player p, boolean in) {
        JsonObject j = getFreshUpdate(s);
        j.addProperty("player", p.getId());
        j.addProperty("ingame", in);
        return j;
        //setInGame
    }

    public static JsonObject get(String s, Square square){
        JsonObject j = getFreshUpdate(s);
        j.addProperty("player", square.getId());
        return j;
        //removeAmmoTile
    }

    /*public static JsonObject getRevert(Board board) {
        JsonObject j = getFreshUpdate("revert");
        JsonArray playerArray = new JsonArray();
        JsonArray positionArray = new JsonArray();
        JsonArray damageArray = new JsonArray();
        JsonArray powerUpArray = new JsonArray();
        JsonArray blueAmmoArray = new JsonArray();
        JsonArray redAmmoArray = new JsonArray();
        JsonArray yellowAmmoArray = new JsonArray();
        for (Player p : board.getPlayers()) {
            playerArray.add(p.getId());
            try {
                positionArray.add(p.getPosition().getId());
            } catch (NotAvailableAttributeException ex) {
                //manage
            }
            JsonArray temp = new JsonArray();
            for (Player q : p.getDamages()) {
                temp.add(p.getId());
            }
            damageArray.add(temp);
            JsonArray temp2 = new JsonArray();
            for (PowerUp q : p.getPowerUpList()) {
                temp2.add(q.getName().toString());
            }
            powerUpArray.add(temp2);
            blueAmmoArray.add(p.getAmmoPack().getBlueAmmo());
            redAmmoArray.add(p.getAmmoPack().getRedAmmo());
            yellowAmmoArray.add(p.getAmmoPack().getYellowAmmo());
        }
        j.add("players", playerArray);
        j.add("positions", positionArray);
        j.add("damage", damageArray);
        j.add("powerup", powerUpArray);
        j.add("blueammo", blueAmmoArray);
        j.add("redammo", redAmmoArray);
        j.add("yellowammo", yellowAmmoArray);

        JsonArray weaponArray = new JsonArray();
        JsonArray loadedWeaponArray = new JsonArray();
        for (Weapon w : board.getCurrentPlayer().getWeaponList()) {
            weaponArray.add(w.getWeaponName().toString());
            if (w.isLoaded()) {
                loadedWeaponArray.add(w.getWeaponName().toString());
            }
        }
        j.add("weapons", weaponArray);
        j.add("loadedweapons", loadedWeaponArray);

        JsonArray squareArray = new JsonArray();
        JsonArray weaponsInSquareArray = new JsonArray();
        for (WeaponSquare s : board.getSpawnPoints()) {
            squareArray.add(s.getId());
            JsonArray temp = new JsonArray();
            for (Weapon w : s.getWeapons()) {
                temp.add(w.getWeaponName().toString());
            }
            weaponsInSquareArray.add(temp);
        }
        j.add("squares", squareArray);
        j.add("weaponsinsquare", weaponsInSquareArray);

        return j;
    }*/


    /**
     * Updates the ClientModel of the specified player on the specified board and returns it.
     *
     * @param board         the game board.
     * @param player        the player who will receive the updated model.
     * @return              the updated model.
     */
    public static JsonObject getModel(Board board, Player player) {

        ClientModel cm = new ClientModel();

        //simpleSquares
        List<ClientModel.SimpleSquare> simpleSquares = new ArrayList<>();
        for (Square s : board.getMap()){
            if (board.getSpawnPoints().contains(s))     simpleSquares.add(ClientModel.toSimpleSquare((WeaponSquare)s));
            else  simpleSquares.add(ClientModel.toSimpleSquare((AmmoSquare) s));
        }
        cm.setSquares(simpleSquares);


        //simplePlayers, currentPlayer, killShotTrack
        List<ClientModel.SimplePlayer> simplePlayers = new ArrayList<>();
        List<ClientModel.SimplePlayer> killers = new ArrayList<>();

        for (Player p : board.getPlayers()){

            //create a new simplePlayer
            List<Integer> damages = new ArrayList<>();
            for (Player shooter : p.getDamages()){
                damages.add(shooter.getId());
            }
            List<Integer> marks = new ArrayList<>();
            for (Player marker : p.getMarks()){
                marks.add(marker.getId());
            }
            List<ClientModel.SimpleWeapon> weapons = new ArrayList<>();
            for (Weapon weapon : p.getWeaponList()){
                weapons.add(ClientModel.toSimpleWeapon(weapon));
            }
            ClientModel.SimpleSquare position = null;
            boolean isInGame = false;
            try {
                if (board.getSpawnPoints().contains(p.getPosition())) position = ClientModel.toSimpleSquare((WeaponSquare)p.getPosition());
                else position = ClientModel.toSimpleSquare((AmmoSquare)p.getPosition());
                isInGame = true;

            } catch (NotAvailableAttributeException e) {
                LOGGER.log(Level.FINE, "The player is not on the board, is in game remains false");
            }
            ClientModel.SimplePlayer simplePlayer = new ClientModel().new SimplePlayer(p.getId(), p.getstringColor(), p.getPowerUpList().size(), damages, marks, weapons, position, p.getUsername(), p.getAmmoPack().getBlueAmmo(), p.getAmmoPack().getRedAmmo(), p.getAmmoPack().getYellowAmmo(), isInGame, p.isFlipped(), p.getPoints(), p.getDeaths());
            simplePlayers.add(simplePlayer);
            //currentPlayer
            /*if (p.equals(board.getCurrentPlayer())){
                cm.setCurrentPlayer(simplePlayer);
            }*/



            //killShotTrack
            try {
                for (Player killer : board.getKillShotTrack().getKillers()) {
                    if (killer.equals(p)) {
                        killers.add(board.getKillShotTrack().getKillers().indexOf(killer), simplePlayer);
                    }
                }
            } catch (NotAvailableAttributeException e){e.printStackTrace();}

        }
        cm.setPlayers(simplePlayers);
        cm.setCurrentPlayer(cm.getPlayer(board.getCurrentPlayer().getId()));
        cm.setPlayerID(player.getId());

        cm.setKillShotTrack(killers);
        try {
            cm.setSkullsLeft(board.getKillShotTrack().getSkullsLeft());
        } catch (NotAvailableAttributeException e) {
            LOGGER.log(Level.SEVERE, "Impossible to get the number of skulls left, all skulls removed.");
            cm.setSkullsLeft(0);
        }

        //decks size
        cm.setPowerUpCardsLeft(board.getPowerUpDeck().getDrawable().size());
        cm.setWeaponCardsLeft(board.getWeaponDeck().getDrawable().size());
        cm.setAmmoTilesLeft(board.getAmmoDeck().getDrawable().size());
        cm.setMapID(board.getId());
        cm.setLeftWalls(board.getLeftWalls());
        cm.setTopWalls(board.getTopWalls());

        //powerUpInHand (of the selected player)
        List<String> powerUpInHand = new ArrayList<>();
        for (PowerUp powerUp: player.getPowerUpList()){
            powerUpInHand.add(powerUp.toString());
        }
        cm.setPowerUpInHand(powerUpInHand);

        Gson gson = new Gson();
        String json = gson.toJson(cm);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("head", "UPD");
        jsonObject.addProperty("type", "mod");
        jsonObject.addProperty("mod", json);

        return jsonObject;
    }


    private static JsonObject getFreshUpdate(String msg) {
        JsonObject j = new JsonObject();
        j.addProperty("head", "UPD");
        j.addProperty("type", msg);
        return j;
    }

}