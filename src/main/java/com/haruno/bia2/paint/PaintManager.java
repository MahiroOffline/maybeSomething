package com.haruno.bia2.paint;

import java.awt.Graphics;
import java.util.ArrayList;

public class PaintManager {

    private final ArrayList<Painting> paintings = new ArrayList<>();

    public void paint(Graphics g)
    {
        for(int i = 0; i< paintings.size();i++)
            paintings.get(i).paint(g);
    }

    public void add(Painting p)
    {
        if(p != null)
            paintings.add(p);
    }

    public void remove(Painting p)
    {
        if(p != null)
            paintings.remove(p);
    }
}
