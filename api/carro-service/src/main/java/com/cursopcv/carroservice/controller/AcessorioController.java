package com.cursopcv.carroservice.controller;

import com.cursopcv.carroservice.dto.acessorio.AcessorioRequest;
import com.cursopcv.carroservice.dto.acessorio.AcessorioResponse;
import com.cursopcv.carroservice.service.AcessorioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/acessorios")
@RequiredArgsConstructor
public class AcessorioController {

    private final AcessorioService service;

    @GetMapping
    public ResponseEntity<List<AcessorioResponse>> findAll(){
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AcessorioResponse> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<AcessorioResponse>> buscarPorDescricao(@RequestParam String descricao){
        return ResponseEntity.ok(service.buscarPorDescricao(descricao));
    }

    @PostMapping
    public ResponseEntity<AcessorioResponse> cadastrar(@RequestBody @Valid AcessorioRequest acessorioRequest){
        return ResponseEntity.created(null).body(service.cadastrar(acessorioRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AcessorioResponse> atualizar(@RequestBody @Valid AcessorioRequest request, @PathVariable Long id){
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AcessorioResponse> deletar(@PathVariable Long id){
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
