package it.polimi.ingsw.network.server;

import org.junit.Test;

import static org.junit.Assert.*;

public class ChooseOptionsTypeTest {

    @Test
    public void chooseOptionsTypeTestToString() {
        assertEquals("string", VirtualView.ChooseOptionsType.CHOOSE_STRING.toString());
        assertEquals("powerup", VirtualView.ChooseOptionsType.CHOOSE_POWERUP.toString());
        assertEquals("weapon", VirtualView.ChooseOptionsType.CHOOSE_WEAPON.toString());
        assertEquals("player", VirtualView.ChooseOptionsType.CHOOSE_PLAYER.toString());
        assertEquals("square", VirtualView.ChooseOptionsType.CHOOSE_SQUARE.toString());

    }

}