package it.polimi.ingsw.view;

//TODO: implement

import java.util.List;




/**
 * Graphical user interface for I/O operations
 *
 * @author  marcobaga
 */

public class GUI implements UI, Runnable {

    //niente hardcode, tenere presente le dimensioni della finestra
    //i file png o jpeg delle carte vanno nella cartella resources (comei json)

    private ClientMain clientMain;

    public GUI(ClientMain clientMain){
        this.clientMain = clientMain;
    }

    /**
     * Displays content
     * @param message   message to be displayed
     */
    @Override
    public void display(String message) { }

    /**
     * Queries the user for input
     * @return      the user's input
     */
    @Override
    public String get() {
        return null;
    }

    @Override
    public String get(List<String> list){
        return "";
    }

    @Override
    public String get(String max){ return "";}

    @Override
    public void render(){}


        /**
         * Main GUI loop
         */
    public void run(){}

    public void display(List<String> list){}
}