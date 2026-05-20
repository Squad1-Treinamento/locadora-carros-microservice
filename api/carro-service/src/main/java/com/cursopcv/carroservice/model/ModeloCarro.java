package com.cursopcv.carroservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "modelos_carros")
public class ModeloCarro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String descricao;
    @Enumerated(EnumType.STRING)
    private Categoria categoria;
    @ManyToOne
    @JoinColumn(name = "fabricante_id")
    private Fabricante fabricante;
    @OneToMany(mappedBy = "modelo", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Carro> carros;
}
