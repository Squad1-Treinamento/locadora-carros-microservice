package com.cursopcv.carroservice.dto.modeloCarro;

import com.cursopcv.carroservice.dto.fabricante.FabricanteRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ModeloCarroRequest(
        @NotBlank(message = "Descrição não pode estar vazia")
        String descricao,

        @NotNull(message = "Categoria não pode estar vazia")
        String categoria,

        @NotNull(message = "Fabricante não pode estar vazio")
        FabricanteRequest fabricante
){}
