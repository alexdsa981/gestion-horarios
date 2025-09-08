package com.ipor.horariostua.core.bloquehorario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BloqueHorarioRepository extends JpaRepository<BloqueHorario, Long> {
    @Query("SELECT COALESCE(MAX(b.grupoAnidado), 0) FROM BloqueHorario b")
    Integer findMaxGrupoAnidado();

    List<BloqueHorario> findByGrupoAnidado(Integer grupoAnidado);

    List<BloqueHorario> findByAgrupacionId(Long id);
}
