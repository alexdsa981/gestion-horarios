package com.ipor.horariostua.core.bloquehorario.agrupacion.rangohoras.dto;

import com.ipor.horariostua.core.bloquehorario.agrupacion.rangohoras.RangoHorario;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RangoHorarioDTO {
    private Long id;
    private Integer rangoInicio;
    private Integer rangoFin;

    public RangoHorarioDTO(RangoHorario rangoHorario) {
        this.id = rangoHorario.getId();
        this.rangoInicio = rangoHorario.getRangoInicio();
        this.rangoFin = rangoHorario.getRangoFin();
    }

}