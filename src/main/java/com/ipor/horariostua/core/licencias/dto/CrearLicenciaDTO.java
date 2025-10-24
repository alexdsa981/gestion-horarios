package com.ipor.horariostua.core.licencias.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CrearLicenciaDTO {
    private Long colaboradorId;
    private Long motivoId;
    private List<String> fechas; // Formato "yyyy-MM-dd"
}