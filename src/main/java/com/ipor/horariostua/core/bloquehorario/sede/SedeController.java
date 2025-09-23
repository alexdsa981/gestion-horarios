package com.ipor.horariostua.core.bloquehorario.sede;

import com.ipor.horariostua.core.bloquehorario.agrupacion.sedes.DetalleSedeAgrupacion;
import com.ipor.horariostua.core.bloquehorario.agrupacion.sedes.DetalleSedeAgrupacionService;
import com.ipor.horariostua.core.bloquehorario.sede.dto.ListarSedesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/app/sedes")
public class SedeController {
    @Autowired
    SedeService sedeService;
    @Autowired
    DetalleSedeAgrupacionService detalleSedeAgrupacionService;

    @GetMapping("/listar")
    public ResponseEntity<List<Sede>> getSedesGlobal() {
        List<Sede> sedes = sedeService.getSedes();
        return ResponseEntity.ok(sedes);
    }

    @GetMapping("/listar/activos")
    public ResponseEntity<List<Sede>> getSedesGlobalActivos() {
        List<Sede> sedes = sedeService.getSedesActivas();
        return ResponseEntity.ok(sedes);
    }

    @GetMapping("/listar/activos/{IdAgrupacion}")
    public ResponseEntity<List<Sede>> getSedesGlobalActivosAgrupacion(@PathVariable Long IdAgrupacion) {
        List<DetalleSedeAgrupacion> listaDetalle =  detalleSedeAgrupacionService.listarDetalleActivosPorIdAgrupacion(IdAgrupacion);
        List<Sede> listaSedes = new ArrayList<>();
        for (DetalleSedeAgrupacion detalle : listaDetalle){
            listaSedes.add(detalle.getSede());
        }
        return ResponseEntity.ok(listaSedes);
    }




    @PostMapping("/estado/{idSede}")
    public ResponseEntity<?> cambiarEstadoSedeGlobal(@PathVariable Long idSede, @RequestBody Boolean isActive) {
        sedeService.cambiarEstado(idSede, isActive);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/listar/{idAgrupacion}")
    public ResponseEntity<List<ListarSedesDTO>> getSedesSeleccionables(@PathVariable Long idAgrupacion) {
        List<Sede> sedes = sedeService.getSedes();
        List<ListarSedesDTO> listaDTO =  new ArrayList<>();
        for (Sede sede : sedes){
            DetalleSedeAgrupacion detalle = detalleSedeAgrupacionService.getDetallePorSedeYAgrupacion(sede.getId(),idAgrupacion);
            if (sede.getIsActive() || detalle.getIsActive()){
                listaDTO.add(new ListarSedesDTO(sede, detalle));
            }
        }
        return ResponseEntity.ok(listaDTO);
    }
    @PostMapping("/estado/{idAgrupacion}/{idSede}")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long idAgrupacion,@PathVariable Long idSede, @RequestBody Boolean isActive) {
        DetalleSedeAgrupacion detalleSedeAgrupacion = detalleSedeAgrupacionService.getDetallePorSedeYAgrupacion(idSede,idAgrupacion);
        if (detalleSedeAgrupacion == null){
            detalleSedeAgrupacionService.agrupar(sedeService.getSedePorId(idSede),idAgrupacion);
        }else{
            detalleSedeAgrupacionService.cambiarEstado(detalleSedeAgrupacion.getId(), isActive);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearSede(@RequestBody Sede nuevaSede) {
        if (nuevaSede.getNombre() == null || nuevaSede.getNombre().trim().isEmpty())
            return ResponseEntity.badRequest().body("Nombre requerido");
        Sede creada = sedeService.crearSede(nuevaSede.getNombre());
        return ResponseEntity.ok(creada);
    }

    @PutMapping("/editar/{idSede}")
    public ResponseEntity<?> editarSede(@PathVariable Long idSede, @RequestBody Sede datosSede) {
        if (datosSede.getNombre() == null || datosSede.getNombre().trim().isEmpty())
            return ResponseEntity.badRequest().body("Nombre requerido");
        Sede sede = sedeService.getSedePorId(idSede);
        if (sede == null) return ResponseEntity.notFound().build();
        sede.setNombre(datosSede.getNombre());
        sedeService.actualizarSede(sede);
        return ResponseEntity.ok(sede);
    }
}
