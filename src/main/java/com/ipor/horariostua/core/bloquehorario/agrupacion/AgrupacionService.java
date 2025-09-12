package com.ipor.horariostua.core.bloquehorario.agrupacion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgrupacionService {
    @Autowired
    AgrupacionRepository agrupacionRepository;

    public Agrupacion getAgrupacionPorId(Long id){
        return agrupacionRepository.findById(id).get();
    }

    public List<Agrupacion> getListaAgrupacion(){
        return agrupacionRepository.findAll();
    }
    public List<Agrupacion> getListaAgrupacionTrue(){
        return agrupacionRepository.findByIsActiveTrue();
    }

}
