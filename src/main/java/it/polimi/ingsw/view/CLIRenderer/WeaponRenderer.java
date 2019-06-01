package it.polimi.ingsw.view.CLIRenderer;

import it.polimi.ingsw.view.ClientModel;

import java.util.List;
import java.util.stream.Collectors;

//TODO: rewrite all CLI rendering functions properly, make the code more robust
public class WeaponRenderer {

    public static String[][] get(ClientModel clientModel){
        String[][] box = new String[9][55];

        String[] weapons = new String[clientModel.getSquares().stream().map(x->!x.getWeapons().isEmpty()).collect(Collectors.toList()).size()];
        List<ClientModel.SimplePlayer> shooters = clientModel.getKillShotTrack();
        String[] killshotTrack = new String[shooters.size()];

        for(int i=0; i<shooters.size(); i++){
            killshotTrack[i] = ClientModel.getEscapeCode(shooters.get(i).getColor()) + "♱" + "\u001b[0m";
        }

        int ind=0;
        for(ClientModel.SimpleSquare s : clientModel.getSquares()){
            if(!s.getWeapons().isEmpty()) {
                weapons[ind] = "Room " + s.getId() + ": ";
                List<ClientModel.SimpleWeapon> list = s.getWeapons();
                for (int i = 0; i < list.size(); i++) {
                    weapons[ind] = weapons[ind].concat(list.get(i).getName());
                    if (i != list.size() - 1) {
                        weapons[ind] = weapons[ind].concat(", ");
                    }
                }
                ind++;
            }
        }

        for(int i=0; i<box.length; i++){
            for(int j=0; j<box[i].length; j++){
                box[i][j] = " ";
            }
        }

        for(int i=0; i<weapons.length; i++){
            //for(int j=0; j<weapons[i].length(); j++){
            //    box[1+i][3+j] = String.valueOf(weapons[i].charAt(j));
            //}
        }

        for(int i=0; i<"KillShot: ".length(); i++){
            box[5][i+3] = String.valueOf("Killshot: ".charAt(i));
        }

        for(int i=0; i< killshotTrack.length; i++){
            box[5][i+14] = String.valueOf(killshotTrack[i]);
        }

        String hand = "Hand: ";

        List<String> powerUpList = clientModel.getPowerUpInHand();
        for (int i = 0; i< powerUpList.size(); i++){
            hand = hand.concat(powerUpList.get(i));
            if(i!=powerUpList.size()-1){
                hand = hand.concat(", ");
            }
        }

        for(int i=0; i<hand.length(); i++){
            box[7][i+3] = String.valueOf(hand.charAt(i));
        }

        return box;
    }

}
