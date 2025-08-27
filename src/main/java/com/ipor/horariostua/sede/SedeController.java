package com.ipor.horariostua.sede;

import com.ipor.horariostua.colaborador.ColaboradorService;
import com.ipor.horariostua.colaborador.dto.ColaboradorSeleccionableDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/app/sedes")
public class SedeController {
    @Autowired
    SedeService sedeService;

    @GetMapping("/select")
    public ResponseEntity<List<Sede>> getColaboradoresSeleccionables() {
        List<Sede> sedes = sedeService.getSelectSedes();
        return ResponseEntity.ok(sedes);
    }

}
