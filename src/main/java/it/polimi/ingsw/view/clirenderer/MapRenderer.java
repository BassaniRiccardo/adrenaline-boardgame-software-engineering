package it.polimi.ingsw.view.clirenderer;

import it.polimi.ingsw.view.ClientModel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import static it.polimi.ingsw.view.clirenderer.MainRenderer.RESET;

/**
 * Class creating a bidimensional String array representing the game map
 */
public class MapRenderer {

    private static final Logger LOGGER = Logger.getLogger("clientLogger");
    private boolean firstCall;
    private String[][] backup;

    //these parameters should not be changed as they are strictly related to the serialization of the map and the game rules
    private static final int MAP_HEIGHT = 18;
    private static final int MAP_WIDTH = 55;
    private static final int SQUARES_IN_MAP_1 = 10;
    private static final int SQUARES_IN_MAP_DEFAULT = 11;
    private static final int SQUARES_IN_MAP_4 = 12;
    private static final int MAP_LINES = 4;
    private static final int SQUARE_HEIGHT = 6;
    private static final int SQUARE_WIDTH = 14;
    private static final int FIRST_JUMP = 2;
    private static final int SECOND_JUMP = 7;
    private static final int LEFT_PADDING = 5;


    /**
     * Standard constructor
     */
    MapRenderer(){
        this.firstCall = true;
        this.backup = new String[MAP_HEIGHT][MAP_WIDTH];
    }

    /**
     * Creates a bidimensional String array representing the map graphically. Relies on constructing SquareRenderers
     * and merging their output with the background.
     *
     * @param model     reference to the model
     * @return          graphical representation of the map
     */
    public String[][] getMap(ClientModel model){

        int mapID = model.getMapID();

        //loading map background
        String[][] map = loadMap(mapID);

        if(MAP_HEIGHT<18||MAP_WIDTH<55){
            return map;
        }

        int squareNumber;

        if(mapID==1){
            squareNumber = SQUARES_IN_MAP_1;
        } else if(mapID==4){
            squareNumber = SQUARES_IN_MAP_4;
        } else{
            squareNumber = SQUARES_IN_MAP_DEFAULT;
        }

        //initiating arrays
        SquareRenderer[] squares = new SquareRenderer[squareNumber];
        List<List<String>> ammo = new ArrayList<>(squareNumber);
        int[] weaponNum = new int[squareNumber];
        List<List<String>> players = new ArrayList<>(squareNumber);
        for(int i=0; i<squareNumber; i++){
            players.add(new ArrayList<>());
            ammo.add(new ArrayList<>());
        }

        //constructing parameters for SquareRenderer
        IntStream.range(0, squareNumber-1).forEachOrdered(n -> {

            for(int i=0; i< model.getSquare(n).getBlueAmmo(); i++) {
                ammo.get(n).add(ClientModel.getEscapeCode("blue") + "|" + RESET);    //blue ammo!
            }
            for(int i=0; i< model.getSquare(n).getRedAmmo(); i++) {
                ammo.get(n).add(ClientModel.getEscapeCode("red")+"|"+ RESET);    //red ammo!
            }
            for(int i=0; i< model.getSquare(n).getYellowAmmo(); i++) {
                ammo.get(n).add(ClientModel.getEscapeCode("yellow")+"|"+ RESET);    //yellow ammo!
            }
            if(model.getSquare(n).isPowerup()){
                ammo.get(n).add("+");    //powerup!
            }
            weaponNum[n] = model.getSquare(n).getWeapons().size();  //weapons on ground

        });

        for(int playerID : model.getPlayers().stream().map(ClientModel.SimplePlayer::getId).collect(Collectors.toList())){
            String color;
            String mark;
            if(playerID == model.getPlayerID()){
                mark = "◯";
            } else {
                mark = "●";
            }
            color = ClientModel.getEscapeCode(model.getPlayer(playerID).getColor());
            if(model.getPlayer(playerID).getPosition()!=null) {
                players.get(model.getPlayer(playerID).getPosition().getId()).add(color + mark + RESET);
            }
        }

        //gathering squares and placing them
        for(int n=0; n<squareNumber; n++){
            squares[n] = new SquareRenderer(n, ammo.get(n), weaponNum[n], players.get(n));
            placeSquareOnMap(map, squares[n], n, mapID);
        }

        //adding padding
        String[][] paddedMap = new String[MAP_HEIGHT][MAP_WIDTH+LEFT_PADDING];
        for(int i=0; i<paddedMap.length; i++){
            for(int j=0; j<paddedMap[i].length; j++){
                if(j<LEFT_PADDING){
                    paddedMap[i][j] = " ";
                } else {
                    paddedMap[i][j] = map[i][j-LEFT_PADDING];
                }
            }
        }

        return paddedMap;
    }

    /**
     * Overwrites the content of one array of strings with those of another smaller one.
     *
     * @param box1      base array
     * @param box2      array to be merged on top
     * @param x         horizontal offset
     * @param y         vertical offset
     * @return          result of the merge
     */
    private static String[][] merge(String[][] box1, String[][]box2, int x, int y){
        for(int i = 0; i<box2.length; i++){
            for(int j = 0; j<box2[0].length; j++){
                box1[i+x][j+y]=box2[i][j];
            }
        }
        return box1;
    }

    /**
     * Places a square on the map in the correct position
     *
     * @param map       the map
     * @param square    square to place
     * @param index     number of the square
     * @param mapID     ID of the map
     */
    private static void placeSquareOnMap(String[][] map, SquareRenderer square, int index, int mapID){
        switch (mapID){
            case 1:
                if(index>FIRST_JUMP) index++;
                if(index>SECOND_JUMP) index++;
                break;
            case 2:
                if(index>FIRST_JUMP) index++;
                break;
            case 3:
                if(index>SECOND_JUMP) index++;
                break;
            case 4:
                break;
            default:
                break;
        }
        merge(map, square.getBox(), 1+SQUARE_HEIGHT*(index/MAP_LINES), 1+SQUARE_WIDTH*(index%MAP_LINES));
    }

    /**
     * Loads the map background from file (or from a memorized backup if this is not the first time it is called
     * @param id    map ID
     * @return      map background
     */
    private String[][] loadMap(int id){

        String[][] map = new String[MAP_HEIGHT][MAP_WIDTH];

        String fileName = "/map"+ id + ".map";

        if(firstCall) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(MapRenderer.class.getResourceAsStream(fileName)));
                for (int i = 0; i < MAP_HEIGHT; i++) {
                    for (int j = 0; j < MAP_WIDTH; j++) {
                        String buff;
                        try {
                            buff = br.readLine().replace("\\u001B", "\u001B").concat(RESET);
                        }catch (Exception ex){
                            buff="";
                        }
                        map[i][j] = buff;
                        backup[i][j] = buff;
                    }
                firstCall=false;
                }
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Issue loading map", ex);
            }
        }else{
            for(int i=0; i<MAP_HEIGHT; i++){
                for(int j=0; j<MAP_WIDTH; j++){
                    map[i][j] = backup[i][j];
                }
            }
        }
        return map;
    }
}