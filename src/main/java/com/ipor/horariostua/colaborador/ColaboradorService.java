package com.ipor.horariostua.colaborador;

import com.ipor.horariostua.colaborador.dto.ColaboradorSeleccionableDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ColaboradorService {
    @Autowired
    ColaboradorRepository colaboradorRepository;

    public List<ColaboradorSeleccionableDTO> getSelectColaboradores(){
        List<ColaboradorSeleccionableDTO> listaDTO = new ArrayList<>();
        for (Colaborador colaborador : colaboradorRepository.findByIsActiveTrue()){
            ColaboradorSeleccionableDTO dto = new ColaboradorSeleccionableDTO();
            dto.setId(colaborador.getId());
            dto.setNombreCompleto(colaborador.getNombreCompleto());
            listaDTO.add(dto);
        }
        return listaDTO;
    }
}
