package com.cursopcv.pessoaservice.controller;

import com.cursopcv.pessoaservice.dto.PessoaRequest;
import com.cursopcv.pessoaservice.dto.PessoaResponse;
import com.cursopcv.pessoaservice.mapper.PessoaMapper;
import com.cursopcv.pessoaservice.model.Pessoa;
import com.cursopcv.pessoaservice.service.MotoristaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/motoristas")
public class MotoristaController {
    @Autowired
    private MotoristaService motoristaService;

    @PostMapping
    public ResponseEntity<PessoaResponse> cadastrar (@RequestBody PessoaRequest request) {
        PessoaResponse cadastrado = motoristaService.cadastrar(request);

        return ResponseEntity.status(201).body(cadastrado);
    }

    @GetMapping("{id}")
    public ResponseEntity<PessoaResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(motoristaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PessoaResponse> atualizar(@PathVariable Integer id, @RequestBody PessoaRequest request) {
        return ResponseEntity.ok(motoristaService.atualizarPorId(id, request));
    }
}
