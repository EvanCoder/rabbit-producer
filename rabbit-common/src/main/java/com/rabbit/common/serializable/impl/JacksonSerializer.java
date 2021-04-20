package com.rabbit.common.serializable.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.rabbit.common.serializable.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author Evan
 * @create 2021/2/19 16:22
 */
@Slf4j
public class JacksonSerializer implements Serializer {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.disable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        mapper.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
        mapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    }

    private final JavaType javaType;

    private JacksonSerializer(JavaType javaType){
        this.javaType = javaType;
    }

    public JacksonSerializer(Type type){
        this.javaType = mapper.getTypeFactory().constructType(type);
    }

    public static JacksonSerializer createType(Class<?> cls){
        return new JacksonSerializer(mapper.getTypeFactory().constructType(cls));
    }

    @Override
    public byte[] serializeObject(Object data) {
        try {
            return mapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e){
            log.error("serializeObject failure");
        }
        return null;
    }

    @Override
    public String serialize(Object data) {
        try {
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException e){
            log.error("serialize failure");
        }
        return null;
    }

    @Override
    public <T> T deserialize(byte[] data) {
        try {
            return mapper.readValue(data, javaType);
        } catch (IOException e){
            log.error("deserialize failure");
        }
        return null;
    }

    @Override
    public <T> T deserialize(String data) {
        try {
            return mapper.readValue(data, javaType);
        } catch (IOException e){
            log.error("deserialize failure");
        }
        return null;
    }

}
