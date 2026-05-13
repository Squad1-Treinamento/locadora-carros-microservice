package com.cursopcv.pessoaservice.dto;

import com.cursopcv.pessoaservice.model.Sexo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.br.CPF;

import java.util.Date;

public record PessoaRequest(
        @NotBlank String nome, @CPF String cpf, @NotNull Date dataNascimento, String matricula, String numeroCNH, Sexo sexo
) {
}
