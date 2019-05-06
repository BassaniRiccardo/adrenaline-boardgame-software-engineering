//TODO properly comment methods
//TODO testing

package it.polimi.ingsw.controller;

import java.util.*;
import java.util.stream.*;

import static it.polimi.ingsw.model.cards.Color.*;
import static it.polimi.ingsw.model.cards.FireMode.FireModeName.*;
import static it.polimi.ingsw.model.cards.Weapon.*;
import static it.polimi.ingsw.model.board.Board.*;
import com.google.common.annotations.VisibleForTesting;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Json;
import it.polimi.ingsw.model.board.Player;
import it.polimi.ingsw.model.board.Square;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;

/**
     * Factory class to create a weapon.
     *
     * @author  marcobaga, davidealde
     */

    public class WeaponFactory {


        private static Board board;
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

        /**
         *Creates a Weapon object according to its name
         *
         * @param  weaponName  the name of the weapon to be created
         * @return      the Weapon object created
         */

        /**
         * Getters of some characteristics of the weapons
         *
         * @param weaponName            the name of the weapon of interest
         * @retun                       the value of interest
         */
    @VisibleForTesting
    public static Color getColor(Weapon.WeaponName weaponName) {

        Json j = new Json();
        String color = j.getColor(weaponName);

        if(color.equals("yellow")){
            return YELLOW;
        }else if(color.equals("blue")){
            return BLUE;
        }else return RED;
    }

    @VisibleForTesting
    public static AmmoPack getFullCost(Weapon.WeaponName weaponName) {

        Json j = new Json();
        int r = Integer.parseInt(j.getFullCostRed(weaponName));
        int b = Integer.parseInt(j.getFullCostBlue(weaponName));
        int y = Integer.parseInt(j.getFullCostYellow(weaponName));

        return new AmmoPack(r,b,y);
    }

    @VisibleForTesting
    public static AmmoPack getReducedCost(Weapon.WeaponName weaponName) {
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

    @VisibleForTesting
    public static List<FireMode.FireModeName> getNameList(Weapon.WeaponName weaponName) {

        Json j = new Json();
        int type = Integer.parseInt(j.getNameList(weaponName));
        if (type==1) {
            return new ArrayList<>(Arrays.asList(MAIN));
        } else if (type==2) {
            return new ArrayList<>(Arrays.asList(MAIN, SECONDARY));
        } else if (type==3) {
            return new ArrayList<>(Arrays.asList(MAIN, OPTION1));
        } else if (type==4) {
            return new ArrayList<>(Arrays.asList(MAIN, OPTION1, OPTION2));
        } else {
            return new ArrayList<>(Arrays.asList(MAIN));
        }
    }

        /**
         * Getters of some characteristics of the firemodes of the weapons
         *
         * @param weaponName            the name of the weapon of interest
         * @param fireModeName          the name of the firemode of interest
         * @retun                       the value of interest
         */
    @VisibleForTesting
    public static Integer getTargetNumber (Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        Json j = new Json();
        int number = Integer.parseInt(j.getTargetNumber(weaponName, fireModeName));

        return number;
    }

    @VisibleForTesting
    public static AmmoPack getFireModeCost (Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {
        if(fireModeName == MAIN) {
            return new AmmoPack(0,0,0);
        }

        Json j = new Json();
        int r = Integer.parseInt(j.getFireModeCostRed(weaponName, fireModeName));
        int b = Integer.parseInt(j.getFireModeCostBlue(weaponName, fireModeName));
        int y = Integer.parseInt(j.getFireModeCostYellow(weaponName, fireModeName));

        return new AmmoPack(r,b,y);
    }

    @VisibleForTesting
    public static Effect getEffect(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        Json j = new Json();
        String eff= j.getEff(weaponName, fireModeName);
        int dmg = Integer.parseInt(j.getDmg(weaponName,fireModeName));
        int mark = Integer.parseInt(j.getMark(weaponName,fireModeName));
        Effect effect;
        if(eff.equals("0")){
            effect = createEffect(dmg,mark);
        }else if (eff.equals("moveDmg")){
            effect = ((shooter, target, destination) -> {
                target.setPosition(destination);
                target.sufferDamage(dmg, shooter);
                target.addMarks(mark, shooter);
            });
        }else if (eff.equals("dmgMove")){
            effect=((shooter, target, destination) -> {
                target.sufferDamage(dmg, shooter);
                target.setPosition(destination);
            });
        }else if (eff.equals("hellion")){
            effect= ((shooter, target, destination) -> {
                target.sufferDamage(dmg, shooter);
                board.getPlayersInside(target.getPosition()).forEach(x -> x.addMarks(mark, shooter));
            });
        }else {
            int dmg2 = Integer.parseInt(j.getDmg2(weaponName,fireModeName));
            int steps = Integer.parseInt(j.getSteps(weaponName,fireModeName));
            effect=((shooter, target, destination) -> {
                if (board.getReachable(shooter.getPosition(), steps).contains(target.getPosition())) {
                    target.sufferDamage(dmg, shooter);
                } else target.sufferDamage(dmg2, shooter);
            });
        }


        return effect;
    }

    @VisibleForTesting
    public static TargetFinder getTargetFinder(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {
        Json j = new Json();
        String where = j.getWhere(weaponName, fireModeName);
        TargetFinder targetFinder;
        switch(where) {
            case "sight1":
                targetFinder= p -> board.getVisible(p.getPosition()).stream()
                        .map(Square::getPlayers)
                        .flatMap(x -> x.stream())
                        .distinct()
                        .filter( x -> !x.equals(p))
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
                break;
            case "sight2":
                targetFinder=p -> {
                    List<List<Player>> res = board.getVisible(p.getPosition()).stream()
                            .map(Square::getPlayers)
                            .flatMap(x -> x.stream())
                            .distinct()
                            .filter( x -> !x.equals(p))
                            .map(Arrays::asList)
                            .collect(Collectors.toList());
                    res.addAll(cartesian(res, res));
                    return res;
                };
                break;
            case "previous1":
                targetFinder=p -> (p.getMainTargets().stream()
                        .distinct()
                        .filter(x -> p.getMainTargets().contains(x))
                        .filter(x -> !p.getOptionalTargets().contains(x))
                        .map(Arrays::asList)
                        .collect(Collectors.toList()));
                break;
            case "previous2":
                targetFinder=p -> {
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
                };
                break;
            case "chain1":
                targetFinder=p -> (p.getMainTargets().isEmpty()) ?
                        new ArrayList<>() : board.getVisible(p.getMainTargets().get(0).getPosition()).stream()
                        .map(Square::getPlayers)
                        .flatMap(x -> x.stream())
                        .distinct()
                        .filter(x -> !p.getMainTargets().contains(x))
                        .filter(x -> !x.equals(p))
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
                break;
            case "chain2":
                targetFinder=p -> (p.getMainTargets().isEmpty()||p.getOptionalTargets().isEmpty()) ?
                        new ArrayList<>() : board.getVisible(p.getOptionalTargets().get(0).getPosition()).stream()
                        .map(Square::getPlayers)
                        .flatMap(x -> x.stream())
                        .distinct()
                        .filter(x -> !(p.getMainTargets().contains(x) || p.getOptionalTargets().contains(x)))
                        .filter(x -> !x.equals(p))
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
                break;
            case "myself":
                targetFinder= p -> Arrays.asList(Arrays.asList(p));
                break;
            case"2min":
                targetFinder= p -> board.getVisible(p.getPosition()).stream()
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
                        .collect(Collectors.toList());
                break;
            case"here":
                targetFinder=p -> Arrays.asList(p.getPosition().getPlayers().stream()
                        .distinct()
                        .filter(x -> (!x.equals(p)))
                        .collect(Collectors.toList()));
                break;
            case"tractor1":
                targetFinder=p -> {
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
                };
                break;
            case"tractor2":
                targetFinder=p -> board.getReachable(p.getPosition(), 2).stream()
                        .map(Square::getPlayers)
                        .flatMap(x -> x.stream())
                        .distinct()
                        .filter(x -> !x.equals(p))
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
                break;
            case"vortex1":
                targetFinder=p -> {
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
                };
                break;
            case"vortex2":
                targetFinder= p -> {
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
                };
                break;
            case"room":
                targetFinder= p -> {
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
                };
                break;
            case"1away":
                targetFinder= p -> board.getReachable(p.getPosition(), 1).stream()
                        .filter(x -> {
                            try {
                                return !x.equals(p.getPosition());
                            } catch (NotAvailableAttributeException e) {
                                throw new IllegalArgumentException("Some players do not have a position.");
                            }
                        })
                        .map(Square::getPlayers)
                        .collect(Collectors.toList());
                break;
            case"notSight":
                targetFinder=p -> board.getMap().stream()
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
                        .collect(Collectors.toList());
                break;
            case"1min":
                targetFinder=p -> board.getVisible(p.getPosition()).stream()
                        .filter(x -> !x.containsPlayer(p))
                        .map(Square::getPlayers)
                        .flatMap(x -> x.stream())
                        .distinct()
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
                break;
            case"2row":
                targetFinder= p -> {
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
                };
                break;
            case"allSquare":
                targetFinder=p -> {List<List<Player>> l = board.getVisible(p.getPosition()).stream()
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
                };
                break;
            case"previousSquare":
                targetFinder=p -> {
                    List<Player> l = new ArrayList<>(p.getMainTargets());
                    for(Player player : p.getMainTargets()){
                        l.addAll(player.getPreviousPosition().getPlayers());
                    }
                    return Arrays.asList(l);
                };
                break;
            case"cardinal1":
                targetFinder= p -> {
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
                };
                break;
            case"cardinal2":
                targetFinder=p -> {
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
                };
                break;
            case"hereDifferent":
                targetFinder=p -> p.getPosition().getPlayers().stream()
                        .distinct()
                        .filter(x -> !x.equals(p))
                        .filter(x -> !p.getMainTargets().contains(x))
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
                break;
            case"sight3":
                targetFinder=p -> {
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
                };
                break;
            case"2steps":
                targetFinder=p -> {
                    List<List<Player>> targets = new ArrayList<>();
                    for (Direction d : Direction.values()) {
                        List<List<Player>> close =board.getSquaresInLineIgnoringWalls(p.getPosition(), d)
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
                };
                break;
            case"all1Away":
                targetFinder=p -> Arrays.asList(board.getReachable(p.getPosition(), 1).stream()
                        .map(Square::getPlayers)
                        .flatMap(x -> x.stream())
                        .distinct()
                        .collect(Collectors.toList()));
                break;
            default:
                targetFinder=p -> {
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
                        for (int w = i + 1; w < directionalTargets.size(); w++) {
                            targets.addAll(cartesian(directionalTargets.get(i), directionalTargets.get(w)));
                            for (int k = w + 1; k < directionalTargets.size(); k++) {
                                targets.addAll(cartesian(cartesian(directionalTargets.get(i), directionalTargets.get(w)), directionalTargets.get(k)));
                            }
                        }
                    }
                    return targets;
                };
        }

        return targetFinder;
    }

    @VisibleForTesting
    public static DestinationFinder getDestinationFinder(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        Json j = new Json();
        int move = Integer.parseInt(j.getMove(weaponName,fireModeName));
        String where = j.getWhere(weaponName, fireModeName);
        DestinationFinder destinationFinder;

        if(move==0){
            destinationFinder=(p, t) -> new ArrayList<>();
        }else if (where.equals("myself")){
            destinationFinder=(p, t) -> {List<Square > l = board.getReachable(p.getPosition(), move); l.remove(p.getPosition()); return l;};
        }else {
            String moveType = j.getMoveType(weaponName, fireModeName);

            switch (moveType) {
                case "onHit":
                    destinationFinder = (p, t) -> t.isEmpty() ? new ArrayList<>() : board.getReachable(t.get(0).getPosition(), move);
                    break;
                case "tractor1":
                    destinationFinder = (p, t) -> board.getVisible(p.getPosition()).stream()
                            .distinct()
                            .filter(x -> {
                                try {
                                    return t.isEmpty() || board.getVisible(t.get(0).getPosition()).contains(x);
                                } catch (NotAvailableAttributeException e) {
                                    throw new IllegalArgumentException("Some players do not have a position.");
                                }
                            })
                            .collect(Collectors.toList());
                    break;
                case "tractor2":
                    destinationFinder = (p, t) -> Arrays.asList(p.getPosition());
                    break;
                case "vortex1":
                    destinationFinder = (p, t) -> board.getVisible(p.getPosition()).stream()
                            .filter(x -> {
                                try {
                                    return t.isEmpty() || board.getReachable(t.get(0).getPosition(), move).contains(x);
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
                            .collect(Collectors.toList());
                    break;
                case "vortex2":
                    destinationFinder = (p, t) -> p.getMainTargets().isEmpty() ? new ArrayList<Square>() : Arrays.asList(p.getMainTargets().get(0).getPosition());
                    break;
                case "dash1":
                    destinationFinder = (p, t) -> t.isEmpty() ? new ArrayList<>() : Arrays.asList(t.get(0).getPosition());
                    break;
                case "dash2":
                    destinationFinder = (p, t) -> {
                        for (Player temp : t) {
                            if (board.getDistance(p.getPosition(), temp.getPosition()) > 1) {
                                return Arrays.asList(temp.getPosition());
                            }
                        }
                        List<Square> res = new ArrayList<>();
                        res.add(t.get(0).getPosition());
                        for (Direction d : Direction.values()) {
                            if (board.getSquaresInLine(p.getPosition(), d).contains(t.get(0).getPosition())) {
                                for (Square sq : board.getSquaresInLine(p.getPosition(), d)) {
                                    if (board.getDistance(sq, p.getPosition()) == 2) {
                                        res.add(sq);
                                    }
                                }
                            }

                        }
                        return res;

                    };
                    break;
                default:
                    destinationFinder = (p, t) -> {
                        List<Square> res = new ArrayList<>();
                        Square center = p.getPosition();
                        res.add(center);
                        for (Direction d : Direction.values()) {
                            res.addAll(board.getSquaresInLine(center, d).stream()
                                    .filter(x -> board.getDistance(center, x) < 3)
                                    .collect(Collectors.toList()));
                        }
                        return res;
                    };
            }
        }

       return destinationFinder;
    }

        /**
         * Inflicts damages and marks in consequence of a shoot
         *
         * @param damage                the amount of damages
         * @param marks                 the amount of marks
         * @retun                       the effect
         */
    @VisibleForTesting
    public static Effect createEffect(int damage, int marks){

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

    @VisibleForTesting
    public static List<List<Player>> cartesian (List<List<Player>> a, List<List<Player>> b){
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