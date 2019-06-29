package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.board.*;
import it.polimi.ingsw.model.cards.AmmoPack;
import it.polimi.ingsw.model.cards.AmmoTile;
import it.polimi.ingsw.model.cards.PowerUp;
import it.polimi.ingsw.model.cards.Weapon;
import it.polimi.ingsw.model.exceptions.NoMoreCardsException;
import it.polimi.ingsw.model.exceptions.UnacceptableItemNumberException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static it.polimi.ingsw.model.board.Player.HeroName;

/**
 * Contains several methods to configure a board.
 * Only one instance of the board configurer is allowed.
 * Helps the other classes in the process of testing, by simulating the necessary scenarios.
 *
 * @author  BassaniRiccardo, serialized by davidealde
 */

public class BoardConfigurer {

    private static BoardConfigurer instance = null;
    private static ModelDataReader j = new ModelDataReader();
    private static final String BOARDS = "boards";
    private static final String AMMO_TILES = "ammoTiles";
    private static final String AMMO_SQUARE = "aS";
    private static final String WEAPON_SQUARE = "wS";
    private static final String ID = "Id";



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
     * @param b         the type of map, to be chosen between 1,2,3,4.
     * @return          the configured board.
     */
    public static Board configureMap(int b) {

        Board board = new Board();

        board.setId(b);
        List<Square> map = new ArrayList<>();
        List<WeaponSquare> spawnPoints = new ArrayList<>();

        int rowsNumber = j.getIntBC("rowsNumber");
        int columnNumber = j.getIntBC("columnsNumber");
        boolean[][] topWall = new boolean[rowsNumber][columnNumber];
        boolean[][] leftWall = new boolean[rowsNumber][columnNumber];

        for (int i = 1; i <= rowsNumber; i++) {
            for (int k = 1; k <= columnNumber; k++) {
                topWall[i-1][k-1] = j.getBooleanBC("wallT" + i + k, BOARDS, b);
                leftWall[i-1][k-1] = j.getBooleanBC("wallL" + i + k, BOARDS, b);
            }
        }

        int ammoSquareNumber = j.getIntBC("aSNumber", BOARDS, b);
        int weaponSquareNumber = j.getIntBC("wSNumber", BOARDS, b);

        int w=1;
        int a=1;
        for (int i = 0; i < weaponSquareNumber+ammoSquareNumber; i++){
            if((j.getIntBC(WEAPON_SQUARE + w + ID, BOARDS, b)) == i){
                map.add(new WeaponSquare(board, j.getIntBC(WEAPON_SQUARE + w + ID, BOARDS, b),
                        j.getIntBC(WEAPON_SQUARE + w + "RoomId", BOARDS,b),
                        j.getIntBC(WEAPON_SQUARE + w + "Row", BOARDS,b),
                        j.getIntBC(WEAPON_SQUARE + w + "Column", BOARDS,b),
                        j.getColorBC(WEAPON_SQUARE + w + "Color", BOARDS, b)));
                w++;
            }else{
                map.add(new AmmoSquare(board, j.getIntBC(AMMO_SQUARE + a + ID, BOARDS, b),
                        j.getIntBC(AMMO_SQUARE + a + "RoomId", BOARDS, b),
                        j.getIntBC(AMMO_SQUARE + a + "Row", BOARDS, b),
                        j.getIntBC(AMMO_SQUARE + a + "Column", BOARDS, b),
                        j.getColorBC(AMMO_SQUARE + a + "Color", BOARDS, b)));
                a++;
            }
        }

        for(int i=1;i<=weaponSquareNumber;i++)
            spawnPoints.add((WeaponSquare) map.get(j.getIntBC(WEAPON_SQUARE + i + ID, BOARDS, b)));

        board.setMap(map);
        board.setSpawnPoints(spawnPoints);
        board.setTopWalls(topWall);
        board.setLeftWalls(leftWall);

        return board;
    }


    /**
     * Adds a specified number of players to the board, and coherently sets the number of players.
     *
     * @param playerNumber          the number of player.
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
        int ammoTilesTypesNumber = j.getIntBC("ammoTilesTypesNumber");
        for(int i=0; i < ammoTilesTypesNumber; i++){
            for(int k = 0; k < j.getIntBC("quantity", AMMO_TILES, i); k++){
                    ammoDeck.addCard((new AmmoTile((j.getBooleanBC("pU", AMMO_TILES, i)),
                        new AmmoPack(j.getIntBC("r", AMMO_TILES, i),
                                j.getIntBC("b", AMMO_TILES, i),
                                j.getIntBC("y", AMMO_TILES, i)))));
            }
        }
        ammoDeck.shuffleDeck();

        //configures the powerUps deck
        PowerUpFactory powerUpFactory = new PowerUpFactory(board);
        Deck powerUpsDeck = new Deck();
        for (int i = 0; i< j.getIntBC("pUNumberPerColor"); i++) {
            for (PowerUp.PowerUpName powerUpName : PowerUp.PowerUpName.values()) {
                for(int k = 0; k< j.getIntBC("pUColorsNumber"); k++) {
                    powerUpsDeck.addCard(powerUpFactory.createPowerUp(powerUpName, j.getColorBC("pUColor"+k)));
                }
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
     * @throws UnacceptableItemNumberException      if it is thrown by addAllCards().
     * @throws NoMoreCardsException                 if it is thrown by addAllCards().
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
     * @throws      UnacceptableItemNumberException         if thrown by setAmmoTilesAndWeapons
     * @throws      NoMoreCardsException                    if thrown by setAmmoTilesAndWeapons
     */
    public static Board simulateScenario() throws UnacceptableItemNumberException, NoMoreCardsException {

        //sets the board components
        Board board = configureMap(j.getIntBC("simulationMapNumber"));
        configurePlayerOptions(j.getIntBC("simulationPlayersNumber"), board);
        configureDecks(board);
        configureKillShotTrack(j.getIntBC("simulationSkullsNumber"), board);
        setAmmoTilesAndWeapons(board);

        //sets the players positions on the board
        for(int i = 0; i< j.getIntBC("simulationPlayersNumber"); i++) {
            board.getPlayers().get(i).setPosition(board.getMap().get(j.getIntBC("simulationPositionPlayer" + i)));
            board.getPlayers().get(i).setInGame(true);
        }
        board.setCurrentPlayer(board.getPlayers().get(0));
        return board;

    }

}