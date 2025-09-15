package com.ipor.horariostua.core.bloquehorario.bloquehorarioDTO;

import com.ipor.horariostua.core.bloquehorario.BloqueHorario;
import com.ipor.horariostua.core.bloquehorario.agrupacion.Agrupacion;
import com.ipor.horariostua.core.bloquehorario.agrupacion.colaboradores.DetalleColaboradorAgrupacion;
import com.ipor.horariostua.core.bloquehorario.colaborador.Colaborador;
import com.ipor.horariostua.core.bloquehorario.sede.Sede;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
public class Export_BH_DTO {
    private Long id;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private LocalDate fecha;

    private String nombreColaborador;

    private String nombreSede;

    private String nombreAgrupacion;

    public Export_BH_DTO(BloqueHorario entity) {
        this.id = entity.getId();
        this.horaInicio = entity.getHoraInicio();
        this.horaFin = entity.getHoraFin();
        this.fecha = entity.getFecha();

        Colaborador colab = entity.getColaborador();
        if (colab != null) {
            this.nombreColaborador = colab.getNombreCompleto();

        }

        Sede sede = entity.getSede();
        if (sede != null) {
            this.nombreSede = sede.getNombre();
        }

        Agrupacion agrupacion = entity.getAgrupacion();
        if (agrupacion != null) {
            this.nombreAgrupacion = agrupacion.getNombre();
        }
    }
}
