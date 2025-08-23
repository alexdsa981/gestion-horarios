package com.ipor.horariostua.logs;

import com.ipor.horariostua.core.model.LogHorarios;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LogHorarioRepository extends JpaRepository<LogHorarios, Long> {

//    List<LogHorarios> findByFichaPaciente_Id(Long idFicha);

}
