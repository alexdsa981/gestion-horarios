package com.ipor.horariostua.core.licencias.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor
public class FechaMotivoLicenciaDTO {
    private LocalDate fecha;
    private String motivo;

    public FechaMotivoLicenciaDTO(LocalDate fecha, String motivo) {
        this.fecha = fecha;
        this.motivo = motivo;
    }
}
