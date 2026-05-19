package com.cursopcv.carroservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "acessorios")
@Entity(name = "carros")
public class Carro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String placa;
    private String chassi;
    private String cor;
    private BigDecimal valorDiaria;
    @Column(columnDefinition = "TEXT")
    private String imagemUrl;
    private boolean disponivel;
    @ManyToOne
    @JoinColumn(name = "modelo_id")
    private ModeloCarro modelo;
    @ManyToMany
    @JoinTable(
            name = "carro_acessorio",
            joinColumns = @JoinColumn(name = "carro_id"),
            inverseJoinColumns = @JoinColumn(name = "acessorio_id")
    )
    private Set<Acessorio> acessorios = new HashSet<>();
}
