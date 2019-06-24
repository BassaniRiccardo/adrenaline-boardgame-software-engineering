package it.polimi.ingsw.view.CLIRenderer;

import it.polimi.ingsw.view.ClientModel;

import java.util.List;
import static it.polimi.ingsw.view.CLIRenderer.MainRenderer.RESET;

/**
 * Class creating a bidimensional String array containing data about other players
 */
public class PlayersRenderer {

    private static final int PLAYERS_WIDTH = 55;
    private static final int LINES_PER_PLAYER = 6;

    public static String[][] get(ClientModel clientModel){

        List<ClientModel.SimplePlayer> players = clientModel.getPlayers();
        int playerNum = players.size();
        String[] names = new String[playerNum];
        String[] weapons = new String[playerNum];
        String[][] ammo = new String[playerNum][PLAYERS_WIDTH];
        for(int i=0; i<playerNum; i++){
            names[i] = "";
            weapons[i] = "";
            for(int j=0; j<ammo[i].length; j++){
                ammo[i][j] = " ";
            }
        }

        for(int i = 0; i<playerNum; i++){
            ClientModel.SimplePlayer p = players.get(i);
            names[i] = p.getUsername();
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
                ammo[i][count] = clientModel.getEscapeCode("blue") + "|"+RESET;
                count++;
            }
            for(int j=0; j<p.getRedAmmo(); j++){
                ammo[i][count] = clientModel.getEscapeCode("red") + "|"+RESET;
                count++;
            }
            for(int j=0; j<p.getYellowAmmo(); j++){
                ammo[i][count] = clientModel.getEscapeCode("yellow") + "|"+RESET;
                count++;
            }
        }

        int playersHeight = (playerNum-1)*LINES_PER_PLAYER + 1;
        String[][] box = new String[playersHeight][PLAYERS_WIDTH];

        for(int i=0; i<box.length; i++){
            for(int j =0; j<box[i].length; j++){
                box[i][j] = " ";
            }
        }

        int row =0;
        for(int i=0; i<playerNum && row*LINES_PER_PLAYER<playersHeight; i++){

            if(i+1 != clientModel.getPlayerID()) {

                for (int j = 0; j < names[i].length()&&j+3<PLAYERS_WIDTH; j++) {
                    box[row * LINES_PER_PLAYER + 1][j + 3] = ClientModel.getEscapeCode(clientModel.getPlayer(i+1).getColor()) + (names[i].charAt(j)) + "\u001b[0m";
                }

                for (int j = 0; j < "Damage: ".length()&&j+3<PLAYERS_WIDTH; j++) {
                    box[row * LINES_PER_PLAYER + 2][j + 3] = String.valueOf("Damage: ".charAt(j));
                }

                int k = 0;

                for (int color : clientModel.getPlayer(i+1).getDamageID()) {
                    if(k+9>PLAYERS_WIDTH){
                        break;
                    }
                    box[row * LINES_PER_PLAYER + 2][k + 9] = ClientModel.getEscapeCode(clientModel.getPlayer(color).getColor()) + "●" + "\u001b[0m";
                    k++;
                }

                for (int j = 0; j < "Marks: ".length(); j++) {
                    if(k+23>PLAYERS_WIDTH){
                        break;
                    }
                    box[row * LINES_PER_PLAYER + 2][j + 23] = String.valueOf("Marks: ".charAt(j));
                }
                k = 0;

                for (int color : clientModel.getPlayer(i+1).getMarksID()) {
                    if(k+29>PLAYERS_WIDTH){
                        break;
                    }
                    box[row * LINES_PER_PLAYER + 2][k + 29] = ClientModel.getEscapeCode(clientModel.getPlayer(color).getColor()) + "◎" + "\u001b[0m";
                    k++;
                }

                for(int j = 0; j<ammo[i].length&&j+3<PLAYERS_WIDTH; j++){
                    box[row * LINES_PER_PLAYER + 3][j+3] = ammo[i][j];
                }

                for (int j = 0; j < weapons[i].length()&&j+3<PLAYERS_WIDTH; j++) {
                    box[row * LINES_PER_PLAYER + 4][j + 3] = String.valueOf(weapons[i].charAt(j));
                }

                String hand = "Cards in hand: " + clientModel.getPlayer(i+1).getCardNumber();
                for(int j = 0; j < hand.length()&&j+3<PLAYERS_WIDTH; j++){
                    box[row * LINES_PER_PLAYER + 5][j + 3] = String.valueOf(hand.charAt(j));
                }
            } else{
                row--;
            }
            row++;
        }


        return box;
    }

}
