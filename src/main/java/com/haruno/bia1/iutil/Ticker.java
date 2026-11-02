package com.haruno.bia1.iutil;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Ticker{
    private final Thread thread;

    private final List<Runnable> tasks = new CopyOnWriteArrayList<>();

    private volatile double expectedFps;
    private volatile boolean shouldStop;
    private volatile double fps;

    public Ticker(int expectedFps){
        setExpectedFps(expectedFps);
        this.shouldStop = false;
        fps = 0;

        thread = new Thread(this::loop,"ticker");
    }

    public void loop(){
        long lastTime = System.nanoTime();
        long oneFrameTime = (long)(1_000_000_000L / expectedFps);
        long nextFrameTime = System.nanoTime();


        while (!shouldStop) {
            for(Runnable task : tasks)
                try{
                    task.run();
                }catch(RuntimeException e){
                    e.printStackTrace();
                }

            nextFrameTime += oneFrameTime;
            long now = System.nanoTime();
            long elapsed = now - lastTime;
            lastTime = now;
            if(elapsed > 0)
                fps = 1e9 / elapsed;

            long remainTime = nextFrameTime - System.nanoTime();
            if(remainTime > 0 ){
                try {
                    Thread.sleep(remainTime / 1_000_000, (int) (remainTime % 1_000_000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }else if(remainTime < -oneFrameTime)
                nextFrameTime = System.nanoTime();
        }
    }

    public void setExpectedFps(int efps){
        if(efps <= 0 )
            throw new IllegalArgumentException("Ticker:fps cannot be negative or zero");
        this.expectedFps = efps;
    }

    public double fps(){
        return fps;
    }

    public void stop(){
        shouldStop = true;
    }

    public void start(){
        thread.start();
    }

    public void addTask(Runnable task){
        tasks.add(task);
    }
}
