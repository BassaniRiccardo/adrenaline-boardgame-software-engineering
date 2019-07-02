package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.BoardConfigurer;
import it.polimi.ingsw.controller.PowerUpFactory;
import it.polimi.ingsw.controller.WeaponFactory;
import it.polimi.ingsw.model.board.*;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.exceptions.UnacceptableItemNumberException;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests the method getModel of the class WeaponSquare.
 * The method shared with the class AmmoSquare are tested in SquareTest.
 *
 * @author BassaniRiccardo
 */

public class UpdaterTest {

    /**
     * Tests the method getModel, simulating a game and checking that the client model is build correctly.
     *
     * @throws UnacceptableItemNumberException if thrown by addWeapon() or addCard().
     */
    @Test
    public void getModel() throws  UnacceptableItemNumberException{
        Board board = BoardConfigurer.configureMap(4);
        WeaponFactory weaponFactory = new WeaponFactory(board);
        PowerUpFactory powerUpFactory = new PowerUpFactory(board);
        Deck wd = new Deck();
        for (int i = 0; i<21; i++) {
            wd.addCard(weaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE));
        }
        board.setWeaponDeck(wd);
        Deck pd = new Deck();
        for (int i = 0; i<24; i++) {
            pd.addCard(powerUpFactory.createPowerUp(PowerUp.PowerUpName.TELEPORTER, Color.RED));
        }
        board.setPowerUpDeck(pd);
        Deck ad = new Deck();
        for (int i = 0; i<36; i++) {
            ad.addCard(new AmmoTile(true,new AmmoPack(1,2,0)));
        }
        board.setAmmoDeck(ad);
        board.setKillShotTrack(new KillShotTrack(7, board));
        board.getPlayers().add(new Player(1, Player.HeroName.BANSHEE, board));
        board.getPlayers().add(new Player(2, Player.HeroName.DOZER, board));
        board.setCurrentPlayer(board.getPlayers().get(0));
        board.getPlayers().get(0).setUsername("Giuliano");
        board.getPlayers().get(0).addWeapon(weaponFactory.createWeapon(Weapon.WeaponName.POWER_GLOVE));
        board.getSpawnPoints().get(2).addCard(weaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE));
        String modelString = Updater.getModel(board, board.getPlayers().get(0)).toString();
        assertEquals("{\"head\":\"UPD\",\"type\":\"model\",\"model\":\"" +
                        "{\\\"squares\\\":[{\\\"id\\\":0,\\\"spawnPoint\\\":false,\\\"weapons\\\":[],\\\"blueAmmo\\\":0,\\\"redAmmo\\\":0,\\\"yellowAmmo\\\":0,\\\"powerup\\\":false},{\\\"id\\\":1,\\\"spawnPoint\\\":false,\\\"weapons\\\":[],\\\"blueAmmo\\\":0,\\\"redAmmo\\\":0,\\\"yellowAmmo\\\":0,\\\"powerup\\\":false},{\\\"id\\\":2,\\\"spawnPoint\\\":true,\\\"weapons\\\":[],\\\"blueAmmo\\\":0,\\\"redAmmo\\\":0,\\\"yellowAmmo\\\":0,\\\"powerup\\\":false},{\\\"id\\\":3,\\\"spawnPoint\\\":false,\\\"weapons\\\":[],\\\"blueAmmo\\\":0,\\\"redAmmo\\\":0,\\\"yellowAmmo\\\":0,\\\"powerup\\\":false},{\\\"id\\\":4,\\\"spawnPoint\\\":true,\\\"weapons\\\":[],\\\"blueAmmo\\\":0,\\\"redAmmo\\\":0,\\\"yellowAmmo\\\":0,\\\"powerup\\\":false},{\\\"id\\\":5,\\\"spawnPoint\\\":false,\\\"weapons\\\":[],\\\"blueAmmo\\\":0,\\\"redAmmo\\\":0,\\\"yellowAmmo\\\":0,\\\"powerup\\\":false},{\\\"id\\\":6,\\\"spawnPoint\\\":false,\\\"weapons\\\":[],\\\"blueAmmo\\\":0,\\\"redAmmo\\\":0,\\\"yellowAmmo\\\":0,\\\"powerup\\\":false},{\\\"id\\\":7,\\\"spawnPoint\\\":false,\\\"weapons\\\":[],\\\"blueAmmo\\\":0,\\\"redAmmo\\\":0,\\\"yellowAmmo\\\":0,\\\"powerup\\\":false},{\\\"id\\\":8,\\\"spawnPoint\\\":false,\\\"weapons\\\":[],\\\"blueAmmo\\\":0,\\\"redAmmo\\\":0,\\\"yellowAmmo\\\":0,\\\"powerup\\\":false},{\\\"id\\\":9,\\\"spawnPoint\\\":false,\\\"weapons\\\":[],\\\"blueAmmo\\\":0,\\\"redAmmo\\\":0,\\\"yellowAmmo\\\":0,\\\"powerup\\\":false},{\\\"id\\\":10,\\\"spawnPoint\\\":false,\\\"weapons\\\":[],\\\"blueAmmo\\\":0,\\\"redAmmo\\\":0,\\\"yellowAmmo\\\":0,\\\"powerup\\\":false},{\\\"id\\\":11,\\\"spawnPoint\\\":true,\\\"weapons\\\":[{\\\"name\\\":\\\"Lock Rifle\\\",\\\"loaded\\\":false}],\\\"blueAmmo\\\":0,\\\"redAmmo\\\":0,\\\"yellowAmmo\\\":0,\\\"powerup\\\":false}]," +
                        "\\\"players\\\":[{\\\"id\\\":1,\\\"color\\\":\\\"blue\\\",\\\"cardNumber\\\":0,\\\"damage\\\":[],\\\"marks\\\":[],\\\"weapons\\\":[{\\\"name\\\":\\\"Power Glove\\\",\\\"loaded\\\":false}],\\\"username\\\":\\\"Giuliano\\\",\\\"redAmmo\\\":1,\\\"blueAmmo\\\":1,\\\"yellowAmmo\\\":1,\\\"flipped\\\":false,\\\"inGame\\\":false,\\\"points\\\":0,\\\"deaths\\\":0,\\\"nextDeathAwards\\\":8,\\\"status\\\":\\\"BASIC\\\"},{\\\"id\\\":2,\\\"color\\\":\\\"grey\\\",\\\"cardNumber\\\":0,\\\"damage\\\":[],\\\"marks\\\":[],\\\"weapons\\\":[],\\\"username\\\":\\\"anonymous\\\",\\\"redAmmo\\\":1,\\\"blueAmmo\\\":1,\\\"yellowAmmo\\\":1,\\\"flipped\\\":false,\\\"inGame\\\":false,\\\"points\\\":0,\\\"deaths\\\":0,\\\"nextDeathAwards\\\":8,\\\"status\\\":\\\"BASIC\\\"}]," +
                        "\\\"weaponCardsLeft\\\":21," +
                        "\\\"powerUpCardsLeft\\\":24," +
                        "\\\"mapID\\\":4," +
                        "\\\"currentPlayerId\\\":1," +
                        "\\\"killShotTrack\\\":[]," +
                        "\\\"skullsLeft\\\":7," +
                        "\\\"points\\\":0," +
                        "\\\"powerUpInHand\\\":[]," +
                        "\\\"colorPowerUpInHand\\\":[]," +
                        "\\\"playerID\\\":1}\"}",
                modelString);

    }
}