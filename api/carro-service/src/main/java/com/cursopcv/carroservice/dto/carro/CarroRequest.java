package com.cursopcv.carroservice.dto.carro;

import com.cursopcv.carroservice.dto.modeloCarro.ModeloCarroRequest;
import com.cursopcv.carroservice.dto.acessorio.AcessorioRequestCarro;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Set;

public record CarroRequest(
        @NotBlank(message = "Placa não pode estar vazia")
        String placa,

        @NotBlank(message = "Chassi não pode estar vazio")
        String chassi,

        @NotBlank(message = "Cor não pode estar vazia")
        String cor,

        @NotNull(message = "Valor da diária não pode estar vazia")
        @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
        BigDecimal valorDiaria,

        @NotNull(message = "Modelo não estar vazio")
        ModeloCarroRequest modelo,

        Set<AcessorioRequestCarro> acessorios
){}