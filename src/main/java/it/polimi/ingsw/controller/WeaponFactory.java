//TODO load data from JSON/XML: NB: loading data solves the cognitive complexity problem
//      every "getter" should read a JSON or XML file importing the hashmap instead of creating it. Check out serializable interfaces.
//TODO properly comment methods
//TODO testing

package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Player;
import it.polimi.ingsw.model.board.Square;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;

import java.util.*;
import java.util.stream.*;

import static it.polimi.ingsw.model.cards.Color.*;
import static it.polimi.ingsw.model.cards.FireMode.FireModeName.*;
import static it.polimi.ingsw.model.cards.Weapon.WeaponName.*;
import static it.polimi.ingsw.model.board.Board.Direction;

/**
 * Factory class to create a weapon.
 *
 * @author  marcobaga
 */

public class WeaponFactory {


    private Board board;
    /**
     * Constructs a weapon factory with a reference to the game board.
     *
     * @param board         the board of the game.
     * @return      a WeaponFactory
     */
    public WeaponFactory(Board board){this.board = board;}

    /**
     *Creates a Weapon object according to its name
     *
     * @param  weaponName  the name of the weapon to be created
     * @return      the Weapon object created
     */
    public Weapon createWeapon(Weapon.WeaponName weaponName) {

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

        Weapon weapon = new Weapon(weaponName, color, fullCost, reducedCost, fireModeList, board);

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

    private Effect getEffect(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {
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
            board.getPlayersInside(target.getPosition()).forEach(x -> x.addMarks(1, shooter));
        }));
        effectMap.put(SECONDARY, ((shooter, target, destination) -> {
            target.sufferDamage(1, shooter);
            board.getPlayersInside(target.getPosition()).forEach(x -> x.addMarks(2, shooter));
        }));
        fireModeMap.put(HELLION, effectMap);

        effectMap = new EnumMap<>(FireMode.FireModeName.class);
        effectMap.put(MAIN, createEffect(1,0));
        effectMap.put(SECONDARY, ((shooter, target, destination) -> {
            if (board.getReachable(shooter.getPosition(), 1).contains(target.getPosition())) {
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

    private TargetFinder getTargetFinder(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {
        Map<Weapon.WeaponName, Map> fireModeMap = new EnumMap<>(Weapon.WeaponName.class);


        Map<FireMode.FireModeName, TargetFinder> targetMap = new EnumMap<>(FireMode.FireModeName.class);
        targetMap.put(MAIN, p -> board.getVisible(p.getPosition()).stream()
                .map(Square::getPlayers)
                .flatMap(x -> x.stream())
                .distinct()
                .filter( x -> !x.equals(p))
                .map(Arrays::asList)
                .collect(Collectors.toList()));
        targetMap.put(OPTION1, p -> (board.getVisible(p.getPosition()).stream()
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
            List<List<Player>> res = board.getVisible(p.getPosition()).stream()
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
            List<List<Player>> others = board.getVisible(p.getPosition()).stream()
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
        targetMap.put(MAIN, p -> board.getVisible(p.getPosition()).stream()
                .map(Square::getPlayers)
                .flatMap(x -> x.stream())
                .distinct()
                .filter(x -> !x.equals(p))
                .map(Arrays::asList)
                .collect(Collectors.toList()));
        targetMap.put(OPTION1, p -> (p.getMainTargets().isEmpty()) ?
                new ArrayList<>() : board.getVisible(p.getMainTargets().get(0).getPosition()).stream()
                .map(Square::getPlayers)
                .flatMap(x -> x.stream())
                .distinct()
                .filter(x -> !p.getMainTargets().contains(x))
                .filter(x -> !x.equals(p))
                .map(Arrays::asList)
                .collect(Collectors.toList()));
        targetMap.put(OPTION2, p -> (p.getMainTargets().isEmpty()||p.getOptionalTargets().isEmpty()) ?
                new ArrayList<>() : board
                .getVisible(p.getOptionalTargets().get(0).getPosition()).stream()
                .map(Square::getPlayers)
                .flatMap(x -> x.stream())
                .distinct()
                .filter(x -> !(p.getMainTargets().contains(x) || p.getOptionalTargets().contains(x)))
                .filter(x -> !x.equals(p))
                .map(Arrays::asList)
                .collect(Collectors.toList()));
        fireModeMap.put(THOR, targetMap);


        targetMap = new EnumMap<>(FireMode.FireModeName.class);
        targetMap.put(MAIN, p -> board.getVisible(p.getPosition()).stream()
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
        targetMap.put(MAIN, p -> board.getVisible(p.getPosition()).stream()
                .map(Square::getPlayers)
                .flatMap(x -> x.stream())
                .distinct()
                .filter(x -> !x.equals(p))
                .filter(x -> {
                    try {
                        return board.getDistance(x.getPosition(), p.getPosition()) >= 2;
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
            List<Square> l = board.getVisible(p.getPosition());
            List<Square> temp = new ArrayList<>();
            for (Square s : l) {
                temp.addAll(board.getReachable(s, 2));
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
        targetMap.put(SECONDARY, p -> board.getReachable(p.getPosition(), 2).stream()
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
            List<Square> l = board.getVisible(p.getPosition());
            List<Square> temp = new ArrayList<>();
            for (Square s : l) {
                temp.addAll(board.getReachable(s, 1));
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
            List<List<Player>> lp = board.getReachable(p.getMainTargets().get(0).getPosition(), 1).stream()
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
            List<List<Square>> roomList = board.getVisible(p.getPosition()).stream()
                    .map(Square::getRoomId)
                    .distinct()
                    .filter(x -> {
                        try {
                            return x!=p.getPosition().getRoomId();
                        } catch (NotAvailableAttributeException e) {
                            throw new IllegalArgumentException("Some players do not have a position.");
                        }
                    })
                    .map(x -> board.getSquaresInRoom(x))
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
        targetMap.put(SECONDARY, p -> board.getReachable(p.getPosition(), 1).stream()
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
        targetMap.put(MAIN, p -> board.getMap().stream()
                .filter(x -> {
                    try {
                        return !board.getVisible(p.getPosition()).contains(x);
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
        targetMap.put(MAIN, p -> board.getVisible(p.getPosition()).stream()
                .filter(x -> !x.containsPlayer(p))
                .map(Square::getPlayers)
                .flatMap(x -> x.stream())
                .distinct()
                .map(Arrays::asList)
                .collect(Collectors.toList()));
        targetMap.put(SECONDARY, p -> board.getVisible(p.getPosition()).stream()
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
                List<List<Player>> close = board.getSquaresInLine(p.getPosition(), d).stream()
                        .filter(x -> {
                            try {
                                return board.getReachable(p.getPosition(), 1).contains(x);
                            } catch (NotAvailableAttributeException e) {
                                throw new IllegalArgumentException("Some players do not have a position.");
                            }
                        })
                        .map(Square::getPlayers)
                        .flatMap(x -> x.stream())
                        .distinct()
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
                List<List<Player>> far = board.getSquaresInLine(p.getPosition(), d).stream()
                        .filter(x -> {
                            try {
                                return board.getReachable(p.getPosition(), 2).contains(x)&&!board.getReachable(p.getPosition(), 1).contains(x);
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
                List<List<Player>> close = board.getSquaresInLine(p.getPosition(), d).stream()
                        .filter(x -> {
                            try {
                                return !board.getReachable(p.getPosition(), 1).contains(x);
                            } catch (NotAvailableAttributeException e) {
                                throw new IllegalArgumentException("Some players do not have a position.");
                            }
                        })
                        .map(Square::getPlayers)
                        .flatMap(x -> x.stream())
                        .distinct()
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
                List<List<Player>> far = board.getSquaresInLine(p.getPosition(), d).stream()
                        .filter(x -> {
                            try {
                                return board.getReachable(p.getPosition(), 2).contains(x)&&!board.getReachable(p.getPosition(), 1).contains(x);
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
        targetMap.put(MAIN, p -> board.getVisible(p.getPosition()).stream()
                .map(Square::getPlayers)
                .flatMap(x -> x.stream())
                .distinct()
                .filter(x -> !x.equals(p))
                .map(Arrays::asList)
                .collect(Collectors.toList())
        );
        targetMap.put(OPTION1, p -> {List<List<Player>> l = board.getVisible(p.getPosition()).stream()
                .map(Square::getPlayers)
                .filter(x -> {
                    try {
                        return !x.equals(p.getPosition());
                    } catch (NotAvailableAttributeException e) {
                        throw new IllegalArgumentException("Some players do not have a position.");
                    }
                })
                .collect(Collectors.toList());
                l.add(p.getPosition().getPlayers().stream().filter(x -> !x.equals(p)).collect(Collectors.toList()));
                return l;
        });
        fireModeMap.put(GRENADE_LAUNCHER, targetMap);

        targetMap = new EnumMap<>(FireMode.FireModeName.class);
        targetMap.put(MAIN, p -> board.getVisible(p.getPosition()).stream()
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
            List<Player> l = new ArrayList<>(p.getMainTargets());
            for(Player player : p.getMainTargets()){
                l.addAll(player.getPreviousPosition().getPlayers());
            }
            return Arrays.asList(l);
        });
        fireModeMap.put(ROCKET_LAUNCHER, targetMap);

        targetMap = new EnumMap<>(FireMode.FireModeName.class);
        targetMap.put(MAIN, p -> {
            List<List<Player>> targets = new ArrayList<>();
            for (Direction d : Direction.values()){
                List<List<Player>> single = board.getSquaresInLineIgnoringWalls(p.getPosition(), d)
                        .stream()
                        .map(Square::getPlayers)
                        .flatMap(x -> x.stream())
                        .distinct()
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
                targets.addAll(single);
            }
            targets.addAll(p.getPosition().getPlayers().stream()
                    .distinct()
                    .filter(x -> x.equals(p))
                    .map(Arrays::asList)
                    .collect(Collectors.toList())
            );
            return targets;
        });
        targetMap.put(SECONDARY, p -> {
            List<List<Player>> targets = new ArrayList<>();
            List<List<Player>> close = p.getPosition().getPlayers().stream()
                    .distinct()
                    .filter(x -> x.equals(p))
                    .map(Arrays::asList)
                    .collect(Collectors.toList());
            targets.addAll(close);
            targets.addAll(cartesian(close, close));
            for (Direction d : Direction.values()){
                List<List<Player>> single = board.getSquaresInLineIgnoringWalls(p.getPosition(), d)
                        .stream()
                        .map(Square::getPlayers)
                        .flatMap(x -> x.stream())
                        .distinct()
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
                targets.addAll(single);
                List<List<Player>> both = new ArrayList<>(close);
                both.addAll(single);
                targets.addAll(cartesian(both, single));
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
        targetMap.put(MAIN, p -> board.getVisible(p.getPosition()).stream()
                .map(Square::getPlayers)
                .flatMap(x -> x.stream())
                .distinct()
                .filter(x -> !x.equals(p))
                .map(Arrays::asList)
                .collect(Collectors.toList())
        );
        targetMap.put(SECONDARY, p -> {
            List<List<Player>> targets = new ArrayList<>();
            List<List<Player>> single = board.getVisible(p.getPosition()).stream()
                    .map(Square::getPlayers)
                    .flatMap(x -> x.stream())
                    .distinct()
                    .filter(x -> !x.equals(p))
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
                .filter(x -> !x.equals(p))
                .map(Arrays::asList)
                .collect(Collectors.toList()));
        targetMap.put(SECONDARY, p -> board.getReachable(p.getPosition(), 1).stream()
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
                .collect(Collectors.toList()));
        fireModeMap.put(SHOTGUN, targetMap);

        targetMap = new EnumMap<>(FireMode.FireModeName.class);
        targetMap.put(MAIN, p -> board.getReachable(p.getPosition(), 1).stream()
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
                .collect(Collectors.toList()));
        targetMap.put(SECONDARY, p -> {
            List<List<Player>> targets = new ArrayList<>();
            for (Direction d : Direction.values()){
                List<List<Player>> close = board.getSquaresInLineIgnoringWalls(p.getPosition(), d)
                        .stream()
                        .filter(x -> {
                            try {
                                return board.getReachable(p.getPosition(), 1).contains(x);
                            } catch (NotAvailableAttributeException e) {
                                throw new IllegalArgumentException("Some players do not have a position.");
                            }
                        })
                        .map(Square::getPlayers)
                        .flatMap(x -> x.stream())
                        .distinct()
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
                List<List<Player>> far = board.getSquaresInLineIgnoringWalls(p.getPosition(), d)
                        .stream()
                        .filter(x -> {
                            try {
                                return board.getReachable(p.getPosition(), 2).contains(x);
                            } catch (NotAvailableAttributeException e) {
                                throw new IllegalArgumentException("Some players do not have a position.");
                            }
                        })
                        .filter(x -> {
                            try {
                                return !board.getReachable(p.getPosition(), 1).contains(x);
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
                directionalTargets.add(board.getReachable(p.getPosition(), 1).stream()
                        .filter(x -> {
                            try {
                                return board.getSquaresInLine(p.getPosition(), d).contains(x);
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
        targetMap.put(SECONDARY, p -> Arrays.asList(board.getReachable(p.getPosition(), 1).stream()
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

    private DestinationFinder getDestinationFinder(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {
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
        destinationMap.put(OPTION1, (p, t) -> {List<Square > l = board.getReachable(p.getPosition(), 2); l.remove(p.getPosition()); return l;});
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
        destinationMap.put(MAIN, (p, t) -> board.getVisible(p.getPosition()).stream()
                .distinct()
                .filter(x -> {
                    try {
                        return t.isEmpty()||board.getVisible(t.get(0).getPosition()).contains(x);
                    } catch (NotAvailableAttributeException e) {
                        throw new IllegalArgumentException("Some players do not have a position.");
                    }
                })
                .collect(Collectors.toList())
        );
        destinationMap.put(SECONDARY, (p, t) -> Arrays.asList(p.getPosition()));
        fireModeMap.put(TRACTOR_BEAM, destinationMap);

        destinationMap = new EnumMap<>(FireMode.FireModeName.class);
        destinationMap.put(MAIN, (p, t) -> board.getVisible(p.getPosition()).stream()
                .filter(x -> {
                    try {
                        return t.isEmpty()||board.getReachable(t.get(0).getPosition(), 1).contains(x);
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
        destinationMap.put(OPTION1, (p, t) -> p.getMainTargets().isEmpty() ? new ArrayList<Square>() : Arrays.asList(p.getMainTargets().get(0).getPosition()));
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
        destinationMap.put(MAIN, (p, t) -> t.isEmpty() ? new ArrayList<>() : board.getReachable(t.get(0).getPosition(), 1));
        destinationMap.put(OPTION1, (p, t) -> new ArrayList<>());
        fireModeMap.put(GRENADE_LAUNCHER, destinationMap);

        destinationMap = new EnumMap<>(FireMode.FireModeName.class);
        destinationMap.put(MAIN, (p, t) -> t.isEmpty() ? new ArrayList<>() : board.getReachable(t.get(0).getPosition(), 1));
        destinationMap.put(OPTION1, (p, t) -> {List<Square > l = board.getReachable(p.getPosition(), 2); l.remove(p.getPosition()); return l;});
        destinationMap.put(OPTION2, (p, t) -> new ArrayList<>());
        fireModeMap.put(ROCKET_LAUNCHER, destinationMap);

        destinationMap = new EnumMap<>(FireMode.FireModeName.class);
        destinationMap.put(MAIN, (p, t) -> new ArrayList<>());
        destinationMap.put(SECONDARY, (p, t) -> new ArrayList<>());
        fireModeMap.put(RAILGUN, destinationMap);

        destinationMap = new EnumMap<>(FireMode.FireModeName.class);
        destinationMap.put(MAIN, (p, t) -> new ArrayList<>());
        destinationMap.put(OPTION1, (p, t) -> {List<Square > l = board.getReachable(p.getPosition(), 1); l.remove(p.getPosition()); return l;});
        destinationMap.put(OPTION2, (p, t) -> new ArrayList<>());
        fireModeMap.put(CYBERBLADE, destinationMap);

        destinationMap = new EnumMap<>(FireMode.FireModeName.class);
        destinationMap.put(MAIN, (p, t) -> new ArrayList<>());
        destinationMap.put(SECONDARY, (p, t) -> new ArrayList<>());
        fireModeMap.put(ZX2, destinationMap);

        destinationMap = new EnumMap<>(FireMode.FireModeName.class);
        destinationMap.put(MAIN, (p, t) -> board.getReachable(p.getPosition(), 1));
        destinationMap.put(SECONDARY, (p, t) -> new ArrayList<>());
        fireModeMap.put(SHOTGUN, destinationMap);

        destinationMap = new EnumMap<>(FireMode.FireModeName.class);
        destinationMap.put(MAIN, (p, t) -> t.isEmpty() ? new ArrayList<>(): Arrays.asList(t.get(0).getPosition()));
        destinationMap.put(SECONDARY, (p, t) -> {
            for (Player temp : t) {
                if (board.getDistance(p.getPosition(), temp.getPosition()) > 1) {
                    return Arrays.asList(temp.getPosition());
                }
            }
            List<Square> res = new ArrayList<>();
            res.add(t.get(0).getPosition());
            for (Direction d : Direction.values()){
                if (board.getSquaresInLine(p.getPosition(), d).contains(t.get(0).getPosition())) {
                    for (Square sq : board.getSquaresInLine(p.getPosition(), d)) {
                        if (board.getDistance(sq, p.getPosition()) == 2) {
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
            List<Square> res = new ArrayList<>();
            Square center = p.getPosition();
            res.add(center);
            for (Direction d : Direction.values()){
                res.addAll(board.getSquaresInLine(center, d).stream()
                        .filter(x->board.getDistance(center, x)<3)
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