package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Player;
import it.polimi.ingsw.model.board.Square;
import it.polimi.ingsw.model.cards.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    private static final String TARGETING_SCOPE_DMG = "targetingScopeDmg";
    private static final String NEWTON_MAX_DISTANCE = "newtonMaxDistance";
    private static final String TAGBACK_GRENADE_MARKS = "tagbackGrenadeMarks";


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
                effect = (shooter, target, destination)-> target.sufferDamageNoMarksExtra(j.getInt(TARGETING_SCOPE_DMG), shooter);
                targetFinder = p -> board.getPlayers().stream()
                        .filter(Player::isJustDamaged)
                        .distinct()
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
                destinationFinder = (p, t) -> new ArrayList<>();
                break;

            case NEWTON:
                effect = (shooter, target, destination)-> target.setPosition(destination);
                targetFinder = p -> board.getActivePlayers().stream()
                        .filter(x->!x.equals(p))
                        .distinct()
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
                destinationFinder = (p, t) -> {
                    if(t.isEmpty()){
                        return Collections.singletonList(p.getPosition());
                    }
                    List<Square> res = new ArrayList<>();
                    Square center = t.get(0).getPosition();
                    res.add(center);
                    for (Direction d : Direction.values()) {
                        res.addAll(board.getSquaresInLine(center, d).stream()
                                .filter(x->board.getDistance(center, x)< j.getInt(NEWTON_MAX_DISTANCE))
                                .collect(Collectors.toList()));
                    }
                    return res;
                };
                break;

            case TAGBACK_GRENADE:
                effect = (shooter, target, destination)-> target.addMarks(j.getInt(TAGBACK_GRENADE_MARKS), shooter);
                targetFinder = p -> p.isJustDamaged()? new ArrayList<>():Collections.singletonList(Collections.singletonList(board.getCurrentPlayer()));
                destinationFinder = (p, t) -> new ArrayList<>();
                break;

            case TELEPORTER:
                effect = (shooter, target, destination)-> target.setPosition(destination);
                targetFinder = p -> Collections.singletonList(Collections.singletonList(p));
                destinationFinder = (p, t) -> board.getMap();
                break;

            default:
                effect = (shooter, target, destination)-> shooter.setPosition(destination);
                targetFinder = p -> Collections.singletonList(Collections.singletonList(p));
                destinationFinder = (p, t) -> board.getMap();
                break;
        }
        return new PowerUp(powerUpName, destinationFinder, targetFinder, effect, color, board);
    }
}
