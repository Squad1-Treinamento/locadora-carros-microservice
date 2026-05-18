package com.cursopcv.notificationcontracts.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import org.hibernate.validator.constraints.br.CPF;

import java.util.Date;

public record CadastroNotificationRequest(
        @NotBlank
        String nome,

        @CPF
        String cpf,

        @Past
        Date dataNascimento,

        String matricula,

        String numeroCNH,

        @Email
        String email
) {
}