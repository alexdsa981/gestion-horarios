package com.ipor.horariostua.core.licencias.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter @NoArgsConstructor
public class FechasLicenciaColaboradorDTO {
    private Long colaboradorId;
    private String nombreColaborador;
    private List<FechaMotivoLicenciaDTO> fechas; // Cambia a lista de objetos

    public FechasLicenciaColaboradorDTO(Long colaboradorId, String nombreColaborador, List<FechaMotivoLicenciaDTO> fechas) {
        this.colaboradorId = colaboradorId;
        this.nombreColaborador = nombreColaborador;
        this.fechas = fechas;
    }
}