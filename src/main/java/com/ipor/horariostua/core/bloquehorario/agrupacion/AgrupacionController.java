package com.ipor.horariostua.core.bloquehorario.agrupacion;

import com.ipor.horariostua.core.bloquehorario.agrupacion.departamento.Departamento;
import com.ipor.horariostua.core.bloquehorario.agrupacion.departamento.DepartamentoService;
import com.ipor.horariostua.core.bloquehorario.agrupacion.dto.ListarAgrupacionDTO;
import com.ipor.horariostua.core.bloquehorario.agrupacion.rangohoras.RangoHorarioService;
import com.ipor.horariostua.core.bloquehorario.agrupacion.sedes.DetalleSedeAgrupacion;
import com.ipor.horariostua.core.bloquehorario.agrupacion.sedes.DetalleSedeAgrupacionService;
import com.ipor.horariostua.core.bloquehorario.agrupacion.usuarios.DetalleGruposUsuario;
import com.ipor.horariostua.core.bloquehorario.agrupacion.usuarios.DetalleGruposUsuarioService;
import com.ipor.horariostua.core.bloquehorario.sede.SedeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/app/agrupacion")
public class AgrupacionController {
    @Autowired
    private AgrupacionService agrupacionService;
    @Autowired
    private DetalleGruposUsuarioService detalleGruposUsuarioService;
    @Autowired
    private DetalleSedeAgrupacionService detalleSedeAgrupacionService;
    @Autowired
    private SedeService sedeService;
    @Autowired
    private DepartamentoService departamentoService;
    @Autowired
    private RangoHorarioService rangoHorarioService;

    @GetMapping("/listar")
    @ResponseBody
    public ResponseEntity<List<ListarAgrupacionDTO>> listarAgrupaciones() {
        List<ListarAgrupacionDTO> lista = agrupacionService.getListaAgrupacion().stream()
                .map(ListarAgrupacionDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/listar/{idDepartamento}")
    @ResponseBody
    public ResponseEntity<List<ListarAgrupacionDTO>> listarAgrupaciones(@PathVariable Long idDepartamento) {
        List<ListarAgrupacionDTO> lista = agrupacionService.getListaAgrupacionPorDepartamento(idDepartamento).stream()
                .map(ListarAgrupacionDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }


    @PostMapping("/crear")
    public ResponseEntity<?> crearAgrupacion(@RequestBody Agrupacion nuevaAgrupacion) {
        if (nuevaAgrupacion.getNombre() == null || nuevaAgrupacion.getNombre().trim().isEmpty())
            return ResponseEntity.badRequest().body("Nombre requerido");
        if (nuevaAgrupacion.getDepartamento() == null || nuevaAgrupacion.getDepartamento().getId() == null)
            return ResponseEntity.badRequest().body("Departamento requerido");

        Departamento dep = departamentoService.getDepartamentoPorId(nuevaAgrupacion.getDepartamento().getId());
        if (dep == null)
            return ResponseEntity.badRequest().body("Departamento no encontrado");

        Agrupacion agrupacion = agrupacionService.crearAgrupacion(nuevaAgrupacion.getNombre(), dep);
        rangoHorarioService.creaRangoHorario(agrupacion, 7, 20);

        detalleSedeAgrupacionService.agrupar(sedeService.getSedePorId(1L), agrupacion.getId());

        return ResponseEntity.ok(agrupacion);
    }
    @PutMapping("/editar/{idAgrupacion}")
    public ResponseEntity<?> editarAgrupacion(@PathVariable Long idAgrupacion, @RequestBody Agrupacion datosAgrupacion) {
        if (datosAgrupacion.getNombre() == null || datosAgrupacion.getNombre().trim().isEmpty())
            return ResponseEntity.badRequest().body("Nombre requerido");
        Agrupacion agrupacion = agrupacionService.getAgrupacionPorId(idAgrupacion);
        if (agrupacion == null) return ResponseEntity.notFound().build();
        agrupacion.setNombre(datosAgrupacion.getNombre());
        agrupacionService.actualizarAgrupacion(agrupacion);
        return ResponseEntity.ok(agrupacion);
    }

    @PostMapping("/estado/{idAgrupacion}")
    public ResponseEntity<?> cambiarEstadoAgrupacion(@PathVariable Long idAgrupacion, @RequestBody Boolean isActive) {
        agrupacionService.cambiarEstado(idAgrupacion, isActive);
        return ResponseEntity.ok().build();
    }



















    @GetMapping("/listar-activos")
    @ResponseBody
    public ResponseEntity<List<ListarAgrupacionDTO>> listarAgrupacionesActivas() {
        List<Agrupacion> lista = agrupacionService.getListaAgrupacionTrue();
        List<ListarAgrupacionDTO> listadto = new ArrayList<>();
        for (Agrupacion agrupacion : lista){
            ListarAgrupacionDTO dto = new ListarAgrupacionDTO(agrupacion);
            listadto.add(dto);
        }
        return ResponseEntity.ok(listadto);
    }

    @GetMapping("/listar-activos/usuario/{idUsuario}")
    @ResponseBody
    public ResponseEntity<List<ListarAgrupacionDTO>> listarAgrupacionesActivasPorUsuario(@PathVariable Long idUsuario) {
        List<ListarAgrupacionDTO> listadto = new ArrayList<>();
        List<Agrupacion> listaPorUsuario = detalleGruposUsuarioService.getAgrupacionesPorUsuarioId(idUsuario);
        for (Agrupacion agrupacion : listaPorUsuario){
            ListarAgrupacionDTO dto = new ListarAgrupacionDTO(agrupacion);
            listadto.add(dto);
        }
        return ResponseEntity.ok(listadto);
    }

}
