package it.polimi.ingsw.view.CLIRenderer;

import it.polimi.ingsw.view.ClientModel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;


//TODO: rewrite all CLI rendering functions properly, make the code more robust
//TODO: only draw existing rooms
//TODO: implement other maps
public class MapRenderer {

    public static String[][] getMap(ClientModel model){

        SquareRenderer[] squares = new SquareRenderer[12];

        String[][] map = new String[18][55];

        int mapID = model.getMapID();


        map = loadMap(1);     //mapID needs to go here

        List<List<String>> ammo = new ArrayList<>(12);
        int[] weaponNum = new int[12];
        List<List<String>> players = new ArrayList<>(12);
        for(int i=0; i<12; i++){
            players.add(new ArrayList<>());
            ammo.add(new ArrayList<>());
        }

        IntStream.range(0, 11).forEachOrdered(n -> {

            for(List<String> l : ammo){
                l = new ArrayList<>();
            }
            weaponNum[n] = 0;
            for(List<String> l : players){
                l = new ArrayList<>();
            }

            for(int i=0; i< model.getBlueAmmoOnGround().getOrDefault(n, 0); i++) {
                ammo.get(n).add(ClientModel.getEscapeCode("blue") + "x" + "\u001b[0m");    //blue ammo!
            }
            for(int i=0; i< model.getRedAmmoOnGround().getOrDefault(n, 0); i++) {
                ammo.get(n).add(ClientModel.getEscapeCode("red")+"x"+"\u001b[0m");    //red ammo!
            }
            for(int i=0; i< model.getYellowAmmoOnGround().getOrDefault(n, 0); i++) {
                ammo.get(n).add(ClientModel.getEscapeCode("yellow")+"x"+"\u001b[0m");    //yellow ammo!
            }
            if(model.getPowerUpOnGround().getOrDefault(n, false)){
                ammo.get(n).add("+");    //powerup!
            }

            weaponNum[n] = model.getWeaponsOnGround().getOrDefault(n, new ArrayList<>(0)).size();  //weapons on ground

        });

        for(int playerID : model.getPlayerPosition().keySet()){
            String color = "";
            String mark = "";
            if(playerID == model.getCurrentPlayer()){
                mark = "@";
            } else {
                mark = "O";
            }
            color = ClientModel.getEscapeCode(model.getPlayerColor().get(playerID));
            players.get(model.getPlayerPosition().get(playerID)).add(color + mark + "\u001b[0m");
        }

        for(int n = 0; n<12; n++){
            squares[n] = new SquareRenderer(n, ammo.get(n), weaponNum[n], players.get(n));
        };

        placeSquaresOnMap(map, squares);

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

    public static void placeSquaresOnMap(String[][] map, SquareRenderer[] squares){
        for(int i = 0; i < squares.length; i++){
            merge(map, squares[i].getBox(), 1+6*(i/4), 1+14*(i%4));
        }
    }

    public static String[][] loadMap(int id){

        //TODO: avoid reading map at all iterations
        //TODO: move maps to resources
        //TODO: draw other maps

        String[][] map = new String[18][55];

        String fileName = "map"+ id + ".txt";

        try{
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        for(int i=0; i<18; i++){
            for(int j=0; j<55; j++){
                String buff = br.readLine().replace("\\u001B", "\u001B").concat("\u001B[0m");
                map[i][j] = buff;
            }
        }
        }catch(Exception ex){
            //LOGGER.Log(Level.SEVERE, "Issue loading map", ex);
        }
        return map;
    }

    public  void displayMap(String[][] map){
        for(int i=0; i<map.length; i++){
            for(int j=0; j<map[i].length; j++){
                System.out.print(map[i][j]);
            }
            System.out.print("\n");
        }
    }

    //public void setSquares(SquareRenderer[] squares){
    //    this.squares = squares;
    //}

}