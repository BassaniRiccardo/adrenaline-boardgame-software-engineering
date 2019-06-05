package it.polimi.ingsw.view.CLIRenderer;

import it.polimi.ingsw.view.ClientModel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


//TODO: rewrite all CLI rendering functions properly, make the code more robust
public class MapRenderer {

    private static final Logger LOGGER = Logger.getLogger("clientLogger");
    private boolean firstCall;
    private String[][] backup;

    public MapRenderer(){
        this.firstCall = true;
        this.backup = new String[18][55];
    }

    public String[][] getMap(ClientModel model){

        int mapID = model.getMapID();

        String[][] map = loadMap(mapID);     //mapID needs to go here

        int squareNumber;

        if(mapID==1){
            squareNumber = 10;
        } else if(mapID==4){
            squareNumber = 12;
        } else{
            squareNumber = 11;
        }

        SquareRenderer[] squares = new SquareRenderer[squareNumber];

        List<List<String>> ammo = new ArrayList<>(squareNumber);
        int[] weaponNum = new int[squareNumber];
        List<List<String>> players = new ArrayList<>(squareNumber);
        for(int i=0; i<squareNumber; i++){
            players.add(new ArrayList<>());
            ammo.add(new ArrayList<>());
        }

        IntStream.range(0, squareNumber-1).forEachOrdered(n -> {

            for(List<String> l : ammo){
                l = new ArrayList<>();
            }
            weaponNum[n] = 0;
            for(List<String> l : players){
                l = new ArrayList<>();
            }

            for(int i=0; i< model.getSquare(n).getBlueAmmo(); i++) {
                ammo.get(n).add(ClientModel.getEscapeCode("blue") + "|" + "\u001b[0m");    //blue ammo!
            }
            for(int i=0; i< model.getSquare(n).getRedAmmo(); i++) {
                ammo.get(n).add(ClientModel.getEscapeCode("red")+"|"+"\u001b[0m");    //red ammo!
            }
            for(int i=0; i< model.getSquare(n).getYellowAmmo(); i++) {
                ammo.get(n).add(ClientModel.getEscapeCode("yellow")+"|"+"\u001b[0m");    //yellow ammo!
            }
            if(model.getSquare(n).isPowerup()){
                ammo.get(n).add("+");    //powerup!
            }

            weaponNum[n] = model.getSquare(n).getWeapons().size();  //weapons on ground

        });

        for(int playerID : model.getPlayers().stream().map(x->x.getId()).collect(Collectors.toList())){
            String color = "";
            String mark = "";
            if(playerID == model.getPlayerID()){
                mark = "◯";
            } else {
                mark = "●";
            }
            color = ClientModel.getEscapeCode(model.getPlayer(playerID).getColor());
            if(model.getPlayer(playerID).getPosition()!=null) {
                players.get(model.getPlayer(playerID).getPosition().getId()).add(color + mark + "\u001b[0m");
            }
        }

        for(int n=0; n<squareNumber; n++){
            squares[n] = new SquareRenderer(n, ammo.get(n), weaponNum[n], players.get(n));
            placeSquareOnMap(map, squares[n], n, mapID);
        }

        return map;
    }

    private static String[][] merge(String[][] box1, String[][]box2, int x, int y){
        for(int i = 0; i<box2.length; i++){
            for(int j = 0; j<box2[0].length; j++){
                box1[i+x][j+y]=box2[i][j];
            }
        }
        return box1;
    }

    public static void placeSquareOnMap(String[][] map, SquareRenderer square, int index, int mapID){
        switch (mapID){
            case 1:
                if(index>2) index++;
                if(index>7) index++;
                break;
            case 2:
                if(index>2) index++;
                break;
            case 3:
                if(index>7) index++;
                break;
            case 4:
                break;
            default:
                break;
        }
        merge(map, square.getBox(), 1+6*(index/4), 1+14*(index%4));
    }

    public String[][] loadMap(int id){

        String[][] map = new String[18][55];

        String fileName = "/map"+ id + ".map";

        if(firstCall) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(MapRenderer.class.getResourceAsStream(fileName)));
                for (int i = 0; i < 18; i++) {
                    for (int j = 0; j < 55; j++) {
                        String buff = br.readLine().replace("\\u001B", "\u001B").concat("\u001B[0m");
                        map[i][j] = buff;
                        backup[i][j] = buff;
                    }
                }
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Issue loading map", ex);
            }
        }else{
            for (int i = 0; i < 18; i++) {
                for (int j = 0; j < 55; j++) {
                    map[i][j] = backup[i][j];
                }
            }        }
        return map;
    }
}