package com.haruno.maybeSomething.util;

public class Vec2i {
    private int x;
    private int y;

    public Vec2i() {
        this(0, 0);
    }
    public Vec2i(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public int getX() {return x;}
    public int getY() {return y;}
    public void setX(int x) {this.x = x;}
    public void setY(int y) {this.y = y;}

    public Vec2i copyOf(){return new Vec2i(x,y);}
    public void add(Vec2i v){
        x += v.x;
        y += v.y;
    }
    public void sub(Vec2i v){
        x -= v.x;
        y -= v.y;
    }
    public int distance(Vec2i v){
        int distanceX = Math.abs(x - v.x);
        int distanceY = Math.abs(y - v.y);
        return distanceX + distanceY;
    }

    public static Vec2i add(Vec2i v1, Vec2i v2){
        return new Vec2i(v1.x + v2.x, v1.y + v2.y);
    }
    public static Vec2i sub(Vec2i v1, Vec2i v2){
        return new Vec2i(v1.x - v2.x, v1.y - v2.y);
    }
    public static int distance(Vec2i v1, Vec2i v2){
        int distanceX = Math.abs(v1.x - v2.x);
        int distanceY = Math.abs(v1.y - v2.y);
        return distanceX + distanceY;
    }

    @Override
    public String toString(){
        return "x: " + x + ", y: " + y;
    }
}
