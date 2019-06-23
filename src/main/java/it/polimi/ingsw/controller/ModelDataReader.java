package it.polimi.ingsw.controller;

import com.google.gson.*;
import it.polimi.ingsw.model.cards.*;

import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import static it.polimi.ingsw.model.cards.Color.*;

/**
 * Offers thr methods to read from a .json file the characteristics of a weapon and the keywords that map the weapon
 * into the algorithms that describes the mechanism of the weapon
 * the files is weapons.json
 *
 * @author  davidealde
 */

//TODO remove targetnumbers from json file (they are not needed anymore)
//TODO test the new methods
//TODO check all the project for values that can be written in a json file

public class ModelDataReader {
    private static final Logger LOGGER = Logger.getLogger("serverLogger");
    private static JsonParser parser = new JsonParser();
    //private static String weaponsFile = "src/main/resources/weapons.json";
    //private static String boardConfFile = "src/main/resources/boardConf.json";
    //private static String miscellaneous = "src/main/resources/miscellaneous.json";
    private static String weaponsFile = "weapons.json";
    private static String boardConfFile = "boardConf.json";
    private static String miscellaneous = "miscellaneous.json";

    /**
     * Constructor of a json class
     */
    public ModelDataReader() {//all attributes are static
    }

//------methods for BoardConfigurer and other classes (not WeaponFactory)------------------------------------------------------------------------------------------------------

    private JsonObject analyzer(String fileName){
        try {
            JsonElement jsontree = parser.parse(new InputStreamReader(this.getClass().getResourceAsStream("/"+fileName)));
            //JsonElement jsontree = parser.parse(new FileReader(fileName));
            return jsontree.getAsJsonObject();
        }
        catch (JsonIOException e) {
            LOGGER.log(Level.SEVERE, "Unable to read from file", e);
        //} catch (FileNotFoundException e) {
        //    LOGGER.log(Level.SEVERE, "File not found", e);
        }
        return null;
    }

    private JsonObject analyzer(String fileName, String array, int elemId){
        try {
            JsonElement jsontree = parser.parse(new InputStreamReader(this.getClass().getResourceAsStream("/"+fileName)));
            //JsonElement jsontree = parser.parse(new FileReader(fileName));
            JsonObject je = jsontree.getAsJsonObject();
            JsonArray ja = je.getAsJsonArray(array);
            for (Object o : ja) {
                JsonObject arrayElement = (JsonObject) o;
                if (elemId==arrayElement.get("elementId").getAsInt()) {
                    return arrayElement;
                }}}
        catch (JsonIOException e) {
            LOGGER.log(Level.SEVERE, "Unable to read from file", e);
        //} catch (FileNotFoundException e) {
        //    LOGGER.log(Level.SEVERE, "File not found", e);
        }
        return null;
    }

    public int getIntBC(String key) {

        JsonObject obj=analyzer(boardConfFile);
        if(obj==null){
            LOGGER.log(Level.SEVERE, "Data not found");
            return -1;
        }
        return obj.get(key).getAsInt();
    }

    public int getIntBC(String key, String array, int elemId){

        JsonObject obj=analyzer(boardConfFile,array,elemId);
        if(obj==null) {
            LOGGER.log(Level.SEVERE, "Data not found");
            return -1;
        }
        return obj.get(key).getAsInt();
    }

    public boolean getBooleanBC(String key, String array, int elemId) {

        JsonObject obj=analyzer(boardConfFile,array,elemId);
        if(obj==null) {
            LOGGER.log(Level.SEVERE, "Data not found");
            return false;
        }
        int out=obj.get(key).getAsInt();
        return (out==1);

    }

    public int getInt(String key) {

        JsonObject obj=analyzer(miscellaneous);
        if(obj==null) {
            LOGGER.log(Level.SEVERE, "Data not found");
            return -1;
        }
        return obj.get(key).getAsInt();
    }

    public int getInt(String key, String array, int elemId) {

        JsonObject obj=analyzer(miscellaneous,array,elemId);
        if(obj==null) {
            LOGGER.log(Level.SEVERE, "Data not found");
            return -1;
        }
        return obj.get(key).getAsInt();
    }

    public boolean getBoolean(String key, String array, int elemId) {

        JsonObject obj=analyzer(miscellaneous,array,elemId);
        if(obj==null) {
            LOGGER.log(Level.SEVERE, "Data not found");
            return false;
        }
        int out=obj.get(key).getAsInt();
        return (out==1);
    }

    public Color getColorBC(String key, String array, int elemId) {
        JsonObject obj=analyzer(boardConfFile,array,elemId);
        if(obj==null) {
            LOGGER.log(Level.SEVERE, "Data not found");
            return GREEN;
        }
        String out=obj.get(key).getAsString();
        if(out.equals("r")){
            return Color.RED;
        } else if (out.equals("b")) {
            return Color.BLUE;
        }else
            return Color.YELLOW;
    }

    public Color getColorBC(String key) {
        JsonObject obj=analyzer(boardConfFile);
        if(obj==null) {
            LOGGER.log(Level.SEVERE, "Data not found");
            return GREEN;
        }
        String out=obj.get(key).getAsString();
        if(out.equals("r")){
            return Color.RED;
        } else if (out.equals("b")) {
            return Color.BLUE;
        }else
            return Color.YELLOW;
    }


//---METHODS FOR WeaponFactory -------------------------------------------------------------------------------------------------------------


    private JsonObject analyzer(Weapon.WeaponName weaponName){
        try {
            JsonElement jsontree = parser.parse(new InputStreamReader(this.getClass().getResourceAsStream("/"+weaponsFile)));
            //JsonElement jsontree = parser.parse(new FileReader(weaponsFile));
            JsonObject je = jsontree.getAsJsonObject();
            JsonArray ja = je.getAsJsonArray("weapons");
            for (Object o : ja) {
                JsonObject arrayElement = (JsonObject) o;
                if (weaponName.toString().equals(arrayElement.get("name").getAsString())) {
                    return arrayElement;
                }}}
        catch (JsonIOException e) {
            LOGGER.log(Level.SEVERE, "Unable to read from file", e);
        //} catch (FileNotFoundException e) {
        //    LOGGER.log(Level.SEVERE, "File not found", e);
        }
        return null;
    }

    private JsonObject analyzer(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName){
        try {
            JsonElement jsontree = parser.parse(new InputStreamReader(this.getClass().getResourceAsStream("/"+weaponsFile)));
            //JsonElement jsontree = parser.parse(new FileReader(weaponsFile));
            JsonObject je = jsontree.getAsJsonObject();
            JsonArray ja = je.getAsJsonArray("weapons");
            for (Object o : ja) {
                JsonObject arrayElement = (JsonObject) o;
                if (weaponName.toString().equals(arrayElement.get("name").getAsString())) {
                    JsonArray modesList = arrayElement.getAsJsonArray("modes");
                    for (Object k : modesList) {
                        JsonObject mode = (JsonObject) k;
                        String modeName = mode.get("mode").getAsString();
                        if (modeName.equals(fireModeName.toString())) {
                            return mode;
                        }
                    }
                }
            }
        }
        catch (JsonIOException e) {
            LOGGER.log(Level.SEVERE, "Unable to read from file", e);
        //} catch (FileNotFoundException e) {
        //    LOGGER.log(Level.SEVERE, "File not found", e);
        }
        return null;
    }

    /**
     * Reads from the json file the color of the weapon of interest
     *
     * @param weaponName the name of the weapon
     * @return the color of the weapon
     */
    public Color getColor(Weapon.WeaponName weaponName) {
        JsonObject obj=analyzer(weaponName);
        if(obj==null) {
            LOGGER.log(Level.SEVERE, "Data not found");
            return GREEN;
        }
        String out=obj.get("color").getAsString();
        if(out.equals("red")){
            return Color.RED;
        } else if (out.equals("blue")) {
            return Color.BLUE;
        }else
            return Color.YELLOW;
    }

    /**
     * Reads from the json file the red full cost of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @return                      the red full cost of the weapon
     */
    public AmmoPack getFullCostRed(Weapon.WeaponName weaponName) {
        JsonObject obj=analyzer(weaponName);
        if(obj==null) {
            LOGGER.log(Level.SEVERE, "Data not found");
            return new AmmoPack(0,0,0);
        }
        int out=obj.get("costR").getAsInt();
        return new AmmoPack(out,0,0);
    }

    /**
     * Reads from the json file the blue full cost of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @return                      the blue full cost of the weapon
     */
    public AmmoPack getFullCostBlue(Weapon.WeaponName weaponName) {
        JsonObject obj=analyzer(weaponName);
        if(obj==null) {
            LOGGER.log(Level.SEVERE, "Data not found");
            return new AmmoPack(0,0,0);
        }
        int out=obj.get("costB").getAsInt();
        return new AmmoPack(0,out,0);
    }

    /**
     * Reads from the json file the yellow full cost of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @return                      the yellow full cost of the weapon
     */
    public AmmoPack getFullCostYellow(Weapon.WeaponName weaponName) {
        JsonObject obj=analyzer(weaponName);
        if(obj==null) {
            LOGGER.log(Level.SEVERE, "Data not found");
            return new AmmoPack(0,0,0);
        }
        int out=obj.get("costY").getAsInt();
        return new AmmoPack(0,0,out);
    }

     /**
     * Reads from the json file the list of fire modes of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @return                      the list of fire modes of the weapon
     */
     public int getFireModeList(Weapon.WeaponName weaponName) {

         JsonObject obj=analyzer(weaponName);
         if(obj==null) {
             LOGGER.log(Level.SEVERE, "Data not found");
             return -1;
         }
         return obj.get("type").getAsInt();
     }

    /**
     * Reads from the json file the red cost of the firemode of interest of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @param fireModeName          the firemode name
     * @return                      the red cost of the firemode of the weapon
     */
    public AmmoPack getFireModeCostRed(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        JsonObject obj = analyzer(weaponName, fireModeName);
        if (obj == null) {
            LOGGER.log(Level.SEVERE, "Data not found");
            return new AmmoPack(0,0,0);
        }
        int out=obj.get("costR").getAsInt();
        return new AmmoPack(out,0,0);
    }

    /**
     * Reads from the json file the blue cost of the firemode of interest of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @param fireModeName          the firemode name
     * @return                      the blue cost of the firemode of the weapon
     */
    public AmmoPack getFireModeCostBlue(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        JsonObject obj = analyzer(weaponName, fireModeName);
        if (obj == null) {
            LOGGER.log(Level.SEVERE, "Data not found");
            return new AmmoPack(0,0,0);
        }
        int out=obj.get("costB").getAsInt();
        return new AmmoPack(0,out,0);
    }

    /**
     * Reads from the json file the yellow cost of the firemode of interest of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @param fireModeName          the firemode name
     * @return                      the yellow cost of the firemode of the weapon
     */
    public AmmoPack getFireModeCostYellow(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        JsonObject obj = analyzer(weaponName, fireModeName);
        if (obj == null) {
            LOGGER.log(Level.SEVERE, "Data not found");
            return new AmmoPack(0,0,0);
        }
        int out=obj.get("costY").getAsInt();
        return new AmmoPack(0,0,out);
    }

    /**
     * Reads from the json file the number of moves permitted of the firemode of interest of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @param fireModeName          the firemode name
     * @return                      the number of moves permitted of the firemode of the weapon
     */
    public int getMove(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        JsonObject obj=analyzer(weaponName, fireModeName);
        if(obj==null) {
            LOGGER.log(Level.SEVERE, "Data not found");
            return -1;
        }
        return obj.get("move").getAsInt();
    }

    /**
     * Reads from the json file the damages of the firemode of interest of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @param fireModeName          the firemode name
     * @return                      the damages of the firemode of the weapon
     */
    public int getDmg(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        JsonObject obj=analyzer(weaponName, fireModeName);
        if(obj==null) {
            LOGGER.log(Level.SEVERE, "Data not found");
            return -1;
        }
        return obj.get("dmg").getAsInt();
    }

    /**
     * Reads from the json file the other amount of damages of the firemode of interest of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @param fireModeName          the firemode name
     * @return                      the other amount of damages of the firemode of the weapon
     */
    public int getDmg2(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        JsonObject obj=analyzer(weaponName, fireModeName);
        if(obj==null) {
            LOGGER.log(Level.SEVERE, "Data not found");
            return -1;
        }
        return obj.get("dmg2").getAsInt();
    }

    /**
     * Reads from the json file the marks of the firemode of interest of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @param fireModeName          the firemode name
     * @return                      the marks of the firemode of the weapon
     */
    public int getMark(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        JsonObject obj=analyzer(weaponName, fireModeName);
        if(obj==null) {
            LOGGER.log(Level.SEVERE, "Data not found");
            return -1;
        }
        return obj.get("mark").getAsInt();
    }

    /**
     * Reads from the json file the steps of the firemode of interest of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @param fireModeName          the firemode name
     * @return                      the steps of the firemode of the weapon
     */
    public int getSteps(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        JsonObject obj=analyzer(weaponName, fireModeName);
        if(obj==null) {
            LOGGER.log(Level.SEVERE, "Data not found");
            return -1;
        }
        return obj.get("steps").getAsInt();
    }

    /**
     * Reads from the json file the effect of the firemode of interest of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @param fireModeName          the firemode name
     * @return                      the effect of the firemode of the weapon
     */
    public String getEff(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        JsonObject obj=analyzer(weaponName, fireModeName);
        if(obj==null) {
            LOGGER.log(Level.SEVERE, "Data not found");
            return "Nothing";
        }
        return obj.get("effect").getAsString();
    }

    /**
     * Reads from the json file the type of targets of the firemode of interest of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @param fireModeName          the firemode name
     * @return                      the type of targets of the firemode of the weapon
     */
    public String getWhere(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        JsonObject obj=analyzer(weaponName, fireModeName);
        if(obj==null) {
            LOGGER.log(Level.SEVERE, "Data not found");
            return "Nothing";
        }
        return obj.get("where").getAsString();
    }

    /**
     * Reads from the json file the type of move of the firemode of interest of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @param fireModeName          the firemode name
     * @return                      the type of move of the firemode of the weapon
     */
    public String getMoveType(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        JsonObject obj=analyzer(weaponName, fireModeName);
        if(obj==null) {
            LOGGER.log(Level.SEVERE, "Data not found");
            return "Nothing";
        }
        return obj.get("moveType").getAsString();
    }

}