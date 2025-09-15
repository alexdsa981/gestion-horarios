package com.ipor.horariostua.core.web;
import com.ipor.horariostua.core.bloquehorario.agrupacion.Agrupacion;
import com.ipor.horariostua.core.bloquehorario.agrupacion.AgrupacionService;
import com.ipor.horariostua.core.usuario.Usuario;
import com.ipor.horariostua.core.usuario.UsuarioService;
import com.ipor.horariostua.core.bloquehorario.agrupacion.usuarios.DetalleGruposUsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalAdviceController {

    @Autowired
    UsuarioService usuarioService;
    @Autowired
    AgrupacionService agrupacionService;
    @Autowired
    DetalleGruposUsuarioService detalleGruposUsuarioService;


    @ModelAttribute
    public void addGlobalHeaderData(Model model, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal())) {

            Long usuarioId = usuarioService.getIDdeUsuarioLogeado();
            Usuario usuario = usuarioService.getUsuarioPorId(usuarioId);

            List<Agrupacion> listaGruposUsuario = detalleGruposUsuarioService.getAgrupacionesPorUsuarioId(usuario.getId());
            model.addAttribute("listaGruposUsuario", listaGruposUsuario);

            model.addAttribute("agrupacionSeleccionadaId", usuario.getAgrupacionSeleccionada().getId());

        }
    }
}

