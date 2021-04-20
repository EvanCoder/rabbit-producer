package com.rabbit.producer.broker;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.rabbit.api.Message;
import com.rabbit.api.MessageType;
import com.rabbit.common.convert.GenericMessageConverter;
import com.rabbit.common.serializable.Serializer;
import com.rabbit.common.serializable.SerializerFactory;
import com.rabbit.common.serializable.impl.JacksonSerializerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author Evan
 * @create 2021/2/19 15:28
 */
@Slf4j
@Component
public class RabbitTemplateContainer implements RabbitTemplate.ConfirmCallback {

    private Map<String, RabbitTemplate> rabbitTemplateMap = Maps.newConcurrentMap();

    private Splitter splitter = Splitter.on("#");

    @Autowired
    private ConnectionFactory connectionFactory;

    private SerializerFactory serializerFactory = JacksonSerializerFactory.INSTANCE;

    public RabbitTemplate getTemplate(Message message) {
        Preconditions.checkNotNull(message);

        String topic = message.getTopic();
        RabbitTemplate template = rabbitTemplateMap.get(topic);
        if (template != null){
            return  template;
        }

        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setExchange(topic);
        rabbitTemplate.setRoutingKey(message.getRoutingKey());
        rabbitTemplate.setRetryTemplate(new RetryTemplate());

        Serializer serializer = serializerFactory.create();
        GenericMessageConverter gmc = new GenericMessageConverter(serializer);

        rabbitTemplate.setMessageConverter(gmc);

        String messageType = message.getMessageType();
        if (!MessageType.RAPID.equals(messageType)){
            rabbitTemplate.setConfirmCallback(this::confirm);
        }

        rabbitTemplateMap.putIfAbsent(topic, rabbitTemplate);
        return rabbitTemplateMap.get(topic);
    }

    @Override
    public void confirm(@Nullable CorrelationData correlationData, boolean b, @Nullable String s) {
        List<String> strings = splitter.splitToList(correlationData.getId());

        String messageId = strings.get(0);
        long sendTime = Long.parseLong(strings.get(1));

        if (b){
            log.info("send message success");
        } else {
            log.error("send message failure");
        }


    }

}
