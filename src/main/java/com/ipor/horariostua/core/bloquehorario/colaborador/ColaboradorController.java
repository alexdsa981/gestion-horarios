package com.ipor.horariostua.core.bloquehorario.colaborador;

import com.ipor.horariostua.core.bloquehorario.agrupacion.AgrupacionService;
import com.ipor.horariostua.core.bloquehorario.agrupacion.colaboradores.DetalleColaboradorAgrupacion;
import com.ipor.horariostua.core.bloquehorario.agrupacion.colaboradores.DetalleColaboradorAgrupacionService;
import com.ipor.horariostua.core.bloquehorario.colaborador.dto.AgregarColaboradorDTO;
import com.ipor.horariostua.core.bloquehorario.colaborador.dto.EditarColorColaboradorDTO;
import com.ipor.horariostua.core.bloquehorario.colaborador.dto.ListarColaboradoresDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
        // colaboradorService.actualizarColor(dto.getColaboradorId(), dto.getColor());
        DetalleColaboradorAgrupacion detalleColaboradorAgrupacion = detalleColaboradorAgrupacionService.getDetallePorColaboradorYAgrupacion(dto.getColaboradorId(), dto.getAgrupacionId());
        detalleColaboradorAgrupacion.setEventoColor(dto.getColor());
        detalleColaboradorAgrupacionService.save(detalleColaboradorAgrupacion);
        return ResponseEntity.ok().build();
    }
}
