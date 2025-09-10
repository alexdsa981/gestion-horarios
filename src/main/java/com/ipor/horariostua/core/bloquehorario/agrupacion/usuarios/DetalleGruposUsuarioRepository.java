package com.ipor.horariostua.core.bloquehorario.agrupacion.usuarios;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleGruposUsuarioRepository extends JpaRepository<DetalleGruposUsuario, Long> {
    List<DetalleGruposUsuario> findByUsuarioId(Long id);
}
