package com.cursopcv.carroservice.dto.carro;

import com.cursopcv.carroservice.dto.modeloCarro.ModeloCarroResponse;
import com.cursopcv.carroservice.dto.acessorio.AcessorioResponse;

import java.math.BigDecimal;
import java.util.Set;

public record CarroResponse(
        Long id,
        String placa,
        String chassi,
        String cor,
        BigDecimal valorDiaria,
        ModeloCarroResponse modelo,
        Set<AcessorioResponse> acessorios
){}
