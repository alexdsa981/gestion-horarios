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
public class Mostrar_BH_DTO {

    private Long id;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private LocalDate fecha;
    private Integer grupoAnidado;


    private Long idColaborador;
    private String nombreColaborador;

    private Long idSede;
    private String nombreSede;

    private Long idAgrupacion;

    private String color;

    public Mostrar_BH_DTO(BloqueHorario entity, DetalleColaboradorAgrupacion detalle) {
        this.id = entity.getId();
        this.horaInicio = entity.getHoraInicio();
        this.horaFin = entity.getHoraFin();
        this.fecha = entity.getFecha();
        this.grupoAnidado = entity.getGrupoAnidado();

        Colaborador colab = entity.getColaborador();
        if (colab != null) {
            this.idColaborador = colab.getId();
            this.nombreColaborador = colab.getNombreCompleto();

            this.color = detalle.getEventoColor();
        }

        Sede sede = entity.getSede();
        if (sede != null) {
            this.idSede = sede.getId();
            this.nombreSede = sede.getNombre();
        }

        Agrupacion agrupacion = entity.getAgrupacion();
        if (agrupacion != null) {
            this.idAgrupacion = agrupacion.getId();
        }
    }
}
