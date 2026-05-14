package com.cursopcv.carroservice.dto.modeloCarro;

import com.cursopcv.carroservice.dto.fabricante.FabricanteResponse;
import com.cursopcv.carroservice.model.Categoria;

public record ModeloCarroResponse (
        Long id,
        String descricao,
        Categoria categoria,
        FabricanteResponse fabricante
){}
