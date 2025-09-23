package com.ipor.horariostua.core.bloquehorario.agrupacion.departamento;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartamentoService {
    @Autowired
    DepartamentoRepository departamentoRepository;

    public Departamento getDepartamentoPorId(Long id){
        return departamentoRepository.findById(id).get();
    }

    public List<Departamento> getListaDepartamento(){
        return departamentoRepository.findAll();
    }
    public List<Departamento> getListaDepartamentoTrue(){
        return departamentoRepository.findByIsActiveTrue();
    }



    public Departamento crearDepartamento(String nombre) {
        Departamento departamento = new Departamento();
        departamento.setNombre(nombre);
        departamento.setIsActive(true);
        return departamentoRepository.save(departamento);
    }

    public void actualizarDepartamento(Departamento departamento) {
        departamentoRepository.save(departamento);
    }

    public void cambiarEstado(Long idDepartamento, Boolean isActive) {
        Departamento departamento = getDepartamentoPorId(idDepartamento);
        if (departamento != null) {
            departamento.setIsActive(isActive);
            departamentoRepository.save(departamento);
        }
    }

}
