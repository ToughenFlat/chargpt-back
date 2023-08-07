package com.toughenflat.chatai.exception;

public class EmailException extends RuntimeException{
    public EmailException() {
        super("存在相同的邮箱");
    }
}
