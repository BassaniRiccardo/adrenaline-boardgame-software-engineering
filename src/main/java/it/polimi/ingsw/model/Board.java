package it.polimi.ingsw.model;

import java.util.*;

/**
 * Represents the game board, made of a map with walls.
 * Only one instance of the board is allowed.
 * Contains the players and information about the players.
 * Contains a deck of weapons, a deck of ammo tiles, a deck of power ups and a killshot track.
 * Provides information about the relations between the squares in the map.
 *
 * @author  BassaniRiccardo
 */

public class Board {

    //the unique instance of Board
    private static Board instance = null;

    //depend on the map_id, set by BoardConfigurer
    private List<Square> map;
    private boolean[][] leftWall;
    private boolean[][] topWall;
    private List<WeaponSquare> spawnPoints;

    //depend on the game settings, set by BoardConfigurer
    private List<Player> players;
    private int playerNumber;

    //set by BoardConfigurer
    private Deck weaponDeck;
    private Deck powerUpDeck;
    private Deck ammoDeck;

    //depends on skullsNumber, set by the BoardConfigurer
    private KillShotTrack killShotTrack;


    /**
     * Constructs a board with no map, walls, players, weapons, nor killshot track.
     */
    private Board() {

        this.map = null;
        this.topWall = null;
        this.leftWall = null;
        this.spawnPoints = new ArrayList<>();

        this.players = new ArrayList<>();
        this.playerNumber = 0;

        this.weaponDeck = new Deck();
        this.powerUpDeck = new Deck();
        this.ammoDeck = new Deck();

        this.killShotTrack =null;

    }


    /**
     * Returns the unique instance of the board.
     * If not already existing, it calls Board private constructor.
     *
     * @return      the unique instance of the board.
     */
    public static Board getInstance() {

        if (instance == null){
            instance = new Board();
        }
        return instance;

    }


    /**
     * Getter for map.
     *
     * @return      the map.
     */
    public List<Square> getMap() { return map;  }


    /**
     * Getter for a map.
     *
     * @return      the spawn points.
     */
    public List<WeaponSquare> getSpawnPoints() {
        return spawnPoints;
    }


    /**
     * Getter for players.
     *
     * @return      the players on the board.
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Getter for playerNumber.
     *
     * @return      the number of players on the board.
     */
    public int getPlayerNumber() {
        return playerNumber;
    }

    /**
     * Getter for weaponsDek.
     *
     * @return      the deck of weapons.
     */
    public Deck getWeaponDeck() {
        return weaponDeck;
    }

    /**
     * Getter for powerUpDeck.
     *
     * @return      the deck of power ups.
     */
    public Deck getPowerUpDeck() {
        return powerUpDeck;
    }

    /**
     * Getter for ammoUpsDeck.
     *
     * @return      the deck of ammo.
     */
    public Deck getAmmoDeck() {
        return ammoDeck;
    }

    /**
     * Getter for killShotTrack.
     *
     * @return      the killshot track.
     */
    public KillShotTrack getKillShotTrack() {
        return killShotTrack;
    }


    /**
     * Setter for map.
     *
     * @param map   the value to assign to map.
     */
    public void setMap(List<Square> map) {
        this.map = map;
    }

    /**
     * Setter for leftWall.
     *
     * @param leftWall   the value to assign to leftWall.
     */
    public void setLeftWall(boolean[][] leftWall) {
        this.leftWall = leftWall;
    }

    /**
     * Setter for topWall.
     *
     * @param topWall   the value to assign to topWall.
     */
    public void setTopWall(boolean[][] topWall) {
        this.topWall = topWall;
    }

    /**
     * Setter for spawnPoints.
     *
     * @param spawnPoints       the value to assign to spawnPoints.
     */
    public void setSpawnPoints(List<WeaponSquare> spawnPoints) {
        this.spawnPoints = spawnPoints;
    }

    /**
     * Setter for players.
     *
     * @param players   the value to assign to players.
     */
    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    /**
     * Setter for playerNumber.
     *
     * @param playerNumber   the value to assign to playerNumber.
     */
    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    /**
     * Setter for weaponDeck.
     *
     * @param weaponDeck   the value to assign to weaponDeck.
     */
    public void setWeaponDeck(Deck weaponDeck) {
        this.weaponDeck = weaponDeck;
    }

    /**
     * Setter for powerUpDeck.
     *
     * @param powerUpDeck   the value to assign to powerUpDeck.
     */
    public void setPowerUpDeck(Deck powerUpDeck) {
        this.powerUpDeck = powerUpDeck;
    }

    /**
     * Setter for ammoDeck.
     *
     * @param ammoDeck   the value to assign to ammoDeck.
     */
    public void setAmmoDeck(Deck ammoDeck) {
        this.ammoDeck = ammoDeck;
    }

    /**
     * Setter for killShotTrack.
     *
     * @param killShotTrack   the value to assign to killShotTrack.
     */
    public void setKillShotTrack(KillShotTrack killShotTrack) {
        this.killShotTrack = killShotTrack;
    }


    /**
     * Returns the players in the specified square.
     *
     * @param s         the specified square.
     * @return          the players in the square.
     */
    public List<Player> getPlayersInside(Square s){
        return s.getPlayers();
    }


    /**
     * Returns the squares adjacent to the specified starting square.
     *
     * @param   s       the starting square.
     * @return          the squares adjacent to s.
     */
    public List<Square> getAdjacent(Square s) {

        List<Square> adjacent = new ArrayList<>();
        int c;
        int r;
        int sc;
        int sr;
        sc = s.getColumn();
        sr = s.getRow();
        Iterator<Square> squareIt = map.iterator();
        while (squareIt.hasNext()){
            Square s1 = squareIt.next();
            c = s1.getColumn();
            r = s1.getRow();
            if     (r == sr - 1 && c == sc && !topWall[sr-1][sc-1]    ||            //top
                    r == sr + 1 && c == sc && !topWall[r-1][c-1]  ||                //down
                    r == sr && c == sc + 1 && !leftWall[r-1][c-1]   ||              //right
                    r == sr && c == sc - 1 && !leftWall[sr-1][sc-1]) {              //left
                adjacent.add(s1);
            }
        }
        return adjacent;

    }


    /**
     * Returns the squares reachable from the starting square with the specified number of steps.
     * The starting square is excluded.
     *
     * @param s             the starting square.
     * @param steps         the number of steps that can be taken.
     * @return              the reachable squares.
     */
    public List<Square> getReachable (Square s, int steps) {

        List<Square> reachable = new ArrayList<>();
        Iterator<Square> squareIt = map.iterator();
        while (squareIt.hasNext()){
            Square s1 = squareIt.next();
            if (!s.equals(s1) && getDistance(s,s1) <= steps) {
                reachable.add(s1);
            }
        }
        return reachable;

    }


    /**
     * Returns the square visible from the starting square.
     * The starting square is excluded.
     *
     * @param s             the starting square.
     * @return              the visible squares.
     */
    public List<Square> getVisible (Square s) {

        List<Square> visible = new ArrayList<>();
        for (Square s1 : map) {
            if (!s.equals(s1)) {
                if (getSquaresInSameRoom(s).contains(s1)) {
                    visible.add(s1);
                } else {
                    for (Square adj : getAdjacent(s)) {
                        if (getSquaresInSameRoom(s1).contains(adj) || s1.getId() == adj.getId()) {
                            visible.add(s1);
                        }
                    }
                }
            }
        }
        return visible;

    }


    /**
     * Returns the squares in the same room of the starting square.
     * The starting square is excluded.
     *
     * @param s         the starting square.
     * @return          the squares in the same room.
     */
    public List<Square> getSquaresInSameRoom (Square s){

        List<Square> sameRoom = new ArrayList<>();
        Iterator<Square> squareIt = map.iterator();
        while (squareIt.hasNext()) {
            Square s1 = squareIt.next();
            if (s.getRoomId() == s1.getRoomId() &&!s.equals(s1)) {
                sameRoom.add(s1);
            }
        }
        return sameRoom;

    }


    /**
     * Returns the squares reachable moving or shooting in a specified direction from the starting square.
     * The starting square is excluded.
     *
     * @param s                 the starting square.
     * @param direction         the direction.
     * @return                  the squares reachable moving in the specified direction.
     */
    public List<Square> getSquaresInLine(Square s, String direction){

        ArrayList<Square> squaresInLine = new ArrayList<>();
        int sr = s.getRow();
        int sc = s.getColumn();
        Iterator<Square> squareIt = map.iterator();
        while (squareIt.hasNext()) {
            Square s1 = squareIt.next();
            int r = s1.getRow();
            int c = s1.getColumn();
            if ( (direction.equals("top")   && inLineTop  (sr, sc, r, c))    ||
                 (direction.equals("down")  && inLineDown (sr, sc, r, c))    ||
                 (direction.equals("left")  && inLineLeft (sr, sc, r, c))    ||
                 (direction.equals("right") && inLineRight(sr, sc, r, c))     )
            {
                squaresInLine.add(s1);
            }
        }
        return squaresInLine;

    }


    /**
     * Returns the squares reachable moving in a specified direction from the starting square, ignoring walls.
     * The starting square is excluded.
     *
     * @param s                 the starting square.
     * @param direction         the movement direction.
     * @return                  the squares reachable moving in the specified direction, ignoring walls.
     */
    public List<Square> getSquaresInLineIgnoringWalls(Square s, String direction){

        ArrayList<Square> squaresInLineThroughWall = new ArrayList<>();
        Iterator<Square> squareIt = map.iterator();
        int sr = s.getRow();
        int sc = s.getColumn();
        while (squareIt.hasNext()) {
            Square s1 = squareIt.next();
            int r = s1.getRow();
            int c = s1.getColumn();
            if ((direction.equals("top") && r < sr && c == sc) || (direction.equals("down") && r > sr && c == sc) ||
            (direction.equals("right") && r == sr && c > sc) || (direction.equals("left") && r == sr && c < sc)){
                squaresInLineThroughWall.add(s1);
            }
        }
        return squaresInLineThroughWall;

    }


    /**
     * Returns the distance, in steps, form a square to another.
     *
     * @param start         the starting square.
     * @param dest          the destination square.
     * @return              the distance in steps.
     */
    public int getDistance(Square start, Square dest) {

        List<List<Square>> levels = new ArrayList<>(6);

        levels.add(new ArrayList<>());
        levels.get(0).add(start);
        levels.add(getAdjacent(start));
        levels.add(new ArrayList<>());
        levels.add(new ArrayList<>());
        levels.add(new ArrayList<>());
        levels.add(new ArrayList<>());

        for (int i = 2; i < 6; i++) {
            Iterator<Square> levelIt = levels.get(i - 1).iterator();
            while (levelIt.hasNext()) {
                Square lev = levelIt.next();
                Iterator<Square> levelAdjIt = getAdjacent(lev).iterator();
                while (levelAdjIt.hasNext()) {
                    Square levAdj = levelAdjIt.next();
                    boolean alreadyReached = false;
                    int j = i;
                    while (j > 0 && !alreadyReached) {
                        if (levels.get(j).contains(levAdj)) {
                            alreadyReached = true;
                        }
                        j--;
                    }
                    if (!alreadyReached) {
                        levels.get(i).add(levAdj);
                    }
                }
            }
        }
        for (List<Square> lev: levels) {
            if (lev.contains(dest)){
                return levels.indexOf(lev);
            }
        }
        return -1;

    }


    /**
     * Returns whether two squares are in line moving up from the starting square.
     *
     * @param startRow                the row of the starting square.
     * @param startCol                the column of the starting square.
     * @param destRow                 the row of the destination square.
     * @param destCol                 the column of the destination square.
     * @return                        true if the squares are in line.
     *                                false otherwise.
     */
    private boolean inLineTop(int startRow, int startCol, int destRow, int destCol){

        return ( destCol == startCol && (destRow == startRow - 1 && !topWall[startRow-1][startCol-1]||
                destRow == startRow - 2 && !topWall[startRow-1][startCol-1] && !topWall[startRow-1-1][startCol-1]));

    }

    /**
     * Returns whether two squares are in line moving down from the starting square.
     *
     * @param startRow                the row of the starting square.
     * @param startCol                the column of the starting square.
     * @param destRow                 the row of the destination square.
     * @param destCol                 the column of the destination square.
     * @return                        true if the squares are in line.
     *                                false otherwise.
     */
    private boolean inLineDown(int startRow, int startCol, int destRow, int destCol){

        return ( destCol == startCol && (destRow == startRow + 1 && !topWall[destRow-1][destCol-1] ||
                destRow == startRow + 2 && !topWall[destRow-1][destCol-1] && !topWall[destRow-1-1][startCol-1]));

    }

    /**
     * Returns whether two squares are in line moving left from the starting square.
     *
     * @param startRow                the row of the starting square.
     * @param startCol                the column of the starting square.
     * @param destRow                 the row of the destination square.
     * @param destCol                 the column of the destination square.
     * @return                        true if the squares are in line.
     *                                false otherwise.
     */
    private boolean inLineLeft(int startRow, int startCol, int destRow, int destCol){

        return ( destRow == startRow && (destCol == startCol - 1 && !leftWall[destRow-1][startCol-1] ||
                destCol ==startCol - 2 && !leftWall[destRow-1][startCol-1] && !leftWall[destRow-1][startCol-1-1] ||
                destCol == startCol - 3 && !leftWall[destRow-1][startCol-1] && !leftWall[destRow-1][startCol-1-1]
                        && !leftWall[destRow-1][startCol-2-1] ));

    }

    /**
     * Returns whether two squares are in line moving right from the starting square.
     *
     * @param startRow                the row of the starting square.
     * @param startCol                the column of the starting square.
     * @param destRow                 the row of the destination square.
     * @param destCol                 the column of the destination square.
     * @return                        true if the squares are in line.
     *                                false otherwise.
     */
    private boolean inLineRight(int startRow, int startCol, int destRow, int destCol){

        return ( destRow == startRow && (destCol == startCol + 1 && !leftWall[destRow-1][destCol-1] ||
                destCol == startCol + 2 && !leftWall[destRow-1][destCol-1] && !leftWall[destRow-1][destCol-1-1] ||
                destCol == startCol + 3 && !leftWall[destRow-1][destCol-1] &&
                        !leftWall[destRow-1][destCol-1-1] && !leftWall[destRow-1][destCol-2-1]));

    }


    //TODO
    //implement these

    /**
     * Returns squares in a certain room
     *
     * @param id                      room's id
     * @return                        list of squares in given room
     */
    public List<Square> getSquaresInRoom(int id){
        return new ArrayList<>();
    }


    /**
     * Returns all squares in the map.
     *
     * @return                        a list of all squares
     */
    public List<Square> getAllSquares(){
        return new ArrayList<>();
    }

}