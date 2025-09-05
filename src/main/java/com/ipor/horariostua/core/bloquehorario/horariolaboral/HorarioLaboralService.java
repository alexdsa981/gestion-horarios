package com.ipor.horariostua.core.bloquehorario.horariolaboral;

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
