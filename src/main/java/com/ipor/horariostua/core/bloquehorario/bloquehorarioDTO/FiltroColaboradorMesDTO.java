package com.ipor.horariostua.core.bloquehorario.bloquehorarioDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class FiltroColaboradorMesDTO {
    private Long colaboradorId;
    private Long agrupacionId;
    private LocalDate inicioMes;
    private LocalDate finMes;
}