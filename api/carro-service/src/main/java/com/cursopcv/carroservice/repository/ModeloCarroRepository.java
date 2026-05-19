package com.cursopcv.carroservice.repository;

import com.cursopcv.carroservice.model.Acessorio;
import com.cursopcv.carroservice.model.Fabricante;
import com.cursopcv.carroservice.model.ModeloCarro;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ModeloCarroRepository extends JpaRepository<ModeloCarro, Long> {
    Optional<ModeloCarro> findById(Long id);
    Optional<ModeloCarro> findByDescricao(String descricao);
    List<ModeloCarro> findAllByDescricaoContainingIgnoreCase(String descricao);
    boolean existsByDescricaoIgnoreCase(String descricao);
    @Transactional
    void deleteById(Long id);
}
