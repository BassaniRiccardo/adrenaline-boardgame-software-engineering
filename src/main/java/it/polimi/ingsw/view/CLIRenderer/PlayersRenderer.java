package it.polimi.ingsw.view.CLIRenderer;

import it.polimi.ingsw.view.ClientModel;

import java.util.List;

public class PlayersRenderer {


//TODO: rewrite all CLI rendering functions properly, make the code more robust
    //TODO: adapt the class to work on any set of playerID
    //required player id to be a continuous sequence between 1 and 3/4/5

    public static String[][] get(ClientModel clientModel){

        String[][] box = new String[24][55];

        //List<Integer> players = clientModel.getPlayers();
        //int playerNum = players.size();
        //String[] names = new String[playerNum];
        //String[] weapons = new String[playerNum];
        //for(int i=0; i<playerNum; i++){
        //    names[i] = "";
        //    weapons[i] = "";
        //}

/*
        for(int i = 0; i<players.size(); i++){

            int playerID = players.get(i);
            names[i] = clientModel.getPlayerName().get(playerID);
            weapons[i] ="Weapons: ";
            if(clientModel.getEquippedWeapons().get(playerID)!=null) {
                List<String> currentWeapons = clientModel.getEquippedWeapons().get(playerID);
                for (int j = 0; j< currentWeapons.size(); j++){
                    weapons[i] = weapons[i].concat(currentWeapons.get(j));
                    if(j!=currentWeapons.size()-1){
                        weapons[i] = weapons[i].concat(", ");
                    }
                }
            }
        }
*/

        for(int i=0; i<box.length; i++){
            for(int j =0; j<box[i].length; j++){
                box[i][j] = " ";
            }
        }
/*
        for(int i=0; i<playerNum; i++){

            for(int j=0; j<names[i].length(); j++){
                box[i*4 + 4][j+3] = ClientModel.getEscapeCode(clientModel.getPlayerColor().get(i)) + String.valueOf(names[i].charAt(j)) + "\u001b[0m";
                //might cause issues
            }

            for(int j =0; j<"Life: ".length(); j++){
                box[i*4+5][j+3] = String.valueOf("Life: ".charAt(j));
            }


            for(int j =0; j<"Marks: ".length(); j++){
                box[i*4+5][j+23] = String.valueOf("Marks: ".charAt(j));
            }

            for(int j =0; j<weapons[i].length(); j++){
                box[i*4+6][j+3] = String.valueOf(weapons[i].charAt(j));
            }

            int j=0;
            if(clientModel.getMarks().get(players.get(i))!=null) {
                for (int color : clientModel.getMarks().get(players.get(i))) {    //watch out in case getplayercolor returns null
                    box[i * 4 + 5][j + 29] = ClientModel.getEscapeCode(clientModel.getPlayerColor().get(color)) + "◎" + "\u001b[0m";
                    j++;
                }
            }

            j=0;
            if(clientModel.getDamage().get(players.get(i))!=null) {
                for (int color : clientModel.getDamage().get(players.get(i))) {
                    box[i * 4 + 5][j + 9] = ClientModel.getEscapeCode(clientModel.getPlayerColor().get(color)) + "♥" + "\u001b[0m";
                    j++;
                }
            }
        }
*/
        //resize
        int max = 0;
        for(int i=0; i<box.length; i++){
            for(int j=0; j<box[i].length; j++){
                if(!box[i][j].equals(" ")&&j>max){
                    max = j;
                }
            }
        }
        max = max + 3;

        String[][] res = new String[24][max+1];
        for(int i=0; i<res.length; i++) {
            for (int j = 0; j < res[i].length; j++) {
                res[i][j] = box[i][j];
            }
        }

        return res;
    }

}
