package com.cursopcv.carroservice.repository;

import com.cursopcv.carroservice.model.Acessorio;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AcessorioRepository extends JpaRepository<Acessorio,Integer> {
    Optional<Acessorio> findById(Long id);
    Optional<Acessorio> findFirstByDescricaoIgnoreCase(String descricao);
    List<Acessorio> findAllByDescricaoContainingIgnoreCase(String descricao);
    boolean existsByDescricaoIgnoreCase(String descricao);
    @Transactional
    void deleteById(Long id);
}
