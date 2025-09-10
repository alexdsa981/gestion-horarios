package com.ipor.horariostua.core.usuario.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/app/usuarios")

public class UsuarioSpringController {
    @Autowired
    private SpringUserService springUserService;

    @PreAuthorize("hasAuthority('Admin')")
    @GetMapping("/spring/buscar")
    public ResponseEntity<List<UsuarioSpringDTO>> buscarUsuariosPorNombre(@RequestParam String nombre) {
        List<UsuarioSpringDTO> lista = springUserService.buscarUsuariosPorNombre(nombre);
        return ResponseEntity.ok(lista);
    }


}
