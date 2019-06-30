package it.polimi.ingsw.view;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.logging.Level;
import java.util.logging.Logger;

import static it.polimi.ingsw.model.Updater.*;

/**
 * Class applying update messages received from the server to the ClientModel. It can also add messages to the history
 * and task the ui with rendering.
 *
 * @author marcobaga
 */
public class ClientUpdater {

    private static final Logger LOGGER = Logger.getLogger("clientLogger");
    private static final String DISCARD_MSG = " discarded a ";
    private static final String PICKUP_MSG = " picked up a ";
    private static final String MOVED_MSG = " is now in square ";
    private static final String KILLED_MSG = " was killed!";
    private static final String DAMAGED_MSG = " took damage!";
    private static final String MARK_MSG = " was marked!";
    private static final String DMG_INCREMENTED_MSG = " took more damage because of the marks he had!";

    /**
     * Applies updates to the ClientModel.
     *
     * @param   j update message
     * @param   clientModel reference to the model to update
     * @param   clientMain reference to main class, used for replacing the model in particular cases
     * @param   ui reference to user interface, used for rendering
     */
    public void update(JsonObject j, ClientModel clientModel, ClientMain clientMain, UI ui) {

        switch (j.get(TYPE_PROP).getAsString()) {

            case (RENDER_UPD):
                ui.render();
                break;
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
                ui.addHistory(clientModel.getCurrentPlayer().getUsername() + DISCARD_MSG + j.get(POWER_UP_NAME_PROP).getAsString());
                break;
            case (PICKUP_WEAPON_UPD):
                clientModel.getCurrentPlayer().pickUpWeapon(j.get(WEAPON_PROP).getAsString());
                ui.addHistory(clientModel.getCurrentPlayer().getUsername() + PICKUP_MSG + j.get(WEAPON_PROP).getAsString());
                break;
            case (DISCARD_WEAPON_UPD):
                clientModel.getPlayer(j.get(PLAYER_PROP).getAsInt()).discardWeapon(j.get(WEAPON_PROP).getAsString());
                ui.addHistory(clientModel.getPlayer(j.get(PLAYER_PROP).getAsInt()).getUsername() + DISCARD_MSG + j.get(WEAPON_PROP).getAsString());
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
                ui.addHistory(clientModel.getPlayer(j.get(PLAYER_PROP).getAsInt()).getUsername() + MOVED_MSG + j.get(SQUARE_PROP).getAsInt());
                break;
            case (STATUS_UPD):
                clientModel.getPlayer(j.get(PLAYER_PROP).getAsInt()).setStatus(j.get(STATUS_PROP).getAsString(), j.get(BOOLEAN_PROP).getAsBoolean());
                break;
            case (ADD_DEATH_UPD):
                clientModel.getPlayer(j.get(PLAYER_PROP).getAsInt()).addDeath();
                clientModel.getPlayer(j.get(PLAYER_PROP).getAsInt()).setNextDeathAwards(j.get(POINTS_PROP).getAsInt());
                ui.addHistory(clientModel.getPlayer(j.get(PLAYER_PROP).getAsInt()).getUsername() + KILLED_MSG);
                break;
            case (DAMAGE_UPD):
                clientModel.damage(j.get(PLAYER_PROP).getAsInt(), j.getAsJsonArray(PLAYER_LIST_PROP));
                ui.addHistory(clientModel.getPlayer(j.get(PLAYER_PROP).getAsInt()).getUsername() + DAMAGED_MSG);
                break;
            case (MARK_UPD):
                clientModel.mark(j.get(PLAYER_PROP).getAsInt(), j.getAsJsonArray(PLAYER_LIST_PROP));
                ui.addHistory(clientModel.getPlayer(j.get(PLAYER_PROP).getAsInt()).getUsername() + MARK_MSG);
                break;
            case (REMOVE_MARKS):
                clientModel.mark(j.get(PLAYER_PROP).getAsInt(), j.getAsJsonArray(PLAYER_LIST_PROP));
                ui.addHistory(clientModel.getPlayer(j.get(PLAYER_PROP).getAsInt()).getUsername() + DMG_INCREMENTED_MSG);
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
                    JsonObject mod = new JsonParser().parse(j.get(MODEL_PROP).getAsString()).getAsJsonObject();
                    clientMain.setClientModel(new Gson().fromJson(mod, ClientModel.class));
                break;
            default:
                LOGGER.log(Level.SEVERE, "Malformed update header: " + j.get(TYPE_PROP).getAsString());
                break;
        }
    }
}