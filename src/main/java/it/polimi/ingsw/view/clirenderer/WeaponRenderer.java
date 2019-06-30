package it.polimi.ingsw.view.clirenderer;

import it.polimi.ingsw.view.ClientModel;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static it.polimi.ingsw.view.clirenderer.MainRenderer.RESET;

/**
 * Class responsible for rendering information regarding weapons on the ground, cards in deck and the killshot track.
 */
public class WeaponRenderer {

    private static final Logger LOGGER = Logger.getLogger("clientLogger");
    private static final int WEAPON_HEIGHT = 8;
    private static final int WEAPON_LENGTH = 55;
    private static final int PADDING = 3;
    private static final int SECOND_COLUMN = 14;

    private WeaponRenderer(){}

    /**
     * Creates a bidimensional String array containing data about weapons on the ground and so on
     *
     * @param clientModel       reference to the model
     * @return                  graphical representation of weapons on ground
     */
    public static String[][] get(ClientModel clientModel){

        //calculates number of rows needed
        int rows = (int)clientModel.getSquares().stream().filter(x->!x.getWeapons().isEmpty()).count();

        String[][] box = new String[rows+WEAPON_HEIGHT][WEAPON_LENGTH];
        for(int i=0; i<box.length; i++){
            for(int j=0; j<box[i].length; j++){
                box[i][j] = " ";
            }
        }

        //writes list of weapons on ground
        try {
            String[] weapons = new String[rows];
            int ind = 0;
            for (ClientModel.SimpleSquare s : clientModel.getSquares()) {
                if (!s.getWeapons().isEmpty()) {
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
            for (int i = 0; i < weapons.length; i++) {
                for (int j = 0; j < weapons[i].length() && j + PADDING < WEAPON_LENGTH; j++) {
                    box[1 + i][PADDING + j] = String.valueOf(weapons[i].charAt(j));
                }
            }
        }catch(Exception ex){
            LOGGER.log(Level.SEVERE, "Error while listing weapons in rooms", ex);
        }

        //draws killshot track
        try {
            List<ClientModel.SimplePlayer> shooters = clientModel.getKillShotTrack();
            String[] killshotTrack = new String[shooters.size()+clientModel.getSkullsLeft()];
            for (int i = 0; i < shooters.size(); i++) {
                killshotTrack[i] = ClientModel.getEscapeCode(shooters.get(i).getColor()) + "♱" + RESET;
            }
            for(int i=shooters.size(); i<shooters.size()+clientModel.getSkullsLeft(); i++){
                killshotTrack[i] = "#";
            }
            for (int i = 0; i < "KillShot: ".length()&&i + PADDING < WEAPON_LENGTH; i++) {
                box[rows + 2][i + PADDING] = String.valueOf("Killshot: ".charAt(i));
            }
            for (int i = 0; i < killshotTrack.length&&i + SECOND_COLUMN < WEAPON_LENGTH; i++) {
                box[rows + 2][i + SECOND_COLUMN] = String.valueOf(killshotTrack[i]);
            }
        }catch(Exception ex){
            LOGGER.log(Level.SEVERE, "Error while printing killshot track", ex);
        }

        //prints weapon deck size
        String weaponDeck = "Weapons left in deck: " + clientModel.getWeaponCardsLeft();
        for(int i=0; i<weaponDeck.length()&&i + PADDING < WEAPON_LENGTH; i++){
            box[rows+4][i+PADDING] = String.valueOf(weaponDeck.charAt(i));
        }

        //prints powerup deck size
        String powerUpDeck = "PowerUps left in deck: " + clientModel.getPowerUpCardsLeft();
        for(int i=0; i<powerUpDeck.length()&&i + PADDING < WEAPON_LENGTH; i++){
            box[rows+6][i+PADDING] = String.valueOf(powerUpDeck.charAt(i));
        }
        return box;
    }

}
