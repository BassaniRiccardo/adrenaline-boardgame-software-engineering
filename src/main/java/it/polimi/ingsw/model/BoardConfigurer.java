//TODO load data from JSON/XML

package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static it.polimi.ingsw.model.Player.HeroName;

import static it.polimi.ingsw.model.Color.*;

/**
 * Contains several methods to configure the board.
 * Only one instance of the board configurer is allowed.
 * Helps the other classes in the process of testing, by simulating the necessary scenarios.
 * It will be modified and incorporated in the controller.
 *
 * @author  BassaniRiccardo
 */

public class BoardConfigurer {

    private static BoardConfigurer instance = null;


    /**
     * Constructs a board configurer.
     */
    private BoardConfigurer() {}


    /**
     * Returns the unique instance of the board configurer.
     * If not already existing, it calls BoardConfigurer private constructor.
     *
     * @return      the unique instance of the board configurer.
     */
    public static BoardConfigurer getInstance() {

        if (instance == null){
            instance = new BoardConfigurer();
        }
        return instance;

    }


    /**
     * Configures the board setting the map and the walls.
     *
     * @param type      the type of map, to be chosen between 1,2,3,4.
     */
    public void configureMap(int type){

        switch (type) {

            case 1:

                List<Square> firstMap = new ArrayList<>();
                List<WeaponSquare> spawnPoints1 = new ArrayList<>();

                boolean[][] firstTopWall = {{true,true,true,false},{false,true,false,true},{true,false,true,false}};
                boolean[][] firstLeftWall = {{true,false,false,true},{true,false,false,false},{false,false,false,false}};

                firstMap.add(new AmmoSquare(0,1,1,1, BLUE));
                firstMap.add(new AmmoSquare(1,1,1,2, BLUE));
                firstMap.add(new WeaponSquare(2,1,1,3, BLUE));
                firstMap.add(new WeaponSquare(3,2,2,1, RED));
                firstMap.add(new AmmoSquare(4,2,2,2, RED));
                firstMap.add(new AmmoSquare(5,2,2,3, PURPLE));
                firstMap.add(new AmmoSquare(6,3,2,4, YELLOW));
                firstMap.add(new AmmoSquare(7,4,3,2, GREY));
                firstMap.add(new AmmoSquare(8,4,3,3, GREY));
                firstMap.add(new WeaponSquare(9,3,3,4, YELLOW));

                spawnPoints1.add((WeaponSquare) firstMap.get(2));
                spawnPoints1.add((WeaponSquare) firstMap.get(3));
                spawnPoints1.add((WeaponSquare) firstMap.get(9));

                Board.getInstance().setMap(firstMap);
                Board.getInstance().setSpawnPoints(spawnPoints1);
                Board.getInstance().setTopWall(firstTopWall);
                Board.getInstance().setLeftWall(firstLeftWall);

                break;

            case 2:

                ArrayList<Square> secondMap = new ArrayList<>();
                List<WeaponSquare> spawnPoints2 = new ArrayList<>();

                boolean[][] secondTopWall = {{true,true,true,false},{false,false,false,true},{false,false,true,false}};
                boolean[][] secondLeftWall = {{true,false,false,true},{true,true,false,false},{false,false,false,false}};

                secondMap.add(new AmmoSquare(0,1,1,1, RED));
                secondMap.add(new AmmoSquare(1,2,1,2, BLUE));
                secondMap.add(new WeaponSquare(2,2,1,3, BLUE));
                secondMap.add(new WeaponSquare(3,1,2,1, RED));
                secondMap.add(new AmmoSquare(4,3,2,2, PURPLE));
                secondMap.add(new AmmoSquare(5,3,2,3, PURPLE));
                secondMap.add(new AmmoSquare(6,4,2,4, YELLOW));
                secondMap.add(new AmmoSquare(7,5,3,1, GREY));
                secondMap.add(new AmmoSquare(8,5,3,2, GREY));
                secondMap.add(new AmmoSquare(9,5,3,3, GREY));
                secondMap.add(new WeaponSquare(10,4,3,4, YELLOW));

                spawnPoints2.add((WeaponSquare) secondMap.get(2));
                spawnPoints2.add((WeaponSquare) secondMap.get(3));
                spawnPoints2.add((WeaponSquare) secondMap.get(10));

                Board.getInstance().setMap(secondMap);
                Board.getInstance().setSpawnPoints(spawnPoints2);
                Board.getInstance().setTopWall(secondTopWall);
                Board.getInstance().setLeftWall(secondLeftWall);

                break;

            case 4:

                ArrayList<Square> fourthMap = new ArrayList<>();
                List<WeaponSquare> spawnPoints4 = new ArrayList<>();

                boolean[][] fourthTopWall = {{true,true,true,true},{false,false,false,false},{false,false,false,false}};
                boolean[][] fourthLeftWall = {{true,false,false,false},{true,true,true,false},{true,false,false,false}};

                fourthMap.add(new AmmoSquare(0,1,1,1, RED));
                fourthMap.add(new AmmoSquare(1,2,1,2, BLUE));
                fourthMap.add(new WeaponSquare(2,2,1,3, BLUE));
                fourthMap.add(new AmmoSquare(3,3,1,4, GREEN));
                fourthMap.add(new WeaponSquare(4,1,2,1, RED));
                fourthMap.add(new AmmoSquare(5,4,2,2, PURPLE));
                fourthMap.add(new AmmoSquare(6,5,2,3, YELLOW));
                fourthMap.add(new AmmoSquare(7,5,2,4, YELLOW));
                fourthMap.add(new AmmoSquare(8,6,3,1, GREY));
                fourthMap.add(new AmmoSquare(9,6,3,2, GREY));
                fourthMap.add(new AmmoSquare(10,5,3,3, YELLOW));
                fourthMap.add(new WeaponSquare(11,5,3,4, YELLOW));

                spawnPoints4.add((WeaponSquare) fourthMap.get(2));
                spawnPoints4.add((WeaponSquare) fourthMap.get(4));
                spawnPoints4.add((WeaponSquare) fourthMap.get(11));

                Board.getInstance().setMap(fourthMap);
                Board.getInstance().setSpawnPoints(spawnPoints4);
                Board.getInstance().setTopWall(fourthTopWall);
                Board.getInstance().setLeftWall(fourthLeftWall);

                break;

            // map 3 is good for every number of players
            default:

                ArrayList<Square> thirdMap = new ArrayList<>();
                List<WeaponSquare> spawnPoints3 = new ArrayList<>();

                boolean[][] thirdTopWall = {{true,true,true,true},{false,true,false,false},{false,false,false,false}};
                boolean[][] thirdLeftWall = {{true,false,false,false},{true,false,true,false},{true,false,false,false}};

                thirdMap.add(new AmmoSquare(0,1,1,1, BLUE));
                thirdMap.add(new AmmoSquare(1,1,1,2, BLUE));
                thirdMap.add(new WeaponSquare(2,1,1,3, BLUE));
                thirdMap.add(new AmmoSquare(3,2,1,4, GREEN));
                thirdMap.add(new WeaponSquare(4,3,2,1, RED));
                thirdMap.add(new AmmoSquare(5,3,2,2, RED));
                thirdMap.add(new AmmoSquare(6,4,2,3, YELLOW));
                thirdMap.add(new AmmoSquare(7,4,2,4, YELLOW));
                thirdMap.add(new AmmoSquare(8,5,3,2, GREY));
                thirdMap.add(new AmmoSquare(9,4,3,3, YELLOW));
                thirdMap.add(new WeaponSquare(10,4,3,4, YELLOW));

                spawnPoints3.add((WeaponSquare) thirdMap.get(2));
                spawnPoints3.add((WeaponSquare) thirdMap.get(4));
                spawnPoints3.add((WeaponSquare) thirdMap.get(10));

                Board.getInstance().setMap(thirdMap);
                Board.getInstance().setSpawnPoints(spawnPoints3);
                Board.getInstance().setTopWall(thirdTopWall);
                Board.getInstance().setLeftWall(thirdLeftWall);

        }

    }


    /**
     * Adds a specified number of players to the board, and coherently sets the number of players.
     *
     * @param playerNumber         the number of player.
     */
    public void configurePlayerOptions(int playerNumber){

        //set players
        int i = 1;
        List<Player> allPlayers = new ArrayList<>();
        for (HeroName heroName : HeroName.values() ){
            if (i <= playerNumber) {
                allPlayers.add(new Player(i, heroName));
                i++;
            }
        }
        Board.getInstance().setPlayers(allPlayers);

        //setPlayerNumber
        Board.getInstance().setPlayerNumber(playerNumber);

    }


    /**
     * Adds a deck of weapon, a deck of ammoTiles and a deck of powerUps to the board.
     * The decks are filled and randomly shuffled.
     */
    public void configureDecks(){

        //configures the weapons deck
        Deck weaponsDeck = new Deck();
        for (Weapon.WeaponName weaponName : Weapon.WeaponName.values()) {
            weaponsDeck.addCard(WeaponFactory.createWeapon(weaponName));
        }
        weaponsDeck.shuffleDeck();

        //configures the ammo deck
        Deck ammoDeck = new Deck();
        for (int i = 0; i < 3; i++) {
            ammoDeck.addCard(new AmmoTile(false, new AmmoPack(0, 2, 1)));
            ammoDeck.addCard(new AmmoTile(false, new AmmoPack(0, 1, 2)));
            ammoDeck.addCard(new AmmoTile(false, new AmmoPack(1, 0, 2)));
            ammoDeck.addCard(new AmmoTile(false, new AmmoPack(1, 2, 0)));
            ammoDeck.addCard(new AmmoTile(false, new AmmoPack(2, 1, 0)));
            ammoDeck.addCard(new AmmoTile(false, new AmmoPack(2, 0, 1)));
        }
        for (int i = 0; i < 2; i++) {
            ammoDeck.addCard(new AmmoTile(true, new AmmoPack(2, 0, 0)));
            ammoDeck.addCard(new AmmoTile(true, new AmmoPack(0, 2, 0)));
            ammoDeck.addCard(new AmmoTile(true, new AmmoPack(0, 0, 2)));
        }
        for (int i = 0; i < 4; i++) {
            ammoDeck.addCard(new AmmoTile(true, new AmmoPack(0, 1, 1)));
            ammoDeck.addCard(new AmmoTile(true, new AmmoPack(1, 0, 1)));
            ammoDeck.addCard(new AmmoTile(true, new AmmoPack(1, 1, 0)));
        }
        ammoDeck.shuffleDeck();

        //configures the powerUps deck
        Deck powerUpsDeck = new Deck();
        for (int i = 0; i< 2; i++) {
            for (PowerUp.PowerUpName powerUpName : PowerUp.PowerUpName.values()) {
                powerUpsDeck.addCard(PowerUpFactory.createPowerUp(powerUpName, RED));
                powerUpsDeck.addCard(PowerUpFactory.createPowerUp(powerUpName, BLUE));
                powerUpsDeck.addCard(PowerUpFactory.createPowerUp(powerUpName, YELLOW));
            }
        }
        powerUpsDeck.shuffleDeck();

        Board.getInstance().setWeaponDeck(weaponsDeck);
        Board.getInstance().setAmmoDeck(ammoDeck);
        Board.getInstance().setPowerUpDeck(powerUpsDeck);

    }


    /**
     * Sets the ammoTiles and the weapons on the board, drawing from the respective decks.
     */
    public void setAmmoTilesAndWeapons() throws UnacceptableItemNumberException, NoMoreCardsException {

        Iterator<Square> squareIt = Board.getInstance().getMap().iterator();
        while (squareIt.hasNext()){
            Square s = squareIt.next();
                s.addAllCards();
         }

    }


    /**
     * Configures the killShotTrack, depending on the number of skulls selected.
     *
     * @param skullNumber          the number of skulls to add to the track.
     */
    public void configureKillShotTrack(int skullNumber){

        Board.getInstance().setKillShotTrack(new KillShotTrack(skullNumber));

    }


    /**
     * Simulates a scenario.
     * Selects the fourth board type.
     * Adds to the board five players and eight skulls.
     * The decks are configured.
     * The starting weapons and ammo tiles are placed on the board.    *
     */
    public void simulateScenario() throws UnacceptableItemNumberException, NoMoreCardsException {

        //sets the board components
        configureMap(4);
        configurePlayerOptions(5);
        configureDecks();
        configureKillShotTrack(8);
        setAmmoTilesAndWeapons();

        //sets the players positions on the board
        Board.getInstance().getPlayers().get(0).setPosition(Board.getInstance().getMap().get(0));
        Board.getInstance().getPlayers().get(1).setPosition(Board.getInstance().getMap().get(1));
        Board.getInstance().getPlayers().get(2).setPosition(Board.getInstance().getMap().get(2));
        Board.getInstance().getPlayers().get(3).setPosition(Board.getInstance().getMap().get(3));
        Board.getInstance().getPlayers().get(4).setPosition(Board.getInstance().getMap().get(11));

    }

}