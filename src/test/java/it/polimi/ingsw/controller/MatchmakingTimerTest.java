package it.polimi.ingsw.controller;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class MatchmakingTimerTest {

    @Test
    public void standardFunctioning() {
        Timer m = new Timer(3);
        assertFalse(m.isOver());
        assertFalse(m.isRunning());
        m.start();
        assertTrue(m.isRunning());
        assertFalse(m.isOver());
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(m.isRunning());
        assertFalse(m.isOver());
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(m.isRunning());
        assertTrue(m.isOver());
    }

    @Test
    public void stop() {
        Timer m = new Timer(3);
        assertFalse(m.isOver());
        assertFalse(m.isRunning());
        m.start();
        assertTrue(m.isRunning());
        assertFalse(m.isOver());
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        m.stop();
        assertFalse(m.isRunning());
        assertFalse(m.isOver());
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertFalse(m.isRunning());
        assertFalse(m.isOver());
    }

    @Test
    public void reset() {
        Timer m = new Timer(3);
        assertFalse(m.isOver());
        assertFalse(m.isRunning());
        m.start();
        assertTrue(m.isRunning());
        assertFalse(m.isOver());
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(m.isRunning());
        assertTrue(m.isOver());
        m.reset();
        assertTrue(m.isRunning());
        assertFalse(m.isOver());
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(m.isRunning());
        assertTrue(m.isOver());
        m.stop();
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertFalse(m.isRunning());
        assertFalse(m.isOver());
    }
}