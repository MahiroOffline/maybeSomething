package com.haruno.bia1.imath;

import java.util.Objects;

import static java.lang.Math.round;

public class Anchor {
    public static final Anchor CENTER = of(0.5f,0.5f);
    public static final Anchor TOP_LEFT = of(0f,0f);
    public static final Anchor TOP_RIGHT = of(1f,0f);
    public static final Anchor BOTTOM_LEFT = of(0f,1f);
    public static final Anchor BOTTOM_RIGHT = of(1f,1f);
    public static final Anchor TOP_CENTER = of(0.5f,0f);
    public static final Anchor BOTTOM_CENTER = of(0.5f,1f);
    public static final Anchor CENTER_LEFT = of(0f,0.5f);
    public static final Anchor CENTER_RIGHT = of(1f,0.5f);

    private final float x;
    private final float y;

    private Anchor(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public static Anchor of(float x, float y){
        if (x < 0f || x > 1f || y < 0f || y > 1f)
            throw new IllegalArgumentException("Anchor out of range: (" + x + ", " + y + "),must be between 0.0 and 1.0");
        return new Anchor(x,y);
    }

    public float x(){return x;}
    public float y(){return y;}

    public static Position anchorToPosition(Position point, int width, int height, Anchor local, Anchor target){
        if (width < 0 || height < 0)
            throw new IllegalArgumentException("size must be positive");

        float xRelative = target.x() - local.x();
        float yRelative = target.y() - local.y();
        return Position.of(round(point.x() + width * xRelative),round(point.y() + height * yRelative));
    }

    public static Position anchorToPosition(Position point, int width, int height, Anchor target){
        return anchorToPosition(point,width,height, Anchor.TOP_LEFT,target);
    }

    public static Anchor positionToAnchor(Position local, Position target, int width, int height, Anchor point){
        if (width <= 0 || height <= 0)
            throw new IllegalArgumentException("size must be positive");

        float xRelative = (float) (target.x() - local.x()) /width;
        float yRelative = (float) (target.y() - local.y()) /height;

        return Anchor.of(round(point.x() + xRelative),round(point.y() + yRelative));
    }

    public static Anchor positionToAnchor(Position local, Position target, int width, int height){
        return positionToAnchor(local,target,width,height,TOP_LEFT);
    }

    public boolean isNormalized() {
        return x >= 0f && x <= 1f && y >= 0f && y <= 1f;
    }

    public Anchor clamp() {
        return of(Math.clamp(x, 0f, 1f), Math.clamp(y, 0f, 1f));
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Anchor)
            return  x == ((Anchor)obj).x && y == ((Anchor)obj).y;
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
