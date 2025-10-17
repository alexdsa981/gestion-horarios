package com.ipor.horariostua.core.bloquehorario.agrupacion;

import com.ipor.horariostua.core.bloquehorario.agrupacion.departamento.Departamento;
import com.ipor.horariostua.core.bloquehorario.agrupacion.rangohoras.RangoHorario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Agrupacion {
    public Agrupacion(String nombre, Departamento departamento) {
        this.departamento = departamento;
        this.nombre = nombre;
        this.isActive = true;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "id_departamento", nullable = false)
    private Departamento departamento;

    @OneToOne(mappedBy = "agrupacion")
    private RangoHorario rangoHorario;

    private Boolean isActive;


}
