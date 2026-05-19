package com.cursopcv.carroservice.model;

import com.cursopcv.carroservice.exeption.CategoriaInvalidExeption;

public enum Categoria {
    HATCH_COMPACTO,
    HATCH_MEDIO,
    SEDAN_COMPACTO,
    SEDAN_MEDIO,
    SEDAN_GRANDE,
    MINIVAN,
    ESPORTIVO,
    UTILITARIO_COMERCIAL;

    public static Categoria toStringValue(String categoria) {
        try {
            return Categoria.valueOf(categoria.stripTrailing().replace(" ", "_").toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new CategoriaInvalidExeption("A categoria passada é invalida ou não corresponde a uma categoria existente");
        }
    }
}
