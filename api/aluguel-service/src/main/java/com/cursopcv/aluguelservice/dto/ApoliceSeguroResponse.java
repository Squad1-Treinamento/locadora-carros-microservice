package com.cursopcv.aluguelservice.dto;

import java.math.BigDecimal;

public record ApoliceSeguroResponse(
        Integer id,
        Boolean protecaoTerceiro,
        Boolean protecaoCausasNaturais,
        Boolean protecaoRoubo,
        BigDecimal valorFranquia,
        BigDecimal custoApolice
) {
}