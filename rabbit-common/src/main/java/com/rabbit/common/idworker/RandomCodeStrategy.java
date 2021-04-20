package com.rabbit.common.idworker;

/**
 * @author Evan
 * @create 2021/3/4 9:18
 */
public interface RandomCodeStrategy {

    void init();

    int prefix();

    int next();

    void release();

}
