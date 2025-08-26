package com.ipor.horariostua.core.repository;

import com.ipor.horariostua.core.model.RolUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolUsuarioRepository extends JpaRepository<RolUsuario, Long> {
    RolUsuario findByNombre(String nombre);
}
