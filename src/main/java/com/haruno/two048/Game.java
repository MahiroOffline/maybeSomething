package com.haruno.two048;

import com.haruno.bia1.icontainer.Grid;
import com.haruno.bia1.iutil.Ticker;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.haruno.bia1.iutil.Container.moveToMiddle;
import static java.util.Objects.requireNonNull;

public class Game {
    private static final int FPS = 60;

    private static final int DEFAULT_FRAME_WIDTH = 800;
    private static final int DEFAULT_FRAME_HEIGHT = 800;

    static final BufferedImage[] NUM_CELLS = new BufferedImage[11];

    JFrame frame;
    Grid grid;
    Grid grid2;
    Logic logic;
    Ticker ticker;
    Ticker fpsFresher;

    public Game()  {
        initFrame();

        logic = new Logic(this,grid);
        ticker = new Ticker(FPS);
        fpsFresher = new Ticker(1);

        logic.start();
    }

    private void initFrame() {
        frame = new JFrame();
        frame.setSize(DEFAULT_FRAME_WIDTH, DEFAULT_FRAME_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        moveToMiddle(frame);

        grid = new Grid(4, 4);
//        grid.setVisible(3, 3, false);
        frame.add(grid);

        frame.setVisible(true);

        grid.initCamera();
        grid.repaint();
        frame.repaint();
    }

    public void run(){
        grid.setSize(800,800);
        ticker.addTask(grid::repaint);
        fpsFresher.addTask(() -> frame.setTitle("%dfps".formatted(Math.round(ticker.fps()))));
        fpsFresher.addTask(()->{
//            System.out.println(grid.getWidth()+"x"+grid.getHeight());
        });

        ticker.start();
        fpsFresher.start();
    }

    public void stop(){
        ticker.stop();
        fpsFresher.stop();
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }

    private static void initResources() {
        try {
            NUM_CELLS[0] = ImageIO.read(requireNonNull(Two048.class.getResourceAsStream("/two048/pic_2.png")));
            NUM_CELLS[1] = ImageIO.read(requireNonNull(Two048.class.getResourceAsStream("/two048/pic_4.png")));
            NUM_CELLS[2] = ImageIO.read(requireNonNull(Two048.class.getResourceAsStream("/two048/pic_8.png")));
            NUM_CELLS[3] = ImageIO.read(requireNonNull(Two048.class.getResourceAsStream("/two048/pic_16.png")));
            NUM_CELLS[4] = ImageIO.read(requireNonNull(Two048.class.getResourceAsStream("/two048/pic_32.png")));
            NUM_CELLS[5] = ImageIO.read(requireNonNull(Two048.class.getResourceAsStream("/two048/pic_64.png")));
            NUM_CELLS[6] = ImageIO.read(requireNonNull(Two048.class.getResourceAsStream("/two048/pic_128.png")));
            NUM_CELLS[7] = ImageIO.read(requireNonNull(Two048.class.getResourceAsStream("/two048/pic_256.png")));
            NUM_CELLS[8] = ImageIO.read(requireNonNull(Two048.class.getResourceAsStream("/two048/pic_512.png")));
            NUM_CELLS[9] = ImageIO.read(requireNonNull(Two048.class.getResourceAsStream("/two048/pic_1024.png")));
            NUM_CELLS[10] = ImageIO.read(requireNonNull(Two048.class.getResourceAsStream("/two048/pic_2048.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static {
        initResources();
    }
}
