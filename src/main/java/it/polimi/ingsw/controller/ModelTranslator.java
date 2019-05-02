package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import java.util.List;

//TODO: implement
public class ModelTranslator {


    //this is a static class providing translation from a list of object to a string/JSON object to be sent to clients
    //this string holds the information necessary for displaying the choice given to the player
    //the client may answer with an int representing his choice
    //exiting the game and resetting to the beginning of the action might always be possible choices

    public static String WeaponsToString(List<Weapon> weaponList){
        return new String();
    }

    public static Weapon chooseWeapon(List<Weapon> weaponList, int index){
        return WeaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE);
    }
}
