package it.polimi.ingsw.view;

import it.polimi.ingsw.view.CLIRenderer.MapRenderer;
import it.polimi.ingsw.view.CLIRenderer.PlayersRenderer;
import it.polimi.ingsw.view.CLIRenderer.WeaponRenderer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

//TODO: CLI rendering needs a major overhaul. Right now it should be considered an experimental package
//TODO: access rendering functionalities from requests

/**
 * Simple command line interface for the client's I/O operations
 *
 * @author  marcobaga
 */
public class CLI implements UI{

    private Scanner in;
    private ClientMain clientMain;
    private static final Logger LOGGER = Logger.getLogger("clientLogger");
    private String[][] render;
    private String answer;
    private boolean receiving, justReceived;


    /**
     * Standard constructor
     */
    public CLI(ClientMain clientMain) {
        this.in = new Scanner(System.in);
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
        drawModel();
        System.out.println(message);}

    @Override
    public void display(String message, String max){
        drawModel();
        System.out.println(message + "[max. " + max + "caratteri]");
    }

    @Override
    public void display(String message, List<String> list) {

        System.out.println(message);
        drawModel();
        System.out.println("Here are your choices:");
        for(int i = 0; i<list.size(); i++){
            System.out.println((i+1) +") "+list.get(i));
        }
        System.out.println("Choose one");

    }

    /**
     * Queries the user for input (blocking)
     *
     * @return              the user's input as a string
     */
    public String get(){
        receiving = true;
        while(!justReceived){
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            }catch(InterruptedException ex){
                LOGGER.log(Level.INFO,"Skipped waiting time.");
                Thread.currentThread().interrupt();
            }
        }
        justReceived = false;
        receiving = false;
        return answer;
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
                    //System.out.println("Ho letto " + msg);
                    if(msg.equals("q")){
                        display("CLI: quitting");
                        //quit
                    }else if(receiving&&!justReceived) {
                        //System.out.println("Ho salvato come answer " + msg);
                        answer = msg;
                        justReceived = true;
                    }else{
                        display("Wait for your turn or press q to quit");
                    }
                }
            }catch(IOException e){
                LOGGER.log(Level.SEVERE, "Cannot retrieve input from keyboard, quitting");
                //quit
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
                System.out.println("Your answer must be shorter than " + max + " characters, try again");
            }
        }
        receiving = false;
        return answer;
    }




    private String[][] addFrame(String[][] base){

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
            return;
        }
        for(int i=0;i<100;i++){
            System.out.print("\n");
        }
        for(int i=0; i<render.length; i++){
            for(int j = 0; j<render[i].length; j++){
                System.out.print(render[i][j]);
            }
            System.out.print("\n");
        }
    }

    public void render(){
        ClientModel model = clientMain.getClientModel();
        render = addFrame(join(false,join(true,  MapRenderer.getMap(model), WeaponRenderer.get(model), false), PlayersRenderer.get(model), true));
    }

}