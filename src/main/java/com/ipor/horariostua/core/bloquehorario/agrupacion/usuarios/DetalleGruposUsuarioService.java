package com.ipor.horariostua.core.bloquehorario.agrupacion.usuarios;

import com.ipor.horariostua.core.bloquehorario.agrupacion.Agrupacion;
import com.ipor.horariostua.core.bloquehorario.agrupacion.AgrupacionService;
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
        List<DetalleGruposUsuario> detalleList = detalleGruposUsuarioRepository.findByUsuarioId(id);
        List<Agrupacion> listaAgrupaciones = new ArrayList<>();
        for (DetalleGruposUsuario detalle : detalleList){
            listaAgrupaciones.add(detalle.getAgrupacion());
        }
        return listaAgrupaciones;
    }
}
