package com.ipor.horariostua.core.bloquehorario.agrupacion.usuarios;

import com.ipor.horariostua.core.bloquehorario.agrupacion.Agrupacion;
import com.ipor.horariostua.core.bloquehorario.agrupacion.AgrupacionService;
import com.ipor.horariostua.core.usuario.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DetalleGruposUsuarioService {
    @Autowired
    DetalleGruposUsuarioRepository detalleGruposUsuarioRepository;
    @Autowired
    AgrupacionService agrupacionService;

    public List<Agrupacion> getAgrupacionesPorUsuarioId(Long id){
        List<DetalleGruposUsuario> detalleList = detalleGruposUsuarioRepository.findByUsuarioIdAndIsActiveTrue(id);
        List<Agrupacion> listaAgrupaciones = new ArrayList<>();
        for (DetalleGruposUsuario detalle : detalleList){
            listaAgrupaciones.add(detalle.getAgrupacion());
        }
        return listaAgrupaciones;
    }
    public void agrupar(Usuario usuario, Long idAgrupacion){
        DetalleGruposUsuario detalleAgrupacion;
        if (detalleGruposUsuarioRepository.findByUsuarioIdAndAgrupacionId(usuario.getId(), idAgrupacion).isPresent()){
            detalleAgrupacion = detalleGruposUsuarioRepository.findByUsuarioIdAndAgrupacionId(usuario.getId(), idAgrupacion).get();
        }else{
            detalleAgrupacion = new DetalleGruposUsuario();
        }
        Agrupacion agrupacion = agrupacionService.getAgrupacionPorId(idAgrupacion);
        detalleAgrupacion.setIsActive(true);
        detalleAgrupacion.setUsuario(usuario);
        detalleAgrupacion.setAgrupacion(agrupacion);
        detalleGruposUsuarioRepository.save(detalleAgrupacion);
    }

    public void desagrupar(Usuario usuario, Long idAgrupacion){
        DetalleGruposUsuario detalleAgrupacion;
        if (detalleGruposUsuarioRepository.findByUsuarioIdAndAgrupacionId(usuario.getId(), idAgrupacion).isPresent()){
            detalleAgrupacion = detalleGruposUsuarioRepository.findByUsuarioIdAndAgrupacionId(usuario.getId(), idAgrupacion).get();
            detalleGruposUsuarioRepository.delete(detalleAgrupacion);
        }
    }

}
