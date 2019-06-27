package it.polimi.ingsw.view;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.logging.Level;
import java.util.logging.Logger;

import static it.polimi.ingsw.model.Updater.*;

public class ClientUpdater {

    private static final Logger LOGGER = Logger.getLogger("clientLogger");

    public void update(JsonObject j, ClientModel clientModel, ClientMain clientMain, UI ui) {

        switch (j.get(TYPE_PROP).getAsString()) {

            case (RELOAD_UPD):
                clientModel.getCurrentPlayer().getWeapon(j.get(WEAPON_PROP).getAsString()).setLoaded(j.get(LOADED_PROP).getAsBoolean());
                break;
            case (REMOVE_SKULL_UPD):
                clientModel.removeSkulls(j.get(SKULL_NUMBER_PROP).getAsInt());
                break;
            case (POWER_UP_DECK_REGEN_UPD):
                clientModel.setPowerUpCardsLeft(j.get(CARDS_NUMBER_PROP).getAsInt());
                break;
            case (DRAW_POWER_UP_UPD):
                clientModel.setPowerUpCardsLeft(clientModel.getPowerUpCardsLeft() - 1);
                clientModel.getPlayer(j.get(PLAYER_PROP).getAsInt()).setCardNumber(clientModel.getPlayer(j.get(PLAYER_PROP).getAsInt()).getCardNumber() + 1);
                if (clientModel.getPlayerID() == j.get(PLAYER_PROP).getAsInt()) {
                    clientModel.getPowerUpInHand().add(j.get(POWER_UP_NAME_PROP).getAsString());
                    clientModel.getColorPowerUpInHand().add(j.get(POWER_UP_COLOR_PROP).getAsString());
                }
                break;
            case (DISCARD_POWER_UP_UPD):
                clientModel.getPlayer(j.get(PLAYER_PROP).getAsInt()).setCardNumber(clientModel.getPlayer(j.get(PLAYER_PROP).getAsInt()).getCardNumber() - 1);
                if (clientModel.getPlayerID() == j.get(PLAYER_PROP).getAsInt()) {
                    clientModel.getPowerUpInHand().remove(j.get(POWER_UP_NAME_PROP).getAsString());
                    clientModel.getColorPowerUpInHand().remove(j.get(POWER_UP_COLOR_PROP).getAsString());
                }
                ui.addHistory(clientModel.getCurrentPlayer().getUsername() + " discarded a " + j.get(POWER_UP_NAME_PROP).getAsString());
                break;
            case (PICKUP_WEAPON_UPD):
                clientModel.getCurrentPlayer().pickUpWeapon(j.get(WEAPON_PROP).getAsString());
                ui.addHistory(clientModel.getCurrentPlayer().getUsername() + " picked up a " + j.get(WEAPON_PROP).getAsString());
                break;
            case (DISCARD_WEAPON_UPD):
                clientModel.getPlayer(j.get(PLAYER_PROP).getAsInt()).discardWeapon(j.get(WEAPON_PROP).getAsString());
                ui.addHistory(clientModel.getPlayer(j.get(PLAYER_PROP).getAsInt()).getUsername() + " discarded a " + j.get(WEAPON_PROP).getAsString());
                break;
            case (ADD_WEAPON_UPD):
                ClientModel.SimpleWeapon w = new ClientModel().new SimpleWeapon(j.get(WEAPON_PROP).getAsString(), true);
                clientModel.getSquare(j.get(SQUARE_PROP).getAsInt()).getWeapons().add(w);
                break;
            case (USE_AMMO_UPD):
                clientModel.getCurrentPlayer().subAmmo(j.get(RED_AMMO_PROP).getAsInt(), j.get(BLUE_AMMO_PROP).getAsInt(), j.get(YELLOW_AMMO_PROP).getAsInt());
                break;
            case (ADD_AMMO_UPD):
                clientModel.getCurrentPlayer().addAmmo(j.get(RED_AMMO_PROP).getAsInt(), j.get(BLUE_AMMO_PROP).getAsInt(), j.get(YELLOW_AMMO_PROP).getAsInt());
                break;
            case (MOVE_UPD):
                clientModel.moveTo(j.get(PLAYER_PROP).getAsInt(), j.get(SQUARE_PROP).getAsInt());
                ui.addHistory(clientModel.getPlayer(j.get(PLAYER_PROP).getAsInt()).getUsername() + " moved to square " + j.get(SQUARE_PROP).getAsInt());
                break;
            case (STATUS_UPD):
                clientModel.getPlayer(j.get(PLAYER_PROP).getAsInt()).setStatus(j.get(STATUS_PROP).getAsString(), j.get(BOOLEAN_PROP).getAsBoolean());
                break;
            case (ADD_DEATH_UPD):
                clientModel.getPlayer(j.get(PLAYER_PROP).getAsInt()).addDeath();
                clientModel.getPlayer(j.get(PLAYER_PROP).getAsInt()).setNextDeathAwards(j.get(POINTS_PROP).getAsInt());
                ui.addHistory(clientModel.getPlayer(j.get(PLAYER_PROP).getAsInt()).getUsername() + " was killed!");
                break;
            case (DAMAGE_UPD):
                clientModel.damage(j.get(PLAYER_PROP).getAsInt(), j.getAsJsonArray(PLAYER_LIST_PROP));
                ui.addHistory(clientModel.getPlayer(j.get(PLAYER_PROP).getAsInt()).getUsername() + " took damage!");
                break;
            case (MARK_UPD):
                clientModel.mark(j.get(PLAYER_PROP).getAsInt(), j.getAsJsonArray(PLAYER_LIST_PROP));
                ui.addHistory(clientModel.getPlayer(j.get(PLAYER_PROP).getAsInt()).getUsername() + " was marked!");
                break;
            case (REMOVE_WEAPON_UPD):
                (clientModel.getSquare(j.get(SQUARE_PROP).getAsInt())).removeWeapon(j.get(WEAPON_PROP).getAsString());
                break;
            case (SET_IN_GAME_UPD):
                clientModel.getPlayer(j.get(PLAYER_PROP).getAsInt()).setInGame(j.get(BOOLEAN_PROP).getAsBoolean());
                break;
            case (REMOVE_AMMO_TILE_UPD):
                clientModel.getSquare(j.get(SQUARE_PROP).getAsInt()).setBlueAmmo(0);
                clientModel.getSquare(j.get(SQUARE_PROP).getAsInt()).setYellowAmmo(0);
                clientModel.getSquare(j.get(SQUARE_PROP).getAsInt()).setRedAmmo(0);
                clientModel.getSquare(j.get(SQUARE_PROP).getAsInt()).setPowerup(false);
                break;
            case (MODEL_UPD):
                try {
                    JsonObject mod = new JsonParser().parse(j.get(MODEL_PROP).getAsString()).getAsJsonObject();
                    clientMain.setClientModel(new Gson().fromJson(mod, ClientModel.class));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;

                /*
            case ("revert"):
                JsonArray players = j.get("players").getAsJsonArray();
                for(int i = 0; i<players.size(); i++){
                     clientModel.damage(players.get(i).getAsInt(), j.getAsJsonArray("damage").get(i).getAsJsonArray());
                     clientModel.getPlayer(i+1).
                }
                JsonArray powerup = j.get("powerup").getAsJsonArray();
                clientModel.getPowerUpInHand().clear();
                for(JsonElement e : powerup){
                    clientModel.getPowerUpInHand().add(e.getAsString());
                }
                clientModel.getCurrentPlayer().setAmmo(j.get("blueammo").getAsInt(), j.get("redammo").getAsInt(), j.get("yellowammo").getAsInt());

                JsonArray weapons = j.getAsJsonArray("weapons");
                JsonArray loadedWeapons = j.getAsJsonArray("loadedweapons");
                clientModel.getCurrentPlayer().getWeapons().clear();
                for(JsonElement e : weapons){
                    clientModel.getCurrentPlayer().getWeapons().add(clientModel.new SimpleWeapon(e.toString(), false));
                }
                for(JsonElement e : loadedWeapons) {
                    clientModel.getCurrentPlayer().getWeapon(e.getAsString()).setLoaded(true);
                }
                JsonArray squares = j.getAsJsonArray("squares");
                JsonArray weaponInSquare = j.getAsJsonArray("weaponsinsquare");
                for(JsonElement e : squares) {
                    List<ClientModel.SimpleWeapon> list = clientModel.getSquare(e.getAsInt()).getWeapons();
                    list.clear();
                    for(JsonElement f : weaponInSquare) {
                        list.add(clientModel.new SimpleWeapon(f.getAsString(), false));
                    }
                }
                //ui.onUpdate();
                //wait a little
                //redraw model
                ui.render();
                break;
                */
            default:
                LOGGER.log(Level.SEVERE, "Malformed update header: " + j.get(TYPE_PROP).getAsString());
                break;
        }
    }
}