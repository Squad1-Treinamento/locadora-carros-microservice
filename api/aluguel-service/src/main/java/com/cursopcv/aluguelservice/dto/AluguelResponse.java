package com.cursopcv.aluguelservice.dto;

import com.cursopcv.aluguelservice.model.StatusAluguel;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

public record AluguelResponse(
        Integer id,
        Integer idPessoa,
        Long idCarro,

        @JsonFormat(pattern = "yyyy-MM-dd")
        Date dataEntrega,

        @JsonFormat(pattern = "yyyy-MM-dd")
        Date dataDevolucao,

        BigDecimal valorDiaria,
        Integer quantidadeDias,
        BigDecimal valorTotal,

        StatusAluguel status,
        ApoliceSeguroResponse apoliceSeguro
){}