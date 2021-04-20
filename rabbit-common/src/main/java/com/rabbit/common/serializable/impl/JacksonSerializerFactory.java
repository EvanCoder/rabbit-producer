package com.rabbit.common.serializable.impl;


import com.rabbit.api.Message;
import com.rabbit.common.serializable.Serializer;
import com.rabbit.common.serializable.SerializerFactory;

/**
 * @author Evan
 * @create 2021/2/19 16:39
 */
public class JacksonSerializerFactory implements SerializerFactory {

    public static final SerializerFactory INSTANCE = new JacksonSerializerFactory();

    @Override
    public Serializer create() {
        return JacksonSerializer.createType(Message.class);
    }

}
