package com.ipor.horariostua.logs.licencia;

import com.ipor.horariostua.core.bloquehorario.BloqueHorario;
import com.ipor.horariostua.core.licencias.Licencias;
import com.ipor.horariostua.core.usuario.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "log_licencia", indexes = {
        @Index(name = "idx_log_licencia_licenciaid", columnList = "licencia_id"),
        @Index(name = "idx_log_licencia_usuarioid", columnList = "usuario_id"),
        @Index(name = "idx_log_licencia_fecha", columnList = "fecha_hora")
})
public class LogLicencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "licencia_id")
    private Long licenciaId;

    @Column(name = "accion", length = 50, nullable = false)
    private String accion;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "usuario_nombre", length = 200)
    private String usuarioNombre;

    @Lob
    @Column(name = "valor_anterior", columnDefinition = "NVARCHAR(MAX)")
    private String valorAnterior;

    @Lob
    @Column(name = "valor_nuevo", columnDefinition = "NVARCHAR(MAX)")
    private String valorNuevo;

    @Lob
    @Column(name = "descripcion", columnDefinition = "NVARCHAR(MAX)")
    private String descripcion;

    @PrePersist
    public void prePersist() {
        if (this.fechaHora == null) {
            this.fechaHora = LocalDateTime.now();
        }
    }
}
