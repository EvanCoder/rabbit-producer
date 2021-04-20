package com.rabbit.api;


/**
 * 传送消息实体类
 *
 * @author Evan
 * @create 2021/2/18 20:45
 */
public final class MessageType {

    //迅速发送接收消息：不需要保障消息的可靠性，也不需要confirm确认
    public static final String RAPID = "0";

    //确认消息：不需要保障消息的可靠性，但需要confirm确认
    public static final String COMFIRM = "1";

    //可靠消息：保障消息的100%可靠性传递
    public static final String RELIANT = "2";

}
