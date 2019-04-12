//TODO check whether this factory and Board methods share the same logic (eg. does getVisible return the shooter's square?)
//TODO reformat this class (check the commented section)
//TODO check whether MainTargets can be a single Player instead of an Array
//TODO check whether returning a List ofPlayers instead of an ArrayList can cause issues
//TODO load data from JSON/XML

package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.*;

import static it.polimi.ingsw.model.Color.*;

/**
 * Factory class to create a weapon.
 *
 * @author  marcobaga
 */

//TODO filter the players in order not to include the shooter in the target list.
// necessary since now getVisible include the shooter square.
// Look at the SonarLint issues.
// Throw NotAvailableArgumentException.

public class WeaponFactory {

    /**
     *Creates a Weapon object according to its name
     *
     * @param  weaponName  the name of the weapon to be created
     * @return      the Weapon object created
     */
    public static Weapon createWeapon(Weapon.WeaponName weaponName)     {

        Color color;
        AmmoPack fullCost;
        AmmoPack reducedCost;
        ArrayList <FireMode> fireModeList;
        FireMode fireMode;
        TargetFinder targetFinder;
        DestinationFinder destinationFinder;
        Effect effect;


        /*
        Color color = getColor(weaponName);
        AmmoPack fullCost = getFullCost(weaponName);
        AmmoPack reducedCost = getReducedCost(weaponNameeaponName);
        ArrayList<FireMode.FireModeName> nameList = getNameList(weaponName);
        ArrayList<FireMode> fireModeList = new ArrayList<FireMode>(nameList.size());

        for (FireMode.FireModeName name : nameList) {
            effect = getEffect(weaponName, name);
            targetFinder = getTargetFinder(weaponName, name);
            destinationFinder = getDestinationFinder(weaponName, name);
            targetNumber = getTargetNumber(weaponName, name);
            fireModeCost = getFireModeCost(weaponName, name);

            fireMode = new FireMode(name, targetNumber, fireModeCost, destinationFinder, targetFinder, effect);
            fireModeList.add(fireMode);
        }

        Weapon weapon = new Weapon(weaponName, color, fullCost, reducedCost, fireModeList);

        for (FireMode f : fireModeList) {
            f.setWeapon(weapon);
        }
        return weapon;*/


        //The only weapon implemented so far
        //weaponName = Weapon.WeaponName.LOCK_RIFLE;

        switch (weaponName) {

            case LOCK_RIFLE:    //Distruttore

                color = BLUE;
                fullCost = new AmmoPack(0, 2, 0);
                reducedCost = new AmmoPack(0, 1, 0);
                fireModeList = new ArrayList<>(2);

                effect = createEffect(1, 1);
                targetFinder = p -> Board.getInstance().getVisible(p.getPosition()).stream()
                        .map(x -> x.getPlayers())
                        .flatMap(x -> x.stream())
                        .filter( x -> x != p)
                        .distinct()
                        .map(x -> Arrays.asList(x))
                        .collect(Collectors.toList());
                destinationFinder = (p, t) -> null;
                fireMode = new FireMode(FireMode.FireModeName.MAIN, 1, new AmmoPack(0, 0, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);

                effect = createEffect(1, 0);
                targetFinder = p -> (p.getMainTargets().isEmpty()) ? new ArrayList() : Board.getInstance().getVisible(p.getPosition()).stream()
                        .map(x -> x.getPlayers())
                        .flatMap(x -> x.stream())
                        .distinct()
                        .filter(x -> !p.getMainTargets().contains(x))
                        .map(x -> Arrays.asList(x))
                        .collect(Collectors.toCollection(ArrayList::new));
                destinationFinder = (p, t) -> null;
                fireMode = new FireMode(FireMode.FireModeName.OPTION1, 1, new AmmoPack(1, 0, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);
                break;

            case MACHINE_GUN:    //Mitragliatrice

                color = BLUE;
                fullCost = new AmmoPack(1, 1, 0);
                reducedCost = new AmmoPack(1, 0, 0);
                fireModeList = new ArrayList<>(3);

                effect = createEffect(1, 0);
                targetFinder = p -> {
                    List<List<Player>> temp = Board.getInstance().getVisible(p.getPosition()).stream()
                            .map(x -> x.getPlayers())
                            .flatMap(x -> x.stream())
                            .distinct()
                            .map(x -> Arrays.asList(x))
                            .collect(Collectors.toList());
                    List<List<Player>> res = cartesian(temp, temp);
                    res.addAll(temp);
                    return res;
                };
                destinationFinder = (p, t) -> null;
                fireMode = new FireMode(FireMode.FireModeName.MAIN, 2, new AmmoPack(0, 0, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);

                effect = createEffect(1, 0);
                targetFinder = p -> (p.getMainTargets().stream()
                        .filter(x -> !p.getOptionalTargets().contains(x))
                        .distinct()
                        .map(x -> Arrays.asList(x))
                        .collect(Collectors.toList()));
                destinationFinder = (p, t) -> null;
                fireMode = new FireMode(FireMode.FireModeName.OPTION1, 1, new AmmoPack(0, 0, 1), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);

                effect = createEffect(1, 0);
                targetFinder = p -> {
                    List<List<Player>> pastTargets = p.getMainTargets().stream()
                            .filter(x -> !p.getOptionalTargets().contains(x))
                            .distinct()
                            .map(x -> Arrays.asList(x))
                            .collect(Collectors.toList());
                    List<List<Player>> others = Board.getInstance().getVisible(p.getPosition()).stream()
                            .map(x -> x.getPlayers())
                            .flatMap(x -> x.stream())
                            .filter(x -> !(p.getMainTargets().contains(x) || p.getOptionalTargets().contains(x)))
                            .distinct()
                            .map(x -> Arrays.asList(x))
                            .collect(Collectors.toList());
                    others.addAll(cartesian(pastTargets, others));
                    others.addAll(pastTargets);
                    return others;
                };
                destinationFinder = (p, t) -> null;
                fireMode = new FireMode(FireMode.FireModeName.OPTION2, 1, new AmmoPack(0, 0, 1), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);
                break;

            case THOR:    //Torpedine

                color = BLUE;
                fullCost = new AmmoPack(1, 1, 0);
                reducedCost = new AmmoPack(1, 0, 0);
                fireModeList = new ArrayList<>(3);

                effect = createEffect(1, 1);
                targetFinder = p -> Board.getInstance().getVisible(p.getPosition()).stream()
                        .map(x -> x.getPlayers())
                        .flatMap(x -> x.stream())
                        .distinct()
                        .map(x -> Arrays.asList(x))
                        .collect(Collectors.toList());
                destinationFinder = (p, t) -> null;
                fireMode = new FireMode(FireMode.FireModeName.MAIN, 1, new AmmoPack(0, 0, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);

                effect = createEffect(1, 0);
                targetFinder = p -> (p.getMainTargets().isEmpty()) ?
                        p.getMainTargets()
                                .stream()
                                .map(x -> Arrays.asList(x))
                                .collect(Collectors.toCollection(ArrayList::new))
                        : Board.getInstance().getVisible(p.getMainTargets().get(0).getPosition()).stream()
                        .map(x -> x.getPlayers())
                        .flatMap(x -> x.stream())
                        .distinct()
                        .filter(x -> !p.getMainTargets().contains(x))
                        .map(x -> Arrays.asList(x))
                        .collect(Collectors.toCollection(ArrayList::new));
                destinationFinder = (p, t) -> null;
                fireMode = new FireMode(FireMode.FireModeName.OPTION1, 1, new AmmoPack(0, 1, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);


                effect = createEffect(2, 0);
                targetFinder = p -> (p.getOptionalTargets().isEmpty()) ?
                        p.getOptionalTargets()
                                .stream()
                                .map(x -> Arrays.asList(x))
                                .collect(Collectors.toCollection(ArrayList::new))
                        : Board.getInstance().getVisible(p.getOptionalTargets().get(0).getPosition()).stream()
                        .map(x -> x.getPlayers())
                        .flatMap(x -> x.stream())
                        .distinct()
                        .filter(x -> !(p.getMainTargets().contains(x) || p.getOptionalTargets().contains(x)))
                        .map(x -> Arrays.asList(x))
                        .collect(Collectors.toCollection(ArrayList::new));
                destinationFinder = (p, t) -> null;
                fireMode = new FireMode(FireMode.FireModeName.OPTION1, 1, new AmmoPack(0, 1, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);
                break;

            case PLASMA_GUN:    //Fucile al plasma

                color = BLUE;
                fullCost = new AmmoPack(0, 1, 1);
                reducedCost = new AmmoPack(0, 0, 1);
                fireModeList = new ArrayList<>(3);

                effect = createEffect(2, 0);
                targetFinder = p -> Board.getInstance().getVisible(p.getPosition()).stream()
                        .map(x -> x.getPlayers())
                        .flatMap(x -> x.stream())
                        .distinct()
                        .map(x -> Arrays.asList(x))
                        .collect(Collectors.toList());
                destinationFinder = (p, t) -> null;
                fireMode = new FireMode(FireMode.FireModeName.MAIN, 1, new AmmoPack(0, 0, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);

                effect = (shooter, target, destination) -> shooter.setPosition(destination);
                targetFinder = p -> Arrays.asList(p).stream()
                        .map(x -> Arrays.asList(x))
                        .collect(Collectors.toList());
                destinationFinder = (p, t) -> Board.getInstance().getReachable(p.getPosition(), 2);
                fireMode = new FireMode(FireMode.FireModeName.OPTION1, 1, new AmmoPack(0, 0, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);

                effect = createEffect(1, 0);
                targetFinder = p -> (p.getMainTargets().stream()
                        .filter(x -> !p.getOptionalTargets().contains(x))
                        .distinct()
                        .map(x -> Arrays.asList(x))
                        .collect(Collectors.toList()));
                destinationFinder = (p, t) -> null;
                fireMode = new FireMode(FireMode.FireModeName.OPTION1, 1, new AmmoPack(0, 1, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);
                break;

            case WHISPER:    //Fucile di precisione

                color = BLUE;
                fullCost = new AmmoPack(0, 2, 1);
                reducedCost = new AmmoPack(0, 1, 1);
                fireModeList = new ArrayList<>(1);

                effect = createEffect(3, 1);
                targetFinder = p -> Board.getInstance().getVisible(p.getPosition()).stream()
                        .map(x -> x.getPlayers())
                        .flatMap(x -> x.stream())
                        .distinct()
                        .filter(x -> Board.getInstance().getDistance(x.getPosition(), p.getPosition()) >= 2)
                        .map(x -> Arrays.asList(x))
                        .collect(Collectors.toList());
                destinationFinder = (p, t) -> null;
                fireMode = new FireMode(FireMode.FireModeName.MAIN, 1, new AmmoPack(0, 0, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);
                break;

            case ELECTROSCYTHE:    //Falce protonica

                color = BLUE;
                fullCost = new AmmoPack(0, 1, 0);
                reducedCost = new AmmoPack(0, 0, 0);
                fireModeList = new ArrayList<>(2);

                effect = createEffect(1, 0);
                targetFinder = p -> (p.getPosition()).getPlayers().stream()
                        .distinct()
                        .filter(x -> (!x.equals(p)))
                        .map(x -> Arrays.asList(x))
                        .collect(Collectors.toList());
                destinationFinder = (p, t) -> null;
                fireMode = new FireMode(FireMode.FireModeName.MAIN, 1, new AmmoPack(0, 0, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);

                effect = createEffect(2, 0);
                targetFinder = p -> (p.getPosition()).getPlayers().stream()
                        .distinct()
                        .filter(x -> (!x.equals(p)))
                        .map(x -> Arrays.asList(x))
                        .collect(Collectors.toList());
                destinationFinder = (p, t) -> null;
                fireMode = new FireMode(FireMode.FireModeName.SECONDARY, 1, new AmmoPack(1, 1, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);
                break;

            case TRACTOR_BEAM:    //Distrutto;re

                color = BLUE;
                fullCost = new AmmoPack(0, 1, 0);
                reducedCost = new AmmoPack(0, 0, 0);
                fireModeList = new ArrayList<>(2);

                effect = (shooter, target, destination) -> {
                    target.setPosition(destination);
                    target.sufferDamage(1, shooter);
                };
                targetFinder = p -> {
                    List<Square> l = Board.getInstance().getVisible(p.getPosition());
                    List<Square> temp = new ArrayList<>();
                    for (Square s : l) {
                        temp.addAll(Board.getInstance().getReachable(s, 2));
                    }
                    l.addAll(temp);
                    return l.stream()
                            .distinct()
                            .map(x -> x.getPlayers())
                            .flatMap(x -> x.stream())
                            .distinct()
                            .map(x -> Arrays.asList(x))
                            .collect(Collectors.toList());
                };
                destinationFinder = (p, t) -> Board.getInstance().getVisible(p.getPosition()).stream()
                        .distinct()
                        .filter(x -> (t.isEmpty() ? true : Board.getInstance().getVisible(t.get(0).getPosition()).contains(x)))
                        .collect(Collectors.toList());
                fireMode = new FireMode(FireMode.FireModeName.MAIN, 1, new AmmoPack(0, 0, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);

                effect = (shooter, target, destination) -> {
                    target.setPosition(shooter.getPosition());
                    target.sufferDamage(3, shooter);
                };
                targetFinder = p -> Board.getInstance().getReachable(p.getPosition(), 3).stream()
                        .map(x -> x.getPlayers())
                        .flatMap(x -> x.stream())
                        .distinct()
                        .map(x -> Arrays.asList(x))
                        .collect(Collectors.toCollection(ArrayList::new));
                destinationFinder = (p, t) -> null;
                fireMode = new FireMode(FireMode.FireModeName.SECONDARY, 1, new AmmoPack(1, 0, 1), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);
                break;

            case VORTEX_CANNON:    //Cannone Vortex

                color = RED;
                fullCost = new AmmoPack(1, 1, 0);
                reducedCost = new AmmoPack(0, 1, 0);
                fireModeList = new ArrayList<>(2);

                effect = (shooter, target, destination) -> {
                    target.setPosition(destination);
                    target.sufferDamage(2, shooter);
                };
                targetFinder = p -> {
                    List<Square> l = Board.getInstance().getVisible(p.getPosition());
                    List<Square> temp = new ArrayList<>();
                    for (Square s : l) {
                        temp.addAll(Board.getInstance().getReachable(s, 1));
                    }
                    l.addAll(temp);
                    return l.stream()
                            .map(x -> x.getPlayers())
                            .flatMap(x -> x.stream())
                            .distinct()
                            .map(x -> Arrays.asList(x))
                            .collect(Collectors.toList());
                };
                destinationFinder = (p, t) -> Board.getInstance().getReachable(t.get(0).getPosition(), 1).stream()
                        .filter(x -> Board.getInstance().getVisible(p.getPosition()).contains(x))
                        .filter(x -> !x.equals(p.getPosition()))
                        .distinct()
                        .collect(Collectors.toList());
                fireMode = new FireMode(FireMode.FireModeName.MAIN, 1, new AmmoPack(0, 0, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);

                effect = (shooter, target, destination) -> {
                    target.setPosition(destination);
                    target.sufferDamage(1, shooter);
                };
                targetFinder = p -> {
                    if (p.getMainTargets().isEmpty()) {
                        return new ArrayList();
                    }
                    List<Square> s = Board.getInstance().getReachable(p.getMainTargets().get(0).getPosition(), 1);
                    s.add(p.getPosition());
                    List<List<Player>> lp = s.stream()
                            .map(x -> x.getPlayers())
                            .flatMap(x -> x.stream())
                            .distinct()
                            .map(x -> Arrays.asList(x))
                            .collect(Collectors.toList());
                    List<List<Player>> res = cartesian(lp, lp);
                    res.addAll(lp);
                    return res;
                };
                destinationFinder = (p, t) -> p.getMainTargets().isEmpty() ? new ArrayList<Square>() : Stream.of(p.getMainTargets().get(0).getPosition()).collect(Collectors.toList());
                fireMode = new FireMode(FireMode.FireModeName.OPTION1, 1, new AmmoPack(1, 0, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);
                break;

            case FURNACE:    //Vulcanizzatore

                color = RED;
                fullCost = new AmmoPack(1, 1, 0);
                reducedCost = new AmmoPack(0, 1, 0);
                fireModeList = new ArrayList<>(2);

                effect = createEffect(1, 0);
                targetFinder = p -> {
                    List<List<Square>> lls = Board.getInstance().getVisible(p.getPosition()).stream()
                            .map(x -> x.getRoomId())
                            .distinct()
                            .map(x -> Board.getInstance().getSquaresInRoom(x))
                            .collect(Collectors.toList());
                    List<List<Player>> res = new ArrayList<>();
                    List<Player> temp = new ArrayList<Player>();
                    for (List<Square> ls : lls) {
                        for (Square s : ls) {
                            temp.addAll(s.getPlayers());
                        }
                        res.add(temp);
                        temp = new ArrayList<>();
                    }
                    return res;
                };

                destinationFinder = (p, t) -> null;
                fireMode = new FireMode(FireMode.FireModeName.MAIN, 4, new AmmoPack(0, 0, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);

                effect = createEffect(1, 1);
                targetFinder = p -> Board.getInstance().getAdjacent(p.getPosition()).stream()
                        .map(x -> x.getPlayers())
                        .collect(Collectors.toCollection(ArrayList::new));
                destinationFinder = (p, t) -> null;
                fireMode = new FireMode(FireMode.FireModeName.OPTION1, 4, new AmmoPack(0, 0, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);
                break;

            case HEATSEEKER:    //Raggio termico

                color = RED;
                fullCost = new AmmoPack(2, 0, 1);
                reducedCost = new AmmoPack(1, 0, 1);
                fireModeList = new ArrayList<>(1);

                effect = createEffect(3, 0);
                targetFinder = p -> Board.getInstance().getMap().stream()
                        .filter(x -> !Board.getInstance().getVisible(p.getPosition()).contains(x))
                        .map(x -> x.getPlayers())
                        .flatMap(x -> x.stream())
                        .distinct()
                        .map(x -> Arrays.asList(x))
                        .collect(Collectors.toList());
                destinationFinder = (p, t) -> null;
                fireMode = new FireMode(FireMode.FireModeName.MAIN, 1, new AmmoPack(0, 0, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);
                break;

            case HELLION:    //Raggio solare

                color = RED;
                fullCost = new AmmoPack(1, 0, 1);
                reducedCost = new AmmoPack(0, 0, 1);
                fireModeList = new ArrayList<>(2);

                effect = (shooter, target, destination) -> {
                    target.sufferDamage(1, shooter);
                    Board.getInstance().getPlayersInside(target.getPosition()).forEach(x -> x.addMarks(1, shooter));
                };
                targetFinder = p -> Board.getInstance().getVisible(p.getPosition()).stream()
                        .filter(x -> !x.containsPlayer(p))
                        .map(x -> x.getPlayers())
                        .flatMap(x -> x.stream())
                        .distinct()
                        .map(x -> Arrays.asList(x))
                        .collect(Collectors.toList());
                destinationFinder = (p, t) -> null;
                fireMode = new FireMode(FireMode.FireModeName.MAIN, 1, new AmmoPack(0, 0, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);


                effect = (shooter, target, destination) -> {
                    target.sufferDamage(1, shooter);
                    Board.getInstance().getPlayersInside(target.getPosition()).forEach(x -> x.addMarks(2, shooter));
                };
                targetFinder = p -> Board.getInstance().getVisible(p.getPosition()).stream()
                        .filter(x -> !x.containsPlayer(p))
                        .map(x -> x.getPlayers())
                        .flatMap(x -> x.stream())
                        .distinct()
                        .map(x -> Arrays.asList(x))
                        .collect(Collectors.toList());
                destinationFinder = (p, t) -> null;
                fireMode = new FireMode(FireMode.FireModeName.SECONDARY, 1, new AmmoPack(1, 0, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);
                break;

            case FLAMETHROWER:    //Lanciafiamme

                color = RED;
                fullCost = new AmmoPack(1, 0, 0);
                reducedCost = new AmmoPack(0, 0, 0);
                fireModeList = new ArrayList<>(2);

                effect = createEffect(1, 0);
                targetFinder = p -> {
                    List<List<Player>> targets = new ArrayList<>();
                    for (String s : new ArrayList<String>(Arrays.asList("right", "left", "up", "down"))) {
                        List<List<Player>> close = Board.getInstance().getSquaresInLine(p.getPosition(), s).stream()
                                .filter(x -> Board.getInstance().getReachable(p.getPosition(), 1).contains(x))
                                .map(x -> x.getPlayers())
                                .flatMap(x -> x.stream())
                                .distinct()
                                .map(x -> Arrays.asList(x))
                                .collect(Collectors.toList());
                        List<List<Player>> far = Board.getInstance().getSquaresInLine(p.getPosition(), s).stream()
                                .filter(x -> Board.getInstance().getReachable(p.getPosition(), 2).contains(x))
                                .filter(x -> !Board.getInstance().getReachable(p.getPosition(), 1).contains(x))
                                .map(x -> x.getPlayers())
                                .flatMap(x -> x.stream())
                                .distinct()
                                .map(x -> Arrays.asList(x))
                                .collect(Collectors.toList());
                        targets.addAll(close);
                        targets.addAll(far);
                        targets.addAll(cartesian(close, far));
                    }
                    return targets;
                };
                destinationFinder = (p, t) -> null;
                fireMode = new FireMode(FireMode.FireModeName.MAIN, 2, new AmmoPack(0, 0, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);

                effect = (shooter, target, destination) -> {
                    if(Board.getInstance().getReachable(shooter.getPosition(), 1).contains(target.getPosition())){
                        target.sufferDamage(2, shooter);
                    } else target.sufferDamage(1, shooter);
                };
                targetFinder = p -> {
                    List<List<Player>> targets = new ArrayList<>();
                    for (String s : new ArrayList<String>(Arrays.asList("right", "left", "up", "down"))) {
                        List<List<Player>> close = Board.getInstance().getSquaresInLine(p.getPosition(), s).stream()
                                .filter(x -> Board.getInstance().getReachable(p.getPosition(), 1).contains(x))
                                .map(x -> x.getPlayers())
                                .flatMap(x -> x.stream())
                                .distinct()
                                .map(x -> Arrays.asList(x))
                                .collect(Collectors.toList());
                        List<List<Player>> far = Board.getInstance().getSquaresInLine(p.getPosition(), s).stream()
                                .filter(x -> Board.getInstance().getReachable(p.getPosition(), 2).contains(x))
                                .filter(x -> !Board.getInstance().getReachable(p.getPosition(), 1).contains(x))
                                .map(x -> x.getPlayers())
                                .flatMap(x -> x.stream())
                                .distinct()
                                .map(x -> Arrays.asList(x))
                                .collect(Collectors.toList());
                        targets.addAll(close);
                        targets.addAll(far);
                        targets.addAll(cartesian(close, far));
                    }
                    return targets;
                };
                destinationFinder = (p, t) -> null;
                fireMode = new FireMode(FireMode.FireModeName.SECONDARY, 2, new AmmoPack(0, 0, 2), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);
                break;

            case GRENADE_LAUNCHER:    //Lanciagranate

                color = RED;
                fullCost = new AmmoPack(1, 0, 0);
                reducedCost = new AmmoPack(0, 0, 0);
                fireModeList = new ArrayList<>(2);

                effect = (shooter, target, destination) -> {
                    target.sufferDamage(1, shooter);
                    target.setPosition(destination);
                };
                targetFinder = p -> Board.getInstance().getVisible(p.getPosition()).stream()
                        .map(x -> x.getPlayers())
                        .flatMap(x -> x.stream())
                        .distinct()
                        .map(x -> Arrays.asList(x))
                        .collect(Collectors.toList());
                destinationFinder = (p, t) -> t.isEmpty() ? Arrays.asList(p.getPosition()) : Board.getInstance().getReachable(t.get(0).getPosition(), 1);
                fireMode = new FireMode(FireMode.FireModeName.MAIN, 1, new AmmoPack(0, 0, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);

                effect = createEffect(1, 0);
                targetFinder = p -> Board.getInstance().getVisible(p.getPosition()).stream()
                        .map(x -> x.getPlayers())
                        .collect(Collectors.toCollection(ArrayList::new));
                destinationFinder = (p, t) -> null;
                fireMode = new FireMode(FireMode.FireModeName.OPTION1, 4, new AmmoPack(1, 0, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);
                break;

            case ROCKET_LAUNCHER:    //Distrutto;re

                color = RED;
                fullCost = new AmmoPack(2, 0, 0);
                reducedCost = new AmmoPack(1, 0, 0);
                fireModeList = new ArrayList<>(3);

                effect = (shooter, target, destination) -> {
                    target.sufferDamage(2, shooter);
                    target.setPosition(destination);
                };
                targetFinder = p -> Board.getInstance().getVisible(p.getPosition()).stream()
                        .filter(x -> !x.equals(p.getPosition()))
                        .map(x -> x.getPlayers())
                        .flatMap(x -> x.stream())
                        .distinct()
                        .map(x -> Arrays.asList(x))
                        .collect(Collectors.toList());
                destinationFinder = (p, t) -> t.isEmpty() ? Arrays.asList(p.getPosition()) : Board.getInstance().getReachable(t.get(0).getPosition(), 1);
                fireMode = new FireMode(FireMode.FireModeName.MAIN, 1, new AmmoPack(0, 0, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);

                effect = (shooter, target, destination) -> shooter.setPosition(destination);
                targetFinder = p -> Arrays.asList(Arrays.asList(p));
                destinationFinder = (p, t) -> Board.getInstance().getReachable(p.getPosition(), 2);
                fireMode = new FireMode(FireMode.FireModeName.OPTION1, 1, new AmmoPack(0, 1, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);

                effect = createEffect(1, 0);
                targetFinder = p -> {
                    List<Player> l = p.getMainTargets().get(0).getPreviousPosition().getPlayers();
                    l.addAll(p.getMainTargets());
                    return Arrays.asList(l);
                };
                destinationFinder = (p, t) -> null;
                fireMode = new FireMode(FireMode.FireModeName.OPTION2, 4, new AmmoPack(0, 0, 1), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);
                break;

            case RAILGUN:    //Fucile Laser

                color = YELLOW;
                fullCost = new AmmoPack(0, 1, 2);
                reducedCost = new AmmoPack(0, 1, 1);
                fireModeList = new ArrayList<>(2);

                effect = createEffect(3, 0);
                targetFinder = p -> {
                    List<List<Player>> targets = new ArrayList<>();
                    for (String s : new ArrayList<String>(Arrays.asList("right", "left", "up", "down"))) {
                        List<List<Player>> single = Board.getInstance().getSquaresInLineIgnoringWalls(p.getPosition(), s)
                                .stream()
                                .map(x -> x.getPlayers())
                                .flatMap(x -> x.stream())
                                .distinct()
                                .map(x -> Arrays.asList(x))
                                .collect(Collectors.toList());
                        targets.addAll(single);
                    }
                    return targets;
                };
                destinationFinder = (p, t) -> null;
                fireMode = new FireMode(FireMode.FireModeName.MAIN, 1, new AmmoPack(0, 0, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);

                effect = createEffect(2, 0);
                targetFinder = p -> {
                    List<List<Player>> targets = new ArrayList<>();
                    for (String s : new ArrayList<String>(Arrays.asList("right", "left", "up", "down"))) {
                        List<List<Player>> single = Board.getInstance().getSquaresInLineIgnoringWalls(p.getPosition(), s)
                                .stream()
                                .map(x -> x.getPlayers())
                                .flatMap(x -> x.stream())
                                .distinct()
                                .map(x -> Arrays.asList(x))
                                .collect(Collectors.toList());
                        targets.addAll(single);
                        targets.addAll(cartesian(single, single));
                    }
                    return targets;
                };
                destinationFinder = (p, t) -> null;
                fireMode = new FireMode(FireMode.FireModeName.SECONDARY, 2, new AmmoPack(0, 0, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);
                break;

            case CYBERBLADE:    //Spada fotonica

                color = YELLOW;
                fullCost = new AmmoPack(1, 0, 1);
                reducedCost = new AmmoPack(1, 0, 0);
                fireModeList = new ArrayList<>(3);


                effect = createEffect(2, 0);
                targetFinder = p -> p.getPosition().getPlayers().stream()
                        .distinct()
                        .filter(x -> !x.equals(p))
                        .map(x -> Arrays.asList(x))
                        .collect(Collectors.toList());
                destinationFinder = (p, t) -> null;
                fireMode = new FireMode(FireMode.FireModeName.MAIN, 1, new AmmoPack(0, 0, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);

                effect = (shooter, target, destination) -> shooter.setPosition(destination);
                targetFinder = p -> Arrays.asList(Arrays.asList(p));
                destinationFinder = (p, t) -> Board.getInstance().getReachable(p.getPosition(), 1);
                fireMode = new FireMode(FireMode.FireModeName.OPTION1, 1, new AmmoPack(0, 1, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);

                effect = createEffect(2, 0);
                targetFinder = p -> p.getPosition().getPlayers().stream()
                        .distinct()
                        .filter(x -> !x.equals(p))
                        .filter(x -> !p.getMainTargets().contains(x))
                        .map(x -> Arrays.asList(x))
                        .collect(Collectors.toList());
                destinationFinder = (p, t) -> null;
                fireMode = new FireMode(FireMode.FireModeName.OPTION2, 1, new AmmoPack(0, 0, 1), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);
                break;

            case ZX2:    //Spada fotonica

                color = YELLOW;
                fullCost = new AmmoPack(1, 0, 1);
                reducedCost = new AmmoPack(1, 0, 0);
                fireModeList = new ArrayList<>(2);

                effect = createEffect(1, 2);
                targetFinder = p -> Board.getInstance().getVisible(p.getPosition()).stream()
                        .map(x -> x.getPlayers())
                        .flatMap(x -> x.stream())
                        .distinct()
                        .map(x -> Arrays.asList(x))
                        .collect(Collectors.toList());
                destinationFinder = (p, t) -> null;
                fireMode = new FireMode(FireMode.FireModeName.MAIN, 1, new AmmoPack(0, 0, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);

                effect = createEffect(0, 1);
                targetFinder = p -> {
                    List<List<Player>> targets = new ArrayList<>();
                    List<List<Player>> single = Board.getInstance().getVisible(p.getPosition()).stream()
                            .map(x -> x.getPlayers())
                            .flatMap(x -> x.stream())
                            .distinct()
                            .map(x -> Arrays.asList(x))
                            .collect(Collectors.toList());
                    targets.addAll(single);
                    targets.addAll(cartesian(single, single));
                    targets.addAll(cartesian(cartesian(single, single), single));
                    return targets;
                };
                destinationFinder = (p, t) -> null;
                fireMode = new FireMode(FireMode.FireModeName.SECONDARY, 3, new AmmoPack(0, 0, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);
                break;

            case SHOTGUN:    //Fucile a pompa

                color = YELLOW;
                fullCost = new AmmoPack(0, 0, 2);
                reducedCost = new AmmoPack(0, 0, 1);
                fireModeList = new ArrayList<>(2);

                effect = (shooter, target, destination) -> {
                    target.sufferDamage(3, shooter);
                    target.setPosition(destination);
                };
                targetFinder = p ->p.getPosition().getPlayers().stream()
                        .distinct()
                        .map(x -> Arrays.asList(x))
                        .collect(Collectors.toList());
                destinationFinder = (p, t) -> {
                    List<Square> destination = Board.getInstance().getReachable(p.getPosition(), 1);
                    destination.add(p.getPosition());
                    return destination;
                };
                fireMode = new FireMode(FireMode.FireModeName.MAIN, 1, new AmmoPack(0, 0, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);

                effect = createEffect(2, 0);
                targetFinder = p -> Board.getInstance().getReachable(p.getPosition(), 1).stream()
                        .map(x -> x.getPlayers())
                        .flatMap(x -> x.stream())
                        .distinct()
                        .map(x -> Arrays.asList(x))
                        .collect(Collectors.toCollection(ArrayList::new));
                destinationFinder = (p, t) -> null;
                fireMode = new FireMode(FireMode.FireModeName.SECONDARY, 1, new AmmoPack(0, 0, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);
                break;

            case POWER_GLOVE:    //Cyberguanto

                color = BLUE;
                fullCost = new AmmoPack(0, 1, 1);
                reducedCost = new AmmoPack(0, 1, 0);
                fireModeList = new ArrayList<>(2);

                effect = (shooter, target, destination) -> {
                    shooter.setPosition(target.getPosition());
                    target.sufferDamage(1, shooter);
                    target.addMarks(2, shooter);
                };
                targetFinder = p -> Board.getInstance().getReachable(p.getPosition(), 1).stream()
                        .map(x -> x.getPlayers())
                        .flatMap(x -> x.stream())
                        .distinct()
                        .map(x -> Arrays.asList(x))
                        .collect(Collectors.toList());
                destinationFinder = (p, t) -> t.isEmpty() ? Arrays.asList(p.getPosition()) : Arrays.asList(t.get(0).getPosition());
                fireMode = new FireMode(FireMode.FireModeName.MAIN, 1, new AmmoPack(0, 0, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);

                effect = (shooter, target, destination) -> {
                    shooter.setPosition(destination);
                    target.sufferDamage(2, shooter);
                };
                targetFinder = p -> {
                    List<List<Player>> targets = new ArrayList<>();
                    for (String s : new ArrayList<String>(Arrays.asList("right", "left", "up", "down"))) {
                        List<List<Player>> close = Board.getInstance().getSquaresInLineIgnoringWalls(p.getPosition(), s)
                                .stream()
                                .filter(x -> Board.getInstance().getReachable(p.getPosition(), 1).contains(x))
                                .map(x -> x.getPlayers())
                                .flatMap(x -> x.stream())
                                .distinct()
                                .map(x -> Arrays.asList(x))
                                .collect(Collectors.toList());
                        List<List<Player>> far = Board.getInstance().getSquaresInLineIgnoringWalls(p.getPosition(), s)
                                .stream()
                                .filter(x -> Board.getInstance().getReachable(p.getPosition(), 2).contains(x))
                                .filter(x -> !Board.getInstance().getReachable(p.getPosition(), 1).contains(x))
                                .map(x -> x.getPlayers())
                                .flatMap(x -> x.stream())
                                .distinct()
                                .map(x -> Arrays.asList(x))
                                .collect(Collectors.toList());
                        targets.addAll(close);
                        targets.addAll(far);
                        targets.addAll(cartesian(close, far));
                    }
                    return targets;
                };
                destinationFinder = (p, t) -> {
                    for (Player temp : t) {
                        if (Board.getInstance().getDistance(p.getPosition(), temp.getPosition()) > 1) {
                            return Arrays.asList(temp.getPosition());
                        }
                    }
                    List<Square> res = new ArrayList<>();
                    res.add(t.get(0).getPosition());
                    for (String s : new ArrayList<String>(Arrays.asList("right", "left", "up", "down"))) {
                        if (Board.getInstance().getSquaresInLine(p.getPosition(), s).contains(t.get(0).getPosition())) {
                            for (Square sq : Board.getInstance().getSquaresInLine(p.getPosition(), s)) {
                                if (Board.getInstance().getDistance(sq, p.getPosition()) == 2) {
                                    res.add(sq);
                                }
                            }
                        }

                    }
                    return res;

                };
                fireMode = new FireMode(FireMode.FireModeName.OPTION1, 1, new AmmoPack(0, 1, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);
                break;

            case SCHOCKWAVE:    //Onda d'urto

                color = YELLOW;
                fullCost = new AmmoPack(0, 0, 1);
                reducedCost = new AmmoPack(0, 0, 0);
                fireModeList = new ArrayList<>(2);

                effect = createEffect(1, 0);
                targetFinder = p -> {
                    List<List<Player>> targets = new ArrayList<>();
                    List<List<List<Player>>> directionalTargets = new ArrayList<>();

                    for (String s : new ArrayList<String>(Arrays.asList("right", "left", "up", "down"))) {
                        directionalTargets.add(Board.getInstance().getReachable(p.getPosition(), 1).stream()
                                .filter(x -> Board.getInstance().getSquaresInLine(p.getPosition(), s).contains(x))
                                .map(x -> x.getPlayers())
                                .flatMap(x -> x.stream())
                                .distinct()
                                .map(x -> Arrays.asList(x))
                                .collect(Collectors.toList()));
                    }
                    for (int i = 0; i < directionalTargets.size(); i++) {
                        targets.addAll(directionalTargets.get(i));
                        for (int j = i + 1; j < directionalTargets.size(); j++) {
                            targets.addAll(cartesian(directionalTargets.get(i), directionalTargets.get(j)));
                            for (int k = j + 1; k < directionalTargets.size(); k++) {
                                targets.addAll(cartesian(cartesian(directionalTargets.get(i), directionalTargets.get(j)), directionalTargets.get(k)));
                            }
                        }
                    }
                    return targets;
                };
                destinationFinder = (p, t) -> null;
                fireMode = new FireMode(FireMode.FireModeName.MAIN, 3, new AmmoPack(0, 0, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);

                effect = createEffect(1, 0);
                targetFinder = p -> Arrays.asList(Board.getInstance().getReachable(p.getPosition(), 1).stream()
                        .map(x -> x.getPlayers())
                        .flatMap(x -> x.stream())
                        .distinct()
                        .collect(Collectors.toList()));
                destinationFinder = (p, t) -> null;
                fireMode = new FireMode (FireMode.FireModeName.SECONDARY, 4, new AmmoPack(0, 0, 1), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);
                break;

            case SLEDGEHAMMER:    //Martello ionico

                color = BLUE;
                fullCost = new AmmoPack(0, 0, 1);
                reducedCost = new AmmoPack(0, 0, 0);
                fireModeList = new ArrayList<>(2);

                effect = createEffect(2, 0);
                targetFinder = p -> p.getPosition().getPlayers().stream()
                        .distinct()
                        .filter(x -> !x.equals(p))
                        .map(x -> Arrays.asList(x))
                        .collect(Collectors.toList());
                destinationFinder = (p, t) -> null;
                fireMode = new FireMode(FireMode.FireModeName.MAIN, 1, new AmmoPack(0, 0, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);

                effect = (shooter, target, destination) -> {target.sufferDamage(3, shooter); target.setPosition(destination);};
                targetFinder = p -> p.getPosition().getPlayers().stream()
                        .distinct()
                        .filter(x -> !x.equals(p))
                        .map(x -> Arrays.asList(x))
                        .collect(Collectors.toList());
                destinationFinder = (p, t) -> {
                    if(t.isEmpty()){
                        return Arrays.asList(p.getPosition());
                    }
                    List<Square> res = new ArrayList<>();
                    Square center = t.get(0).getPosition();
                    res.add(center);
                    for (String s : new ArrayList<String>(Arrays.asList("right", "left", "up", "down"))) {
                        res.addAll(Board.getInstance().getSquaresInLine(center, s).stream()
                                .filter(x->Board.getInstance().getDistance(center, x)<3)
                                .collect(Collectors.toList()));
                    }
                    return res;
                };
                fireMode = new FireMode (FireMode.FireModeName.SECONDARY, 1, new AmmoPack(1, 0, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);
                break;

            default:

                color = BLUE;
                fullCost = new AmmoPack(1, 1, 1);
                reducedCost = new AmmoPack(0, 1, 1);
                fireModeList = new ArrayList<>(1);

                effect = createEffect(1, 0);
                targetFinder = p -> Board.getInstance().getVisible(p.getPosition()).stream()
                        .map(x -> x.getPlayers())
                        .flatMap(x -> x.stream())
                        .distinct()
                        .map(x -> Arrays.asList(x))
                        .collect(Collectors.toList());
                destinationFinder = (p, t) -> null;
                fireMode = new FireMode(FireMode.FireModeName.MAIN, 1, new AmmoPack(0, 0, 0), destinationFinder, targetFinder, effect);
                fireModeList.add(fireMode);
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

    private static List<List<Player>> cartesian (List<List<Player>> a, List<List<Player>> b){
        List<Player> atemp = a.stream().map(x->x.get(0)).collect(Collectors.toList());
        List<Player> btemp = b.stream().map(x->x.get(0)).collect(Collectors.toList());
        List<List<Player>> res = new ArrayList<>();
        for(Player p1 : atemp){
            for (Player p2 : btemp){
                if(!p1.equals(p2)){
                    List<Player> restemp = new ArrayList<>();
                    restemp.add(p1);
                    restemp.add(p2);
                    res.add(restemp);
                }
            }
        }
        return res;
    }
}