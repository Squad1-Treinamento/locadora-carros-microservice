package com.cursopcv.carroservice.controller;

import com.cursopcv.carroservice.dto.modeloCarro.ModeloCarroRequest;
import com.cursopcv.carroservice.dto.modeloCarro.ModeloCarroResponse;
import com.cursopcv.carroservice.service.ModeloCarroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/modelos-carros")
@RequiredArgsConstructor
public class ModeloCarroController {

    private  final ModeloCarroService service;

    @GetMapping
    public ResponseEntity<List<ModeloCarroResponse>> listarTodos(){
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModeloCarroResponse> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ModeloCarroResponse> cadastrar(@RequestBody @Valid ModeloCarroRequest modeloCarroRequest
    ){
        return ResponseEntity.created(null).body(service.cadastrar(modeloCarroRequest
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ModeloCarroResponse> atualizar(@RequestBody ModeloCarroRequest modeloCarroRequest
            , @PathVariable Long id){
        return ResponseEntity.ok(service.atualizar(id, modeloCarroRequest
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ModeloCarroResponse> deletar(@PathVariable Long id){
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
