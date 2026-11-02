package com.haruno.bia2.util;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;

public class Ticker {
    public static final double DEFAULT_FPS = 60.0;

    private final Thread thisThread;

    private final List<TickContent> tasks = new CopyOnWriteArrayList<>();
    private final List<PauseContent> pauseTasks = new CopyOnWriteArrayList<>();
    private static int unnamedCount = 0;

    private record TickContent(String name,long startTime,long offset,int sequence,boolean paused,Tick tick, Tick...onFinish){}
    private record PauseContent(TickContent tickContent,long pauseStartTime){}

    private boolean shouldStop = false;
    private boolean exitOnFree = false;

    private double expectedFps;
    private volatile double actualFps;
    private long startTime;
    private long endTime;

    public Ticker(){
        expectedFps = DEFAULT_FPS;

        thisThread = new Thread(this::loop);
    }

    public Ticker setFromFps(double fps){
        if(fps <= 0)
            throw new IllegalArgumentException("FPS must be greater than zero");

        expectedFps = fps;

        return this;
    }

    public Ticker setFromRefreshSpan(double span){
        if(span <= 0)
            throw new IllegalArgumentException("Refresh span must be greater than zero");

        expectedFps = 1 / span;

        return this;
    }

    private void loop(){
        long lastTime = System.nanoTime();
        long nextFrameTime = System.nanoTime();

        while (!shouldStop) {
            execute();

            long oneFrameTime = (long) (1_000_000_000L / expectedFps);

            nextFrameTime += oneFrameTime;
            long now = System.nanoTime();
            long elapsed = now - lastTime;
            lastTime = now;
            if(elapsed > 0)
                actualFps = 1e9 / elapsed;

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

    private void execute(){
        if(exitOnFree && tasks.isEmpty())
            stop();

        for(TickContent task : tasks){
            if(task.paused())
                continue;

            Tick tick = task.tick();

            long accurateTime = task.startTime + task.offset;
            boolean isEnd = !tick.tick(accurateTime);

            if (isEnd) {
                tasks.remove(task);
                if (task.onFinish.length > 0) {
                    List<Tick> onFinished = new ArrayList<>(List.of(task.onFinish));
                    Tick next = onFinished.removeFirst();
                    addTask(task.name, task.sequence + 1, next, onFinished.toArray(new Tick[0]));
                }
            }
//            if(tick instanceof RunnableTick) {
//                long accurateTime = task.startTime + task.offset;
//                boolean isEnd = !tick.tick(accurateTime);
//
//                if (isEnd) {
//                    tasks.remove(task);
//                    if (task.onFinish.length > 0) {
//                        List<Tick> onFinished = new ArrayList<>(List.of(task.onFinish));
//                        Tick next = onFinished.removeFirst();
//                        addTask(task.name, task.sequence + 1, next, onFinished.toArray(new Tick[0]));
//                    }
//                }
//
//                continue;
//            } else if (tick instanceof TaskTick) {
//
//                continue;
//            }
        }
    }

    public Ticker start(){
        thisThread.start();
        return this;
    }

    synchronized public Ticker pause(String name){
        TickContent task;
        if((task = getTask(name)) == null)
            throw new NoSuchElementException(name + " is not in the task list");
        if(getPause(name) != null)
            throw new IllegalStateException(name + " is already paused");

        setPaused(task,true);

        PauseContent pause = new PauseContent(task,System.nanoTime());
        pauseTasks.add(pause);

        return this;
    }

    synchronized public Ticker unPause(String name){
        PauseContent pause;
        TickContent task;
        if((task = getTask(name)) == null)
            throw new NoSuchElementException(name + " is not in the task list");
        if((pause = getPause(name)) == null)
            throw new IllegalStateException(name + " is not paused");

        task = setOffset(task,task.offset + System.nanoTime() - pause.pauseStartTime);
        setPaused(task, false);
        pauseTasks.remove(pause);

        return this;
    }

    private TickContent setPaused(TickContent task,boolean paused){
        int index = tasks.indexOf(task);
        TickContent newTick = new TickContent(task.name, task.startTime,task.offset,task.sequence,paused,task.tick,task.onFinish);
        tasks.set(index,newTick);
        return newTick;
    }

    private TickContent setOffset(TickContent task,long offset){
        int index = tasks.indexOf(task);
        TickContent newTick = new TickContent(task.name,task.startTime,offset,task.sequence,task.paused,task.tick,task.onFinish);
        tasks.set(index,newTick);
        return newTick;
    }

    private TickContent setSequence(TickContent task,int sequence){
        int index = tasks.indexOf(task);
        TickContent newTick = new TickContent(task.name,task.startTime,task.offset,sequence,task.paused,task.tick,task.onFinish);
        tasks.set(index,newTick);
        return newTick;
    }

    public Ticker stop(){
        shouldStop = true;
        return this;
    }

    public double fps(){
        return this.actualFps;
    }

    private TickContent getTask(String name){
        for(TickContent task : tasks)
            if(task.name.equals(name))
                return task;
        return null;
    }

    private PauseContent getPause(String name){
        for(PauseContent task : pauseTasks)
            if(task.tickContent().name.equals(name))
                return task;
        return null;
    }

    private Ticker addTask(String name,int sequence,Tick tick,Tick...onFinish){
        if(getTask(name) != null)
            throw new IllegalArgumentException("This name has been used!");

        tasks.add(new TickContent(name,System.nanoTime(),0,sequence,false,tick,onFinish));

        return this;
    }

    public Ticker addTask(String name,Tick tick,Tick...onFinish){
        return addTask(name,0,tick,onFinish);
    }

    public Ticker addTask(Runnable task){
        return addTask("Unnamed"+unnamedCount++,(startTime)->{task.run();
            return true;
        });
    }

    public Ticker removeTask(String name){
        TickContent task;

        if((task = getTask(name))!=null) {
            tasks.remove(task);

            if(task.paused)
                pauseTasks.remove(getPause(name));
        }else{
            throw new IllegalArgumentException("This name is not in the task list!");
        }

        return this;
    }

    public Ticker setExitOnFree(boolean exitOnFree){
        this.exitOnFree = exitOnFree;
        return this;
    }
}
