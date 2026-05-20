package com.cursopcv.aluguelservice.dto;

import com.cursopcv.aluguelservice.model.StatusAluguel;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

public record CheckoutResponse(
        Integer idAluguel,
        String numeroTransacao,
        StatusAluguel statusAluguel,
        String statusPagamento,
        String mensagem,

        Integer idPessoa,
        Long idCarro,
        String nomeCarro,
        String placaCarro,

        @JsonFormat(pattern = "yyyy-MM-dd")
        Date dataEntrega,

        @JsonFormat(pattern = "yyyy-MM-dd")
        Date dataDevolucao,

        Integer quantidadeDias,
        BigDecimal valorTotal,

        ApoliceSeguroResponse apoliceSeguro,
        String termos
) {
}