package it.polimi.ingsw.view.CLIRenderer;

import it.polimi.ingsw.view.ClientModel;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

//TODO: rewrite all CLI rendering functions properly, make the code more robust
public class HandRenderer {

    private static final Logger LOGGER = Logger.getLogger("clientLogger");

    public static String[][] get(ClientModel clientModel) {

        String[][] box = new String[7][55];
        for (int i = 0; i < box.length; i++) {
            for (int j = 0; j < box[i].length; j++) {
                box[i][j] = " ";
            }
        }

        ClientModel.SimplePlayer you = clientModel.getPlayer(clientModel.getPlayerID());

        String name =  "You (" + you.getUsername() + ")";
        for(int i=0; i<name.length()&&i<55; i++){
            box[1][i+3] = clientModel.getEscapeCode(you.getColor()) + String.valueOf(name.charAt(i)) + "\u001b[0m";
        }

        for(int i =0; i<"Life: ".length()&&i<60; i++){
            box[2][i+3] = String.valueOf("Life: ".charAt(i));
        }
        int j=0;
        for (int shooter : you.getDamage()) {
            try {
                box[2][j + 9] = ClientModel.getEscapeCode(clientModel.getPlayer(shooter).getColor()) + "●" + "\u001b[0m";
            }catch(Exception ex){
                LOGGER.log(Level.SEVERE, "Error while reading color", ex);
            }
            j++;
        }

        for(int i =0; i<"Marks: ".length(); i++){
            box[2][i+30] = String.valueOf("Marks: ".charAt(i));
        }
        j=0;
        for (int shooter : you.getMarks()) {
            try {
                box[2][j + 37] = ClientModel.getEscapeCode(clientModel.getPlayer(shooter).getColor()) + "◎" + "\u001b[0m";
            }catch(Exception ex){
                LOGGER.log(Level.SEVERE, "Error while reading color", ex);
            }
            j++;
        }

        for(int i=0; i<"Ammo: ".length(); i++){
            box[3][i+3] = String.valueOf("Ammo: ".charAt(i));
        }
        j=0;
        for(int i=0; i<you.getBlueAmmo(); i++){
            box[3][j+9] = ClientModel.getEscapeCode("blue") + "|" + "\u001b[0m";
            j++;
        }
        for(int i=0; i<you.getRedAmmo(); i++){
            box[3][j+9] = ClientModel.getEscapeCode("red") + "|" + "\u001b[0m";
            j++;
        }
        for(int i=0; i<you.getYellowAmmo(); i++){
            box[3][j+9] = ClientModel.getEscapeCode("yellow") + "|" + "\u001b[0m";
            j++;
        }

        String weapons = "Weapons: ";
        if(you.getWeapons().isEmpty()){
            weapons = weapons + "none";
        } else{
            for(int i=0; i<you.getWeapons().size(); i++){
                ClientModel.SimpleWeapon w = you.getWeapons().get(i);
                weapons = weapons + w.getName();
                if(w.isLoaded()){
                    weapons = weapons + "*";
                }
                if(i!=you.getWeapons().size()-1){
                    weapons = weapons + ", ";
                }
            }
        }
        for(int i=0; i<weapons.length()&&i<57; i++){
            box[4][i+3] = String.valueOf(weapons.charAt(i));
        }

        String hand = "Hand: ";
        if(clientModel.getPowerUpInHand().isEmpty()){
            hand = hand + "empty";
        } else {
            for(int i=0; i<clientModel.getPowerUpInHand().size(); i++){
                hand = hand + clientModel.getPowerUpInHand().get(i);
                if(i!=clientModel.getPowerUpInHand().size()-1){
                    weapons = weapons + ", ";
                }
            }
        }
        for(int i=0; i<hand.length()&&i<57; i++){
            box[5][i+3] = String.valueOf(hand.charAt(i));
        }

        return box;
    }
}
