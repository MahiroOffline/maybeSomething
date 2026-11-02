package com.haruno.bia1.iutil;


/**
 *表示每tick根据到目前为止的时间或者继上次的时间偏移量执行一次操作
 * 一般跟rate搭配操作
 */
@FunctionalInterface
public interface Tick {
    /**
    *通过进度或偏移量在每tick处理操作
    *@param elapsed 从开始到现在的时间-s
    *@param delta 继上次时间的偏移量-s
    *@return 操作是否进行完成
    */
    boolean performed(float elapsed ,float delta);
}
