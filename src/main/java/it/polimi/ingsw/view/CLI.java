package it.polimi.ingsw.view;

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

/**
 * Simple command line interface for the client's I/O operations
 *
 * @author  marcobaga
 */
public class CLI implements UI{

    private ClientMain clientMain;
    private static final Logger LOGGER = Logger.getLogger("clientLogger");
    private String[][] render;
    private String answer;
    private boolean receiving, justReceived;
    String[][] messageBox;


    /**
     * Standard constructor
     */
    public CLI(ClientMain clientMain) {
        this.clientMain = clientMain;
        receiving = false;
        justReceived = false;
    }

    /**
     * Displays a certain message
     *
     * @param message       message to be displayed
     */
    @Override
    public void display(String message) {
        //String toDisplay = message + "\n" + currentRequest;
        int rows = 1;
        int count = 0;
        boolean attention = false;
        for(int i = 0; i<message.length(); i++){
            count++;
            if(count>58){
                count = 0;
                rows++;
                attention = false;
            }
            if(message.charAt(i)=='\\'){
                attention = true;
            }
            if(attention&&message.charAt(i)=='n'){
                count = 0;
                rows++;
                attention = false;
            }
        }

        messageBox = new String[rows][60];

        for(int i = 0; i<messageBox.length; i++){
            for(int j = 0; j<messageBox[i].length; j++){
                messageBox[i][j] = " ";
            }
        }

        int row = 0;
        int col = 0;
        for(int i=0; i<message.length(); i++){
            if(message.charAt(i)=='\\'){
                attention = true;
            } else if(attention&&message.charAt(i)=='n'){
                row++;
                attention = false;
            } else {
                messageBox[row][col] = String.valueOf(message.charAt(i));
                attention = false;
                col++;
            }
            if(col>58){
                row++;
                col=0;
            }
        }

        render();
    }

    @Override
    public void display(String message, String max){
        display(message + "[max. " + max + " characters]");
    }

    @Override
    public void display(String message, List<String> list) {

        String line = message + "\nHere are your choices: ";
        for(int i = 0; i<list.size(); i++){
            line = line + "\n" + (i+1) +") "+list.get(i);
        }
        display(line + "\nChoose one");
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
                    if(msg.equals("q")){
                        display("CLI: quitting");
                        System.exit(0);
                    }else if(receiving&&!justReceived) {
                        answer = msg;
                        justReceived = true;
                    }else{
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
            for(int i = 1; i<=list.size(); i++){
                if(String.valueOf(i).equals(answer)) {
                    verified = true;
                }
                justReceived = false;
                receiving = true;
            }
        }
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
                justReceived = false;
            } else{
                display("Your answer must be shorter than " + max + " characters, try again");
            }
        }
        receiving = false;
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
                            if(j<box2[i-box1.length].length) {
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
                        } else{
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
                        if(j>box1[i].length+2){
                            if(i<box2.length) {
                                res[i][j] = box2[i][j - (box1[i].length+3)];
                            }else{
                                res[i][j] = " ";
                            }
                        } else if (j==box1[i].length) {
                            res[i][j] = " ";
                        }else if (j==box1[i].length+1) {
                            res[i][j] = "⊡";
                        }else if (j==box1[i].length+2){
                            res[i][j] = " ";
                        } else{
                            if(i<box1.length) {
                                res[i][j] = box1[i][j];
                            }else {
                                res[i][j] = " ";
                            }
                        }
                    }
                }
            } else{
                res = new String[Math.max(box1.length, box2.length)][box1[0].length+box2[0].length];
                for(int i=0; i < res.length; i++){
                    for(int j=0; j<res[0].length;j++){
                        if(j>=box1[i].length){
                            if(i<box2.length) {
                                res[i][j] = box2[i][j - (box1[i].length + 1)];
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
        if(clientMain.getClientModel()==null){      //temporary
            render = messageBox;
        }
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
            render = join(true, (addFrame(join(false, join(true, MapRenderer.getMap(model), WeaponRenderer.get(model), false), PlayersRenderer.get(model), true))), messageBox, false);
        }
        drawModel();
    }

}