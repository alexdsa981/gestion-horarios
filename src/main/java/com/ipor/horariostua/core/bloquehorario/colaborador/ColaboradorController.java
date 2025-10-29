package com.ipor.horariostua.core.bloquehorario.colaborador;

import com.ipor.horariostua.core.bloquehorario.BloqueHorario;
import com.ipor.horariostua.core.bloquehorario.BloqueHorarioService;
import com.ipor.horariostua.core.bloquehorario.agrupacion.AgrupacionService;
import com.ipor.horariostua.core.bloquehorario.agrupacion.colaboradores.DetalleColaboradorAgrupacion;
import com.ipor.horariostua.core.bloquehorario.agrupacion.colaboradores.DetalleColaboradorAgrupacionService;
import com.ipor.horariostua.core.bloquehorario.colaborador.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/app/colaboradores")
public class ColaboradorController {
    @Autowired
    ColaboradorService colaboradorService;
    @Autowired
    DetalleColaboradorAgrupacionService detalleColaboradorAgrupacionService;
    @Autowired
    AgrupacionService agrupacionService;
    @Autowired
    BloqueHorarioService bloqueHorarioService;

    @GetMapping("/agrupacion/{idAgrupacion}")
    @ResponseBody
    public ResponseEntity<List<ListarColaboradoresDTO>> listarColaboradoresArea(@PathVariable Long idAgrupacion) {
        List<Colaborador> listaColaboradores = colaboradorService.getListarPorAgrupacionId(idAgrupacion);
        List<ListarColaboradoresDTO> listaDTO = new ArrayList<>();
        for (Colaborador colaborador : listaColaboradores){
            DetalleColaboradorAgrupacion detalle = detalleColaboradorAgrupacionService.getDetallePorColaboradorYAgrupacion(colaborador.getId(), agrupacionService.getAgrupacionPorId(idAgrupacion).getId());
            listaDTO.add(new ListarColaboradoresDTO(colaborador, detalle));
        }
        return ResponseEntity.ok(listaDTO);
    }

    @PostMapping("/guardar")
    @ResponseBody
    public ResponseEntity<String> guardarColaborador(@RequestBody AgregarColaboradorDTO agregarColaboradorDTO) {
        detalleColaboradorAgrupacionService.agrupar(colaboradorService.agregar(agregarColaboradorDTO), agregarColaboradorDTO.getIdAgrupacion());
        return ResponseEntity.ok("Colaborador guardado exitosamente");
    }

    @PostMapping("/desactivar/{idAgrupacion}/{idColaborador}")
    @ResponseBody
    public ResponseEntity<String> desactivarColaborador(@PathVariable Long idAgrupacion ,@PathVariable Long idColaborador) {
        boolean resultado = colaboradorService.cambiarEstado(idAgrupacion ,idColaborador, false);
        if (resultado) {
            return ResponseEntity.ok("Colaborador desactivado exitosamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Colaborador no encontrado");
        }
    }

    @PostMapping("/color")
    public ResponseEntity<?> actualizarColor(@RequestBody EditarColorColaboradorDTO dto) {
        DetalleColaboradorAgrupacion detalleColaboradorAgrupacion = detalleColaboradorAgrupacionService.getDetallePorColaboradorYAgrupacion(dto.getColaboradorId(), dto.getAgrupacionId());
        detalleColaboradorAgrupacion.setEventoColor(dto.getColor());
        detalleColaboradorAgrupacionService.save(detalleColaboradorAgrupacion);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/horas-mes")
    public ResponseEntity<?> actualizarHoras(@RequestBody EditarHorasColaboradorDTO dto) {
        DetalleColaboradorAgrupacion detalleColaboradorAgrupacion = detalleColaboradorAgrupacionService.getDetallePorColaboradorYAgrupacion(dto.getColaboradorId(), dto.getAgrupacionId());
        detalleColaboradorAgrupacion.setHorasPorLaborar(dto.getHoras());
        detalleColaboradorAgrupacionService.save(detalleColaboradorAgrupacion);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/reporte-horas/{idAgrupacion}/{anio}/{mes}")
    public ResponseEntity<List<HorasMensualesColaboradorDTO>> getReporteHoras(
            @PathVariable Long idAgrupacion,
            @PathVariable int anio,
            @PathVariable int mes) {
        LocalDate inicioMes = LocalDate.of(anio, mes, 1);
        LocalDate finMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());

        List<DetalleColaboradorAgrupacion> detalles = detalleColaboradorAgrupacionService
                .listarDetallePorIdAgrupacion(idAgrupacion);

        List<HorasMensualesColaboradorDTO> resultado = new ArrayList<>();
        for (DetalleColaboradorAgrupacion detalle : detalles) {
            Colaborador colaborador = detalle.getColaborador();

            List<BloqueHorario> bloques = bloqueHorarioService.bloquesDeColaboradorPorMes(colaborador.getId(), idAgrupacion, inicioMes, finMes);

            Double horasTrabajadas = bloqueHorarioService.sumarHorasBloques(bloques);
            Double horasAlmuerzo = bloqueHorarioService.sumarHorasAlmuerzo(bloques);
            Integer horasMensuales = detalle.getHorasPorLaborar() != null ? detalle.getHorasPorLaborar() : 0;

            HorasMensualesColaboradorDTO dto = new HorasMensualesColaboradorDTO();
            dto.setColaboradorId(colaborador.getId());
            dto.setNombreCompleto(colaborador.getNombreCompleto());
            dto.setHorasTotales(horasTrabajadas-horasAlmuerzo);
            dto.setHorasMensuales(horasMensuales);
            resultado.add(dto);
        }

        return ResponseEntity.ok(resultado);
    }

}
