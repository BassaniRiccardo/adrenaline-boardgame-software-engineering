package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.model.Color.*;

/**
 * Factory class to create a weapon.
 *
 * @author  marcobaga
 */

public class WeaponFactory {

    /**
     *Creates a Weapon object according to its name
     *
     * @param  weaponName  the name of the weapon to be created
     * @return      the Weapon object created
     */
    public static Weapon createWeapon(Weapon.WeaponName weaponName){

        Color color;
        AmmoPack fullCost;
        AmmoPack reducedCost;
        ArrayList <FireMode> fireModeList;
        FireMode fireMode;
        TargetFinder targetFinder;
        DestinationFinder destinationFinder;
        Effect effect;

        //The only weapon implemented so far
        weaponName = Weapon.WeaponName.LOCK_RIFLE;

        switch (weaponName) {

            case LOCK_RIFLE:    //Distrutto;re

                color = BLUE;
                fullCost = new AmmoPack(0, 2, 0);
                reducedCost = new AmmoPack(0, 1, 0);
                fireModeList = new ArrayList<>(2);

                effect = createEffect(1, 1);

                targetFinder = p -> {
                    List<Square> sl = Board.getInstance().getVisible(p.getPosition());
                    List<List<Player>> targetGroups = new ArrayList<>();
                    for (Square s : sl) {
                        for (Player pla : Board.getInstance().getPlayersInside(s)) {
                            ArrayList<Player> temp = new ArrayList<>();
                            temp.add(pla);
                            targetGroups.add(temp);
                        }
                    }
                    return targetGroups;
                };

                destinationFinder = (p,t) -> null;
                fireMode = new FireMode (FireMode.FireModeName.MAIN, 1, new AmmoPack(0, 0, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);

                effect = createEffect(1, 0);
                targetFinder = p -> {
                    if(!p.getMainTargets().isEmpty()) {
                        List<Square> sl = Board.getInstance().getVisible(p.getPosition());
                        List<List<Player>> targetGroups = new ArrayList<>();
                        for (Square s : sl) {
                            for (Player pla : Board.getInstance().getPlayersInside(s)) {
                                if (!(p.getMainTargets().contains(pla))) {
                                    List<Player> temp = new ArrayList<>();
                                    temp.add(pla);
                                    targetGroups.add(temp);
                                }
                            }
                        }
                        return targetGroups;
                    }
                    List<List<Player>> res = new ArrayList<>();
                    return res;
                };
                destinationFinder = (p, t) -> null;
                fireMode = new FireMode (FireMode.FireModeName.OPTION1, 1, new AmmoPack(1, 0, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);
                break;

            default:

                color = BLUE;
                fireModeList = null;
                fullCost = null;
                reducedCost = null;

        }

        Weapon weapon = new Weapon(weaponName, color, fullCost, reducedCost, fireModeList);

        for(FireMode f : fireModeList){
            f.setWeapon(weapon);
        }
        return weapon;

    }

    private static Effect createEffect(int damage, int marks) throws IllegalArgumentException {

        if(damage<0 || marks<0){
            throw new IllegalArgumentException();
        }
        if(damage!=0&&marks!=0) {
            return (shooter, target, destination) -> {
                target.sufferDamage(damage, shooter);
                target.addMarks(marks, shooter);
            };
        }
        if(damage!=0){
            return (shooter, target, destination) -> {
                target.sufferDamage(damage, shooter);
            };
        }
        return (shooter, target, destination) -> {
            target.sufferDamage(damage, shooter);
        };

    }
}