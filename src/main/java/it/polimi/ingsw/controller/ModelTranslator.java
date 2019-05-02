package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.PowerUp;

import java.util.ArrayList;
import java.util.List;

//TODO: implement
public class ModelTranslator {

    //this is a static class providing translation from a list of object to a string/JSON object to be sent to clients
    //this string holds the information necessary for displaying the choice given to the player
    //the client may answer with an int representing his choice
    //exiting the game and resetting to the beginning of the action might always be possible choices

    public static List<String> encode (List<PowerUp> options){
        List<String> encoded = new ArrayList<>();
        for (PowerUp p : options ){
            encoded.add(p.toString());
        }
        return encoded;

    }
}
