package com.ipor.horariostua.core.bloquehorario.agrupacion.rangohoras;

import com.ipor.horariostua.core.bloquehorario.agrupacion.Agrupacion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class RangoHorario {
    public RangoHorario(Agrupacion agrupacion, Integer rangoInicio, Integer rangoFin) {
        this.agrupacion = agrupacion;
        this.rangoFin = rangoFin;
        this.rangoInicio = rangoInicio;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer rangoInicio;
    private Integer rangoFin;

    @OneToOne
    @JoinColumn(name = "id_agrupacion", nullable = false, unique = true)
    private Agrupacion agrupacion;
}