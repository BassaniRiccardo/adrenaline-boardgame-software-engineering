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
    //private static String boardConfFile = "src/main/resources/boardConf.json";
    //private static String miscellaneous = "src/main/resources/miscellaneous.json";
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
}