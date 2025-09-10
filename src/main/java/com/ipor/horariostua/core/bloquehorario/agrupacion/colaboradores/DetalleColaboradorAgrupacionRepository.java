package com.ipor.horariostua.core.bloquehorario.agrupacion.colaboradores;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DetalleColaboradorAgrupacionRepository extends JpaRepository<DetalleColaboradorAgrupacion, Long> {

    Optional<DetalleColaboradorAgrupacion> findByColaboradorIdAndAgrupacionId(Long idColaborador, Long idAgrupacion);

    List<DetalleColaboradorAgrupacion> findByAgrupacionIdAndIsActiveTrue(Long idAgrupacion);
}
