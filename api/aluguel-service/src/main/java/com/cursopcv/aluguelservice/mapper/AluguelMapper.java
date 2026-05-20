package com.cursopcv.aluguelservice.mapper;

import com.cursopcv.aluguelservice.dto.*;
import com.cursopcv.aluguelservice.model.Aluguel;
import com.cursopcv.aluguelservice.model.ApoliceSeguro;
import com.cursopcv.aluguelservice.model.StatusAluguel;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

@Component
public class AluguelMapper {

    public ApoliceSeguro toApoliceEntity(ApoliceSeguroRequest request) {
        if (request == null) return null;
        ApoliceSeguro apolice = new ApoliceSeguro();
        apolice.setProtecaoTerceiro(request.protecaoTerceiro());
        apolice.setProtecaoCausasNaturais(request.protecaoCausasNaturais());
        apolice.setProtecaoRoubo(request.protecaoRoubo());
        apolice.setValorFranquia(request.valorFranquia());
        apolice.setCustoApolice(request.custoApolice());
        return apolice;
    }

    public ApoliceSeguroResponse toApoliceResponse(ApoliceSeguro entity) {
        if (entity == null) return null;
        return new ApoliceSeguroResponse(
                entity.getId(),
                entity.getProtecaoTerceiro(),
                entity.getProtecaoCausasNaturais(),
                entity.getProtecaoRoubo(),
                entity.getValorFranquia(),
                entity.getCustoApolice()
        );
    }

    public AluguelResponse toResponse(Aluguel aluguel) {
        if (aluguel == null) return null;
        return new AluguelResponse(
                aluguel.getId(),
                aluguel.getIdPessoa(),
                aluguel.getIdCarro(),
                aluguel.getDataEntrega(),
                aluguel.getDataDevolucao(),
                aluguel.getValorDiaria(),
                aluguel.getQuantidadeDias(),
                aluguel.getValorTotal(),
                aluguel.getStatus(),
                toApoliceResponse(aluguel.getApoliceSeguro())
        );
    }

    public Aluguel toEntity(AluguelRequest request, BigDecimal valorDiaria, Integer quantidadeDias, BigDecimal valorTotal) {
        Aluguel aluguel = new Aluguel();
        aluguel.setIdPessoa(request.idMotorista());
        aluguel.setIdCarro(request.idCarro());
        aluguel.setDataPedido(new Date());
        aluguel.setDataEntrega(request.dataEntrega());
        aluguel.setDataDevolucao(request.dataDevolucao());
        aluguel.setValorDiaria(valorDiaria);
        aluguel.setQuantidadeDias(quantidadeDias);
        aluguel.setValorTotal(valorTotal);
        aluguel.setStatus(StatusAluguel.PENDENTE);
        aluguel.setApoliceSeguro(toApoliceEntity(request.apoliceSeguro()));
        return aluguel;
    }
}
