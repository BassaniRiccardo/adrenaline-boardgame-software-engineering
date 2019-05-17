package it.polimi.ingsw.view.CLIRenderer;

import it.polimi.ingsw.view.ClientModel;

import java.util.List;

public class PlayersRenderer {


//TODO: rewrite all CLI rendering functions properly, make the code more robust
    //TODO: adapt the class to work on any set of playerID
    //required player id to be a continuous sequence between 1 and 3/4/5

    public static String[][] get(ClientModel clientModel){

        String[][] box = new String[24][60];

        List<Integer> players = clientModel.getPlayers();
        int playerNum = players.size();
        String[] names = new String[playerNum];
        String[] weapons = new String[playerNum];
        for(int i=0; i<playerNum; i++){
            names[i] = "";
            weapons[i] = "";
        }


        for(int i = 0; i<players.size(); i++){

            int playerID = players.get(i);
            names[i] = clientModel.getPlayerName().get(playerID);
            weapons[i] ="Weapons: ";
            if(clientModel.getEquippedWeapons().get(playerID)!=null) {
                for (String w : clientModel.getEquippedWeapons().get(playerID)) {
                    weapons[i].concat(w + " ");
                }
            }
        }


        for(int i=0; i<box.length; i++){
            for(int j =0; j<box[i].length; j++){
                box[i][j] = " ";
            }
        }

        for(int i=0; i<playerNum; i++){

            for(int j=0; j<names[i].length(); j++){
                box[i*4 + 1][j+3] = String.valueOf(names[i].charAt(j));
            }

            for(int j =0; j<"Life: ".length(); j++){
                box[i*4+2][j+3] = String.valueOf("Life: ".charAt(j));
            }


            for(int j =0; j<"Marks: ".length(); j++){
                box[i*4+2][j+23] = String.valueOf("Marks: ".charAt(j));
            }

            for(int j =0; j<weapons[i].length(); j++){
                box[i*4+3][j+3] = String.valueOf(weapons[i].charAt(j));
            }

            int j=0;
            if(clientModel.getMarks().get(players.get(i))!=null) {
                for (int color : clientModel.getMarks().get(players.get(i))) {    //watch out in case getplayercolor returns null
                    box[i * 4 + 2][j + 9] = ClientModel.getEscapeCode(clientModel.getPlayerColor().get(color)) + "x" + "\u001b[0m";
                    j++;
                }
            }

            j=0;
            if(clientModel.getDamage().get(players.get(i))!=null) {
                for (int color : clientModel.getDamage().get(players.get(i))) {
                    box[i * 4 + 2][j + 29] = ClientModel.getEscapeCode(clientModel.getPlayerColor().get(color)) + "x" + "\u001b[0m";
                    j++;
                }
            }
        }
        return box;
    }

}
