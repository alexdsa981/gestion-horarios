package com.ipor.horariostua.logs.horario;

import com.ipor.horariostua.core.bloquehorario.BloqueHorario;
import com.ipor.horariostua.core.usuario.Usuario;
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
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

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
