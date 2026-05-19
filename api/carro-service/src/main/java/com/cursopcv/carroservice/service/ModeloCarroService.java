package com.cursopcv.carroservice.service;

import com.cursopcv.carroservice.dto.modeloCarro.ModeloCarroRequest;
import com.cursopcv.carroservice.dto.modeloCarro.ModeloCarroResponse;
import com.cursopcv.carroservice.exeption.EntityConflitExeption;
import com.cursopcv.carroservice.exeption.EntityNotFoundExeption;
import com.cursopcv.carroservice.mapper.ModeloCarroMapper;
import com.cursopcv.carroservice.model.Acessorio;
import com.cursopcv.carroservice.model.Categoria;
import com.cursopcv.carroservice.model.Fabricante;
import com.cursopcv.carroservice.model.ModeloCarro;
import com.cursopcv.carroservice.repository.FabricanteRepository;
import com.cursopcv.carroservice.repository.ModeloCarroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModeloCarroService {

    private final ModeloCarroRepository modeloCarroRepository;
    private final FabricanteRepository fabricanteRepository;


    public List<ModeloCarroResponse> listar() {
        return modeloCarroRepository.findAll().stream()
                .map(ModeloCarroMapper::toResponse)
                .toList();
    }

    public ModeloCarroResponse buscarPorId(Long id) {
        return modeloCarroRepository.findById(id).map(ModeloCarroMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundExeption("Modelo de Carro com o ID " + id + " não encontrado."));
    }


    public List<ModeloCarroResponse> buscarPorDescricao(String descricao) {
        return modeloCarroRepository.findAllByDescricaoContainingIgnoreCase(descricao).stream()
                .map(ModeloCarroMapper::toResponse)
                .toList();
    }

    public ModeloCarroResponse atualizar(Long id, ModeloCarroRequest modeloCarroRequest) {
        ModeloCarro modeloCarro = modeloCarroRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundExeption("Modelo de Carro com o ID " + id + " não encontrado."));

        if (modeloCarroRepository.existsByDescricaoIgnoreCase(modeloCarroRequest.descricao())){
            throw new EntityConflitExeption("Modelo de Carro já cadastrado.");
        }

        if (modeloCarroRequest.descricao() != null && !modeloCarroRequest.descricao().isEmpty()){
            modeloCarro.setDescricao(modeloCarro.getDescricao());
        }

        if (modeloCarroRequest.categoria() != null && !modeloCarroRequest.categoria().isEmpty()){
            modeloCarro.setCategoria(Categoria.toStringValue(modeloCarroRequest.categoria()));
        }

        if (modeloCarroRequest.fabricante() != null && !modeloCarroRequest.fabricante().nome().isEmpty()) {
            fabricanteRepository.findByNome(modeloCarroRequest.fabricante().nome())
                    .ifPresentOrElse(modeloCarro::setFabricante,
                            () -> { throw new EntityNotFoundExeption("Fabricante não encontrado"); });
        }

        return ModeloCarroMapper.toResponse(modeloCarroRepository.save(modeloCarro));
    }

    public ModeloCarroResponse cadastrar(ModeloCarroRequest modeloCarroRequest) {
        if (modeloCarroRepository.existsByDescricaoIgnoreCase(modeloCarroRequest.descricao())){
            throw new EntityConflitExeption("Modelo de Carro já cadastrado.");
        }

        Fabricante fabricante = fabricanteRepository.findByNomeContainingIgnoreCase(modeloCarroRequest.fabricante().nome())
                .orElseThrow(() -> new EntityNotFoundExeption("Fabricante com o nome " + modeloCarroRequest.fabricante().nome() + " não encontrado."));

        ModeloCarro modeloCarro = ModeloCarroMapper.toEntity(modeloCarroRequest, fabricante);

        return ModeloCarroMapper.toResponse(modeloCarroRepository.save(modeloCarro));
    }

    public void deletar(Long id) {
        modeloCarroRepository.findById(id).orElseThrow(() -> new EntityNotFoundExeption("Modelo de Carro com o ID " + id + " não encontrado."));

        modeloCarroRepository.deleteById(id);
    }
}
