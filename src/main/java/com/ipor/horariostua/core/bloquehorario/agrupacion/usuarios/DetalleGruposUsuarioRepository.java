package com.ipor.horariostua.core.bloquehorario.agrupacion.usuarios;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DetalleGruposUsuarioRepository extends JpaRepository<DetalleGruposUsuario, Long> {
    List<DetalleGruposUsuario> findByUsuarioIdAndIsActiveTrueOrderByAgrupacionNombre(Long id);
    
    Optional<DetalleGruposUsuario> findByUsuarioIdAndAgrupacionId(Long idSede, Long idAgrupacion);
}
