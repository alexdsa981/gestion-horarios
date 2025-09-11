package com.ipor.horariostua.core.bloquehorario.agrupacion.sedes;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DetalleSedeAgrupacionRepository extends JpaRepository<DetalleSedeAgrupacion, Long> {
    Optional<DetalleSedeAgrupacion> findBySedeIdAndAgrupacionId(Long idSede, Long idAgrupacion);
    List<DetalleSedeAgrupacion> findByAgrupacionIdAndIsActiveTrue(Long idAgrupacion);
}
