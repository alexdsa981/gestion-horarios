package com.ipor.horariostua.core.licencias;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LicenciasRepository extends JpaRepository<Licencias, Long> {
    List<Licencias> findByIsActiveTrue();
    List<Licencias> findByIsActiveTrueAndAgrupacionId(Long agrupacionId);
    List<Licencias> findByColaboradorIdAndAgrupacionIdAndIsActiveTrue(Long colaboradorId, Long agrupacionId);
}
