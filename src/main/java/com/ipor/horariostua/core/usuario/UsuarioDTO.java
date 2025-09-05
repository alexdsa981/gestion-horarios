package com.ipor.horariostua.core.usuario;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioDTO {
    private Long id;
    private String username;
    private Boolean springUser;
    private String nombre;
    private String rol;
    private Boolean isActive;
}
