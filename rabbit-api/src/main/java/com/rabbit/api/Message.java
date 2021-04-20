package com.rabbit.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 传送消息实体类
 *
 * @author Evan
 * @create 2021/2/18 20:45
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message implements Serializable {

    private String messageId;

    private String topic;

    private String routingKey;

    private Map<String, Object> attributes = new HashMap<String, Object>();

    private String delayMills;

    private String messageType = MessageType.COMFIRM;

}
