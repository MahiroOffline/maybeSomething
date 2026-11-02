package com.haruno.bia1.imath;

public class Mathf {
    public static float lerp(float a,float b,float t){
        return a + (b - a) * t;
    }
    public static double lerp(double a,double b,double t){
        return a + (b - a) * t;
    }


}
