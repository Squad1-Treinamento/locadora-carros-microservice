package com.cursopcv.carroservice.mapper;

import com.cursopcv.carroservice.dto.fabricante.FabricanteResponse;
import com.cursopcv.carroservice.dto.modeloCarro.ModeloCarroRequest;
import com.cursopcv.carroservice.dto.modeloCarro.ModeloCarroResponse;
import com.cursopcv.carroservice.model.Categoria;
import com.cursopcv.carroservice.model.Fabricante;
import com.cursopcv.carroservice.model.ModeloCarro;

public class ModeloCarroMapper {

    public  static ModeloCarroResponse toResponse(ModeloCarro modeloCarro) {
        FabricanteResponse fabricanteResponse = new FabricanteResponse(
                modeloCarro.getFabricante().getId(),
                modeloCarro.getFabricante().getNome()
        );

        ModeloCarroResponse response = new ModeloCarroResponse(
                modeloCarro.getId(),
                modeloCarro.getDescricao(),
                modeloCarro.getCategoria(),
                fabricanteResponse
        );

        return response;
    }

    public static ModeloCarro toEntity(ModeloCarroRequest modeloCarroRequest, Fabricante fabricante) {
        ModeloCarro modeloCarro = new ModeloCarro();
        modeloCarro.setDescricao(modeloCarroRequest.descricao());
        modeloCarro.setFabricante(fabricante);
        modeloCarro.setCategoria(Categoria.toStringValue(modeloCarroRequest.categoria()));

        return modeloCarro;
    }
}
