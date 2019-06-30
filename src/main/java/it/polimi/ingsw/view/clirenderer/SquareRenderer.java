package it.polimi.ingsw.view.clirenderer;

import java.util.List;

/**
 * Class creating a bidimensional String array representing a single square
 */
class SquareRenderer {

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

    /**
     * Class constructor. Note that the String array is built during construction.
     *
     * @param squareID      id of the square
     * @param ammo          String containing ammo symbols
     * @param weaponNum     number of weapons in square
     * @param players       String containing player symbols
     */
    SquareRenderer(int squareID, List<String> ammo, int weaponNum, List<String> players) {

        box = new String[SQUARE_HEIGHT][SQUARE_WIDTH];

        for (int i = 0; i < box.length; i++) {
            for (int j = 0; j < box[0].length; j++) {
                box[i][j] = " ";
            }
        }

        //printing square number
        box[FIRST_DIGIT_X][FIRST_DIGIT_Y] = String.valueOf(squareID / 10);
        box[SECOND_DIGIT_X][SECOND_DIGIT_Y] = String.valueOf(squareID % 10);


        //printing ammo and weapons
        for (int i = 0; i < ammo.size() && i+AMMO_Y<SQUARE_WIDTH; i++) {
            box[AMMO_X][i+AMMO_Y] = ammo.get(i);
        }
        if(weaponNum!=0) {
            box[WEAPON_X][WEAPON_Y] = String.valueOf(weaponNum);
        }

        //placing players
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
                    box[PLAYER_X][3 + i] = players.get(i); break;
                default:
                    box[PLAYER_X][5] = players.get(i); break;
            }
        }
    }

    /**
     * Returns the bidimensional array representing the square
     *
     * @return bidimensional String array
     */
    String[][] getBox(){return box;}
}
