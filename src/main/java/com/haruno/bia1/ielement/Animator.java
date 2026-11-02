package com.haruno.bia1.ielement;

import com.haruno.bia1.imath.Anchor;
import com.haruno.bia1.imath.Position;
import com.haruno.bia1.iutil.Rate;
import com.haruno.bia1.iutil.Tick;

import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

public class Animator {
    public static final byte TOP = 0x00;
    public static final byte MIDDLE = 0x01;
    public static final byte BOTTOM = 0x02;

    private final List<Entry> topLayout = new ArrayList<>();
    private final List<Entry> middleLayout = new ArrayList<>();
    private final List<Entry> bottomLayout = new ArrayList<>();
    private long lastDrawTime = System.nanoTime();

    private Graphics2D g;

    private record Entry(Tick tick,long startTime,Runnable onFinish){}

    public void add(Tick tick,byte layout,Runnable onFinish){
        switch(layout){
            case TOP -> topLayout.add(new Entry(tick,System.nanoTime(),onFinish));
            case MIDDLE -> middleLayout.add(new Entry(tick,System.nanoTime(),onFinish));
            case BOTTOM -> bottomLayout.add(new Entry(tick,System.nanoTime(),onFinish));
            default -> throw new IllegalArgumentException("Unknown layout");
        }
    }

    public void drawTick(Graphics2D g) {
        long now = System.nanoTime();

        this.g = g;
        try{
            drawTick(bottomLayout, now);
            drawTick(middleLayout, now);
            drawTick(topLayout, now);
        }finally{
            this.g = null;
        }

        lastDrawTime = now;
    }

    private void drawTick(List<Entry> animations,long now){
        float delta = (now -lastDrawTime)/1e9f;

        for(int index = animations.size() - 1; index >= 0; index--){
            Entry entry = animations.get(index);
            Tick tick = entry.tick;
            float elapsed = (now - entry.startTime)/1e9f;

            if(tick.performed(elapsed,delta)) {
                animations.remove(index);
                if(entry.onFinish != null)
                    entry.onFinish.run();
            }
        }
    }

    public void clear(){
        topLayout.forEach(entry -> {
            if(entry.onFinish == null) return;
            entry.onFinish.run();
        });
        middleLayout.forEach(entry -> {
            if(entry.onFinish == null) return;
            entry.onFinish.run();
        });
        bottomLayout.forEach(entry -> {
            if(entry.onFinish == null) return;
            entry.onFinish.run();
        });

        topLayout.clear();
        middleLayout.clear();
        bottomLayout.clear();
    }

    public Tick flatSlide(Position start, Position end, int width, int height, Anchor relative, Image image, float span, Rate rate) {
        Position origin = Anchor.anchorToPosition(start,width,height,relative, Anchor.TOP_LEFT);
        Position finalOrigin = Position.add(origin, Position.sub(end,start) );

        return (elapsed,delta)->{
            double t = rate.get(elapsed,delta,span);

            Position current = Position.lerp(origin,finalOrigin,t);
            int x = current.x();
            int y = current.y();

            g.drawImage(image,x,y,width,height,null);
            return elapsed/span >= 1.0;
        };
    }

    public Tick flatSlide(Position start, Position end, int width, int height, Image image, float span, Rate rate) {
        return flatSlide(start,end,width,height, Anchor.TOP_LEFT,image,span,rate);
    }

    public Tick sizeChange(Position position, Position startSize, Position endSize, Anchor relative, Image image, float span, Rate rate) {
        return ((elapsed, delta) -> {
            double t =rate.get(elapsed,delta,span);
            Position currentSize = Position.lerp(startSize,endSize,t);
            int currentWidth = currentSize.x();
            int currentHeight = currentSize.y();

            Position origin = Anchor.anchorToPosition(position,currentWidth,currentHeight,relative, Anchor.TOP_LEFT);
            g.drawImage(image,origin.x(),origin.y(),currentWidth,currentHeight,null);
            return elapsed/span >= 1.0;
        });
    }

    public Tick sizeChange(Position position, Position startSize, Position endSize, Image image, float span, Rate rate) {
        return sizeChange(position,startSize,endSize, Anchor.CENTER,image,span,rate);
    }
}