package com.ipor.horariostua.bloquehorario.horariolaboral;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HorarioLaboralRepository extends JpaRepository<HorarioLaboral, Long> {
    HorarioLaboral findTopByOrderByFechaCreacionDesc();
}
