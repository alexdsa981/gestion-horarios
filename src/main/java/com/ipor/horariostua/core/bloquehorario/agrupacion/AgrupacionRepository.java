package com.ipor.horariostua.core.bloquehorario.agrupacion;

import com.ipor.horariostua.core.usuario.caracteristicas.rol.RolUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgrupacionRepository extends JpaRepository<Agrupacion, Long> {
    List<Agrupacion> findByIsActiveTrue();

    List<Agrupacion> findByDepartamentoId(Long idDepartamento);
}
