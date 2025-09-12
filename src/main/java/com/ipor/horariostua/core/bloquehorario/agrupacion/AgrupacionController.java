package com.ipor.horariostua.core.bloquehorario.agrupacion;

import com.ipor.horariostua.core.bloquehorario.agrupacion.dto.ListarAgrupacionDTO;
import com.ipor.horariostua.core.bloquehorario.agrupacion.usuarios.DetalleGruposUsuario;
import com.ipor.horariostua.core.bloquehorario.agrupacion.usuarios.DetalleGruposUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/app/agrupacion")
public class AgrupacionController {
    @Autowired
    private AgrupacionService agrupacionService;
    @Autowired
    private DetalleGruposUsuarioService detalleGruposUsuarioService;


    @GetMapping("/listar")
    @ResponseBody
    public ResponseEntity<List<Agrupacion>> listarAgrupaciones() {
        List<Agrupacion> lista = agrupacionService.getListaAgrupacion();
        return ResponseEntity.ok(lista);
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
