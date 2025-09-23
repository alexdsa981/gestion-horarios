package com.ipor.horariostua.core.bloquehorario.agrupacion.departamento;

import com.ipor.horariostua.core.bloquehorario.agrupacion.departamento.dto.ListarDepartamentoDTO;
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
@RequestMapping("/app/departamento")
public class DepartamentoController {
    @Autowired
    private DepartamentoService departamentoService;
    @Autowired
    private DetalleGruposUsuarioService detalleGruposUsuarioService;
    @Autowired
    private SedeService sedeService;


    @GetMapping("/listar")
    @ResponseBody
    public ResponseEntity<List<ListarDepartamentoDTO>> listarDepartamentos() {
        List<ListarDepartamentoDTO> lista = departamentoService.getListaDepartamento().stream()
                .map(ListarDepartamentoDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearDepartamento(@RequestBody Departamento nuevoDepartamento) {
        if (nuevoDepartamento.getNombre() == null || nuevoDepartamento.getNombre().trim().isEmpty())
            return ResponseEntity.badRequest().body("Nombre requerido");
        Departamento creada = departamentoService.crearDepartamento(nuevoDepartamento.getNombre());
        return ResponseEntity.ok(creada);
    }

    @PutMapping("/editar/{idDepartamento}")
    public ResponseEntity<?> editarDepartamento(@PathVariable Long idDepartamento, @RequestBody Departamento datosDepartamento) {
        if (datosDepartamento.getNombre() == null || datosDepartamento.getNombre().trim().isEmpty())
            return ResponseEntity.badRequest().body("Nombre requerido");
        Departamento departamento = departamentoService.getDepartamentoPorId(idDepartamento);
        if (departamento == null) return ResponseEntity.notFound().build();
        departamento.setNombre(datosDepartamento.getNombre());
        departamentoService.actualizarDepartamento(departamento);
        return ResponseEntity.ok(departamento);
    }

    @PostMapping("/estado/{idDepartamento}")
    public ResponseEntity<?> cambiarEstadoDepartamento(@PathVariable Long idDepartamento, @RequestBody Boolean isActive) {
        departamentoService.cambiarEstado(idDepartamento, isActive);
        return ResponseEntity.ok().build();
    }







    @GetMapping("/listar-activos")
    @ResponseBody
    public ResponseEntity<List<ListarDepartamentoDTO>> listarDepartamentosActivas() {
        List<Departamento> lista = departamentoService.getListaDepartamentoTrue();
        List<ListarDepartamentoDTO> listadto = new ArrayList<>();
        for (Departamento departamento : lista){
            ListarDepartamentoDTO dto = new ListarDepartamentoDTO(departamento);
            listadto.add(dto);
        }
        return ResponseEntity.ok(listadto);
    }



}
