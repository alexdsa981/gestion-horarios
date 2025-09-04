package com.ipor.horariostua.bloquehorario;

import com.ipor.horariostua.bloquehorario.colaborador.ColaboradorService;
import com.ipor.horariostua.bloquehorario.dto.Recibido_BH_DTO;
import com.ipor.horariostua.bloquehorario.sede.SedeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BloqueHorarioService {
    @Autowired
    BloqueHorarioRepository bloqueHorarioRepository;
    @Autowired
    ColaboradorService colaboradorService;
    @Autowired
    SedeService sedeService;


    public BloqueHorario save(BloqueHorario bloqueHorario){
        bloqueHorarioRepository.save(bloqueHorario);
        return bloqueHorario;
    }

    public void eliminar(Long id){
        bloqueHorarioRepository.deleteById(id);
    }

    public BloqueHorario editar(Recibido_BH_DTO dto, Long id){
        BloqueHorario bloque = bloqueHorarioRepository.findById(id).get();
        bloque.setColaborador(colaboradorService.getColaboradorPorId(dto.getIdColaborador()));
        bloque.setSede(sedeService.getSedePorId(dto.getIdSede()));
        bloque.setHoraInicio(dto.getHoraInicio());
        bloque.setHoraFin(dto.getHoraFin());
        bloqueHorarioRepository.save(bloque);
        return bloque;
    }

    public List<BloqueHorario> listarTodo(){
        return bloqueHorarioRepository.findAll();
    }

}
