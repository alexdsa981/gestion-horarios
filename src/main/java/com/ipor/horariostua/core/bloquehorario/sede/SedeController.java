package com.ipor.horariostua.core.bloquehorario.sede;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
