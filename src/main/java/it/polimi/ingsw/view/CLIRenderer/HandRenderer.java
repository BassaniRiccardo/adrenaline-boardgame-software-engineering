package it.polimi.ingsw.view.CLIRenderer;

import it.polimi.ingsw.view.ClientModel;

import java.util.ArrayList;
import java.util.List;

//TODO: rewrite all CLI rendering functions properly, make the code more robust
public class HandRenderer {

    public static String[][] get(ClientModel clientModel) {

        String[][] box = new String[3][60];
        //List<String> list = clientModel.getPowerUpinHand();
        List<String> list = new ArrayList<>();
        String ans = "Hand: ";

        if (list.isEmpty()) {
            ans.concat("empty");
        }

        for (int i = 0; i < list.size(); i++) {
            ans.concat(list.get(i));
            if (i != list.size() - 1) {
                ans.concat(", ");
            }
        }

        for (int i = 0; i < box.length; i++) {
            for (int j = 0; j < box[i].length; j++) {
                box[i][j] = " ";
            }
        }

        for(int i = 0; i<ans.length();i++){
            box[1][i+3] = String.valueOf(ans.charAt(i));
        }

        return box;
    }
}
