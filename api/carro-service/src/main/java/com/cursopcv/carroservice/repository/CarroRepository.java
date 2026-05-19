package com.cursopcv.carroservice.repository;

import com.cursopcv.carroservice.model.Carro;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import com.cursopcv.carroservice.model.Categoria;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CarroRepository extends JpaRepository<Carro, Long> {
    boolean existsByPlaca(String placa);

    @Transactional
    void deleteById(Long id);

    // Filtrar carros disponíveis
    List<Carro> findByDisponivel(boolean disponivel);

    // Filtrar por categoria e disponibilidade
    @Query("SELECT c FROM carros c WHERE c.modelo.categoria = :categoria AND c.disponivel = :disponivel")
    List<Carro> findDisponivelPorCategoria(@Param("categoria") Categoria categoria, @Param("disponivel") boolean disponivel);

    // Filtrar por acessório e disponibilidade
    @Query("SELECT DISTINCT c FROM carros c JOIN c.acessorios a WHERE a.id = :acessorioId AND c.disponivel = :disponivel")
    List<Carro> findDisponivelPorAcessorio(@Param("acessorioId") Long acessorioId, @Param("disponivel") boolean disponivel);

    // Filtro combinado: categoria e acessório
    @Query("SELECT DISTINCT c FROM carros c WHERE c.modelo.categoria = :categoria AND c.disponivel = :disponivel AND c.id IN (SELECT ca.id FROM carros ca JOIN ca.acessorios a WHERE a.id = :acessorioId)")
    List<Carro> findDisponivelPorCategoriaEAcessorio(@Param("categoria") Categoria categoria, @Param("acessorioId") Long acessorioId, @Param("disponivel") boolean disponivel);
}


