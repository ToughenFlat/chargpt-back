package com.toughenflat.chatai.exception;

public class SessionNameRepeatException extends RuntimeException{
    public SessionNameRepeatException(String message) {
        super(message);
    }
}
