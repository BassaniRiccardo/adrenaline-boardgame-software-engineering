package it.polimi.ingsw.view.CLIRenderer;

import it.polimi.ingsw.view.ClientModel;

import java.util.List;
import java.util.stream.Collectors;

public class PlayersRenderer {

    public static String[][] get(ClientModel clientModel){

        String[][] box = new String[25][55];

        for(int i=0; i<box.length; i++){
            for(int j =0; j<box[i].length; j++){
                box[i][j] = " ";
            }
        }

        List<ClientModel.SimplePlayer> players = clientModel.getPlayers();
        int playerNum = players.size();
        String[] names = new String[playerNum];
        String[] weapons = new String[playerNum];
        String[][] ammo = new String[playerNum][55];
        for(int i=0; i<playerNum; i++){
            names[i] = "";
            weapons[i] = "";
            for(int j=0; j<ammo[i].length; j++){
                ammo[i][j] = " ";
            }
        }

        for(int i = 0; i<playerNum; i++){
            ClientModel.SimplePlayer p = players.get(i);
            names[i] = p.getUsername() + " " + p.getColor();
            if(clientModel.getCurrentPlayer().getId()==p.getId()){
                names[i] = names[i] + " [current]";
            }
            weapons[i] ="Weapons: ";
            List<ClientModel.SimpleWeapon> currentWeapons = p.getWeapons();
            if(currentWeapons.isEmpty()){
                weapons[i] = weapons[i] + "none";
            } else {
                for (int j = 0; j < currentWeapons.size(); j++) {
                    weapons[i] = weapons[i] + currentWeapons.get(j).getName();
                    if (currentWeapons.get(j).isLoaded()) {
                        weapons[i] = weapons[i] + "*";
                    }
                    if (j != currentWeapons.size() - 1) {
                        weapons[i] = weapons[i].concat(", ");
                    }
                }
            }
            int count;
            for(count = 0; count<"Ammo: ".length(); count++){
                ammo[i][count] = String.valueOf("Ammo: ".charAt(count));
            }
            for(int j=0; j<p.getBlueAmmo(); j++){
                ammo[i][count] = clientModel.getEscapeCode("blue") + "|"+"\u001b[0m";
                count++;
            }
            for(int j=0; j<p.getRedAmmo(); j++){
                ammo[i][count] = clientModel.getEscapeCode("red") + "|"+"\u001b[0m";
                count++;
            }
            for(int j=0; j<p.getYellowAmmo(); j++){
                ammo[i][count] = clientModel.getEscapeCode("yellow") + "|"+"\u001b[0m";
                count++;
            }
        }

        int row =0;
        for(int i=0; i<playerNum; i++){

            if(i+1 != clientModel.getPlayerID()) {

                for (int j = 0; j < names[i].length(); j++) {
                    box[row * 6 + 1][j + 3] = ClientModel.getEscapeCode(clientModel.getPlayer(i+1).getColor()) + (names[i].charAt(j)) + "\u001b[0m";
                    //might cause issues
                }

                for (int j = 0; j < "Damage: ".length(); j++) {
                    box[row * 6 + 2][j + 3] = String.valueOf("Damage: ".charAt(j));
                }

                int k = 0;

                for (int color : clientModel.getPlayer(i+1).getDamageID()) {
                    box[row * 6 + 2][k + 9] = ClientModel.getEscapeCode(clientModel.getPlayer(color).getColor()) + "●" + "\u001b[0m";
                    k++;
                }

                for (int j = 0; j < "Marks: ".length(); j++) {
                    box[row * 6 + 2][j + 23] = String.valueOf("Marks: ".charAt(j));
                }
                k = 0;

                for (int color : clientModel.getPlayer(i+1).getMarksID()) {    //watch out in case getplayercolor returns null
                    box[row * 6 + 2][k + 29] = ClientModel.getEscapeCode(clientModel.getPlayer(color).getColor()) + "◎" + "\u001b[0m";
                    k++;
                }

                for(int j = 0; j<ammo[i].length&&j<52; j++){
                    box[row * 6 + 3][j+3] = ammo[i][j];
                }

                for (int j = 0; j < weapons[i].length()&&j<52; j++) {
                    box[row * 6 + 4][j + 3] = String.valueOf(weapons[i].charAt(j));
                }

                String hand = "Cards in hand: " + clientModel.getPlayer(i+1).getCardNumber();
                for(int j = 0; j < hand.length()&&j<21; j++){
                    box[row * 6 + 5][j + 3] = String.valueOf(hand.charAt(j));
                }
            } else{
                row--;
            }
            row++;
        }


        return box;
    }

}
