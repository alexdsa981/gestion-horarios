package com.ipor.horariostua.core.licencias.tipo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoLicenciaService {

    @Autowired
    private TipoLicenciaRepository tipoLicenciaRepository;

    public List<TipoLicencia> listarTodos() {
        return tipoLicenciaRepository.findAll();
    }

    public List<TipoLicencia> listarActivos() {
        return tipoLicenciaRepository.findByIsActiveTrue();
    }

    public TipoLicencia findById(Long id) {
        return tipoLicenciaRepository.findById(id)
                .orElse(null);
    }

    public TipoLicencia crearTipoLicencia(String nombre) {
        TipoLicencia nuevo = new TipoLicencia(nombre);
        return tipoLicenciaRepository.save(nuevo);
    }

    public TipoLicencia activar(Long id) {
        TipoLicencia tipoLicencia = findById(id);
        if (tipoLicencia != null) {
            tipoLicencia.setIsActive(Boolean.TRUE);
            tipoLicenciaRepository.save(tipoLicencia);
        }
        return tipoLicencia;
    }

    public TipoLicencia desactivar(Long id) {
        TipoLicencia tipoLicencia = findById(id);
        if (tipoLicencia != null) {
            tipoLicencia.setIsActive(Boolean.FALSE);
            tipoLicenciaRepository.save(tipoLicencia);
        }
        return tipoLicencia;
    }

    public TipoLicencia editarNombre(Long id, String nuevoNombre) {
        TipoLicencia tipoLicencia = findById(id);
        if (tipoLicencia != null) {
            tipoLicencia.setNombre(nuevoNombre);
            tipoLicenciaRepository.save(tipoLicencia);
        }
        return tipoLicencia;
    }

}