package com.cursopcv.pessoaservice.repository;

import com.cursopcv.pessoaservice.model.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PessoaRepository extends JpaRepository<Pessoa, Integer> {
    Boolean existsByCpf(String cpf);
    Boolean existsByEmail(String email);
    Optional<Pessoa> findByCpf(String cpf);
}
