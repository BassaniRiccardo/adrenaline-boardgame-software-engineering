package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

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
        AmmoPack cost;
        DestinationFinder destinationFinder;
        TargetFinder targetFinder;

        switch (powerUpName) {
            case TARGETING_SCOPE:
                cost = new AmmoPack(0,0,0);
                effect = (shooter, target, destination)-> target.sufferDamage(1, shooter);
                targetFinder = p -> { List<Player> temp = Board.getInstance().getPlayers();
                                        List<List<Player>> targets = new ArrayList<>();
                                        for(Player pla : temp){
                                            if (pla.isJustDamaged()){
                                                List<Player> al = new ArrayList<>();
                                                al.add(pla);
                                                targets.add(al);
                                            }
                                        }
                                        return targets;
                                      };
                destinationFinder = (p, t) -> null;
                break;

            default:
                cost = new AmmoPack(0,0,0);
                effect = (shooter, target, destination)-> target.sufferDamage(1, shooter);
                targetFinder = p -> { List<Player> temp = Board.getInstance().getPlayers();
                    List<List<Player>> targets = new ArrayList<>();
                    for(Player pla : temp){
                        if (pla.isJustDamaged()){
                            ArrayList<Player> al = new ArrayList<>();
                            al.add(pla);
                            targets.add(al);
                        }
                    }
                    return targets;
                };
                destinationFinder = (p, t) -> null;
        }
        return new PowerUp(powerUpName, cost, destinationFinder, targetFinder, effect, color);

    }

}
