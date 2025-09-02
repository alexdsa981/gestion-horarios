package com.ipor.horariostua.bloquehorario;

import com.ipor.horariostua.bloquehorario.agrupacion.Agrupacion;
import com.ipor.horariostua.bloquehorario.colaborador.Colaborador;
import com.ipor.horariostua.bloquehorario.sede.Sede;
import com.ipor.horariostua.bloquehorario.horariolaboral.HorarioLaboral;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class BloqueHorario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalTime horaInicio;
    private LocalTime horaFin;
    private LocalDate fecha;

    private Integer grupoAnidado;

    @ManyToOne
    @JoinColumn(name = "id_colaborador", nullable = false)
    private Colaborador colaborador;

    @ManyToOne
    @JoinColumn(name = "id_sede", nullable = false)
    private Sede sede;

    @ManyToOne
    @JoinColumn(name = "id_horario_laboral", nullable = false)
    private HorarioLaboral horarioLaboral;

    @ManyToOne
    @JoinColumn(name = "id_agrupacion")
    private Agrupacion agrupacion;

}
