package com.ipor.horariostua.core.bloquehorario.agrupacion;

import com.ipor.horariostua.core.bloquehorario.agrupacion.departamento.Departamento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgrupacionService {
    @Autowired
    AgrupacionRepository agrupacionRepository;

    public Agrupacion getAgrupacionPorId(Long id){
        return agrupacionRepository.findById(id).get();
    }

    public List<Agrupacion> getListaAgrupacion(){
        return agrupacionRepository.findAllByOrderByNombre();
    }

    public List<Agrupacion> getListaAgrupacionPorDepartamento(Long idDepartamento){
        return agrupacionRepository.findByDepartamentoIdOrderByNombre(idDepartamento);
    }

    public List<Agrupacion> getListaAgrupacionPorDepartamentoAndTrue(Long idDepartamento){
        return agrupacionRepository.findByDepartamentoIdAndIsActiveTrueOrderByNombre(idDepartamento);
    }

    public List<Agrupacion> getListaAgrupacionTrue(){
        return agrupacionRepository.findByIsActiveTrueOrderByNombre();
    }



    public Agrupacion crearAgrupacion(String nombre, Departamento departamento) {
        Agrupacion agrupacion = new Agrupacion();
        agrupacion.setDepartamento(departamento);
        agrupacion.setNombre(nombre);
        agrupacion.setIsActive(true);
        return agrupacionRepository.save(agrupacion);
    }

    public void actualizarAgrupacion(Agrupacion agrupacion) {
        agrupacionRepository.save(agrupacion);
    }

    public void cambiarEstado(Long idAgrupacion, Boolean isActive) {
        Agrupacion agrupacion = getAgrupacionPorId(idAgrupacion);
        if (agrupacion != null) {
            agrupacion.setIsActive(isActive);
            agrupacionRepository.save(agrupacion);
        }
    }

}
