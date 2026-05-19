package com.cursopcv.carroservice.service;

import com.cursopcv.carroservice.dto.acessorio.AcessorioRequest;
import com.cursopcv.carroservice.dto.acessorio.AcessorioResponse;
import com.cursopcv.carroservice.exeption.EntityConflitExeption;
import com.cursopcv.carroservice.exeption.EntityNotFoundExeption;
import com.cursopcv.carroservice.mapper.AcessorioMapper;
import com.cursopcv.carroservice.model.Acessorio;
import com.cursopcv.carroservice.repository.AcessorioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AcessorioService {

    private final AcessorioRepository repository;

    public List<AcessorioResponse> listar() {
        return repository.findAll().stream()
                .map(AcessorioMapper::toResponse)
                .toList();
    }

    public AcessorioResponse buscarPorId(Long id) {
        return repository.findById(id).map(AcessorioMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundExeption("Acessório com o ID " + id + " não encontrado."));
    }


    public List<AcessorioResponse> buscarPorDescricao(String descricao) {
        return repository.findAllByDescricaoContainingIgnoreCase(descricao).stream()
                .map(AcessorioMapper::toResponse)
                .toList();
    }

    public AcessorioResponse atualizar(Long id, AcessorioRequest request) {
        Acessorio acessorio = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundExeption("Acessorio com o ID " + id + " não encontrado."));

        if (repository.existsByDescricaoIgnoreCase(request.descricao())){
            throw new EntityConflitExeption("Acessório já cadastrado.");
        }

        acessorio.setDescricao(acessorio.getDescricao());

        return AcessorioMapper.toResponse(repository.save(acessorio));
    }

    public AcessorioResponse cadastrar(AcessorioRequest acessorioRequest) {
        if (repository.existsByDescricaoIgnoreCase(acessorioRequest.descricao())){
            throw new EntityConflitExeption("Acessório já cadastrado.");
        }

        Acessorio acessorio = AcessorioMapper.toEntity(acessorioRequest);

        return AcessorioMapper.toResponse(repository.save(acessorio));
    }

    public void deletar(Long id) {
        repository.findById(id).orElseThrow(() -> new EntityNotFoundExeption("Acessório com o ID " + id + " não encontrado."));

        repository.deleteById(id);
    }
}
