package com.cursopcv.aluguelservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ApoliceSeguroRequest(

        @NotNull(message = "Proteção a terceiros é obrigatória")
        Boolean protecaoTerceiro,

        @NotNull(message = "Proteção a causas naturais é obrigatória")
        Boolean protecaoCausasNaturais,

        @NotNull(message = "Proteção a roubo é obrigatória")
        Boolean protecaoRoubo,

        @NotNull(message = "Valor da franquia é obrigatório")
        @Positive(message = "Valor da franquia deve ser positivo")
        BigDecimal valorFranquia
) {
}