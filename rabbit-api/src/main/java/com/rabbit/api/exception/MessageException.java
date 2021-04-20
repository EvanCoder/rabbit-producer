package com.rabbit.api.exception;

/**
 * @author Evan
 * @create 2021/2/19 14:03
 */
public class MessageException extends Exception {

    public MessageException(){
        super();
    }

    public MessageException(String message){
        super(message);
    }

    public MessageException(String message, Throwable throwable){
        super(message, throwable);
    }

    public MessageException(Throwable throwable){
        super(throwable);
    }

}
