package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static java.util.Collections.*;

/**
 * Represents the killshot track of the board.
 * Registers the kills memorizing the killer for each kill.
 * Rewards the killers at the end of the game.
 *
 * @author  BassaniRiccardo
 */


public class KillShotTrack {

    private int skullsLeft;
    private List<Player> killers;


    /**
     * Constructs a killshot track with the number of skulls to add to the track.
     *
     * @param skullsNumber  the number of skulls to add to the track.
     * @throws              IllegalArgumentException
     */
    public KillShotTrack(int skullsNumber) {

        if (skullsNumber<5 || skullsNumber > 8) throw new IllegalArgumentException("The number of skulls must be between 5 and 8");

        this.skullsLeft = skullsNumber;
        this.killers = new ArrayList<>();

    }


    /**
     * Getter for skullsLeft.
     *
     * @return          the number of left skulls.
     */
    public int getSkullsLeft() {
        return skullsLeft;
    }

    /**
     * Getter for killers
     *
     * @return          the players responsible for the kills, orderly and with an occurrence
     *                  per kill and an extra occurrence for each overkill.
     */
    public List<Player> getKillers() {
        return killers;
    }


    /**
     * Removes a specified number of skulls from the track.
     *
     * @param quantity  the number of skulls to remove.
     * @throws          UnacceptableItemNumberException
     */
    public void removeSkulls(int quantity) throws UnacceptableItemNumberException{

        if (quantity > skullsLeft) throw new UnacceptableItemNumberException("The number of skulls left must be major or equal to zero.");
        skullsLeft -= quantity;

    }


    /**
     * Registers a kill.
     * Adds the killer to the killers list, two times if he overkilled the opponent.
     * If there are still skulls left, removes a skull and updates the awards given by the next death of the dead.
     * If there are no skulls left, it means the kill occurred in the final turn or in the final frenzy,
     * therefore the awards do not need to be updated.
     *
     * @param killer        the player who killed the opponent.
     * @param dead          the player who had been killed by the opponent.
     * @param overkill      whether the killer overkilled the opponent
     * @throws              UnacceptableItemNumberException
     */
    public void registerKill(Player killer, Player dead, boolean overkill) throws UnacceptableItemNumberException {

        if (killer.equals(dead))    throw new IllegalArgumentException("The killer and the dead can not be the same person,");
        killers.add(killer);
        if (overkill){
            killers.add(killer);
        }
        if (skullsLeft != 0) {
            dead.updateAwards();
            removeSkulls(1);
        }

    }


    /**
     * Rewards the players who killed at least one opponent, in accordance with the number of opponents killed.
     * The first gets 8 points, the second 6, the third 4, the fourth 2 and the fifth 1.
     * In the event of a tie, the player who got the earlier killshot wins the tie.
     */
    public void rewardKillers(){

        //asks the board for the players
        List<Player> playersToReward = Board.getInstance().getPlayers();

        //properly orders the playersToReward
        Collections.sort(playersToReward, (p1,p2) -> {
            if (frequency(killers, p1) > frequency(killers, p2)) return -1;
            else if (frequency(killers, p1) < frequency(killers, p2)) return 1;
            else {
                if (killers.indexOf(p1) < killers.indexOf(p2)) return -1;
                else if (killers.indexOf(p1) > killers.indexOf(p2)) return 1;
                return 0;

            }
        });

        //assigns the points
        int pointsToGive =8;
        Iterator<Player> playerToAwardIt = playersToReward.iterator();
        while(playerToAwardIt.hasNext()){
            Player p = playerToAwardIt.next();
            if (killers.contains(p)){
                p.addPoints(pointsToGive);
            }
            if (pointsToGive==2) pointsToGive-= 1;
            else pointsToGive -= 2;
        }

    }
}