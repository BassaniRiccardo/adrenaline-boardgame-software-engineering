//TODO load data from JSON/XML

package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static it.polimi.ingsw.model.Player.HeroName;

import static it.polimi.ingsw.model.Color.*;

/**
 * Contains several methods to configure a board.
 * Only one instance of the board configurer is allowed.
 * Helps the other classes in the process of testing, by simulating the necessary scenarios.
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
     * @return          the configured board.
     */
    public static Board configureMap(int type) {

        Board board = new Board();

        switch (type) {

              case 1:

                  List<Square> firstMap = new ArrayList<>();
                  List<WeaponSquare> spawnPoints1 = new ArrayList<>();

                  boolean[][] firstTopWall = {{true, true, true, false}, {false, true, false, true}, {true, false, true, false}};
                  boolean[][] firstLeftWall = {{true, false, false, true}, {true, false, false, false}, {false, false, false, false}};

                  firstMap.add(new AmmoSquare(board, 0, 1, 1, 1, BLUE));
                  firstMap.add(new AmmoSquare(board, 1, 1, 1, 2, BLUE));
                  firstMap.add(new WeaponSquare(board,2, 1, 1, 3, BLUE));
                  firstMap.add(new WeaponSquare(board,3, 2, 2, 1, RED));
                  firstMap.add(new AmmoSquare(board, 4, 2, 2, 2, RED));
                  firstMap.add(new AmmoSquare(board, 5, 2, 2, 3, PURPLE));
                  firstMap.add(new AmmoSquare(board, 6, 3, 2, 4, YELLOW));
                  firstMap.add(new AmmoSquare(board, 7, 4, 3, 2, GREY));
                  firstMap.add(new AmmoSquare(board, 8, 4, 3, 3, GREY));
                  firstMap.add(new WeaponSquare(board, 9, 3, 3, 4, YELLOW));

                  spawnPoints1.add((WeaponSquare) firstMap.get(2));
                  spawnPoints1.add((WeaponSquare) firstMap.get(3));
                  spawnPoints1.add((WeaponSquare) firstMap.get(9));

                  board.setMap(firstMap);
                  board.setSpawnPoints(spawnPoints1);
                  board.setTopWalls(firstTopWall);
                  board.setLeftWalls(firstLeftWall);

                  break;

              case 2:

                  ArrayList<Square> secondMap = new ArrayList<>();
                  List<WeaponSquare> spawnPoints2 = new ArrayList<>();

                  boolean[][] secondTopWall = {{true, true, true, false}, {false, false, false, true}, {false, false, true, false}};
                  boolean[][] secondLeftWall = {{true, false, false, true}, {true, true, false, false}, {false, false, false, false}};

                  secondMap.add(new AmmoSquare(board, 0, 1, 1, 1, RED));
                  secondMap.add(new AmmoSquare(board, 1, 2, 1, 2, BLUE));
                  secondMap.add(new WeaponSquare(board, 2, 2, 1, 3, BLUE));
                  secondMap.add(new WeaponSquare(board, 3, 1, 2, 1, RED));
                  secondMap.add(new AmmoSquare(board, 4, 3, 2, 2, PURPLE));
                  secondMap.add(new AmmoSquare(board, 5, 3, 2, 3, PURPLE));
                  secondMap.add(new AmmoSquare(board, 6, 4, 2, 4, YELLOW));
                  secondMap.add(new AmmoSquare(board, 7, 5, 3, 1, GREY));
                  secondMap.add(new AmmoSquare(board, 8, 5, 3, 2, GREY));
                  secondMap.add(new AmmoSquare(board, 9, 5, 3, 3, GREY));
                  secondMap.add(new WeaponSquare(board, 10, 4, 3, 4, YELLOW));

                  spawnPoints2.add((WeaponSquare) secondMap.get(2));
                  spawnPoints2.add((WeaponSquare) secondMap.get(3));
                  spawnPoints2.add((WeaponSquare) secondMap.get(10));

                  board.setMap(secondMap);
                  board.setSpawnPoints(spawnPoints2);
                  board.setTopWalls(secondTopWall);
                  board.setLeftWalls(secondLeftWall);

                  break;

              case 4:

                  ArrayList<Square> fourthMap = new ArrayList<>();
                  List<WeaponSquare> spawnPoints4 = new ArrayList<>();

                  boolean[][] fourthTopWall = {{true, true, true, true}, {false, false, false, false}, {false, false, false, false}};
                  boolean[][] fourthLeftWall = {{true, false, false, false}, {true, true, true, false}, {true, false, false, false}};

                  fourthMap.add(new AmmoSquare(board, 0, 1, 1, 1, RED));
                  fourthMap.add(new AmmoSquare(board, 1, 2, 1, 2, BLUE));
                  fourthMap.add(new WeaponSquare(board, 2, 2, 1, 3, BLUE));
                  fourthMap.add(new AmmoSquare(board, 3, 3, 1, 4, GREEN));
                  fourthMap.add(new WeaponSquare(board, 4, 1, 2, 1, RED));
                  fourthMap.add(new AmmoSquare(board, 5, 4, 2, 2, PURPLE));
                  fourthMap.add(new AmmoSquare(board, 6, 5, 2, 3, YELLOW));
                  fourthMap.add(new AmmoSquare(board, 7, 5, 2, 4, YELLOW));
                  fourthMap.add(new AmmoSquare(board, 8, 6, 3, 1, GREY));
                  fourthMap.add(new AmmoSquare(board, 9, 6, 3, 2, GREY));
                  fourthMap.add(new AmmoSquare(board, 10, 5, 3, 3, YELLOW));
                  fourthMap.add(new WeaponSquare(board, 11, 5, 3, 4, YELLOW));

                  spawnPoints4.add((WeaponSquare) fourthMap.get(2));
                  spawnPoints4.add((WeaponSquare) fourthMap.get(4));
                  spawnPoints4.add((WeaponSquare) fourthMap.get(11));

                  board.setMap(fourthMap);
                  board.setSpawnPoints(spawnPoints4);
                  board.setTopWalls(fourthTopWall);
                  board.setLeftWalls(fourthLeftWall);

                  break;

              // map 3 is good for every number of players
              default:

                  ArrayList<Square> thirdMap = new ArrayList<>();
                  List<WeaponSquare> spawnPoints3 = new ArrayList<>();

                  boolean[][] thirdTopWall = {{true, true, true, true}, {false, true, false, false}, {false, false, false, false}};
                  boolean[][] thirdLeftWall = {{true, false, false, false}, {true, false, true, false}, {true, false, false, false}};

                  thirdMap.add(new AmmoSquare(board, 0, 1, 1, 1, BLUE));
                  thirdMap.add(new AmmoSquare(board, 1, 1, 1, 2, BLUE));
                  thirdMap.add(new WeaponSquare(board, 2, 1, 1, 3, BLUE));
                  thirdMap.add(new AmmoSquare(board, 3, 2, 1, 4, GREEN));
                  thirdMap.add(new WeaponSquare(board, 4, 3, 2, 1, RED));
                  thirdMap.add(new AmmoSquare(board, 5, 3, 2, 2, RED));
                  thirdMap.add(new AmmoSquare(board, 6, 4, 2, 3, YELLOW));
                  thirdMap.add(new AmmoSquare(board, 7, 4, 2, 4, YELLOW));
                  thirdMap.add(new AmmoSquare(board, 8, 5, 3, 2, GREY));
                  thirdMap.add(new AmmoSquare(board, 9, 4, 3, 3, YELLOW));
                  thirdMap.add(new WeaponSquare(board, 10, 4, 3, 4, YELLOW));

                  spawnPoints3.add((WeaponSquare) thirdMap.get(2));
                  spawnPoints3.add((WeaponSquare) thirdMap.get(4));
                  spawnPoints3.add((WeaponSquare) thirdMap.get(10));

                  board.setMap(thirdMap);
                  board.setSpawnPoints(spawnPoints3);
                  board.setTopWalls(thirdTopWall);
                  board.setLeftWalls(thirdLeftWall);

        }

        return board;

    }


    /**
     * Adds a specified number of players to the board, and coherently sets the number of players.
     *
     * @param playerNumber         the number of player.
     * @param board                 the board the players must be added to.
     */
    public static void configurePlayerOptions(int playerNumber, Board board){

        //set players
        int i = 1;
        List<Player> allPlayers = new ArrayList<>();
        for (HeroName heroName : HeroName.values() ){
            if (i <= playerNumber) {
                allPlayers.add(new Player(i, heroName, board));
                i++;
            }
        }
        board.setPlayers(allPlayers);

    }


    /**
     * Adds a deck of weapon, a deck of ammoTiles and a deck of powerUps to the board.
     * The decks are filled and randomly shuffled.
     *
     * @param board                 the board the decks must be added to.
     */
    public static void configureDecks(Board board){

        //configures the weapons deck
        WeaponFactory weaponFactory = new WeaponFactory(board);
        Deck weaponsDeck = new Deck();
        for (Weapon.WeaponName weaponName : Weapon.WeaponName.values()) {
            weaponsDeck.addCard(weaponFactory.createWeapon(weaponName));
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
        PowerUpFactory powerUpFactory = new PowerUpFactory(board);
        Deck powerUpsDeck = new Deck();
        for (int i = 0; i< 2; i++) {
            for (PowerUp.PowerUpName powerUpName : PowerUp.PowerUpName.values()) {
                powerUpsDeck.addCard(powerUpFactory.createPowerUp(powerUpName, RED));
                powerUpsDeck.addCard(powerUpFactory.createPowerUp(powerUpName, BLUE));
                powerUpsDeck.addCard(powerUpFactory.createPowerUp(powerUpName, YELLOW));
            }
        }
        powerUpsDeck.shuffleDeck();

        board.setWeaponDeck(weaponsDeck);
        board.setAmmoDeck(ammoDeck);
        board.setPowerUpDeck(powerUpsDeck);

    }


    /**
     * Sets the ammoTiles and the weapons on the board, drawing from the respective decks.
     *
     * @param board                 the board the ammo tiles and the weapons must be added to.
     */
    public static void setAmmoTilesAndWeapons(Board board) throws UnacceptableItemNumberException, NoMoreCardsException {

        Iterator<Square> squareIt = board.getMap().iterator();
        while (squareIt.hasNext()){
            Square s = squareIt.next();
                s.addAllCards();
         }

    }


    /**
     * Configures the killShotTrack, depending on the number of skulls selected.
     *
     * @param skullNumber          the number of skulls to add to the track.
     * @param board                the board the kill shot track must be added to.
     */
    public static void configureKillShotTrack(int skullNumber, Board board){

        board.setKillShotTrack(new KillShotTrack(skullNumber, board));

    }

    /**
     * Simulates a scenario.
     * Selects the fourth board type.
     * Adds to the board five players and eight skulls.
     * The decks are configured.
     * The starting weapons and ammo tiles are placed on the board.
     *
     * @return      the board of the created scenario.
     */
    public static Board simulateScenario() throws UnacceptableItemNumberException, NoMoreCardsException {

        //sets the board components
        Board board = configureMap(4);
        configurePlayerOptions(5, board);
        configureDecks(board);
        configureKillShotTrack(8, board);
        setAmmoTilesAndWeapons(board);

        //sets the players positions on the board
        board.getPlayers().get(0).setPosition(board.getMap().get(0));
        board.getPlayers().get(1).setPosition(board.getMap().get(1));
        board.getPlayers().get(2).setPosition(board.getMap().get(2));
        board.getPlayers().get(3).setPosition(board.getMap().get(3));
        board.getPlayers().get(4).setPosition(board.getMap().get(11));

        return board;

    }

}