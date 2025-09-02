package com.ipor.horariostua.bloquehorario.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
public class Agrega_BH_DTO {

    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Long idColaborador;
    private Long idAgrupacion;
    private Long idSede;

}
