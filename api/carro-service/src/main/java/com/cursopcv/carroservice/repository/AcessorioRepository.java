package com.cursopcv.carroservice.repository;

import com.cursopcv.carroservice.model.Acessorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AcessorioRepository extends JpaRepository<Acessorio,Integer> {
    Optional<Acessorio> findFirstByDescricaoIgnoreCase(String descricao);
}
