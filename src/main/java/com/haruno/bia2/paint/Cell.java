package com.haruno.bia2.paint;

import com.haruno.bia2.math.Position;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

public class Cell implements Painting{
    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    public static final int BACKGROUND = 4;
    public static final int OUTLINE = 5;

    private Line up;
    private Line down;
    private Line left;
    private Line right;

    public Position origin;
    public double xLength;
    public double yLength;
    public double xOutlineLength;
    public double yOutLineLength;
    private double xInlineLength;
    private double yInlineLength;
    public Color backGroundColor;
    public Color outlineColor;

//    public Cell(Position start, double )
    public Cell(Position origin, double xLength, double yLength, double xOutlineLength, double yOutLineLength, Color outlineColor, Color backGroundColor){
        this.origin = origin;

        xInlineLength = xLength - xOutlineLength;
        yInlineLength = yLength - yOutLineLength;
        this.backGroundColor = backGroundColor;
        this.outlineColor = outlineColor;

        Position topLeft = origin;
        Position topRight = Position.add(topLeft,new Position(xLength,0));
        Position bottomLeft = Position.add(topLeft,new Position(0,yLength));
        Position bottomRight = Position.add(topLeft,new Position(xLength,yLength));

        up = new Line(topLeft,topRight,xOutlineLength,outlineColor,Line.LEFT_ALIGNMENT);
        down = new Line(bottomLeft,bottomRight,xOutlineLength,outlineColor,Line.RIGHT_ALIGNMENT);
        left = new Line(topLeft,bottomLeft,xOutlineLength,outlineColor,Line.LEFT_ALIGNMENT);
        right = new Line(topRight,bottomRight,xOutlineLength,outlineColor,Line.RIGHT_ALIGNMENT);
    }

    @Override
    public void paint(Graphics g) {
//        g.drawRect(origin.x, origin.y, xLength, yLength);
        drawRect(g,origin,xLength,yLength);
        up.paint(g);
        down.paint(g);
        left.paint(g);
        right.paint(g);
    }

    private void drawRect(Graphics g,Position origin,double xLength,double yLength){
        Path2D.Double rect = new Path2D.Double();

        rect.moveTo(origin.x,origin.y);
        rect.lineTo(origin.x + xLength,origin.y);
        rect.lineTo(origin.x + xLength,origin.y + yLength);
        rect.lineTo(origin.x,origin.y+yLength);
        rect.closePath();

        Color oldColor = g.getColor();
        g.setColor(backGroundColor);
        ((Graphics2D)g).fill(rect);
        g.setColor(oldColor);
    }

    public void setColor(int flag,Color color){
        switch (flag){
            case UP -> up = up.withColor(color);
            case DOWN -> down =  down.withColor(color);
            case LEFT -> left = left.withColor(color);
            case RIGHT -> right = right.withColor(color);
            case BACKGROUND -> backGroundColor = color;
            case OUTLINE -> {
                up = up.withColor(color);
                down =  down.withColor(color);
                left = left.withColor(color);
                right = right.withColor(color);
            }
            default -> throw new IllegalArgumentException("Invalid flag");
        }
    }
}
