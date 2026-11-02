package com.haruno.bia2.math;

import java.util.Objects;

public class Anchor {
    public static final Anchor CENTER = new Anchor(0.5,0.5);
    public static final Anchor TOP_LEFT = new Anchor(0,0);
    public static final Anchor TOP_RIGHT = new Anchor(1.0,0);
    public static final Anchor BOTTOM_LEFT = new Anchor(0,1.0);
    public static final Anchor BOTTOM_RIGHT = new Anchor(1.0,1.0);
    public static final Anchor TOP_CENTER = new Anchor(0.5,0);
    public static final Anchor BOTTOM_CENTER = new Anchor(0.5,1.0);
    public static final Anchor CENTER_LEFT = new Anchor(0,0.5);
    public static final Anchor CENTER_RIGHT = new Anchor(1.0,0.5);

    public final double x;
    public final double y;
    public final double z;

    public Anchor(double x,double y,double z){
        this.x=x;
        this.y=y;
        this.z=z;
    }

    public Anchor(double x,double y){
        this(x,y,0.0);
    }

    public Anchor(double x){
        this(x,0.0,0.0);
    }

    public Anchor(){
        this(0.0,0.0,0.0);
    }

    public Anchor clamp(){
        return new Anchor(Math.clamp(x,0.0,1.0),Math.clamp(y,0.0,1.0),Math.clamp(z,0.0,1.0));
    }

    public boolean isNormalized(){
        return x >= 0 && x <= 1 && y >= 0 && y <= 1 && z >= 0 && z <= 1;
    }

    public static Anchor add(Anchor a,Anchor b){
        return new Anchor(a.x+b.x,a.y+b.y,a.z+b.z);
    }

    public static Anchor sub(Anchor a,Anchor b){
        return new Anchor(a.x-b.x,a.y-b.y,a.z-b.z);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Anchor a)
            return  x == a.x && y == a.y;
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
