package it.polimi.ingsw.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.model.Player.HeroName.*;
import static org.junit.Assert.*;

/**
 * Tests all methods of the class Board, covering all the instructions.
 * The position of the squares in the map is shown using the notation map[row][column]
 * for a better readability.
 */

public class BoardTest {


    /**
     * Constructs the map.
     */
    @Before
    public void setup(){

        //creates the board and configures the map
        BoardConfigurer.getInstance().configureMap(1);

    }


    /**
     * Tests the method getPlayerInside(), covering all the instructions.
     */
    @Test
    public void getPlayersInside() {

        //declares the array the method is expected to return
        ArrayList<Player> expected = new ArrayList<>();

        //adds expected players
        Player p1 = new Player(1,BANSHEE);
        Player p2 = new Player(2, DOZER);
        expected.add(p1);
        expected.add(p2);

        //adds players to the square
        Board.getInstance().getMap().get(0).addPlayer(p1);
        Board.getInstance().getMap().get(0).addPlayer(p2);

        //checks that the square contains the added players
        assertEquals(expected, Board.getInstance().getPlayersInside(Board.getInstance().getMap().get(0)));

        //remove all the players from the square
        Board.getInstance().getMap().get(0).removePlayer(p1);
        Board.getInstance().getMap().get(0).removePlayer(p2);

        //checks that the square does not contain the removed players
        assertTrue(Board.getInstance().getPlayersInside(Board.getInstance().getMap().get(0)).isEmpty());

    }


    /**
     * Tests the method getAdjacentTest(), covering all the instructions.
     */
    @Test
    public void getAdjacent() {

        //declares the array the method is expected to return
        ArrayList<Square> expected = new ArrayList<>();

        //map.get(1) == map[1][2]: if right, if left covered
        expected.add(Board.getInstance().getMap().get(0));      //map[1][1]
        expected.add(Board.getInstance().getMap().get(2));      //map[1][3]
        assertEquals(expected, Board.getInstance().getAdjacent(Board.getInstance().getMap().get(1)));
        expected.clear();

        //map.get(3) == map[2][1]: if top covered
        expected.add(Board.getInstance().getMap().get(0));     //map[1][1]
        expected.add(Board.getInstance().getMap().get(4));     //map[2][2]
        assertEquals(expected, Board.getInstance().getAdjacent(Board.getInstance().getMap().get(3)));
        expected.clear();

        //map.get(4) == map[2][2]: if down covered
        expected.add(Board.getInstance().getMap().get(3));     //map[2[1]
        expected.add(Board.getInstance().getMap().get(5));     //map[2][3]
        expected.add(Board.getInstance().getMap().get(7));     //map[3][2]
        assertEquals(expected, Board.getInstance().getAdjacent(Board.getInstance().getMap().get(4)));
        expected.clear();

        //explicitly shows that walls are considered, since map[2][2] is not adjacent to map[1][2]
        assertFalse(Board.getInstance().getAdjacent(Board.getInstance().getMap().get(4)).contains(Board.getInstance().getMap().get(1)));

    }


    /**
     * Tests the method getReachable(), covering all the instructions.
     */
    @Test
    public void getReachable() {

        //declares the array the method is expected to return
        ArrayList<Square> expected = new ArrayList<>();

        //map.get(0): map[1][1]: all instructions covered
        expected.add(Board.getInstance().getMap().get(1));      //map[1][2]
        expected.add(Board.getInstance().getMap().get(2));      //map[1][3]
        expected.add(Board.getInstance().getMap().get(3));      //map[2][1]
        expected.add(Board.getInstance().getMap().get(4));      //map[2][2]
        expected.add(Board.getInstance().getMap().get(5));      //map[2][3]
        expected.add(Board.getInstance().getMap().get(7));      //map[3][2]
        assertEquals(expected, Board.getInstance().getReachable(Board.getInstance().getMap().get(0), 3));
    }


    /**
     * Tests the method getVisibleFrom(), covering all the instructions.
     */
    @Test
    public void getVisibleFrom() {

        //declares the array the method is expected to return
        ArrayList<Square> expected = new ArrayList<>();

        //map.get(5) == map[2][3], red room: all instructions covered

        //squares in the same room of one of the adjacent squares: blue room
        expected.add(Board.getInstance().getMap().get(0));      //map[1][1]
        expected.add(Board.getInstance().getMap().get(1));      //map[1][2]
        expected.add(Board.getInstance().getMap().get(2));      //map[1][3]
        // squares in the same room: red room
        expected.add(Board.getInstance().getMap().get(3));      //map[2][1]
        expected.add(Board.getInstance().getMap().get(4));      //map[2][2]
        //squares in the same room of one of the adjacent squares: yellow room
        expected.add(Board.getInstance().getMap().get(6));      //map[2][4]
        expected.add(Board.getInstance().getMap().get(9));      //map[3][4]

        assertEquals(expected, Board.getInstance().getVisible(Board.getInstance().getMap().get(5)));
    }


    /**
     * Tests the method getSquaresInSameRoom(), covering all the instructions.
     */
    @Test
    public void getSquaresInSameRoom() {

        //declares the array the method is expected to return
        ArrayList<Square> expected = new ArrayList<>();

        //map.get(0) == map[1][1], blue room: all instructions covered
        expected.add(Board.getInstance().getMap().get(1));      //map[1][2]
        expected.add(Board.getInstance().getMap().get(2));      //map[1][3]
        assertEquals(expected, Board.getInstance().getSquaresInSameRoom(Board.getInstance().getMap().get(0)));
    }


    /**
     * Tests the method getSquaresInLine(), in the event an empty list should be returned.
     * The square map{2}[2] has a wall in the top direction.
     */
    @Test
    public void getSquaresInLineEmpty() {

        //map.get(4): map[2][2]
        assertTrue(Board.getInstance().getSquaresInLine(Board.getInstance().getMap().get(4), "top").isEmpty());
    }


    /**
     * Tests the method getSquaresInLine(), covering all the directions, therefore all the instructions.
     */
    @Test
    public void getSquaresInLineCoveringAllDirections() {

        //declares the array the method is expected to return
        ArrayList<Square> expected = new ArrayList<>();

        // map.get(4): map[2][2]

        //down
        expected.add(Board.getInstance().getMap().get(7));      //map[3][2]
        assertEquals(expected, Board.getInstance().getSquaresInLine(Board.getInstance().getMap().get(4), "down"));
        expected.clear();

        //left
        expected.add(Board.getInstance().getMap().get(3));      //map[2][1]
        assertEquals(expected, Board.getInstance().getSquaresInLine(Board.getInstance().getMap().get(4), "left"));
        expected.clear();

        //right
        expected.add(Board.getInstance().getMap().get(5));      //map[2][3]
        expected.add(Board.getInstance().getMap().get(6));      //map[2][4]
        assertEquals(expected, Board.getInstance().getSquaresInLine(Board.getInstance().getMap().get(4), "right"));
        expected.clear();

        //map.get(3): map[2][1]

        //top
        expected.add(Board.getInstance().getMap().get(0));      //map[2][3]
        assertEquals(expected, Board.getInstance().getSquaresInLine(Board.getInstance().getMap().get(3), "top"));
    }


    /**
     * Tests the method getSquaresInLineIgnoringWalls(), in the event an empty list should be returned.
     * The square map[1][1] is in the top-left corner, therefore there are no squares moving in the top direction.
     */
    @Test
    public void getSquaresInLineIgnoringWallsEmpty() {

        //map.get(0): map[1][1]
        assertTrue(Board.getInstance().getSquaresInLineIgnoringWalls(Board.getInstance().getMap().get(0), "top").isEmpty());

    }


    /**
     * Tests the method getSquaresInLineIgnoringWalls(), covering all the directions, therefore all the instructions.
     */
    @Test
    public void getSquaresInLineIgnoringWallsCoveringAllDirections() {

        //declares the array the method is expected to return
        List<Square> expected = new ArrayList<>();

        //map.get(5): map[2][3]

        //top
        expected.add(Board.getInstance().getMap().get(2));      //map[1][3]
        assertEquals(expected, Board.getInstance().getSquaresInLineIgnoringWalls(Board.getInstance().getMap().get(5), "top"));
        expected.clear();

        //down
        expected.add(Board.getInstance().getMap().get(8));      //map[3][2]
        assertEquals(expected, Board.getInstance().getSquaresInLineIgnoringWalls(Board.getInstance().getMap().get(5), "down"));
        expected.clear();

        //left
        expected.add(Board.getInstance().getMap().get(3));      //map[2][1]
        expected.add(Board.getInstance().getMap().get(4));      //map[2][2]
        assertEquals(expected, Board.getInstance().getSquaresInLineIgnoringWalls(Board.getInstance().getMap().get(5), "left"));
        expected.clear();

        //right
        expected.add(Board.getInstance().getMap().get(6));      //map[2][4]
        assertEquals(expected, Board.getInstance().getSquaresInLineIgnoringWalls(Board.getInstance().getMap().get(5), "right"));
    }


    /**
     * Tests the method getDistance(), when the arguments are the same square.
     */
    @Test
    public void getDistanceSameSquare() {

        assertEquals(0, Board.getInstance().getDistance(Board.getInstance().getMap().get(6), Board.getInstance().getMap().get(6)));

    }


    /**
     * Tests the method getDistance(), when the arguments are adjacent squares.
     */
    @Test
    public void getDistanceAdjacentSquares() {

        assertEquals(1, Board.getInstance().getDistance(Board.getInstance().getMap().get(0), Board.getInstance().getMap().get(1) ));

    }


    /**
     * Tests the method getDistance(), when the arguments are squares divided by a wall.
     */
    @Test
    public void getDistanceSquaresDividedByWall() {

        assertEquals(3, Board.getInstance().getDistance(Board.getInstance().getMap().get(1), Board.getInstance().getMap().get(4) ));

    }


    /**
     * Tests the method getDistance(), when the arguments are squares located in opposite corners of the map.
     */
    @Test
    public void getDistanceOppositeSquares() {

        assertEquals(5, Board.getInstance().getDistance(Board.getInstance().getMap().get(0), Board.getInstance().getMap().get(9) ));

    }


    /**
     * Tests the method getDistance(), when the arguments are squares on the same row, at the opposite side of the map.
     */
    @Test
    public void getDistanceFourSquaresInLine() {

        assertEquals(3, Board.getInstance().getDistance(Board.getInstance().getMap().get(3), Board.getInstance().getMap().get(6) ));

    }

    /**
     * Tests the method getDistance(), covering the left distance values.
     */
    @Test
    public void getDistanceCoveringAllDistances() {

        assertEquals(2, Board.getInstance().getDistance(Board.getInstance().getMap().get(0), Board.getInstance().getMap().get(2) ));
        assertEquals(4, Board.getInstance().getDistance(Board.getInstance().getMap().get(1), Board.getInstance().getMap().get(7) ));

    }


}