package it.polimi.ingsw.controller;

import com.google.gson.*;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Player;
import it.polimi.ingsw.model.board.Square;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static it.polimi.ingsw.model.board.Board.Direction;
import static it.polimi.ingsw.model.cards.Color.*;
import static it.polimi.ingsw.model.cards.FireMode.FireModeName.*;

/**
 * Factory class that creates Weapons loading attributes from a Json file.
 *
 * @author  marcobaga
 */

public class WeaponFactory {


    private Board board;
    private static final Logger LOGGER = Logger.getLogger("serverLogger");
    private static final String WEAPONS_FILE = "weapons.json";

    private static final String MODES_TAG = "modes";
    private static final String NAME_TAG = "name";
    private static final String COLOR_TAG = "color";

    /**
     * Constructs a weapon factory with a reference to the game board.
     *
     * @param board         the game board
     */
    public WeaponFactory(Board board){this.board = board;}

    /**
     *Creates a Weapon object according to its name
     *
     * @param       weaponName  the name of the weapon to create
     * @return      the Weapon created
     */
    public Weapon createWeapon(Weapon.WeaponName weaponName) {

        FireMode fireMode;
        TargetFinder targetFinder;
        DestinationFinder destinationFinder;
        Effect effect;

        JsonObject weaponTree = getWeaponTree(weaponName);

        Color color = getColor(weaponTree);
        AmmoPack fullCost = getFullCost(weaponTree);
        AmmoPack reducedCost = getReducedCost(fullCost, color);

        JsonArray fireModeArray = weaponTree.getAsJsonArray(MODES_TAG);
        List<FireMode> fireModeList = new ArrayList<>(fireModeArray.size());
        AmmoPack fireModeCost;

        for (JsonElement firemodeElement : fireModeArray) {
            JsonObject firemode = firemodeElement.getAsJsonObject();
            FireMode.FireModeName name = getFireModeName(firemode.get(NAME_TAG).getAsString());
            effect = getEffect(firemode);
            targetFinder = getTargetFinder(firemode);
            destinationFinder = getDestinationFinder(firemode);
            fireModeCost = getFireModeCost(firemode);

            fireMode = new FireMode(name, fireModeCost, destinationFinder, targetFinder, effect);
            fireModeList.add(fireMode);
        }

        Weapon weapon = new Weapon(weaponName, color, fullCost, reducedCost, fireModeList, board);

        for (FireMode f : fireModeList) {
            f.setWeapon(weapon);
        }
        return weapon;
    }

    private JsonObject getWeaponTree(Weapon.WeaponName weaponName){
        JsonParser parser = new JsonParser();
        JsonObject weaponTree = new JsonObject();
        try {
            JsonElement weaponElement = parser.parse(new InputStreamReader(this.getClass().getResourceAsStream("/" + WEAPONS_FILE)));
            JsonObject weaponList = weaponElement.getAsJsonObject();
            weaponTree = weaponList.getAsJsonObject(weaponName.toString());
        }catch (JsonIOException e) {
            LOGGER.log(Level.SEVERE, "Unable to read weapon from file", e);
        }
        return weaponTree;
    }

    public Color getColor(JsonObject weaponTree) {
        String color = "";
        try {
            color = weaponTree.get(COLOR_TAG).getAsString();
        } catch (JsonIOException e) {
            LOGGER.log(Level.SEVERE, "Unable to read color in weaponTree", e);
        }
        for(Color c : Color.values()){
            if(c.toString().equalsIgnoreCase(color)){
                return c;
            }
        }
        LOGGER.log(Level.SEVERE, "Color from weapon file does not match: {0}", color);
        return PURPLE;
    }

    private AmmoPack getFullCost(JsonObject weaponTree) {
        try {
            int r = weaponTree.get("costR").getAsInt();
            int b = weaponTree.get("costB").getAsInt();
            int y = weaponTree.get("costY").getAsInt();
            return new AmmoPack(r, b, y);
        } catch (JsonIOException e) {
            LOGGER.log(Level.SEVERE, "Unable to read cost in weaponTree", e);
        }
        return new AmmoPack(0,0,0);
    }

    private AmmoPack getReducedCost(AmmoPack ammoPack, Color color) {
        AmmoPack reduced = new AmmoPack(ammoPack.getRedAmmo(), ammoPack.getBlueAmmo(), ammoPack.getYellowAmmo());
        if (color == RED ) {
            reduced.subAmmoPack(new AmmoPack(1,0,0));
        }else if (color == BLUE) {
            reduced.subAmmoPack(new AmmoPack(0,1,0));
        }else if (color == YELLOW){
            reduced.subAmmoPack(new AmmoPack(0,0,1));
        }else{
            LOGGER.log(Level.SEVERE, "Error in computing reduced cost");
        }
        return reduced;
    }

    private FireMode.FireModeName getFireModeName (String name){
        for(FireMode.FireModeName fn : FireMode.FireModeName.values()){
            if(fn.toString().equalsIgnoreCase(name)){
                return fn;
            }
        }
        LOGGER.log(Level.SEVERE, "Firemode name from weapon file does not match: {0}", name);
        return MAIN;
    }

    private AmmoPack getFireModeCost (JsonObject fireMode) {
        try {
            if (fireMode.get("name").getAsString().equalsIgnoreCase("MAIN")) {
                return new AmmoPack(0, 0, 0);
            }
            int r = fireMode.get("costR").getAsInt();
            int b = fireMode.get("costB").getAsInt();
            int y = fireMode.get("costY").getAsInt();
            return new AmmoPack(r, b, y);
        }catch (JsonIOException e) {
            LOGGER.log(Level.SEVERE, "Unable to read firemode cost from jsonTree", e);
        }
        return new AmmoPack(0,0,0);
    }

    private TargetFinder getTargetFinder(JsonObject firemode) {

        String target = "";
        try {
            target = firemode.get("target").getAsString();
        }catch(JsonIOException e){
            LOGGER.log(Level.SEVERE, "Unable to get target from jsonTree", e);
        }

        switch(target) {
            case "1visible":
                return p -> board.getVisible(p.getPosition()).stream()
                        .map(Square::getPlayers)
                        .flatMap(List::stream)
                        .distinct()
                        .filter(x -> !x.equals(p))
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
            case "1otherVisible":
                return p -> (p.getMainTargets().isEmpty() ? new ArrayList<>() : board.getVisible(p.getPosition()).stream()
                        .map(Square::getPlayers)
                        .flatMap(List::stream)
                        .distinct()
                        .filter(x -> !x.equals(p) && !p.getMainTargets().isEmpty())
                        .filter(x -> !p.getMainTargets().contains(x))
                        .map(Arrays::asList)
                        .collect(Collectors.toList()));
            case "1or2visible":
                return p -> {
                    List<List<Player>> res = board.getVisible(p.getPosition()).stream()
                            .map(Square::getPlayers)
                            .flatMap(List::stream)
                            .distinct()
                            .filter(x -> !x.equals(p))
                            .map(Arrays::asList)
                            .collect(Collectors.toList());
                    res.addAll(cartesian(res, res));
                    return res;
                };
            case "1mainTarget":
                return p -> (p.getMainTargets().stream()
                        .distinct()
                        .filter(x -> !p.getOptionalTargets().contains(x))
                        .map(Arrays::asList)
                        .collect(Collectors.toList()));
            case "1mainTargetOrOtherVisible":
                return p -> {
                    if(p.getMainTargets().isEmpty()){
                        return new ArrayList<>();
                    }
                    List<List<Player>> pastTargets = p.getMainTargets().stream()
                            .distinct()
                            .filter(x -> !p.getOptionalTargets().contains(x))
                            .map(Arrays::asList)
                            .collect(Collectors.toList());
                    List<List<Player>> others = board.getVisible(p.getPosition()).stream()
                            .map(Square::getPlayers)
                            .flatMap(List::stream)
                            .distinct()
                            .filter(x -> !x.equals(p))
                            .filter(x -> !(p.getMainTargets().contains(x) || p.getOptionalTargets().contains(x)))
                            .map(Arrays::asList)
                            .collect(Collectors.toList());
                    others.addAll(cartesian(pastTargets, others));
                    others.addAll(pastTargets);
                    return others;
                };
            case "thor1":
                return p -> (p.getMainTargets().isEmpty()) ?
                        new ArrayList<>() : board.getVisible(p.getMainTargets().get(0).getPosition()).stream()
                        .map(Square::getPlayers)
                        .flatMap(List::stream)
                        .distinct()
                        .filter(x -> !p.getMainTargets().contains(x))
                        .filter(x -> !x.equals(p))
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
            case "thor2":
                return p -> (p.getMainTargets().isEmpty() || p.getOptionalTargets().isEmpty()) ?
                        new ArrayList<>() : board
                        .getVisible(p.getOptionalTargets().get(0).getPosition()).stream()
                        .map(Square::getPlayers)
                        .flatMap(List::stream)
                        .distinct()
                        .filter(x -> !(p.getMainTargets().contains(x) || p.getOptionalTargets().contains(x)))
                        .filter(x -> !x.equals(p))
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
            case "plasmaGun1":
                return p -> {
                            if (!p.getMainTargets().isEmpty()) {
                                return Arrays.asList(Arrays.asList(p));
                            }
                            List<Square> l = board.getReachable(p.getPosition(), 2);
                            for (Square s : l) {
                                if (!s.containsPlayer(p)) {
                                    if (board.getVisible(s).stream()
                                            .map(Square::getPlayers)
                                            .flatMap(List::stream)
                                            .distinct()
                                            .filter(x -> !x.equals(p))
                                            .map(Arrays::asList)
                                            .count()!=0) {
                                        return Arrays.asList(Arrays.asList(p));
                                    }
                                }
                            }
                            return new ArrayList<>();
                        };
            case "whisper":
                return p -> board.getVisible(p.getPosition()).stream()
                        .map(Square::getPlayers)
                        .flatMap(List::stream)
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
            case "allSameSquare":
                return p -> Arrays.asList(p.getPosition().getPlayers().stream()
                        .distinct()
                        .filter(x -> (!x.equals(p)))
                        .collect(Collectors.toList()));
            case "tractorBeamMain":
                return p -> {
                    List<Square> l = board.getVisible(p.getPosition());
                    List<Square> temp = new ArrayList<>();
                    for (Square s : l) {
                        temp.addAll(board.getReachable(s, 2));
                    }
                    return temp.stream()
                            .distinct()
                            .map(Square::getPlayers)
                            .flatMap(List::stream)
                            .distinct()
                            .filter(x -> !x.equals(p))
                            .map(Arrays::asList)
                            .collect(Collectors.toList());
                };
            case "tractorBeamAlt":
                return p -> board.getReachable(p.getPosition(), 2).stream()
                        .map(Square::getPlayers)
                        .flatMap(List::stream)
                        .distinct()
                        .filter(x -> !x.equals(p))
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
            case "vortexCannonMain":
                return p -> {
                            List<Square> l = board.getVisible(p.getPosition());
                            List<Square> temp = new ArrayList<>();
                            for (Square s : l) {
                                temp.addAll(board.getReachable(s, 1));
                            }
                            return temp.stream()
                                    .map(Square::getPlayers)
                                    .flatMap(List::stream)
                                    .distinct()
                                    .filter(x -> !x.equals(p))
                                    .map(Arrays::asList)
                                    .collect(Collectors.toList());
                        };
            case "vortexCannon1":
                return p -> {
                            if (p.getMainTargets().isEmpty()) {
                                return new ArrayList<>();
                            }
                            List<List<Player>> lp = board.getReachable(p.getMainTargets().get(0).getPosition(), 1).stream()
                                    .map(Square::getPlayers)
                                    .flatMap(List::stream)
                                    .distinct()
                                    .filter(x -> !x.equals(p))
                                    .filter(x -> !p.getMainTargets().contains(x))
                                    .map(Arrays::asList)
                                    .collect(Collectors.toList());
                            List<List<Player>> res = cartesian(lp, lp);
                            res.addAll(lp);
                            return res;
                        };
            case "otherRoom":
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
            case "adjacentSquare":
                return p -> board.getReachable(p.getPosition(), 1).stream()
                        .filter(x -> !x.containsPlayer(p))
                        .map(Square::getPlayers)
                        .filter(x -> !x.isEmpty())
                        .collect(Collectors.toList());
            case "notVisible":
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
                        .flatMap(List::stream)
                        .distinct()
                        .filter(x -> !x.equals(p))
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
            case "notShooterSquareVisible":
                return p -> board.getVisible(p.getPosition()).stream()
                        .filter(x -> !x.containsPlayer(p))
                        .map(Square::getPlayers)
                        .flatMap(List::stream)
                        .distinct()
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
            case "flamethrowerMain":
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
                                        .flatMap(List::stream)
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
                                        .flatMap(List::stream)
                                        .distinct()
                                        .map(Arrays::asList)
                                        .collect(Collectors.toList());
                                targets.addAll(close);
                                targets.addAll(far);
                                targets.addAll(cartesian(close, far));
                            }
                            return targets;
                        };
            case "flamethrowerAlt":
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
                                        .flatMap(List::stream)
                                        .distinct()
                                        .collect(Collectors.toList());
                                if(!line.isEmpty()) {
                                    targets.add(line);
                                }
                            }
                            return targets;
                        };
            case "grenadeLauncher1":
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
            case "rocketLauncher1":
                return p -> {
                            if(!p.getMainTargets().isEmpty()){
                                return Arrays.asList(Arrays.asList(p));
                            }
                            List<Square> l = board.getReachable(p.getPosition(), 2);
                            for (Square s : l) {
                                List<Square> targets = board.getVisible(s).stream()
                                        .filter(x->!x.containsPlayer(p))
                                        .filter(x->!x.getPlayers().isEmpty())
                                        .collect(Collectors.toList());
                                if (!targets.isEmpty() && !s.containsPlayer(p)) {
                                    return Arrays.asList(Arrays.asList(p));
                                }
                            }
                            return new ArrayList<>();
                        };
            case "rocketLauncher2":
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
            case "railgunMain":
                return p -> {
                            List<List<Player>> targets = new ArrayList<>();
                            for (Direction d : Direction.values()) {
                                List<List<Player>> single = board.getSquaresInLineIgnoringWalls(p.getPosition(), d)
                                        .stream()
                                        .map(Square::getPlayers)
                                        .flatMap(List::stream)
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
            case "railgunAlt":
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
                                        .flatMap(List::stream)
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
            case "1sameSquare":
                return p -> p.getPosition().getPlayers().stream()
                        .distinct()
                        .filter(x -> !x.equals(p))
                        .map(Arrays::asList)
                        .collect(Collectors.toList());

            case "cyberblade1":
                return p -> {
                            if(!p.getMainTargets().isEmpty()){
                                return Arrays.asList(Arrays.asList(p));
                            }
                            for (Square s : board.getReachable(p.getPosition(), 1)) {
                                if (!s.containsPlayer(p)) {
                                    if (!s.getPlayers().isEmpty()) {
                                        return Arrays.asList(Arrays.asList(p));
                                    }
                                }
                            }
                            return new ArrayList<>();
                        };
            case "1otherSameSquare":
                return p -> p.getPosition().getPlayers().stream()
                        .distinct()
                        .filter(x -> !x.equals(p))
                        .filter(x -> !p.getMainTargets().contains(x))
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
            case "3visible":
                return p -> {
                            List<List<Player>> targets = new ArrayList<>();
                            List<List<Player>> single = board.getVisible(p.getPosition()).stream()
                                    .map(Square::getPlayers)
                                    .flatMap(List::stream)
                                    .distinct()
                                    .filter(x -> !x.equals(p))
                                    .map(Arrays::asList)
                                    .collect(Collectors.toList());
                            targets.addAll(single);
                            targets.addAll(cartesian(single, single));
                            targets.addAll(cartesian(cartesian(single, single), single));
                            return targets;
                        };
            case "1stepAway":
                return p -> board.getReachable(p.getPosition(), 1).stream()
                        .filter(x -> (!x.getPlayers().contains(p)))
                        .map(Square::getPlayers)
                        .flatMap(List::stream)
                        .distinct()
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
            case "powerGloveAlt":
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
                                        .flatMap(List::stream)
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
                                        .flatMap(List::stream)
                                        .distinct()
                                        .map(Arrays::asList)
                                        .collect(Collectors.toList());
                                targets.addAll(close);
                                targets.addAll(far);
                                targets.addAll(cartesian(close, far));
                            }
                            return targets;
                        };
            case "shockwaveMain":
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
                                        .flatMap(List::stream)
                                        .distinct()
                                        .map(Arrays::asList)
                                        .collect(Collectors.toList());
                                if (!candidate.isEmpty()) {
                                    directionalTargets.add(candidate);
                                }
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
            case "shockwaveAlt":
                return p -> Arrays.asList(board.getReachable(p.getPosition(), 1).stream()
                        .filter(x -> (!x.getPlayers().contains(p)))
                        .map(Square::getPlayers)
                        .flatMap(List::stream)
                        .distinct()
                        .collect(Collectors.toList()));

            default:
                LOGGER.log(Level.SEVERE, "Target name does not match: {0}", target);
                return p -> new ArrayList<>();
        }
    }

    private DestinationFinder getDestinationFinder(JsonObject firemode) {
        String destination = "";
        try {
            destination = firemode.get("destination").getAsString();
        }catch (JsonIOException e){
            LOGGER.log(Level.SEVERE, "Unable to get destination from jsonTree", e);
        }

        switch(destination) {
            case "none":
                return (p, t) -> new ArrayList<>();
            case "plasmaGun1":
                return (p, t) -> {
                            List<Square> l = board.getReachable(p.getPosition(), 2);
                            l.remove(p.getPosition());
                            if (!p.getMainTargets().isEmpty()) {
                                return l;
                            }
                            List<Square> selectable = new ArrayList<>(l);
                            for (Square s : l) {
                                if (board.getVisible(s).stream()
                                        .map(Square::getPlayers)
                                        .flatMap(List::stream)
                                        .distinct()
                                        .filter(x -> !x.equals(p))
                                        .map(Arrays::asList)
                                        .count()==0) {
                                    selectable.remove(s);
                                }
                            }
                            return selectable;
                        };
            case "tractorBeamMain":
                return (p, t) -> board.getVisible(p.getPosition()).stream()
                        .distinct()
                        .filter(x -> {
                            try {
                                return !t.isEmpty() && board.getReachable(t.get(0).getPosition(), 2).contains(x);
                            } catch (NotAvailableAttributeException e) {
                                LOGGER.log(Level.SEVERE, "Some players do not have a position.", e);
                                return false;
                            }
                        })
                        .collect(Collectors.toList());
            case "shooterSquare":
                return (p, t) -> Arrays.asList(p.getPosition());
            case "vortexCannonMain":
                return (p, t) -> board.getVisible(p.getPosition()).stream()
                        .filter(x -> {
                            try {
                                return !t.isEmpty() && board.getReachable(t.get(0).getPosition(), 1).contains(x);
                            } catch (NotAvailableAttributeException e) {
                                LOGGER.log(Level.SEVERE, "Some players do not have a position.", e);
                                return false;
                            }
                        })
                        .filter(x -> !x.containsPlayer(p))
                        .distinct()
                        .collect(Collectors.toList());
            case "vortexCannon1":
                return (p, t) -> p.getMainTargets().isEmpty() ? new ArrayList<>() : Arrays.asList(p.getMainTargets().get(0).getPosition());
            case "adjacentToTarget":
                return (p, t) -> t.isEmpty() ? new ArrayList<>() : board.getReachable(t.get(0).getPosition(), 1);
            case "rocketLauncher1":
                return (p, t) -> {
                            List<Square> l = board.getReachable(p.getPosition(), 2);
                            l.remove(p.getPosition());
                            if (!p.getMainTargets().isEmpty()) {
                                return l;
                            }
                            List<Square> res = new ArrayList<>(l);
                            for (Square s : l) {
                                if (board.getVisible(s).stream()
                                        .filter(x -> !x.equals(s))
                                        .map(Square::getPlayers)
                                        .flatMap(List::stream)
                                        .distinct()
                                        .filter(x -> !x.equals(p))
                                        .map(Arrays::asList)
                                        .count()==0) {
                                    res.remove(s);
                                }
                            }
                            return res;
                        };
            case "cyberblade1":
                return (p, t) -> {
                            if (p.getMainTargets().isEmpty()) {
                                return board.getReachable(p.getPosition(), 1).stream().filter(x -> !x.getPlayers().contains(p)&&!x.getPlayers().isEmpty()).collect(Collectors.toList());
                            }
                            return board.getReachable(p.getPosition(), 1).stream().filter(x -> !x.containsPlayer(p)).collect(Collectors.toList());
                        };
            case "targetSquare":
                return (p, t) -> t.isEmpty() ? new ArrayList<>() : Arrays.asList(t.get(0).getPosition());
            case "powerGloveAlt":
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
            case "sledgehammerAlt":
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
                LOGGER.log(Level.SEVERE, "Destination name does not match: {0}", destination);
                return (p, t) -> new ArrayList<>();
        }
    }

    private Effect getEffect(JsonObject firemode) {
        String effect = "";
        int tmpDmg = 0;
        int tmpMark = 0;
        try {
            effect = firemode.get("effect").getAsString();
            tmpDmg = firemode.get("dmg").getAsInt();
            tmpMark = firemode.get("mark").getAsInt();
        }catch (JsonIOException e) {
            LOGGER.log(Level.SEVERE, "Unable to read effect from jsonTree", e);
        }
        int dmg = tmpDmg;
        int mark = tmpMark;

        switch (effect) {
            case "standard":
                return createEffect(dmg, mark);
            case "move":
                return (shooter, target, destination) -> target.setPosition(destination);
            case "moveDamage":
                return (shooter, target, destination) -> {
                    target.setPosition(destination);
                    target.sufferDamage(dmg, shooter);
                    target.addMarks(mark, shooter);
                };
            case "damageMove":
                return (shooter, target, destination) -> {
                    target.sufferDamage(dmg, shooter);
                    target.addMarks(mark, shooter);
                    target.setPosition(destination);
                };
            case "hellion":
                return (shooter, target, destination) -> {
                    target.sufferDamage(dmg, shooter);
                    board.getPlayersInside(target.getPosition()).forEach(x -> x.addMarks(mark, shooter));
                };
            case "flamethrowerAlt":
                return (shooter, target, destination) -> {
                    if (board.getReachable(shooter.getPosition(), 1).contains(target.getPosition())) {
                        target.sufferDamage(dmg, shooter);
                    } else target.sufferDamage(1, shooter);
                };
            case "powerGlove":
                return (shooter, target, destination) -> {
                    shooter.setPosition(destination);
                    target.sufferDamage(dmg, shooter);
                    target.addMarks(mark, shooter);
                };
            default:
                LOGGER.log(Level.SEVERE, "Effect name does not match: {0}", effect);
                return createEffect(dmg, mark);
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
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        List<List<Player>> res = new ArrayList<>();
        List<Player> temp;

        for (List<Player> l : atemp){
            for (Player p : btemp){
                if(!l.contains(p)) {
                    temp = new ArrayList<>(l);
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