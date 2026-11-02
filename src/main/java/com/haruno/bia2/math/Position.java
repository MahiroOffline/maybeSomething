package com.haruno.bia2.math;

public class Position {
    public final double x;
    public final double y;
    public final double z;

    public Position(double x,double y,double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Position(double x,double y){
        this(x,y,0);
    }

    public Position(double x){
        this(x,0,0);
    }

    public Position(){
        this(0,0,0);
    }

    public static Position opposite(Position p){
        return new Position(-p.x,-p.y,-p.z);
    }

    public static Position add(Position a, Position b){
        return new Position(a.x + b.x, a.y + b.y, a.z + b.z);
    }

    public static Position sub(Position a,Position b){
        return add(a,opposite(b));
    }

    public static Position scale(Position p,double scale){
        return new Position(p.x * scale,p.y * scale,p.z * scale);
    }

    public static Position lerp(Position a,Position b,double t){
        double lerpX = a.x + (b.x - a.x) * t;
        double lerpY = a.y + (b.y - a.y) * t;
        double lerpZ = a.z + (b.z - a.z) * t;
        return new Position(lerpX,lerpY,lerpZ);
    }

    public static Position linear(Position a,Position b,double scaleA,double scaleB){
        return add(scale(a,scaleA),scale(b,scaleB));
    }

    public static double manhattanDistance(Position a,Position b){
        double distanceX = Math.abs(a.x - b.x);
        double distanceY = Math.abs(a.y - b.y);
        double distanceZ = Math.abs(a.z - b.z);
        return distanceX + distanceY + distanceZ;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Position p)
            return p.x == x && p.y == y && p.z == z;
        return false;
    }

    public Position copyOf(){
        return new Position(x,y,z);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString(){
        return "(" + x + "," + y + "," + z + ")";
    }

    public Position toInt(){
        return new Position((int)x,(int)y,(int)z);
    }

    public Position round(){
        return new Position(Math.round(x),Math.round(y),Math.round(z));
    }
}
