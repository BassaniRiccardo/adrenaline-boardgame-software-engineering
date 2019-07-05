package it.polimi.ingsw.view.guirenderer;

import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.scene.image.ImageView;

/**
 * Contains the graphic animations of the GUI
 *
 * @author davidealde
 */
public class Animations {

    private static final int DURATION = 400;
    private static final int CYCLES_NUMBER = 16;

    /**
     * Makes a specific player flash for some moments
     *
     * @param player the player that has to flash
     */
    public void flash(ImageView player){

        FadeTransition ft = new FadeTransition();
        ft.setNode(player);
        ft.setDuration(new Duration(DURATION));
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setCycleCount(CYCLES_NUMBER);
        ft.setAutoReverse(true);
        ft.play();
    }

}