package it.polimi.ingsw.view.CLIRenderer;

import java.util.List;

/**
 * Class creating a bidimensional String array containing representing a single square
 */
public class SquareRenderer {

    private String[][] box;

    private static final int SQUARE_HEIGHT = 4;
    private static final int SQUARE_WIDTH = 11;
    private static final int FIRST_DIGIT_X = 0;
    private static final int FIRST_DIGIT_Y = 1;
    private static final int SECOND_DIGIT_X = 0;
    private static final int SECOND_DIGIT_Y = 2;
    private static final int AMMO_X = 0;
    private static final int AMMO_Y = 8;
    private static final int WEAPON_X = 0;
    private static final int WEAPON_Y = 10;
    private static final int PLAYER_X = 2;


    public SquareRenderer(int squareID, List<String> ammo, int weaponNum, List<String> players) {

        box = new String[SQUARE_HEIGHT][SQUARE_WIDTH];

        for (int i = 0; i < box.length; i++) {
            for (int j = 0; j < box[0].length; j++) {
                box[i][j] = " ";
            }
        }

        box[FIRST_DIGIT_X][FIRST_DIGIT_Y] = String.valueOf(squareID / 10);
        box[SECOND_DIGIT_X][SECOND_DIGIT_Y] = String.valueOf(squareID % 10);


        for (int i = 0; i < ammo.size() && i+AMMO_Y<SQUARE_WIDTH; i++) {
            box[AMMO_X][i+AMMO_Y] = ammo.get(i);
        }
        if(weaponNum!=0) {
            box[WEAPON_X][WEAPON_Y] = String.valueOf(weaponNum);
        }

        for (int i = 0; i < players.size(); i++) {

            switch (players.size()) {
                case 1:
                    box[PLAYER_X][5] = players.get(i); break;
                case 2:
                    box[PLAYER_X][4 + 2 * i] = players.get(i); break;
                case 3:
                    box[PLAYER_X][3 + 2 * i] = players.get(i); break;
                case 4:
                    box[PLAYER_X + i / 2][4 + 2 * (i % 2)] = players.get(i); break;
                case 5:
                    box[PLAYER_X][3 + i] = players.get(i);
            }
        }
    }

    public String[][] getBox(){return box;}
}
