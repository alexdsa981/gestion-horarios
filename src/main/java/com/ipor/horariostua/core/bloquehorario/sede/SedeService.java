package com.ipor.horariostua.core.bloquehorario.sede;

import com.ipor.horariostua.core.bloquehorario.agrupacion.sedes.DetalleSedeAgrupacion;
import com.ipor.horariostua.core.bloquehorario.agrupacion.sedes.DetalleSedeAgrupacionRepository;
import com.ipor.horariostua.core.bloquehorario.agrupacion.sedes.DetalleSedeAgrupacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

@Service
public class SedeService {
    @Autowired
    SedeRepository sedeRepository;
    @Autowired
    DetalleSedeAgrupacionService detalleSedeAgrupacionService;

    public Model getModelSedesActivasPorAgrupacion(Model model, Long idAgrupacion) {
        List<DetalleSedeAgrupacion> listaDetalle =  detalleSedeAgrupacionService.listarDetallePorIdAgrupacion(idAgrupacion);
        List<Sede> listaSedes = new ArrayList<>();
        for (DetalleSedeAgrupacion detalle : listaDetalle){
            listaSedes.add(detalle.getSede());
        }
        model.addAttribute("listaSedesActivasPorAgrupacion", listaSedes);
        return model;
    }
    public List<Sede> getSelectSedes(){
        return sedeRepository.findByIsActiveTrue();
    }

    public Sede getSedePorId(Long id){
        return sedeRepository.findById(id).get();
    }
}
