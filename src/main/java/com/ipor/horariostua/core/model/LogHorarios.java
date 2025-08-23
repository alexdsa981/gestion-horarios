package com.ipor.horariostua.core.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class LogHorarios {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fechaHora;

    @ManyToOne
    @JoinColumn(name = "id_bloque_horario", nullable = false)
    private BloqueHorario bloqueHorario;

    @ManyToOne
    @JoinColumn(name = "id_usuario_creador", nullable = false)
    private Usuario usuarioCreador;

    private String accion;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String valorAnterior;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String valorNuevo;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String descripcion; // Opcional

    @PrePersist
    public void prePersist() {
        this.fechaHora = LocalDateTime.now();
    }
}
