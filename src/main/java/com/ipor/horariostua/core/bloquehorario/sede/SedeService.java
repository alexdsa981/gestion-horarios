package com.ipor.horariostua.core.bloquehorario.sede;

import com.ipor.horariostua.core.bloquehorario.agrupacion.sedes.DetalleSedeAgrupacion;
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


    public Sede crearSede(String nombre) {
        Sede sede = new Sede();
        sede.setNombre(nombre);
        sede.setIsActive(true);
        return sedeRepository.save(sede);
    }

    public void actualizarSede(Sede sede) {

        sedeRepository.save(sede);
    }


    public Model getModelSedesActivasPorAgrupacion(Model model, Long idAgrupacion) {
        List<DetalleSedeAgrupacion> listaDetalle =  detalleSedeAgrupacionService.listarDetalleActivosPorIdAgrupacion(idAgrupacion);
        List<Sede> listaSedes = new ArrayList<>();
        for (DetalleSedeAgrupacion detalle : listaDetalle){
            listaSedes.add(detalle.getSede());
        }
        model.addAttribute("listaSedesActivasPorAgrupacion", listaSedes);
        return model;
    }
    public List<Sede> getSedes(){
        return sedeRepository.findAll();
    }

    public List<Sede> getSedesActivas(){
        return sedeRepository.findByIsActiveTrue();
    }


    public void cambiarEstado(Long id, Boolean estado){
        Sede sede = sedeRepository.findById(id).get();
        sede.setIsActive(estado);
        sedeRepository.save(sede);
    }

    public Sede getSedePorId(Long id){
        return sedeRepository.findById(id).get();
    }
}
