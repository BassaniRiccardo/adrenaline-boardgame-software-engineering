package it.polimi.ingsw.view;

import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.scene.image.ImageView;


class Animations {

    void flash(ImageView player){

        FadeTransition ft = new FadeTransition();
        ft.setNode(player);
        ft.setDuration(new Duration(400));
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setCycleCount(16);
        ft.setAutoReverse(true);
        ft.play();
    }

}