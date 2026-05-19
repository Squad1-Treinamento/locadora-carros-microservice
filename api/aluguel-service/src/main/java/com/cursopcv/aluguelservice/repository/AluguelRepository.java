package com.cursopcv.aluguelservice.repository;

import com.cursopcv.aluguelservice.model.Aluguel;
import com.cursopcv.aluguelservice.model.StatusAluguel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface AluguelRepository extends JpaRepository<Aluguel, Integer> {

    List<Aluguel> findByIdPessoa(Integer idPessoa);
    boolean existsByIdCarroAndStatusAndDataEntregaLessThanEqualAndDataDevolucaoGreaterThanEqual(
            Long idCarro, StatusAluguel status, Date dataEntrega, Date dataDevolucao);
}