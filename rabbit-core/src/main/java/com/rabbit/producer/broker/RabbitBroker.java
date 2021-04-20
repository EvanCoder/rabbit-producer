package com.rabbit.producer.broker;

import com.rabbit.api.Message;

/**
 * @author Evan
 * @create 2021/2/19 14:45
 */
public interface RabbitBroker {

    void sendRapid(Message message);

    void sendConfirm(Message message);

    void sendReliant(Message message);

    void sendMessages(Message message);

}
