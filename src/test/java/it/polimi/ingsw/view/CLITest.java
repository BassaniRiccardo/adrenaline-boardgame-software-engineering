package it.polimi.ingsw.view;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.ingsw.controller.BoardConfigurer;
import it.polimi.ingsw.model.Updater;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.AmmoPack;
import it.polimi.ingsw.model.exceptions.NoMoreCardsException;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;
import it.polimi.ingsw.model.exceptions.UnacceptableItemNumberException;
import it.polimi.ingsw.model.exceptions.WrongTimeException;
import it.polimi.ingsw.view.CLIRenderer.HandRenderer;
import it.polimi.ingsw.view.CLIRenderer.MapRenderer;
import it.polimi.ingsw.view.CLIRenderer.PlayersRenderer;
import it.polimi.ingsw.view.CLIRenderer.WeaponRenderer;
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


        //ClientModel model = new ClientModel(3, 5, 4, weaponsOnGround, blueAmmoOnGround, redAmmoOnGround, yellowAmmoOnGround, powerUpOnGround, mapID, currentPlayer, players, playerColor, cardNumber, damage, marks, equippedWeapons, loaded, playerPosition, playerName, powerUpInHand, killshotTrack);


        //getmap okay
        //weapobox.get okay
        //playerbox almost okay
        //handbox almost okay

        //cm.setClientModel(model);
        //cli.render();
        //cli.drawModel();
    }

    @Test
    public void test2() throws UnacceptableItemNumberException, NotAvailableAttributeException, NoMoreCardsException, WrongTimeException {
        Board board = BoardConfigurer.simulateScenario();

        board.getPlayers().get(0).sufferDamage(3,board.getPlayers().get(2));
        board.getPlayers().get(2).sufferDamage(12,board.getPlayers().get(1));
        board.getPlayers().get(0).addMarks(2,board.getPlayers().get(3));
        board.getPlayers().get(2).setDead(true);
        board.getKillShotTrack().registerKill(board.getPlayers().get(0),board.getPlayers().get(2),false);
        board.getPlayers().get(2).setAmmoPack(new AmmoPack(1,2,3));

        ClientMain clientMain = new ClientMain();
        CLI cli = new CLI(clientMain);

        JsonObject mod = new JsonParser().parse((Updater.getModel(board, board.getPlayers().get(0))).get(Updater.MODEL_PROP).getAsString()).getAsJsonObject();
        clientMain.setClientModel(new Gson().fromJson(mod, ClientModel.class));
        clientMain.getClientModel().setCurrentPlayer(clientMain.getClientModel().getPlayer(2));


        String[][] s = HandRenderer.get(clientMain.getClientModel());
        //draw(s);
        s= WeaponRenderer.get(clientMain.getClientModel());
        //draw(s);
        s= PlayersRenderer.get(clientMain.getClientModel());
        //draw(s);
        s= new MapRenderer().getMap(clientMain.getClientModel());
        //draw(s);


        cli.render();
        cli.display("message 1");
        cli.display("message 2");
        cli.display("message 3");
        cli.display("message 4");
        cli.display("message 5");
        cli.display("message 6");
        System.out.println("adding request");
        cli.display("request", "3");

    }

    private void draw(String[][] s){
        for(int i=0; i<s.length; i++){
            for(int j=0; j<s[i].length; j++){
                System.out.print(s[i][j]);
            }
            System.out.print("\t "+ s[i].length+"\n");
        }
    }
}