package com.abhay.urlshortener.common.exception;

public class ShortUrlNotFoundException extends RuntimeException{

    public ShortUrlNotFoundException(String message) {
        super(message);
    }
}
