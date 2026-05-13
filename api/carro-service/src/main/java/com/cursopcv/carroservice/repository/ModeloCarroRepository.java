package com.cursopcv.carroservice.repository;

import com.cursopcv.carroservice.model.Acessorio;
import com.cursopcv.carroservice.model.Fabricante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModeloCarroRepository extends JpaRepository<Fabricante,Integer> {
}
