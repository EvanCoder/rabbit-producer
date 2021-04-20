package com.rabbit.common.idworker;

/**
 * @author Evan
 * @create 2021/3/4 9:18
 */
public interface WorkIdStrategy {

    void initialize();

    long availableWorkerId();

    void release();

}
