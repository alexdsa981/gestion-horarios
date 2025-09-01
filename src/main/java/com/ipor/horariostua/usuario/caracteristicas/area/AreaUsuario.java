package com.ipor.horariostua.usuario.caracteristicas.area;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ipor.horariostua.core.model.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class AreaUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @JsonIgnore
    @OneToMany(mappedBy = "areaUsuario")
    private Set<Usuario> listaUsuarios;
}
