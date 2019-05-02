package it.polimi.ingsw.model;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;

/**
 * Tests all methods of the class KillShotTrack.
 */

public class KillShotTrackTest {


    /**
     * Tests that an exception is thrown in the constructor of KillShotTrack
     * if the number of skulls is not between 5 and 8.
     *
     * @throws IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructorParameters() throws IllegalArgumentException {
        new KillShotTrack(21, new Board());
    }

    /**
     * Tests that an exception is thrown if the decrement of the number of skulls would make it negative.
     *
     * @throws UnacceptableItemNumberException
     */
    @Test(expected = UnacceptableItemNumberException.class)
    public void removeSkulls() throws UnacceptableItemNumberException {
        KillShotTrack killShotTrack = new KillShotTrack(6, new Board());
        killShotTrack.removeSkulls(11);
    }


    /**
     * Tests the method registerKill() in the case of a single kill.
     *
     * @throws  UnacceptableItemNumberException
     *
     */
    @Test
    public void registerStandardKill() throws NoMoreCardsException, UnacceptableItemNumberException, NotAvailableAttributeException, WrongTimeException {

        //initializes the killShotTrack, a killer and a dead
        Board b = BoardConfigurer.getInstance().simulateScenario();
        KillShotTrack killShotTrack =  b.getKillShotTrack();
        Player killer = b.getPlayers().get(0);
        Player dead = b.getPlayers().get(1);

        //registers the standard kill
        dead.sufferDamage(11, killer);
        killShotTrack.registerKill(killer, dead, false);

        //checks that the number of occurrences of the killer in the killers list is incremented by 1
        //and that he is inserted in the correct position
        assertEquals(1, Collections.frequency(killShotTrack.getKillers(), killer));
        assertEquals(killer, killShotTrack.getKillers().get(0));

        //checks that a skull has been removed
        assertEquals(7, killShotTrack.getSkullsLeft());

        //checks that the points given for the death of the dead player have been updated
        assertEquals(6, dead.getPointsToGive());

    }


    /**
     * Tests the method registerKill() until the points given for the next death of the dead player
     * should not be reduced, since their value is 1.
     *
     * @throws  UnacceptableItemNumberException
     */
    @Test
    public void registerMultipleStandardKills() throws NoMoreCardsException, UnacceptableItemNumberException, NotAvailableAttributeException, WrongTimeException{

        //initializes the killShotTrack, a killer and a dead
        Board b = BoardConfigurer.getInstance().simulateScenario();
        KillShotTrack killShotTrack =  b.getKillShotTrack();
        Player killer = b.getPlayers().get(0);
        Player dead = b.getPlayers().get(1);
        //registers 4 kills and checks that the points given for the death of the dead player have been updated
        dead.sufferDamage(11, killer);
        killShotTrack.registerKill(killer, dead, false);
        assertEquals(6, dead.getPointsToGive());
        dead.sufferDamage(11, killer);
        killShotTrack.registerKill(killer, dead, false);
        assertEquals(4, dead.getPointsToGive());
        dead.sufferDamage(11, killer);
        killShotTrack.registerKill(killer, dead, false);
        assertEquals(2, dead.getPointsToGive());
        dead.sufferDamage(11, killer);
        killShotTrack.registerKill(killer, dead, false);
        assertEquals(1, dead.getPointsToGive());

        //register the fifth kill and
        //checks that the points given for the death of the dead player have not been update to 0
        killShotTrack.registerKill(killer, dead, false);
        assertEquals(1, dead.getPointsToGive());

        //checks that the number of occurrences of the killer in the killers list is incremented by 5 and
        //that he is inserted in the correct positions
        assertEquals(5, Collections.frequency(killShotTrack.getKillers(), killer));
        assertEquals(killer, killShotTrack.getKillers().get(0));
        assertEquals(killer, killShotTrack.getKillers().get(1));
        assertEquals(killer, killShotTrack.getKillers().get(2));
        assertEquals(killer, killShotTrack.getKillers().get(3));
        assertEquals(killer, killShotTrack.getKillers().get(4));

        //checks that 5 skulls has been removed
        assertEquals(8-5, killShotTrack.getSkullsLeft());
    }


    /**
     * Tests the method registerKill() in the case of an overkill.
     *
     * @throws UnacceptableItemNumberException
     */
    @Test
    public void registerOverkill() throws NoMoreCardsException, UnacceptableItemNumberException, NotAvailableAttributeException, WrongTimeException{

        //initializes the killShotTrack, a killer and a dead
        Board b = BoardConfigurer.getInstance().simulateScenario();
        KillShotTrack killShotTrack =  b.getKillShotTrack();
        Player killer = b.getPlayers().get(0);
        Player dead = b.getPlayers().get(1);

        //registers the overkill
        dead.sufferDamage(12, killer);
        killShotTrack.registerKill(killer, dead, true);

        //checks that the number of occurrences of the killer in the killers list is incremented by 2
        //and that he is inserted in the correct positions
        assertEquals(2, Collections.frequency(killShotTrack.getKillers(), killer));
        assertEquals(killer, killShotTrack.getKillers().get(0));
        assertEquals(killer, killShotTrack.getKillers().get(1));

        //checks that a skull has been removed
        assertEquals(7, killShotTrack.getSkullsLeft());

        //checks that the awards given for the death of the dead player have been updated
        assertEquals(6, dead.getPointsToGive());


    }


    /**
     * Tests the method registerKill() in the event that there are not skulls left on the track and
     * the kill occurred in the final frenzy, therefore the player board is flipped and the awards
     * do not need to be updated.
     *
     * @throws UnacceptableItemNumberException
     */
    @Test
    public void registerKillWhenSkullsAbsentFrenzy() throws NoMoreCardsException, UnacceptableItemNumberException, NotAvailableAttributeException, WrongTimeException {

        //initializes the killShotTrack, a killer and a dead
        Board b = BoardConfigurer.getInstance().simulateScenario();
        KillShotTrack killShotTrack =  b.getKillShotTrack();
        Player killer = b.getPlayers().get(0);
        Player dead = b.getPlayers().get(1);

        //removes all the skulls
        killShotTrack.removeSkulls(8);

        //simulates a flipped player board
        dead.setPointsToGive(2);

        //registers the standard kill
        dead.sufferDamage(11, killer);
        killShotTrack.registerKill(killer, dead, false);

        //checks that the number of occurrences of the killer in the killers list is incremented by 1
        //and that he is inserted in the correct position
        assertEquals(1, Collections.frequency(killShotTrack.getKillers(), killer));
        assertEquals(killer, killShotTrack.getKillers().get(0));

        //checks that the number of skulls is 0 and not -1
        assertEquals(0, killShotTrack.getSkullsLeft());

        //checks that the awards given for the death of the dead player have not been updated
        assertEquals(2, dead.getPointsToGive());


    }


    /**
     * Tests the method registerKill() in the event that there are not skulls left on the track and
     * the kill occurred in the final turn.
     * The awards do not need to be updated, since the board of the dead will be flipped in the final frenzy.
     *
     * @throws UnacceptableItemNumberException
     */
    @Test
    public void registerKillWhenSkullsAbsentFinalTurn() throws NoMoreCardsException, UnacceptableItemNumberException, NotAvailableAttributeException, WrongTimeException {

        //initializes the killShotTrack, a killer and a dead
        Board b = BoardConfigurer.getInstance().simulateScenario();
        KillShotTrack killShotTrack =  b.getKillShotTrack();
        Player killer = b.getPlayers().get(0);
        Player dead = b.getPlayers().get(1);

        //removes all the skulls
        killShotTrack.removeSkulls(8);

        //registers the standard kill
        dead.sufferDamage(11,killer);
        killShotTrack.registerKill(killer, dead, false);

        //checks that the number of occurrences of the killer in the killers list is incremented by 1
        //and that he is inserted in the correct position
        assertEquals(1, Collections.frequency(killShotTrack.getKillers(), killer));
        assertEquals(killer, killShotTrack.getKillers().get(0));

        //checks that the number of skulls is 0 and not -1
        assertEquals(0, killShotTrack.getSkullsLeft());

        //checks that the awards given for the death of the dead player have not been updated
        assertEquals(8, dead.getPointsToGive());


    }


    /**
     * Tests the method registerKill() in the event that some kills have already been registered.
     *
     * @throws UnacceptableItemNumberException
     */
    @Test
    public void registerKillWhenTheKillerListIsNotEmpty() throws NoMoreCardsException, UnacceptableItemNumberException, NotAvailableAttributeException, WrongTimeException {

        //initializes the killShotTrack, a killer, a dead and another player
        Board b = BoardConfigurer.getInstance().simulateScenario();
        KillShotTrack killShotTrack =  b.getKillShotTrack();
        Player killer = b.getPlayers().get(0);
        Player dead = b.getPlayers().get(1);
        Player otherPlayer = b.getPlayers().get(2);

        //insert some old killers in the killer list
        killShotTrack.getKillers().add(otherPlayer);
        killShotTrack.getKillers().add(killer);
        killShotTrack.getKillers().add(dead);

        //removes the skulls
        killShotTrack.removeSkulls(3);

        //registers the standard kill
        dead.sufferDamage(11, killer);
        killShotTrack.registerKill(killer, dead, false);

        //checks that the number of occurrences of the killer in the killers list is incremented by 1 and
        //that he is inserted in the correct position
        assertEquals(1+1, Collections.frequency(killShotTrack.getKillers(), killer));
        assertEquals(killer, killShotTrack.getKillers().get(3));

        //checks that a skull has been removed
        assertEquals(8-3-1, killShotTrack.getSkullsLeft());

        //checks that the awards given for the death of the dead player have been updated
        assertEquals(6, dead.getPointsToGive());

    }


    /**
     * Tests the method rewardKillers() in the event that every player killed a different number of opponents.
     */
    @Test
    public void rewardKillersWithDistinctNumberOfKills() throws NoMoreCardsException, UnacceptableItemNumberException, NotAvailableAttributeException {

        //initializes the killShotTrack and five players, with 0 points
        Board b = BoardConfigurer.getInstance().simulateScenario();
        KillShotTrack killShotTrack =  b.getKillShotTrack();
        Player p1 = b.getPlayers().get(0);
        Player p2 = b.getPlayers().get(1);
        Player p3 = b.getPlayers().get(2);
        Player p4 = b.getPlayers().get(3);
        Player p5 = b.getPlayers().get(4);

        //insert players in the killer list several times

        killShotTrack.getKillers().add(p1);
        killShotTrack.getKillers().add(p1);
        killShotTrack.getKillers().add(p1);
        killShotTrack.getKillers().add(p1);
        killShotTrack.getKillers().add(p1);

        killShotTrack.getKillers().add(p2);
        killShotTrack.getKillers().add(p2);
        killShotTrack.getKillers().add(p2);
        killShotTrack.getKillers().add(p2);

        killShotTrack.getKillers().add(p3);
        killShotTrack.getKillers().add(p3);
        killShotTrack.getKillers().add(p3);

        killShotTrack.getKillers().add(p4);
        killShotTrack.getKillers().add(p4);

        killShotTrack.getKillers().add(p5);

        //rewards all the killers
        killShotTrack.rewardKillers();

        //check that every killer has gain the right number of points
        assertEquals(8, p1.getPoints());
        assertEquals(6, p2.getPoints());
        assertEquals(4, p3.getPoints());
        assertEquals(2, p4.getPoints());
        assertEquals(1, p5.getPoints());

    }


    /**
     * Tests the method rewardKillers() in the event that every player killed the same number of opponents.
     */
    @Test
    public void awardKillersWithSameNumberOfKills() throws NoMoreCardsException, UnacceptableItemNumberException, NotAvailableAttributeException {

        //initializes the killShotTrack and five players, with 0 points
        Board b = BoardConfigurer.getInstance().simulateScenario();
        KillShotTrack killShotTrack =  b.getKillShotTrack();
        Player p1 = b.getPlayers().get(0);
        Player p2 = b.getPlayers().get(1);
        Player p3 = b.getPlayers().get(2);
        Player p4 = b.getPlayers().get(3);
        Player p5 = b.getPlayers().get(4);

        //inserts every player in the killer list exactly one time
        killShotTrack.getKillers().add(p1);
        killShotTrack.getKillers().add(p2);
        killShotTrack.getKillers().add(p3);
        killShotTrack.getKillers().add(p4);
        killShotTrack.getKillers().add(p5);

        //rewards all the killers
        killShotTrack.rewardKillers();

        //checks that every killer has gain the right number of points
        assertEquals(8, p1.getPoints());
        assertEquals(6, p2.getPoints());
        assertEquals(4, p3.getPoints());
        assertEquals(2, p4.getPoints());
        assertEquals(1, p5.getPoints());

    }


    /**
     * Tests the method rewardKillers() in the event that the player who killed the highest number of opponents killed
     * his first opponent after everyone else had killed someone.
     */
    @Test
    public void awardKillerWhenTheLastKillerHasTheHighestNumberOfKills() throws NoMoreCardsException, UnacceptableItemNumberException, NotAvailableAttributeException{

        //initializes the killShotTrack and five players, with 0 points
        Board board1 = BoardConfigurer.getInstance().simulateScenario();
        KillShotTrack killShotTrack =  board1.getKillShotTrack();
        Player p1 = board1.getPlayers().get(0);
        Player p2 = board1.getPlayers().get(1);
        Player p3 = board1.getPlayers().get(2);
        Player p4 = board1.getPlayers().get(3);
        Player p5 = board1.getPlayers().get(4);

        //inserts players in the killer list several times
        killShotTrack.getKillers().add(p1);
        killShotTrack.getKillers().add(p2);
        killShotTrack.getKillers().add(p3);
        killShotTrack.getKillers().add(p4);
        killShotTrack.getKillers().add(p5);
        killShotTrack.getKillers().add(p5);

        //rewards all the killers
        killShotTrack.rewardKillers();

        //checks that every killer has gain the right number of points
        assertEquals(8, p5.getPoints());
        assertEquals(6, p1.getPoints());
        assertEquals(4, p2.getPoints());
        assertEquals(2, p3.getPoints());
        assertEquals(1, p4.getPoints());

    }


    /**
     * Tests the method rewardKillers() in the event that some players did not kill anyone.
     */
    @Test
    public void awardKillersWhenSomePlayersWithNoKills() throws NoMoreCardsException, UnacceptableItemNumberException, NotAvailableAttributeException{

        //initializes the killShotTrack and five players, with 0 points
        Board b = BoardConfigurer.getInstance().simulateScenario();
        KillShotTrack killShotTrack =  b.getKillShotTrack();
        Player p1 = b.getPlayers().get(0);
        Player p2 = b.getPlayers().get(1);
        Player p3 = b.getPlayers().get(2);
        Player p4 = b.getPlayers().get(3);
        Player p5 = b.getPlayers().get(4);

        //inserts players in the killer list several times
        killShotTrack.getKillers().add(p1);
        killShotTrack.getKillers().add(p2);
        killShotTrack.getKillers().add(p2);
        killShotTrack.getKillers().add(p1);
        killShotTrack.getKillers().add(p3);

        //awards all the killers
        killShotTrack.rewardKillers();

        //checks that every killer has gain the right number of points
        assertEquals(8, p1.getPoints());
        assertEquals(6, p2.getPoints());
        assertEquals(4, p3.getPoints());
        assertEquals(0, p4.getPoints());
        assertEquals(0, p5.getPoints());

    }

}