package it.polimi.ingsw.view.clirenderer;

import it.polimi.ingsw.view.ClientModel;

import static it.polimi.ingsw.view.clirenderer.MainRenderer.RESET;

/**
 * Class responsible for rendering information regarding the player.
 */
public class HandRenderer {

    private static final int HAND_HEIGHT = 9;
    private static final int HAND_WIDTH = 65;
    private static final int PADDING = 3;
    private static final int SECOND_COLUMN = 30;
    private static final String AMMO_TAG = "Ammo: ";

    private HandRenderer(){}

    /**
     * Creates a bidimensional String array containing graphical data about the player chosen by the user.
     *
     * @param clientModel       reference to the model
     */
    public static String[][] get(ClientModel clientModel) {

        String[][] box = new String[HAND_HEIGHT][HAND_WIDTH];
        for (int i = 0; i < box.length; i++) {
            for (int j = 0; j < box[i].length; j++) {
                box[i][j] = " ";
            }
        }

        if (HAND_HEIGHT<7 || HAND_WIDTH <50) {
            return box;
        }

        //printing name
        ClientModel.SimplePlayer you = clientModel.getPlayer(clientModel.getPlayerID());
        String name =  "You (" + you.getUsername() + ")" + (you.getStatus().equalsIgnoreCase("basic")? "":" [" + you.getStatus() + "]") + (you.isFlipped()? " [FLIPPED]":"");
        for(int i=0; i<name.length()&&i+PADDING<HAND_WIDTH; i++){
            box[1][i+PADDING] = ClientModel.getEscapeCode(you.getColor()) + name.charAt(i) + RESET;
        }

        //printing damage
        String life = "Life: ";
        for(int i =0; i<life.length()&&i+PADDING<HAND_WIDTH; i++){
            box[2][i+PADDING] = String.valueOf(life.charAt(i));
        }
        int j = life.length()+PADDING;
        for (int shooter : you.getDamageID()) {
            if(j>HAND_WIDTH-1){
                break;
            }
            box[2][j] = ClientModel.getEscapeCode(clientModel.getPlayer(shooter).getColor()) + "●" + RESET;
            j++;
        }

        //printing marks
        String marks = "Marks: ";
        for(int i = 0; i<marks.length()&&i+SECOND_COLUMN<HAND_WIDTH; i++){
            box[2][i+SECOND_COLUMN] = String.valueOf(marks.charAt(i));
        }
        j=marks.length()+SECOND_COLUMN;
        for (int shooter : you.getMarksID()) {
            if(j>HAND_WIDTH-1){
                break;
            }
            box[2][j] = ClientModel.getEscapeCode(clientModel.getPlayer(shooter).getColor()) + "◎" + RESET;
            j++;
        }

        //printing ammo
        for(int i=0; i<AMMO_TAG.length()&&i+PADDING<HAND_WIDTH; i++){
            box[3][i+PADDING] = String.valueOf(AMMO_TAG.charAt(i));
        }
        j=AMMO_TAG.length() + PADDING;
        for(int i=0; i<you.getBlueAmmo()&&j<HAND_WIDTH; i++, j++){
            box[3][j] = ClientModel.getEscapeCode("blue") + "|" + RESET;
        }
        for(int i=0; i<you.getRedAmmo()&&j<HAND_WIDTH; i++, j++){
            box[3][j] = ClientModel.getEscapeCode("red") + "|" + RESET;
        }
        for(int i=0; i<you.getYellowAmmo()&&j<HAND_WIDTH; i++, j++){
            box[3][j] = ClientModel.getEscapeCode("yellow") + "|" + RESET;
        }

        //printing weapons
        StringBuilder weapons = new StringBuilder();
        weapons.append("Weapons: ");
        if(you.getWeapons().isEmpty()){
            weapons.append("none");
        } else{
            for(int i=0; i<you.getWeapons().size(); i++){
                ClientModel.SimpleWeapon w = you.getWeapons().get(i);
                weapons.append(w.getName());
                if(w.isLoaded()){
                    weapons.append("*");
                }
                if(i!=you.getWeapons().size()-1){
                    weapons.append(", ");
                }
            }
        }
        for(int i=0; i<weapons.toString().length()&&i+PADDING<HAND_WIDTH; i++){
            box[4][i+PADDING] = String.valueOf(weapons.toString().charAt(i));
        }

        //printing hand
        String hand = "Hand: ";
        int start = 0;
        for(; start<hand.length()&&start+PADDING<HAND_WIDTH; start++){
            box[5][start+PADDING] = String.valueOf(hand.charAt(start));
        }
        if(clientModel.getPowerUpInHand().isEmpty()){
            String empty = "empty";
            for(int i=0; i<empty.length()&&PADDING+start<HAND_WIDTH; i++, start++){
                box[5][PADDING+start] = String.valueOf(empty.charAt(i));
            }
        } else {
            for(int i=0; i<clientModel.getPowerUpInHand().size(); i++){
                String pup = clientModel.getPowerUpInHand().get(i);
                String color = clientModel.getColorPowerUpInHand().get(i);
                for(int k=0; k<pup.length()&&PADDING+start<HAND_WIDTH; k++, start++) {
                    box[5][PADDING+start] = (ClientModel.getEscapeCode(color)+pup.charAt(k)+RESET);
                }
                if(i<clientModel.getPowerUpInHand().size()-1&&PADDING+start<HAND_WIDTH) {
                    box[5][PADDING+start] = ",";
                    start=start+2;
                }
            }
        }

        //printing deaths and points
        String deaths = "Deaths: " + you.getDeaths() + " (next death awards " + you.getNextDeathAwards() + " points)";
        for(int i=0; i<deaths.length()&&i+PADDING<HAND_WIDTH; i++){
            box[6][i+PADDING] = String.valueOf(deaths.charAt(i));
        }
        String points = "Points: " + clientModel.getPoints();
        for(int i=0; i<points.length()&&i+PADDING<HAND_WIDTH; i++){
            box[7][i+PADDING] = String.valueOf(points.charAt(i));
        }

        return MainRenderer.trimBox(box, PADDING, HAND_WIDTH);
    }
}
