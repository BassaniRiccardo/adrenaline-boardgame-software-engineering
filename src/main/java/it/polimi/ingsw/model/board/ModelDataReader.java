package it.polimi.ingsw.model.board;

import java.io.FileNotFoundException;
import java.io.FileReader;

import it.polimi.ingsw.model.cards.Color;
import it.polimi.ingsw.model.cards.FireMode;
import it.polimi.ingsw.model.cards.Weapon;
import com.google.gson.*;


/**
 * Offers thr methods to read from a .json file the characteristics of a weapon and the keywords that map the weapon
 * into the algorithms that describes the mechanism of the weapon
 * the files is weaponsFile.json
 *
 * @author  davidealde
 */
public class ModelDataReader {

    private static         JsonParser parser = new JsonParser();

    /**
     * Constructor of a json class
     */
    public ModelDataReader() {
    }

//---METHODS FOR WeaponFactory-------------------------------------------------------------------------------------------------------------

    /**
     * Reads from the json file the color of the weapon of interest
     *
     * @param weaponName the name of the weapon
     * @return the color of the weapon
     */
    public String getColor(Weapon.WeaponName weaponName) {

        try {
            JsonElement jsontree = parser.parse(new FileReader("weaponsFile.json"));
            JsonObject je = jsontree.getAsJsonObject();
            JsonArray ja = je.getAsJsonArray("weapons");
            for (Object o : ja) {
                JsonObject weapon = (JsonObject) o;
                String name = weapon.get("name").getAsString();
                if (name.equals(weaponName.toString())) {
                    return weapon.get("color").getAsString();
                }
            }
        }
        catch (JsonIOException e) {
            return "IOException";
        } catch (JsonSyntaxException e) {
            return "JsonSyntaxException";
        } catch (FileNotFoundException e) {
            return "FileNotFoundException";
        }

        return "NOT FOUND";
    }

    /**
     * Reads from the json file the red full cost of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @return                      the red full cost of the weapon
     */
    public String getFullCostRed(Weapon.WeaponName weaponName) {

        try {
            JsonElement jsontree = parser.parse(new FileReader("weaponsFile.json"));
            JsonObject je = jsontree.getAsJsonObject();
            JsonArray ja = je.getAsJsonArray("weapons");
            for (Object o : ja) {
                JsonObject weapon = (JsonObject) o;
                String name = weapon.get("name").getAsString();
                if (name.equals(weaponName.toString())) {
                    return weapon.get("costR").getAsString();
                }
            }
        }
        catch (JsonIOException e) {
            return "IOException";
        } catch (JsonSyntaxException e) {
            return "JsonSyntaxException";
        } catch (FileNotFoundException e) {
            return "FileNotFoundException";
        }

        return "NOT FOUND";
    }

    /**
     * Reads from the json file the blue full cost of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @return                      the blue full cost of the weapon
     */
    public String getFullCostBlue(Weapon.WeaponName weaponName) {

        try {
            JsonElement jsontree = parser.parse(new FileReader("weaponsFile.json"));
            JsonObject je = jsontree.getAsJsonObject();
            JsonArray ja = je.getAsJsonArray("weapons");
            for (Object o : ja) {
                JsonObject weapon = (JsonObject) o;
                String name = weapon.get("name").getAsString();
                if (name.equals(weaponName.toString())) {
                    return weapon.get("costB").getAsString();
                }
            }
        }
        catch (JsonIOException e) {
            return "IOException";
        } catch (JsonSyntaxException e) {
            return "JsonSyntaxException";
        } catch (FileNotFoundException e) {
            return "FileNotFoundException";
        }

        return "NOT FOUND";
    }

    /**
     * Reads from the json file the yellow full cost of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @return                      the yellow full cost of the weapon
     */
    public String getFullCostYellow(Weapon.WeaponName weaponName) {

        try {
            JsonElement jsontree = parser.parse(new FileReader("weaponsFile.json"));
            JsonObject je = jsontree.getAsJsonObject();
            JsonArray ja = je.getAsJsonArray("weapons");
            for (Object o : ja) {
                JsonObject weapon = (JsonObject) o;
                String name = weapon.get("name").getAsString();
                if (name.equals(weaponName.toString())) {
                    return weapon.get("costY").getAsString();
                }
            }
        }
        catch (JsonIOException e) {
            return "IOException";
        } catch (JsonSyntaxException e) {
            return "JsonSyntaxException";
        } catch (FileNotFoundException e) {
            return "FileNotFoundException";
        }

        return "NOT FOUND";
    }

    /**
     * Reads from the json file the list of fire modes of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @return                      the list of fire modes of the weapon
     */
    public String getNameList(Weapon.WeaponName weaponName) {

        try {
            JsonElement jsontree = parser.parse(new FileReader("weaponsFile.json"));
            JsonObject je = jsontree.getAsJsonObject();
            JsonArray ja = je.getAsJsonArray("weapons");
            for (Object o : ja) {
                JsonObject weapon = (JsonObject) o;
                String name = weapon.get("name").getAsString();
                if (name.equals(weaponName.toString())) {
                    return weapon.get("type").getAsString();
                }
            }
        }
        catch (JsonIOException e) {
            return "IOException";
        } catch (JsonSyntaxException e) {
            return "JsonSyntaxException";
        } catch (FileNotFoundException e) {
            return "FileNotFoundException";
        }
        return "NOT FOUND";
    }

    /**
     * Reads from the json file the number of the targets of the firemode of interest of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @param fireModeName          the firemode name
     * @return                      the number of the targets of the firemode of the weapon
     */
    public String getTargetNumber(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        try {
            JsonElement jsontree = parser.parse(new FileReader("weaponsFile.json"));
            JsonObject je = jsontree.getAsJsonObject();
            JsonArray ja = je.getAsJsonArray("weapons");
            for (Object o : ja) {
                JsonObject weapon = (JsonObject) o;
                String name = weapon.get("name").getAsString();
                if (name.equals(weaponName.toString())) {
                    JsonArray modesList = weapon.getAsJsonArray("modes");
                    for (Object k : modesList) {
                        JsonObject mode = (JsonObject) k;
                        String modeName = mode.get("mode").getAsString();
                        if (modeName.equals(fireModeName.toString())) {
                            return mode.get("targetsN").getAsString();
                        }
                    }
                }
            }
        }catch (JsonIOException e) {
                return "IOException";
            } catch (JsonSyntaxException e) {
                return "JsonSyntaxException";
            } catch (FileNotFoundException e) {
                return "FileNotFoundException";
            }
        return "NOT FOUND";
    }

    /**
     * Reads from the json file the red cost of the firemode of interest of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @param fireModeName          the firemode name
     * @return                      the red cost of the firemode of the weapon
     */
    public String getFireModeCostRed(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        try {
            JsonElement jsontree = parser.parse(new FileReader("weaponsFile.json"));
            JsonObject je = jsontree.getAsJsonObject();
            JsonArray ja = je.getAsJsonArray("weapons");
            for (Object o : ja) {
                JsonObject weapon = (JsonObject) o;
                String name = weapon.get("name").getAsString();
                if (name.equals(weaponName.toString())) {
                    JsonArray modesList = weapon.getAsJsonArray("modes");
                    for (Object k : modesList) {
                        JsonObject mode = (JsonObject) k;
                        String modeName = mode.get("mode").getAsString();
                        if (modeName.equals(fireModeName.toString())) {
                            return mode.get("costR").getAsString();
                        }
                    }
                }
            }
        }catch (JsonIOException e) {
            return "IOException";
        } catch (JsonSyntaxException e) {
            return "JsonSyntaxException";
        } catch (FileNotFoundException e) {
            return "FileNotFoundException";
        }
        return "NOT FOUND";
    }

    /**
     * Reads from the json file the blue cost of the firemode of interest of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @param fireModeName          the firemode name
     * @return                      the blue cost of the firemode of the weapon
     */
    public String getFireModeCostBlue(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        try {
            JsonElement jsontree = parser.parse(new FileReader("weaponsFile.json"));
            JsonObject je = jsontree.getAsJsonObject();
            JsonArray ja = je.getAsJsonArray("weapons");
            for (Object o : ja) {
                JsonObject weapon = (JsonObject) o;
                String name = weapon.get("name").getAsString();
                if (name.equals(weaponName.toString())) {
                    JsonArray modesList = weapon.getAsJsonArray("modes");
                    for (Object k : modesList) {
                        JsonObject mode = (JsonObject) k;
                        String modeName = mode.get("mode").getAsString();
                        if (modeName.equals(fireModeName.toString())) {
                            return mode.get("costB").getAsString();
                        }
                    }
                }
            }
        }catch (JsonIOException e) {
            return "IOException";
        } catch (JsonSyntaxException e) {
            return "JsonSyntaxException";
        } catch (FileNotFoundException e) {
            return "FileNotFoundException";
        }
        return "NOT FOUND";
    }

    /**
     * Reads from the json file the yellow cost of the firemode of interest of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @param fireModeName          the firemode name
     * @return                      the yellow cost of the firemode of the weapon
     */
    public String getFireModeCostYellow(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        try {
            JsonElement jsontree = parser.parse(new FileReader("weaponsFile.json"));
            JsonObject je = jsontree.getAsJsonObject();
            JsonArray ja = je.getAsJsonArray("weapons");
            for (Object o : ja) {
                JsonObject weapon = (JsonObject) o;
                String name = weapon.get("name").getAsString();
                if (name.equals(weaponName.toString())) {
                    JsonArray modesList = weapon.getAsJsonArray("modes");
                    for (Object k : modesList) {
                        JsonObject mode = (JsonObject) k;
                        String modeName = mode.get("mode").getAsString();
                        if (modeName.equals(fireModeName.toString())) {
                            return mode.get("costY").getAsString();
                        }
                    }
                }
            }
        }catch (JsonIOException e) {
            return "IOException";
        } catch (JsonSyntaxException e) {
            return "JsonSyntaxException";
        } catch (FileNotFoundException e) {
            return "FileNotFoundException";
        }
        return "NOT FOUND";
    }

    /**
     * Reads from the json file the number of moves permitted of the firemode of interest of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @param fireModeName          the firemode name
     * @return                      the number of moves permitted of the firemode of the weapon
     */
    public String getMove(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        try {
            JsonElement jsontree = parser.parse(new FileReader("weaponsFile.json"));
            JsonObject je = jsontree.getAsJsonObject();
            JsonArray ja = je.getAsJsonArray("weapons");
            for (Object o : ja) {
                JsonObject weapon = (JsonObject) o;
                String name = weapon.get("name").getAsString();
                if (name.equals(weaponName.toString())) {
                    JsonArray modesList = weapon.getAsJsonArray("modes");
                    for (Object k : modesList) {
                        JsonObject mode = (JsonObject) k;
                        String modeName = mode.get("mode").getAsString();
                        if (modeName.equals(fireModeName.toString())) {
                            return mode.get("move").getAsString();
                        }
                    }
                }
            }
        }catch (JsonIOException e) {
            return "IOException";
        } catch (JsonSyntaxException e) {
            return "JsonSyntaxException";
        } catch (FileNotFoundException e) {
            return "FileNotFoundException";
        }
        return "NOT FOUND";
    }

    /**
     * Reads from the json file the damages of the firemode of interest of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @param fireModeName          the firemode name
     * @return                      the damages of the firemode of the weapon
     */
    public String getDmg(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        try {
            JsonElement jsontree = parser.parse(new FileReader("weaponsFile.json"));
            JsonObject je = jsontree.getAsJsonObject();
            JsonArray ja = je.getAsJsonArray("weapons");
            for (Object o : ja) {
                JsonObject weapon = (JsonObject) o;
                String name = weapon.get("name").getAsString();
                if (name.equals(weaponName.toString())) {
                    JsonArray modesList = weapon.getAsJsonArray("modes");
                    for (Object k : modesList) {
                        JsonObject mode = (JsonObject) k;
                        String modeName = mode.get("mode").getAsString();
                        if (modeName.equals(fireModeName.toString())) {
                            return mode.get("dmg").getAsString();
                        }
                    }
                }
            }
        }catch (JsonIOException e) {
            return "IOException";
        } catch (JsonSyntaxException e) {
            return "JsonSyntaxException";
        } catch (FileNotFoundException e) {
            return "FileNotFoundException";
        }
        return "NOT FOUND";
    }

    /**
     * Reads from the json file the other amount of damages of the firemode of interest of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @param fireModeName          the firemode name
     * @return                      the other amount of damages of the firemode of the weapon
     */
    public String getDmg2(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        try {
            JsonElement jsontree = parser.parse(new FileReader("weaponsFile.json"));
            JsonObject je = jsontree.getAsJsonObject();
            JsonArray ja = je.getAsJsonArray("weapons");
            for (Object o : ja) {
                JsonObject weapon = (JsonObject) o;
                String name = weapon.get("name").getAsString();
                if (name.equals(weaponName.toString())) {
                    JsonArray modesList = weapon.getAsJsonArray("modes");
                    for (Object k : modesList) {
                        JsonObject mode = (JsonObject) k;
                        String modeName = mode.get("mode").getAsString();
                        if (modeName.equals(fireModeName.toString())) {
                            return mode.get("dmg2").getAsString();
                        }
                    }
                }
            }
        }catch (JsonIOException e) {
            return "IOException";
        } catch (JsonSyntaxException e) {
            return "JsonSyntaxException";
        } catch (FileNotFoundException e) {
            return "FileNotFoundException";
        }
        return "NOT FOUND";
    }

    /**
     * Reads from the json file the marks of the firemode of interest of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @param fireModeName          the firemode name
     * @return                      the marks of the firemode of the weapon
     */
    public String getMark(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        try {
            JsonElement jsontree = parser.parse(new FileReader("weaponsFile.json"));
            JsonObject je = jsontree.getAsJsonObject();
            JsonArray ja = je.getAsJsonArray("weapons");
            for (Object o : ja) {
                JsonObject weapon = (JsonObject) o;
                String name = weapon.get("name").getAsString();
                if (name.equals(weaponName.toString())) {
                    JsonArray modesList = weapon.getAsJsonArray("modes");
                    for (Object k : modesList) {
                        JsonObject mode = (JsonObject) k;
                        String modeName = mode.get("mode").getAsString();
                        if (modeName.equals(fireModeName.toString())) {
                            return mode.get("mark").getAsString();
                        }
                    }
                }
            }
        }catch (JsonIOException e) {
            return "IOException";
        } catch (JsonSyntaxException e) {
            return "JsonSyntaxException";
        } catch (FileNotFoundException e) {
            return "FileNotFoundException";
        }
        return "NOT FOUND";
    }

    /**
     * Reads from the json file the steps of the firemode of interest of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @param fireModeName          the firemode name
     * @return                      the steps of the firemode of the weapon
     */
    public String getSteps(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        try {
            JsonElement jsontree = parser.parse(new FileReader("weaponsFile.json"));
            JsonObject je = jsontree.getAsJsonObject();
            JsonArray ja = je.getAsJsonArray("weapons");
            for (Object o : ja) {
                JsonObject weapon = (JsonObject) o;
                String name = weapon.get("name").getAsString();
                if (name.equals(weaponName.toString())) {
                    JsonArray modesList = weapon.getAsJsonArray("modes");
                    for (Object k : modesList) {
                        JsonObject mode = (JsonObject) k;
                        String modeName = mode.get("mode").getAsString();
                        if (modeName.equals(fireModeName.toString())) {
                            return mode.get("steps").getAsString();
                        }
                    }
                }
            }
        }catch (JsonIOException e) {
            return "IOException";
        } catch (JsonSyntaxException e) {
            return "JsonSyntaxException";
        } catch (FileNotFoundException e) {
            return "FileNotFoundException";
        }
        return "NOT FOUND";
    }

    /**
     * Reads from the json file the effect of the firemode of interest of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @param fireModeName          the firemode name
     * @return                      the effect of the firemode of the weapon
     */
    public String getEff(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        try {
            JsonElement jsontree = parser.parse(new FileReader("weaponsFile.json"));
            JsonObject je = jsontree.getAsJsonObject();
            JsonArray ja = je.getAsJsonArray("weapons");
            for (Object o : ja) {
                JsonObject weapon = (JsonObject) o;
                String name = weapon.get("name").getAsString();
                if (name.equals(weaponName.toString())) {
                    JsonArray modesList = weapon.getAsJsonArray("modes");
                    for (Object k : modesList) {
                        JsonObject mode = (JsonObject) k;
                        String modeName = mode.get("mode").getAsString();
                        if (modeName.equals(fireModeName.toString())) {
                            return mode.get("effect").getAsString();
                        }
                    }
                }
            }
        }catch (JsonIOException e) {
            return "IOException";
        } catch (JsonSyntaxException e) {
            return "JsonSyntaxException";
        } catch (FileNotFoundException e) {
            return "FileNotFoundException";
        }
        return "NOT FOUND";
    }

    /**
     * Reads from the json file the type of targets of the firemode of interest of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @param fireModeName          the firemode name
     * @return                      the type of targets of the firemode of the weapon
     */
    public String getWhere(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        try {
            JsonElement jsontree = parser.parse(new FileReader("weaponsFile.json"));
            JsonObject je = jsontree.getAsJsonObject();
            JsonArray ja = je.getAsJsonArray("weapons");
            for (Object o : ja) {
                JsonObject weapon = (JsonObject) o;
                String name = weapon.get("name").getAsString();
                if (name.equals(weaponName.toString())) {
                    JsonArray modesList = weapon.getAsJsonArray("modes");
                    for (Object k : modesList) {
                        JsonObject mode = (JsonObject) k;
                        String modeName = mode.get("mode").getAsString();
                        if (modeName.equals(fireModeName.toString())) {
                            return mode.get("where").getAsString();
                        }
                    }
                }
            }
        }catch (JsonIOException e) {
            return "IOException";
        } catch (JsonSyntaxException e) {
            return "JsonSyntaxException";
        } catch (FileNotFoundException e) {
            return "FileNotFoundException";
        }
        return "NOT FOUND";
    }

    /**
     * Reads from the json file the type of move of the firemode of interest of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @param fireModeName          the firemode name
     * @return                      the type of move of the firemode of the weapon
     */
    public String getMoveType(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        try {
            JsonElement jsontree = parser.parse(new FileReader("weaponsFile.json"));
            JsonObject je = jsontree.getAsJsonObject();
            JsonArray ja = je.getAsJsonArray("weapons");
            for (Object o : ja) {
                JsonObject weapon = (JsonObject) o;
                String name = weapon.get("name").getAsString();
                if (name.equals(weaponName.toString())) {
                    JsonArray modesList = weapon.getAsJsonArray("modes");
                    for (Object k : modesList) {
                        JsonObject mode = (JsonObject) k;
                        String modeName = mode.get("mode").getAsString();
                        if (modeName.equals(fireModeName.toString())) {
                            return mode.get("moveType").getAsString();
                        }
                    }
                }
            }
        }catch (JsonIOException e) {
            return "IOException";
        } catch (JsonSyntaxException e) {
            return "JsonSyntaxException";
        } catch (FileNotFoundException e) {
            return "FileNotFoundException";
        }
        return "NOT FOUND";
    }


//---METHODS FOR BoardConfigurer-------------------------------------------------------------------------------------------------------------

    public int getWSNumber(int boardNumber) {

        try {
            JsonElement jsontree = parser.parse(new FileReader("boardConfigParameters.json"));
            JsonObject je = jsontree.getAsJsonObject();
            JsonArray ja = je.getAsJsonArray("boards");
            for (Object o : ja) {
                JsonObject board = (JsonObject) o;
                int number = board.get("number").getAsInt();
                if (boardNumber==number) {
                    return  board.get("wSNumber").getAsInt();
                }
            }
        }
        catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getASNumber(int boardNumber) {

        try {
            JsonElement jsontree = parser.parse(new FileReader("boardConfigParameters.json"));
            JsonObject je = jsontree.getAsJsonObject();
            JsonArray ja = je.getAsJsonArray("boards");
            for (Object o : ja) {
                JsonObject board = (JsonObject) o;
                int number = board.get("number").getAsInt();
                if (boardNumber==number) {
                    return  board.get("aSNumber").getAsInt();
                }
            }
        }
        catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean getWallTXX(int boardNumber, int row, int column) {

        try {
            JsonElement jsontree = parser.parse(new FileReader("boardConfigParameters.json"));
            JsonObject je = jsontree.getAsJsonObject();
            JsonArray ja = je.getAsJsonArray("boards");
            for (Object o : ja) {
                JsonObject board = (JsonObject) o;
                int number = board.get("number").getAsInt();
                if (boardNumber==number) {
                    String key="wallT"+row+column;
                    int out=board.get(key).getAsInt();
                    if(out==1)
                        return true;
                    else
                        return false;
                }
            }
        }
        catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean getWallLXX(int boardNumber, int row, int column) {

        try {
            JsonElement jsontree = parser.parse(new FileReader("boardConfigParameters.json"));
            JsonObject je = jsontree.getAsJsonObject();
            JsonArray ja = je.getAsJsonArray("boards");
            for (Object o : ja) {
                JsonObject board = (JsonObject) o;
                int number = board.get("number").getAsInt();
                if (boardNumber==number) {
                    int out=board.get("wallL"+Integer.toString(row)+Integer.toString(column)).getAsInt();
                    if(out==1)
                        return true;
                    else
                        return false;
                }
            }
        }
        catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getASXID(int boardNumber, int n) {

        try {
            JsonElement jsontree = parser.parse(new FileReader("boardConfigParameters.json"));
            JsonObject je = jsontree.getAsJsonObject();
            JsonArray ja = je.getAsJsonArray("boards");
            for (Object o : ja) {
                JsonObject board = (JsonObject) o;
                int number = board.get("number").getAsInt();
                if (boardNumber==number) {
                    return  board.get("aS"+Integer.toString(n)+"ID").getAsInt();
                }
            }
        }
        catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return -1;

    }
    public int getWSXID(int boardNumber, int n) {

        try {
            JsonElement jsontree = parser.parse(new FileReader("boardConfigParameters.json"));
            JsonObject je = jsontree.getAsJsonObject();
            JsonArray ja = je.getAsJsonArray("boards");
            for (Object o : ja) {
                JsonObject board = (JsonObject) o;
                int number = board.get("number").getAsInt();
                if (boardNumber==number) {
                    return  board.get("wS"+Integer.toString(n)+"ID").getAsInt();
                }
            }
        }
        catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getASXRoomId(int boardNumber, int n) {

        try {
            JsonElement jsontree = parser.parse(new FileReader("boardConfigParameters.json"));
            JsonObject je = jsontree.getAsJsonObject();
            JsonArray ja = je.getAsJsonArray("boards");
            for (Object o : ja) {
                JsonObject board = (JsonObject) o;
                int number = board.get("number").getAsInt();
                if (boardNumber==number) {
                    return  board.get("aS"+Integer.toString(n)+"RoomID").getAsInt();
                }
            }
        }
        catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getWSXRoomId(int boardNumber, int n) {

        try {
            JsonElement jsontree = parser.parse(new FileReader("boardConfigParameters.json"));
            JsonObject je = jsontree.getAsJsonObject();
            JsonArray ja = je.getAsJsonArray("boards");
            for (Object o : ja) {
                JsonObject board = (JsonObject) o;
                int number = board.get("number").getAsInt();
                if (boardNumber==number) {
                    return  board.get("wS"+Integer.toString(n)+"RoomID").getAsInt();
                }
            }
        }
        catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getASXRow(int boardNumber, int n) {

        try {
            JsonElement jsontree = parser.parse(new FileReader("boardConfigParameters.json"));
            JsonObject je = jsontree.getAsJsonObject();
            JsonArray ja = je.getAsJsonArray("boards");
            for (Object o : ja) {
                JsonObject board = (JsonObject) o;
                int number = board.get("number").getAsInt();
                if (boardNumber==number) {
                    return  board.get("aS"+Integer.toString(n)+"Row").getAsInt();
                }
            }
        }
        catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getWSXRow(int boardNumber, int n) {

        try {
            JsonElement jsontree = parser.parse(new FileReader("boardConfigParameters.json"));
            JsonObject je = jsontree.getAsJsonObject();
            JsonArray ja = je.getAsJsonArray("boards");
            for (Object o : ja) {
                JsonObject board = (JsonObject) o;
                int number = board.get("number").getAsInt();
                if (boardNumber==number) {
                    return  board.get("wS"+Integer.toString(n)+"Row").getAsInt();
                }
            }
        }
        catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getASXColumn(int boardNumber, int n) {

        try {
            JsonElement jsontree = parser.parse(new FileReader("boardConfigParameters.json"));
            JsonObject je = jsontree.getAsJsonObject();
            JsonArray ja = je.getAsJsonArray("boards");
            for (Object o : ja) {
                JsonObject board = (JsonObject) o;
                int number = board.get("number").getAsInt();
                if (boardNumber==number) {
                    return  board.get("aS"+Integer.toString(n)+"Column").getAsInt();
                }
            }
        }
        catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getWSXColumn(int boardNumber, int n) {

        try {
            JsonElement jsontree = parser.parse(new FileReader("boardConfigParameters.json"));
            JsonObject je = jsontree.getAsJsonObject();
            JsonArray ja = je.getAsJsonArray("boards");
            for (Object o : ja) {
                JsonObject board = (JsonObject) o;
                int number = board.get("number").getAsInt();
                if (boardNumber==number) {
                    return  board.get("wS"+Integer.toString(n)+"Column").getAsInt();
                }
            }
        }
        catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Color getASXColor(int boardNumber, int n) {

        try {
            JsonElement jsontree = parser.parse(new FileReader("boardConfigParameters.json"));
            JsonObject je = jsontree.getAsJsonObject();
            JsonArray ja = je.getAsJsonArray("boards");
            for (Object o : ja) {
                JsonObject board = (JsonObject) o;
                int number = board.get("number").getAsInt();
                if (boardNumber == number) {
                    String color = board.get("aS" + Integer.toString(n) + "Color").getAsString();
                    switch (color) {
                        case "r":
                            return Color.RED;
                        case "b":
                            return Color.BLUE;
                        case "y":
                            return Color.YELLOW;
                        case "p":
                            return Color.PURPLE;
                        case "g":
                            return Color.GREY;
                        case "v":
                            return Color.GREEN;
                        default:
                            return Color.RED;
                    }
                }
            }
        }
        catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Color getWSXColor(int boardNumber, int n) {

        try {
            JsonElement jsontree = parser.parse(new FileReader("boardConfigParameters.json"));
            JsonObject je = jsontree.getAsJsonObject();
            JsonArray ja = je.getAsJsonArray("boards");
            for (Object o : ja) {
                JsonObject board = (JsonObject) o;
                int number = board.get("number").getAsInt();
                if (boardNumber==number) {
                    String color=board.get("wS"+Integer.toString(n)+"Color").getAsString();
                    switch (color) {
                        case "r":
                            return Color.RED;
                        case "b":
                            return Color.BLUE;
                        case "y":
                            return Color.YELLOW;
                        case "p":
                            return Color.PURPLE;
                        case "g":
                            return Color.GREY;
                        case "v":
                            return Color.GREEN;
                        default:
                            return Color.RED;
                    }
                }
            }
        }
        catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}

