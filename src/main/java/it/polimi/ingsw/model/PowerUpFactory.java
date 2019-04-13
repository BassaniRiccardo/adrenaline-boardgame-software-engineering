//TODO load data from JSON/XML

package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static it.polimi.ingsw.model.Board.Direction;

/**
 * Factory class to create a power up.
 *
 * @author  marcobaga
 */

//TODO Add a private constructor....SonarLint
// throw exceptions if necessary

public class PowerUpFactory  {

    /**
     *Creates a PowerUp object according to its name
     *
     * @param  powerUpName  the name of the powerup to be created
     * @param  color        the color of the powerup
     * @return      the PowerUp object created
     */
    public static PowerUp createPowerUp(PowerUp.PowerUpName powerUpName, Color color) {

        Effect effect;
        AmmoPack cost = new AmmoPack(0,0,0);
        DestinationFinder destinationFinder;
        TargetFinder targetFinder;

        switch (powerUpName) {
            case TARGETING_SCOPE:

                effect = (shooter, target, destination)-> target.sufferDamage(1, shooter);
                targetFinder = (p) -> Board.getInstance().getPlayers().stream()
                        .filter(x->x.isJustDamaged())
                        .distinct()
                        .map(x -> Arrays.asList(x))
                        .collect(Collectors.toList());
                destinationFinder = (p, t) -> new ArrayList<>();
                break;

            case NEWTON:
                effect = (shooter, target, destination)-> target.setPosition(destination);
                targetFinder = (p) -> Board.getInstance().getPlayers().stream()
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
                        res.addAll(Board.getInstance().getSquaresInLine(center, d).stream()
                                .filter(x->Board.getInstance().getDistance(center, x)<3)
                                .collect(Collectors.toList()));
                    }
                    return res;
                };
                break;

            case TAGBACK_GRENADE:
                effect = (shooter, target, destination)-> target.addMarks(1, shooter);
                targetFinder = (p) -> p.isJustDamaged()? new ArrayList<>():Arrays.asList(Arrays.asList(Board.getInstance().getCurrentPlayer()));
                destinationFinder = (p, t) -> new ArrayList<>();
                break;

            case TELEPORTER:
                effect = (shooter, target, destination)-> target.setPosition(destination);
                targetFinder = (p) -> Arrays.asList(Arrays.asList(p));
                destinationFinder = (p, t) -> Board.getInstance().getMap();
                break;

            default:
                effect = (shooter, target, destination)-> shooter.setPosition(destination);
                targetFinder = (p) -> Arrays.asList(Arrays.asList(p));
                destinationFinder = (p, t) -> Board.getInstance().getMap();
                break;
        }
        return new PowerUp(powerUpName, cost, destinationFinder, targetFinder, effect, color);
    }
}
