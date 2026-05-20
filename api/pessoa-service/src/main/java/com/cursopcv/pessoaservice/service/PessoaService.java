package com.cursopcv.pessoaservice.service;

import com.cursopcv.pessoaservice.model.Pessoa;
import com.cursopcv.pessoaservice.repository.PessoaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PessoaService {
    private final PessoaRepository pessoaRepository;

    public PessoaService(PessoaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }

    public boolean existePorCpf(String cpf) {
        return pessoaRepository.existsByCpf(cpf);
    }

    public boolean existePorEmail(String email) {
        return pessoaRepository.existsByEmail(email);
    }
}
