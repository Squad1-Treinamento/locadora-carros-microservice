package com.cursopcv.carroservice.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PlacaInvalidFormatException extends RuntimeException {
    public PlacaInvalidFormatException(String message) {
        super(message);
    }
}