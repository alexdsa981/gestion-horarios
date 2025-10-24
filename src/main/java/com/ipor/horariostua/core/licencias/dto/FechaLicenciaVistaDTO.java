package com.ipor.horariostua.core.licencias.dto;

import com.ipor.horariostua.core.licencias.LicenciaFecha;
import com.ipor.horariostua.core.licencias.Licencias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class FechaLicenciaVistaDTO {
    private Long idFecha;
    private String colaborador;
    private String tipoLicencia;
    private String fecha;
    private String color;

    public FechaLicenciaVistaDTO(Licencias licencia, LicenciaFecha licenciaFecha, String color) {
        this.idFecha = licenciaFecha.getId();
        this.colaborador = licencia.getColaborador().getNombreCompleto();
        this.tipoLicencia = licencia.getTipoLicencia().getNombre();
        this.fecha = licenciaFecha.getFecha().toString();
        this.color = color;

    }
}