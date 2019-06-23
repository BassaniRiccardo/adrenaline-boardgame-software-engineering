package it.polimi.ingsw.model.board;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.Updater;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;
import it.polimi.ingsw.network.server.VirtualView;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static it.polimi.ingsw.model.cards.Color.*;
import static java.util.Collections.frequency;

/**
 * Represents the game board, made of a map with walls.
 * Every game has a specific game board.
 * Contains the players and information about the players.
 * Contains a deck of weapons, a deck of ammo tiles, a deck of power ups and a kill shot track.
 * Provides information about the relations between the squares in the map.
 *
 * @author  BassaniRiccardo
 */

public class Board {

    public enum Direction {RIGHT, LEFT, UP, DOWN}

    //depend on the map_id, set by BoardConfigurer
    private int id;
    private List<Square> map;
    private boolean[][] leftWalls;
    private boolean[][] topWalls;
    private List<WeaponSquare> spawnPoints;

    //depend on the game settings, set by BoardConfigurer
    private List<Player> players;
    private Player currentPlayer;

    //set by BoardConfigurer
    private Deck weaponDeck;
    private Deck powerUpDeck;
    private Deck ammoDeck;

    //depends on skullsNumber, set by the BoardConfigurer
    private KillShotTrack killShotTrack;

    private List<VirtualView> observers;
    private Map<VirtualView, List<JsonObject>> updates;

    private boolean reset;

    private static final Logger LOGGER = Logger.getLogger("serverLogger");




    /**
     * Constructs a board with no map, walls, players, weapons, nor kill shot track.
     */
    public Board() {

        this.map = new ArrayList<>();
        this.topWalls = null;
        this.leftWalls = null;
        this.spawnPoints = new ArrayList<>();

        this.players = new ArrayList<>();
        this.currentPlayer = null;

        this.weaponDeck = new Deck();
        this.powerUpDeck = new Deck();
        this.ammoDeck = new Deck();

        this.killShotTrack = new KillShotTrack(0, this);

        this.observers = new ArrayList<>();
        this.updates = new HashMap<>();

        this.reset = false;


    }


    /**
     * Getter for map.
     *
     * @return      the map.
     */
    public List<Square> getMap() {
        return map;
    }


    /**
     * Getter for spawnPoints.
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
     * Getter for players.
     *
     * @return      the players on the board.
     */
    public List<Player> getActivePlayers() {
        List<Player> activePlayers = new ArrayList<>();
        for (Player p: players){
            if (p.isInGame()) activePlayers.add(p);
        }
        Collections.sort(activePlayers, (p1, p2) -> {
            if (p1.getId() < p2.getId()) return -1;
            else if (p1.getId() > p2.getId()) return 1;
            return 0;
        });
        return activePlayers;
    }

    /**
     * Getter for playerNumber.
     *
     * @return      the number of players on the board.
     */
    public int getPlayerNumber() {
        return players.size();
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
     * @return      the kill shot track.
     */
    public KillShotTrack getKillShotTrack() throws NotAvailableAttributeException {
        if (killShotTrack == null){
            throw new NotAvailableAttributeException ("Impossible to return the kill shot track: the board has not been initialized yet.");
        }
        return killShotTrack;
    }

    /**
     * Getter for currentPlayer.
     *
     * @return      the current player.
     */
    public Player getCurrentPlayer(){
        return currentPlayer;
    }

    public boolean[][] getLeftWalls() { return leftWalls; }

    public boolean[][] getTopWalls() { return topWalls; }

    public boolean isReset() {return reset; }

    /**
     * Setter for map.
     *
     * @param map   the value to assign to map.
     */
    public void setMap(List<Square> map) {
        if (map.size() < 10 || map.size() > 12){
            throw new IllegalArgumentException ("The map must contain between 10 and 12 squares");
        }
        this.map = map;
    }



    /**
     * Setter for leftWalls.
     *
     * @param leftWalls   the value to assign to leftWalls.
     * @throws          IllegalArgumentException
     */
    public void setLeftWalls(boolean[][] leftWalls) {
        if (leftWalls.length!=3 || leftWalls[0].length !=4 || leftWalls[1].length !=4 || leftWalls[2].length !=4){
            throw new IllegalArgumentException ("The matrix of left walls must be of dimension 3x4");
        }
        this.leftWalls = leftWalls;
    }

    /**
     * Setter for topWalls.
     *
     * @param topWalls   the value to assign to topWalls.
     * @throws          IllegalArgumentException
     */
    public void setTopWalls(boolean[][] topWalls) {
        if (topWalls.length!=3 || topWalls[0].length !=4 || topWalls[1].length !=4 || topWalls[2].length !=4){
            throw new IllegalArgumentException ("The matrix of top walls must be of dimension 3x4");
        }
        this.topWalls = topWalls;
    }

    /**
     * Setter for spawnPoints.
     *
     * @param spawnPoints       the value to assign to spawnPoints.
     */
    public void setSpawnPoints(List<WeaponSquare> spawnPoints) {
        if (spawnPoints.size() != 3){
            throw new IllegalArgumentException ("There must be three spawn points");
        }
        if (spawnPoints.get(0).getColor() != RED && spawnPoints.get(0).getColor() != BLUE && spawnPoints.get(0).getColor() != YELLOW ||
            spawnPoints.get(1).getColor() != RED && spawnPoints.get(1).getColor() != BLUE && spawnPoints.get(1).getColor() != YELLOW ||
            spawnPoints.get(2).getColor() != RED && spawnPoints.get(2).getColor() != BLUE && spawnPoints.get(2).getColor() != YELLOW ){
             throw new IllegalArgumentException("The spawn points must be red, blue or yellow");
        }
        if (spawnPoints.get(0).getColor()==spawnPoints.get(1).getColor() ||
            spawnPoints.get(0).getColor()==spawnPoints.get(2).getColor() ||
            spawnPoints.get(1).getColor()==spawnPoints.get(2).getColor()){
            throw new IllegalArgumentException ("The spawn points must be of distinct colors");

        }
        if (spawnPoints.get(0).getRoomId()==spawnPoints.get(1).getRoomId() ||
                spawnPoints.get(0).getRoomId()==spawnPoints.get(2).getRoomId() ||
                spawnPoints.get(1).getRoomId()==spawnPoints.get(2).getRoomId()){
            throw new IllegalArgumentException ("The spawn points must be in distinct rooms");

        }
        this.spawnPoints = spawnPoints;
    }

    /**
     * Setter for players.
     *
     * @param players   the value to assign to players.
     */
    public void setPlayers(List<Player> players) {

        if (players.size() < 3 || players.size() > 5){
            throw new IllegalArgumentException ("The number of players must be between 3 and 5");
        }
        this.players = players;
    }

    /**
     * Setter for weaponDeck.
     *
     * @param weaponDeck   the value to assign to weaponDeck.
     */
    public void setWeaponDeck(Deck weaponDeck) {
        if (weaponDeck.getDrawable().size() != 21 || !weaponDeck.getDiscarded().isEmpty()){
            throw new IllegalArgumentException ("The weapon deck must contain 21 drawable cards and 0 discards at the beginning of the game");
        }
        this.weaponDeck = weaponDeck;
    }

    /**
     * Setter for powerUpDeck.
     *
     * @param powerUpDeck   the value to assign to powerUpDeck.
     */
    public void setPowerUpDeck(Deck powerUpDeck) {
        if (powerUpDeck.getDrawable().size() != 24 || !powerUpDeck.getDiscarded().isEmpty()){
            throw new IllegalArgumentException ("The power up deck must contain 24 drawable cards and 0 discards at the beginning of the game");
        }
        this.powerUpDeck = powerUpDeck;
    }

    /**
     * Setter for ammoDeck.
     *
     * @param ammoDeck   the value to assign to ammoDeck.
     */
    public void setAmmoDeck(Deck ammoDeck) {
        if (ammoDeck.getDrawable().size() != 36 || !ammoDeck.getDiscarded().isEmpty()){
            throw new IllegalArgumentException ("The ammo tile deck must contain 36 drawable cards and 0 discards at the beginning of the game");
        }
        this.ammoDeck = ammoDeck;
    }

    /**
     * Setter for killShotTrack.
     *
     * @param killShotTrack   the value to assign to killShotTrack.
     * @throws                IllegalArgumentException
     */
    public void setKillShotTrack(KillShotTrack killShotTrack) {
        if (killShotTrack.getSkullsLeft() < 5 || killShotTrack.getSkullsLeft() > 8){
            throw new IllegalArgumentException ("The number of skulls on the kill shot track must be between 5 and 8");
        }
        this.killShotTrack = killShotTrack;
    }

    /**
     * Setter for killShotTrack.
     *
     * @param currentPlayer   the value to assign to killShotTrack.
     */
    public void setCurrentPlayer(Player currentPlayer){this.currentPlayer= currentPlayer;}

    public void setReset(boolean reset) {this.reset = reset; }

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
            if     (r == sr - 1 && c == sc && !topWalls[sr-1][sc-1]    ||            //top
                    r == sr + 1 && c == sc && !topWalls[r-1][c-1]  ||                //down
                    r == sr && c == sc + 1 && !leftWalls[r-1][c-1]   ||              //right
                    r == sr && c == sc - 1 && !leftWalls[sr-1][sc-1]) {              //left
                adjacent.add(s1);
            }
        }
        return adjacent;

    }


    /**
     * Returns the squares reachable from the starting square with the specified number of steps.
     * The starting square is included.
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
            if (getDistance(s,s1) <= steps) {
                reachable.add(s1);
            }
        }
        return reachable;

    }


    /**
     * Returns the square visible from the starting square.
     * The starting square is included.
     *
     * @param s             the starting square.
     * @return              the visible squares.
     */
    public List<Square> getVisible (Square s) {

        List<Square> visible = new ArrayList<>();
        for (Square s1 : map) {
            if (getSquaresInRoom(s.getRoomId()).contains(s1)) {
                visible.add(s1);
            } else {
                for (Square adj : getAdjacent(s)) {
                    if (getSquaresInRoom(s1.getRoomId()).contains(adj) || s1.getId() == adj.getId()) {
                        visible.add(s1);
                    }
                }
            }
        }
        return visible;

    }


    /**
     * Returns the squares in the specified room.
     *
     * @param roomId    the id of the room.
     * @return          the squares in the room.
     * @throws          IllegalArgumentException
     */
    public List<Square> getSquaresInRoom (int roomId){

        if ( roomId < 1 ||this.getMap().size() - roomId < 6) {
            throw new IllegalArgumentException("This map does not contain so many rooms");
        }
        List<Square> inRoom = new ArrayList<>();
        Iterator<Square> squareIt = map.iterator();
        while (squareIt.hasNext()) {
            Square s1 = squareIt.next();
            if (s1.getRoomId() == roomId) {
                inRoom.add(s1);
            }
        }
        return inRoom;

    }


    /**
     * Returns the squares reachable moving or shooting in a specified direction from the starting square.
     * The starting square is excluded.
     *
     * @param s                 the starting square.
     * @param direction         the direction.
     * @return                  the squares reachable moving in the specified direction.
     */
    public List<Square> getSquaresInLine(Square s, Direction direction){

        ArrayList<Square> squaresInLine = new ArrayList<>();
        int sr = s.getRow();
        int sc = s.getColumn();
        Iterator<Square> squareIt = map.iterator();
        while (squareIt.hasNext()) {
            Square s1 = squareIt.next();
            int r = s1.getRow();
            int c = s1.getColumn();
            if ( (direction==Direction.UP   && inLineTop  (sr, sc, r, c))    ||
                 (direction==Direction.DOWN  && inLineDown (sr, sc, r, c))    ||
                 (direction==Direction.LEFT && inLineLeft (sr, sc, r, c))    ||
                 (direction==Direction.RIGHT && inLineRight(sr, sc, r, c))     )
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
    public List<Square> getSquaresInLineIgnoringWalls(Square s, Direction direction){

        ArrayList<Square> squaresInLineThroughWall = new ArrayList<>();
        Iterator<Square> squareIt = map.iterator();
        int sr = s.getRow();
        int sc = s.getColumn();
        while (squareIt.hasNext()) {
            Square s1 = squareIt.next();
            int r = s1.getRow();
            int c = s1.getColumn();
            if ((direction==Direction.UP && r < sr && c == sc) || (direction==Direction.DOWN && r > sr && c == sc) ||
            (direction==Direction.RIGHT && r == sr && c > sc) || (direction==Direction.LEFT && r == sr && c < sc)){
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
     * @throws              IllegalArgumentException
     */
    public int getDistance(Square start, Square dest) {

        if (!this.getMap().contains(start) || !this.getMap().contains(dest)){
            throw new IllegalArgumentException("The squares must belong to the board.");
        }

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

        return ( destCol == startCol && (destRow == startRow - 1 && !topWalls[startRow-1][startCol-1]||
                destRow == startRow - 2 && !topWalls[startRow-1][startCol-1] && !topWalls[startRow-1-1][startCol-1]));

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

        return ( destCol == startCol && (destRow == startRow + 1 && !topWalls[destRow-1][destCol-1] ||
                destRow == startRow + 2 && !topWalls[destRow-1][destCol-1] && !topWalls[destRow-1-1][startCol-1]));

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

        return ( destRow == startRow && (destCol == startCol - 1 && !leftWalls[destRow-1][startCol-1] ||
                destCol ==startCol - 2 && !leftWalls[destRow-1][startCol-1] && !leftWalls[destRow-1][startCol-1-1] ||
                destCol == startCol - 3 && !leftWalls[destRow-1][startCol-1] && !leftWalls[destRow-1][startCol-1-1]
                        && !leftWalls[destRow-1][startCol-2-1] ));

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
    private boolean inLineRight(int startRow, int startCol, int destRow, int destCol) {

        return (destRow == startRow && (destCol == startCol + 1 && !leftWalls[destRow - 1][destCol - 1] ||
                destCol == startCol + 2 && !leftWalls[destRow - 1][destCol - 1] && !leftWalls[destRow - 1][destCol - 1 - 1] ||
                destCol == startCol + 3 && !leftWalls[destRow - 1][destCol - 1] &&
                        !leftWalls[destRow - 1][destCol - 1 - 1] && !leftWalls[destRow - 1][destCol - 2 - 1]));
    }


    /**
     * Sorts a list of players depending on the occurrences in a specified list of players.
     * Used by both Player and KillShotTrack
     *
     * @param playersToSort
     * @param occurrences
     */
    public void sort(List<Player> playersToSort, List<Player> occurrences){
        Collections.sort(playersToSort, (p1,p2) -> {
            if (frequency(occurrences, p1) > frequency(occurrences, p2)) return -1;
            else if (frequency(occurrences, p1) < frequency(occurrences, p2)) return 1;
            else {
                if (occurrences.indexOf(p1) < occurrences.indexOf(p2)) return -1;
                else if (occurrences.indexOf(p1) > occurrences.indexOf(p2)) return 1;
                return 0;

            }
        });
    }

    public List<AmmoSquare> getAmmoSquares(){

        List<AmmoSquare> l = new ArrayList<>();
        for(Square s : map){
            if(!getSpawnPoints().contains(s)){
                l.add((AmmoSquare)s);
            }
        }
        return l;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId(){
        return id;
    }

    /**
     * Registers VirtualView as an observer
     * @param p     the registering VirtualView
     */
    public void registerObserver(VirtualView p){
        LOGGER.log(Level.INFO, "{0} registered as an observer to Board.", p);
        observers.add(p);
        updates.put(p, new ArrayList<>());
    }

    /**
     * Removes VirtualView p from list of observers
     * @param p     the VirtualView to be removed
     */
    public void removeObserver(VirtualView p){
        observers.remove(p);
    }

    /**
     * Notifies all observers with model update messages
     */
    public void notifyObservers(){
        for(VirtualView p : observers){
            notifyObserver(p);
        }
    }

    /**
     * Notifies only VirtualView p
     * @param p     the VirtualView to be notified
     */
    public void notifyObserver(VirtualView p){
        LOGGER.log(Level.INFO, "Notifying observer {0}", p);
        for(JsonObject update : updates.get(p)) {
            p.update(update);
        }
        updates.get(p).clear();
    }

    /**
     * Adds a single update to all update queues.
     * @param jsonObject    the single update
     */
    public void addToUpdateQueue(JsonObject jsonObject){

        LOGGER.log(Level.INFO, "Adding an update to all queues: {0}", jsonObject.toString());
        System.out.println("Adding to all: " + jsonObject.toString());
        for(VirtualView v : updates.keySet()){
            updates.get(v).add(jsonObject);
        }
    }

    /**
     * Adds a single update to a single update queue
     * @param jsonObject    the single update
     * @param v             the VirtualView meant to receive the update
     */
    public void addToUpdateQueue(JsonObject jsonObject, VirtualView v){
        LOGGER.log(Level.INFO, "Adding an update to a single queues");
        System.out.println(v + " Adding update " + jsonObject.toString());
        updates.get(v).add(jsonObject);
    }

    /**
     * Removes updates to other players in case the current player reverts an action
     * @param v     the current player
     */
    public void revertUpdates(VirtualView v){
        for(VirtualView other : updates.keySet()){
            if(!other.equals(v)){
                updates.get(other).clear();
                LOGGER.log(Level.INFO, "Removed all updates outgoing to {0}", other.getName());
            }
        }
    }

}