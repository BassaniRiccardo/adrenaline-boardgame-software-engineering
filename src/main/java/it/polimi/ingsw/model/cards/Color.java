package it.polimi.ingsw.model.cards;

/**
 * The possible colors of the components of the game.
 *
 * @author  BassaniRiccardo
 */

public enum Color {

    RED, YELLOW, BLUE, GREY, PURPLE, GREEN;

    /**
     * Returns a string representing a color.
     *
     * @return      the description of the color.
     */
    @Override
    public String toString(){

        return (this.name().substring(0,1) + this.name().toLowerCase().substring(1));

    }

    /**
     * Returns a lowercase string representing a color.
     *
     * @return      the lowercase description of the color.
     */
    public String toStringLowerCase(){

        return  this.name().toLowerCase();

    }



}