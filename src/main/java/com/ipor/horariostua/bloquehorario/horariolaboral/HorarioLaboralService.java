package com.ipor.horariostua.bloquehorario.horariolaboral;

import com.ipor.horariostua.bloquehorario.BloqueHorario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HorarioLaboralService {

    @Autowired
    HorarioLaboralRepository horarioLaboralRepository;

    public HorarioLaboral getUltimoHorarioLaboral(){
        return horarioLaboralRepository.findTopByOrderByFechaCreacionDesc();
    }
}
