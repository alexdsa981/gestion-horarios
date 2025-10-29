package com.ipor.horariostua.core.bloquehorario.almuerzo;

import com.ipor.horariostua.core.bloquehorario.BloqueHorario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Almuerzo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalTime horaInicio;
    private LocalTime horaFin;

    @OneToOne(mappedBy = "almuerzo")
    private BloqueHorario bloqueHorario;
}
