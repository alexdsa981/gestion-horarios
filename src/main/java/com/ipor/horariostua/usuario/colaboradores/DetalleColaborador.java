package com.ipor.horariostua.usuario.colaboradores;

import com.ipor.horariostua.bloquehorario.colaborador.Colaborador;
import com.ipor.horariostua.usuario.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class DetalleColaborador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_colaborador", nullable = false)
    private Colaborador colaborador;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
}
