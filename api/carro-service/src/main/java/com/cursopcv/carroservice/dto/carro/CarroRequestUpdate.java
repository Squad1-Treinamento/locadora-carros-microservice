package com.cursopcv.carroservice.dto.carro;

import java.math.BigDecimal;

public record CarroRequestUpdate (
    String placa,
    String chassi,
    String cor,
    BigDecimal valorDiaria
){}
