package com.cursopcv.carroservice.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CategoriaInvalidExeption extends RuntimeException{
public CategoriaInvalidExeption(String message) {
    super(message);
}
}
