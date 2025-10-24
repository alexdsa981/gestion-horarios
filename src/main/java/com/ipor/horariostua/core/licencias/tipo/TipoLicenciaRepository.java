package com.ipor.horariostua.core.licencias.tipo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TipoLicenciaRepository extends JpaRepository<TipoLicencia, Long> {
    List<TipoLicencia> findByIsActiveTrue();
}
