//TODO check whether this factory and Board methods share the same logic (eg. does getVisible return the shooter's square?) for destinations and the second part of targets
//TODO check whether returning a List of Players instead of an ArrayList can cause issues
//TODO load data from JSON/XML: NB: loading data solves the cognitive complexity problem
//      every "getter" should read a JSON or XML file importing the hashmap instead of creating it. Check out serializable interfaces.
//TODO properly comment methods
//TODO testing

package it.polimi.ingsw.model;

import java.util.*;
import java.util.stream.*;

import static it.polimi.ingsw.model.Color.*;
import static it.polimi.ingsw.model.FireMode.FireModeName.*;
import static it.polimi.ingsw.model.Weapon.WeaponName.*;
import static it.polimi.ingsw.model.Board.Direction;

/**
 * Factory class to create a weapon.
 *
 * @author  marcobaga
 */

public class WeaponFactory {

    /**
     *Private constructor
     *
     * @return      a WeaponFactory
     */
    private WeaponFactory(){}

    /**
     *Creates a Weapon object according to its name
     *
     * @param  weaponName  the name of the weapon to be created
     * @return      the Weapon object created
     */
    public static Weapon createWeapon(Weapon.WeaponName weaponName) {

        FireMode fireMode;
        TargetFinder targetFinder;
        DestinationFinder destinationFinder;
        Effect effect;

        Color color = getColor(weaponName);
        AmmoPack fullCost = getFullCost(weaponName);
        AmmoPack reducedCost = getReducedCost(weaponName);
        List<FireMode.FireModeName> nameList = getNameList(weaponName);
        List<FireMode> fireModeList = new ArrayList<>(nameList.size());
        int targetNumber;
        AmmoPack fireModeCost;

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
        return weapon;
    }


    private static Color getColor(Weapon.WeaponName weaponName) {
        if(Arrays.asList(LOCK_RIFLE, MACHINE_GUN, THOR, PLASMA_GUN, WHISPER, ELECTROSCYTHE, TRACTOR_BEAM).contains(weaponName)) {
            return BLUE;
        }else if(Arrays.asList(VORTEX_CANNON, FURNACE,HEATSEEKER, HELLION, FLAMETHROWER, GRENADE_LAUNCHER, ROCKET_LAUNCHER).contains(weaponName)) {
            return RED;
        }
        return YELLOW;
    }

    private static AmmoPack getFullCost(Weapon.WeaponName weaponName) {
        switch (weaponName) {
            case LOCK_RIFLE:    //Distruttore
                return new AmmoPack(0, 2, 0);
            case MACHINE_GUN:    //Mitragliatrice
                return new AmmoPack(1, 1, 0);
            case THOR:    //Torpedine
                return new AmmoPack(1, 1, 0);
            case PLASMA_GUN:    //Fucile al plasma
                return new AmmoPack(0, 1, 1);
            case WHISPER:
                return new AmmoPack(0, 2, 1);
            case ELECTROSCYTHE:
                return new AmmoPack(0, 1, 0);
            case TRACTOR_BEAM:
                return new AmmoPack(0, 1, 0);
            case VORTEX_CANNON:
                return new AmmoPack(1, 1, 0);
            case FURNACE:
                return new AmmoPack(1, 1, 0);
            case HEATSEEKER:
                return new AmmoPack(2, 0, 1);
            case HELLION:
                return new AmmoPack(1, 0, 1);
            case FLAMETHROWER:
                return new AmmoPack(1, 0, 0);
            case GRENADE_LAUNCHER:
                return new AmmoPack(1, 0, 0);
            case ROCKET_LAUNCHER:
                return new AmmoPack(2, 0, 0);
            case RAILGUN:
                return new AmmoPack(0, 1, 2);
            case CYBERBLADE:
                return new AmmoPack(1, 0, 1);
            case ZX2:
                return new AmmoPack(1, 0, 1);
            case SHOTGUN:
                return new AmmoPack(0, 0, 2);
            case POWER_GLOVE:
                return new AmmoPack(0, 1, 1);
            case SHOCKWAVE:
                return new AmmoPack(0, 0, 1);
            case SLEDGEHAMMER:
                return new AmmoPack(0, 0, 1);
            default:
                return new AmmoPack(0, 0, 0);
        }
    }

    private static AmmoPack getReducedCost(Weapon.WeaponName weaponName) {
        AmmoPack res = getFullCost(weaponName);
        Color c = getColor(weaponName);
        if (c == RED ) {
            res.subAmmoPack(new AmmoPack(1,0,0));
        }else if (c==BLUE) {
            res.subAmmoPack(new AmmoPack(0,1,0));
        }else {
            res.subAmmoPack(new AmmoPack(0,0,1));
        }
        return res;
    }

    private static List<FireMode.FireModeName> getNameList(Weapon.WeaponName weaponName) {
        if (Arrays.asList(WHISPER, HEATSEEKER).contains(weaponName)) {
            return Arrays.asList(MAIN);
        } else if (Arrays.asList(ELECTROSCYTHE, TRACTOR_BEAM, FURNACE, HELLION, FLAMETHROWER, RAILGUN, ZX2, SHOTGUN, POWER_GLOVE, SHOCKWAVE, SLEDGEHAMMER).contains(weaponName)) {
            return new ArrayList<>(Arrays.asList(MAIN, SECONDARY));
        } else if (Arrays.asList(LOCK_RIFLE,VORTEX_CANNON,GRENADE_LAUNCHER).contains(weaponName)) {
            return new ArrayList<>(Arrays.asList(MAIN, OPTION1));
        } else if (Arrays.asList(MACHINE_GUN, THOR, PLASMA_GUN, ROCKET_LAUNCHER, CYBERBLADE).contains(weaponName)) {
            return new ArrayList<>(Arrays.asList(MAIN, OPTION1, OPTION2));
        } else {
            return new ArrayList<>(Arrays.asList(MAIN));
        }
    }

    private static Effect getEffect(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {
        Map<Weapon.WeaponName, Map> fireModeMap = new EnumMap<>(Weapon.WeaponName.class);

        Map<FireMode.FireModeName, Effect> effectMap = new EnumMap<>(FireMode.FireModeName.class);
        effectMap.put(MAIN, createEffect(2,1));
        effectMap.put(OPTION1, createEffect(1,0));
        fireModeMap.put(LOCK_RIFLE, effectMap);

        effectMap = new EnumMap<>(FireMode.FireModeName.class);
        effectMap.put(MAIN, createEffect(1,0));
        effectMap.put(OPTION1, createEffect(1,0));
        effectMap.put(OPTION2, createEffect(1,0));
        fireModeMap.put(MACHINE_GUN, effectMap);

        effectMap = new EnumMap<>(FireMode.FireModeName.class);
        effectMap.put(MAIN, createEffect(2,0));
        effectMap.put(OPTION1, createEffect(1,0));
        effectMap.put(OPTION2, createEffect(2,0));
        fireModeMap.put(THOR, effectMap);

        effectMap = new EnumMap<>(FireMode.FireModeName.class);
        effectMap.put(MAIN, createEffect(2,0));
        effectMap.put(OPTION1, ((shooter, target, destination) -> target.setPosition(destination)));
        effectMap.put(OPTION2, createEffect(1,0));
        fireModeMap.put(PLASMA_GUN, effectMap);

        effectMap = new EnumMap<>(FireMode.FireModeName.class);
        effectMap.put(MAIN, createEffect(3,1));
        fireModeMap.put(WHISPER, effectMap);

        effectMap = new EnumMap<>(FireMode.FireModeName.class);
        effectMap.put(MAIN, createEffect(1,0));
        effectMap.put(SECONDARY, createEffect(2,0));
        fireModeMap.put(ELECTROSCYTHE, effectMap);

        effectMap = new EnumMap<>(FireMode.FireModeName.class);
        effectMap.put(MAIN, ((shooter, target, destination) -> {
            target.setPosition(destination);
            target.sufferDamage(1, shooter);
        }));
        effectMap.put(SECONDARY, ((shooter, target, destination) -> {
            target.setPosition(destination);
            target.sufferDamage(3, shooter);
        }));
        fireModeMap.put(TRACTOR_BEAM, effectMap);

        effectMap = new EnumMap<>(FireMode.FireModeName.class);
        effectMap.put(MAIN, ((shooter, target, destination) -> {
            target.setPosition(destination);
            target.sufferDamage(2, shooter);
        }));
        effectMap.put(OPTION1, ((shooter, target, destination) -> {
            target.setPosition(destination);
            target.sufferDamage(1, shooter);
        }));
        fireModeMap.put(VORTEX_CANNON, effectMap);

        effectMap = new EnumMap<>(FireMode.FireModeName.class);
        effectMap.put(MAIN, createEffect(1,0));
        effectMap.put(OPTION1, createEffect(1,1));
        fireModeMap.put(FURNACE, effectMap);

        effectMap = new EnumMap<>(FireMode.FireModeName.class);
        effectMap.put(MAIN, createEffect(3,0));
        fireModeMap.put(HEATSEEKER, effectMap);

        effectMap = new EnumMap<>(FireMode.FireModeName.class);
        effectMap.put(MAIN, ((shooter, target, destination) -> {
            target.sufferDamage(1, shooter);
            Board.getInstance().getPlayersInside(target.getPosition()).forEach(x -> x.addMarks(1, shooter));
        }));
        effectMap.put(SECONDARY, ((shooter, target, destination) -> {
            target.sufferDamage(1, shooter);
            Board.getInstance().getPlayersInside(target.getPosition()).forEach(x -> x.addMarks(2, shooter));
        }));
        fireModeMap.put(HELLION, effectMap);

        effectMap = new EnumMap<>(FireMode.FireModeName.class);
        effectMap.put(MAIN, createEffect(1,0));
        effectMap.put(SECONDARY, ((shooter, target, destination) -> {
            if (Board.getInstance().getReachable(shooter.getPosition(), 1).contains(target.getPosition())) {
                target.sufferDamage(2, shooter);
            } else target.sufferDamage(1, shooter);
        }));
        fireModeMap.put(FLAMETHROWER, effectMap);

        effectMap = new EnumMap<>(FireMode.FireModeName.class);
        effectMap.put(MAIN, ((shooter, target, destination) -> {
            target.sufferDamage(1, shooter);
            target.setPosition(destination);
        }));
        effectMap.put(OPTION1, createEffect(1,0));
        fireModeMap.put(GRENADE_LAUNCHER, effectMap);

        effectMap = new EnumMap<>(FireMode.FireModeName.class);
        effectMap.put(MAIN, ((shooter, target, destination) -> {
            target.sufferDamage(2, shooter);
            target.setPosition(destination);
        }));
        effectMap.put(OPTION1, ((shooter, target, destination) -> target.setPosition(destination)));
        effectMap.put(OPTION2, createEffect(1,0));
        fireModeMap.put(ROCKET_LAUNCHER, effectMap);

        effectMap = new EnumMap<>(FireMode.FireModeName.class);
        effectMap.put(MAIN, createEffect(3,0));
        effectMap.put(SECONDARY, createEffect(2,0));
        fireModeMap.put(RAILGUN, effectMap);

        effectMap = new EnumMap<>(FireMode.FireModeName.class);
        effectMap.put(MAIN, createEffect(2,0));
        effectMap.put(OPTION1, ((shooter, target, destination) -> target.setPosition(destination)));
        effectMap.put(OPTION2, createEffect(2,0));
        fireModeMap.put(CYBERBLADE, effectMap);

        effectMap = new EnumMap<>(FireMode.FireModeName.class);
        effectMap.put(MAIN, createEffect(1,2));
        effectMap.put(SECONDARY, createEffect(0,1));
        fireModeMap.put(ZX2, effectMap);

        effectMap = new EnumMap<>(FireMode.FireModeName.class);
        effectMap.put(MAIN, ((shooter, target, destination) -> {
            target.sufferDamage(3, shooter);
            target.setPosition(destination);
        }));
        effectMap.put(SECONDARY, createEffect(2,0));
        fireModeMap.put(SHOTGUN, effectMap);

        effectMap = new EnumMap<>(FireMode.FireModeName.class);
        effectMap.put(MAIN, ((shooter, target, destination) -> {
            shooter.setPosition(target.getPosition());
            target.sufferDamage(1, shooter);
            target.addMarks(2, shooter);
        }));
        effectMap.put(SECONDARY, ((shooter, target, destination) -> {
            shooter.setPosition(destination);
            target.sufferDamage(2, shooter);
        }));
        fireModeMap.put(POWER_GLOVE, effectMap);

        effectMap = new EnumMap<>(FireMode.FireModeName.class);
        effectMap.put(MAIN, createEffect(1,0));
        effectMap.put(SECONDARY, createEffect(1,0));
        fireModeMap.put(SHOCKWAVE, effectMap);

        effectMap = new EnumMap<>(FireMode.FireModeName.class);
        effectMap.put(MAIN, createEffect(2,0));
        effectMap.put(SECONDARY, ((shooter, target, destination) -> {
            target.sufferDamage(3, shooter);
            target.setPosition(destination);}));
        fireModeMap.put(SLEDGEHAMMER, effectMap);


        return (Effect)fireModeMap.get(weaponName).get(fireModeName);
    }

    private static TargetFinder getTargetFinder(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {
        Map<Weapon.WeaponName, Map> fireModeMap = new EnumMap<>(Weapon.WeaponName.class);


        Map<FireMode.FireModeName, TargetFinder> targetMap = new EnumMap<>(FireMode.FireModeName.class);
        targetMap.put(MAIN, p -> Board.getInstance().getVisible(p.getPosition()).stream()
                .map(Square::getPlayers)
                .flatMap(x -> x.stream())
                .distinct()
                .filter( x -> !x.equals(p))
                .map(Arrays::asList)
                .collect(Collectors.toList()));
        targetMap.put(OPTION1, p -> (Board.getInstance().getVisible(p.getPosition()).stream()
                .map(Square::getPlayers)
                .flatMap(x -> x.stream())
                .distinct()
                .filter( x -> !x.equals(p))
                .filter(x -> !p.getMainTargets().contains(x))
                .map(Arrays::asList)
                .collect(Collectors.toList())));
        fireModeMap.put(LOCK_RIFLE, targetMap);


        targetMap = new EnumMap<>(FireMode.FireModeName.class);
        targetMap.put(MAIN, p -> {
            List<List<Player>> res = Board.getInstance().getVisible(p.getPosition()).stream()
                    .map(Square::getPlayers)
                    .flatMap(x -> x.stream())
                    .distinct()
                    .filter( x -> !x.equals(p))
                    .map(Arrays::asList)
                    .collect(Collectors.toList());
            res.addAll(cartesian(res, res));
            return res;
        });
        targetMap.put(OPTION1, p -> (p.getMainTargets().stream()
                .distinct()
                .filter(x -> p.getMainTargets().contains(x))
                .filter(x -> !p.getOptionalTargets().contains(x))
                .map(Arrays::asList)
                .collect(Collectors.toList())));
        targetMap.put(OPTION2, p -> {
            List<List<Player>> pastTargets = p.getMainTargets().stream()
                    .distinct()
                    .filter(x -> !p.getOptionalTargets().contains(x))
                    .map(Arrays::asList)
                    .collect(Collectors.toList());
            List<List<Player>> others = Board.getInstance().getVisible(p.getPosition()).stream()
                    .map(Square::getPlayers)
                    .flatMap(x->x.stream())
                    .distinct()
                    .filter(x -> !x.equals(p))
                    .filter(x -> !(p.getMainTargets().contains(x) || p.getOptionalTargets().contains(x)))
                    .map(Arrays::asList)
                    .collect(Collectors.toList());
            others.addAll(cartesian(pastTargets, others));
            others.addAll(pastTargets);
            return others;
        });
        fireModeMap.put(MACHINE_GUN, targetMap);


        targetMap = new EnumMap<>(FireMode.FireModeName.class);
        targetMap.put(MAIN, p -> Board.getInstance().getVisible(p.getPosition()).stream()
                .map(Square::getPlayers)
                .flatMap(x -> x.stream())
                .distinct()
                .filter(x -> !x.equals(p))
                .map(Arrays::asList)
                .collect(Collectors.toList()));
        targetMap.put(OPTION1, p -> (p.getMainTargets().isEmpty()) ?
                new ArrayList<>() : Board.getInstance().getVisible(p.getMainTargets().get(0).getPosition()).stream()
                .map(Square::getPlayers)
                .flatMap(x -> x.stream())
                .distinct()
                .filter(x -> !p.getMainTargets().contains(x))
                .filter(x -> !x.equals(p))
                .map(Arrays::asList)
                .collect(Collectors.toList()));
        targetMap.put(OPTION2, p -> (p.getMainTargets().isEmpty()||p.getOptionalTargets().isEmpty()) ?
                new ArrayList<>() : Board.getInstance().getVisible(p.getOptionalTargets().get(0).getPosition()).stream()
                .map(Square::getPlayers)
                .flatMap(x -> x.stream())
                .distinct()
                .filter(x -> !(p.getMainTargets().contains(x) || p.getOptionalTargets().contains(x)))
                .filter(x -> !x.equals(p))
                .map(Arrays::asList)
                .collect(Collectors.toList()));
        fireModeMap.put(THOR, targetMap);


        targetMap = new EnumMap<>(FireMode.FireModeName.class);
        targetMap.put(MAIN, p -> Board.getInstance().getVisible(p.getPosition()).stream()
                .map(Square::getPlayers)
                .flatMap(x -> x.stream())
                .distinct()
                .filter(x -> !x.equals(p))
                .map(Arrays::asList)
                .collect(Collectors.toList()));
        targetMap.put(OPTION1, p -> Arrays.asList(Arrays.asList(p)));
        targetMap.put(OPTION2, p -> (p.getMainTargets().stream()
                .filter(x -> !p.getOptionalTargets().contains(x))
                .distinct()
                .map(Arrays::asList)
                .collect(Collectors.toList())));
        fireModeMap.put(PLASMA_GUN, targetMap);


        targetMap = new EnumMap<>(FireMode.FireModeName.class);
        targetMap.put(MAIN, p -> Board.getInstance().getVisible(p.getPosition()).stream()
                .map(Square::getPlayers)
                .flatMap(x -> x.stream())
                .distinct()
                .filter(x -> !x.equals(p))
                .filter(x -> {
                    try {
                        return Board.getInstance().getDistance(x.getPosition(), p.getPosition()) >= 2;
                    } catch (NotAvailableAttributeException e) {
                        throw new IllegalArgumentException("Some players do not have a position.");
                    }
                })
                .map(Arrays::asList)
                .collect(Collectors.toList()));
        fireModeMap.put(WHISPER, targetMap);


        targetMap = new EnumMap<>(FireMode.FireModeName.class);
        targetMap.put(MAIN, p -> Arrays.asList(p.getPosition().getPlayers().stream()
                .distinct()
                .filter(x -> (!x.equals(p)))
                .collect(Collectors.toList())));
        targetMap.put(SECONDARY, p -> Arrays.asList(p.getPosition().getPlayers().stream()
                .distinct()
                .filter(x -> (!x.equals(p)))
                .collect(Collectors.toList())));
        fireModeMap.put(ELECTROSCYTHE, targetMap);


        targetMap = new EnumMap<>(FireMode.FireModeName.class);
        targetMap.put(MAIN, p -> {
            List<Square> l = Board.getInstance().getVisible(p.getPosition());
            List<Square> temp = new ArrayList<>();
            for (Square s : l) {
                temp.addAll(Board.getInstance().getReachable(s, 2));
            }
            l.addAll(temp);
            return l.stream()
                    .distinct()
                    .map(Square::getPlayers)
                    .flatMap(x -> x.stream())
                    .distinct()
                    .filter(x -> !x.equals(p))
                    .map(Arrays::asList)
                    .collect(Collectors.toList());
        });
        targetMap.put(SECONDARY, p -> Board.getInstance().getReachable(p.getPosition(), 2).stream()
                .map(Square::getPlayers)
                .flatMap(x -> x.stream())
                .distinct()
                .filter(x -> !x.equals(p))
                .map(Arrays::asList)
                .collect(Collectors.toList())
        );
        fireModeMap.put(TRACTOR_BEAM, targetMap);


        targetMap = new EnumMap<>(FireMode.FireModeName.class);
        targetMap.put(MAIN, p -> {
            List<Square> l = Board.getInstance().getVisible(p.getPosition());
            List<Square> temp = new ArrayList<>();
            for (Square s : l) {
                temp.addAll(Board.getInstance().getReachable(s, 1));
            }
            l.addAll(temp);
            return l.stream()
                    .map(Square::getPlayers)
                    .flatMap(x -> x.stream())
                    .distinct()
                    .filter(x -> !x.equals(p))
                    .map(Arrays::asList)
                    .collect(Collectors.toList());
        });
        targetMap.put(OPTION1, p -> {
            if (p.getMainTargets().isEmpty()) {
                return new ArrayList<>();
            }
            List<List<Player>> lp = Board.getInstance().getReachable(p.getMainTargets().get(0).getPosition(), 1).stream()
                    .map(Square::getPlayers)
                    .flatMap(x -> x.stream())
                    .distinct()
                    .filter(x -> !x.equals(p))
                    .filter(x -> !p.getMainTargets().contains(x))
                    .map(Arrays::asList)
                    .collect(Collectors.toList());
            List<List<Player>> res = cartesian(lp, lp);
            res.addAll(lp);
            return res;
        });
        fireModeMap.put(VORTEX_CANNON, targetMap);


        targetMap = new EnumMap<>(FireMode.FireModeName.class);
        targetMap.put(MAIN, p -> {
            List<List<Square>> roomList = Board.getInstance().getVisible(p.getPosition()).stream()
                    .map(Square::getRoomId)
                    .distinct()
                    .filter(x -> {
                        try {
                            return x!=p.getPosition().getRoomId();
                        } catch (NotAvailableAttributeException e) {
                            throw new IllegalArgumentException("Some players do not have a position.");
                        }
                    })
                    .map(x -> Board.getInstance().getSquaresInRoom(x))
                    .collect(Collectors.toList());
            List<List<Player>> res = new ArrayList<>();
            List<Player> temp = new ArrayList<>();
            for (List<Square> ls : roomList) {
                for (Square s : ls) {
                    temp.addAll(s.getPlayers());
                }
                res.add(temp);
                temp = new ArrayList<>();
            }
            return res;
        });
        targetMap.put(SECONDARY, p -> Board.getInstance().getReachable(p.getPosition(), 1).stream()
                .filter(x -> {
                    try {
                        return !x.equals(p.getPosition());
                    } catch (NotAvailableAttributeException e) {
                        throw new IllegalArgumentException("Some players do not have a position.");
                    }
                })
                .map(Square::getPlayers)
                .collect(Collectors.toList()));
        fireModeMap.put(FURNACE, targetMap);


        targetMap = new EnumMap<>(FireMode.FireModeName.class);
        targetMap.put(MAIN, p -> Board.getInstance().getMap().stream()
                .filter(x -> {
                    try {
                        return !Board.getInstance().getVisible(p.getPosition()).contains(x);
                    } catch (NotAvailableAttributeException e) {
                        throw new IllegalArgumentException("Some players do not have a position.");
                    }
                })
                .map(Square::getPlayers)
                .flatMap(x -> x.stream())
                .distinct()
                .filter(x -> x.equals(p))
                .map(Arrays::asList)
                .collect(Collectors.toList())
        );
        fireModeMap.put(HEATSEEKER, targetMap);

        targetMap = new EnumMap<>(FireMode.FireModeName.class);
        targetMap.put(MAIN, p -> Board.getInstance().getVisible(p.getPosition()).stream()
                .filter(x -> !x.containsPlayer(p))
                .map(Square::getPlayers)
                .flatMap(x -> x.stream())
                .distinct()
                .map(Arrays::asList)
                .collect(Collectors.toList()));
        targetMap.put(SECONDARY, p -> Board.getInstance().getVisible(p.getPosition()).stream()
                .filter(x -> !x.containsPlayer(p))
                .map(Square::getPlayers)
                .flatMap(x -> x.stream())
                .distinct()
                .map(Arrays::asList)
                .collect(Collectors.toList()));
        fireModeMap.put(HELLION, targetMap);

        targetMap = new EnumMap<>(FireMode.FireModeName.class);
        targetMap.put(MAIN, p -> {
            List<List<Player>> targets = new ArrayList<>();
            for (Direction d : Direction.values()) {
                List<List<Player>> close = Board.getInstance().getSquaresInLine(p.getPosition(), d).stream()
                        .filter(x -> {
                            try {
                                return Board.getInstance().getReachable(p.getPosition(), 1).contains(x);
                            } catch (NotAvailableAttributeException e) {
                                throw new IllegalArgumentException("Some players do not have a position.");
                            }
                        })
                        .map(Square::getPlayers)
                        .flatMap(x -> x.stream())
                        .distinct()
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
                List<List<Player>> far = Board.getInstance().getSquaresInLine(p.getPosition(), d).stream()
                        .filter(x -> {
                            try {
                                return Board.getInstance().getReachable(p.getPosition(), 2).contains(x);
                            } catch (NotAvailableAttributeException e) {
                                throw new IllegalArgumentException("Some players do not have a position.");
                            }
                        })
                        .filter(x -> {
                            try {
                                return !Board.getInstance().getReachable(p.getPosition(), 1).contains(x);
                            } catch (NotAvailableAttributeException e) {
                                throw new IllegalArgumentException("Some players do not have a position.");
                            }
                        })
                        .map(Square::getPlayers)
                        .flatMap(x -> x.stream())
                        .distinct()
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
                targets.addAll(close);
                targets.addAll(far);
                targets.addAll(cartesian(close, far));
            }
            return targets;
        });
        targetMap.put(SECONDARY, p -> {
            List<List<Player>> targets = new ArrayList<>();
            for (Direction d : Direction.values()) {
                List<List<Player>> close = Board.getInstance().getSquaresInLine(p.getPosition(), d).stream()
                        .filter(x -> {
                            try {
                                return !Board.getInstance().getReachable(p.getPosition(), 1).contains(x);
                            } catch (NotAvailableAttributeException e) {
                                throw new IllegalArgumentException("Some players do not have a position.");
                            }
                        })
                        .map(Square::getPlayers)
                        .flatMap(x -> x.stream())
                        .distinct()
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
                List<List<Player>> far = Board.getInstance().getSquaresInLine(p.getPosition(), d).stream()
                        .filter(x -> {
                            try {
                                return Board.getInstance().getReachable(p.getPosition(), 2).contains(x);
                            } catch (NotAvailableAttributeException e) {
                                throw new IllegalArgumentException("Some players do not have a position.");
                            }
                        })
                        .filter(x -> {
                            try {
                                return !Board.getInstance().getReachable(p.getPosition(), 1).contains(x);
                            } catch (NotAvailableAttributeException e) {
                                throw new IllegalArgumentException("Some players do not have a position.");
                            }
                        })
                        .map(Square::getPlayers)
                        .flatMap(x -> x.stream())
                        .distinct()
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
                targets.addAll(close);
                targets.addAll(far);
                targets.addAll(cartesian(close, far));
            }
            return targets;
        });
        fireModeMap.put(FLAMETHROWER, targetMap);

        targetMap = new EnumMap<>(FireMode.FireModeName.class);
        targetMap.put(MAIN, p -> Board.getInstance().getVisible(p.getPosition()).stream()
                .map(Square::getPlayers)
                .flatMap(x -> x.stream())
                .distinct()
                .map(Arrays::asList)
                .collect(Collectors.toList())
        );
        targetMap.put(OPTION1, p -> Board.getInstance().getVisible(p.getPosition()).stream()
                .map(Square::getPlayers)
                .collect(Collectors.toCollection(ArrayList::new))
        );
        fireModeMap.put(GRENADE_LAUNCHER, targetMap);

        targetMap = new EnumMap<>(FireMode.FireModeName.class);
        targetMap.put(MAIN, p -> Board.getInstance().getVisible(p.getPosition()).stream()
                .filter(x -> {
                    try {
                        return !x.equals(p.getPosition());
                    } catch (NotAvailableAttributeException e) {
                        throw new IllegalArgumentException("Some players do not have a position.");
                    }
                })
                .map(Square::getPlayers)
                .flatMap(x -> x.stream())
                .distinct()
                .map(Arrays::asList)
                .collect(Collectors.toList())
        );
        targetMap.put(OPTION1, p -> Arrays.asList(Arrays.asList(p)));
        targetMap.put(OPTION2, p -> {
            List<Player> l = p.getMainTargets().get(0).getPreviousPosition().getPlayers();
            l.addAll(p.getMainTargets());
            return Arrays.asList(l);
        });
        fireModeMap.put(ROCKET_LAUNCHER, targetMap);

        targetMap = new EnumMap<>(FireMode.FireModeName.class);
        targetMap.put(MAIN, p -> {
            List<List<Player>> targets = new ArrayList<>();
            for (Direction d : Direction.values()){
                List<List<Player>> single = Board.getInstance().getSquaresInLineIgnoringWalls(p.getPosition(), d)
                        .stream()
                        .map(Square::getPlayers)
                        .flatMap(x -> x.stream())
                        .distinct()
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
                targets.addAll(single);
            }
            return targets;
        });
        targetMap.put(SECONDARY, p -> {
            List<List<Player>> targets = new ArrayList<>();
            for (Direction d : Direction.values()){
                List<List<Player>> single = Board.getInstance().getSquaresInLineIgnoringWalls(p.getPosition(), d)
                        .stream()
                        .map(Square::getPlayers)
                        .flatMap(x -> x.stream())
                        .distinct()
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
                targets.addAll(single);
                targets.addAll(cartesian(single, single));
            }
            return targets;
        });
        fireModeMap.put(RAILGUN, targetMap);

        targetMap = new EnumMap<>(FireMode.FireModeName.class);
        targetMap.put(MAIN, p -> p.getPosition().getPlayers().stream()
                .distinct()
                .filter(x -> !x.equals(p))
                .map(Arrays::asList)
                .collect(Collectors.toList()));
        targetMap.put(OPTION1, p -> Arrays.asList(Arrays.asList(p)));
        targetMap.put(OPTION2, p -> p.getPosition().getPlayers().stream()
                .distinct()
                .filter(x -> !x.equals(p))
                .filter(x -> !p.getMainTargets().contains(x))
                .map(Arrays::asList)
                .collect(Collectors.toList()));
        fireModeMap.put(CYBERBLADE, targetMap);

        targetMap = new EnumMap<>(FireMode.FireModeName.class);
        targetMap.put(MAIN, p -> Board.getInstance().getVisible(p.getPosition()).stream()
                .map(Square::getPlayers)
                .flatMap(x -> x.stream())
                .distinct()
                .map(Arrays::asList)
                .collect(Collectors.toList())
        );
        targetMap.put(SECONDARY, p -> {
            List<List<Player>> targets = new ArrayList<>();
            List<List<Player>> single = Board.getInstance().getVisible(p.getPosition()).stream()
                    .map(Square::getPlayers)
                    .flatMap(x -> x.stream())
                    .distinct()
                    .map(Arrays::asList)
                    .collect(Collectors.toList());
            targets.addAll(single);
            targets.addAll(cartesian(single, single));
            targets.addAll(cartesian(cartesian(single, single), single));
            return targets;
        });
        fireModeMap.put(ZX2, targetMap);

        targetMap = new EnumMap<>(FireMode.FireModeName.class);
        targetMap.put(MAIN, p ->p.getPosition().getPlayers().stream()
                .distinct()
                .map(Arrays::asList)
                .collect(Collectors.toList()));
        targetMap.put(SECONDARY, p -> Board.getInstance().getReachable(p.getPosition(), 1).stream()
                .map(Square::getPlayers)
                .flatMap(x -> x.stream())
                .distinct()
                .map(Arrays::asList)
                .collect(Collectors.toCollection(ArrayList::new))
        );
        fireModeMap.put(SHOTGUN, targetMap);

        targetMap = new EnumMap<>(FireMode.FireModeName.class);
        targetMap.put(MAIN, p -> Board.getInstance().getReachable(p.getPosition(), 1).stream()
                .map(Square::getPlayers)
                .flatMap(x -> x.stream())
                .distinct()
                .map(Arrays::asList)
                .collect(Collectors.toList()));
        targetMap.put(SECONDARY, p -> {
            List<List<Player>> targets = new ArrayList<>();
            for (Direction d : Direction.values()){
                List<List<Player>> close = Board.getInstance().getSquaresInLineIgnoringWalls(p.getPosition(), d)
                        .stream()
                        .filter(x -> {
                            try {
                                return Board.getInstance().getReachable(p.getPosition(), 1).contains(x);
                            } catch (NotAvailableAttributeException e) {
                                throw new IllegalArgumentException("Some players do not have a position.");
                            }
                        })
                        .map(Square::getPlayers)
                        .flatMap(x -> x.stream())
                        .distinct()
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
                List<List<Player>> far = Board.getInstance().getSquaresInLineIgnoringWalls(p.getPosition(), d)
                        .stream()
                        .filter(x -> {
                            try {
                                return Board.getInstance().getReachable(p.getPosition(), 2).contains(x);
                            } catch (NotAvailableAttributeException e) {
                                throw new IllegalArgumentException("Some players do not have a position.");
                            }
                        })
                        .filter(x -> {
                            try {
                                return !Board.getInstance().getReachable(p.getPosition(), 1).contains(x);
                            } catch (NotAvailableAttributeException e) {
                                throw new IllegalArgumentException("Some players do not have a position.");
                            }
                        })

                        .map(Square::getPlayers)
                        .flatMap(x -> x.stream())
                        .distinct()
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
                targets.addAll(close);
                targets.addAll(far);
                targets.addAll(cartesian(close, far));
            }
            return targets;
        });
        fireModeMap.put(POWER_GLOVE, targetMap);


        targetMap = new EnumMap<>(FireMode.FireModeName.class);
        targetMap.put(MAIN, p -> {
            List<List<Player>> targets = new ArrayList<>();
            List<List<List<Player>>> directionalTargets = new ArrayList<>();

            for (Direction d : Direction.values()){
                directionalTargets.add(Board.getInstance().getReachable(p.getPosition(), 1).stream()
                        .filter(x -> {
                            try {
                                return Board.getInstance().getSquaresInLine(p.getPosition(), d).contains(x);
                            } catch (NotAvailableAttributeException e) {
                                throw new IllegalArgumentException("Some players do not have a position.");
                            }
                        })
                        .map(Square::getPlayers)
                        .flatMap(x -> x.stream())
                        .distinct()
                        .map(Arrays::asList)
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
        });
        targetMap.put(SECONDARY, p -> Arrays.asList(Board.getInstance().getReachable(p.getPosition(), 1).stream()
                .map(Square::getPlayers)
                .flatMap(x -> x.stream())
                .distinct()
                .collect(Collectors.toList()))
        );
        fireModeMap.put(SHOCKWAVE, targetMap);

        targetMap = new EnumMap<>(FireMode.FireModeName.class);
        targetMap.put(MAIN, p -> p.getPosition().getPlayers().stream()
                .distinct()
                .filter(x -> !x.equals(p))
                .map(Arrays::asList)
                .collect(Collectors.toList())
        );
        targetMap.put(SECONDARY, p -> p.getPosition().getPlayers().stream()
                .distinct()
                .filter(x -> !x.equals(p))
                .map(Arrays::asList)
                .collect(Collectors.toList()));
        fireModeMap.put(SLEDGEHAMMER, targetMap);

        return (TargetFinder) fireModeMap.get(weaponName).get(fireModeName);
    }

    private static DestinationFinder getDestinationFinder(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {
        Map<Weapon.WeaponName, Map> fireModeMap = new EnumMap<>(Weapon.WeaponName.class);


        Map<FireMode.FireModeName, DestinationFinder> destinationMap = new EnumMap<>(FireMode.FireModeName.class);
        destinationMap.put(MAIN, (p, t) -> new ArrayList<>());
        destinationMap.put(OPTION1, (p, t) -> new ArrayList<>());
        fireModeMap.put(LOCK_RIFLE, destinationMap);

        destinationMap = new EnumMap<>(FireMode.FireModeName.class);
        destinationMap.put(MAIN, (p, t) -> new ArrayList<>());
        destinationMap.put(OPTION1, (p, t) -> new ArrayList<>());
        destinationMap.put(OPTION2, (p, t) -> new ArrayList<>());
        fireModeMap.put(MACHINE_GUN, destinationMap);

        destinationMap = new EnumMap<>(FireMode.FireModeName.class);
        destinationMap.put(MAIN, (p, t) -> new ArrayList<>());
        destinationMap.put(OPTION1, (p, t) -> new ArrayList<>());
        destinationMap.put(OPTION2, (p, t) -> new ArrayList<>());
        fireModeMap.put(THOR, destinationMap);

        destinationMap = new EnumMap<>(FireMode.FireModeName.class);
        destinationMap.put(MAIN, (p, t) -> new ArrayList<>());
        destinationMap.put(OPTION1, (p, t) -> Board.getInstance().getReachable(p.getPosition(), 2));
        destinationMap.put(OPTION2, (p, t) -> new ArrayList<>());
        fireModeMap.put(PLASMA_GUN, destinationMap);

        destinationMap = new EnumMap<>(FireMode.FireModeName.class);
        destinationMap.put(MAIN, (p, t) -> new ArrayList<>());
        fireModeMap.put(WHISPER, destinationMap);

        destinationMap = new EnumMap<>(FireMode.FireModeName.class);
        destinationMap.put(MAIN, (p, t) -> new ArrayList<>());
        destinationMap.put(SECONDARY, (p, t) -> new ArrayList<>());
        fireModeMap.put(ELECTROSCYTHE, destinationMap);

        destinationMap = new EnumMap<>(FireMode.FireModeName.class);
        destinationMap.put(MAIN, (p, t) -> Board.getInstance().getVisible(p.getPosition()).stream()
                .distinct()
                .filter(x -> {
                    try {
                        return t.isEmpty()||Board.getInstance().getVisible(t.get(0).getPosition()).contains(x);
                    } catch (NotAvailableAttributeException e) {
                        throw new IllegalArgumentException("Some players do not have a position.");
                    }
                })
                .collect(Collectors.toList())
        );
        destinationMap.put(SECONDARY, (p, t) -> Arrays.asList(p.getPosition()));
        fireModeMap.put(TRACTOR_BEAM, destinationMap);

        destinationMap = new EnumMap<>(FireMode.FireModeName.class);
        destinationMap.put(MAIN, (p, t) -> Board.getInstance().getReachable(t.get(0).getPosition(), 1).stream()
                .filter(x -> {
                    try {
                        return Board.getInstance().getVisible(p.getPosition()).contains(x);
                    } catch (NotAvailableAttributeException e) {
                        throw new IllegalArgumentException("Some players do not have a position.");
                    }
                })
                .filter(x -> {
                    try {
                        return !x.equals(p.getPosition());
                    } catch (NotAvailableAttributeException e) {
                        throw new IllegalArgumentException("Some players do not have a position.");
                    }
                })
                .distinct()
                .collect(Collectors.toList())
        );
        destinationMap.put(OPTION1, (p, t) -> p.getMainTargets().isEmpty() ? new ArrayList<Square>() : Stream.of(p.getMainTargets().get(0).getPosition()).collect(Collectors.toList()));
        fireModeMap.put(VORTEX_CANNON, destinationMap);

        destinationMap = new EnumMap<>(FireMode.FireModeName.class);
        destinationMap.put(MAIN, (p, t) -> new ArrayList<>());
        destinationMap.put(SECONDARY, (p, t) -> new ArrayList<>());
        fireModeMap.put(FURNACE, destinationMap);

        destinationMap = new EnumMap<>(FireMode.FireModeName.class);
        destinationMap.put(MAIN, (p, t) -> new ArrayList<>());
        fireModeMap.put(HEATSEEKER, destinationMap);

        destinationMap = new EnumMap<>(FireMode.FireModeName.class);
        destinationMap.put(MAIN, (p, t) -> new ArrayList<>());
        destinationMap.put(SECONDARY, (p, t) -> new ArrayList<>());
        fireModeMap.put(HELLION, destinationMap);

        destinationMap = new EnumMap<>(FireMode.FireModeName.class);
        destinationMap.put(MAIN, (p, t) -> new ArrayList<>());
        destinationMap.put(SECONDARY, (p, t) -> new ArrayList<>());
        fireModeMap.put(FLAMETHROWER, destinationMap);

        destinationMap = new EnumMap<>(FireMode.FireModeName.class);
        destinationMap.put(MAIN, (p, t) -> t.isEmpty() ? Arrays.asList(p.getPosition()) : Board.getInstance().getReachable(t.get(0).getPosition(), 1));
        destinationMap.put(OPTION1, (p, t) -> new ArrayList<>());
        fireModeMap.put(GRENADE_LAUNCHER, destinationMap);

        destinationMap = new EnumMap<>(FireMode.FireModeName.class);
        destinationMap.put(MAIN, (p, t) -> t.isEmpty() ? Arrays.asList(p.getPosition()) : Board.getInstance().getReachable(t.get(0).getPosition(), 1));
        destinationMap.put(OPTION1, (p, t) -> Board.getInstance().getReachable(p.getPosition(), 2));
        destinationMap.put(OPTION2, (p, t) -> new ArrayList<>());
        fireModeMap.put(ROCKET_LAUNCHER, destinationMap);

        destinationMap = new EnumMap<>(FireMode.FireModeName.class);
        destinationMap.put(MAIN, (p, t) -> new ArrayList<>());
        destinationMap.put(SECONDARY, (p, t) -> new ArrayList<>());
        fireModeMap.put(RAILGUN, destinationMap);

        destinationMap = new EnumMap<>(FireMode.FireModeName.class);
        destinationMap.put(MAIN, (p, t) -> new ArrayList<>());
        destinationMap.put(OPTION1, (p, t) -> Board.getInstance().getReachable(p.getPosition(), 1));
        destinationMap.put(OPTION2, (p, t) -> new ArrayList<>());
        fireModeMap.put(CYBERBLADE, destinationMap);

        destinationMap = new EnumMap<>(FireMode.FireModeName.class);
        destinationMap.put(MAIN, (p, t) -> new ArrayList<>());
        destinationMap.put(SECONDARY, (p, t) -> new ArrayList<>());
        fireModeMap.put(ZX2, destinationMap);

        destinationMap = new EnumMap<>(FireMode.FireModeName.class);
        destinationMap.put(MAIN, (p, t) -> {
            List<Square> destination = Board.getInstance().getReachable(p.getPosition(), 1);
            destination.add(p.getPosition());
            return destination;
        });
        destinationMap.put(SECONDARY, (p, t) -> new ArrayList<>());
        fireModeMap.put(SHOTGUN, destinationMap);

        destinationMap = new EnumMap<>(FireMode.FireModeName.class);
        destinationMap.put(MAIN, (p, t) -> t.isEmpty() ? Arrays.asList(p.getPosition()) : Arrays.asList(t.get(0).getPosition()));
        destinationMap.put(SECONDARY, (p, t) -> {
            for (Player temp : t) {
                if (Board.getInstance().getDistance(p.getPosition(), temp.getPosition()) > 1) {
                    return Arrays.asList(temp.getPosition());
                }
            }
            List<Square> res = new ArrayList<>();
            res.add(t.get(0).getPosition());
            for (Direction d : Direction.values()){
                if (Board.getInstance().getSquaresInLine(p.getPosition(), d).contains(t.get(0).getPosition())) {
                    for (Square sq : Board.getInstance().getSquaresInLine(p.getPosition(), d)) {
                        if (Board.getInstance().getDistance(sq, p.getPosition()) == 2) {
                            res.add(sq);
                        }
                    }
                }

            }
            return res;

        });
        fireModeMap.put(POWER_GLOVE, destinationMap);

        destinationMap = new EnumMap<>(FireMode.FireModeName.class);
        destinationMap.put(MAIN, (p, t) -> new ArrayList<>());
        destinationMap.put(SECONDARY, (p, t) -> new ArrayList<>());
        fireModeMap.put(SHOCKWAVE, destinationMap);

        destinationMap = new EnumMap<>(FireMode.FireModeName.class);
        destinationMap.put(MAIN, (p, t) -> new ArrayList<>());
        destinationMap.put(SECONDARY, (p, t) -> {
            if(t.isEmpty()){
                return Arrays.asList(p.getPosition());
            }
            List<Square> res = new ArrayList<>();
            Square center = t.get(0).getPosition();
            res.add(center);
            for (Direction d : Direction.values()){
                res.addAll(Board.getInstance().getSquaresInLine(center, d).stream()
                        .filter(x->Board.getInstance().getDistance(center, x)<3)
                        .collect(Collectors.toList()));
            }
            return res;
        });
        fireModeMap.put(SLEDGEHAMMER, destinationMap);


        return (DestinationFinder) fireModeMap.get(weaponName).get(fireModeName);
    }


    private static Integer getTargetNumber (Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {
        Map<Weapon.WeaponName, Map> fireModeMap = new EnumMap<>(Weapon.WeaponName.class);

        Map<FireMode.FireModeName, Integer> numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(MAIN, 1);
        numberMap.put(OPTION1, 1);
        fireModeMap.put(LOCK_RIFLE, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(MAIN, 2);
        numberMap.put(OPTION1, 1);
        numberMap.put(OPTION2, 1);
        fireModeMap.put(MACHINE_GUN, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(MAIN, 1);
        numberMap.put(OPTION1, 1);
        numberMap.put(OPTION2, 1);
        fireModeMap.put(THOR, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(MAIN, 1);
        numberMap.put(OPTION1, 1);
        numberMap.put(OPTION2, 1);
        fireModeMap.put(PLASMA_GUN, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(MAIN, 1);
        fireModeMap.put(WHISPER, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(MAIN, 4);
        numberMap.put(SECONDARY, 4);
        fireModeMap.put(ELECTROSCYTHE, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(MAIN, 1);
        numberMap.put(SECONDARY, 1);
        fireModeMap.put(TRACTOR_BEAM, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(MAIN, 1);
        numberMap.put(OPTION1, 2);
        fireModeMap.put(VORTEX_CANNON, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(MAIN, 4);
        numberMap.put(SECONDARY, 4);
        fireModeMap.put(FURNACE, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(MAIN, 1);
        fireModeMap.put(HEATSEEKER, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(MAIN, 1);
        numberMap.put(SECONDARY, 1);
        fireModeMap.put(HELLION, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(MAIN, 2);
        numberMap.put(SECONDARY, 4);
        fireModeMap.put(FLAMETHROWER, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(MAIN, 1);
        numberMap.put(OPTION1, 4);
        fireModeMap.put(GRENADE_LAUNCHER, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(MAIN, 1);
        numberMap.put(OPTION1, 1);
        numberMap.put(OPTION2, 4);
        fireModeMap.put(ROCKET_LAUNCHER, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(MAIN, 1);
        numberMap.put(SECONDARY, 2);
        fireModeMap.put(RAILGUN, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(MAIN, 1);
        numberMap.put(OPTION1, 1);
        numberMap.put(OPTION2, 1);
        fireModeMap.put(CYBERBLADE, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(MAIN, 1);
        numberMap.put(SECONDARY, 3);
        fireModeMap.put(ZX2, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(MAIN, 1);
        numberMap.put(SECONDARY, 1);
        fireModeMap.put(SHOTGUN, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(MAIN, 1);
        numberMap.put(SECONDARY, 2);
        fireModeMap.put(POWER_GLOVE, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(MAIN, 3);
        numberMap.put(SECONDARY, 4);
        fireModeMap.put(SHOCKWAVE, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(MAIN, 1);
        numberMap.put(SECONDARY, 1);
        fireModeMap.put(SLEDGEHAMMER, numberMap);

        return (Integer)fireModeMap.get(weaponName).get(fireModeName);
    }

    private static AmmoPack getFireModeCost (Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {
        if(fireModeName == MAIN) {
            return new AmmoPack(0,0,0);
        }

        Map<Weapon.WeaponName, Map> fireModeMap = new EnumMap<>(Weapon.WeaponName.class);

        Map<FireMode.FireModeName, AmmoPack> numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(OPTION1, new AmmoPack(1,0,0));
        fireModeMap.put(LOCK_RIFLE, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(OPTION1, new AmmoPack(0,0,1));
        numberMap.put(OPTION2, new AmmoPack(0,1,0));
        fireModeMap.put(MACHINE_GUN, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(OPTION1, new AmmoPack(0,1,0));
        numberMap.put(OPTION2, new AmmoPack(0,1,0));
        fireModeMap.put(THOR, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(OPTION1, new AmmoPack(0,0,0));
        numberMap.put(OPTION2, new AmmoPack(0,1,0));
        fireModeMap.put(PLASMA_GUN, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(SECONDARY, new AmmoPack(1,1,0));
        fireModeMap.put(ELECTROSCYTHE, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(SECONDARY, new AmmoPack(1,0,1));
        fireModeMap.put(TRACTOR_BEAM, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(OPTION1, new AmmoPack(1,0,0));
        fireModeMap.put(VORTEX_CANNON, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(SECONDARY, new AmmoPack(0,0,0));
        fireModeMap.put(FURNACE, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(SECONDARY, new AmmoPack(1,0,0));
        fireModeMap.put(HELLION, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(SECONDARY, new AmmoPack(0,0,2));
        fireModeMap.put(FLAMETHROWER, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(OPTION1, new AmmoPack(1,0,0));
        fireModeMap.put(GRENADE_LAUNCHER, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(OPTION1, new AmmoPack(0,1,0));
        numberMap.put(OPTION2, new AmmoPack(0,0,1));
        fireModeMap.put(ROCKET_LAUNCHER, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(SECONDARY, new AmmoPack(0,0,0));
        fireModeMap.put(RAILGUN, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(OPTION1, new AmmoPack(0,0,0));
        numberMap.put(OPTION2, new AmmoPack(0,0,1));
        fireModeMap.put(CYBERBLADE, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(SECONDARY, new AmmoPack(0,0,0));
        fireModeMap.put(ZX2, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(SECONDARY, new AmmoPack(0,0,0));
        fireModeMap.put(SHOTGUN, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(SECONDARY, new AmmoPack(0,1,0));
        fireModeMap.put(POWER_GLOVE, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(SECONDARY, new AmmoPack(0,0,1));
        fireModeMap.put(SHOCKWAVE, numberMap);

        numberMap = new EnumMap<>(FireMode.FireModeName.class);
        numberMap.put(SECONDARY, new AmmoPack(1,0,0));
        fireModeMap.put(SLEDGEHAMMER, numberMap);

        return (AmmoPack)fireModeMap.get(weaponName).get(fireModeName);
    }


    private static Effect createEffect(int damage, int marks){

        if(damage<0 || marks<0){
            throw new IllegalArgumentException("Damage and marks must be positive.");
        }
        if(damage!=0&&marks!=0) {
            return (shooter, target, destination) -> {
                target.sufferDamage(damage, shooter);
                target.addMarks(marks, shooter);
            };
        }
        if(damage!=0){
            return (shooter, target, destination) -> target.sufferDamage(damage, shooter);
        }
        return (shooter, target, destination) -> target.sufferDamage(damage, shooter);
    }

    private static List<List<Player>> cartesian (List<List<Player>> a, List<List<Player>> b){
        List<List<Player>> atemp = a.stream()
                .filter(x->!x.isEmpty())
                .distinct()
                .collect(Collectors.toList());
        List<Player> btemp = b.stream()
                .filter(x->!x.isEmpty())
                .flatMap(x->x.stream())
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        List<List<Player>> res = new ArrayList<>();
        List<Player> temp;

        for (List<Player> l : atemp){
            for (Player p : btemp){
                if(!l.contains(p)) {
                    temp = new ArrayList<>();
                    temp.addAll(l);
                    temp.add(p);
                    res.add(temp);
                }
            }
        }

        for (int i = 0; i<res.size(); i++){
            int j = i+1;
            while(j<res.size()){
                if(res.get(i).containsAll(res.get(j))&&res.get(j).containsAll(res.get(i))){
                    res.remove(j);
                } else {j++;}
            }
        }

        return res;
    }
}