package com.cursopcv.carroservice.controller;

import com.cursopcv.carroservice.dto.carro.CarroRequest;
import com.cursopcv.carroservice.dto.carro.CarroRequestUpdate;
import com.cursopcv.carroservice.dto.carro.CarroResponse;
import com.cursopcv.carroservice.dto.carro.DisponibilidadeRequest;
import com.cursopcv.carroservice.service.CarroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carros")
@RequiredArgsConstructor
public class CarroController {

    private final CarroService service;

    @GetMapping
    public ResponseEntity<List<CarroResponse>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarroResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<List<CarroResponse>> listarDisponiveis() {
        return ResponseEntity.ok(service.listarDisponiveis());
    }

    @GetMapping("/disponiveis/categoria/{categoria}")
    public ResponseEntity<List<CarroResponse>> filtrarPorCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(service.filtrarPorCategoria(categoria));
    }

    @GetMapping("/disponiveis/acessorio/{acessorioId}")
    public ResponseEntity<List<CarroResponse>> filtrarPorAcessorio(@PathVariable Long acessorioId) {
        return ResponseEntity.ok(service.filtrarPorAcessorio(acessorioId));
    }

    @GetMapping("/disponiveis/filtro")
    public ResponseEntity<List<CarroResponse>> filtroAvancado(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Long acessorioId) {

        if (categoria != null && acessorioId != null) {
            return ResponseEntity.ok(service.filtrarPorCategoriaEAcessorio(categoria, acessorioId));
        } else if (categoria != null) {
            return ResponseEntity.ok(service.filtrarPorCategoria(categoria));
        } else if (acessorioId != null) {
            return ResponseEntity.ok(service.filtrarPorAcessorio(acessorioId));
        } else {
            return ResponseEntity.ok(service.listarDisponiveis());
        }
    }

    @PostMapping
    public ResponseEntity<CarroResponse> cadastrar(@RequestBody @Valid CarroRequest carroRequest){
        return ResponseEntity.created(null).body(service.cadastrar(carroRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarroResponse> atualizar(@RequestBody CarroRequestUpdate carroRequest, @PathVariable Long id){
        return ResponseEntity.ok(service.atualizar(id, carroRequest));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id){
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/disponibilidade")
    public ResponseEntity<Void> atualizarDisponibilidade(@PathVariable Long id, @RequestBody DisponibilidadeRequest request) {
        service.atualizarDisponibilidade(id, request.disponivel());
        return ResponseEntity.noContent().build();
    }
}
