package com.cursopcv.aluguelservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "alugueis")
public class Aluguel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer idPessoa;
    private Long idCarro;
    private Date dataPedido;
    private Date dataEntrega;
    private Date dataDevolucao;
    private BigDecimal valorDiaria;
    private Integer quantidadeDias;
    private BigDecimal valorTotal;
    @Enumerated(EnumType.STRING)
    private StatusAluguel status;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "apolice_seguro_id")
    private ApoliceSeguro apoliceSeguro;
}