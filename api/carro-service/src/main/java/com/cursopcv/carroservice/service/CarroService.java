package com.cursopcv.carroservice.service;

import com.cursopcv.carroservice.dto.carro.CarroRequest;
import com.cursopcv.carroservice.dto.carro.CarroRequestUpdate;
import com.cursopcv.carroservice.dto.carro.CarroResponse;
import com.cursopcv.carroservice.exeption.EntityConflitExeption;
import com.cursopcv.carroservice.exeption.EntityNotFoundExeption;
import com.cursopcv.carroservice.exeption.EntityNotNullExeption;
import com.cursopcv.carroservice.exeption.PlacaInvalidFormatException;
import com.cursopcv.carroservice.mapper.CarroMapper;
import com.cursopcv.carroservice.model.*;
import com.cursopcv.carroservice.repository.AcessorioRepository;
import com.cursopcv.carroservice.repository.CarroRepository;
import com.cursopcv.carroservice.repository.FabricanteRepository;
import com.cursopcv.carroservice.repository.ModeloCarroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CarroService {

    private final CarroRepository carroRepository;
    private final FabricanteRepository fabricanteRepository;
    private final ModeloCarroRepository modeloCarroRepository;
    private final AcessorioRepository acessorioRepository;

    public List<CarroResponse> listarTodos() {
        return carroRepository.findAll().stream()
                .map(CarroMapper::toResponse)
                .toList();
    }

    public List<CarroResponse> listarDisponiveis() {
        return carroRepository.findByDisponivel(true).stream()
                .map(CarroMapper::toResponse)
                .toList();
    }

    public List<CarroResponse> filtrarPorCategoria(String categoria) {
        Categoria categoriaEnum = Categoria.toStringValue(categoria);
        return carroRepository.findDisponivelPorCategoria(categoriaEnum, true).stream()
                .map(CarroMapper::toResponse)
                .toList();
    }

    public List<CarroResponse> filtrarPorAcessorio(Long acessorioId) {
        acessorioRepository.findById(acessorioId)
                .orElseThrow(() -> new EntityNotFoundExeption("Acessório com o ID " + acessorioId + " não encontrado."));
        return carroRepository.findDisponivelPorAcessorio(acessorioId, true).stream()
                .map(CarroMapper::toResponse)
                .toList();
    }

    public List<CarroResponse> filtrarPorCategoriaEAcessorio(String categoria, Long acessorioId) {
        Categoria categoriaEnum = Categoria.toStringValue(categoria);
        acessorioRepository.findById(acessorioId)
                .orElseThrow(() -> new EntityNotFoundExeption("Acessório com o ID " + acessorioId + " não encontrado."));
        return carroRepository.findDisponivelPorCategoriaEAcessorio(categoriaEnum, acessorioId, true).stream()
                .map(CarroMapper::toResponse)
                .toList();
    }

    public CarroResponse buscarPorId(Long id) {
        return carroRepository.findById(id).map(CarroMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundExeption("Carro com o ID " + id + " não encontrado."));
    }

    public CarroResponse cadastrar(CarroRequest carroRequest) {
        ModeloCarro modeloCarro = modeloCarroRepository.findById(carroRequest.modeloId())
                .orElseThrow(() -> new EntityNotFoundExeption("Modelo de carro com o ID " + carroRequest.modeloId() + " não encontrado."));

        Carro carro = CarroMapper.toEntity(carroRequest, modeloCarro);
        carro.setDisponivel(true);

        boolean placaAntiga = carroRequest.placa().matches("^[A-Z]{3}-[0-9]{4}$");
        boolean placaMercosul = carroRequest.placa().matches("^[A-Z]{3}[0-9][A-Z][0-9]{2}$");

        if (!placaAntiga && !placaMercosul) {
            throw new PlacaInvalidFormatException("Placa com formato inválido. Use o padrão antigo (ABC-1234) ou Mercosul (ABC1D23)");
        }

        if (carroRepository.existsByPlaca(carro.getPlaca())) {
            throw new EntityConflitExeption("Carro com a mesma placa já cadastrada");
        }

        Fabricante fabricante = carro.getModelo().getFabricante();
        if (fabricante == null) {
            throw new EntityNotNullExeption("O Fabricante deve ser informado");
        }
        Fabricante fabricanteBanco = fabricanteRepository.findByNome(fabricante.getNome())
                .orElseThrow(() -> new EntityNotFoundExeption("Fabricante " + fabricante.getNome() + " não cadastrado."));

        carro.getModelo().setFabricante(fabricanteBanco);

        ModeloCarro modelo = carro.getModelo();
        if (modelo == null) {
            throw new EntityNotNullExeption("O Modelo deve ser informado");
        }
        ModeloCarro modeloBanco = modeloCarroRepository.findByDescricao(modelo.getDescricao())
                .orElseThrow(() -> new EntityNotFoundExeption("Modelo " + modelo.getDescricao() + " não cadastrado."));
        carro.setModelo(modeloBanco);

        Set<Acessorio> acessorios = carro.getAcessorios();

        for (Acessorio acessorio : acessorios) {
            Acessorio acessorioBanco;
            acessorioBanco = acessorioRepository.findFirstByDescricaoIgnoreCase(acessorio.getDescricao())
                    .orElseGet(() -> acessorioRepository.save(acessorio));
            carro.setModelo(modeloBanco);

            acessorio.setId(acessorioBanco.getId());
        }

        return CarroMapper.toResponse(carroRepository.save(carro));
    }

    public CarroResponse atualizar(Long id, CarroRequestUpdate carroRequest) {
        Carro carro = carroRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundExeption("Carro com o ID " + id + " não encontrado."));

        if (carroRequest.placa() != null  && !carroRequest.placa().isBlank()) {
            boolean placaAntiga = carroRequest.placa().matches("^[A-Z]{3}[0-9]{4}$");
            boolean placaMercosul = carroRequest.placa().matches("^[A-Z]{3}[0-9][A-Z][0-9]{2}$");

            if (!placaAntiga && !placaMercosul) {
                throw new IllegalArgumentException("Placa inválida. Use o padrão antigo (ABC1234) ou Mercosul (ABC1D23)");
            }

            if (carroRepository.existsByPlaca(carroRequest.placa())) {
                throw new EntityConflitExeption("Carro com a mesma placa já cadastrada");
            }

            carro.setPlaca(carroRequest.placa());
        }

        if (carroRequest.chassi() != null && !carroRequest.chassi().isBlank()) {
            carro.setChassi(carroRequest.chassi());
        }

        if (carroRequest.cor() != null && !carroRequest.cor().isBlank()) {
            carro.setCor(carroRequest.cor());
        }

        if (carroRequest.valorDiaria() != null && carroRequest.valorDiaria().compareTo(BigDecimal.ZERO) > 0) {
            carro.setValorDiaria(carroRequest.valorDiaria());
        }

        if (carroRequest.imagemUrl() != null && !carroRequest.imagemUrl().isBlank()) {
            carro.setImagemUrl(carroRequest.imagemUrl());
        }

        if (carroRequest.disponivel() != null) {
            carro.setDisponivel(carroRequest.disponivel());
        }

        return CarroMapper.toResponse(carroRepository.save(carro));
    }

    public void atualizarDisponibilidade(Long id, Boolean disponivel) {
        Carro carro = carroRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundExeption("Carro com o ID " + id + " não encontrado."));

        if (disponivel != null) {
            carro.setDisponivel(disponivel);
            carroRepository.save(carro);
        }
    }

    public void deletar(Long id) {
        carroRepository.findById(id).orElseThrow(() -> new EntityNotFoundExeption("Carro com o ID " + id + " não encontrado."));

        carroRepository.deleteById(id);
    }
}