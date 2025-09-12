package com.ipor.horariostua.core.web;


import com.ipor.horariostua.core.bloquehorario.agrupacion.Agrupacion;
import com.ipor.horariostua.core.bloquehorario.agrupacion.AgrupacionService;
import com.ipor.horariostua.core.bloquehorario.agrupacion.usuarios.DetalleGruposUsuarioService;
import com.ipor.horariostua.core.bloquehorario.colaborador.ColaboradorService;
import com.ipor.horariostua.core.bloquehorario.sede.SedeService;
import com.ipor.horariostua.core.usuario.Usuario;
import com.ipor.horariostua.core.usuario.UsuarioService;
import com.ipor.horariostua.core.usuario.caracteristicas.rol.RolUsuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class WebController {

    @Autowired
    SedeService sedeService;
    @Autowired
    ColaboradorService colaboradorService;
    @Autowired
    DetalleGruposUsuarioService detalleGruposUsuarioService;
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    AgrupacionService agrupacionService;

    //redirige / a /login
    @GetMapping("/")
    public String redirectToInicio() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String redirigePaginaLogin(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "username", required = false) String username,
            Model model) {
        // Obtén la autenticación actual
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/registro-horarios";
        }
        model.addAttribute("error", error);
        model.addAttribute("username", username);
        return "index";
    }

    @PostMapping("/actualizar-agrupacion")
    @ResponseBody
    public void actualizarAgrupacion(@RequestParam Long agrupacionId) {
        Usuario usuario = usuarioService.getUsuarioLogeado();
        Agrupacion agrupacion = agrupacionService.getAgrupacionPorId(agrupacionId);
        usuario.setAgrupacionSeleccionada(agrupacion);
        usuarioService.save(usuario);
        System.out.println("[POST] Actualizando agrupacionSeleccionadaId en BD a: " + agrupacionId);
    }



    @GetMapping("/home")
    public String redirigePaginaHome(Model model) {
        Usuario usuario = usuarioService.getUsuarioLogeado();
        Long agrupacionSeleccionadaId = usuario.getAgrupacionSeleccionada().getId();
        colaboradorService.getModelSelectColaboradoresActivosPorAgrupacion(model, agrupacionSeleccionadaId);
        sedeService.getModelSedesActivasPorAgrupacion(model, agrupacionSeleccionadaId);

        model.addAttribute("Titulo", "IPOR - Horarios | Home");
        model.addAttribute("agrupacionSeleccionadaId", agrupacionSeleccionadaId);
        model.addAttribute("paginaActual", "home");
        return "home/vista-general";
    }


    @GetMapping("/registro-horarios")
    public String redirigePaginaRegistro(Model model) {
        Usuario usuario = usuarioService.getUsuarioLogeado();
        Long agrupacionSeleccionadaId = usuario.getAgrupacionSeleccionada().getId();
        colaboradorService.getModelSelectColaboradoresActivosPorAgrupacion(model, agrupacionSeleccionadaId);
        sedeService.getModelSedesActivasPorAgrupacion(model, agrupacionSeleccionadaId);

        model.addAttribute("Titulo", "IPOR - Horarios | Registro");
        model.addAttribute("agrupacionSeleccionadaId", agrupacionSeleccionadaId);
        model.addAttribute("paginaActual", "horarios");
        return "gestion-horarios/inicio";
    }
    @GetMapping("/personal")
    public String redirigePaginaPersonal(Model model) {
        model.addAttribute("paginaActual", "personal");
        model.addAttribute("Titulo", "IPOR - Horarios | Mi Personal");
        return "personalsedes/personal/personal";
    }

    @GetMapping("/sedes")
    public String redirigePaginaSedes(Model model) {
        model.addAttribute("paginaActual", "personal");
        model.addAttribute("Titulo", "IPOR - Horarios | Sedes");
        return "personalsedes/sedes/sedes";
    }

    @GetMapping("/configuracion/usuarios")
    public String redirigePaginaUsuarios(Model model) {
        model.addAttribute("paginaActual", "config");
        List<RolUsuario> listaRoles = usuarioService.getListaRoles();
        model.addAttribute("Lista_Roles", listaRoles);
        model.addAttribute("Titulo", "IPOR - Horarios | Admin - Usuarios");
        return "configuracion/usuarios/usuarios";
    }

    @GetMapping("/configuracion/sedes")
    public String redirigePaginaSedesGlobal(Model model) {
        model.addAttribute("paginaActual", "config");
        model.addAttribute("Titulo", "IPOR - Horarios | Admin - Sedes");
        return "configuracion/sedes/sedes";
    }


}
