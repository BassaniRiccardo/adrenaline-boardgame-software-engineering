package it.polimi.ingsw.controller;

import java.util.concurrent.TimeUnit;

/**
 * Simple synchronous timer used for turn length management
 */

public class Timer {
    private boolean over;
    private long start;
    private long duration;
    private long pausedAt;
    private boolean running;

    public Timer(int duration){
        this.duration = TimeUnit.NANOSECONDS.convert(duration, TimeUnit.SECONDS);
        this.over = false;
        this.start = 0;
        this.pausedAt = 0;
        this.running = false;
    }

    public void start(){
        running = true;
        reset();
    }

    public void stop(){
        running = false;
        reset();
    }

    public void reset(){
        over = false;
        start = System.nanoTime();
    }

    private void update(){
        if(running&&System.nanoTime()-start>duration){
            over = true;
        } else {  over = false; }
    }

    public boolean isRunning(){
        update();
        return running;
    }

    public boolean isOver(){
        update();
        return over&&running;
    }

    public void pause(){
        update();
        if(running) {
            pausedAt = System.nanoTime();
            running = false;
        }
    }

    public void resume(){
        if(!running) {
            start += System.nanoTime() - pausedAt;
            running = true;
            update();
        }
    }

    public long getTimeLeft(){
        return TimeUnit.SECONDS.convert(start + duration - System.nanoTime(), TimeUnit.NANOSECONDS);
    }
}