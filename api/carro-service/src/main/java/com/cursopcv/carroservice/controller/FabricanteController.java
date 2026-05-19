package com.cursopcv.carroservice.controller;

import com.cursopcv.carroservice.dto.fabricante.FabricanteRequest;
import com.cursopcv.carroservice.dto.fabricante.FabricanteResponse;
import com.cursopcv.carroservice.service.FabricanteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/fabricantes")
@RequiredArgsConstructor
public class FabricanteController {

    private final FabricanteService service;

    @GetMapping
    public ResponseEntity<List<FabricanteResponse>> listarTodos(){
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FabricanteResponse> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<FabricanteResponse> buscarPorNome(@RequestParam String nome){
        return ResponseEntity.ok(service.buscarPorNome(nome));
    }

    @PostMapping
    public ResponseEntity<FabricanteResponse> cadastrar(@RequestBody @Valid FabricanteRequest fabricanteRequest){
        return ResponseEntity.created(null).body(service.cadastrar(fabricanteRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FabricanteResponse> atualizar(@RequestBody @Valid FabricanteRequest fabricanteRequest, @PathVariable Long id){
        return ResponseEntity.ok(service.atualizar(id, fabricanteRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<FabricanteResponse> deletar(@PathVariable Long id){
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
