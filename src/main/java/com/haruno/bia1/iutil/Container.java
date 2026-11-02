package com.haruno.bia1.iutil;

import java.awt.Dimension;
import java.awt.Toolkit;

public class Container {
    public static void moveToMiddle(java.awt.Container container) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = dimension.width;
        int screenHeight = dimension.height;

        container.setLocation(screenWidth / 2 - container.getWidth() / 2, screenHeight / 2 - container.getHeight() / 2);
    }

}
