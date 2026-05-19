package com.cursopcv.aluguelservice.controller;

import com.cursopcv.aluguelservice.dto.*;
import com.cursopcv.aluguelservice.service.AluguelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alugueis")
@RequiredArgsConstructor
public class AluguelController {

    private final AluguelService aluguelService;

    @GetMapping
    public ResponseEntity<List<AluguelResponse>> listarTodos() {
        return ResponseEntity.ok(aluguelService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AluguelResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(aluguelService.buscarPorId(id));
    }

    @GetMapping("/motorista/{idMotorista}")
    public ResponseEntity<List<AluguelResponse>> listarPorMotorista(@PathVariable Integer idMotorista) {
        return ResponseEntity.ok(aluguelService.listarPorMotorista(idMotorista));
    }

    @PostMapping
    public ResponseEntity<AluguelResponse> solicitarAluguel(@RequestBody @Valid AluguelRequest request) {
        AluguelResponse response = aluguelService.solicitarAluguel(request);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{id}/resumo")
    public ResponseEntity<ResumoAluguelResponse> resumo(@PathVariable Integer id) {
        return ResponseEntity.ok(aluguelService.obterResumo(id));
    }

    @PostMapping("/{id}/checkout")
    public ResponseEntity<CheckoutResponse> efetivarAluguel(
            @PathVariable Integer id,
            @RequestBody @Valid PagamentoRequest pagamentoRequest) {
        return ResponseEntity.ok(aluguelService.efetivarAluguel(id, pagamentoRequest));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<AluguelResponse> cancelarAluguel(@PathVariable Integer id) {
        return ResponseEntity.ok(aluguelService.cancelarAluguel(id));
    }

    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<AluguelResponse> finalizarAluguel(@PathVariable Integer id) {
        return ResponseEntity.ok(aluguelService.finalizarAluguel(id));
    }
}
