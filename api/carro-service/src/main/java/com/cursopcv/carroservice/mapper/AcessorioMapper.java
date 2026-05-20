package com.cursopcv.carroservice.mapper;

import com.cursopcv.carroservice.dto.acessorio.AcessorioRequest;
import com.cursopcv.carroservice.dto.acessorio.AcessorioResponse;
import com.cursopcv.carroservice.model.Acessorio;

public class AcessorioMapper {

    public static Acessorio toEntity(AcessorioRequest acessorioRequest) {
        Acessorio acessorio = new Acessorio();

        acessorio.setDescricao(acessorio.getDescricao());

        return acessorio;
    }

    public static AcessorioResponse toResponse(Acessorio acessorio) {
        AcessorioResponse response = new AcessorioResponse(
                acessorio.getId(),
                acessorio.getDescricao()
        );

        return  response;
    }
}
