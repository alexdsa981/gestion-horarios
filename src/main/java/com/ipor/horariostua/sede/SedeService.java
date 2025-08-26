package com.ipor.horariostua.sede;

import com.ipor.horariostua.colaborador.Colaborador;
import com.ipor.horariostua.colaborador.ColaboradorRepository;
import com.ipor.horariostua.colaborador.dto.ColaboradorSeleccionableDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SedeService {
    @Autowired
    SedeRepository sedeRepository;

    public List<Sede> getSelectSedes(){
        return sedeRepository.findByIsActiveTrue();
    }
}
