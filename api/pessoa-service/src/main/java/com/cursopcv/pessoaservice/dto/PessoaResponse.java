package com.cursopcv.pessoaservice.dto;

import java.util.Date;

public record PessoaResponse(
        Integer id,
        String nome,
        String cpf,
        Date dataNascimento,
        String matricula,
        String numeroCNH,
        String email
) {}
