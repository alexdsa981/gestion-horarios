package com.ipor.horariostua.core.bloquehorario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface BloqueHorarioRepository extends JpaRepository<BloqueHorario, Long> {
    @Query("SELECT COALESCE(MAX(b.grupoAnidado), 0) FROM BloqueHorario b")
    Integer findMaxGrupoAnidado();

    List<BloqueHorario> findByGrupoAnidado(Integer grupoAnidado);

    List<BloqueHorario> findByAgrupacionId(Long id);

    List<BloqueHorario> findByFechaBetweenAndSedeId(LocalDate desde, LocalDate hasta, Long sedeId);

    List<BloqueHorario> findByAgrupacionIdAndFechaBetween(Long agrupacionId, LocalDate desde, LocalDate hasta);

    List<BloqueHorario> findByAgrupacionIdAndFecha(Long agrupacionId, LocalDate fecha);

    List<BloqueHorario> findByColaboradorIdAndFecha(Long idColaborador, LocalDate fecha);

}
