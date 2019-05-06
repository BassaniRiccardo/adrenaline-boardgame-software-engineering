package it.polimi.ingsw.controller;

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
     * Returns a list of string given a generic list.
     *
     * @param options           the generic list in input.
     * @return                  the String list in output.
     */
    public static List<String> encode (List options){
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

}

