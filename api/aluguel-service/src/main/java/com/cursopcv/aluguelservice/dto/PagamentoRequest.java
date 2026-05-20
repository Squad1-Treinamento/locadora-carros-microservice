package com.cursopcv.aluguelservice.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PagamentoRequest(
    @NotNull(message = "Tipo de pagamento é obrigatório")
    TipoPagamento tipoPagamento,

    Integer parcelas
) {
}

