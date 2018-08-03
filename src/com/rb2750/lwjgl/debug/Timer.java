package com.rb2750.lwjgl.debug;

import java.util.HashMap;

public class Timer {
    private String timerName;
    private long startTime;
    private long pauseTime;
    private long endTime;
    private boolean subTimer;
    private boolean paused;

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

    public void startTimer()
    {
        endTime = 0;
        startTime = System.nanoTime();
    }

    public void pauseTimer()
    {
        pauseTime = System.nanoTime();
        paused = true;
    }

    public void resumeTimer()
    {
        if (!paused)
            return;

        startTime += System.nanoTime() - pauseTime;
        pauseTime = 0;
        paused = false;
    }

    public void stopTimer() {
        if(endTime != 0) return;

        if (paused)
            resumeTimer();

        endTime = System.nanoTime();

        for (Timer t: subTimers.values()) {
            t.stopTimer();
        }
    }

    public void startSubTimer(String name)
    {
        Timer subTimer = subTimers.get(name);

        if (subTimer != null)
            subTimer.startTimer();
        else
            System.err.println("Sub timer " + name + " does not exist in " + timerName + " timer.");
    }

    public void pauseSubTimer(String name)
    {
        Timer subTimer = subTimers.get(name);

        if (subTimer != null)
            subTimer.pauseTimer();
        else
            System.err.println("Sub timer " + name + " does not exist in " + timerName + " timer.");
    }

    public void resumeSubTimer(String name)
    {
        Timer subTimer = subTimers.get(name);

        if (subTimer != null)
            subTimer.resumeTimer();
        else
            System.err.println("Sub timer " + name + " does not exist in " + timerName + " timer.");
    }

    public void stopSubTimer(String name)
    {
        Timer subTimer = subTimers.get(name);

        if (subTimer != null)
            subTimer.stopTimer();
        else
            System.err.println("Sub timer " + name + " does not exist in " + timerName + " timer.");
    }

    public void timerInfomation() {
        if(!subTimer) System.out.println("======");

        System.out.println((subTimer ? "    " : "") + timerName + ": " + (float)(endTime - startTime)/10000000.0f + " ms");

        for (Timer t: subTimers.values()) {
            t.timerInfomation();
        }

        if(subTimer) return;
        else System.out.println("======");
    }
}
