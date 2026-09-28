package com.haruno.two048;

import com.haruno.maybeSomething.util.Vec2i;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Set;

public class Two048 {
    private static final Color EMPTY_CELL_COLOR = new Color(0xFFB0AEAE, true);
    private static final Color BACKGROUND_COLOR = new Color(0xD8C4C4C4, true);
    private static final Color HERO_COLOR = new Color(0xD84D4D4D, true);

    private static final int DEFAULT_WIDTH = 4;
    private static final int DEFAULT_HEIGHT = 4;
    private static final int DEFAULT_FRAME_WIDTH = 800;
    private static final int DEFAULT_FRAME_HEIGHT = 800;
    private static final int DEFAULT_CELL_LENGTH = 160;
    private static final int DEFAULT_GAP_LENGTH = 4;

    private static final int OUT_OF_GRID = -1;
    private static final int IN_GAP = -2;

    private static final float SMOOTH_FPS = 0.9f;
    private static final int PURPOSE_FPS = 60;
    private static final double PURPOSE_FPS_UPDATE_TIME = 1f;
    private static final double SECOND2NANOSECOND = 1e9;
    private static final double SECOND2MILLSECOND = 1e3;

    private static final Set<Integer> UP_KEY = Set.of(KeyEvent.VK_W,KeyEvent.VK_UP);
    private static final Set<Integer> DOWN_KEY = Set.of(KeyEvent.VK_S,KeyEvent.VK_DOWN);
    private static final Set<Integer> RIGHT_KEY = Set.of(KeyEvent.VK_D,KeyEvent.VK_RIGHT);
    private static final Set<Integer> LEFT_KEY = Set.of(KeyEvent.VK_A,KeyEvent.VK_LEFT);
    private static final Set<Integer> REWIND_KEY = Set.of(KeyEvent.VK_Z,KeyEvent.VK_C);
    private static final Set<Integer> RESTART_KEY = Set.of(KeyEvent.VK_R);

    private static final String UP_MOTION = "up";
    private static final String DOWN_MOTION = "down";
    private static final String RIGHT_MOTION = "right";
    private static final String LEFT_MOTION = "left";
    private static final String REWIND_MOTION = "rewind";
    private static final String RESTART_MOTION = "restart";
    private static final String NULL_MOTION = "null";

    private static final BufferedImage[] NUM_CELLS = new BufferedImage[11];

    private static final JFrame frame = new JFrame("2048");
    private static final Canvas canvas = new Canvas();

    public static void runProgram() {
        init();
        loop();
        end();
    }

    private static void init() {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = dimension.width;
        int screenHeight = dimension.height;

        frame.add(canvas);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(DEFAULT_FRAME_WIDTH, DEFAULT_FRAME_HEIGHT);
        frame.setLocation(screenWidth / 2 - frame.getWidth() / 2, screenHeight / 2 - frame.getHeight() / 2);

        frame.setVisible(true);

        canvas.requestFocusInWindow();
    }

    private static void loop() {
        long now = System.nanoTime();                      //-ns
        long lastTime = now;                                //-ns
        double delta = 0;                                   //-s
        double titleRefreshTime = 0;                        //-s
        double refreshTime = 0;                             //-s
        double purposeRefreshTime = (double) 1 / PURPOSE_FPS; //-s
        int fps = 0;                                        //-n

        while (true) {
//         Toolkit.getDefaultToolkit().sync();

            lastTime = now;
            now = System.nanoTime();
            delta = (now-lastTime)/SECOND2NANOSECOND;
            titleRefreshTime += delta;
            refreshTime += delta;

            if(titleRefreshTime >= PURPOSE_FPS_UPDATE_TIME)
            {
                frame.setTitle(String.format("%.2f fps", (double) fps / titleRefreshTime));
                titleRefreshTime -= PURPOSE_FPS_UPDATE_TIME;
                fps = 0;
            }
            if(refreshTime >= purposeRefreshTime)
            {
                canvas.repaint();
                fps++;
                refreshTime -= purposeRefreshTime;
            }
            if(refreshTime < purposeRefreshTime)
            {
                long sleepTime = (long) ((purposeRefreshTime - refreshTime) * SECOND2MILLSECOND);
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static void end() {
        frame.dispose();
    }

    private static void keyBinds(){
        InputMap inputMap = canvas.getInputMap();
        ActionMap actionMap = canvas.getActionMap();

        bindKey(inputMap,actionMap,UP_MOTION,UP_KEY.stream().findFirst().get(), UP_KEY.stream().mapToInt(Integer::intValue).toArray());
        bindKey(inputMap,actionMap,LEFT_MOTION,LEFT_KEY.stream().findFirst().get(), LEFT_KEY.stream().mapToInt(Integer::intValue).toArray());
        bindKey(inputMap,actionMap,DOWN_MOTION,DOWN_KEY.stream().findFirst().get(), DOWN_KEY.stream().mapToInt(Integer::intValue).toArray());
        bindKey(inputMap,actionMap,RIGHT_MOTION,RIGHT_KEY.stream().findFirst().get(), RIGHT_KEY.stream().mapToInt(Integer::intValue).toArray());
        bindKey(inputMap,actionMap,REWIND_MOTION,REWIND_KEY.stream().findFirst().get(),REWIND_KEY.stream().mapToInt(Integer::intValue).toArray());
        bindKey(inputMap,actionMap,RESTART_MOTION,RESTART_KEY.stream().findFirst().get(),RESTART_KEY.stream().mapToInt(Integer::intValue).toArray());
    }

    private static void bindKey(InputMap inputMap,ActionMap actionMap ,String motion,int actualKeyCode,int...keyCodes) {
        for(int keyCode : keyCodes)
            inputMap.put(KeyStroke.getKeyStroke(keyCode,0),motion);

        actionMap.put(motion,new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                canvas.heroMove(actualKeyCode);
                canvas.motionAccepted(Canvas.getMotion(actualKeyCode));
                canvas.repaint();
            }
        });
    }

    private static void initImage() throws IOException {
        NUM_CELLS[0] = ImageIO.read(Two048.class.getResourceAsStream("/two048/pic_2.png"));
        NUM_CELLS[1] = ImageIO.read(Two048.class.getResourceAsStream("/two048/pic_4.png"));
        NUM_CELLS[2] = ImageIO.read(Two048.class.getResourceAsStream("/two048/pic_8.png"));
        NUM_CELLS[3] = ImageIO.read(Two048.class.getResourceAsStream("/two048/pic_16.png"));
        NUM_CELLS[4] = ImageIO.read(Two048.class.getResourceAsStream("/two048/pic_32.png"));
        NUM_CELLS[5] = ImageIO.read(Two048.class.getResourceAsStream("/two048/pic_64.png"));
        NUM_CELLS[6] = ImageIO.read(Two048.class.getResourceAsStream("/two048/pic_128.png"));
        NUM_CELLS[7] = ImageIO.read(Two048.class.getResourceAsStream("/two048/pic_256.png"));
        NUM_CELLS[8] = ImageIO.read(Two048.class.getResourceAsStream("/two048/pic_512.png"));
        NUM_CELLS[9] = ImageIO.read(Two048.class.getResourceAsStream("/two048/pic_1024.png"));
        NUM_CELLS[10] = ImageIO.read(Two048.class.getResourceAsStream("/two048/pic_2048.png"));
    }

    static {
        keyBinds();
        try {
            initImage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class Canvas extends JPanel {
        private static final int WIDTH = DEFAULT_WIDTH;
        private static final int HEIGHT = DEFAULT_HEIGHT;
        private static final int EMPTY_CELL = 0;
        private static final int WALL_CELL = -1;
        private static final int MOVEABLE_CELL = 1;
        private static final int CELL_LENGTH = DEFAULT_CELL_LENGTH;
        private static final int GAP_LENGTH = DEFAULT_GAP_LENGTH;
        private static final int GRID_LENGTH = WIDTH * HEIGHT;

        private static final int CELL_2_WEIGHT = 3;
        private static final int CELL_4_WEIGHT = 2;

        private final List<Cell[]> historyRecord = new ArrayList<>();

        private Vec2i start;
        private Vec2i end;
        private final Cell[] cells;

        Canvas() {
            super();
            cells = new Cell[GRID_LENGTH];
            for(int i=0;i<GRID_LENGTH;i++)
                cells[i] = new Cell(i);

            this.setBackground(BACKGROUND_COLOR);

            generateNewCell();
        }

        private static String getMotion(int key) {
            int sKey = Character.toUpperCase(key);

            if(UP_KEY.contains(sKey))
                return UP_MOTION;
            if(DOWN_KEY.contains(sKey))
                return DOWN_MOTION;
            if(RIGHT_KEY.contains(sKey))
                return RIGHT_MOTION;
            if(LEFT_KEY.contains(sKey))
                return LEFT_MOTION;
            if(REWIND_KEY.contains(sKey))
                return REWIND_MOTION;
            if(RESTART_KEY.contains(sKey))
                return RESTART_MOTION;
            return NULL_MOTION;
        }

        private void motionAccepted(String motion) {
            if(motion.equals(UP_MOTION)||motion.equals(DOWN_MOTION)||motion.equals(RIGHT_MOTION)||motion.equals(LEFT_MOTION)) {
                cellMove(motion);
                System.out.println("Motion Called");
            } else if(motion.equals(REWIND_MOTION)) {
                rewind();
                System.out.println("Rewind Called");
            } else if(motion.equals(RESTART_MOTION)) {
                restart();
                System.out.println("Restart Called");
            }
        }

        private void cellMove(String motion) {
            boolean isMoved = false;

            switch (motion) {
                case UP_MOTION -> {
                    for (int i = 0; i < WIDTH; i++) {
                        Cell[] currentCells = new Cell[HEIGHT];
                        for (int j = 0; j < HEIGHT; j++)
                            currentCells[j] = cells[j * WIDTH + i];
                        isMoved = handleList(HEIGHT, currentCells) || isMoved;
                        for (int j = 0; j < HEIGHT; j++)
                            cells[j * WIDTH + i] = currentCells[j];
                    }
                }
                case DOWN_MOTION -> {
                    for (int i = 0; i < WIDTH; i++) {
                        Cell[] currentCells = new Cell[HEIGHT];
                        for (int j = 0; j < HEIGHT; j++)
                            currentCells[HEIGHT - 1 - j] = cells[j * WIDTH + i];
                        isMoved = handleList(HEIGHT, currentCells) || isMoved;
                        for (int j = 0; j < HEIGHT; j++)
                            cells[j * WIDTH + i] = currentCells[HEIGHT - 1 - j];
                    }
                }
                case RIGHT_MOTION -> {
                    for (int i = 0; i < HEIGHT; i++) {
                        Cell[] currentCells = new Cell[WIDTH];
                        for (int j = 0; j < WIDTH; j++)
                            currentCells[WIDTH - 1 - j] = cells[i * HEIGHT + j];
                        isMoved = handleList(WIDTH, currentCells) || isMoved;
                        for (int j = 0; j < WIDTH; j++)
                            cells[i * HEIGHT + j] = currentCells[WIDTH - 1 - j];
                    }
                }
                case LEFT_MOTION -> {
                    for (int i = 0; i < HEIGHT; i++) {
                        Cell[] currentCells = new Cell[WIDTH];
                        for (int j = 0; j < WIDTH; j++)
                            currentCells[j] = cells[i * HEIGHT + j];
                        isMoved = handleList(WIDTH, currentCells) || isMoved;
                        for (int j = 0; j < WIDTH; j++)
                            cells[i * WIDTH + j] = currentCells[j];
                    }
                }
                default -> {
                    return;
                }
            }

            if(isMoved)
                generateNewCell();
        }

        private void rewind() {
            removeRecord();
        }

        private void restart(){
            for(int i=0;i<GRID_LENGTH;i++)
                cells[i].type = EMPTY_CELL;
            generateNewCell();
        }

        private static boolean handleList(int length,Cell...cells){
            boolean isMoved = false;

            for(int i=0;i<length-1;i++){
                isMoved = removeEmptyCellForward(length, cells) || isMoved;

                if(cells[i].type == EMPTY_CELL)
                    break;
                if(cells[i].type == MOVEABLE_CELL && cells[i+1].type == MOVEABLE_CELL && cells[i].value == cells[i+1].value){
                    isMoved = true;
                    cells[i].value++;
                    moveForward(i+1, cells);
                }
            }

            return isMoved;
        }

        private static boolean removeEmptyCellForward(int length,Cell[] cells) {
            for(int i=0;i<length-1;i++)
                if(cells[i+1].type != EMPTY_CELL && cells[i].type == EMPTY_CELL) {
                    for(int t=0;t<length-1;t++)
                        for(int j =length-t-1;j>0;j--){
                            if (cells[t].type == EMPTY_CELL) {
                                moveForward(t, cells);
                            } else break;
                        }

                    return true;
                }

            return false;
        }

        private static void moveForward(int index,int distance,Cell...cells) {
            for(int i=index;i<cells.length-distance;i++){
                cells[i].type = cells[i + distance].type;
                cells[i].value = cells[i + distance].value;
        }
            for(int i=cells.length-distance;i<cells.length;i++)
                cells[i].type = EMPTY_CELL;
        }

        private static void moveForward(int index,Cell...cells)
        {
            moveForward(index,1,cells);
        }

        private void generateNewCell() {
            List<Cell> emptyCells = new ArrayList<>();
            for(Cell c:cells)
                if(c.type == EMPTY_CELL)
                    emptyCells.add(c);

            int length = emptyCells.size();
            if(length == 0)
                return;

            int randomIndex = (int) (Math.random() * length);
            int generateValue = (int)(Math.random() * (CELL_2_WEIGHT + CELL_4_WEIGHT)) <= CELL_2_WEIGHT ? 0 : 1;

            emptyCells.get(randomIndex).type = MOVEABLE_CELL;
            emptyCells.get(randomIndex).value = generateValue;

            addRecord();
        }

        private void addRecord() {
            Cell[] newCells = new Cell[GRID_LENGTH];
            for(int i=0;i<GRID_LENGTH;i++)
                newCells[i] =  cells[i].copy();
            historyRecord.add(newCells);
        }

        private void removeRecord() {
            if(historyRecord.size()>1) {
                historyRecord.removeLast();
                Cell[] lastArray = historyRecord.getLast();
//                System.arraycopy(lastArray, 0, cells, 0, lastArray.length);
                for(int i=0;i<GRID_LENGTH;i++)
                    cells[i] =  lastArray[i].copy();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            updateFrameData();

            drawBackFrame((Graphics2D) g);
            drawFrontCell((Graphics2D) g);
        }

        private void drawSquare(Graphics2D g, int x, int y,Color color) {
            g.setColor(color);
            g.fillRect(x, y, CELL_LENGTH, CELL_LENGTH);
        }

        private void drawSquare(Graphics2D g, Vec2i v, Color color) {
            drawSquare(g, v.getX(), v.getY(), color);
        }

        private void drawSquare(Graphics2D g, int index,Color color) {
            g.setColor(color);
            drawSquare(g, indexOf(index),color);
        }

        private void drawBackFrame(Graphics2D g) {
            for (int i = 0; i < GRID_LENGTH; i++) {
                drawSquare(g, i,EMPTY_CELL_COLOR);
            }
        }

        private void drawCellImage(Graphics2D g, Cell cell) {
            int index = cell.getIndex();
            int value = cell.getValue();
            Vec2i v = indexOf(index);
            g.drawImage(NUM_CELLS[value],v.getX(), v.getY(), CELL_LENGTH, CELL_LENGTH, this);
        }

        private void drawFrontCell(Graphics2D g) {
            for(int i=0;i<GRID_LENGTH;i++)
                if(cells[i].type == MOVEABLE_CELL)
                    drawCellImage(g,cells[i]);
        }

        private Vec2i indexOf(int index) {
            int offset = CELL_LENGTH + GAP_LENGTH;

            int x = index % WIDTH;
            int y = index / WIDTH;

            return Vec2i.add(start.copyOf(), new Vec2i(offset * x, offset * y));
        }

        private int positionOf(Vec2i v) {
            int offsetX = v.getX();
            int offsetY = v.getY();

            if (offsetX < start.getX() || offsetY < start.getY() || offsetX > end.getX() || offsetY > end.getY())
                return OUT_OF_GRID;

            offsetX -= start.getX();
            offsetY -= start.getY();

            if (offsetX % (CELL_LENGTH + GAP_LENGTH) > CELL_LENGTH || offsetY % (CELL_LENGTH + GAP_LENGTH) > CELL_LENGTH)
                return IN_GAP;

            int x = offsetX / (CELL_LENGTH + GAP_LENGTH);
            int y = offsetY / (CELL_LENGTH + GAP_LENGTH);
            return x + y * WIDTH;
        }

        private void updateFrameData() {
            int canvasWidth = getWidth();
            int canvasHeight = getHeight();

            int gridWidth = WIDTH * CELL_LENGTH + (WIDTH - 1) * GAP_LENGTH;
            int gridHeight = HEIGHT * CELL_LENGTH + (HEIGHT - 1) * GAP_LENGTH;

            start = new Vec2i((canvasWidth - gridWidth) / 2, (canvasHeight - gridHeight) / 2);
            end = new Vec2i((canvasWidth - start.getX()) / 2, (canvasHeight - start.getY()) / 2);
        }

        private static class Cell {
            private final int index;
            private int x;
            private int y;
            private int type;
            private int value;

            Cell(int index,int type) {
                this(index,type,0);
            }

            Cell(int index,int type,int value) {
                this.index = index;
                this.type = type;
                this.value = value;
                this.x = index % WIDTH;
                this.y = index / WIDTH;
            }

            Cell(int index) {
                this(index,EMPTY_CELL);
            }

            int getIndex() {
                return index;
            }

            int getValue(){
                return value;
            }

            static Cell copy(Cell cell){
                return new Cell(cell.index,cell.type,cell.value);
            }

            Cell copy() {
                return copy(this);
            }
        }
    }
}
