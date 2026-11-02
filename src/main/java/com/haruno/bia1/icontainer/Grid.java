package com.haruno.bia1.icontainer;

import com.haruno.bia1.ielement.Camera;
import com.haruno.bia1.imath.Position;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class Grid extends JPanel {
    private static final Color DEFAULT_EMPTY_CELL_COLOR = new Color(0xFFB0AEAE);
    private static final Color DEFAULT_BACKGROUND_COLOR = new Color(0xFFC4C4C4);

    private static final int DEFAULT_WIDTH = 4;
    private static final int DEFAULT_HEIGHT = 4;
    private static final int DEFAULT_SCALE = 8;
    private static final int DEFAULT_CELL_LENGTH = 20;
    private static final int DEFAULT_GAP_LENGTH = 1;
    private static final int DEFAULT_GRID_LENGTH = DEFAULT_WIDTH * DEFAULT_HEIGHT;

    private static final int OUT_OF_GRID = -1;
    private static final int IN_GAP = -2;


    private Color emptyCellColor;
    private Color backGroundColor;

    private int width;
    private int height;
    private int scale;
    private int gridLength;
    private int cellLength;
    private int gapLength;
    private Consumer<Graphics2D> overlay;

    private boolean[] visible;

    public Camera camera;

    private Position start;
    private Position end;

    public Grid(int width,int height,int scale,boolean[] vision){
        super();

        this.width = width;
        this.height = height;
        this.gridLength = width * height;
        this.scale = scale;
        this.cellLength = DEFAULT_CELL_LENGTH * DEFAULT_SCALE;
        this.gapLength = DEFAULT_GAP_LENGTH * DEFAULT_SCALE;
        this.emptyCellColor = DEFAULT_EMPTY_CELL_COLOR;
        this.backGroundColor = DEFAULT_BACKGROUND_COLOR;
        this.visible = new boolean[gridLength];
        if(vision == null)
            IntStream.range(0, gridLength).forEach(i -> this.visible[i] = true);
        else
            IntStream.range(0, gridLength).forEach(i -> this.visible[i] = vision[i]);
        this.camera = new Camera();

        addListener();
        setBackground(DEFAULT_BACKGROUND_COLOR);
        setVisible(true);
    }

//漂亮但是复杂冗余了
//    public Grid(int width,int height) {
//        this(width,height,DEFAULT_SCALE,new BiFunction<Integer, Integer, boolean[]>() {
//            @Override
//            public boolean[] apply(Integer width, Integer height) {
//                boolean[] result = new boolean[width * height];
//                IntStream.range(0, width * height).forEach(i -> result[i] = true);
//
//                return result;
//            }
//        }.apply(width,height));
//    }

    public Grid(int width,int height) {
        this(width,height,DEFAULT_SCALE,null);
    }

    public Grid() {
        this(DEFAULT_WIDTH,DEFAULT_HEIGHT);
    }

    private void addListener()
    {
//        this.addComponentListener(new ComponentAdapter() {
//            @Override
//            public void componentResized(ComponentEvent e) {
//                super.componentResized(e);
//
//                updateFrameData();
//            }
//        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        camera.applyTo(g2d,getWidth(),getHeight());

        drawBackFrame(g2d);

        if(overlay != null)
            overlay.accept(g2d);    
    }

    private void drawBackFrame(Graphics2D g) {
        for(int i = 0;i < gridLength;i++)
            drawCell(g,i);
    }

//    public void updateFrameData() {
//        int canvasWidth = getWidth();
//        int canvasHeight = getHeight();
//
//        int gridWidth = width * cellLength + (width - 1) * gapLength;
//        int gridHeight = height * cellLength + (height - 1) * gapLength;
//
//        start = Position.of((canvasWidth - gridWidth) / 2, (canvasHeight - gridHeight) / 2);
//        end = Position.of(start.x() + gridWidth, start.y() + gridHeight);
//    }

    public void initCamera(){
        int canvasWidth = getWidth();
        int canvasHeight = getHeight();

        int gridWidth = width * cellLength + (width - 1) * gapLength;
        int gridHeight = height * cellLength + (height - 1) * gapLength;

        start = Position.of((canvasWidth - gridWidth) / 2, (canvasHeight - gridHeight) / 2);
        end = Position.of(start.x() + gridWidth, start.y() + gridHeight);
        camera.setOffset(start);
//        camera.setPosition(0,0);
//        camera.setPosition(100,100);
    }

    private void updateArray(){
        visible = new boolean[gridLength];
        IntStream.range(0, gridLength).forEach(i -> this.visible[i] = true);
    }

    private void drawCell(Graphics2D g, int index) {
        Position point = indexOf(index);
        int x = point.x();
        int y = point.y();
        g.setColor(visible[index] ? emptyCellColor : backGroundColor);
        g.fillRect(x, y, cellLength, cellLength);
    }

    public Position indexOf(int index) {
        int offset = cellLength + gapLength;

        int x = index % width;
        int y = index / width;

//        return Position.add(start, Position.of(offset * x, offset * y));
        return Position.of(offset * x,offset * y);
    }

    //TODO 实现有问题
    public int positionOf(Position v) {
        int offsetX = v.x();
        int offsetY = v.y();

        if (offsetX < start.x() || offsetY < start.y() || offsetX > end.x() || offsetY > end.y())
            return OUT_OF_GRID;

        offsetX -= start.x();
        offsetY -= start.y();

        if (offsetX % (cellLength + gapLength) > cellLength || offsetY % (cellLength + gapLength) > cellLength)
            return IN_GAP;

        int x = offsetX / (cellLength + gapLength);
        int y = offsetY / (cellLength + gapLength);
        return x + y * width;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backGroundColor = backgroundColor;
        repaint();
    }

    public void setEmptyCellColor(Color emptyCellColor) {
        this.emptyCellColor = emptyCellColor;
        repaint();
    }

    public void setScale(int scale) {
        this.scale = scale;
        cellLength = scale * DEFAULT_CELL_LENGTH;
        gapLength = scale * DEFAULT_GAP_LENGTH;
//        updateFrameData();
        repaint();
    }

    public void setGridWidth(int width) {
        this.width = width;
        this.gridLength = width * height;
        updateArray();
//        updateFrameData();
        repaint();
    }

    public void setGridHeight(int height) {
        this.height = height;
        this.gridLength = width * height;
        updateArray();
//        updateFrameData();
        repaint();
    }

    public void setVisible(boolean[] vision) {
        System.arraycopy(vision, 0, this.visible, 0, vision.length);
        repaint();
    }

    public void setVisible(int index,boolean visible) {
        this.visible[index] = visible;
        repaint();
    }

    public void setVisible(int x,int y,boolean visible) {
        this.visible[x + y * width] = visible;
        repaint();
    }

    public void setOverlay(Consumer<Graphics2D> overlay) {
        this.overlay = overlay;
    }

    public int getCellLength() {
        return cellLength;
    }

    public int getGridWidth(){
        return width;
    }

    public int getGridHeight(){
        return height;
    }

    public boolean reverse(int index) {
        return !(visible[index] = !visible[index]);
    }
}
