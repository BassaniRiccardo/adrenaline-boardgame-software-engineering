package it.polimi.ingsw.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.model.Color.*;
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
    public void getPlayersInside() throws NoMoreCardsException, UnacceptableItemNumberException, NotAvailableAttributeException {

        //declares the array the method is expected to return
        ArrayList<Player> expected = new ArrayList<>();

        //simulates a scenario
        BoardConfigurer.getInstance().simulateScenario();

        //selects two player and a square; p1 is already in the square
        Player p1 = Board.getInstance().getPlayers().get(0);
        Player p2 = Board.getInstance().getPlayers().get(1);
        AmmoSquare ammoSquare = (AmmoSquare)Board.getInstance().getMap().get(0);

        //checks that p1 is in the square
        expected.add(p1);
        assertEquals(expected, Board.getInstance().getPlayersInside(Board.getInstance().getMap().get(0)));

        //adds p2 to the square
        p2.setPosition(ammoSquare);
        expected.add(p2);

        //checks that the square contains p1 the added players, p2
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
     * Tests the method getVisible(), covering all the instructions.
     */
    @Test
    public void getVisible() {

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
        expected.add(Board.getInstance().getMap().get(5));      //map[2][3]
        //squares in the same room of one of the adjacent squares: yellow room
        expected.add(Board.getInstance().getMap().get(6));      //map[2][4]
        expected.add(Board.getInstance().getMap().get(9));      //map[3][4]

        assertEquals(expected, Board.getInstance().getVisible(Board.getInstance().getMap().get(5)));
    }

    /**
     * Tests the method getInRoom(), covering all the instructions.
     */
    @Test
    public void getSquaresInRoom() {

        //declares the array the method is expected to return
        ArrayList<Square> expected = new ArrayList<>();

        //map.get(5) == map[2][3], red room: all instructions covered

        // squares in the same room: red room
        expected.add(Board.getInstance().getMap().get(3));      //map[2][1]
        expected.add(Board.getInstance().getMap().get(4));      //map[2][2]
        expected.add(Board.getInstance().getMap().get(5));      //map[2][3]

        assertEquals(expected, Board.getInstance().getSquaresInRoom(2));
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

    /**
     * Tests the method getDistance(), when an exception should be thrown since a square does not belong to the map.
     */
    @Test(expected = IllegalArgumentException.class)
    public void getDistanceBadArguments() {

        AmmoSquare externalSquare = new AmmoSquare(15,2,3,4, RED);
        int dist = Board.getInstance().getDistance(Board.getInstance().getMap().get(0), externalSquare);

    }

    /**
     * Tests the method setLeftWalls(), when a bad parameter is entered.
     */
    @Test(expected = IllegalArgumentException.class)
    public void setLeftWallsBadArgument() {

        boolean[][] walls = {{true,true,true,true},{false,false,false,false}};
        Board.getInstance().setLeftWalls(walls);
    }

    /**
     * Tests the method setTopWalls(), when a bad parameter is entered.
     */
    @Test(expected = IllegalArgumentException.class)
    public void setTopWallsBadArgument() {

        boolean[][] walls = {{true,true,true,true},{false,false,false,false}, {false, true, false,false,false}};
        Board.getInstance().setTopWalls(walls);
    }


    /**
     * Tests the method setSpawnPoints(), when a bad parameter is entered: less than three swap points.
     */
    @Test(expected = IllegalArgumentException.class)
    public void setSpawnPointsOnlyOne() {

        List spawnPoints = new ArrayList<>();
        spawnPoints.add(new WeaponSquare(1,1,1,1,RED));
        Board.getInstance().setSpawnPoints(spawnPoints);
    }

    /**
     * Tests the method setSpawnPoints(), when a bad parameter is entered: a color different from red, blue, green.
     */
    @Test(expected = IllegalArgumentException.class)
    public void setSpawnPointsWrongColors() {

        List spawnPoints = new ArrayList<>();
        spawnPoints.add(new WeaponSquare(1,1,1,1,RED));
        spawnPoints.add(new WeaponSquare(2,2,2,2,GREEN));
        spawnPoints.add(new WeaponSquare(3,3,3,3,BLUE));
        Board.getInstance().setSpawnPoints(spawnPoints);
    }

    /**
     * Tests the method setSpawnPoints(), when a bad parameter is entered: two swap points have the same color.
     */
    @Test(expected = IllegalArgumentException.class)
    public void setSpawnPointsNotDistinctColors() {

        List spawnPoints = new ArrayList<>();
        spawnPoints.add(new WeaponSquare(1,1,1,1,RED));
        spawnPoints.add(new WeaponSquare(2,2,2,2,BLUE));
        spawnPoints.add(new WeaponSquare(3,3,3,3,BLUE));
        Board.getInstance().setSpawnPoints(spawnPoints);
    }

    /**
     * Tests the method setSpawnPoints(), when a bad parameter is entered: two swap points are in the same room.
     */
    @Test(expected = IllegalArgumentException.class)
    public void setSpawnPointsNotDistinctRooms() {

        List spawnPoints = new ArrayList<>();
        spawnPoints.add(new WeaponSquare(1,1,1,1,RED));
        spawnPoints.add(new WeaponSquare(2,2,2,2,BLUE));
        spawnPoints.add(new WeaponSquare(3,2,3,3,YELLOW));
        Board.getInstance().setSpawnPoints(spawnPoints);
    }

    /**
     * Tests the method setPlayers(), when a bad parameter is entered: player number not between 3 and 5.
     */
    @Test(expected = IllegalArgumentException.class)
    public void setPlayersWrongNumber() {

        List players = new ArrayList<>();
        players.add(new Player(1, Player.HeroName.BANSHEE));
        Board.getInstance().setPlayers(players);
    }

    /**
     * Tests the method setWeaponDeck(), when a bad parameter is entered: not 21 drawable cards.
     */
    @Test(expected = IllegalArgumentException.class)
    public void setWeaponDeckWrongNumberOfCards() {

        Deck weaponDeck = new Deck();
        weaponDeck.addCard(WeaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE));
        Board.getInstance().setWeaponDeck(weaponDeck);
    }

    /**
     * Tests the method setPowerUpDeck(), when a bad parameter is entered: not 24 drawable cards.
     */
    @Test(expected = IllegalArgumentException.class)
    public void setPowerUpDeckWrongNumberOfCards() {

        Deck powerUpDeck = new Deck();
        for (int i = 0; i< 50; i++) powerUpDeck.addCard(PowerUpFactory.createPowerUp(PowerUp.PowerUpName.TARGETING_SCOPE, RED));
        Board.getInstance().setPowerUpDeck(powerUpDeck);
    }

    /**
     * Tests the method setAmmoDeck(), when a bad parameter is entered: not 0 discarded cards.
     */
    @Test(expected = IllegalArgumentException.class)
    public void setAmmoDeckNotEmptyDiscards() {

        Deck ammoDeck = new Deck();
        for (int i = 0; i < 36; i++) ammoDeck.addCard(new AmmoTile(false, new AmmoPack(0,1,2)));
        ammoDeck.addDiscardedCard(new AmmoTile(true,new AmmoPack(1,0,2)));
        Board.getInstance().setWeaponDeck(ammoDeck);
    }

    /**
     * Tests the method setKillShotTrack(), when a bad parameter is entered: less than 5 skulls.
     */
    @Test(expected = IllegalArgumentException.class)
    public void setKillShotTrack() {

        Board.getInstance().setKillShotTrack(new KillShotTrack(3));
    }

    /**
     * Tests the method setMap(), when a bad parameter is entered: less than 10 squares.
     */
    @Test(expected = IllegalArgumentException.class)
    public void setMap() {

        List<Square> map = new ArrayList<>();
        map.add(new AmmoSquare(1,1,1,1,GREEN));
        Board.getInstance().setMap(map);
    }

}