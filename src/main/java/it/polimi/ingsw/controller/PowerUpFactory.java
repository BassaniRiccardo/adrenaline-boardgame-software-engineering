package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Square;
import it.polimi.ingsw.model.cards.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static it.polimi.ingsw.model.board.Board.Direction;

/**
 * Factory class to create power ups.
 *
 * @author  marcobaga
 */

public class PowerUpFactory  {

    private Board board;
    private static ModelDataReader j = new ModelDataReader();

    /**
     * Constructs a PowerUpFactory with a reference to the game board
     *
     * @param board         the board of the game
     */
    public PowerUpFactory(Board board){this.board = board;}

    /**
     *Creates a PowerUp object according to its name
     *
     * @param  powerUpName  the name of the power up to be created
     * @param  color        the color of the power up
     * @return      the PowerUp object created
     */
    public PowerUp createPowerUp(PowerUp.PowerUpName powerUpName, Color color) {

        Effect effect;
        DestinationFinder destinationFinder;
        TargetFinder targetFinder;

        switch (powerUpName) {
            case TARGETING_SCOPE:

                effect = (shooter, target, destination)-> target.sufferDamage(j.getInt("targetingScopeDmg"), shooter);
                targetFinder = p -> board.getPlayers().stream()
                        .filter(x->x.isJustDamaged())
                        .distinct()
                        .map(x -> Arrays.asList(x))
                        .collect(Collectors.toList());
                destinationFinder = (p, t) -> new ArrayList<>();
                break;

            case NEWTON:
                effect = (shooter, target, destination)-> target.setPosition(destination);
                targetFinder = p -> board.getActivePlayers().stream()
                        .filter(x->!x.equals(p))
                        .distinct()
                        .map(x -> Arrays.asList(x))
                        .collect(Collectors.toList());
                destinationFinder = (p, t) -> {
                    if(t.isEmpty()){
                        return Arrays.asList(p.getPosition());
                    }
                    List<Square> res = new ArrayList<>();
                    Square center = t.get(0).getPosition();
                    res.add(center);
                    for (Direction d : Direction.values()) {
                        res.addAll(board.getSquaresInLine(center, d).stream()
                                .filter(x->board.getDistance(center, x)< j.getInt("newtonMaxDistance"))
                                .collect(Collectors.toList()));
                    }
                    return res;
                };
                break;

            case TAGBACK_GRENADE:
                effect = (shooter, target, destination)-> target.addMarks(j.getInt("tagbackGrenadeMarks"), shooter);
                targetFinder = p -> p.isJustDamaged()? new ArrayList<>():Arrays.asList(Arrays.asList(board.getCurrentPlayer()));
                destinationFinder = (p, t) -> new ArrayList<>();
                break;

            case TELEPORTER:
                effect = (shooter, target, destination)-> target.setPosition(destination);
                targetFinder = p -> Arrays.asList(Arrays.asList(p));
                destinationFinder = (p, t) -> board.getMap();
                break;

            default:
                effect = (shooter, target, destination)-> shooter.setPosition(destination);
                targetFinder = p -> Arrays.asList(Arrays.asList(p));
                destinationFinder = (p, t) -> board.getMap();
                break;
        }
        return new PowerUp(powerUpName, destinationFinder, targetFinder, effect, color, board);
    }
}
