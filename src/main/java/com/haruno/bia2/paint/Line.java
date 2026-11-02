package com.haruno.bia2.paint;

import com.haruno.bia2.math.Position;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

/**
 *       left-alignment
 * start ----------------> end  middle-alignment
 *       right-alignment
 *  不可变对象
 * */
public class Line implements Painting{
    public static final int LEFT_ALIGNMENT = -1;
    public static final int MIDDLE_ALIGNMENT = 0;
    public static final int RIGHT_ALIGNMENT = 1;
    public static final int DEFAULT_WIDTH = 2;
    public static final Color DEFAULT_COLOR = Color.BLACK;
    public static final int DEFAULT_ALIGNMENT = MIDDLE_ALIGNMENT;

    public final Position start;
    public final Position end;
    public final double width;
    public final Color color;
    private final Path2D.Double path;
    public final int alignment;
//    private final Polygon polygon;

    public Line(Position start,Position end,double width,Color color,int alignment){
        this.start = start;
        this.end = end;
        this.width = width;
        this.color = color;
        this.alignment = alignment;

//        double arc = Math.atan((end.y - start.y) /  (end.x - start.x));
//        double xOffset = Math.sin(arc) * width / 2;
//        double yOffset = Math.cos(arc) * width / 2;

        double dx = end.x - start.x;
        double dy = end.y - start.y;
        double length = Math.hypot(dx,dy);

        double xOffset = -dy / length * width / 2;
        double yOffset = dx / length * width / 2;

        path = new Path2D.Double();

        if(alignment == LEFT_ALIGNMENT){
            path.moveTo(start.x,start.y);
            path.lineTo(start.x - xOffset * 2,start.y - yOffset * 2);
            path.lineTo(end.x - xOffset * 2,end.y - yOffset * 2);
            path.lineTo(end.x,end.y);
        } else if (alignment == MIDDLE_ALIGNMENT) {
            path.moveTo(start.x + xOffset , start.y + yOffset);
            path.lineTo(start.x - xOffset , start.y - yOffset);
            path.lineTo(end.x - xOffset , end.y - yOffset);
            path.lineTo(end.x + xOffset , end.y + yOffset);
        } else if (alignment == RIGHT_ALIGNMENT) {
            path.moveTo(start.x + xOffset * 2,start.y + yOffset * 2);
            path.lineTo(start.x,start.y);
            path.lineTo(end.x,end.y);
            path.lineTo(end.x + xOffset * 2,end.y + yOffset * 2);
        }else{
            throw new IllegalArgumentException("Invalid alignment");
        }
        path.closePath();

//        polygon = new Polygon(new int[]{
//                round(start.x + xOffset),
//                round(start.x - xOffset),
//                round(end.x - xOffset),
//                round(end.x + xOffset)
//        },new int[]{
//                round(start.y + yOffset),
//                round(start.y - yOffset),
//                round(end.y - yOffset),
//                round(end.y + yOffset)
//        },4);
    }
    public Line(Position start,Position end,double width,Color color){
        this(start,end,width,color,DEFAULT_ALIGNMENT);
    }
    public Line(Position start,Position end,double width){
        this(start,end,width,DEFAULT_COLOR);
    }
    public Line(Position start,Position end,Color color){
        this(start,end,DEFAULT_WIDTH,color);
    }
    public Line(Position start,Position end){
        this(start,end,DEFAULT_WIDTH,DEFAULT_COLOR);
    }
    public Line(){
        this(new Position(),new Position(),0,null);
    }

//    private int round(double d){
////        return Math.toIntExact(Math.round(d));
//        return  (int) Math.round(d);
//    }

    @Override
    public void paint(Graphics g) {
        Color oldColor = g.getColor();

        g.setColor(color);
//        g.fillPolygon(polygon);
        ((Graphics2D)g).fill(path);

        g.setColor(oldColor);
    }

    public Line withStart(Position start){
        return new Line(start,end,width,color,alignment);
    }
    public Line withEnd(Position end){
        return new Line(start,end,width,color,alignment);
    }
    public Line withWidth(double width){
        return new Line(start,end,width,color,alignment);
    }
    public Line withColor(Color color){
        return new Line(start,end,width,color,alignment);
    }
    public Line withAlignment(int alignment){
        return new Line(start,end,width,color,alignment);
    }
}
