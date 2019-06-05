package it.polimi.ingsw.view;

import it.polimi.ingsw.view.CLIRenderer.HandRenderer;
import it.polimi.ingsw.view.CLIRenderer.MapRenderer;
import it.polimi.ingsw.view.CLIRenderer.PlayersRenderer;
import it.polimi.ingsw.view.CLIRenderer.WeaponRenderer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

//FIXME: model does not show number of cards in hand correctly
//fixme: model does not tell the correct ID

/**
 * Simple command line interface for the client's I/O operations
 *
 * @author  marcobaga
 */
public class CLI implements UI{

    private ClientMain clientMain;
    private static final Logger LOGGER = Logger.getLogger("clientLogger");
    private String[][] render;
    private String[][] messageBox;
    private String lastRequest;
    private String answer;
    private boolean receiving, justReceived, info;
    private String weaponRequested;


    /**
     * Standard constructor
     */
    public CLI(ClientMain clientMain) {
        this.clientMain = clientMain;
        this.receiving = false;
        this.lastRequest = "";
        this.answer = "";
        this.render = new String[0][0];
        this.messageBox = new String[0][0];
        this.justReceived = false;
        this.info = false;
        this.weaponRequested = "";
    }

    /**
     * Displays a certain message
     *
     * @param message       message to be displayed
     */
    @Override
    public void display(String message) {
        String toDisplay = message + "\n" + lastRequest;
        int rows = 1;
        int count = 0;
        for(int i = 0; i<toDisplay.length(); i++){
            if(toDisplay.charAt(i)=='\n'){
                count = 0;
                rows++;
            } else {
                count++;
                if (count > 53) {
                    count = 0;
                    rows++;
                }
            }
        }

        messageBox = new String[rows+2][60];

        for(int i = 0; i<messageBox.length; i++){
            for(int j = 0; j<messageBox[i].length; j++){
                messageBox[i][j] = " ";
            }
        }

        int row = 0;
        int col = 0;
        for(int i=0; i<toDisplay.length(); i++){
            if(toDisplay.charAt(i)=='\n'){
                row++;
                col=0;
            } else {
                messageBox[row+1][col+1] = String.valueOf(toDisplay.charAt(i));//+String.valueOf(col);
                col++;
                if(col>53){
                    row++;
                    col=0;
                }
            }
        }

        render();
    }

    @Override
    public void display(String message, String max){
        String fullRequest = message + "[max. " + max + " characters]";
        lastRequest = fullRequest;
        display("");
    }

    @Override
    public void display(String message, List<String> list) {
        String fullRequest = message + "\nHere are your choices: ";
        for(int i = 0; i<list.size(); i++){
            fullRequest = fullRequest + "\n" + (i+1) +") "+list.get(i);
        }
        fullRequest = fullRequest + "\nChoose one";
        lastRequest = fullRequest;
        display("");
    }


    /**
     * Main CLI loop checking for user input asynchronously from other threads, in particular for closing the client while awaiting a message.
     */
    @Override
    public void run() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        while(Thread.currentThread().isAlive()) {
            try{
                if (in.ready()) {
                    String msg = in.readLine();
                    if(msg.equals("q")) {
                        display("CLI: quitting");
                        System.exit(0);
                    }else if(msg.startsWith("info")){
                        info = true;
                        weaponRequested = msg.substring(5);
                        render();
                    }else if(receiving&&!justReceived) {
                        answer = msg;
                        justReceived = true;
                        info = false;
                    }else{
                        info = false;
                        display("Wait for your turn or press q to quit");
                    }
                }
            }catch(IOException e){
                LOGGER.log(Level.SEVERE, "Cannot retrieve input from keyboard, quitting");
                System.exit(0);
            }
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            }catch(InterruptedException ex){
                LOGGER.log(Level.INFO,"Skipped waiting time.");
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public String get(List<String> list){
        receiving = true;
        boolean verified = false;
        while(!verified) {
            while (!justReceived) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException ex) {
                    LOGGER.log(Level.INFO, "Skipped waiting time.");
                    Thread.currentThread().interrupt();
                }
            }
            try {
                if (Integer.parseInt(answer) <= list.size() && Integer.parseInt(answer) > 0) {
                    verified = true;
                } else {
                    display("Input not valid, try again.");
                }
            }catch(NumberFormatException ex){
                display("Input not valid, try again.");
            }
            justReceived = false;
            receiving = true;
        }
        receiving = false;
        lastRequest="";
        return answer;
    }


    @Override
    public String get(String m){
        receiving = true;
        boolean verified = false;
        int max = Integer.parseInt(m);
        while(!verified) {
            while (!justReceived) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException ex) {
                    LOGGER.log(Level.INFO, "Skipped waiting time.");
                    Thread.currentThread().interrupt();
                }
            }
            if (answer.length()<max) {
                verified = true;
            } else{
                display("Your answer must be shorter than " + max + " characters, try again.");
            }
            justReceived = false;
            receiving = true;
        }
        receiving = false;
        lastRequest="";
        return answer;
    }

    private String[][] addFrame(String[][] base){
        if(base.length==0){
            String[][] res = new String [1][1];
            res[0][0]="⊡";
            return res;
        }

        String[][] res = new String[base.length+2][base[0].length+4];
        for(int i=0;i<res[0].length;i++){
            res[0][i] = "⊡";
            res[res.length-1][i] = "⊡";
        }

        for(int i=1;i<res.length-1;i++){
            res[i][0] = "⊡";
            res[i][1] = " ";
            res[i][res[i].length-1] = "⊡";
            res[i][res[i].length-2] = " ";
        }

        for(int i=0; i<base.length; i++){
            for(int j = 0; j<base[i].length; j++){
                res[i+1][j+2] = base[i][j];
            }
        }

        return res;
    }

    public String[][] join(boolean vertical, String[][] box1, String[][] box2, boolean separate){

        if(box1.length==0){
            return box2;
        } else if(box2.length==0){
            return box1;
        }
        String[][] res;
        if(vertical){
            if(separate) {
                res = new String[box1.length+box2.length+1][Math.max(box1[0].length, box2[0].length)];
                for(int i=0; i < res.length; i++){
                    for(int j=0; j<res[0].length;j++){
                        if(i>box1.length){
                            if(j<box2[i-(box1.length+1)].length) {
                                res[i][j] = box2[i - (box1.length+1)][j];
                            } else {
                                res[i][j] = " ";
                            }
                        } else if (i==box1.length){
                            res[i][j] = "⊡";
                        } else{
                            if(j<box1[i].length) {
                                res[i][j] = box1[i][j];
                            }else{
                                res[i][j] = " ";
                            }
                        }
                    }
                }
            } else{
                res = new String[box1.length+box2.length][Math.max(box1[0].length, box2[0].length)];
                for(int i=0; i < res.length; i++){
                    for(int j=0; j<res[i].length; j++){
                        if(i>=box1.length){
                            if(j<box2[i-box1.length].length) {
                                res[i][j] = box2[i-box1.length][j];
                            } else{
                                res[i][j] = " ";
                            }
                        }else{
                            if(j<box1[i].length) {
                                res[i][j] = box1[i][j];
                            }else {
                                res[i][j] = " ";
                            }
                        }
                    }
                }
            }
        } else{
            if(separate) {
                res = new String[Math.max(box1.length, box2.length)][box1[0].length+box2[0].length+3];
                for(int i=0; i < res.length; i++){
                    for(int j=0; j<res[0].length;j++){
                        if(j<box1[0].length){
                            if(i<box1.length){
                                res[i][j] = box1[i][j];
                            } else{
                                res[i][j] = " ";
                            }
                        } else if (j==box1[0].length){
                            res[i][j] = " ";
                        } else if (j==box1[0].length+1) {
                            res[i][j] = "⊡";
                        } else if (j==box1[0].length+2) {
                            res[i][j] = " ";
                        } else {
                            if(i<box2.length){
                                res[i][j] = box2[i][j-(box1[0].length+3)];
                            } else {
                                res[i][j] = " ";
                            }
                        }
                    }
                }
            } else{
                res = new String[Math.max(box1.length, box2.length)][box1[0].length+box2[0].length];
                for(int i=0; i < res.length; i++){
                    for(int j=0; j<res[0].length;j++){
                        if(j>=box1[0].length){
                            if(i<box2.length) {
                                res[i][j] = box2[i][j - (box1[0].length)];
                            }else{
                                res[i][j] = " ";
                            }
                        } else{
                            if(i<box1.length) {
                                res[i][j] = box1[i][j];
                            }else{
                                res[i][j] = " ";
                            }
                        }
                    }
                }
            }
        }
        return res;
    }

    public void drawModel(){
        System.out.print("\033[H\033[2J");
        System.out.flush();
        for(int i=0; i<render.length; i++){
            for(int j = 0; j<render[i].length; j++){
                System.out.print(render[i][j]);
            }
            System.out.print("\n");
        }
    }

    public void render(){
        ClientModel model = clientMain.getClientModel();
        if(model==null){
            render = messageBox;
        } else {
            render = join(true,
                        (addFrame(
                            join(false,
                                join(true,
                                    MapRenderer.getMap(model),
                                    WeaponRenderer.get(model),
                                    false),
                                join(true,
                                    PlayersRenderer.get(model),
                                    HandRenderer.get(model),
                                    true),
                                true))),
                        messageBox,
                        false);
        }
        if(info) {
            render = getInfo(weaponRequested);
        }
        drawModel();
    }

     public String[][] getInfo (String name){
         String toDisplay = "This is the info of the weapon requested.\nPress q to quit, any other key to get back to the game";
         int rows = 1;
         int count = 0;
         for(int i = 0; i<toDisplay.length(); i++){
             if(toDisplay.charAt(i)=='\n'){
                 count = 0;
                 rows++;
             } else {
                 count++;
                 if (count > 53) {
                     count = 0;
                     rows++;
                 }
             }
         }

         String[][] res = new String[rows+2][60];

         for(int i = 0; i<res.length; i++){
             for(int j = 0; j<res[i].length; j++){
                 res[i][j] = " ";
             }
         }

         int row = 0;
         int col = 0;
         for(int i=0; i<toDisplay.length(); i++){
             if(toDisplay.charAt(i)=='\n'){
                 row++;
                 col=0;
             } else {
                 res[row+1][col+1] = String.valueOf(toDisplay.charAt(i));//+String.valueOf(col);
                 col++;
                 if(col>53){
                     row++;
                     col=0;
                 }
             }
         }
         return res;
     }
}