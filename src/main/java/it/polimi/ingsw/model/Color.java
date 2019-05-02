package it.polimi.ingsw.model;

/**
 * The possible colors of the components of the game.
 *
 * @author  BassaniRiccardo
 */

public enum Color {

    RED, YELLOW, BLUE, GREY, PURPLE, GREEN;

    @Override
    public String toString(){
        return name().toLowerCase();
    }

}