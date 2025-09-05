package com.ipor.horariostua.core.usuario.caracteristicas.rol;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolUsuarioService {
    @Autowired
    RolUsuarioRepository rolUsuarioRepository;

    public List<RolUsuario> getLista(){
        return rolUsuarioRepository.findAll();
    }


}
