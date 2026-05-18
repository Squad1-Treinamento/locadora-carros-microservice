package com.cursopcv.notificationcontracts.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

public record AluguelNotificationRequest(
        @NotNull
        Pessoa pessoa,
        @NotNull
        Carro carro
) {
    public record Carro(
            Long id,
            String placa,
            String chassi,
            String cor,
            BigDecimal valorDiaria,
            ModeloCarro modelo,
            Set<Acessorio> acessorios
    ) {
        public record ModeloCarro(
                Long id,
                String descricao,
                Categoria categoria,
                FabricanteResponse fabricante
        ) {
            public enum Categoria {
                HATCH_COMPACTO,
                HATCH_MEDIO,
                SEDAN_COMPACTO,
                SEDAN_MEDIO,
                SEDAN_GRANDE,
                MINIVAN,
                ESPORTIVO,
                UTILITARIO_COMERCIAL;
            }
            public record FabricanteResponse(
                    Long id,
                    String nome
            ){}
        }
        public record Acessorio(
                Long id,
                String descricao
        ) {}
    }
    public record Pessoa(
            String nome,
            String cpf,
            Date dataNascimento,
            String matricula,
            String numeroCNH,

            @Email
            String email
    ) {}
}

