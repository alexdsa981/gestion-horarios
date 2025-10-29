package com.ipor.horariostua.core.bloquehorario.sede.dto;

import com.ipor.horariostua.core.bloquehorario.agrupacion.sedes.DetalleSedeAgrupacion;
import com.ipor.horariostua.core.bloquehorario.sede.Sede;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ListarSedesDTO {

    public ListarSedesDTO(Sede sede, DetalleSedeAgrupacion detalle){
        this.id = sede.getId();
        this.nombre = sede.getNombre();
        if (detalle == null){
            this.isActive = false;
        }else{
            this.isActive = detalle.getIsActive();
        }
    }
    public ListarSedesDTO(Sede sede){
        this.id = sede.getId();
        this.nombre = sede.getNombre();
    }

    private Long id;
    private String nombre;
    private Boolean isActive;
}
