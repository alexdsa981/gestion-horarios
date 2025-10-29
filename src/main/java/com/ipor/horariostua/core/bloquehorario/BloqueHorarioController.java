package com.ipor.horariostua.core.bloquehorario;

import com.ipor.horariostua.core.bloquehorario.agrupacion.AgrupacionService;
import com.ipor.horariostua.core.bloquehorario.agrupacion.colaboradores.DetalleColaboradorAgrupacion;
import com.ipor.horariostua.core.bloquehorario.agrupacion.colaboradores.DetalleColaboradorAgrupacionService;
import com.ipor.horariostua.core.bloquehorario.bloquehorarioDTO.FiltroColaboradorMesDTO;
import com.ipor.horariostua.core.bloquehorario.colaborador.ColaboradorService;
import com.ipor.horariostua.core.bloquehorario.bloquehorarioDTO.Recibido_BH_DTO;
import com.ipor.horariostua.core.bloquehorario.bloquehorarioDTO.Mostrar_BH_DTO;
import com.ipor.horariostua.core.bloquehorario.bloquehorarioDTO.Repetir_BH_DTO;
import com.ipor.horariostua.core.bloquehorario.horariolaboral.HorarioLaboralService;
import com.ipor.horariostua.core.bloquehorario.sede.SedeService;
import com.ipor.horariostua.core.bloquehorario.sede.dto.ListarSedesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/app/bloque-horarios")
public class BloqueHorarioController {

    @Autowired
    private BloqueHorarioService bloqueHorarioService;
    @Autowired
    private HorarioLaboralService horarioLaboralService;
    @Autowired
    private ColaboradorService colaboradorService;
    @Autowired
    private SedeService sedeService;
    @Autowired
    private AgrupacionService agrupacionService;
    @Autowired
    private DetalleColaboradorAgrupacionService detalleColaboradorAgrupacionService;


    @GetMapping("/listar-fecha/{idAgrupacion}")
    public ResponseEntity<List<Mostrar_BH_DTO>> listarBloquesAgrupacionYFecha(
            @PathVariable Long idAgrupacion,
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {
        List<BloqueHorario> listaHorarios = bloqueHorarioService.listarPorRangoFechaYagrupacion(idAgrupacion, desde, hasta);
        List<Mostrar_BH_DTO> listaDTO = new ArrayList<>();
        for (BloqueHorario bloque : listaHorarios) {
            DetalleColaboradorAgrupacion detalle = detalleColaboradorAgrupacionService.getDetallePorColaboradorYAgrupacion(
                    bloque.getColaborador().getId(), idAgrupacion);
            Mostrar_BH_DTO dto = new Mostrar_BH_DTO(bloque, detalle);
            listaDTO.add(dto);
        }
        return ResponseEntity.ok(listaDTO);
    }

    @GetMapping("/listar/{idAgrupacion}")
    public ResponseEntity<List<Mostrar_BH_DTO>> listarBloquesAgrupacion(@PathVariable Long idAgrupacion) {
        System.out.println(idAgrupacion);
        List<BloqueHorario> listaHorarios = bloqueHorarioService.listarPorAgrupacionId(idAgrupacion);
        List<Mostrar_BH_DTO> listaDTO = new ArrayList<>();
        for(BloqueHorario bloque : listaHorarios){
            DetalleColaboradorAgrupacion detalle = detalleColaboradorAgrupacionService.getDetallePorColaboradorYAgrupacion(bloque.getColaborador().getId(), idAgrupacion);
            Mostrar_BH_DTO dto = new Mostrar_BH_DTO(bloque, detalle);
            listaDTO.add(dto);
        }
        return ResponseEntity.ok(listaDTO);
    }

    @GetMapping("/dia")
    public ResponseEntity<List<Mostrar_BH_DTO>> obtenerHorariosDelDia(
            @RequestParam Long agrupacionId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        List<BloqueHorario> bloques = bloqueHorarioService.listarPorFechaYagrupacion(agrupacionId, fecha);
        List<Mostrar_BH_DTO> dtos = bloques.stream()
                .map(bh -> {
                    DetalleColaboradorAgrupacion detalle = detalleColaboradorAgrupacionService
                            .getDetallePorColaboradorYAgrupacion(bh.getColaborador().getId(), agrupacionId);
                    return new Mostrar_BH_DTO(bh, detalle);
                })
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/agregar")
    public ResponseEntity<?> registraBloqueHorario(@RequestBody Recibido_BH_DTO dto) {
        try {
            BloqueHorario guardado = bloqueHorarioService.agregar(dto);
            DetalleColaboradorAgrupacion detalle = detalleColaboradorAgrupacionService.getDetallePorColaboradorYAgrupacion(dto.getIdColaborador(), dto.getIdAgrupacion());
            Mostrar_BH_DTO mostrarDto = new Mostrar_BH_DTO(guardado, detalle);
            return ResponseEntity.status(HttpStatus.CREATED).body(mostrarDto);
        } catch (IllegalArgumentException ex) {
            // Devuelve el mensaje de error y un 409 Conflict
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Collections.singletonMap("error", ex.getMessage()));
        }
    }

    @PostMapping("/repetir")
    public ResponseEntity<List<Mostrar_BH_DTO>> registrarRepeticionBloque(@RequestBody Repetir_BH_DTO dto) {
        BloqueHorario bloqueRepetir = bloqueHorarioService.getPorId(dto.getId());
        DetalleColaboradorAgrupacion detalle = detalleColaboradorAgrupacionService.getDetallePorColaboradorYAgrupacion(bloqueRepetir.getColaborador().getId(), bloqueRepetir.getAgrupacion().getId());


        if (dto.getFechas() != null) {
            dto.getFechas().forEach(fecha -> System.out.println("Fecha: " + fecha));
        }
        try {
            List<BloqueHorario> listaRepeticion = bloqueHorarioService.repetir(dto);
            List<Mostrar_BH_DTO> listaMostrar = new ArrayList<>();
            for (BloqueHorario repetido : listaRepeticion){
                Mostrar_BH_DTO mostrarDTO = new Mostrar_BH_DTO(repetido, detalle);
                listaMostrar.add(mostrarDTO);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(listaMostrar);
        } catch (Exception e) {
            System.out.println("Error al registrar repetición: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PutMapping("/editar/{id}")
    public ResponseEntity<?> editarBloqueHorario(@RequestBody Recibido_BH_DTO dto, @PathVariable Long id) {
        System.out.println("LLAMADA A EDITAR BLOQUE con id=" + id + ", dto=" + dto); // log para depuración
        try {
            BloqueHorario guardado = bloqueHorarioService.editar(dto, id);
            DetalleColaboradorAgrupacion detalle = detalleColaboradorAgrupacionService.getDetallePorColaboradorYAgrupacion(
                    guardado.getColaborador().getId(),
                    guardado.getAgrupacion().getId()
            );
            Mostrar_BH_DTO mostrarDto = new Mostrar_BH_DTO(guardado, detalle);
            return ResponseEntity.ok(mostrarDto);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Collections.singletonMap("error", ex.getMessage()));
        }
    }
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarBloqueHorario(@PathVariable Long id) {
        bloqueHorarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/fechas-repeticion/{id}")
    public ResponseEntity<List<LocalDate>> listarBloquesHorarios(@PathVariable Long id) {
        List<LocalDate> listaFechas = bloqueHorarioService.listarFechasRepeticion(id);
        return ResponseEntity.ok(listaFechas);
    }

    @PostMapping("/bloques-colaborador")
    @ResponseBody
    public Map<String, Object> getBloquesYsedesColaborador(
            @RequestBody FiltroColaboradorMesDTO filtro) {

        List<BloqueHorario> bloques = bloqueHorarioService.bloquesDeColaboradorPorMes(
                filtro.getColaboradorId(), filtro.getAgrupacionId(), filtro.getInicioMes(), filtro.getFinMes()
        );
        List<Mostrar_BH_DTO> bloquesDTO = bloques.stream()
                .map(bh -> new Mostrar_BH_DTO(bh, detalleColaboradorAgrupacionService.getDetallePorColaboradorYAgrupacion(filtro.getColaboradorId(), filtro.getAgrupacionId())))
                .collect(Collectors.toList());

        // Obtener sedes únicas de los bloques
        List<ListarSedesDTO> sedes = bloques.stream()
                .map(BloqueHorario::getSede)
                .distinct()
                .map(ListarSedesDTO::new)
                .collect(Collectors.toList());

        Map<String, Object> resp = new HashMap<>();
        resp.put("bloques", bloquesDTO);
        resp.put("sedes", sedes);
        return resp;
    }

}
