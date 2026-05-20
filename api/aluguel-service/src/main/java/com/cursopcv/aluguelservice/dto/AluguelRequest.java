package com.cursopcv.aluguelservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public record AluguelRequest(

        @NotNull(message = "ID do motorista é obrigatório")
        Integer idMotorista,

        @NotNull(message = "ID do carro é obrigatório")
        Long idCarro,

        @NotNull(message = "Data de entrega é obrigatória")
        @FutureOrPresent(message = "Data de entrega deve ser hoje ou no futuro")
        @JsonFormat(pattern = "yyyy-MM-dd")
        Date dataEntrega,

        @NotNull(message = "Data de devolução é obrigatória")
        @Future(message = "Data de devolução deve ser no futuro")
        @JsonFormat(pattern = "yyyy-MM-dd")
        Date dataDevolucao,

        @NotNull(message = "Apólice de seguro é obrigatória")
        @Valid
        ApoliceSeguroRequest apoliceSeguro
){}