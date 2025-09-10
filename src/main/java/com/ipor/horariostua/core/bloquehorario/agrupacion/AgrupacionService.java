package com.ipor.horariostua.core.bloquehorario.agrupacion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgrupacionService {
    @Autowired
    AgrupacionRepository agrupacionRepository;

    public Agrupacion getAgrupacionPorId(Long id){
        return agrupacionRepository.findById(id).get();
    }

}
