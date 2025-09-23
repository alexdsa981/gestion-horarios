package com.ipor.horariostua.core.bloquehorario.agrupacion.departamento.dto;

import com.ipor.horariostua.core.bloquehorario.agrupacion.Agrupacion;
import com.ipor.horariostua.core.bloquehorario.agrupacion.departamento.Departamento;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ListarDepartamentoDTO {
    public ListarDepartamentoDTO(Departamento departamento) {
        this.id = departamento.getId();
        this.nombre = departamento.getNombre();
        this.isActive = departamento.getIsActive();
    }

    Long id;
    String nombre;
   Boolean isActive;
}
