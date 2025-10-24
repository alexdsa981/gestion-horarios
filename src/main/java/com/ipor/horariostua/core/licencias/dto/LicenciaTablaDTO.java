package com.ipor.horariostua.core.licencias.dto;

import com.ipor.horariostua.core.licencias.Licencias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LicenciaTablaDTO {
    private Long id;
    private String colaborador;
    private String tipoLicencia;
    private int dias;


    public LicenciaTablaDTO(Licencias licencia, int diasDelMes) {
        this.id = licencia.getId();
        this.colaborador = licencia.getColaborador().getNombreCompleto();
        this.tipoLicencia = licencia.getTipoLicencia().getNombre();
        this.dias = diasDelMes;
    }


}