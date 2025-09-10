package com.ipor.horariostua.core.bloquehorario;

import com.ipor.horariostua.core.bloquehorario.agrupacion.AgrupacionService;
import com.ipor.horariostua.core.bloquehorario.agrupacion.colaboradores.DetalleColaboradorAgrupacion;
import com.ipor.horariostua.core.bloquehorario.agrupacion.colaboradores.DetalleColaboradorAgrupacionService;
import com.ipor.horariostua.core.bloquehorario.colaborador.ColaboradorService;
import com.ipor.horariostua.core.bloquehorario.dto.Recibido_BH_DTO;
import com.ipor.horariostua.core.bloquehorario.dto.Mostrar_BH_DTO;
import com.ipor.horariostua.core.bloquehorario.dto.Repetir_BH_DTO;
import com.ipor.horariostua.core.bloquehorario.horariolaboral.HorarioLaboralService;
import com.ipor.horariostua.core.bloquehorario.sede.SedeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @GetMapping("/listar/{idAgrupacion}")
    public ResponseEntity<List<BloqueHorario>> listarBloquesAgrupacion(@PathVariable Long idAgrupacion) {
        List<BloqueHorario> listaHorarios = bloqueHorarioService.listarPorAgrupacionId(idAgrupacion);
        return ResponseEntity.ok(listaHorarios);
    }

    @PostMapping("/agregar")
    public ResponseEntity<Mostrar_BH_DTO> registraBloqueHorario(@RequestBody Recibido_BH_DTO dto) {
        BloqueHorario guardado = bloqueHorarioService.agregar(dto);
        DetalleColaboradorAgrupacion detalle = detalleColaboradorAgrupacionService.getDetallePorColaboradorYAgrupacion(dto.getIdColaborador(), dto.getIdAgrupacion());
        Mostrar_BH_DTO mostrarDto = new Mostrar_BH_DTO(guardado, detalle);
        return ResponseEntity.status(HttpStatus.CREATED).body(mostrarDto);
    }

    @PostMapping("/repetir")
    public ResponseEntity<List<Mostrar_BH_DTO>> registrarRepeticionBloque(@RequestBody Repetir_BH_DTO dto) {
        System.out.println("DTO recibido: " + dto);
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
                System.out.println("Bloque creado: " + mostrarDTO);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(listaMostrar);
        } catch (Exception e) {
            System.out.println("Error al registrar repetición: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PutMapping("/editar/{id}")
    public ResponseEntity<Mostrar_BH_DTO> editarBloqueHorario( @RequestBody Recibido_BH_DTO dto, @PathVariable Long id) {
        BloqueHorario guardado = bloqueHorarioService.editar(dto, id);
        DetalleColaboradorAgrupacion detalle = detalleColaboradorAgrupacionService.getDetallePorColaboradorYAgrupacion(guardado.getColaborador().getId(), guardado.getAgrupacion().getId());
        Mostrar_BH_DTO mostrarDto = new Mostrar_BH_DTO(guardado, detalle);
        return ResponseEntity.ok(mostrarDto);
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

}
