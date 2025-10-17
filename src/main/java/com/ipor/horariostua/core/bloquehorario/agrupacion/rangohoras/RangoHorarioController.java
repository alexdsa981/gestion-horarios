package com.ipor.horariostua.core.bloquehorario.agrupacion.rangohoras;

import com.ipor.horariostua.core.bloquehorario.agrupacion.Agrupacion;
import com.ipor.horariostua.core.bloquehorario.agrupacion.AgrupacionService;
import com.ipor.horariostua.core.bloquehorario.agrupacion.rangohoras.dto.RangoHorarioDTO;
import com.ipor.horariostua.core.bloquehorario.agrupacion.sedes.DetalleSedeAgrupacion;
import com.ipor.horariostua.core.bloquehorario.sede.Sede;
import com.ipor.horariostua.core.bloquehorario.sede.dto.ListarSedesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/app/rango-horas")
public class RangoHorarioController {

    @Autowired
    private RangoHorarioService rangoHorarioService;
    @Autowired
    private AgrupacionService agrupacionService;

    @GetMapping("/{idAgrupacion}")
    @ResponseBody
    public ResponseEntity<RangoHorarioDTO> getRangoHorasPorAgrupacion(@PathVariable Long idAgrupacion) {
        Agrupacion agrupacion = agrupacionService.getAgrupacionPorId(idAgrupacion);
        if (agrupacion == null) {
            return ResponseEntity.notFound().build();
        }

        RangoHorario rangoHorario = rangoHorarioService.obtenerRangoHorarioPorAgrupacion(agrupacion);
        if (rangoHorario == null) {
            return ResponseEntity.notFound().build();
        }

        RangoHorarioDTO dto = new RangoHorarioDTO(rangoHorario);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{idAgrupacion}/actualizar")
    @ResponseBody
    public ResponseEntity<RangoHorarioDTO> actualizarRangoHorario(
            @PathVariable Long idAgrupacion,
            @RequestParam Integer horaInicio,
            @RequestParam Integer horaFin) {
        Agrupacion agrupacion = agrupacionService.getAgrupacionPorId(idAgrupacion);
        if (agrupacion == null) {
            return ResponseEntity.notFound().build();
        }
        RangoHorario rangoHorario = rangoHorarioService.actualizarRangoHorarioPorAgrupacion(agrupacion, horaInicio, horaFin);
        RangoHorarioDTO dto = new RangoHorarioDTO(rangoHorario);
        return ResponseEntity.ok(dto);
    }
}