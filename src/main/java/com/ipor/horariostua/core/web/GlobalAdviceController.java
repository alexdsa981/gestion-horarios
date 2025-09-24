package com.ipor.horariostua.core.web;
import com.ipor.horariostua.core.bloquehorario.agrupacion.Agrupacion;
import com.ipor.horariostua.core.bloquehorario.agrupacion.AgrupacionService;
import com.ipor.horariostua.core.bloquehorario.agrupacion.departamento.Departamento;
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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
            List<Agrupacion> listaGruposPorDepartamento = detalleGruposUsuarioService.getAgrupacionesPorUsuarioIdAndDepartamentoId(usuario.getId(), usuario.getAgrupacionSeleccionada().getDepartamento().getId());


            Set<Departamento> departamentosSet = listaGruposUsuario.stream()
                    .map(Agrupacion::getDepartamento)
                    .collect(Collectors.toCollection(LinkedHashSet::new)); // preserva el orden

            List<Departamento> listaDepartamentosUsuario = new ArrayList<>(departamentosSet);

            model.addAttribute("listaGruposUsuario", listaGruposPorDepartamento);
            model.addAttribute("listaDepartamentosUsuario", listaDepartamentosUsuario);
            model.addAttribute("agrupacionSeleccionadaId", usuario.getAgrupacionSeleccionada().getId());
            model.addAttribute("departamentoSeleccionadoId", usuario.getAgrupacionSeleccionada().getDepartamento().getId());


        }
    }
}

