package com.cursopcv.aluguelservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PagamentoRequest(
    @NotNull(message = "Tipo de pagamento é obrigatório")
    TipoPagamento tipoPagamento,

    @NotBlank(message = "Número do cartão é obrigatório")
    @Pattern(regexp = "^[0-9]{13,19}$", message = "Número do cartão inválido")
    String numeroCartao,

    @NotBlank(message = "Nome do titular é obrigatório")
    String nomeTitular,

    @NotBlank(message = "Data de validade é obrigatória")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/[0-9]{2}$", message = "Data de validade deve estar no formato MM/YY")
    String dataValidade,

    @NotBlank(message = "CVV é obrigatório")
    @Pattern(regexp = "^[0-9]{3,4}$", message = "CVV deve conter 3 ou 4 dígitos")
    String cvv,

    @NotNull(message = "Valor do pagamento é obrigatório")
    BigDecimal valor,

    Boolean salvarCartao
) {
}

