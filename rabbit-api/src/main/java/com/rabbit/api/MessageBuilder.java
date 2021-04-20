package com.rabbit.api;

import com.rabbit.api.exception.MessageRunTimeException;
import com.rabbit.api.exception.MessageRunTimeExceptionType;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 建造者模式
 *
 * @author Evan
 * @create 2021/2/18 20:45
 */
@Data
public class MessageBuilder implements Serializable {

    private String messageId;

    private String topic;

    private String routingKey;

    private Map<String, Object> attributes = new HashMap<String, Object>();

    private String delayMills;

    private String messageType = MessageType.COMFIRM;

    public MessageBuilder create(){
        return new MessageBuilder();
    }

    public Message build(){
        if (messageId == null){
            messageId = UUID.randomUUID().toString();
        }

        if (topic == null){
            throw new MessageRunTimeException(MessageRunTimeExceptionType.MESSAGE_RUNTIME_EXCEPTION_TYPE_001);
        }

        Message message = new Message(messageId, topic, routingKey, attributes,
                delayMills, messageType);
        return message;
    }


}
