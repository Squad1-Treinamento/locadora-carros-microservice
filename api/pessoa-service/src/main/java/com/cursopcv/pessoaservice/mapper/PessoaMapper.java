package com.cursopcv.pessoaservice.mapper;

import com.cursopcv.pessoaservice.dto.PessoaRequest;
import com.cursopcv.pessoaservice.dto.PessoaResponse;
import com.cursopcv.pessoaservice.model.Funcionario;
import com.cursopcv.pessoaservice.model.Motorista;
import com.cursopcv.pessoaservice.model.Pessoa;
import org.springframework.stereotype.Component;

@Component
public class PessoaMapper {
    public Pessoa toEntity(PessoaRequest request) {
        if (request == null) return null;

        Pessoa pessoa;

        if (request.numeroCNH() != null && !request.numeroCNH().isBlank()) {
            Motorista motorista = new Motorista();
            motorista.setNumeroCNH(request.numeroCNH());
            pessoa = motorista;
        } else if (request.matricula() != null && !request.matricula().isBlank()) {
            Funcionario funcionario = new Funcionario();
            funcionario.setMatricula(request.matricula());
            pessoa = funcionario;
        } else if (request.email() != null && !request.email().isBlank()) {
            Motorista motorista = new Motorista();
            motorista.setEmail(request.email());
            pessoa = motorista;
        } else {
            throw new IllegalArgumentException("É necessário informar CNH ou Matrícula.");
        }

        pessoa.setNome(request.nome());
        pessoa.setCpf(request.cpf());
        pessoa.setDataNascimento(request.dataNascimento());
        pessoa.setEmail(request.email());
        pessoa.setSexo(request.sexo());

        return pessoa;
    }

    public PessoaResponse toResponse(Pessoa entity) {
        if (entity == null) return null;
        String matricula = null;
        String numeroCNH = null;

        if (entity instanceof Motorista motorista) {
            numeroCNH = motorista.getNumeroCNH();
        } else if (entity instanceof Funcionario funcionario) {
            matricula = funcionario.getMatricula();
        }

        return new PessoaResponse(
                entity.getId(),
                entity.getNome(),
                entity.getCpf(),
                entity.getDataNascimento(),
                matricula,
                numeroCNH,
                entity.getEmail(),
                entity.getSexo()
        );
    }
}
