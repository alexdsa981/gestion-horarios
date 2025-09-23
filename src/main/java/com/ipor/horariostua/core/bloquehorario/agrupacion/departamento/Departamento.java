package com.ipor.horariostua.core.bloquehorario.agrupacion.departamento;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Departamento {
    public Departamento(String nombre) {
        this.nombre = nombre;
        this.isActive = true;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private Boolean isActive;

}
