package it.polimi.ingsw.controller;

import com.google.gson.*;
import it.polimi.ingsw.model.board.*;
import it.polimi.ingsw.model.cards.Weapon;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;
import it.polimi.ingsw.view.ClientModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Provides methods which encode message toward the client and decode arriving messages, following a protocol.
 *
 * @author BassaniRiccardo, marcobaga
 */

//TODO: choose a protocol and implement it.
// exiting the game and resetting to the beginning of the action might always be possible choices. Add them.


public class Encoder {

    public enum Header {
        PNG, MSG, REQ, OPT, UPD;
    }

    private Encoder instance;
    private static final Logger LOGGER = Logger.getLogger("serverLogger");

    private Encoder(){
        this.instance = new Encoder();
    }

    public Encoder getInstance() {
        return instance;
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
     *Translates the model is a siplified JsonObject to be displayed by the client.
     *
     * @param board                 the model's board
     * @param current               the current player's model
     */
    public static ClientModel getModel(Board board, Player current){
        //this method is temporary, it needs a major overhaul

        int weaponCardsLeft = board.getWeaponDeck().getDrawable().size();
        int powerUpCardsLeft = board.getPowerUpDeck().getDrawable().size();
        int ammoTilesLeft = board.getAmmoDeck().getDrawable().size();

        Map<Integer, List<String>> weaponsOnGround = new HashMap<>();
        for (WeaponSquare s : board.getSpawnPoints()){
            weaponsOnGround.put(s.getId(), s.getWeapons().stream().map(x->x.getWeaponName().toString()).collect(Collectors.toList()));
        }

        Map<Integer, Integer> blueAmmoOnGround = new HashMap<>();
        for(AmmoSquare s : board.getAmmoSquares()){
            try {
                blueAmmoOnGround.put(s.getId(), s.getAmmoTile().getAmmoPack().getBlueAmmo());
            }catch(NotAvailableAttributeException ex) {
                blueAmmoOnGround.put(s.getId(), 0);
                LOGGER.log(Level.SEVERE, "Cannot check AmmoPack in square", ex);
            }
        }

        Map<Integer, Integer> redAmmoOnGround = new HashMap<>();
        for(AmmoSquare s1 : board.getAmmoSquares()){
            try{
                redAmmoOnGround.put(s1.getId(), s1.getAmmoTile().getAmmoPack().getRedAmmo());
            }catch(NotAvailableAttributeException ex){
                redAmmoOnGround.put(s1.getId(), 0);
                LOGGER.log(Level.SEVERE, "Cannot check AmmoPack in square", ex);
            }
        }

        Map<Integer, Integer> yellowAmmoOnGround = new HashMap<>();
        for(AmmoSquare s2 : board.getAmmoSquares()){
            try{
                yellowAmmoOnGround.put(s2.getId(), s2.getAmmoTile().getAmmoPack().getYellowAmmo());
            }catch(NotAvailableAttributeException ex){
                yellowAmmoOnGround.put(s2.getId(), 0);
                LOGGER.log(Level.SEVERE, "Cannot check AmmoPack in square", ex);
            }
        }

        Map<Integer, Boolean> powerUpOnGround = new HashMap<>();
        for(AmmoSquare s3 : board.getAmmoSquares()){
            try{
                powerUpOnGround.put(s3.getId(), s3.getAmmoTile().hasPowerUp());
            }catch(NotAvailableAttributeException ex){
                powerUpOnGround.put(s3.getId(), false);
                LOGGER.log(Level.SEVERE, "Cannot check AmmoPack in square", ex);
            }
        }

        int mapID = board.getId();
        int currentPlayer = board.getCurrentPlayer().getId();
        List<Integer> players = board.getPlayers().stream().map(x->x.getId()).collect(Collectors.toList());

        Map<Integer, String> playerColor = new HashMap<>();
        Map<Integer, Integer> cardNumber = new HashMap<>();
        Map<Integer, List<Integer>> damage = new HashMap<>();
        Map<Integer, List<Integer>> marks = new HashMap<>();
        Map<Integer, List<String>> weapons = new HashMap<>();
        Map<String, Boolean> loaded = new HashMap<>();
        Map<Integer, Integer> position = new HashMap<>();
        Map<Integer, String> name = new HashMap<>();

        for(Player p : board.getPlayers()){
            int currId = p.getId();
            cardNumber.put(currId, p.getPowerUpList().size());
            damage.put(currId, p.getDamages().stream().map(x->x.getId()).collect(Collectors.toList()));
            marks.put(currId, p.getMarks().stream().map(x->x.getId()).collect(Collectors.toList()));
            weapons.put(currId, p.getWeaponList().stream().map(x->x.getWeaponName().toString()).collect(Collectors.toList()));
            for(Weapon w : p.getWeaponList()) {
                loaded.put(w.getWeaponName().toString(), w.isLoaded());
            }
            try {
                position.put(currId, p.getPosition().getId());
            }catch(NotAvailableAttributeException ex){position.put(currId, 0); LOGGER.log(Level.SEVERE, "Cannot access player's position, setting it to 0", ex);}
            name.put(currId, p.getName().toString());
        }

        List<String> powerUpInHand = current.getPowerUpList().stream().map(x->x.getName().toString()).collect(Collectors.toList());

        List<Integer> killshotTrack = new ArrayList<>();

        try {
            killshotTrack.addAll(board.getKillShotTrack().getKillers().stream().map(x -> x.getId()).collect(Collectors.toList()));
        }catch(NotAvailableAttributeException ex){ LOGGER.log(Level.SEVERE, "Cannot access killshot track", ex);}


        return new ClientModel(weaponCardsLeft, powerUpCardsLeft, ammoTilesLeft, weaponsOnGround, blueAmmoOnGround, redAmmoOnGround, yellowAmmoOnGround, powerUpOnGround, mapID, currentPlayer, players, playerColor, cardNumber, damage, marks, weapons, loaded, position, name, powerUpInHand, killshotTrack);
    }
}

