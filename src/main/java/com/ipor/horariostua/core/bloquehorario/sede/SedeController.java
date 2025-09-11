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

    @GetMapping("/listar/{idAgrupacion}")
    public ResponseEntity<List<ListarSedesDTO>> getColaboradoresSeleccionables(@PathVariable Long idAgrupacion) {
        List<Sede> sedes = sedeService.getSedes();
        List<ListarSedesDTO> listaDTO =  new ArrayList<>();
        for (Sede sede : sedes){
            DetalleSedeAgrupacion detalle = detalleSedeAgrupacionService.getDetallePorSedeYAgrupacion(sede.getId(),idAgrupacion);
            listaDTO.add(new ListarSedesDTO(sede, detalle));
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
}
