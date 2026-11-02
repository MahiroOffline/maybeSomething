package com.haruno.bia2.math;

public class Mathf {
    public static int lerp(int a,int b,double t){
        return Math.toIntExact(a + Math.round((b - a) * t));
    }

    public static float lerp(float a,float b,double t){
        return  a + (float) ((b - a) * t);
    }

    public static double lerp(double a,double b,double t){
        return a + (b - a) * t;
    }
}
