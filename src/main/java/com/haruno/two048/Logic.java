package com.haruno.two048;

import com.haruno.bia1.icontainer.Grid;
import com.haruno.bia1.ielement.Animator;
import com.haruno.bia1.ielement.Camera;
import com.haruno.bia1.imath.Anchor;
import com.haruno.bia1.imath.Position;
import com.haruno.bia1.iutil.Rate;
import com.haruno.bia1.iutil.WeightPool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static com.haruno.two048.Game.NUM_CELLS;
import static com.haruno.two048.KeyHandler.CAMERA_DOWN_MOTION;
import static com.haruno.two048.KeyHandler.CAMERA_LEFT_MOTION;
import static com.haruno.two048.KeyHandler.CAMERA_RIGHT_MOTION;
import static com.haruno.two048.KeyHandler.CAMERA_UP_MOTION;
import static com.haruno.two048.KeyHandler.DOWN_MOTION;
import static com.haruno.two048.KeyHandler.LEFT_MOTION;
import static com.haruno.two048.KeyHandler.RIGHT_MOTION;
import static com.haruno.two048.KeyHandler.UP_MOTION;
import static com.haruno.two048.KeyHandler.keyBinds;

public class Logic {
    private static final int EMPTY_CELL = -1;
    private static final int NO_SUCH_CELL = -2;
    private static final int MERGED = 16;
    private static final int FIRST_VALUE = 0;
    private static final int LAST_VALUE = 10;
    private static final int WIDTH = 4;
    private static final int GRID_LENGTH = 16;
    private static final float TOTAL_TIME = 0.32f;
    private static final float CELL_MOVE_EXECUTE_TIME_MERGEDNAII = TOTAL_TIME;
    private static final float CELL_MOVE_EXECUTE_TIME_MERGED = TOTAL_TIME / 3;
    private static final float CELL_MERGED_EXECUTE_TIME = TOTAL_TIME / 2;

    private static final WeightPool<Integer> weightPool = new WeightPool<>();
    static{
        weightPool.put(0,3);
        weightPool.put(1,2);
    }

    Thread gameThread;
    Game game;
    Grid grid;
    Animator animator;
    Camera camera;

    private long lastTime = System.nanoTime();
    private final List<String> cellMotions = new ArrayList<>();
    private final int[] cells = new int[GRID_LENGTH];
    private final int[] drawCells = new int[GRID_LENGTH];
    private final List<int[]> historyList = new ArrayList<>();

    Logic(Game game,Grid grid) {
        this.game = game;
        this.grid = game.grid;
        this.animator = new Animator();
        this.camera = grid.camera;
        Arrays.fill(cells, EMPTY_CELL);
        Arrays.fill(drawCells, EMPTY_CELL);

        grid.setOverlay(g -> {
            for (int i = 0; i < GRID_LENGTH; i++)
                if (drawCells[i] >= FIRST_VALUE && drawCells[i] <= LAST_VALUE)
                    g.drawImage(Game.NUM_CELLS[drawCells[i]], grid.indexOf(i).x(), grid.indexOf(i).y(), null);

            animator.drawTick(g);
        });

        keyBinds(this);
    }

    void generateNewCell(){
        List<Integer> emptyCells = new ArrayList<>();
        IntStream.range(0,GRID_LENGTH).forEach(index -> {
            if (cells[index] == EMPTY_CELL) emptyCells.add(index);
        });

        int randomIndex = emptyCells.get((int) (Math.random() * emptyCells.size()));

        int randomValue = weightPool.roll();
        cells[randomIndex] = randomValue;

        addRecord();

        cellAppear(randomIndex,randomValue);
    }

    void cellsMove(String motion){
        animator.clear();

        float delta = (System.nanoTime() - lastTime)/1e9f;

        boolean isMoved = false;
        boolean winned = false;
        int frontPace;
        int rowPace;
        int startIndex;

        switch (motion){
            case UP_MOTION -> {
                frontPace = -4;
                rowPace = 1;
                startIndex = 0;
                System.out.println("UP_MOTION");
            }
            case RIGHT_MOTION -> {
                frontPace = 1;
                rowPace = 4;
                startIndex = 3;
                System.out.println("RIGHT_MOTION");
            }
            case DOWN_MOTION -> {
                frontPace = 4;
                rowPace = -1;
                startIndex = 15;
                System.out.println("DOWN_MOTION");
            }
            case LEFT_MOTION -> {
                frontPace = -1;
                rowPace = -4;
                startIndex = 12;
                System.out.println("LEFT_MOTION");
            }
            default -> {
                return;
            }
        }

        for(int i = 0;i < WIDTH;i++){
            for(int j = 1;j < WIDTH;j++){
                int curIndex = startIndex - j * frontPace;
                if(cells[curIndex] == EMPTY_CELL)
                    continue;
                else{
                    boolean merged = false;
                    int farestEmptyCellIndex = NO_SUCH_CELL;
                    int frontCellIndex;

                    for(int f = j - 1;f >= 0;f--){
                        frontCellIndex = startIndex - f * frontPace;
                        if(cells[frontCellIndex] == EMPTY_CELL)
                            farestEmptyCellIndex = frontCellIndex;
                        else if(cells[curIndex] == cells[frontCellIndex]){
                            merged = true;
                            isMoved = true;

                            int value = cells[frontCellIndex]++;
                            cells[frontCellIndex] += MERGED;
                            cells[curIndex] = EMPTY_CELL;
                            drawCells[curIndex] = EMPTY_CELL;
                            drawCells[frontCellIndex] = EMPTY_CELL;
                            cellMove(curIndex,frontCellIndex,value++, merged);
                            cellMerge(frontCellIndex,value);
                            if(value == LAST_VALUE)
                                winned = true;

                            break;
                        }else /*cells[curIndex] != cells[frontCellIndex])*/
                            break;
                    }

                    if(merged)
                        continue;
                    if(farestEmptyCellIndex == NO_SUCH_CELL)
                        continue;

                    drawCells[curIndex] = EMPTY_CELL;
                    cellMove(curIndex,farestEmptyCellIndex,cells[curIndex],merged);
                    cells[farestEmptyCellIndex] = cells[curIndex];
                    cells[curIndex] = EMPTY_CELL;
                    isMoved = true;
                }
            }

            startIndex += rowPace;
        }

        for(int i = 0;i < GRID_LENGTH;i++)
            if(cells[i] > MERGED)
                cells[i] -= MERGED;

        checkLost();

        if(winned)
            win();

        if(isMoved)
            generateNewCell();
    }

    void cellMove(int srcIndex,int destIndex,int value,boolean merged){
        int length = grid.getCellLength();
        animator.add(animator.flatSlide(grid.indexOf(srcIndex),grid.indexOf(destIndex) ,
                        length, length,
                Game.NUM_CELLS[value],
                merged ? CELL_MOVE_EXECUTE_TIME_MERGED : CELL_MOVE_EXECUTE_TIME_MERGEDNAII,
                Rate.easeOutQuint),
                merged ? Animator.BOTTOM : Animator.MIDDLE,
                merged ? null
//                        ()->animator.add(animator.sizeChange(Anchor.anchorToPosition(grid.indexOf(destIndex), length, length,Anchor.CENTER),
//                        Position.of(length,length),Position.of(length/2,length/2),Game.NUM_CELLS[value],
//                         CELL_MERGED_EXECUTE_TIME ,Rate.linearity),Animator.BOTTOM,null )
                        : () -> drawCells[destIndex] = cells[destIndex]);
    }

    //TODO 融合加速函数
    void cellMerge(int index,int value){
        animator.add(animator.sizeChange(Anchor.anchorToPosition(grid.indexOf(index),grid.getCellLength(),grid.getCellLength(), Anchor.CENTER),
                Position.of(0,0),Position.of(grid.getCellLength(),grid.getCellLength()),
                Game.NUM_CELLS[value],CELL_MERGED_EXECUTE_TIME + CELL_MOVE_EXECUTE_TIME_MERGED,Rate.easeOutBack ),Animator.TOP,
                () -> drawCells[index] = cells[index]);
    }

    void cellAppear(int index,int value){
        animator.add(animator.sizeChange(Anchor.anchorToPosition(grid.indexOf(index),grid.getCellLength(),grid.getCellLength(), Anchor.CENTER),
                Position.of(0,0),Position.of(grid.getCellLength(),grid.getCellLength()),
                Game.NUM_CELLS[value],CELL_MOVE_EXECUTE_TIME_MERGEDNAII,Rate.easeOutBack),Animator.TOP,
                ()->drawCells[index] = cells[index]);
    }

    void cameraMove(String motion){
        int pace = 10;
        switch (motion){
            case CAMERA_UP_MOTION ->{camera.moveCamera(0,-pace );
                System.out.println("CAMERA_UP_MOTION");}
            case CAMERA_DOWN_MOTION ->{camera.moveCamera(0,pace );
                System.out.println("CAMERA_DOWN_MOTION");}
            case CAMERA_LEFT_MOTION ->{camera.moveCamera(-pace,0 );
                System.out.println("CAMERA_LEFT_MOTION");}
            case CAMERA_RIGHT_MOTION ->{camera.moveCamera(pace,0 );
                System.out.println("CAMERA_RIGHT_MOTION");}
            default -> {}
        }
    }

    void rewind(){
        System.out.println("Rewind");
        removeRecord();
    }

    void restart(){
        System.out.println("Restart");
        animator.clear();
        for(int i = 0;i < GRID_LENGTH;i++) {
            cells[i] = EMPTY_CELL;
            drawCells[i] = EMPTY_CELL;
        }
        generateNewCell();
    }

    void addRecord(){
        int[] newRecord = new int[GRID_LENGTH];
        for(int i = 0;i < GRID_LENGTH;i++)
            newRecord[i] = cells[i];
        historyList.add(newRecord);
    }

    void removeRecord(){
        if(historyList.size() <= 1)
            return;
        historyList.removeLast();
        int[] lastRecord = historyList.getLast();
        animator.clear();
        for(int i = 0;i < GRID_LENGTH;i++) {
            cells[i] = lastRecord[i];
            drawCells[i] = cells[i];
        }
    }

    void checkLost(){
        boolean lost = true;
        for(int row = 0;row < WIDTH;row++)
            for(int i=0;i<WIDTH-1;i++)
                if (cells[i + row * WIDTH] == cells[i + 1 + row * WIDTH] || cells[i + row * WIDTH] == EMPTY_CELL || cells[i + 1 + row * WIDTH] == EMPTY_CELL) {
                    lost = false;
                    break;
                }

        for(int col = 0;col<WIDTH;col++)
            for(int i=0;i<WIDTH-1;i++){
                if(cells[col + i * WIDTH] == cells[col + (i + 1) * WIDTH] || cells[col + i * WIDTH] == EMPTY_CELL || cells[col + (i + 1) * WIDTH] == EMPTY_CELL){
                    lost = false;
                    break;
                }
            }

        if(lost)
            lost();
    }

    void start(){
        generateNewCell();
    }

    void win(){
        System.out.println("You win!");
    }

    void lost(){
        System.out.println("You lost");
    }

    void exit(){
        System.out.println("Exit Called");
        game.stop();
    }
    void test(){
        test4();
        System.out.println("test Called");
    }

    void test1(){
        animator.add(animator.flatSlide(
                        grid.indexOf(0),grid.indexOf(3),
                        grid.getCellLength(),grid.getCellLength(),
                        NUM_CELLS[0],2, Rate.paraCurve),
                Animator.TOP,null);
        animator.add(animator.flatSlide(
                        grid.indexOf(1),grid.indexOf(3),
                        grid.getCellLength(),grid.getCellLength(),
                        NUM_CELLS[1],2, Rate.paraCurve),
                Animator.MIDDLE,null);
        animator.add(animator.flatSlide(
                        grid.indexOf(2),grid.indexOf(3),
                        grid.getCellLength(),grid.getCellLength(),
                        NUM_CELLS[2],2, Rate.paraCurve),
                Animator.BOTTOM,null);
    }
    void test2(){
        animator.add(animator.sizeChange(Anchor.anchorToPosition(grid.indexOf(6),grid.getCellLength(),grid.getCellLength(), Anchor.CENTER),
                Position.of(0,0), Position.of(grid.getCellLength(),grid.getCellLength()),
                Anchor.CENTER,NUM_CELLS[6],0.3f,Rate.linearity),Animator.TOP,null);
    }
    void test3(){
        cells[0] = LAST_VALUE-1;
        cells[1] = LAST_VALUE-1;
    }

    void test4(){
        animator.clear();
        for(int i = 0 ;i < GRID_LENGTH;i++){
            int value = (i%2+(i/4)%2)%2;
            cells[i] = value;
            drawCells[i] = value;
        }

        checkLost();
    }
}
