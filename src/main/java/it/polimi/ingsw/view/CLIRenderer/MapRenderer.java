package it.polimi.ingsw.view.CLIRenderer;

import it.polimi.ingsw.view.ClientModel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


//TODO: rewrite all CLI rendering functions properly, make the code more robust
//TODO: only draw existing rooms
//TODO: implement other maps
public class MapRenderer {

    public static String[][] getMap(ClientModel model){

        SquareRenderer[] squares = new SquareRenderer[12];

        String[][] map = new String[18][55];

        int mapID = model.getMapID();


        map = loadMap(mapID);     //mapID needs to go here

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

            for(int i=0; i< model.getSquare(n).getBlueAmmo(); i++) {
                ammo.get(n).add(ClientModel.getEscapeCode("blue") + "❚" + "\u001b[0m");    //blue ammo!
            }
            for(int i=0; i< model.getSquare(n).getRedAmmo(); i++) {
                ammo.get(n).add(ClientModel.getEscapeCode("red")+"❚"+"\u001b[0m");    //red ammo!
            }
            for(int i=0; i< model.getSquare(n).getYellowAmmo(); i++) {
                ammo.get(n).add(ClientModel.getEscapeCode("yellow")+"❚"+"\u001b[0m");    //yellow ammo!
            }
            if(model.getSquare(n).isPowerup()){
                ammo.get(n).add("⚡");    //powerup!
            }

            weaponNum[n] = model.getSquare(n).getWeapons().size();  //weapons on ground

        });

        for(int playerID : model.getPlayers().stream().map(x->x.getId()).collect(Collectors.toList())){
            String color = "";
            String mark = "";
            if(playerID == model.getCurrentPlayer().getId()){//maybe the actual player
                mark = "◯";
            } else {
                mark = "◯";
            }
            color = ClientModel.getEscapeCode(model.getPlayer(playerID).getColor());
            if(model.getPlayer(playerID).getPosition()!=null) {
                players.get(model.getPlayer(playerID).getPosition().getId()).add(color + mark + "\u001b[0m");
            }
        }

        for(int n : getRoomSet(mapID)){
            squares[n] = new SquareRenderer(n, ammo.get(n), weaponNum[n], players.get(n));
            placeSquareOnMap(map, squares[n], n);
        };

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

    public static void placeSquareOnMap(String[][] map, SquareRenderer square, int index){
        merge(map, square.getBox(), 1+6*(index/4), 1+14*(index%4));
    }

    public static String[][] loadMap(int id){

        //TODO: avoid reading map at all iterations
        //TODO: move maps to resources
        //TODO: draw other maps

        String[][] map = new String[18][55];

        String fileName = "/map"+ id + ".map";

        try{
        BufferedReader br = new BufferedReader(new InputStreamReader(MapRenderer.class.getResourceAsStream(fileName)));
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

    private static List<Integer>getRoomSet(int id){
        List<Integer> res  = new ArrayList<>(Arrays.asList(0,1,2,4,5,6,7,9,10,11));
        switch(id){
            case 1: res.add(3); res.add(8); break;
            case 2: break;
            case 3: res.add(3); break;
            case 4: res.add(8); break;
            default: break;
        }
        return res;
    }

}