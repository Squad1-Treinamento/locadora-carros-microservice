package com.cursopcv.pessoaservice.dto;

import com.cursopcv.pessoaservice.model.Sexo;

import java.util.Date;

public record PessoaResponse(
        Integer id,
        String nome,
        String cpf,
        Date dataNascimento,
        String matricula,
        String numeroCNH,
        String email,
        Sexo sexo
) {}
