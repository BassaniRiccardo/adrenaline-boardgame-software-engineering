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
import org.junit.Test;


public class GUITest {

    /*@Test

    public void render() throws UnacceptableItemNumberException, NoMoreCardsException {

        Board board = BoardConfigurer.configureMap(4);
        WeaponFactory weaponFactory = new WeaponFactory(board);
        PowerUpFactory powerUpFactory = new PowerUpFactory(board);
        Deck wd = new Deck();
        for (int i = 0; i < 21; i++) {
            wd.addCard(weaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE));
        }
        board.setWeaponDeck(wd);
        Deck pd = new Deck();
        for (int i = 0; i < 24; i++) {
            pd.addCard(powerUpFactory.createPowerUp(PowerUp.PowerUpName.TELEPORTER, Color.RED));
        }
        board.setPowerUpDeck(pd);
        Deck ad = new Deck();
        for (int i = 0; i < 36; i++) {
            ad.addCard(new AmmoTile(true, new AmmoPack(1, 2, 0)));
        }
        board.setAmmoDeck(ad);
        board.setKillShotTrack(new KillShotTrack(7, board));
        board.getPlayers().add(new Player(1, Player.HeroName.BANSHEE, board));
        board.getPlayers().add(new Player(2, Player.HeroName.DOZER, board));
        board.getPlayers().get(0).setUsername("Giuliano");
        board.getPlayers().get(0).addWeapon(weaponFactory.createWeapon(Weapon.WeaponName.POWER_GLOVE));
        board.getSpawnPoints().get(2).addCard(weaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE));*/

    @Test
    public void render() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException, WrongTimeException {

         Board board = BoardConfigurer.simulateScenario();

         board.getPlayers().get(0).sufferDamage(3,board.getPlayers().get(2));
        board.getPlayers().get(2).sufferDamage(12,board.getPlayers().get(1));
        board.getPlayers().get(0).addMarks(2,board.getPlayers().get(3));
        board.getPlayers().get(2).setDead(true);
        board.getKillShotTrack().registerKill(board.getPlayers().get(0),board.getPlayers().get(2),false);
        board.getPlayers().get(2).setAmmoPack(new AmmoPack(1,2,3));

        new Thread() {
            @Override
            public void run() {
                javafx.application.Application.launch(GUI.class);
            }
        }.start();
        UI ui = GUI.waitGUI();
        ClientMain clientMain = new ClientMain();
        ((GUI) ui).setClientMain(clientMain);

        JsonObject mod = new JsonParser().parse((Updater.getModel(board, board.getPlayers().get(0))).get("mod").getAsString()).getAsJsonObject();
        ((GUI)ui).getClientMain().setClientModel(new Gson().fromJson(mod, ClientModel.class));
        ((GUI)ui).getClientMain().getClientModel().setCurrentPlayer(((GUI)ui).getClientMain().getClientModel().getPlayer(1));
        ((GUI) ui).render();
        try {
            Thread.sleep(1000000);

        } catch (InterruptedException e){}



    }



    @Test
    public void display() {
        new Thread() {
            @Override
            public void run() {
                javafx.application.Application.launch(GUI.class);
            }
        }.start();
        GUI gui = GUI.waitGUI();
        gui.display("ciao");

    }
}