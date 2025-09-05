package com.ipor.horariostua.core.bloquehorario.colaborador;

import com.ipor.horariostua.core.bloquehorario.colaborador.dto.AgregarColaborador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/app/colaboradores")
public class ColaboradorController {
    @Autowired
    ColaboradorService colaboradorService;

    @PostMapping("/guardar")
    @ResponseBody
    public ResponseEntity<String> guardarColaborador(@RequestBody AgregarColaborador agregarColaborador) {
        Colaborador colaborador;
        if (colaboradorService.existeEnBD(agregarColaborador.getEmpleado())){
            colaborador = colaboradorService.getColaboradorPorId(agregarColaborador.getEmpleado());
        }else{
            colaborador = new Colaborador();
            colaborador.setServicio("No Asignado");
            colaborador.setEventoColor(colaboradorService.getRandomColorHex());
        }
        colaborador.setId(agregarColaborador.getEmpleado());
        colaborador.setNombreCompleto(agregarColaborador.getNombreCompleto());
        colaborador.setNumeroDocumento(agregarColaborador.getDocumento());
        colaborador.setColegioProfesional(agregarColaborador.getTipoColegio());
        colaborador.setNumeroColegiatura(agregarColaborador.getCmp());
        colaborador.setEspecialidad(agregarColaborador.getEspecialidadNombre());
        colaborador.setIsActive(Boolean.TRUE);
        colaboradorService.save(colaborador);

        return ResponseEntity.ok("Colaborador guardado exitosamente");
    }

    @GetMapping("/listar")
    @ResponseBody
    public ResponseEntity<List<Colaborador>> listarColaboradores() {
        List<Colaborador> colaboradores = colaboradorService.getListarTodo();
        return ResponseEntity.ok(colaboradores);
    }

}
