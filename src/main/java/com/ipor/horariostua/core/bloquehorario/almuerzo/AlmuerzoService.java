package com.ipor.horariostua.core.bloquehorario.almuerzo;

import com.ipor.horariostua.core.bloquehorario.BloqueHorario;
import com.ipor.horariostua.core.bloquehorario.BloqueHorarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AlmuerzoService {
    @Autowired
    AlmuerzoRepository almuerzoRepository;


    public Almuerzo save(Almuerzo almuerzo){
        return  almuerzoRepository.save(almuerzo);
    }

    public Optional<Almuerzo> getAlmuerzoById(Long id) {
        return almuerzoRepository.findById(id);
    }

    public void deleteAlmuerzo(Long id) {
        almuerzoRepository.deleteById(id);
    }

    public Optional<Almuerzo> updateAlmuerzo(Long id, Almuerzo almuerzoDetalles) {
        return almuerzoRepository.findById(id).map(almuerzo -> {
            almuerzo.setHoraInicio(almuerzoDetalles.getHoraInicio());
            almuerzo.setHoraFin(almuerzoDetalles.getHoraFin());
            // agrega aquí otros campos si los tienes
            return almuerzoRepository.save(almuerzo);
        });
    }

}
