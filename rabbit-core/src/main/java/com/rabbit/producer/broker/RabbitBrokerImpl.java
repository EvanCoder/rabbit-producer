package com.rabbit.producer.broker;

import com.rabbit.api.Message;
import com.rabbit.api.MessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Evan
 * @create 2021/2/19 15:00
 */
@Slf4j
public class RabbitBrokerImpl implements RabbitBroker {

    @Autowired
    private RabbitTemplateContainer rabbitTemplateContainer;

    @Override
    public void sendRapid(Message message) {
        message.setMessageType(MessageType.RAPID);
        sendMessage(message);

    }

    @Override
    public void sendConfirm(Message message) {

    }

    @Override
    public void sendReliant(Message message) {

    }

    @Override
    public void sendMessages(Message message) {

    }

    private void sendMessage(Message message){
        AsyncQueue.submit((Runnable) () -> {
            CorrelationData correlationData = new CorrelationData(String.format("%s#%s",
                    message.getMessageId(), System.currentTimeMillis()));

            RabbitTemplate rabbitTemplate = rabbitTemplateContainer.getTemplate(message);

            rabbitTemplate.convertAndSend("exchange", message.getRoutingKey(),
                    message, correlationData);

            log.info("send message.");
        });
    }

}
