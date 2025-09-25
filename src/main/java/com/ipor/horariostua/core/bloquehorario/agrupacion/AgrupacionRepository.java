package com.ipor.horariostua.core.bloquehorario.agrupacion;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgrupacionRepository extends JpaRepository<Agrupacion, Long> {

    List<Agrupacion> findAllByOrderByNombre();

    List<Agrupacion> findByIsActiveTrueOrderByNombre();

    List<Agrupacion> findByDepartamentoIdOrderByNombre(Long idDepartamento);

    List<Agrupacion> findByDepartamentoIdAndIsActiveTrueOrderByNombre(Long idDepartamento);

}
