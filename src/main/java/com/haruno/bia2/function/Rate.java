package com.haruno.bia2.function;

//TODO 通过elapsed时间和总时间传出一个以1为单位的double值
public interface Rate {
    double OUT_OF_SPAN = Double.MAX_VALUE;

    double get(double elapsed,double span);
}
