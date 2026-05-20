package com.cursopcv.carroservice.service;

import com.cursopcv.carroservice.dto.fabricante.FabricanteRequest;
import com.cursopcv.carroservice.dto.fabricante.FabricanteResponse;
import com.cursopcv.carroservice.exeption.EntityConflitExeption;
import com.cursopcv.carroservice.exeption.EntityNotFoundExeption;
import com.cursopcv.carroservice.mapper.FabricanteMapper;
import com.cursopcv.carroservice.model.Fabricante;
import com.cursopcv.carroservice.repository.FabricanteRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FabricanteService {

    private final FabricanteRepository repository;

    public List<FabricanteResponse> listar() {
        return repository.findAll().stream()
                .map(FabricanteMapper::toResponse)
                .toList();
    }

    public FabricanteResponse buscarPorId(Long id){
        return repository.findById(id).map(FabricanteMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundExeption("Fabricante com o ID " + id + " não encontrado."));
    }

    public FabricanteResponse buscarPorNome(String nome){
        return repository.findByNomeContainingIgnoreCase(nome).map(FabricanteMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundExeption("Fabricante com o nome " + nome +" não encontrado."));
    }

    public FabricanteResponse cadastrar(@Valid FabricanteRequest fabricanteRequest) {
        if (repository.existsByNomeIgnoreCase(fabricanteRequest.nome())){
            throw new EntityConflitExeption("Fabricante já cadastrado.");
        }

        Fabricante fabricante = FabricanteMapper.toEntity(fabricanteRequest);

        return FabricanteMapper.toResponse(repository.save(fabricante));
    }

    public FabricanteResponse atualizar(Long id, @Valid FabricanteRequest fabricanteRequest) {
        Fabricante fabricante = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundExeption("Fabricante com o ID " + id + " não encontrado."));

        if (repository.existsByNomeIgnoreCase(fabricanteRequest.nome())){
            throw new EntityConflitExeption("Fabricante já cadastrado.");
        }

        fabricante.setNome(fabricanteRequest.nome());

        return FabricanteMapper.toResponse(repository.save(fabricante));
    }

    public void deletar(Long id) {
        repository.findById(id).orElseThrow(() -> new EntityNotFoundExeption("Fabricante com o ID " + id + " não encontrado."));

        repository.deleteById(id);
    }
}
