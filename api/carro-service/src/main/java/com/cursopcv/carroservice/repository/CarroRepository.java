package com.cursopcv.carroservice.repository;

import com.cursopcv.carroservice.model.Carro;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarroRepository extends JpaRepository<Carro,Integer> {
    boolean findByPlaca(String placa);
    boolean existsByPlaca(String placa);
    Optional<Carro> findById(Long id);
    @Transactional
    void deleteById(Long id);


}
