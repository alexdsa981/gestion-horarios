package com.ipor.horariostua.core.bloquehorario.colaborador.dto;

import com.ipor.horariostua.core.bloquehorario.agrupacion.colaboradores.DetalleColaboradorAgrupacion;
import com.ipor.horariostua.core.bloquehorario.colaborador.Colaborador;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ListarColaboradoresDTO {
    public ListarColaboradoresDTO(Colaborador colaborador, DetalleColaboradorAgrupacion detalleColaboradorAgrupacion){
        this.id = colaborador.getId();
        this.nombreCompleto = colaborador.getNombreCompleto();
        this.color = detalleColaboradorAgrupacion.getEventoColor();
        this.isActive = detalleColaboradorAgrupacion.getIsActive();
    }
    private Long id;
    private String nombreCompleto;
    private String color;
    private Boolean isActive;
}
