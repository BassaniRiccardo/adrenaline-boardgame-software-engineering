package it.polimi.ingsw.controller;

import java.util.concurrent.TimeUnit;

public class MatchmakingTimer {
    private boolean over;
    private long start;
    private long duration;
    private boolean running;

    public MatchmakingTimer(int duration){
        this.duration = TimeUnit.NANOSECONDS.convert(duration, TimeUnit.SECONDS);
        this.over = false;
        this.start = 0;
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



}