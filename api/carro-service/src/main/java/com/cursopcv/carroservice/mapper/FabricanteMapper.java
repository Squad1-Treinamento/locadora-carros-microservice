package com.cursopcv.carroservice.mapper;

import com.cursopcv.carroservice.dto.fabricante.FabricanteRequest;
import com.cursopcv.carroservice.dto.fabricante.FabricanteResponse;
import com.cursopcv.carroservice.model.Fabricante;

public class FabricanteMapper {

    public static Fabricante toEntity(FabricanteRequest fabricanteRequest) {
        Fabricante fabricante = new Fabricante();

        fabricante.setNome(fabricanteRequest.nome());
        return fabricante;
    }

    public static FabricanteResponse toResponse(Fabricante fabricante) {
        FabricanteResponse response = new FabricanteResponse(
                fabricante.getId(),
                fabricante.getNome()
        );

        return response;
    }
}
