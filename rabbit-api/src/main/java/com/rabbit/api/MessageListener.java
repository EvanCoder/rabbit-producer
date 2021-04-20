package com.rabbit.api;

/**
 * 监听消息
 *
 * @author Evan
 * @create 2021/2/19 14:17
 */
public interface MessageListener {

    void onMessage(Message message);

}
