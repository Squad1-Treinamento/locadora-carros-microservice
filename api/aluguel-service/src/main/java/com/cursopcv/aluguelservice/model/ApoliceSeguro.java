package com.cursopcv.aluguelservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "apolices_seguro")
public class ApoliceSeguro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Boolean protecaoTerceiro;
    private Boolean protecaoCausasNaturais;
    private Boolean protecaoRoubo;
    private BigDecimal valorFranquia;
}