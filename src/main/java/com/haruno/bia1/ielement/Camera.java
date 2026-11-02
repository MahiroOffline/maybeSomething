package com.haruno.bia1.ielement;

import com.haruno.bia1.imath.Position;

import java.awt.Graphics2D;

public class Camera {
    private Position offset;

    public Camera(){
        this(Position.of(0,0));
    }
    public Camera(Position position){
        this.offset = position;
    }

    public void applyTo(Graphics2D g,int width,int height) {
        g.translate(offset.x(), offset.y());
    }

    public void moveCamera(Position offset){
        this.offset = Position.add(this.offset,offset);
    }

    public void moveCamera(int x,int y){
        moveCamera(Position.of(x,y));
    }

    public void setOffset(Position offset){
        this.offset = offset;
    }
}
