package com.ipor.horariostua.core.bloquehorario.bloquehorarioDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
public class Recibido_BH_DTO {

    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Long idColaborador;
    private Long idAgrupacion;
    private Long idSede;

}
