package com.cursopcv.pessoaservice.repository;

import com.cursopcv.pessoaservice.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Integer> {
}
