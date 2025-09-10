package com.ipor.horariostua.core.bloquehorario.agrupacion.sedes;

import com.ipor.horariostua.core.bloquehorario.agrupacion.Agrupacion;
import com.ipor.horariostua.core.bloquehorario.agrupacion.AgrupacionService;
import com.ipor.horariostua.core.bloquehorario.agrupacion.colaboradores.DetalleColaboradorAgrupacion;
import com.ipor.horariostua.core.bloquehorario.colaborador.Colaborador;
import com.ipor.horariostua.core.bloquehorario.sede.Sede;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class DetalleSedeAgrupacionService {
    @Autowired
    DetalleSedeAgrupacionRepository detalleSedeAgrupacionRepository;
    @Autowired
    AgrupacionService agrupacionService;

    public void agrupar(Sede sede, Long idAgrupacion){
        DetalleSedeAgrupacion detalleAgrupacion;
        if (detalleSedeAgrupacionRepository.findBySedeIdAndAgrupacionId(sede.getId(), idAgrupacion).isPresent()){
            detalleAgrupacion = detalleSedeAgrupacionRepository.findBySedeIdAndAgrupacionId(sede.getId(), idAgrupacion).get();
        }else{
            detalleAgrupacion = new DetalleSedeAgrupacion();
        }
        Agrupacion agrupacion = agrupacionService.getAgrupacionPorId(idAgrupacion);
        detalleAgrupacion.setSede(sede);
        detalleAgrupacion.setAgrupacion(agrupacion);
        detalleSedeAgrupacionRepository.save(detalleAgrupacion);
    }

    public List<DetalleSedeAgrupacion> listarDetallePorIdAgrupacion(Long idAgrupacion){
        return detalleSedeAgrupacionRepository.findByAgrupacionId(idAgrupacion);
    }

    public DetalleSedeAgrupacion getDetallePorColaboradorYAgrupacion(Long idSede, Long idAgrupacion) {
        Optional<DetalleSedeAgrupacion> detalle = detalleSedeAgrupacionRepository.findBySedeIdAndAgrupacionId(idSede, idAgrupacion);
        return detalle.orElse(null);
    }

}
