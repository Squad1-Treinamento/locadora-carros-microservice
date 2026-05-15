package com.cursopcv.carroservice.controller;

import com.cursopcv.carroservice.dto.carro.CarroRequest;
import com.cursopcv.carroservice.dto.carro.CarroRequestUpdate;
import com.cursopcv.carroservice.dto.carro.CarroResponse;
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
    public ResponseEntity<List<CarroResponse>> findAll() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("{id}")
    public ResponseEntity<CarroResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<CarroResponse> cadastrar(@RequestBody @Valid CarroRequest carroRequest){
        return ResponseEntity.created(null).body(service.cadastrar(carroRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarroResponse> atualizar(@RequestBody CarroRequestUpdate carroRequest, Long id){
        return ResponseEntity.ok(service.atualizar(id, carroRequest));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id){
        service.deleter(id);
        return ResponseEntity.noContent().build();
    }
}
