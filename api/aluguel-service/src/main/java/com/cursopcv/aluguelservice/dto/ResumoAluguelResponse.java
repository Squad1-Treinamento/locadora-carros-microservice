package com.cursopcv.aluguelservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

public record ResumoAluguelResponse(
    Integer idAluguel,
    Integer idPessoa,
    Long idCarro,

    String nomeCarro,
    String placaCarro,
    String corCarro,
    String imagemUrlCarro,
    BigDecimal valorDiariaVeiculo,

    @JsonFormat(pattern = "yyyy-MM-dd")
    Date dataEntrega,

    @JsonFormat(pattern = "yyyy-MM-dd")
    Date dataDevolucao,

    Integer quantidadeDias,
    BigDecimal valorDiaria,
    BigDecimal subtotal,

    ApoliceSeguroResponse apolice,
    BigDecimal custoApolice,

    BigDecimal valorTotal,

    String termos
) {
}

