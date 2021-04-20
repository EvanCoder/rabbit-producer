package com.rabbit.api.exception;

/**
 * @author Evan
 * @create 2021/2/19 14:03
 */
public class MessageRunTimeException extends RuntimeException {

    public MessageRunTimeException(){
        super();
    }

    public MessageRunTimeException(String message){
        super(message);
    }

    public MessageRunTimeException(String message, Throwable throwable){
        super(message, throwable);
    }

    public MessageRunTimeException(Throwable throwable){
        super(throwable);
    }

}
