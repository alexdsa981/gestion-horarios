package com.ipor.horariostua.bloquehorario;

import com.ipor.horariostua.bloquehorario.agrupacion.Agrupacion;
import com.ipor.horariostua.bloquehorario.agrupacion.AgrupacionService;
import com.ipor.horariostua.bloquehorario.colaborador.ColaboradorService;
import com.ipor.horariostua.bloquehorario.dto.Agrega_BH_DTO;
import com.ipor.horariostua.bloquehorario.dto.Mostrar_BH_DTO;
import com.ipor.horariostua.bloquehorario.horariolaboral.HorarioLaboralRepository;
import com.ipor.horariostua.bloquehorario.horariolaboral.HorarioLaboralService;
import com.ipor.horariostua.bloquehorario.sede.SedeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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


    @PostMapping("/agregar")
    public ResponseEntity<Mostrar_BH_DTO> registraBloqueHorario(@RequestBody Agrega_BH_DTO dto) {
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

}
