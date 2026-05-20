package com.cursopcv.carroservice.repository;

import com.cursopcv.carroservice.model.Acessorio;
import com.cursopcv.carroservice.model.Fabricante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FabricanteRepository extends JpaRepository<Fabricante,Integer> {
    Optional<Fabricante> findById(Long id);
    Optional<Fabricante> findByNome(String nome);
    Optional<Fabricante> findByNomeContainingIgnoreCase(String nome);
    boolean existsByNomeIgnoreCase(String nome);
    void deleteById(Long id);
}
