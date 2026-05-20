package com.cursopcv.carroservice.dto.acessorio;

import jakarta.validation.constraints.NotBlank;

public record AcessorioRequest(
        @NotBlank(message = "Descrição não pode estar vazia")
        String descricao
){}
