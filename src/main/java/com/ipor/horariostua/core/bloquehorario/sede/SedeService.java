package com.ipor.horariostua.core.bloquehorario.sede;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;

@Service
public class SedeService {
    @Autowired
    SedeRepository sedeRepository;

    public Model getModelSedesActivas(Model model) {
        List<Sede> sedes = sedeRepository.findByIsActiveTrue();
        model.addAttribute("listaSedesActivas", sedes);
        return model;
    }
    public List<Sede> getSelectSedes(){
        return sedeRepository.findByIsActiveTrue();
    }

    public Sede getSedePorId(Long id){
        return sedeRepository.findById(id).get();
    }
}
