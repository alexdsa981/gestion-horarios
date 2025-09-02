package com.ipor.horariostua.bloquehorario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BloqueHorarioService {
    @Autowired
    BloqueHorarioRepository bloqueHorarioRepository;

    public BloqueHorario save(BloqueHorario bloqueHorario){
        bloqueHorarioRepository.save(bloqueHorario);
        return bloqueHorario;
    }
}
