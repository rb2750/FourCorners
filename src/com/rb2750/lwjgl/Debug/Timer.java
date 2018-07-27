package com.rb2750.lwjgl.Debug;

import java.util.HashMap;

public class Timer {
    private String timerName;
    private long startTime;
    private long endTime;
    private boolean subTimer;

    private HashMap<String, Timer> subTimers;

    public Timer(String name) {
        timerName = name;
        startTime = System.nanoTime();
        subTimers = new HashMap<>();
    }

    protected Timer(String name, boolean subTimer) {
        timerName = name;
        startTime = System.nanoTime();
        subTimers = new HashMap<>();
        this.subTimer = subTimer;
    }

    public void addTimer(String name) {
        subTimers.put(name, new Timer(name, true));
    }

    public Timer getTimer(String name) {
        return subTimers.get(name);
    }

    public void stopTimer() {
        if(endTime != 0) return;
        endTime = System.nanoTime();

        for (Timer t: subTimers.values()) {
            t.stopTimer();
        }
    }

    public void timerInfomation() {
        if(!subTimer) System.out.println("======");

        System.out.println(timerName + ": " + (endTime - startTime)/1000 + " ms");

        for (Timer t: subTimers.values()) {
            t.timerInfomation();
        }

        if(subTimer) return;
        else System.out.println("======");
    }
}
