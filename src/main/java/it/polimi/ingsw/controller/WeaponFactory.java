package it.polimi.ingsw.controller;

import com.google.gson.*;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Player;
import it.polimi.ingsw.model.board.Square;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;

import java.io.InputStreamReader;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static it.polimi.ingsw.model.board.Board.Direction;
import static it.polimi.ingsw.model.cards.Color.*;
import static it.polimi.ingsw.model.cards.FireMode.FireModeName.*;

/**
 * Factory class that creates Weapons loading attributes from a Json file. Each weapon has a number of different firemodes,
 * and each firemode has a TargetFinder (function finding lists of possible targets), a DestinationFinder (function selecting
 * possible destination for the target or the shooter) and an Effect (function applying the firemode effects to the game state).
 * This classes retrieves these three functions and other simple parameters.
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
    private static final String MAIN_TAG = "MAIN";

    private static final String COST_R = "costR";
    private static final String COST_B = "costB";
    private static final String COST_Y = "costY";

    private static final String TARGET = "target";
    private static final String ONE_VISIBLE = "1visible";
    private static final String ONE_OTHER_VISIBLE = "1otherVisible";
    private static final String ONE_OR_TWO_VISIBLE = "1or2visible";
    private static final String ONE_MAIN_TARGET = "1mainTarget";
    private static final String ONE_MAIN_TARGET_OR_OTHER_VIISBLE = "1mainTargetOrOtherVisible";
    private static final String THOR_ONE = "thor1";
    private static final String THOR_TWO = "thor2";
    private static final String PLASMA_GUN_ONE = "plasmaGun1";
    private static final String WHISPER = "whisper";
    private static final String ALL_SAME_SQUARE = "allSameSquare";
    private static final String TRACTOR_BEAM_MAIN = "tractorBeamMain";
    private static final String TRACTOR_BEAM_ALT = "tractorBeamAlt";
    private static final String VORTEX_CANNON_MAIN = "vortexCannonMain";
    private static final String VORTEX_CANNON_ONE = "vortexCannon1";
    private static final String OTHER_ROOM = "otherRoom";
    private static final String ADJACENT_SQUARE = "adjacentSquare";
    private static final String NOT_VISIBLE = "notVisible";
    private static final String NOT_SHOOTER_SQUARE_VISIBLE = "notShooterSquareVisible";
    private static final String FLAMETHROWER_MAIN = "flamethrowerMain";
    private static final String FLAMETHROWER_ALT = "flamethrowerAlt";
    private static final String GRENADE_LAUNCHER = "grenadeLauncher1";
    private static final String ROCKET_LAUNCHER_ONE = "rocketLauncher1";
    private static final String ROCKET_LAUNCHER_TWO = "rocketLauncher2";
    private static final String RAILGUN_MAIN = "railgunMain";
    private static final String RAILGUN_ALT = "railgunAlt";
    private static final String ONE_SAME_SQUARE = "1sameSquare";
    private static final String CYBERBLADE_ONE = "cyberblade1";
    private static final String ONE_OTHER_SAME_SQUARE = "1otherSameSquare";
    private static final String THREE_VISIBLE = "3visible";
    private static final String ONE_STEP_AWAY = "1stepAway";
    private static final String POWER_GLOVE_ALT = "powerGloveAlt";
    private static final String SHOCKWAVE_MAIN = "shockwaveMain";
    private static final String SHOCKWAVE_ALT = "shockwaveAlt";

    private static final String NONE = "none";
    private static final String SHOOTER_SQAURE = "shooterSquare";
    private static final String ADJACENT_TO_TARGET = "adjacentToTarget";
    private static final String TARGET_SQUARE = "targetSquare";
    private static final String SLEDGEHAMMER_ALT = "sledgehammerAlt";

    private static final String EFFECT = "effect";
    private static final String DMG = "dmg";
    private static final String MARK = "mark";
    private static final String STANDARD = "standard";
    private static final String MOVE = "move";
    private static final String MOVE_DAMAGE = "moveDamage";
    private static final String DAMAGE_MOVE = "damageMove";
    private static final String HELLION = "hellion";
    private static final String POWER_GLOVE = "powerGlove";

    private static final String MISSING_POSITION = "Some players do not have a position.";


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

    /**
     * Retrieves a jsonObject with information about the weapon from a file.
     * @param weaponName    weapon to describe
     * @return              information about the weapon
     */
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

    /**
     * Parses the weaponTree to get information about the color
     *
     * @param weaponTree    jsonObject to parse
     * @return              the color of the weapon
     */
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

    /**
     * Parses the weaponTree to get information about the full cost
     *
     * @param weaponTree    jsonObject to parse
     * @return              the full cost of the weapon
     */
    private AmmoPack getFullCost(JsonObject weaponTree) {
        try {
            int r = weaponTree.get(COST_R).getAsInt();
            int b = weaponTree.get(COST_B).getAsInt();
            int y = weaponTree.get(COST_Y).getAsInt();
            return new AmmoPack(r, b, y);
        } catch (JsonIOException e) {
            LOGGER.log(Level.SEVERE, "Unable to read cost in weaponTree", e);
        }
        return new AmmoPack(0,0,0);
    }

    /**
     * Computes the reduced cost of a weapon from its fullcost and its color.
     *
     * @param ammoPack      full cost
     * @param color         color of the weapon
     * @return              the reduced cost of the weapon
     */
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

    /**
     * Converts a String representing a FireModeName to the correct object
     *
     * @param name    string to convert
     * @return        proper FireModeName
     */
    private FireMode.FireModeName getFireModeName (String name){
        for(FireMode.FireModeName fn : FireMode.FireModeName.values()){
            if(fn.toString().equalsIgnoreCase(name)){
                return fn;
            }
        }
        LOGGER.log(Level.SEVERE, "Firemode name from weapon file does not match: {0}", name);
        return MAIN;
    }

    /**
     * Parses a jsonObject with information about the firemode to retrieve its cost
     *
     * @param fireMode  information about the firemode
     * @return          the cost of the firemode
     */
    private AmmoPack getFireModeCost (JsonObject fireMode) {
        try {
            if (fireMode.get(NAME_TAG).getAsString().equalsIgnoreCase(MAIN_TAG)) {
                return new AmmoPack(0, 0, 0);
            }
            int r = fireMode.get(COST_R).getAsInt();
            int b = fireMode.get(COST_B).getAsInt();
            int y = fireMode.get(COST_Y).getAsInt();
            return new AmmoPack(r, b, y);
        }catch (JsonIOException e) {
            LOGGER.log(Level.SEVERE, "Unable to read firemode cost from jsonTree", e);
        }
        return new AmmoPack(0,0,0);
    }

    /**
     * Returns a lambda implementing the TargetFinder. Each possible lambda is associated with a string that can also be found
     * in the file weapons.json.
     *
     * @param firemode  information about the firemode
     * @return          targetFinder logic
     */
    private TargetFinder getTargetFinder(JsonObject firemode) {

        String target = "";
        try {
            target = firemode.get(TARGET).getAsString();
        }catch(JsonIOException e){
            LOGGER.log(Level.SEVERE, "Unable to get target from jsonTree", e);
        }

        switch(target) {
            case ONE_VISIBLE:
                return p -> board.getVisible(p.getPosition()).stream()
                        .map(Square::getPlayers)
                        .flatMap(List::stream)
                        .distinct()
                        .filter(x -> !x.equals(p))
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
            case ONE_OTHER_VISIBLE:
                return p -> (p.getMainTargets().isEmpty() ? new ArrayList<>() : board.getVisible(p.getPosition()).stream()
                        .map(Square::getPlayers)
                        .flatMap(List::stream)
                        .distinct()
                        .filter(x -> !x.equals(p) && !p.getMainTargets().isEmpty())
                        .filter(x -> !p.getMainTargets().contains(x))
                        .map(Arrays::asList)
                        .collect(Collectors.toList()));
            case ONE_OR_TWO_VISIBLE:
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
            case ONE_MAIN_TARGET:
                return p -> (p.getMainTargets().stream()
                        .distinct()
                        .filter(x -> !p.getOptionalTargets().contains(x))
                        .map(Arrays::asList)
                        .collect(Collectors.toList()));
            case ONE_MAIN_TARGET_OR_OTHER_VIISBLE:
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
            case THOR_ONE:
                return p -> (p.getMainTargets().isEmpty()) ?
                        new ArrayList<>() : board.getVisible(p.getMainTargets().get(0).getPosition()).stream()
                        .map(Square::getPlayers)
                        .flatMap(List::stream)
                        .distinct()
                        .filter(x -> !p.getMainTargets().contains(x))
                        .filter(x -> !x.equals(p))
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
            case THOR_TWO:
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
            case PLASMA_GUN_ONE:
                return p -> {
                            if (!p.getMainTargets().isEmpty()) {
                                return Collections.singletonList(Collections.singletonList(p));
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
                                        return Collections.singletonList(Collections.singletonList(p));
                                    }
                                }
                            }
                            return new ArrayList<>();
                        };
            case WHISPER:
                return p -> board.getVisible(p.getPosition()).stream()
                        .map(Square::getPlayers)
                        .flatMap(List::stream)
                        .distinct()
                        .filter(x -> !x.equals(p))
                        .filter(x -> {
                            try {
                                return board.getDistance(x.getPosition(), p.getPosition()) >= 2;
                            } catch (NotAvailableAttributeException e) {
                                LOGGER.log(Level.SEVERE, MISSING_POSITION, e);
                                return false;
                            }
                        })
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
            case ALL_SAME_SQUARE:
                return p -> Collections.singletonList(p.getPosition().getPlayers().stream()
                        .distinct()
                        .filter(x -> (!x.equals(p)))
                        .collect(Collectors.toList()));
            case TRACTOR_BEAM_MAIN:
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
            case TRACTOR_BEAM_ALT:
                return p -> board.getReachable(p.getPosition(), 2).stream()
                        .map(Square::getPlayers)
                        .flatMap(List::stream)
                        .distinct()
                        .filter(x -> !x.equals(p))
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
            case VORTEX_CANNON_MAIN:
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
            case VORTEX_CANNON_ONE:
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
            case OTHER_ROOM:
                return p -> {
                            List<List<Square>> roomList = board.getVisible(p.getPosition()).stream()
                                    .map(Square::getRoomId)
                                    .distinct()
                                    .filter(x -> {
                                        try {
                                            return x != p.getPosition().getRoomId();
                                        } catch (NotAvailableAttributeException ex) {
                                            LOGGER.log(Level.SEVERE, MISSING_POSITION, ex);
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
            case ADJACENT_SQUARE:
                return p -> board.getReachable(p.getPosition(), 1).stream()
                        .filter(x -> !x.containsPlayer(p))
                        .map(Square::getPlayers)
                        .filter(x -> !x.isEmpty())
                        .collect(Collectors.toList());
            case NOT_VISIBLE:
                return p -> board.getMap().stream()
                        .filter(x -> {
                            try {
                                return !board.getVisible(p.getPosition()).contains(x);
                            } catch (NotAvailableAttributeException e) {
                                LOGGER.log(Level.SEVERE, MISSING_POSITION, e);
                                return false;
                            }
                        })
                        .map(Square::getPlayers)
                        .flatMap(List::stream)
                        .distinct()
                        .filter(x -> !x.equals(p))
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
            case NOT_SHOOTER_SQUARE_VISIBLE:
                return p -> board.getVisible(p.getPosition()).stream()
                        .filter(x -> !x.containsPlayer(p))
                        .map(Square::getPlayers)
                        .flatMap(List::stream)
                        .distinct()
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
            case FLAMETHROWER_MAIN:
                return p -> {
                            List<List<Player>> targets = new ArrayList<>();
                            for (Direction d : Direction.values()) {
                                List<List<Player>> close = board.getSquaresInLine(p.getPosition(), d).stream()
                                        .filter(x -> {
                                            try {
                                                return board.getReachable(p.getPosition(), 1).contains(x);
                                            } catch (NotAvailableAttributeException e) {
                                                LOGGER.log(Level.SEVERE, MISSING_POSITION, e);
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
                                                LOGGER.log(Level.SEVERE, MISSING_POSITION, e);
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
            case FLAMETHROWER_ALT:
                return p -> {
                            List<List<Player>> targets = new ArrayList<>();
                            for (Direction d : Direction.values()) {
                                List<Player> line = board.getSquaresInLine(p.getPosition(), d).stream()
                                        .filter(x -> {
                                            try {
                                                return board.getReachable(p.getPosition(), 2).contains(x);
                                            } catch (NotAvailableAttributeException e) {
                                                LOGGER.log(Level.SEVERE, MISSING_POSITION, e);
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
            case GRENADE_LAUNCHER:
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
            case ROCKET_LAUNCHER_ONE:
                return p -> {
                            if(!p.getMainTargets().isEmpty()){
                                return Collections.singletonList(Collections.singletonList(p));
                            }
                            List<Square> l = board.getReachable(p.getPosition(), 2);
                            for (Square s : l) {
                                List<Square> targets = board.getVisible(s).stream()
                                        .filter(x->!x.containsPlayer(p))
                                        .filter(x->!x.getPlayers().isEmpty())
                                        .collect(Collectors.toList());
                                if (!targets.isEmpty() && !s.containsPlayer(p)) {
                                    return Collections.singletonList(Collections.singletonList(p));
                                }
                            }
                            return new ArrayList<>();
                        };
            case ROCKET_LAUNCHER_TWO:
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
                            return Collections.singletonList(l);
                        };
            case RAILGUN_MAIN:
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
            case RAILGUN_ALT:
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
            case ONE_SAME_SQUARE:
                return p -> p.getPosition().getPlayers().stream()
                        .distinct()
                        .filter(x -> !x.equals(p))
                        .map(Arrays::asList)
                        .collect(Collectors.toList());

            case CYBERBLADE_ONE:
                return p -> {
                            if(!p.getMainTargets().isEmpty()){
                                return Collections.singletonList(Collections.singletonList(p));
                            }
                            for (Square s : board.getReachable(p.getPosition(), 1)) {
                                if (!s.containsPlayer(p)&&!s.getPlayers().isEmpty()) {
                                    return Collections.singletonList(Collections.singletonList(p));
                                }
                            }
                            return new ArrayList<>();
                        };
            case ONE_OTHER_SAME_SQUARE:
                return p -> p.getMainTargets().isEmpty()? new ArrayList<>():p.getPosition().getPlayers().stream()
                        .distinct()
                        .filter(x -> !x.equals(p))
                        .filter(x -> !p.getMainTargets().contains(x))
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
            case THREE_VISIBLE:
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
            case ONE_STEP_AWAY:
                return p -> board.getReachable(p.getPosition(), 1).stream()
                        .filter(x -> (!x.getPlayers().contains(p)))
                        .map(Square::getPlayers)
                        .flatMap(List::stream)
                        .distinct()
                        .map(Arrays::asList)
                        .collect(Collectors.toList());
            case POWER_GLOVE_ALT:
                return p -> {
                            List<List<Player>> targets = new ArrayList<>();
                            for (Direction d : Direction.values()) {
                                List<List<Player>> close = board.getSquaresInLineIgnoringWalls(p.getPosition(), d)
                                        .stream()
                                        .filter(x -> {
                                            try {
                                                return board.getReachable(p.getPosition(), 1).contains(x);
                                            } catch (NotAvailableAttributeException e) {
                                                LOGGER.log(Level.SEVERE, MISSING_POSITION, e);
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
                                                LOGGER.log(Level.SEVERE, MISSING_POSITION, e);
                                                return false;
                                            }
                                        })
                                        .filter(x -> {
                                            try {
                                                return !board.getReachable(p.getPosition(), 1).contains(x);
                                            } catch (NotAvailableAttributeException e) {
                                                LOGGER.log(Level.SEVERE, MISSING_POSITION, e);
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
            case SHOCKWAVE_MAIN:
                return p -> {
                            List<List<Player>> targets = new ArrayList<>();
                            List<List<List<Player>>> directionalTargets = new ArrayList<>();
                            for (Direction d : Direction.values()) {
                                List<List<Player>> candidate = board.getReachable(p.getPosition(), 1).stream()
                                        .filter(x -> {
                                            try {
                                                return board.getSquaresInLine(p.getPosition(), d).contains(x);
                                            } catch (NotAvailableAttributeException e) {
                                                LOGGER.log(Level.SEVERE, MISSING_POSITION, e);
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
            case SHOCKWAVE_ALT:
                return p -> Collections.singletonList(board.getReachable(p.getPosition(), 1).stream()
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

    /**
     * Returns a lambda implementing the DestinationFinder. Each possible lambda is associated with a string that can also be found
     * in the file weapons.json.
     *
     * @param firemode  information about the firemode
     * @return          destinationFinder logic
     */
    private DestinationFinder getDestinationFinder(JsonObject firemode) {
        String destination = "";
        try {
            destination = firemode.get("destination").getAsString();
        }catch (JsonIOException e){
            LOGGER.log(Level.SEVERE, "Unable to get destination from jsonTree", e);
        }

        switch(destination) {
            case NONE:
                return (p, t) -> new ArrayList<>();
            case PLASMA_GUN_ONE:
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
            case TRACTOR_BEAM_MAIN:
                return (p, t) -> board.getVisible(p.getPosition()).stream()
                        .distinct()
                        .filter(x -> {
                            try {
                                return !t.isEmpty() && board.getReachable(t.get(0).getPosition(), 2).contains(x);
                            } catch (NotAvailableAttributeException e) {
                                LOGGER.log(Level.SEVERE, MISSING_POSITION, e);
                                return false;
                            }
                        })
                        .collect(Collectors.toList());
            case SHOOTER_SQAURE:
                return (p, t) -> Collections.singletonList(p.getPosition());
            case VORTEX_CANNON_MAIN:
                return (p, t) -> board.getVisible(p.getPosition()).stream()
                        .filter(x -> {
                            try {
                                return !t.isEmpty() && board.getReachable(t.get(0).getPosition(), 1).contains(x);
                            } catch (NotAvailableAttributeException e) {
                                LOGGER.log(Level.SEVERE, MISSING_POSITION, e);
                                return false;
                            }
                        })
                        .filter(x -> !x.containsPlayer(p))
                        .distinct()
                        .collect(Collectors.toList());
            case VORTEX_CANNON_ONE:
                return (p, t) -> p.getMainTargets().isEmpty() ? new ArrayList<>() : Collections.singletonList(p.getMainTargets().get(0).getPosition());
            case ADJACENT_TO_TARGET:
                return (p, t) -> t.isEmpty() ? new ArrayList<>() : board.getReachable(t.get(0).getPosition(), 1);
            case ROCKET_LAUNCHER_ONE:
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
            case CYBERBLADE_ONE:
                return (p, t) -> {
                            if (p.getMainTargets().isEmpty()) {
                                return board.getReachable(p.getPosition(), 1).stream().filter(x -> !x.getPlayers().contains(p)&&!x.getPlayers().isEmpty()).collect(Collectors.toList());
                            }
                            return board.getReachable(p.getPosition(), 1).stream().filter(x -> !x.containsPlayer(p)).collect(Collectors.toList());
                        };
            case TARGET_SQUARE:
                return (p, t) -> t.isEmpty() ? new ArrayList<>() : Collections.singletonList(t.get(0).getPosition());
            case POWER_GLOVE_ALT:
                return (p, t) -> {
                            for (Player temp : t) {
                                if (board.getDistance(p.getPosition(), temp.getPosition()) > 1) {
                                    return Collections.singletonList(temp.getPosition());
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
            case SLEDGEHAMMER_ALT:
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

    /**
     * Returns a lambda implementing the effect. Each possible lambda is associated with a string that can also be found
     * in the file weapons.json.
     *
     * @param firemode  information about the firemode
     * @return          effect logic
     */
    private Effect getEffect(JsonObject firemode) {
        String effect = "";
        int tmpDmg = 0;
        int tmpMark = 0;
        try {
            effect = firemode.get(EFFECT).getAsString();
            tmpDmg = firemode.get(DMG).getAsInt();
            tmpMark = firemode.get(MARK).getAsInt();
        }catch (JsonIOException e) {
            LOGGER.log(Level.SEVERE, "Unable to read effect from jsonTree", e);
        }
        int dmg = tmpDmg;
        int mark = tmpMark;

        switch (effect) {
            case STANDARD:
                return createEffect(dmg, mark);
            case MOVE:
                return (shooter, target, destination) -> target.setPosition(destination);
            case MOVE_DAMAGE:
                return (shooter, target, destination) -> {
                    target.setPosition(destination);
                    target.sufferDamage(dmg, shooter);
                    target.addMarks(mark, shooter);
                };
            case DAMAGE_MOVE:
                return (shooter, target, destination) -> {
                    target.sufferDamage(dmg, shooter);
                    target.addMarks(mark, shooter);
                    target.setPosition(destination);
                };
            case HELLION:
                return (shooter, target, destination) -> {
                    target.sufferDamage(dmg, shooter);
                    board.getPlayersInside(target.getPosition()).forEach(x -> x.addMarks(mark, shooter));
                };
            case FLAMETHROWER_ALT:
                return (shooter, target, destination) -> {
                    if (board.getReachable(shooter.getPosition(), 1).contains(target.getPosition())) {
                        target.sufferDamage(dmg, shooter);
                    } else target.sufferDamage(1, shooter);
                };
            case POWER_GLOVE:
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

    /**
     * Creates a lambda implementing the most common effect (dealing damage and marks.
     *
     * @param damage    damage dealt
     * @param marks     marks dealt
     * @return          effect
     */
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
        return (shooter, target, destination) -> target.addMarks(marks, shooter);

    }

    /**
     * Takes two sets of possible target groups and computes the cartesian product of those two sets
     *
     * @param a     first group
     * @param b     second group
     * @return      cartesian product of the two groups
     */
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