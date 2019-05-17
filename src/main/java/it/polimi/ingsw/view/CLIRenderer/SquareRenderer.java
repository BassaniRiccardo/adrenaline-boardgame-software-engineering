package it.polimi.ingsw.view.CLIRenderer;

import java.util.List;


//TODO: rewrite all CLI rendering functions properly, make the code more robust
public class SquareRenderer {

    private String[][] box = new String[4][11];
    private Integer squareID;

    public SquareRenderer(int squareID, List<String> ammo, int weaponNum, List<String> players) {

        this.squareID = squareID;

        for (int i = 0; i < box.length; i++) {
            for (int j = 0; j < box[0].length; j++) {
                box[i][j] = " ";
            }
        }

        box[0][1] = String.valueOf(squareID / 10);
        box[0][2] = String.valueOf(squareID % 10);


        for (int i = 0; i < ammo.size(); i++) {
            box[0][i + 8] = ammo.get(i);
        }

        box[0][10] = String.valueOf(weaponNum);

        for (int i = 0; i < players.size(); i++) {

            switch (players.size()) {
                case 1:
                    box[2][5] = players.get(i); break;
                case 2:
                    box[2][4 + 2 * i] = players.get(i); break;
                case 3:
                    box[2][3 + 2 * i] = players.get(i); break;
                case 4:
                    box[2 + i / 2][4 + 2 * (i % 2)] = players.get(i); break;
                case 5:
                    box[2][3 + i] = players.get(i);
            }
        }
    }

    public String[][] getBox(){return box;}
}
