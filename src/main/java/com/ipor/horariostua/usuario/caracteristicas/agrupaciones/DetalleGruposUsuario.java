package com.ipor.horariostua.usuario.caracteristicas.agrupaciones;

import com.ipor.horariostua.bloquehorario.agrupacion.Agrupacion;
import com.ipor.horariostua.usuario.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class DetalleGruposUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "id_agrupacion", nullable = false)
    private Agrupacion agrupacion;
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
}
