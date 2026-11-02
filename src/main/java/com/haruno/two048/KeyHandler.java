package com.haruno.two048;

import com.haruno.bia1.icontainer.Grid;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Set;
import java.util.function.Consumer;

public class KeyHandler {
    public static final Set<Integer> UP_KEY = Set.of(KeyEvent.VK_UP);
    public static final Set<Integer> DOWN_KEY = Set.of(KeyEvent.VK_DOWN);
    public static final Set<Integer> RIGHT_KEY = Set.of(KeyEvent.VK_RIGHT);
    public static final Set<Integer> LEFT_KEY = Set.of(KeyEvent.VK_LEFT);
    public static final Set<Integer> CAMERA_UP_KEY = Set.of(KeyEvent.VK_W);
    public static final Set<Integer> CAMERA_DOWN_KEY = Set.of(KeyEvent.VK_S);
    public static final Set<Integer> CAMERA_RIGHT_KEY = Set.of(KeyEvent.VK_D);
    public static final Set<Integer> CAMERA_LEFT_KEY = Set.of(KeyEvent.VK_A);
    public static final Set<Integer> REWIND_KEY = Set.of(KeyEvent.VK_Z,KeyEvent.VK_C);
    public static final Set<Integer> RESTART_KEY = Set.of(KeyEvent.VK_R);
    public static final Set<Integer> EXIT_KEY = Set.of(KeyEvent.VK_ESCAPE);
    public static final Set<Integer> TEST_KEY = Set.of(KeyEvent.VK_T);

    public static final String UP_MOTION = "up";
    public static final String DOWN_MOTION = "down";
    public static final String RIGHT_MOTION = "right";
    public static final String LEFT_MOTION = "left";
    public static final String CAMERA_UP_MOTION = "camera up";
    public static final String CAMERA_DOWN_MOTION = "camera down";
    public static final String CAMERA_RIGHT_MOTION = "camera right";
    public static final String CAMERA_LEFT_MOTION = "camera left";
    public static final String REWIND_MOTION = "rewind";
    public static final String RESTART_MOTION = "restart";
    public static final String EXIT_MOTION = "exit";
    public static final String TEST_MOTION = "test";
    public static final String NULL_MOTION = "null";

    static void keyBinds(Logic logic){
        Grid grid = logic.grid;
        InputMap inputMap = grid.getInputMap();
        ActionMap actionMap = grid.getActionMap();

        bindKey(inputMap,actionMap,UP_MOTION,UP_KEY, logic::cellsMove);
        bindKey(inputMap,actionMap,LEFT_MOTION,LEFT_KEY, logic::cellsMove);
        bindKey(inputMap,actionMap,DOWN_MOTION,DOWN_KEY, logic::cellsMove);
        bindKey(inputMap,actionMap,RIGHT_MOTION,RIGHT_KEY, logic::cellsMove);
        bindKey(inputMap,actionMap,CAMERA_UP_MOTION,CAMERA_UP_KEY, logic::cameraMove);
        bindKey(inputMap,actionMap,CAMERA_LEFT_MOTION,CAMERA_LEFT_KEY, logic::cameraMove);
        bindKey(inputMap,actionMap,CAMERA_DOWN_MOTION,CAMERA_DOWN_KEY, logic::cameraMove);
        bindKey(inputMap,actionMap,CAMERA_RIGHT_MOTION,CAMERA_RIGHT_KEY, logic::cameraMove);
        bindKey(inputMap,actionMap,REWIND_MOTION,REWIND_KEY, logic::rewind);
        bindKey(inputMap,actionMap,RESTART_MOTION,RESTART_KEY, logic::restart);
        bindKey(inputMap,actionMap,EXIT_MOTION,EXIT_KEY,logic::exit);
        bindKey(inputMap,actionMap,TEST_MOTION,TEST_KEY,logic::test);

    }

    private static void bindKey(InputMap inputMap, ActionMap actionMap, String motion, Set<Integer> keyCodeSet, Consumer<String> motionPerformed) {
        bindKey(inputMap, actionMap,motion,keyCodeSet,()->motionPerformed.accept(motion));
    }

    private static void bindKey(InputMap inputMap, ActionMap actionMap, String motion, Set<Integer> keyCodeSet, Runnable motionPerformed) {
        int actualKeyCode = keyCodeSet.stream().findFirst().get();
        int[] keyCodes =  keyCodeSet.stream().mapToInt(Integer::intValue).toArray();

        for(int keyCode : keyCodes)
            inputMap.put(KeyStroke.getKeyStroke(keyCode,0),motion);

        actionMap.put(motion,new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                motionPerformed.run();
            }
        });
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

}
