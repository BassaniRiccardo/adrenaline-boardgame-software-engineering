package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Color;
import it.polimi.ingsw.model.exceptions.NoMoreCardsException;
import it.polimi.ingsw.model.exceptions.UnacceptableItemNumberException;

import java.util.*;

/**
 * Represents an abstract square, that can be instantiated as a WeaponSquare (a square containing weapons)
 * or as an AmmoSquare (a square containing ammo tiles).
 * A square is identified by an id, it is placed in a room and it has a row, a column and a color.
 * It also contains a list of the players located in the square and it allows to addList and remove a player
 * and to check whether a particular player is in the square.
 *
 * @author  BassaniRiccardo
 */

public abstract class Square {

    private final Board board;
    private final int id;
    private final int roomId;
    private final int row;
    private final int column;
    private final Color color;
    private List<Player> players;

    /**
     * Constructor for the abstract class Square.
     * This constructor is never really used but it is invoked by the constructors of Square subclasses.
     *
     * @param board         the board hte square is in.
     * @param id            the square id.
     * @param roomId        the square room id.
     * @param row           the square row.
     * @param column        the square column.
     * @param color         the square color.
     *
     * @throws IllegalFormatCodePointException
     */
    public Square(Board board, int id, int roomId, int row, int column, Color color) {

        if(id<0 || id>11 || roomId<1 || roomId>6 || row<1 || row>4 || column<1 || column>4){
            throw new IllegalArgumentException("Bad parameters for the constructor of Square");
        }

        this.board = board;
        this.id = id;
        this.roomId = roomId;
        this.row = row;
        this.column = column;
        this.color = color;
        this.players = new ArrayList<>();
    }

    /**
     * Getter for id.
     *
     * @return      the id of the square.
     */
    public int getId() {
        return id;
    }

    /**
     * Getter for roomId.
     *
     * @return      the id of the room the square is in.
     */
    public int getRoomId() {
        return roomId;
    }

    /**
     * Getter for row.
     *
     * @return      the row the square.
     */
    public int getRow() {
        return row;
    }

    /**
     * Getter for column.
     *
     * @return      the column of square.
     */
    public int getColumn() {
        return column;
    }

    /**
     * Getter for color.
     *
     * @return      the color of the square.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Getter for players.
     *
     * @return      the players in the square.
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Getter for board.
     *
     * @return      the board the square is in.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Returns true if the square contains the player
     *
     * @param p     the player to consider.
     * @return      true if the square contains the player.
     *              false otherwise.
     */
    public boolean containsPlayer(Player p) {
        return players.contains(p);
    }


    /**
     * Adds a player to the player list of the square.
     *
     * @param p     the player to addList.
     */
    public void addPlayer(Player p) {
//        if (!this.board.getPlayers().contains(p)) throw new IllegalArgumentException("Only players of the Board can be added to the Square");
        this.players.add(p);
    }



    /**
     * Removes a player from the player list of the square.
     *
     * @param p     the player to remove.
     * @throws      IllegalArgumentException
     */
    public void removePlayer(Player p) {

        if (!containsPlayer(p)) throw new IllegalArgumentException("The square does not contain the player whose removal is asked");
        this.players.remove(p);
    }


    /**
     * Removed a card form the square.
     *
     * @param card      the removed card.
     * @return          the removed card.
     * @throws NoMoreCardsException
     */
    public abstract Card removeCard(Card card) throws NoMoreCardsException;

    /**
     * Adds cards from the deck to the square.
     * It is called at the beginning of the game and it adds a ammo tile to the ammo squares and
     * three weapons to the weapon squares.
     *
     * @throws UnacceptableItemNumberException
     * @throws NoMoreCardsException
     */
    public abstract void addAllCards() throws UnacceptableItemNumberException, NoMoreCardsException;


    /**
     * Abstract method which returns true if the square does not contain any item.
     *
     * @return      true if the square does not contain any item.
     *              false otherwise.
     */
    public abstract boolean isEmpty();


    /**
     * Returns true if the two squares have the same id.
     *
     * @param o the square to compare to the current square.
     * @return      true if the two squares have the same id.
     *              false otherwise.
     */
    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        // Checks if o is an instance of Square or not
        if (!(o instanceof Square)) {
            return false;
        }

        // typecast o to Square in order to compare the IDs
        Square s = (Square) o;

        // Compares the IDs and returns accordingly
        return s.getId() == getId();
    }

    /**
     * Returns the hashCode of the square.
     *
     * @return      the hashCode of the square.
     */
    @Override
    public int hashCode() {
        int result = 0;
        result = id;
        return result;
    }

    /**
     * Returns a string representing the square.
     *
     * @return      the description of the square.
     */
    @Override
    public String toString(){
        return "Square " + id;
    }



}