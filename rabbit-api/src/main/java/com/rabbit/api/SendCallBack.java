package com.rabbit.api;

/**
 * 发送消息回调方法
 *
 * @author Evan
 * @create 2021/2/19 14:18
 */
public interface SendCallBack {

    void sendSuccess();

    void sendFailed();

}
