package com.cursopcv.carroservice.repository;

import com.cursopcv.carroservice.model.Carro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarroRepository extends JpaRepository<Carro,Integer> {
}
