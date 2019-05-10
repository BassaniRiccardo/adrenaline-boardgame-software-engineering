package it.polimi.ingsw.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides methods which encode message toward the client and decode arriving messages, following a protocol.
 */

//TODO: choose a protocol and implement it.
// exiting the game and resetting to the beginning of the action might always be possible choices. Add them.


public class Encoder {

    private Encoder instance;

    private Encoder(){
        this.instance = new Encoder();
    }

    public Encoder getInstance() {
        return instance;
    }

    /**
     * Returns a JsonObject given a header and a generic list.
     *
     * @param message           the type of message to send.
     * @param options           the generic list.
     * @return                  the JsonObject.
     */
    public static JsonObject encode (String message, List options){

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("head", message);
        addList(jsonObject, "Options", options);
        System.out.println(jsonObject.toString());

        return jsonObject;

    }

    /**
     * Returns a list of string given a generic list.
     *
     * @param options       the generic list to transform.
     * @return              the String list.
     */
    public static List<String> toStringList(List options){
        List<String> encoded = new ArrayList<>();
        for (Object p : options ){
            encoded.add(p.toString());
        }
        return encoded;
    }

    /**
     * Returns a list of string given an array of int.
     *
     * @param options           the int array in input.
     * @return                  the String list in output.
     */
    public static List<String> encode (int[] options){
        List<String> encoded = new ArrayList<>();
        for (int i=0; i<options.length; i++){
            encoded.add(String.valueOf(options[i]));
        }
        return encoded;
    }

    /**
     * Adds a generic list to a JsonObject, specifying the name of the list.
     *
     * @param jsonObject            the JsonObject the list must be added to.
     * @param property              the name of the list.
     * @param values                the list itself.
     */
    public static void addList(JsonObject jsonObject, String property, List values) {
        JsonArray array = new JsonArray();
        for (Object value : values) {
            array.add(new JsonPrimitive(value.toString()));
        }
        jsonObject.add(property, array);
    }

}

