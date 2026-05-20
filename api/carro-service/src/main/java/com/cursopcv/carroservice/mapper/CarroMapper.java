package com.cursopcv.carroservice.mapper;

import com.cursopcv.carroservice.dto.acessorio.AcessorioRequestCarro;
import com.cursopcv.carroservice.dto.acessorio.AcessorioResponse;
import com.cursopcv.carroservice.dto.carro.CarroRequest;
import com.cursopcv.carroservice.dto.carro.CarroResponse;
import com.cursopcv.carroservice.dto.fabricante.FabricanteResponse;
import com.cursopcv.carroservice.dto.modeloCarro.ModeloCarroResponse;
import com.cursopcv.carroservice.model.*;

import java.util.HashSet;
import java.util.Set;

public class CarroMapper {

    public static Carro toEntity(CarroRequest carroRequest, ModeloCarro modeloCarro) {
        Set<Acessorio> acessorioCarro = new HashSet<>();
        if (carroRequest.acessorios() != null) {

            for (AcessorioRequestCarro request : carroRequest.acessorios()) {
                if (request.descricao() != null) {
                    Acessorio acessorio = new Acessorio();
                    acessorio.setDescricao(request.descricao());
                    acessorioCarro.add(acessorio);
                }
            }
        }

        Carro carro = new Carro();
        carro.setPlaca(carroRequest.placa());
        carro.setChassi(carroRequest.chassi());
        carro.setCor(carroRequest.cor());
        carro.setValorDiaria(carroRequest.valorDiaria());
        carro.setImagemUrl(carroRequest.imagemUrl());
        carro.setDisponivel(true);
        carro.setModelo(modeloCarro);
        carro.setAcessorios(acessorioCarro);

        return carro;
    }

    public static CarroResponse toResponse(Carro carro) {
        Fabricante fabricante = carro.getModelo().getFabricante();
        FabricanteResponse fabricanteResponse = new FabricanteResponse(fabricante.getId(), fabricante.getNome());

        ModeloCarro modeloCarro = carro.getModelo();
        ModeloCarroResponse modeloCarroResponse = new ModeloCarroResponse(modeloCarro.getId(), modeloCarro.getDescricao(), modeloCarro.getCategoria(), fabricanteResponse);

        Set<AcessorioResponse> acessoriosResponse = new HashSet<>();
        if (carro.getAcessorios() != null) {

            for (Acessorio acessorio : carro.getAcessorios()) {
                AcessorioResponse acessorioResponse = new AcessorioResponse(acessorio.getId(), acessorio.getDescricao());
                acessoriosResponse.add(acessorioResponse);
            }
        }

        CarroResponse response = new CarroResponse(
                carro.getId(),
                carro.getPlaca(),
                carro.getChassi(),
                carro.getCor(),
                carro.getValorDiaria(),
                carro.getImagemUrl(),
                carro.isDisponivel(),
                modeloCarroResponse,
                acessoriosResponse
        );

        return response;
    }
}
