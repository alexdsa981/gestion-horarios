package com.ipor.horariostua.core.bloquehorario.colaborador;

import com.ipor.horariostua.core.bloquehorario.colaborador.dto.ColaboradorSeleccionableDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class ColaboradorService {
    @Autowired
    ColaboradorRepository colaboradorRepository;

    public Colaborador save(Colaborador colaborador){
        return colaboradorRepository.save(colaborador);
    }

    public List<Colaborador> getListarTodo(){
        return colaboradorRepository.findAll();
    }

    public Boolean existeEnBD(Long id){
        if (colaboradorRepository.findById(id).isPresent()){
            return true;
        }else{
            return false;
        }
    }

    public void getModelSelectColaboradoresActivos(Model model) {
        List<ColaboradorSeleccionableDTO> listaDTO = new ArrayList<>();
        for (Colaborador colaborador : colaboradorRepository.findByIsActiveTrue()) {
            ColaboradorSeleccionableDTO dto = new ColaboradorSeleccionableDTO();
            dto.setId(colaborador.getId());
            dto.setNombreCompleto(colaborador.getNombreCompleto());
            dto.setColor(colaborador.getEventoColor());
            listaDTO.add(dto);
        }
        model.addAttribute("listaSelectColaboradoresActivos", listaDTO);
    }

    public Colaborador getColaboradorPorId(Long id){
        return colaboradorRepository.findById(id).get();
    }

    public String getRandomColorHex() {
        Random rand = new Random();
        int r, g, b;
        do {
            r = rand.nextInt(256);
            g = rand.nextInt(256);
            b = rand.nextInt(256);
            // Evitar colores oscuros cerca del negro (todos menores a 40)
        } while (r < 40 && g < 40 && b < 40);

        // Formato hexadecimal
        return String.format("#%02X%02X%02X", r, g, b);
    }
}
