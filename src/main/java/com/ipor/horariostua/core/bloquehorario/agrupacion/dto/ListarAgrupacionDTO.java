package com.ipor.horariostua.core.bloquehorario.agrupacion.dto;

import com.ipor.horariostua.core.bloquehorario.agrupacion.Agrupacion;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ListarAgrupacionDTO {
    public ListarAgrupacionDTO(Agrupacion agrupacion) {
        this.id = agrupacion.getId();
        this.nombre = agrupacion.getNombre();
        this.isActive = agrupacion.getIsActive();
        this.idDepartamento = agrupacion.getDepartamento().getId();
    }

    Long id;
    Long idDepartamento;
    String nombre;
    Boolean isActive;
}
