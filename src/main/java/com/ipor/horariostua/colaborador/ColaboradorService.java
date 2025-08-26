package com.ipor.horariostua.colaborador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColaboradorService {
    @Autowired
    ColaboradorRepository colaboradorRepository;

    public List<Colaborador> getListaColaboradores(){
        return colaboradorRepository.findByIsActiveTrue();
    }
}
