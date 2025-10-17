package com.ipor.horariostua.core.bloquehorario.agrupacion.rangohoras;

import com.ipor.horariostua.core.bloquehorario.agrupacion.Agrupacion;
import com.ipor.horariostua.core.bloquehorario.agrupacion.AgrupacionService;
import com.ipor.horariostua.core.bloquehorario.agrupacion.sedes.DetalleSedeAgrupacion;
import com.ipor.horariostua.core.bloquehorario.agrupacion.sedes.DetalleSedeAgrupacionRepository;
import com.ipor.horariostua.core.bloquehorario.sede.Sede;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RangoHorarioService {
    @Autowired
    RangoHorarioRepository rangoHorarioRepository;

    public RangoHorario creaRangoHorario(Agrupacion agrupacion, Integer horaInicio, Integer horaFin){
        RangoHorario rangoHorario = new RangoHorario(agrupacion, horaInicio, horaFin);
        return rangoHorarioRepository.save(rangoHorario);
    }

    public RangoHorario obtenerRangoHorarioPorAgrupacion(Agrupacion agrupacion){
        return  agrupacion.getRangoHorario();
    }

    public RangoHorario actualizarRangoHorarioPorAgrupacion(Agrupacion agrupacion, Integer horaInicio, Integer horaFin) {
        RangoHorario rangoHorario = agrupacion.getRangoHorario();
        if (rangoHorario == null) {
            rangoHorario = creaRangoHorario(agrupacion, horaInicio, horaFin);
            agrupacion.setRangoHorario(rangoHorario);
        } else {
            rangoHorario.setRangoInicio(horaInicio);
            rangoHorario.setRangoFin(horaFin);
        }
        return rangoHorarioRepository.save(rangoHorario);
    }

}
