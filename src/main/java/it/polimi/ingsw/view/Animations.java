package it.polimi.ingsw.view;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.FadeTransition;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class Animations {

    public void flash(ImageView player){

        FadeTransition ft = new FadeTransition();
        ft.setNode(player);
        ft.setDuration(new Duration(400));
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setCycleCount(16);
        ft.setAutoReverse(true);
        ft.play();
    }
  /*  public
    TranslateTransition movement = new TranslateTransition();
    translateTransition.setDuration(Duration.millis(1000));




    //Creating Translate Transition
    TranslateTransition translateTransition = new TranslateTransition();

//Setting the duration of the transition
      translateTransition.setDuration(Duration.millis(1000));

              //Setting the node for the transition
              translateTransition.setNode(circle);

              //Setting the value of the transition along the x axis.
              translateTransition.setByX(300);

              //Setting the cycle count for the transition
              translateTransition.setCycleCount(50);

              //Setting auto reverse value to false
              translateTransition.setAutoReverse(false);

              //Playing the animation
              translateTransition.play();

              //Creating a Group object
              Group root = new Group(circle);

              //Creating a scene object
              Scene scene = new Scene(root, 600, 300);

              //Setting title to the Stage
              stage.setTitle("Translate transition example");

              //Adding scene to the stage
              stage.setScene(scene);

              //Displaying the contents of the stage
              stage.show();*/

}