package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Player;
import it.polimi.ingsw.model.board.Square;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.*;
import static it.polimi.ingsw.model.cards.Color.*;
import static it.polimi.ingsw.model.cards.FireMode.FireModeName.*;
import static it.polimi.ingsw.model.board.Board.Direction;

/**
 * Factory class to create a weapon.
 *
 * @author  marcobaga, davidealde
 */

public class WeaponFactory {


    private Board board;
    private static final Logger LOGGER = Logger.getLogger("serverLogger");
    private static ModelDataReader j = new ModelDataReader();

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

        Color color = j.getColor(weaponName);
        AmmoPack fullCost = getFullCost(weaponName);
        AmmoPack reducedCost = getReducedCost(weaponName);
        List<FireMode.FireModeName> nameList = getNameList(weaponName);
        List<FireMode> fireModeList = new ArrayList<>(nameList.size());
        AmmoPack fireModeCost;


        for (FireMode.FireModeName name : nameList) {
            effect = getEffect(weaponName, name);
            targetFinder = getTargetFinder(weaponName, name);
            destinationFinder = getDestinationFinder(weaponName, name);
            fireModeCost = getFireModeCost(weaponName, name);

            fireMode = new FireMode(name, fireModeCost, destinationFinder, targetFinder, effect);
            fireModeList.add(fireMode);
        }

        Weapon weapon = new Weapon(weaponName, color, fullCost, reducedCost, fireModeList, board);

        for (FireMode f : fireModeList) {
            f.setWeapon(weapon);
        }
        return weapon;
    }

    /**
     * Getters of some characteristics of the weapons
     * They should all be private but they're protected to be visible for testing
     *
     * @param weaponName            the name of the weapon of interest
     * @retun                       the value of interest
     */
    protected static Color getColor(Weapon.WeaponName weaponName) {
        return j.getColor(weaponName);
    }

    protected static AmmoPack getFullCost(Weapon.WeaponName weaponName) {

        AmmoPack r = j.getFullCostRed(weaponName);
        AmmoPack b = j.getFullCostBlue(weaponName);
        AmmoPack y = j.getFullCostYellow(weaponName);
        AmmoPack sum = new AmmoPack(0,0,0);
        sum.addAmmoPack(r);
        sum.addAmmoPack(b);
        sum.addAmmoPack(y);

        return sum;
    }

    protected AmmoPack getReducedCost(Weapon.WeaponName weaponName) {
        AmmoPack res = getFullCost(weaponName);
        Color c = getColor(weaponName);
        if (c == RED ) {
            res.subAmmoPack(new AmmoPack(1,0,0));
        }else if (c==BLUE) {
            res.subAmmoPack(new AmmoPack(0,1,0));
        }else if (c==YELLOW){
            res.subAmmoPack(new AmmoPack(0,0,1));
        }else{
            LOGGER.log(Level.SEVERE, "Error in retrieving weapon color");
        }
        return res;
    }

    protected static List<FireMode.FireModeName> getNameList(Weapon.WeaponName weaponName) {    //gestire eccezione diversamente

        int type = j.getFireModeList(weaponName);
        if (type==1) {
            return new ArrayList<>(Arrays.asList(MAIN));
        } else if (type==2) {
            return new ArrayList<>(Arrays.asList(MAIN, SECONDARY));
        } else if (type==3) {
            return new ArrayList<>(Arrays.asList(MAIN, OPTION1));
        } else if (type==4) {
            return new ArrayList<>(Arrays.asList(MAIN, OPTION1, OPTION2));
        } else {
            return new ArrayList<>(Arrays.asList(MAIN));  //gestire eccezione diversamente
        }
    }

    /**
     * Getters of some characteristics of the firemodes of the weapons
     * They should all be private but they're protected to be visible for testing
     *
     * @param weaponName            the name of the weapon of interest
     * @param fireModeName          the name of the firemode of interest
     * @retun                       the value of interest
     */

    protected static AmmoPack getFireModeCost (Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {
        if(fireModeName == MAIN) {
            return new AmmoPack(0,0,0);
        }

        AmmoPack r = j.getFireModeCostRed(weaponName, fireModeName);
        AmmoPack b = j.getFireModeCostBlue(weaponName, fireModeName);
        AmmoPack y = j.getFireModeCostYellow(weaponName, fireModeName);
        AmmoPack sum = new AmmoPack(0,0,0);
        sum.addAmmoPack(r);
        sum.addAmmoPack(b);
        sum.addAmmoPack(y);

        return sum;
        }

    protected Effect getEffect(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        String eff= j.getEff(weaponName, fireModeName);
        int dmg = j.getDmg(weaponName,fireModeName);
        int mark = j.getMark(weaponName,fireModeName);
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
            int dmg2 = j.getDmg2(weaponName,fireModeName);
            int steps = j.getSteps(weaponName,fireModeName);
            effect=((shooter, target, destination) -> {
                if (board.getReachable(shooter.getPosition(), steps).contains(target.getPosition())) {
                    target.sufferDamage(dmg, shooter);
                } else target.sufferDamage(dmg2, shooter);
            });
        }


        return effect;
    }

    private TargetFinder getTargetFinder(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {
        String key = j.getWhere(weaponName, fireModeName);
        TargetFinder targetFinder;
        Map<String, TargetFinder> targetFinderMap = new HashMap<>();

        switch(key) {
            case "sight":
                return p -> board.getVisible(p.getPosition()).stream()
                        .map(Square::getPlayers)
                        .flatMap(x -> x.stream())
                        .distinct()
                        .filter(x -> !x.equals(p))
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
            case "sight1optional":
                return p -> (p.getMainTargets().isEmpty() ? new ArrayList<>() : board.getVisible(p.getPosition()).stream()
                        .map(Square::getPlayers)
                        .flatMap(x -> x.stream())
                        .distinct()
                        .filter(x -> !x.equals(p) && !p.getMainTargets().isEmpty())
                        .filter(x -> !p.getMainTargets().contains(x))
                        .map(Arrays::asList)
                        .collect(Collectors.toList()));
            case "sight2":
                return p -> {
                    List<List<Player>> res = board.getVisible(p.getPosition()).stream()
                            .map(Square::getPlayers)
                            .flatMap(x -> x.stream())
                            .distinct()
                            .filter(x -> !x.equals(p))
                            .map(Arrays::asList)
                            .collect(Collectors.toList());
                    res.addAll(cartesian(res, res));
                    return res;
                };
            case "previous1":
                return p -> (p.getMainTargets().stream()
                        .distinct()
                        .filter(x -> p.getMainTargets().contains(x))
                        .filter(x -> !p.getOptionalTargets().contains(x))
                        .map(Arrays::asList)
                        .collect(Collectors.toList()));
            case "previous2":
                return p -> {
                    List<List<Player>> pastTargets = p.getMainTargets().stream()
                            .distinct()
                            .filter(x -> !p.getOptionalTargets().contains(x))
                            .map(Arrays::asList)
                            .collect(Collectors.toList());
                    List<List<Player>> others = board.getVisible(p.getPosition()).stream()
                            .map(Square::getPlayers)
                            .flatMap(x -> x.stream())
                            .distinct()
                            .filter(x -> !x.equals(p))
                            .filter(x -> !(p.getMainTargets().contains(x) || p.getOptionalTargets().contains(x)))
                            .map(Arrays::asList)
                            .collect(Collectors.toList());
                    others.addAll(cartesian(pastTargets, others));
                    others.addAll(pastTargets);
                    return others;
                };
            case "chain1":
                return p -> (p.getMainTargets().isEmpty()) ?
                        new ArrayList<>() : board.getVisible(p.getMainTargets().get(0).getPosition()).stream()
                        .map(Square::getPlayers)
                        .flatMap(x -> x.stream())
                        .distinct()
                        .filter(x -> !p.getMainTargets().contains(x))
                        .filter(x -> !x.equals(p))
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
            case "chain2":
                return p -> (p.getMainTargets().isEmpty() || p.getOptionalTargets().isEmpty()) ?
                        new ArrayList<>() : board
                        .getVisible(p.getOptionalTargets().get(0).getPosition()).stream()
                        .map(Square::getPlayers)
                        .flatMap(x -> x.stream())
                        .distinct()
                        .filter(x -> !(p.getMainTargets().contains(x) || p.getOptionalTargets().contains(x)))
                        .filter(x -> !x.equals(p))
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
            case "myself1":
                return p -> {
                            if (!p.getMainTargets().isEmpty()) {
                                return Arrays.asList(Arrays.asList(p));
                            }
                            List<Square> l = board.getReachable(p.getPosition(), 2);
                            List<Square> selectable = new ArrayList<>(l);
                            for (Square s : l) {
                                if (!s.containsPlayer(p)) {
                                    if (!board.getVisible(s).stream()
                                            .map(Square::getPlayers)
                                            .flatMap(x -> x.stream())
                                            .distinct()
                                            .filter(x -> !x.equals(p))
                                            .map(Arrays::asList)
                                            .collect(Collectors.toList())
                                            .isEmpty()) {
                                        return Arrays.asList(Arrays.asList(p));
                                    }
                                }
                            }
                            return new ArrayList<>();
                        };
            case "previous3":
                return p -> (p.getMainTargets().stream()
                        .distinct()
                        .map(Arrays::asList)
                        .collect(Collectors.toList()));
            case "2min":
                return p -> board.getVisible(p.getPosition()).stream()
                        .map(Square::getPlayers)
                        .flatMap(x -> x.stream())
                        .distinct()
                        .filter(x -> !x.equals(p))
                        .filter(x -> {
                            try {
                                return board.getDistance(x.getPosition(), p.getPosition()) >= 2;
                            } catch (NotAvailableAttributeException e) {
                                LOGGER.log(Level.SEVERE, "Some players do not have a position.", e);
                                return false;
                            }
                        })
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
            case "here":
                return p -> Arrays.asList(p.getPosition().getPlayers().stream()
                        .distinct()
                        .filter(x -> (!x.equals(p)))
                        .collect(Collectors.toList()));
            case "tractor1":
                return p -> {
                    List<Square> l = board.getVisible(p.getPosition());
                    List<Square> temp = new ArrayList<>();
                    for (Square s : l) {
                        temp.addAll(board.getReachable(s, 2));
                    }
                    return temp.stream()
                            .distinct()
                            .map(Square::getPlayers)
                            .flatMap(x -> x.stream())
                            .distinct()
                            .filter(x -> !x.equals(p))
                            .map(Arrays::asList)
                            .collect(Collectors.toList());
                };
            case "tractor2":
                return p -> board.getReachable(p.getPosition(), 2).stream()
                        .map(Square::getPlayers)
                        .flatMap(x -> x.stream())
                        .distinct()
                        .filter(x -> !x.equals(p))
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
            case "vortex1":
                return p -> {
                            List<Square> l = board.getVisible(p.getPosition());
                            List<Square> temp = new ArrayList<>();
                            for (Square s : l) {
                                temp.addAll(board.getReachable(s, 1));
                            }
                            return temp.stream()
                                    .map(Square::getPlayers)
                                    .flatMap(x -> x.stream())
                                    .distinct()
                                    .filter(x -> !x.equals(p))
                                    .map(Arrays::asList)
                                    .collect(Collectors.toList());
                        };
            case "vortex2":
                return p -> {
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
            case "room":
                return p -> {
                            List<List<Square>> roomList = board.getVisible(p.getPosition()).stream()
                                    .map(Square::getRoomId)
                                    .distinct()
                                    .filter(x -> {
                                        try {
                                            return x != p.getPosition().getRoomId();
                                        } catch (NotAvailableAttributeException ex) {
                                            LOGGER.log(Level.SEVERE, "Some players do not have a position.", ex);
                                            return false;
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
                                if (!temp.isEmpty()) {
                                    res.add(temp);
                                }
                                temp = new ArrayList<>();
                            }
                            return res;
                        };
            case "1away":
                return p -> board.getReachable(p.getPosition(), 1).stream()
                        .filter(x -> !x.containsPlayer(p))
                        .map(Square::getPlayers)
                        .filter(x -> !x.isEmpty())
                        .collect(Collectors.toList());
            case "notSight":
                return p -> board.getMap().stream()
                        .filter(x -> {
                            try {
                                return !board.getVisible(p.getPosition()).contains(x);
                            } catch (NotAvailableAttributeException e) {
                                LOGGER.log(Level.SEVERE, "Some players do not have a position.", e);
                                return false;
                            }
                        })
                        .map(Square::getPlayers)
                        .flatMap(x -> x.stream())
                        .distinct()
                        .filter(x -> !x.equals(p))
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
            case "1min":
                return p -> board.getVisible(p.getPosition()).stream()
                        .filter(x -> !x.containsPlayer(p))
                        .map(Square::getPlayers)
                        .flatMap(x -> x.stream())
                        .distinct()
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
            case "2row1":
                return p -> {
                            List<List<Player>> targets = new ArrayList<>();
                            for (Direction d : Direction.values()) {
                                List<List<Player>> close = board.getSquaresInLine(p.getPosition(), d).stream()
                                        .filter(x -> {
                                            try {
                                                return board.getReachable(p.getPosition(), 1).contains(x);
                                            } catch (NotAvailableAttributeException e) {
                                                LOGGER.log(Level.SEVERE, "Some players do not have a position.", e);
                                                return false;
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
                                                return board.getReachable(p.getPosition(), 2).contains(x) && !board.getReachable(p.getPosition(), 1).contains(x);
                                            } catch (NotAvailableAttributeException e) {
                                                LOGGER.log(Level.SEVERE, "Some players do not have a position.", e);
                                                return false;
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
            case "2row2":
                return p -> {
                            List<List<Player>> targets = new ArrayList<>();
                            for (Direction d : Direction.values()) {
                                List<Player> line = board.getSquaresInLine(p.getPosition(), d).stream()
                                        .filter(x -> {
                                            try {
                                                return board.getReachable(p.getPosition(), 2).contains(x);
                                            } catch (NotAvailableAttributeException e) {
                                                LOGGER.log(Level.SEVERE, "Some players do not have a position.", e);
                                                return false;
                                            }
                                        })
                                        .map(Square::getPlayers)
                                        .flatMap(x -> x.stream())
                                        .distinct()
                                        .collect(Collectors.toList());
                                if(!line.isEmpty()) {
                                    targets.add(line);
                                }
                            }
                            return targets;
                        };
            case "allSquare":
                return p -> {
                            List<List<Player>> l = board.getVisible(p.getPosition()).stream()
                                    .filter(x -> !x.containsPlayer(p))
                                    .map(Square::getPlayers)
                                    .filter(x->!x.isEmpty())
                                    .collect(Collectors.toList());
                            List<Player> inSameRoomAsPlayer = p.getPosition().getPlayers().stream().filter(x -> !x.equals(p)).collect(Collectors.toList());
                            if(!inSameRoomAsPlayer.isEmpty()){
                                l.add(inSameRoomAsPlayer);
                            }
                            return l;
                        };
            case "myself3":
                return p -> {
                            List<Square> l = board.getReachable(p.getPosition(), 2);
                            for (Square s : l) {
                                List<List<Player>> targets = board.getVisible(s).stream()
                                        .filter(x -> {
                                            try {
                                                return !x.equals(p.getPosition());
                                            } catch (NotAvailableAttributeException e) {
                                                LOGGER.log(Level.SEVERE, "Some players do not have a position.", e);
                                                return false;
                                            }
                                        })
                                        .map(Square::getPlayers)
                                        .flatMap(x -> x.stream())
                                        .distinct()
                                        .map(Arrays::asList)
                                        .collect(Collectors.toList());
                                if (!targets.isEmpty() && !s.containsPlayer(p)) {
                                    return Arrays.asList(Arrays.asList(p));
                                }
                            }
                            return new ArrayList<>();
                        };
            case "previousSquare":
                return p -> {
                            if (p.getMainTargets().isEmpty()) {
                                return new ArrayList<>();
                            }
                            List<Player> l = new ArrayList<>(p.getMainTargets());
                            for (Player player : p.getMainTargets()) {
                                for (Player opt2target : player.getPreviousPosition().getPlayers())
                                    if (!l.contains(opt2target))
                                        l.add(opt2target);
                            }
                            return Arrays.asList(l);
                        };
            case "cardinal1":
                return p -> {
                            List<List<Player>> targets = new ArrayList<>();
                            for (Direction d : Direction.values()) {
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
                                    .filter(x -> !x.equals(p))
                                    .map(Arrays::asList)
                                    .collect(Collectors.toList())
                            );
                            return targets;
                        };
            case "cardinal2":
                return p -> {
                            List<List<Player>> targets = new ArrayList<>();
                            List<List<Player>> close = p.getPosition().getPlayers().stream()
                                    .distinct()
                                    .filter(x -> !x.equals(p))
                                    .map(Arrays::asList)
                                    .collect(Collectors.toList());
                            targets.addAll(close);
                            targets.addAll(cartesian(close, close));
                            for (Direction d : Direction.values()) {
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
            case "here2":
                return p -> p.getPosition().getPlayers().stream()
                        .distinct()
                        .filter(x -> !x.equals(p))
                        .map(Arrays::asList)
                        .collect(Collectors.toList());

            case "myself2":
                return p -> {
                            for (Square s : board.getReachable(p.getPosition(), 1)) {
                                if (!s.containsPlayer(p)) {
                                    if (s.getPlayers().size() > 1 || !s.getPlayers().contains(p)) {
                                        return Arrays.asList(Arrays.asList(p));
                                    }
                                }
                            }
                            return new ArrayList<>();
                        };
            case "hereDifferent":
                return p -> p.getPosition().getPlayers().stream()
                        .distinct()
                        .filter(x -> !x.equals(p))
                        .filter(x -> !p.getMainTargets().contains(x))
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
            case "sight3":
                return p -> {
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
            case "1away2":
                return p -> Arrays.asList(board.getReachable(p.getPosition(), 1).stream()
                        .filter(x -> {
                            try {
                                return !x.equals(p.getPosition());
                            } catch (NotAvailableAttributeException e) {
                                LOGGER.log(Level.SEVERE, "Some players do not have a position.", e);
                                return false;
                            }
                        })
                        .map(Square::getPlayers)
                        .flatMap(x -> x.stream())
                        .distinct()
                        .collect(Collectors.toList()));
            case "1away3":
                return p -> board.getReachable(p.getPosition(), 1).stream()
                        .filter(x -> {
                            try {
                                return !x.equals(p.getPosition());
                            } catch (NotAvailableAttributeException e) {
                                LOGGER.log(Level.SEVERE, "Some players do not have a position.", e);
                                return false;
                            }
                        })
                        .map(Square::getPlayers)
                        .flatMap(x -> x.stream())
                        .distinct()
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
            case "2steps":
                return p -> {
                            List<List<Player>> targets = new ArrayList<>();
                            for (Direction d : Direction.values()) {
                                List<List<Player>> close = board.getSquaresInLineIgnoringWalls(p.getPosition(), d)
                                        .stream()
                                        .filter(x -> {
                                            try {
                                                return board.getReachable(p.getPosition(), 1).contains(x);
                                            } catch (NotAvailableAttributeException e) {
                                                LOGGER.log(Level.SEVERE, "Some players do not have a position.", e);
                                                return false;
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
                                                LOGGER.log(Level.SEVERE, "Some players do not have a position.", e);
                                                return false;
                                            }
                                        })
                                        .filter(x -> {
                                            try {
                                                return !board.getReachable(p.getPosition(), 1).contains(x);
                                            } catch (NotAvailableAttributeException e) {
                                                LOGGER.log(Level.SEVERE, "Some players do not have a position.", e);
                                                return false;
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
            case "1awayDifferent":
                return p -> {
                            List<List<Player>> targets = new ArrayList<>();
                            List<List<List<Player>>> directionalTargets = new ArrayList<>();
                            for (Direction d : Direction.values()) {
                                List<List<Player>> candidate = board.getReachable(p.getPosition(), 1).stream()
                                        .filter(x -> {
                                            try {
                                                return board.getSquaresInLine(p.getPosition(), d).contains(x);
                                            } catch (NotAvailableAttributeException e) {
                                                LOGGER.log(Level.SEVERE, "Some players do not have a position.", e);
                                                return false;
                                            }
                                        })
                                        .map(Square::getPlayers)
                                        .flatMap(x -> x.stream())
                                        .distinct()
                                        .map(Arrays::asList)
                                        .collect(Collectors.toList());
                                System.out.println("Looking in direction " + d + " there are: " + candidate);
                                if (!candidate.isEmpty()) {
                                    directionalTargets.add(candidate);
                                }
                            }
                            System.out.println("These are the directional targets: " + directionalTargets);
                            for (int i = 0; i < directionalTargets.size(); i++) {
                                targets.addAll(directionalTargets.get(i));
                                System.out.println("adding all these dir targets: " + directionalTargets.get(i));
                                for (int j = i + 1; j < directionalTargets.size(); j++) {
                                    targets.addAll(cartesian(directionalTargets.get(i), directionalTargets.get(j)));
                                    System.out.println("Adding all these pairs: " + cartesian(directionalTargets.get(i), directionalTargets.get(j)));
                                    for (int k = j + 1; k < directionalTargets.size(); k++) {
                                        targets.addAll(cartesian(cartesian(directionalTargets.get(i), directionalTargets.get(j)), directionalTargets.get(k)));
                                        System.out.println("Adding all these triples: " + cartesian(cartesian(directionalTargets.get(i), directionalTargets.get(j)), directionalTargets.get(k)));
                                    }
                                }
                            }
                            return targets;
                        };
            default:
                return p -> new ArrayList<>();
        }
    }

    private DestinationFinder getDestinationFinder(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {
        int move = j.getMove(weaponName,fireModeName);
        String where = j.getWhere(weaponName, fireModeName);
        String key;
        if(move==0){
            key="normal";
        }else if (where.equals("myself1")||where.equals("myself2")||where.equals("myself3")){
            key="myself";
        }else {
            key = j.getMoveType(weaponName, fireModeName);
        }

        switch(key) {
            case "normal":
                return (p, t) -> new ArrayList<>();
            case "myself":
                return (p, t) -> {
                            List<Square> l = board.getReachable(p.getPosition(), move);
                            l.remove(p.getPosition());
                            if (!p.getMainTargets().isEmpty()) {
                                return l;
                            }
                            List<Square> selectable = new ArrayList<>(l);
                            for (Square s : l) {
                                if (board.getVisible(s).stream()
                                        .map(Square::getPlayers)
                                        .flatMap(x -> x.stream())
                                        .distinct()
                                        .filter(x -> !x.equals(p))
                                        .map(Arrays::asList)
                                        .collect(Collectors.toList())
                                        .isEmpty()) {
                                    selectable.remove(s);
                                }
                            }
                            return selectable;
                        };
            case "tractor1":
                return (p, t) -> board.getVisible(p.getPosition()).stream()
                        .distinct()
                        .filter(x -> {
                            try {
                                return !t.isEmpty() && board.getReachable(t.get(0).getPosition(), move).contains(x);
                            } catch (NotAvailableAttributeException e) {
                                LOGGER.log(Level.SEVERE, "Some players do not have a position.", e);
                                return false;
                            }
                        })
                        .collect(Collectors.toList());
            case "tractor2":
                return (p, t) -> Arrays.asList(p.getPosition());
            case "vortex1":
                return (p, t) -> board.getVisible(p.getPosition()).stream()
                        .filter(x -> {
                            try {
                                return !t.isEmpty() && board.getReachable(t.get(0).getPosition(), move).contains(x);
                            } catch (NotAvailableAttributeException e) {
                                LOGGER.log(Level.SEVERE, "Some players do not have a position.", e);
                                return false;
                            }
                        })
                        .filter(x -> !x.containsPlayer(p))
                        .distinct()
                        .collect(Collectors.toList());
            case "vortex2":
                return (p, t) -> p.getMainTargets().isEmpty() ? new ArrayList<>() : Arrays.asList(p.getMainTargets().get(0).getPosition());
            case "onHit":
                return (p, t) -> t.isEmpty() ? new ArrayList<>() : board.getReachable(t.get(0).getPosition(), move);
            case "onHit2":
                return (p, t) -> t.isEmpty() ? new ArrayList<>() : board.getReachable(t.get(0).getPosition(), move);
            case "myself2":
                return (p, t) -> {
                            List<Square> l = board.getReachable(p.getPosition(), 2);
                            l.remove(p.getPosition());
                            if (!p.getMainTargets().isEmpty()) {
                                return l;
                            }
                            List<Square> res = new ArrayList<>(l);
                            for (Square s : l) {
                                if (board.getVisible(s).stream()
                                        .filter(x -> !x.containsPlayer(p))
                                        .map(Square::getPlayers)
                                        .flatMap(x -> x.stream())
                                        .distinct()
                                        .map(Arrays::asList)
                                        .collect(Collectors.toList()).isEmpty()) {
                                    res.remove(s);
                                }
                            }
                            return res;
                        };
            case "myself3":
                return (p, t) -> {
                            if (p.getMainTargets().isEmpty()) {
                                return board.getReachable(p.getPosition(), move).stream().filter(x -> x.getPlayers().size() > 1 || !x.getPlayers().contains(p)).collect(Collectors.toList());
                            }
                            return board.getReachable(p.getPosition(), move).stream().filter(x -> !x.containsPlayer(p)).collect(Collectors.toList());
                        };
            case "onHit3":
                return (p, t) -> board.getReachable(p.getPosition(), move);
            case "dash1":
                return (p, t) -> t.isEmpty() ? new ArrayList<>() : Arrays.asList(t.get(0).getPosition());
            case "dash2":
                return (p, t) -> {
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
            case "push":
                return (p, t) -> {
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
            default:
                return (p, t) -> new ArrayList<>();
        }
    }

    private Effect createEffect(int damage, int marks){

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

    private List<List<Player>> cartesian (List<List<Player>> a, List<List<Player>> b){
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