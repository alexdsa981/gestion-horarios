package com.ipor.horariostua.core.bloquehorario.agrupacion.sedes;

import com.ipor.horariostua.core.bloquehorario.agrupacion.Agrupacion;
import com.ipor.horariostua.core.bloquehorario.agrupacion.AgrupacionService;
import com.ipor.horariostua.core.bloquehorario.sede.Sede;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        detalleAgrupacion.setIsActive(true);
        detalleAgrupacion.setSede(sede);
        detalleAgrupacion.setAgrupacion(agrupacion);
        detalleSedeAgrupacionRepository.save(detalleAgrupacion);
    }

    public List<DetalleSedeAgrupacion> listarDetalleActivosPorIdAgrupacion(Long idAgrupacion){
        return detalleSedeAgrupacionRepository.findByAgrupacionIdAndIsActiveTrue(idAgrupacion);
    }

    public DetalleSedeAgrupacion getDetallePorSedeYAgrupacion(Long idSede, Long idAgrupacion) {
        Optional<DetalleSedeAgrupacion> detalle = detalleSedeAgrupacionRepository.findBySedeIdAndAgrupacionId(idSede, idAgrupacion);
        return detalle.orElse(null);
    }

    public void cambiarEstado(Long id, Boolean estado){
        DetalleSedeAgrupacion detalleSedeAgrupacion = detalleSedeAgrupacionRepository.findById(id).get();
        detalleSedeAgrupacion.setIsActive(estado);
        detalleSedeAgrupacionRepository.save(detalleSedeAgrupacion);
    }

}
