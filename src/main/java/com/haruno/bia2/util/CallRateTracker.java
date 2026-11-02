package com.haruno.bia2.util;

public class CallRateTracker {
    public static final double DEFAULT_SMOOTH = 0.9;
    public static final double CLOSE_SMOOTH = 0.0;

    private double smoothRate;
    private double lastRate = 0;

    private long lastTime = System.nanoTime();

    public CallRateTracker(double smoothRate) {
        setSmooth(smoothRate);
    }

    public CallRateTracker(){
        setSmooth();
    }

    public void setSmooth(double smoothRate){
        if(smoothRate < 0 || smoothRate > 1)
            throw new IllegalArgumentException("Smooth rate must be between 0 and 1");

        this.smoothRate = smoothRate;
    }

    public void setSmooth(){
        setSmooth(DEFAULT_SMOOTH);
    }

    public void closeSmooth(){
        this.smoothRate = CLOSE_SMOOTH;
    }

    public double call(){
        long now = System.nanoTime();
        long elapsed = now -lastTime;
        lastTime = now;

        if(elapsed == 0) {
            lastRate = Long.MAX_VALUE;
            return lastRate;
        }

        double rate = 1e9 / elapsed;
        lastRate = rate * (1 - smoothRate) + lastRate * smoothRate;

        return lastRate;
    }
}
