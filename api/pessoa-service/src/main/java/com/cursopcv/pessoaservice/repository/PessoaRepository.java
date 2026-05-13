package com.cursopcv.pessoaservice.repository;

import com.cursopcv.pessoaservice.model.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PessoaRepository extends JpaRepository<Pessoa, Integer> {
}
