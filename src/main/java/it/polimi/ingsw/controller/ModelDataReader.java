package it.polimi.ingsw.controller;

import com.google.gson.*;
import it.polimi.ingsw.model.cards.*;

import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import static it.polimi.ingsw.model.cards.Color.*;

/**
 * Offers methods to read from a Json file the characteristics of a weapon and the keywords that map the weapon
 * into the algorithms that describes the mechanism of the weapon
 * the files is weapons.json
 *
 * @author  davidealde
 */


public class ModelDataReader {

    private static final Logger LOGGER = Logger.getLogger("serverLogger");
    private static JsonParser parser = new JsonParser();
    private static String boardConfFile = "boardConf.json";
    private static String miscellaneous = "miscellaneous.json";
    private static final String DATA_NOT_FOUND = "Data not found";

    /**
     * Constructor of a json class
     */
    public ModelDataReader() {
        //the constructor is empty since all the attributes are static
    }

    private JsonObject analyzer(String fileName){
        try {
            JsonElement jsonTree = parser.parse(new InputStreamReader(this.getClass().getResourceAsStream("/"+fileName)));
            return jsonTree.getAsJsonObject();
        }
        catch (JsonIOException e) {
            LOGGER.log(Level.SEVERE, "Unable to read from file", e);
        }
        return null;
    }

    private JsonObject analyzer(String fileName, String array, int elemId){
        try {
            JsonElement jsontree = parser.parse(new InputStreamReader(this.getClass().getResourceAsStream("/"+fileName)));
            JsonObject je = jsontree.getAsJsonObject();
            JsonArray ja = je.getAsJsonArray(array);
            for (Object o : ja) {
                JsonObject arrayElement = (JsonObject) o;
                if (elemId==arrayElement.get("elementId").getAsInt()) {
                    return arrayElement;
                }}}
        catch (JsonIOException e) {
            LOGGER.log(Level.SEVERE, "Unable to read from file", e);
        }
        return null;
    }



    int getIntBC(String key) {
        JsonObject obj = analyzer(boardConfFile);
        return getInt(obj, key);
    }

    int getIntBC(String key, String array, int elemId){
        JsonObject obj = analyzer(boardConfFile, array, elemId);
        return getInt(obj, key);
    }

    boolean getBooleanBC(String key, String array, int elemId) {

        JsonObject obj = analyzer(boardConfFile,array,elemId);
        return getBoolean(obj, key);
    }

    public int getInt(String key) {
        JsonObject obj = analyzer(miscellaneous);
        return getInt(obj, key);
    }

    public int getInt(String key, String array, int elemId) {
        JsonObject obj = analyzer(miscellaneous,array,elemId);
        return getInt(obj, key);
    }

    public boolean getBoolean(String key, String array, int elemId){
        JsonObject obj = analyzer(miscellaneous,array,elemId);
        return getBoolean(obj, key);
    }


    Color getColorBC(String key, String array, int elemId) {
        JsonObject obj = analyzer(boardConfFile,array,elemId);
        return getColor(obj, key);
    }

    Color getColorBC(String key) {
        JsonObject obj = analyzer(boardConfFile);
        return getColor(obj, key);
    }

    private int getInt(JsonObject obj, String key){
        if(obj==null){
            LOGGER.log(Level.SEVERE, DATA_NOT_FOUND);
            return -1;
        }
        return obj.get(key).getAsInt();
    }

    private boolean getBoolean(JsonObject obj, String key){
        if(obj==null) {
            LOGGER.log(Level.SEVERE, DATA_NOT_FOUND);
            return false;
        }
        int out=obj.get(key).getAsInt();
        return (out == 1);
    }

    private Color getColor(JsonObject obj, String key){
        if(obj==null) {
            LOGGER.log(Level.SEVERE, DATA_NOT_FOUND);
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