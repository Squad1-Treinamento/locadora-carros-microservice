package com.cursopcv.carroservice.exeption;

import org.springframework.http.HttpStatus;

public class GlobalHandlerException extends RuntimeException {
    public GlobalHandlerException(HttpStatus http, String message) {
        super(message);
    }
}
