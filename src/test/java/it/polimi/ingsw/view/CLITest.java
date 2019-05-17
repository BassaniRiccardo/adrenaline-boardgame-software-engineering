package it.polimi.ingsw.view;

import org.junit.Test;

import java.util.*;

public class CLITest {

    @Test
    public void test(){
        ClientMain cm = new ClientMain();
        CLI cli = new CLI(cm);

        Map<Integer, List<String>> weaponsOnGround = new HashMap<>();
        List<String> weapons = new ArrayList<>();
        weapons.add("Lock Rifle");
        weapons.add("Gatling");
        weapons.add("Spade");
        weaponsOnGround.put(0, weapons);
        weapons.clear();
        weapons.add("Missile Launcher");
        weapons.add("Flamethrower");
        weaponsOnGround.put(4, weapons);

        Map<Integer, Integer> blueAmmoOnGround = new HashMap<>();
        for(int i=0; i<12; i++){
            blueAmmoOnGround.put(1, 1);
        }
        Map<Integer, Integer> redAmmoOnGround = new HashMap<>();
        redAmmoOnGround.put(2, 2);
        redAmmoOnGround.put(3, 1);
        redAmmoOnGround.put(4, 1);
        redAmmoOnGround.put(11, 2);
        Map<Integer, Integer> yellowAmmoOnGround = new HashMap<>();
        yellowAmmoOnGround.put(1, 1);
        yellowAmmoOnGround.put(3, 1);
        yellowAmmoOnGround.put(10, 1);
        Map<Integer, Boolean> powerUpOnGround = new HashMap<>();
        powerUpOnGround.put(5, true);
        powerUpOnGround.put(10, false);

        int mapID = 1;
        int currentPlayer = 2;

        List<Integer> players = new ArrayList<>();
        players.add(0);
        players.add(1);
        players.add(2);
        players.add(3);
        players.add(4);

        Map<Integer, String> playerColor = new HashMap<>();
        playerColor.put(0, "blue");
        playerColor.put(1, "red");
        playerColor.put(2, "white");
        playerColor.put(3, "cyan");
        playerColor.put(4, "red");

        Map<Integer, Integer> cardNumber = new HashMap<>();

        Map<Integer, List<Integer>> damage = new HashMap<>();
        Map<Integer, List<Integer>> marks = new HashMap<>();
        List<Integer> damageList = new ArrayList<>();
        damageList.add(2);
        damageList.add(2);
        damageList.add(2);
        damageList.add(2);


        damage.put(1, Arrays.asList(2, 2, 4, 5, 4, 4, 7));
        marks.put(0, Arrays.asList(0,0,10));

        Map<Integer, List<String>> equippedWeapons = new HashMap<>();
        equippedWeapons.put(3, Arrays.asList("Banana", "Lampone", "TurboSniffle"));
        Map<String, Boolean> loaded = new HashMap<>();
        Map<Integer, Integer> playerPosition = new HashMap<>();
        playerPosition.put(0, 3);
        playerPosition.put(1, 3);
        playerPosition.put(2, 3);
        playerPosition.put(3, 7);
        playerPosition.put(4, 8);

        Map<Integer, String> playerName = new HashMap<>();
        playerName.put(0, "Eva");
        playerName.put(1, "Eva2");
        playerName.put(2, "Eva3");
        playerName.put(3, "Eva4");
        playerName.put(4, "Evangelos");

        List<String> powerUpInHand = Arrays.asList("Caffeina", "Cioccolato");
        List<Integer> killshotTrack = Arrays.asList(1,1,1,4,0);


        ClientModel model = new ClientModel(3, 5, 4, weaponsOnGround, blueAmmoOnGround, redAmmoOnGround, yellowAmmoOnGround, powerUpOnGround, mapID, currentPlayer, players, playerColor, cardNumber, damage, marks, equippedWeapons, loaded, playerPosition, playerName, powerUpInHand, killshotTrack);

        //getmap okay
        //weapobox.get okay
        //playerbox almost okay
        //handbox almost okay

        cm.setClientModel(model);
        cli.render();
        cli.drawModel();
    }

}