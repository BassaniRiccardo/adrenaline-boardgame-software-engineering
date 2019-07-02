package it.polimi.ingsw.controller;

import java.util.concurrent.TimeUnit;

/**
 * Simple synchronous timer used for turn length management.
 */

public class Timer {

    private boolean over;
    private long start;
    private long duration;
    private long pausedAt;
    private boolean running;

    /**
     * Standard constructor
     *
     * @param duration      duration of the timer
     */
    Timer(int duration){
        this.duration = TimeUnit.NANOSECONDS.convert(duration, TimeUnit.SECONDS);
        this.over = false;
        this.start = 0;
        this.pausedAt = 0;
        this.running = false;
    }

    /**
     * Starts the timer
     */
    public void start(){
        running = true;
        reset();
    }

    /**
     * Stops the timer
     */
    void stop(){
        running = false;
        reset();
    }

    /**
     * Registers the time at which the timer started or stopped
     */
    public void reset(){
        over = false;
        start = System.nanoTime();
    }

    /**
     * Checks how long has passed since the start
     */
    private void update(){
        if(running&&System.nanoTime()-start>duration){
            over = true;
        } else {  over = false; }
    }

    /**
     * Checks if the timer is still running or not
     *
     * @return      true is the timer is running, else false
     */
    boolean isRunning(){
        update();
        return running;
    }

    /**
     * Checks if the timer ran out
     *
     * @return      true if the timer is over, else false
     */
    boolean isOver(){
        update();
        return over&&running;
    }

    /**
     * Pauses the timer
     */
    void pause(){
        update();
        if(running) {
            pausedAt = System.nanoTime();
            running = false;
        }
    }

    /**
     * Resumes the timer
     */
    void resume(){
        if(!running) {
            start += System.nanoTime() - pausedAt;
            running = true;
            update();
        }
    }


    /**
     * Returns how long is left until the timer is over
     *
     * @return      the amount of time lefts
     */
    long getTimeLeft(){
        return TimeUnit.SECONDS.convert(start + duration - System.nanoTime(), TimeUnit.NANOSECONDS);
    }
}