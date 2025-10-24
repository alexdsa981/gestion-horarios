package com.ipor.horariostua.core.bloquehorario.colaborador.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HorasMensualesColaboradorDTO {

    private Long colaboradorId;
    private String nombreCompleto;
    private Double horasTotales;
    private Integer horasMensuales;
}
