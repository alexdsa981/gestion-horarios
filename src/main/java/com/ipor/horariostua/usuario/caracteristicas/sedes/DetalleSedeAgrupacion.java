package com.ipor.horariostua.usuario.caracteristicas.sedes;

import com.ipor.horariostua.bloquehorario.agrupacion.Agrupacion;
import com.ipor.horariostua.usuario.Usuario;
import com.ipor.horariostua.bloquehorario.sede.Sede;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class DetalleSedeAgrupacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "id_sede", nullable = false)
    private Sede sede;
    @ManyToOne
    @JoinColumn(name = "id_agrupacion", nullable = false)
    private Agrupacion agrupacion;
}
