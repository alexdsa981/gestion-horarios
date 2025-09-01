package com.ipor.horariostua.colaborador;

import com.ipor.horariostua.colaborador.dto.ColaboradorSeleccionableDTO;
import com.ipor.horariostua.sede.Sede;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

@Service
public class ColaboradorService {
    @Autowired
    ColaboradorRepository colaboradorRepository;

    public void getModelSelectColaboradoresActivos(Model model) {
        List<ColaboradorSeleccionableDTO> listaDTO = new ArrayList<>();
        for (Colaborador colaborador : colaboradorRepository.findByIsActiveTrue()) {
            ColaboradorSeleccionableDTO dto = new ColaboradorSeleccionableDTO();
            dto.setId(colaborador.getId());
            dto.setNombre(colaborador.getNombre());
            dto.setApellidoP(colaborador.getApellidoP());
            dto.setColor(colaborador.getEventoColor());
            listaDTO.add(dto);
        }
        model.addAttribute("listaSelectColaboradoresActivos", listaDTO);
    }



}
