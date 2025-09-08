package com.ipor.horariostua.core.bloquehorario.colaborador;

import com.ipor.horariostua.core.bloquehorario.agrupacion.colaboradores.DetalleColaboradorAgrupacionService;
import com.ipor.horariostua.core.bloquehorario.colaborador.dto.AgregarColaborador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/app/colaboradores")
public class ColaboradorController {
    @Autowired
    ColaboradorService colaboradorService;
    @Autowired
    DetalleColaboradorAgrupacionService detalleColaboradorAgrupacionService;

    @GetMapping("/listar")
    @ResponseBody
    public ResponseEntity<List<Colaborador>> listarColaboradores() {
        List<Colaborador> colaboradores = colaboradorService.getListarTodo();
        return ResponseEntity.ok(colaboradores);
    }

    @GetMapping("/listar/colaboradores-area/{id}")
    @ResponseBody
    public ResponseEntity<List<Colaborador>> listarColaboradoresArea(@PathVariable Long id) {
        List<Colaborador> colaboradores = colaboradorService.getListarPorAgrupacionId(id);
        return ResponseEntity.ok(colaboradores);
    }

    @PostMapping("/guardar")
    @ResponseBody
    public ResponseEntity<String> guardarColaborador(@RequestBody AgregarColaborador agregarColaborador) {
        detalleColaboradorAgrupacionService.agrupar(colaboradorService.agregar(agregarColaborador), agregarColaborador.getIdAgrupacion());
        return ResponseEntity.ok("Colaborador guardado exitosamente");
    }

    @PostMapping("/desactivar/{id}")
    @ResponseBody
    public ResponseEntity<String> desactivarColaborador(@PathVariable Long id) {
        boolean resultado = colaboradorService.cambiarEstado(id, false);
        if (resultado) {
            return ResponseEntity.ok("Colaborador desactivado exitosamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Colaborador no encontrado");
        }
    }
}
