package com.haruno.bia1.iutil;

public class CallRateTracker {
    long lastTime = System.nanoTime();

    public double call(){
        long now = System.nanoTime();
        long elapsed = now - lastTime;
        lastTime = now;
        if(elapsed == 0)
            return Long.MAX_VALUE;
        return 1e9 / elapsed;
    }
}
