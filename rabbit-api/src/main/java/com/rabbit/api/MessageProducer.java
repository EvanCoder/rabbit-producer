package com.rabbit.api;

import com.rabbit.api.exception.MessageRunTimeException;

import java.util.List;

/**
 * 发送消息
 *
 * @author Evan
 * @create 2021/2/19 14:17
 */
public interface MessageProducer {

    void send(Message message) throws MessageRunTimeException;

    void send(Message message, SendCallBack sendCallBack) throws MessageRunTimeException;

    void send(List<Message> messages) throws MessageRunTimeException;

}
