package com.cursopcv.carroservice.dto.fabricante;

import jakarta.validation.constraints.NotBlank;

public record FabricanteRequest (
        @NotBlank(message = "Nome do fabricante não pode estar vazia")
        String nome
){}
