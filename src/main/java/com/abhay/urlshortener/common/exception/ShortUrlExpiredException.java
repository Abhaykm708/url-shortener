package com.abhay.urlshortener.common.exception;

public class ShortUrlExpiredException extends RuntimeException{

    public ShortUrlExpiredException(String message) {
        super(message);
    }
}
