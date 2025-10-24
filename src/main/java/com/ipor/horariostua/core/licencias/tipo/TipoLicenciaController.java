package com.ipor.horariostua.core.licencias.tipo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/tipo-licencias")
public class TipoLicenciaController {

    @Autowired
    private TipoLicenciaService tipoLicenciaService;



    @GetMapping("/{id}")
    public TipoLicencia getTipoLicencia(@PathVariable Long id) {
        return tipoLicenciaService.findById(id);
    }

    @PostMapping("/crear")
    public TipoLicencia crearTipoLicencia(@RequestBody NombreDTO dto) {
        return tipoLicenciaService.crearTipoLicencia(dto.nombre);
    }

    @PostMapping("/{id}/activar")
    public TipoLicencia activarTipoLicencia(@PathVariable Long id) {
        return tipoLicenciaService.activar(id);
    }

    @PostMapping("/{id}/desactivar")
    public TipoLicencia desactivarTipoLicencia(@PathVariable Long id) {
        return tipoLicenciaService.desactivar(id);
    }

    @PutMapping("/{id}/nombre")
    public TipoLicencia editarNombreTipoLicencia(
            @PathVariable Long id,
            @RequestBody NombreDTO dto) {
        return tipoLicenciaService.editarNombre(id, dto.nombre);
    }

    @GetMapping
    public List<TipoLicencia> listarTodos() {
        return tipoLicenciaService.listarTodos();
    }

    @GetMapping("/activos")
    public List<TipoLicencia> listarActivos() {
        return tipoLicenciaService.listarActivos();
    }
}