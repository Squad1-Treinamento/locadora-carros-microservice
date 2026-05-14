package com.cursopcv.pessoaservice.model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public enum Sexo {
@Enumerated(EnumType.STRING)
    MASCULINO,
    FEMININO
}
