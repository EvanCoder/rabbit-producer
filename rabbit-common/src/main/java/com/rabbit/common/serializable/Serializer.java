package com.rabbit.common.serializable;

/**
 * @author Evan
 * @create 2021/2/19 16:18
 */
public interface Serializer {

    byte[] serializeObject(Object data);

    String serialize(Object data);

    <T> T deserialize(byte[] data);

    <T> T deserialize(String data);


}
