package com.cursopcv.aluguelservice.dto;

import java.math.BigDecimal;

public record PagamentoResponse(
    String numeroTransacao,
    String status,
    String mensagem,
    BigDecimal valorProcessado
) {
}

