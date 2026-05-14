package com.cursopcv.carroservice.repository;

import com.cursopcv.carroservice.model.Acessorio;
import com.cursopcv.carroservice.model.Fabricante;
import com.cursopcv.carroservice.model.ModeloCarro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModeloCarroRepository extends JpaRepository<ModeloCarro,Integer> {
    boolean existsByDescricao(String descricao);

    Optional<ModeloCarro> findByDescricao(String descricao);
}
