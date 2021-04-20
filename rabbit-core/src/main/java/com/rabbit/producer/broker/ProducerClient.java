package com.rabbit.producer.broker;

import com.rabbit.api.Message;
import com.rabbit.api.MessageProducer;
import com.rabbit.api.MessageType;
import com.rabbit.api.SendCallBack;
import com.rabbit.api.exception.MessageRunTimeException;
import org.assertj.core.util.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Evan
 * @create 2021/2/19 14:38
 */
@Component
public class ProducerClient implements MessageProducer {

    @Autowired
    private RabbitBroker rabbitBroker;

    public void send(Message message) throws MessageRunTimeException {
        Preconditions.checkNotNullOrEmpty(message.getMessageId());
        String messageType = message.getMessageType();
        switch (messageType){
            case MessageType.RAPID:
                rabbitBroker.sendRapid(message);
                break;
            case MessageType.COMFIRM:
                rabbitBroker.sendConfirm(message);
                break;
            case MessageType.RELIANT:
                rabbitBroker.sendReliant(message);
                break;
            default:
                break;
        }

    }

    public void send(Message message, SendCallBack sendCallBack) throws MessageRunTimeException {

    }

    public void send(List<Message> messages) throws MessageRunTimeException {

    }

}
