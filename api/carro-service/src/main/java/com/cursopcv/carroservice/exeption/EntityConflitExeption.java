package com.cursopcv.carroservice.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EntityConflitExeption extends RuntimeException{
    public EntityConflitExeption(String message) {
        super(message);
    }
}
