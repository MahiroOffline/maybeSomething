package com.haruno.bia2.paint;

import java.awt.Color;
import java.awt.Graphics;

public class Grid implements Painting {
    public static final int DEFAULT_WIDTH = 4;
    public static final int DEFAULT_HEIGHT = 4;
    public static final double DEFAULT_X_LENGTH = 800.0;
    public static final double DEFAULT_Y_LENGTH = 800.0;
    public static final double DEFAULT_X_GAP_LENGTH = 2.0;
    public static final double DEFAULT_Y_GAP_LENGTH = 2.0;
    public static final Color DEFAULT_CELL_COLOR = new Color(0xFFB0AEAE, true);
    public static final Color DEFAULT_INLINE_GAP_COLOR = new Color(0xD8C4C4C4, true);

    private int width;
    private int height;
    private double xLength;
    private double yLength;
    private double xGapLength;
    private double yGapLength;


    @Override
    public void paint(Graphics g)
    {

    }
}
