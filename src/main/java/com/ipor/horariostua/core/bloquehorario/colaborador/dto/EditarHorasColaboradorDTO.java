package com.ipor.horariostua.core.bloquehorario.colaborador.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EditarHorasColaboradorDTO {
    private Long colaboradorId;
    private Long agrupacionId;
    private Integer horas;
}
