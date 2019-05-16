package it.polimi.ingsw.controller;

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

//TODO
//substitute all methods for the weapon factory with the definitive ones
//test the new methods
//check all the project for values that can be written in a json file

public class ModelDataReader {

    private static JsonParser parser = new JsonParser();
    private static String weaponsFile = "src/main/resources/weaponsFile.json";
    private static String boardConfFile = "src/main/resources/boardConfFile.json";
    private static String miscellaneous = "src/main/resources/miscellaneous.json";

    /**
     * Constructor of a json class
     */
    public ModelDataReader() {
    }

//------DEFINITIVE methods------------------------------------------------------------------------------------------------------

    private JsonObject analyzer(String fileName){
        try {
            JsonElement jsontree = parser.parse(new FileReader(fileName));
            return jsontree.getAsJsonObject();
        }
        catch (JsonIOException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JsonObject analyzer(String fileName, String array, int elemId){
        try {
            JsonElement jsontree = parser.parse(new FileReader(fileName));
            JsonObject je = jsontree.getAsJsonObject();
            JsonArray ja = je.getAsJsonArray(array);
            for (Object o : ja) {
                JsonObject arrayElement = (JsonObject) o;
                if (elemId==arrayElement.get("elementId").getAsInt()) {
                    return arrayElement;
                }}}
        catch (JsonIOException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public int getIntBC(String key) {

        JsonObject obj=analyzer(boardConfFile);
        if(obj==null) throw new NullPointerException();
        return obj.get(key).getAsInt();
    }

    public int getIntBC(String key, String array, int elemId){

        JsonObject obj=analyzer(boardConfFile,array,elemId);
        if(obj==null) throw new NullPointerException();
        return obj.get(key).getAsInt();
    }

    public boolean getBooleanBC(String key, String array, int elemId) {

        JsonObject obj=analyzer(boardConfFile,array,elemId);
        if(obj==null) throw new NullPointerException();
        int out=obj.get(key).getAsInt();
        if(out==1)
            return true;
        else
            return false;
    }

    public Color getColorBC(String key) {

        JsonObject obj=analyzer(boardConfFile);
        if(obj==null) throw new NullPointerException();
        String out=obj.get(key).getAsString();
        if(out.equals("r")){
            return Color.RED;
        } else if (out.equals("b")) {
            return Color.BLUE;
        }else
            return Color.YELLOW;
    }

    public Color getColorBC(String key, String array, int elemId) {

        JsonObject obj=analyzer(boardConfFile,array,elemId);
        if(obj==null) throw new NullPointerException();
        String out=obj.get(key).getAsString();
        if(out.equals("r")){
            return Color.RED;
        } else if (out.equals("b")) {
            return Color.BLUE;
        }else
            return Color.YELLOW;
    }

    public int getInt(String key) {

        JsonObject obj=analyzer(miscellaneous);
        if(obj==null) throw new NullPointerException();
        return obj.get(key).getAsInt();
    }

    public int getInt(String key, String array, int elemId) {

        JsonObject obj=analyzer(miscellaneous,array,elemId);
        if(obj==null) throw new NullPointerException();
        return obj.get(key).getAsInt();
    }

    public boolean getBoolean(String key, String array, int elemId) {

        JsonObject obj=analyzer(miscellaneous,array,elemId);
        if(obj==null) throw new NullPointerException();
        int out=obj.get(key).getAsInt();
        return (out==1);
    }

    public String getString(String key) {

        JsonObject obj=analyzer(miscellaneous);
        if(obj==null) throw new NullPointerException();
        return obj.get(key).getAsString();
    }


//---METHODS FOR WeaponFactory (THEY HAVE TO BE CHANGED)-------------------------------------------------------------------------------------------------------------

    /**
     * Reads from the json file the color of the weapon of interest
     *
     * @param weaponName the name of the weapon
     * @return the color of the weapon
     */
    public String getColor(Weapon.WeaponName weaponName) {

        try {
            JsonElement jsontree = parser.parse(new FileReader(weaponsFile));
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
            JsonElement jsontree = parser.parse(new FileReader(weaponsFile));
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
            JsonElement jsontree = parser.parse(new FileReader(weaponsFile));
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
            JsonElement jsontree = parser.parse(new FileReader(weaponsFile));
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
            JsonElement jsontree = parser.parse(new FileReader(weaponsFile));
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
            JsonElement jsontree = parser.parse(new FileReader(weaponsFile));
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
            JsonElement jsontree = parser.parse(new FileReader(weaponsFile));
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
            JsonElement jsontree = parser.parse(new FileReader(weaponsFile));
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
            JsonElement jsontree = parser.parse(new FileReader(weaponsFile));
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
            JsonElement jsontree = parser.parse(new FileReader(weaponsFile));
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
            JsonElement jsontree = parser.parse(new FileReader(weaponsFile));
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
            JsonElement jsontree = parser.parse(new FileReader(weaponsFile));
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
            JsonElement jsontree = parser.parse(new FileReader(weaponsFile));
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
            JsonElement jsontree = parser.parse(new FileReader(weaponsFile));
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
            JsonElement jsontree = parser.parse(new FileReader(weaponsFile));
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
            JsonElement jsontree = parser.parse(new FileReader(weaponsFile));
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
            JsonElement jsontree = parser.parse(new FileReader(weaponsFile));
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

}

