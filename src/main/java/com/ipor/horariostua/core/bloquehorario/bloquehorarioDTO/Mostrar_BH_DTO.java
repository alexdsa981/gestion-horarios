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
import java.time.format.DateTimeFormatter;

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

    private String horaInicioAlmuerzo;
    private String horaFinAlmuerzo;

    private Boolean isTurnoNoche;

    private static final DateTimeFormatter HORA_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public Mostrar_BH_DTO(BloqueHorario entity, DetalleColaboradorAgrupacion detalle) {
        this.id = entity.getId();
        this.horaInicio = entity.getHoraInicio();
        this.horaFin = entity.getHoraFin();
        this.fecha = entity.getFecha();
        this.grupoAnidado = entity.getGrupoAnidado();

        if (entity.getAlmuerzo() != null){
            this.horaInicioAlmuerzo = entity.getAlmuerzo().getHoraInicio() != null
                    ? entity.getAlmuerzo().getHoraInicio().format(HORA_FORMATTER)
                    : null;
            this.horaFinAlmuerzo = entity.getAlmuerzo().getHoraFin() != null
                    ? entity.getAlmuerzo().getHoraFin().format(HORA_FORMATTER)
                    : null;
        }

        Colaborador colab = entity.getColaborador();
        if (colab != null) {
            this.idColaborador = colab.getId();
            this.nombreColaborador = formatearNombre(colab.getNombreCompleto());

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
        if (entity.getIsTurnoNoche() == null){
            this.isTurnoNoche = false;
        }else{
            this.isTurnoNoche = entity.getIsTurnoNoche();
        }
    }

    private String formatearNombre(String nombreCompleto) {
        if (nombreCompleto == null) return "";
        String[] partes = nombreCompleto.split(",");
        if (partes.length < 2) return nombreCompleto.trim();

        String apellidoPaterno = partes[0].trim().split(" ")[0];
        String nombre1 = partes[1].trim().split(" ")[0];
        return apellidoPaterno + " " + nombre1;
    }


}
