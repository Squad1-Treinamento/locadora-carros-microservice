package com.cursopcv.aluguelservice.repository;

import com.cursopcv.aluguelservice.model.ApoliceSeguro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApoliceRepository extends JpaRepository<ApoliceSeguro, Integer> {
}