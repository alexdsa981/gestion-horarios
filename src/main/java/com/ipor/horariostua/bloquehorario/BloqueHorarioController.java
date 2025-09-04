package com.ipor.horariostua.bloquehorario;

import com.ipor.horariostua.bloquehorario.agrupacion.AgrupacionService;
import com.ipor.horariostua.bloquehorario.colaborador.ColaboradorService;
import com.ipor.horariostua.bloquehorario.dto.Recibido_BH_DTO;
import com.ipor.horariostua.bloquehorario.dto.Mostrar_BH_DTO;
import com.ipor.horariostua.bloquehorario.horariolaboral.HorarioLaboralService;
import com.ipor.horariostua.bloquehorario.sede.SedeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/app/bloque-horarios")
public class BloqueHorarioController {

    @Autowired
    BloqueHorarioService bloqueHorarioService;
    @Autowired
    HorarioLaboralService horarioLaboralService;
    @Autowired
    ColaboradorService colaboradorService;
    @Autowired
    SedeService sedeService;
    @Autowired
    AgrupacionService agrupacionService;


    @GetMapping("/listar-todo")
    public ResponseEntity<List<Mostrar_BH_DTO>> listarBloquesHorarios() {
        List<BloqueHorario> bloques = bloqueHorarioService.listarTodo();
        List<Mostrar_BH_DTO> dtos = bloques.stream()
                .map(Mostrar_BH_DTO::new)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/agregar")
    public ResponseEntity<Mostrar_BH_DTO> registraBloqueHorario(@RequestBody Recibido_BH_DTO dto) {
        // Imprime el DTO completo
        System.out.println("DTO recibido: " + dto);

        // Si quieres ver cada campo explícitamente:
        System.out.println("fecha: " + dto.getFecha());
        System.out.println("horaInicio: " + dto.getHoraInicio());
        System.out.println("horaFin: " + dto.getHoraFin());
        System.out.println("idColaborador: " + dto.getIdColaborador());
        System.out.println("idSede: " + dto.getIdSede());
        System.out.println("idAgrupacion: " + dto.getIdAgrupacion());

        BloqueHorario bloqueHorario = new BloqueHorario();
        bloqueHorario.setHorarioLaboral(horarioLaboralService.getUltimoHorarioLaboral());
        bloqueHorario.setColaborador(colaboradorService.getColaboradorPorId(dto.getIdColaborador()));
        bloqueHorario.setFecha(dto.getFecha());
        bloqueHorario.setHoraInicio(dto.getHoraInicio());
        bloqueHorario.setHoraFin(dto.getHoraFin());
        bloqueHorario.setAgrupacion(agrupacionService.getAgrupacionPorId(1L)); // O usa dto.getIdAgrupacion() si corresponde
        bloqueHorario.setSede(sedeService.getSedePorId(dto.getIdSede()));
        bloqueHorario.setGrupoAnidado(null);

        BloqueHorario guardado = bloqueHorarioService.save(bloqueHorario);

        Mostrar_BH_DTO mostrarDto = new Mostrar_BH_DTO(guardado);
        return ResponseEntity.status(HttpStatus.CREATED).body(mostrarDto);
    }

    @PutMapping("/editar/{id}")
    public ResponseEntity<Mostrar_BH_DTO> editarBloqueHorario( @RequestBody Recibido_BH_DTO dto, @PathVariable Long id) {
        System.out.println("DTO recibido para edición: " + dto);

        System.out.println("fecha: " + dto.getFecha());
        System.out.println("horaInicio: " + dto.getHoraInicio());
        System.out.println("horaFin: " + dto.getHoraFin());
        System.out.println("idColaborador: " + dto.getIdColaborador());
        System.out.println("idSede: " + dto.getIdSede());

        BloqueHorario guardado = bloqueHorarioService.editar(dto, id);

        Mostrar_BH_DTO mostrarDto = new Mostrar_BH_DTO(guardado);
        return ResponseEntity.ok(mostrarDto);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarBloqueHorario(@PathVariable Long id) {
        bloqueHorarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}
