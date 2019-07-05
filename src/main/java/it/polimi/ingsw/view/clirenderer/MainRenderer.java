package it.polimi.ingsw.view.clirenderer;

import it.polimi.ingsw.view.ClientMain;
import it.polimi.ingsw.view.ClientModel;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main class used for rendering. It assembles the products of other rendering classes and manages what appears
 * on the console.
 */
public class MainRenderer {

    private static final int BOX_WIDTH = 55;
    private static final int MESSAGE_HEIGHT = 10;
    private static final int REQUEST_HEIGHT = 3;
    private static final int MESSAGE_MEMORY = 3;
    private static final int PADDING = 1;
    static final String RESET = "\u001b[0m";
    public static final String CLEAR_CONSOLE = "\033[H\033[2J";

    private String currentRequest;
    private String currentMessage;
    private List<String> messages;
    private ClientMain clientMain;
    private MapRenderer mapRenderer;

    /**
     * Standard constructor
     *
     * @param clientMain    reference to main class
     */
    public MainRenderer(ClientMain clientMain) {
        this.currentRequest = "";
        this.currentMessage = "";
        this.messages = new ArrayList<>();
        this.clientMain = clientMain;
        this.mapRenderer = new MapRenderer();
    }

    /**
     * Prepares a bidimensional array of Strings to be displayed by assembling different content boxes.
     * The graphical interface is aves as a bidimensional array of strings, each containing a single character and escape codes
     * to format it.
     */
    public void render(){
        ClientModel model = clientMain.getClientModel();
        String[][] render;
        if(model==null) {
            render = getMessages();
            if (!currentRequest.isEmpty()||!currentMessage.isEmpty()){
                render = join(true, getMessages(), stringToBox(currentMessage + currentRequest, REQUEST_HEIGHT, BOX_WIDTH, false), true);
            }
        } else {
            render = join(  true,
                            join(   true,
                                    (addFrame(
                                            join(false,
                                                    join(true,
                                                            mapRenderer.getMap(model),
                                                            WeaponRenderer.get(model),
                                                            false),
                                                    join(true,
                                                            PlayersRenderer.get(model),
                                                            HandRenderer.get(model),
                                                            true),
                                                    true))),
                                    getMessages(),
                                    false),
                            stringToBox(currentMessage + currentRequest, REQUEST_HEIGHT, BOX_WIDTH, false),
                            true);
        }
        drawModel(render);
    }

    /**
     * Shows quit warning
     */
    public static void showQuitScreen(){
        System.out.print(CLEAR_CONSOLE);
        System.out.println("Are you sure you want to quit? (Y/N)");
    }

    /**
     * Shows the info o a certain weapon or powerup
     */
    public static void showInfoScreen(String weaponName){
        System.out.print(CLEAR_CONSOLE);
        try {
            Scanner input = new Scanner(new InputStreamReader(MainRenderer.class.getResourceAsStream("/guides/"+weaponName.replace(' ', '_').toLowerCase()+".txt")));
            while (input.hasNextLine()) {
                System.out.println(input.nextLine());
            }
        }catch(Exception ex){
            System.out.println("We could not find that weapon in our manual, sorry!");
        }
        System.out.println("\nPress any key to get back to the game.");
        System.out.flush();
    }

    /**
     * Sets the latest request to be displayed
     *
     * @param request   request to be displayed
     */
    public void setCurrentRequest(String request){
        this.currentRequest = request;
    }

    /**
     * Sets the latest message to be displayed
     *
     * @param message   messagr to be displayed
     */
    public void setCurrentMessage(String message) {this.currentMessage = message + "\n";}

    /**
     * Adds a message to the list of those to be shown and resizes it
     * @param message   message to add
     */
    public void addMessage(String message){
        while(messages.size()>=MESSAGE_MEMORY) {
            messages.remove(0);
        }
        messages.add(message);
    }

    /**
     * Returns a bidimensional String array containing the last messages received
     * @return  String array
     */
    private String[][] getMessages() {
        StringBuilder bld = new StringBuilder();
        for(String message : messages){
            bld.append(message);
            bld.append("\n");
        }
        if(!messages.isEmpty()) {
            bld.deleteCharAt(bld.lastIndexOf("\n"));
        }
        return stringToBox(bld.toString(), MESSAGE_HEIGHT, BOX_WIDTH,true);
    }


    /**
     * Builds a bidimensional string array showing a message
     *
     * @param message   the message to show
     * @param height    the height of the array
     * @param width     the width of the array
     * @param fixedHeight   if true, it trims the first lines of the message to fit in the array
     * @return          string array
     */
    private static String[][] stringToBox(String message, int height, int width, boolean fixedHeight){

        //counts how many rows are necessary
        int rows = 3;
        int count = 0;
        for(int i = 0; i<message.length(); i++){
            if(message.charAt(i)=='\n'){
                count = 0;
                rows++;
            } else {
                count++;
                if (count > width-2) {
                    count = 0;
                    rows++;
                }
            }
        }

        int startFromRow = 0;
        if(fixedHeight&&rows>height) {
            startFromRow = rows-height;
            rows = height;
        }

        String[][] res = new String[rows][width];

        for(int i = 0; i<res.length; i++){
            for(int j = 0; j<res[i].length; j++){
                res[i][j] = " ";
            }
        }

        int row = 0;
        int col = 0;
        for(int i=0; i<message.length()&&row-startFromRow<=rows; i++){
            if(message.charAt(i)=='\n'){
                row++;
                col=0;
            } else {
                if(row>=startFromRow&&row<rows-2*PADDING) {
                    res[row - startFromRow + PADDING][col + PADDING] = String.valueOf(message.charAt(i));
                }
                col++;
                if(col>width-2-2*PADDING&&row>=startFromRow&&row<rows-2*PADDING){
                    if(i<message.length()-1&&message.charAt(i)!=' '&&message.charAt(i+1)!=' ') {
                        res[row - startFromRow + PADDING][col + PADDING+1] = "-";
                    }
                    row++;
                    col=0;
                }
            }
        }
        return res;

    }

    /**
     * Adds a frame to a String array
     *
     * @param base      content to be framed
     * @return  String array
     */
    private static String[][] addFrame(String[][] base){
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

    /**
     * Joins two bidimensional String arrays
     *
     * @param vertical  if true, the two arrays are set in a vertical layout, else they are set side by side
     * @param box1  first merging array
     * @param box2  second merging array
     * @param separate  if true, the two arrays are separated by a line
     * @return  resulting String array
     */
    private static String[][] join(boolean vertical, String[][] box1, String[][] box2, boolean separate){

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

    /**
     * Manages actual printing to console
     *
     * @param render    String array to print
     */
    private static void drawModel(String[][] render){
        System.out.print(CLEAR_CONSOLE);
        System.out.flush();
        for(int i=0; i<render.length; i++){
            for(int j = 0; j<render[i].length; j++){
                System.out.print(render[i][j]);
            }
            System.out.print("\n");
        }
        System.out.flush();
    }

    /**
     * Takes a bidimensional string array and removed empty columns on the right.
     *
     * @param box       array to resize
     * @param padding   white space to be mantained
     * @param width     width of the box
     * @return          resized box
     */
    static String[][] trimBox(String[][] box, int padding, int width){
        int jmax = 0;
        for(int i=0; i<box.length; i++){
            for(int j=0; j<box[i].length; j++){
                if(!box[i][j].equals(" ")&&j>jmax){
                    jmax = j;
                }
            }
        }
        jmax = Math.min(jmax+padding, width);
        String[][] trimmedBox = new String [box.length][jmax];
        for(int i=0; i<trimmedBox.length; i++){
            for(int j=0; j<trimmedBox[i].length; j++){
                trimmedBox[i][j] = box[i][j];
            }
        }
        return trimmedBox;
    }
}
